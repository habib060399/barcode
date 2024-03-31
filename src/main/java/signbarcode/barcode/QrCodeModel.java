package signbarcode.barcode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "qrcode")
public class QrCodeModel {
    @JoinTable(name = "document")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomor_dokumen;
    private String nama_dokumen;
    private String nama_qrcode;

}
