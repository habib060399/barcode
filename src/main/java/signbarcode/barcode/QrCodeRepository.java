package signbarcode.barcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCodeModel, Integer> {

    @Query(value = "select q from QrCodeModel q where q.nomor_dokumen like :nomor_dokumen")
    List<QrCodeModel> getFileName(@Param("nomor_dokumen") String nomor_dokumen);

}
