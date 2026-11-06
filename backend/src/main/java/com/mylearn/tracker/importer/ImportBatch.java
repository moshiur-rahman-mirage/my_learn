package com.mylearn.tracker.importer;

import com.mylearn.tracker.plan.LearningPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** Audit row for one confirmed import; its content hash detects accidental re-imports. */
@Entity
@Table(name = "import_batch")
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false, updatable = false)
    private LearningPlan plan;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "content_sha256", nullable = false)
    private String contentSha256;

    @Column(name = "item_count", nullable = false)
    private int itemCount;

    @Column(name = "imported_at", nullable = false)
    private Instant importedAt;

    protected ImportBatch() {
    }

    public ImportBatch(LearningPlan plan, String fileName, String sheetName, String contentSha256,
                       int itemCount, Instant importedAt) {
        this.plan = plan;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.contentSha256 = contentSha256;
        this.itemCount = itemCount;
        this.importedAt = importedAt;
    }

    public LearningPlan getPlan() {
        return plan;
    }

    public Instant getImportedAt() {
        return importedAt;
    }
}
