package com.mylearn.tracker.importer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {

    @Query("select b from ImportBatch b join fetch b.plan where b.contentSha256 = :hash order by b.importedAt desc")
    List<ImportBatch> findByHash(@Param("hash") String hash);
}
