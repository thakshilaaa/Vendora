package com.vendora.epic1.repository;

import com.vendora.epic1.model.PartnershipApplication;
import com.vendora.epic1.model.enums.PartnershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("epic1PartnershipApplicationRepository")
public interface PartnershipApplicationRepository extends JpaRepository<PartnershipApplication, Long> {
    List<PartnershipApplication> findByStatus(PartnershipStatus status);

    boolean existsByEmail(String email);
}
