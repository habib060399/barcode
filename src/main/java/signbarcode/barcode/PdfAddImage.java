package signbarcode.barcode;

import com.aspose.pdf.*;
import com.aspose.pdf.Document;
import com.aspose.pdf.operators.ConcatenateMatrix;
import com.aspose.pdf.operators.Do;
import com.aspose.pdf.operators.GRestore;
import com.aspose.pdf.operators.GSave;

import java.io.IOException;
import java.util.Locale;

public class PdfAddImage {

    private static String pdfDir = "/Users/Abib/Downloads/barcode/barcode/src/main/resources/static/assets/upload/";
    private static String signPdfDir = "/Users/Abib/Downloads/barcode/barcode/src/main/resources/static/assets/signpdf/";
    private static String imageDir = "src/main/resources/static/assets/barcode/";

    public static void AddImageToPdf(String fileNamePdf, String fileNameQr) throws IOException {
        com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));

        Document document = new Document(pdfDir + fileNamePdf);

//        int lowerLeftX = 350;
        int lowerLeftX = 335;
        int lowerLeftY = 160;
//        int upperRightX = 400;
        int upperRightX = 385;
        int upperRightY = 110;

        Page page = document.getPages().get_Item(1);

        java.io.FileInputStream imageStream = new java.io.FileInputStream(new java.io.File(imageDir + fileNameQr +".png"));

        page.getResources().getImages().add(imageStream);

        page.getContents().add(new GSave());

        Rectangle rectangle = new Rectangle(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
        Matrix matrix = new Matrix(new double[] {rectangle.getURX() - rectangle.getLLX(), 0, 0,
        rectangle.getURY() - rectangle.getLLY(), rectangle.getLLX(), rectangle.getLLY()});

        page.getContents().add(new ConcatenateMatrix(matrix));
        XImage xImage = page.getResources().getImages().get_Item(page.getResources().getImages().size());

        page.getContents().add(new Do(xImage.getName()));

        page.getContents().add(new GRestore());

        document.save(signPdfDir + fileNamePdf +".pdf");

        imageStream.close();
    }

}
