package com.aiarchitect.repository;

import com.aiarchitect.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Project database operations.
 *
 * Custom queries here support the core features:
 * - Listing a user's projects (ordered by newest first)
 * - Searching projects by keyword in the title
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find all projects belonging to a specific user, ordered by creation date (newest first).
     * Used for the project history dashboard.
     *
     * Spring sees: find + By + User_Id + OrderBy + CreatedAt + Desc
     * Generated SQL: SELECT * FROM projects WHERE user_id = ? ORDER BY created_at DESC
     */
    List<Project> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Search a user's projects by keyword in the title (case-insensitive).
     * Used for the search feature on the history dashboard.
     *
     * "Containing" translates to SQL LIKE '%keyword%'
     * "IgnoreCase" adds LOWER() for case-insensitive matching
     *
     * Generated SQL: SELECT * FROM projects
     *                WHERE user_id = ?
     *                AND LOWER(title) LIKE LOWER('%keyword%')
     *                ORDER BY created_at DESC
     */
    List<Project> findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            Long userId, String keyword);

    /**
     * Count how many projects a user has.
     * Could be used for usage limits or dashboard stats.
     *
     * Generated SQL: SELECT COUNT(*) FROM projects WHERE user_id = ?
     */
    long countByUserId(Long userId);
}
