package signbarcode.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GenerateQRCode {

    public static Boolean GenerateQR(String name_file, String no_doc) {
        RSA rsa = new RSA();
        try {
        rsa.initFromStrings();

//        String path = "src/main/resources/static/assets/barcode/"+name_file+".png";
        ClassPathResource classPathResource = new ClassPathResource("static/assets/barcode/");
        File directory = classPathResource.getFile();
        Path fileStorageLocation = directory.toPath().toAbsolutePath().normalize();
        Path targetLocation = fileStorageLocation.resolve(name_file+".png");


            String signature = rsa.encrypt(no_doc);
            Map<String, String> qrCodeDataMap = Map.of(
                    "noDoc", no_doc,
                    "namaFile", name_file,
                    "signature", signature
                    );

            BitMatrix matrix = new MultiFormatWriter().encode(signature, BarcodeFormat.QR_CODE, 1000, 1000);
//            MatrixToImageWriter.writeToPath(matrix, "PNG", Paths.get(stringPath+name_file+".png"));
            MatrixToImageWriter.writeToPath(matrix, "PNG", targetLocation);
            return true;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
