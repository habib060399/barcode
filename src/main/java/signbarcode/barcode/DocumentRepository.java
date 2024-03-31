package signbarcode.barcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query(value = "select d from Document d where d.nomor_dokumen like :nomor_dokumen")
    List<Document> nomor_dokumen(@Param("nomor_dokumen") String nomor_dokumen);
}
