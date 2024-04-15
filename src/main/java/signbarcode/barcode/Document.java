package signbarcode.barcode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "document")
public class Document {
        @JoinTable(name = "qrcode")
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        private String original_name;
        private String nama_dokumen;
        private String nomor_dokumen;
        private String status;
        private String created_at;
        private String updated_at;
        private String ket;
}
