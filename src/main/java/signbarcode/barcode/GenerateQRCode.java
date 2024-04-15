package signbarcode.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class GenerateQRCode {

    public static void GenerateQR(String name_file, String no_doc) {
        RSA rsa = new RSA();
        rsa.initFromStrings();

        String path = "src/main/resources/static/assets/barcode/"+name_file+".png";

        try {
            String signature = rsa.encrypt(no_doc);
            Map<String, String> qrCodeDataMap = Map.of(
                    "noDoc", no_doc,
                    "namaFile", name_file,
                    "signature", signature
                    );

            String jsonString = new JSONObject(qrCodeDataMap).toString();
            BitMatrix matrix = new MultiFormatWriter().encode(signature, BarcodeFormat.QR_CODE, 1000, 1000);
            MatrixToImageWriter.writeToPath(matrix, "PNG", Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.print("sukses membuat Qr Code");
    }
}
