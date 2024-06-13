package signbarcode.barcode;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageInterface {
    String storeFile(MultipartFile file, String uploadDir);
}
