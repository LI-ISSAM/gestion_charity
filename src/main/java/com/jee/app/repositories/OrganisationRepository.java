package com.jee.app.repositories;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jee.app.enums.OrganisationsStatus;
import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganisationRepository extends JpaRepository<Organisation,Long>{
    Optional<Organisation> findByManager(Users manager);
    Optional<Organisation> findByManagerId(Long managerId);
    List<Organisation> findByStatus(OrganisationsStatus status);
    boolean existsByManagerId(Long managerId);
    Page<Organisation> findByStatus(OrganisationsStatus status, Pageable pageable);


}