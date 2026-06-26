package com.aiarchitect.repository;

import com.aiarchitect.model.ExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ExportHistory database operations.
 *
 * Tracks when users export architecture reports and in which format.
 */
@Repository
public interface ExportHistoryRepository extends JpaRepository<ExportHistory, Long> {

    /**
     * Find all export records for a specific project.
     * Used to show export history on the project detail page.
     *
     * Generated SQL: SELECT * FROM export_history
     *                WHERE project_id = ?
     *                ORDER BY exported_at DESC
     */
    List<ExportHistory> findByProjectIdOrderByExportedAtDesc(Long projectId);
}
