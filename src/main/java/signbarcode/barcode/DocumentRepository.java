package signbarcode.barcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query(value = "select d from Document d where d.nomor_dokumen like :nomor_dokumen")
    List<Document> nomor_dokumen(@Param("nomor_dokumen") String nomor_dokumen);


    List<Document> findByStatusOrStatus(String param1, String param2);

    List<Document> findAllByStatusLike(String status);
    Optional<Document> findById(int id);
}
