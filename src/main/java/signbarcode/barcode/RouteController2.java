package signbarcode.barcode;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
public class RouteController2 {

//    @GetMapping(path = "/")
//    public ModelAndView readQRCode(HttpSession session) throws IOException {
//        String status = "";
//        if ( (String) session.getAttribute("message") != null){
//            status = (String) session.getAttribute("message");
//        }
//        return new ModelAndView("reader_qrcode", Map.of(
//                "urlPost", "http://localhost:8080/read-qrcode/read",
//                "message", status,
//                sessionUser(), true
//
//        ));
//    }

}
