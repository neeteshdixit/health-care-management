package com.healthvault.repository;

import com.healthvault.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByPatientProfileIdAndActiveTrue(Long patientProfileId);
    Optional<FamilyMember> findByHealthVaultId(String healthVaultId);
    boolean existsByHealthVaultId(String healthVaultId);
}
