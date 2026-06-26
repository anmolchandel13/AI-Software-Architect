package com.aiarchitect.repository;

import com.aiarchitect.model.ArchitectureReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ArchitectureReport database operations.
 *
 * Relatively simple because reports are always accessed through their
 * parent Project entity. The main custom query finds a report by its
 * project ID (useful when we have the project ID but need the report).
 */
@Repository
public interface ArchitectureReportRepository extends JpaRepository<ArchitectureReport, Long> {

    /**
     * Find the architecture report for a specific project.
     *
     * Generated SQL: SELECT * FROM architecture_reports WHERE project_id = ?
     */
    Optional<ArchitectureReport> findByProjectId(Long projectId);
}
