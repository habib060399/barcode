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

    private static String pdfDir = "src/main/resources/static/assets/signing/";
    private static String signPdfDir = "src/main/resources/static/assets/signpdf/";
    private static String imageDir = "src/main/resources/static/assets/barcode/";

    public static void AddImageToPdf(String fileNamePdf, String fileNameQr) throws IOException {
        com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));

        Document document = new Document(pdfDir + fileNamePdf);

        int lowerLeftX = 370;
        int lowerLeftY = 170;
        int upperRightX = 300;
        int upperRightY = 100;

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

        document.save(signPdfDir+fileNamePdf);

        imageStream.close();
    }

}
