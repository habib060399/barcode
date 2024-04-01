package signbarcode.barcode;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RouteController {

    @Autowired(required = false)
    private DocumentRepository documentRepository;
    @Autowired(required = false)
    private QrCodeRepository qrCodeRepository;

    @RequestMapping(path = "/hello")
    public void helloWorld(HttpServletResponse response) throws IOException {
        response.getWriter().println("Hello World");
    }

    @GetMapping(path = "/")
    public ModelAndView index() {
        return new ModelAndView("index", Map.of(
                "name", "Habib Shibghatallah"
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
        System.out.println(status);
        return new ModelAndView("cetak_QRCode", Map.of(
                "urlPostCetakQR", "http://localhost:8080/cetak-QrCode/create",
                "status", status
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
        System.out.print(nama_doc);
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
                "status", status
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
        session.setAttribute("status", "Data Berhasil ditambahkan");

        Path path = Path.of("src/main/resources/static/assets/upload/" + file.getOriginalFilename());
        file.transferTo(path);

        Document document = new Document();
        document.setNama_dokumen(nama_dokumen);
        document.setNomor_dokumen(nomor_dokumen);
        document.setOriginal_name(file.getOriginalFilename());
        documentRepository.save(document);

        return new ModelAndView("redirect:/form-entry");
    }

    @RequestMapping(path = "/sign-pdf")
    public ModelAndView signPdf()  {
        var getAll = documentRepository.findAll();
        return new ModelAndView("sign_input", Map.of(
                "data", getAll
        ));
    }

    @GetMapping(path="/sign-pdf/create")
    public ModelAndView createSignPdf(@RequestParam(name = "no_doc") String no_doc) throws IOException {
        List<Document> documents = documentRepository.nomor_dokumen(no_doc);
        List<QrCodeModel> qrCodeModels = qrCodeRepository.getFileName(no_doc);

        PdfAddImage.AddImageToPdf(documents.getFirst().getOriginal_name(), qrCodeModels.getFirst().getNama_qrcode());

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
                "message", status

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
                    "status", "Document Terverifikasi"
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

}