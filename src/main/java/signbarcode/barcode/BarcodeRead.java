package signbarcode.barcode;

import com.aspose.barcode.barcoderecognition.BarCodeReader;
import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.pdf.Document;
import com.aspose.pdf.devices.BmpDevice;
import com.aspose.pdf.devices.Resolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

public class BarcodeRead {

    public String resulDecodeQR;

    BarcodeRead(String fileName) {
        RSA rsa = new RSA();
        rsa.initFromStrings();
        try {
            com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));
            ClassPathResource classPathResourcePdf = new ClassPathResource("static/assets/tmp/");
            String PdfDir = String.valueOf(classPathResourcePdf.getFile())+"/";
            Document pdfDocument = new Document(PdfDir+fileName+".pdf");

            java.io.OutputStream imageStream = new java.io.FileOutputStream(PdfDir+fileName+".bmp");

            Resolution resolution = new Resolution(300);
            BmpDevice bmpDevice = new BmpDevice(resolution);
//
            bmpDevice.process(pdfDocument.getPages().get_Item(1), imageStream);

//            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/static/assets/tmp/"+fileName+".bmp"));
            BufferedImage bufferedImage = ImageIO.read(new File(PdfDir+fileName+".bmp"));
            int x = 916;
            int y = 2664;
            int width = 800;
            int height = 800;

            BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
//            File croppedFile = new File("src/main/resources/static/assets/tmp/"+fileName+"cropped.bmp");
            File croppedFile = new File(PdfDir+fileName+"cropped.bmp");
            ImageIO.write(croppedImage, "bmp", croppedFile);
//            BufferedImage resultCropImage = ImageIO.read(new File("src/main/resources/static/assets/tmp/"+fileName+"cropped.bmp"));
            BufferedImage resultCropImage = ImageIO.read(new File(PdfDir+fileName+"cropped.bmp"));

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(resultCropImage)));

            Result result = new MultiFormatReader().decode(binaryBitmap);
            if (rsa.decrypt(result.getText()).isEmpty()){
                resulDecodeQR = null;
            }else {
                resulDecodeQR = rsa.decrypt(result.getText());
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String test() {

        BarCodeReader reader = new BarCodeReader("src/main/resources/static/assets/signpdf/TRANSKRIP.bmp", DecodeType.QR);
        for (BarCodeResult result : reader.readBarCodes()){
            System.out.println(result.getCodeText());
            System.out.println(result.getCodeTypeName());
        }
        return  reader.readBarCodes().toString();


    }
}
