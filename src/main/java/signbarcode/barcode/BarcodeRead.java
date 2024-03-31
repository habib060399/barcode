package signbarcode.barcode;

import com.aspose.barcode.barcoderecognition.BarCodeReader;
import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.pdf.Document;
import com.aspose.pdf.devices.BmpDevice;
import com.aspose.pdf.devices.ImageDevice;
import com.aspose.pdf.devices.Resolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BarcodeRead {

    BarcodeRead(String fileName) {
        RSA rsa = new RSA();
        rsa.initFromStrings();
        try {
            com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));
            Document pdfDocument = new Document("src/main/resources/static/assets/signpdf/updated_document3.pdf");
//
////            Path path = Path.of("src/main/resources/static/assets/signpdf/"+fileName+".bmp");
            java.io.OutputStream imageStream = new java.io.FileOutputStream("src/main/resources/static/assets/signpdf/updated_document3.bmp");
//
            Resolution resolution = new Resolution(100);
            BmpDevice bmpDevice = new BmpDevice(resolution);
//
            bmpDevice.process(pdfDocument.getPages().get_Item(1), imageStream);

            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/static/assets/signpdf/updated_document3.bmp"));
            int[] pixels = bufferedImage.getRGB(0,0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
            RGBLuminanceSource sourceRGB = new RGBLuminanceSource(bufferedImage.getWidth(), bufferedImage.getHeight(), pixels);

//            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);


            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(sourceRGB));
//            BinaryBitmap binaryBitmapp = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

            Result result = new MultiFormatReader().decode(binaryBitmap);
            System.out.println(result.getText());

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String test() {

        BarCodeReader reader = new BarCodeReader("src/main/resources/static/assets/signpdf/updated_document1.bmp", DecodeType.QR);
        for (BarCodeResult result : reader.readBarCodes()){
            System.out.println(result.getCodeText());
            System.out.println(result.getCodeTypeName());
        }
        return  reader.readBarCodes().toString();


    }
}
