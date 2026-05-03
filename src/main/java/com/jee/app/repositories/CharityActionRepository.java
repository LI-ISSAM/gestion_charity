package com.jee.app.repositories;
import com.jee.app.enums.ActionStatus;
import com.jee.app.enums.Category;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharityActionRepository
        extends JpaRepository<CharityAction, Long> {

    List<CharityAction> findByOrganisation(Organisation organisation);
    List<CharityAction> findByOrganisationAndStatus(
            Organisation organisation, ActionStatus status);
    Page<CharityAction> findByStatus(ActionStatus status, Pageable pageable);
    Page<CharityAction> findByStatusAndCategory(
            ActionStatus status, Category category, Pageable pageable);
    List<CharityAction> findByStatusOrderByCreatedAtDesc(ActionStatus status);
    List<CharityAction> findByOrganisationAndStatusOrderByCreatedAtDesc(
    Organisation organisation, ActionStatus status);
        @Query("SELECT a FROM CharityAction a WHERE a.status = :status " +
        "AND (:keyword IS NULL OR " +
        "LOWER(a.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
        "LOWER(a.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
        "LOWER(a.organisation.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
        "AND (:category IS NULL OR a.category = :category) " +
        "AND (:location IS NULL OR " +
        "LOWER(a.location) LIKE LOWER(CONCAT('%', CAST(:location AS string), '%')))")
        Page<CharityAction> search(
                @Param("status") ActionStatus status,
                @Param("keyword") String keyword,
                @Param("category") Category category,
                @Param("location") String location,
                PageRequest pageable);

}