package signbarcode.barcode;

import com.aspose.pdf.operators.Do;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.samskivert.mustache.MustacheException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.view.MustacheView;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.RouteMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class RouteController {

    @Autowired(required = false)
    private DocumentRepository documentRepository;
    @Autowired(required = false)
    private QrCodeRepository qrCodeRepository;
    @Autowired
    private Environment environment;
    private String url;
    public String Url() {
        return this.url = environment.getProperty("application.url");
    }
    HttpSession session;


    @RequestMapping(path = "/hello")
    public void helloWorld(HttpServletResponse response) throws IOException {
        response.getWriter().println("Hello World");
    }

    public String sessionUser() {
//        var user = session.getAttribute("user");
        var user = "user";
        return (String) user;
    }

    @GetMapping(path = "/")
    public ModelAndView index() {
        return new ModelAndView("index", Map.of(
                "name", "Habib Shibghatallah",
                sessionUser(), true
        ));
    }

    @GetMapping(path = "/login")
    public ModelAndView login() {
        return new ModelAndView("login", Map.of(
                "urlPostCetakQR", "Habib Shibghatallah"
        ));
    }

    @GetMapping(path="/cetak-QrCode")
    public ModelAndView cetakQRCode(HttpSession session) {
        String status = "";
        if ( (String) session.getAttribute("status") != null){
            status = (String) session.getAttribute("status");
        }

        return new ModelAndView("cetak_QRCode", Map.of(
                "urlPostCetakQR", "http://localhost:8080/cetak-QrCode/create",
                "status", status,
                sessionUser(), true
        ));
    }

    @PostMapping(path="/cetak-QrCode/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ModelAndView createQRCode(
            HttpServletRequest request,
            @RequestParam(name = "nama_doc") String nama_doc,
            @RequestParam(name = "no_doc") String no_doc,
            @RequestParam(name = "plain_txt") String plain_txt
    ){

        GenerateQRCode.GenerateQR(plain_txt, no_doc);

        QrCodeModel qrCodeModel = new QrCodeModel();
        qrCodeModel.setNomor_dokumen(no_doc);
        qrCodeModel.setNama_dokumen(nama_doc);
        qrCodeModel.setNama_qrcode(plain_txt);
        qrCodeRepository.save(qrCodeModel);

        HttpSession session = request.getSession();
        session.setAttribute("status", "QR Code Berhasil Ditambahkan");

        return new ModelAndView("redirect:/cetak-QrCode");
    }

    @GetMapping(path="/form-entry")
    public ModelAndView formEntry(HttpSession session) {

        String status = "";
        if ( (String) session.getAttribute("status") != null){
            status = (String) session.getAttribute("status");
        }
        session.invalidate();
        return new ModelAndView("form_entry", Map.of(
                "status", status,
                sessionUser(), true
        ));
    }

    @PostMapping(path="/form-entry/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ModelAndView createFormEntry(
            HttpServletRequest request,
            @RequestParam(name="nama_dokumen") String nama_dokumen,
            @RequestParam(name="nomor_dokumen") String nomor_dokumen,
            @RequestPart(name="file") MultipartFile file
            ) throws IOException{

        HttpSession session = request.getSession();
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        if (file.getSize() > 2000000 ) {
            System.out.println("file melebihi dari 2 MB");
            session.setAttribute("status", "file size melebihi 2MB");
        } else if (!Objects.equals(file.getContentType(), "application/pdf")) {
            System.out.println("file yang diupload tidak mendukung");
            session.setAttribute("status", "file yang diupload tidak didukung");
        } else {
            Path path = Path.of("src/main/resources/static/assets/upload/" + file.getOriginalFilename());
            file.transferTo(path);

            session.setAttribute("status", "Data Berhasil ditambahkan");

            Document document = new Document();
            document.setNama_dokumen(nama_dokumen);
            document.setNomor_dokumen(nomor_dokumen);
            document.setOriginal_name(file.getOriginalFilename());
            document.setCreated_at(sdf.format(dt));
            document.setUpdated_at(sdf.format(dt));
            document.setStatus("pengajuan");
            document.setKet("-");
            documentRepository.save(document);

            return new ModelAndView("redirect:/form-entry");
        }

        return new ModelAndView("redirect:/form-entry");
    }

    @RequestMapping(path = "/sign-pdf")
    public ModelAndView signPdf(HttpSession session)  {
        String status="";

        if ( (String) session.getAttribute("error") != null){
            status = (String) session.getAttribute("error");
        }
        session.removeAttribute("error");

        var getAll = documentRepository.findAll();
        return new ModelAndView("sign_input", Map.of(
                "data", getAll,
                "urlDownload", Url() + "download-document-sign-input/",
                "status", status,
                sessionUser(), true
        ));
    }

    @GetMapping(path="/sign-pdf/create")
    public ModelAndView createSignPdf(@RequestParam(name = "no_doc") String no_doc, HttpServletRequest request) throws IOException {
        List<Document> documents = documentRepository.nomor_dokumen(no_doc);
        List<QrCodeModel> qrCodeModels = qrCodeRepository.getFileName(no_doc);

        if(qrCodeModels.isEmpty()){
            System.out.println("belum membuat qrcode");
            HttpSession session = request.getSession();
            session.setAttribute("error", "belum membuat qrcode");
        }else {
            PdfAddImage.AddImageToPdf(documents.getFirst().getOriginal_name(), qrCodeModels.getFirst().getNama_qrcode());
        }

        System.out.println(documents.getFirst().getOriginal_name());
        return new ModelAndView("redirect:/sign-pdf");
    }

    @GetMapping(path = "/read-qrcode")
    public ModelAndView readQRCode(HttpSession session) {
        String status = "";
        if ( (String) session.getAttribute("message") != null){
            status = (String) session.getAttribute("message");
        }
        return new ModelAndView("reader_qrcode", Map.of(
                "urlPost", "http://localhost:8080/read-qrcode/read",
                "message", status,
                sessionUser(), true

        ));
    }

    @PostMapping(path="/read-qrcode/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView reaByQRCode (
            @RequestPart(name="file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException  {
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        String fileName = file.getOriginalFilename();
        Path path = Path.of("src/main/resources/static/assets/tmp/" + file.getOriginalFilename());
        file.transferTo(path);
        BarcodeRead read = new  BarcodeRead(fileName.replace(".pdf",""));
        System.out.println(read.resulDecodeQR);
        List<Document> documents = documentRepository.nomor_dokumen(read.resulDecodeQR);

        if (!documents.isEmpty()){
            return new ModelAndView("document_verify", Map.of(
                    "nama_document", documents.getFirst().getOriginal_name(),
                    "no_document", documents.getFirst().getNomor_dokumen(),
                    "created_at", documents.getFirst().getCreated_at(),
                    "status", "Document Terverifikasi",
                    sessionUser(), true
            ));
        }else {
            HttpSession session = request.getSession();
            session.setAttribute("message", "document tidak terverifikasi");
            return new ModelAndView("redirect:/read-qrcode");
        }

    }

    @GetMapping(path = "/read-qrcode/verify")
    public ModelAndView verifyDocument() {
        return new ModelAndView("document_verify");
    }

    @GetMapping(path = "/form-revisi")
    public ModelAndView formRevisi() {
        var getDocument = documentRepository.findAllByStatusLike("revisi");

        return new ModelAndView("form_revisi", Map.of(
                "data", getDocument,
                "url", Url() + "form-revisi",
                sessionUser(), true

        ));
    }

    @GetMapping(path = "/form-revisi/{id}")
    public ModelAndView formRevisiEdit(@PathVariable int id) {
        Optional<Document> documentById = documentRepository.findById(id);

        return new ModelAndView("form_revisi_edit", Map.of(
                "nama_dokumen", documentById.get().getNama_dokumen(),
                "nomor_dokumen", documentById.get().getNomor_dokumen(),
                "id", documentById.get().getId(),
                "url", Url() + "form-revisi/edit",
                sessionUser(), true


        ));
    }

    @PostMapping(path = "/form-revisi/edit")
    public ModelAndView revisiEdit(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "nama_dokumen") String nama_dokumen,
            @RequestParam(name = "nomor_dokumen") String nomor_dokumen,
            @RequestPart(name = "file") MultipartFile file,
            HttpServletRequest request
    )throws IOException{
        HttpSession session = request.getSession();
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        if (file.getSize() > 2000000 ) {
            System.out.println("file melebihi dari 2 MB");
            session.setAttribute("status", "file size melebihi 2MB");
        } else if (file.isEmpty()) {

            session.setAttribute("status", "Data Berhasil ditambahkan");

            Document document = documentRepository.findById(Integer.parseInt(id)).orElse(null);
            document.setNama_dokumen(nama_dokumen);
            document.setNomor_dokumen(nomor_dokumen);
            document.setUpdated_at(sdf.format(dt));
            document.setStatus("pengajuan");
            documentRepository.save(document);

            return new ModelAndView("redirect:/form-revisi");
        }else {
            if (!Objects.equals(file.getContentType(), "application/pdf")){
                System.out.println("file yang diupload tidak mendukung");
                session.setAttribute("status", "file yang diupload tidak didukung");
            }
            Path path = Path.of("src/main/resources/static/assets/upload/" + file.getOriginalFilename());
            file.transferTo(path);

            session.setAttribute("status", "Data Berhasil ditambahkan");

            Document document = documentRepository.findById(Integer.parseInt(id)).orElse(null);
            document.setNama_dokumen(nama_dokumen);
            document.setNomor_dokumen(nomor_dokumen);
            document.setUpdated_at(sdf.format(dt));
            document.setStatus("pengajuan");
            document.setOriginal_name(file.getOriginalFilename());
            documentRepository.save(document);

            return new ModelAndView("redirect:/form-revisi");
        }

        return new ModelAndView("redirect:/form-revisi");
    }

    @GetMapping(path = "/document-signing")
    public ModelAndView documentSigning() {
        List<Document> getDocument = documentRepository.findAllByStatusLike("approve");
        return new ModelAndView("document_signing", Map.of(
                sessionUser(), true,
                "data", getDocument,
                "download", Url() + "assets/signpdf/",
                "urlDownload", Url() + "download-document-sign/",
                "url", Url() + "document-signing/"
        ));
    }

    @GetMapping(path = "/document-signing/{id}")
    public ModelAndView uploadSignin(@PathVariable int id) {
        Optional<Document> documentById = documentRepository.findById(id);

        return new ModelAndView("upload_document_signin", Map.of(
                "nama_document", documentById.get().getNama_dokumen(),
                "nomor_document", documentById.get().getNomor_dokumen(),
                "id", documentById.get().getId(),
                "url", Url() + "document-signing/upload"
        ));
    }

    @PostMapping(path = "/document-signing/upload")
    public ModelAndView uploadSigninDocument(
            @RequestParam(name = "id") String id,
            @RequestPart(name = "file") MultipartFile file
            ) throws IOException {
        Document document = documentRepository.findById(Integer.parseInt(id)).orElse(null);
        if (file.getSize() > 2000000 ) {
            session.setAttribute("status", "file size melebihi 2MB");
        } else if (!Objects.equals(file.getContentType(), "application/pdf")){
            session.setAttribute("status", "file yang diupload tidak didukung");
        }else {
            Path path = Path.of("src/main/resources/static/assets/signing/" + file.getOriginalFilename());
            file.transferTo(path);
            document.setOriginal_name(file.getOriginalFilename());
            documentRepository.save(document);
        }

        return new ModelAndView("redirect:/document-signing");
    }

    @GetMapping(path = "/cek-document")
    public ModelAndView cekDocument(){
        var documents = documentRepository.findAll();
        return new ModelAndView("check_document", Map.of(
                "data", documents,
                sessionUser(), true

        ));
    }

    @GetMapping(path = "/revisi-deocument")
    public ModelAndView revisiDocument(){
        List<Document> documents = documentRepository.findAllByStatusLike("revisi");
        return new ModelAndView("revisi_document", Map.of(
                "data", documents,
                sessionUser(), true

        ));
    }

    @GetMapping(path = "/approved")
    public ModelAndView cekApprovedDocument(HttpServletRequest request){
        String url = environment.getProperty("application.url");
        var getAll = documentRepository.findByStatusOrStatus("revisi", "pengajuan");
//        System.out.println(getAll.get(1).getCreated_at());

        return new ModelAndView("approved_document", Map.of(
                "url", url + "approved-document",
                "data", getAll,
                sessionUser(), true
        ));
    }

    @GetMapping(path = "/approved-document/{id}")
    public ModelAndView approvedDocument(@PathVariable int id) {

        Optional<Document> documentById = documentRepository.findById(id);

        return new ModelAndView("approved", Map.of(
                "id", documentById.get().getId(),
                "nama_document", documentById.get().getNama_dokumen(),
                "nomor_document", documentById.get().getNomor_dokumen(),
                "created_at", documentById.get().getCreated_at(),
                "updated_at", documentById.get().getUpdated_at(),
                "url_post", Url()+"approved-document/approve",
                sessionUser(), true
        ));
    }

    @PostMapping(path = "/approved-document/approve")
    public ModelAndView approvedDocumentPost(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "approved") String approved,
            @RequestParam(name = "keterangan") String keterangan
    ){
        System.out.println(approved);
        String status = "";
        if (approved.equals("setuju")){
            status = "approve";
        } else {
            status = "revisi";
        }

        Document document = documentRepository.findById(Integer.parseInt(id)).orElse(null);
        document.setStatus(status);
        document.setKet(keterangan);
        documentRepository.save(document);

        return new ModelAndView("redirect:/approved");
    }

    @GetMapping(path = "/download-document-sign/{filename}")
    public ResponseEntity<InputStreamResource> fileDownloadDocumentSign(@PathVariable String filename) throws IOException {
        File file = new File("src/main/resources/static/assets/upload/"+filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        System.out.println(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);


    }

    @GetMapping(path = "/download-document-sign-input/{filename}")
    public ResponseEntity<InputStreamResource> fileDownloadDocumentSignInput(@PathVariable String filename) throws IOException {
        File file = new File("src/main/resources/static/assets/signpdf/"+filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        System.out.println(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);


    }
}