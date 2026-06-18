package com.healthvault.service;

import com.healthvault.dto.request.FamilyMemberRequest;
import com.healthvault.dto.response.FamilyMemberResponse;
import com.healthvault.entity.FamilyMember;
import com.healthvault.entity.PatientProfile;
import com.healthvault.entity.User;
import com.healthvault.repository.FamilyMemberRepository;
import com.healthvault.repository.PatientProfileRepository;
import com.healthvault.repository.UserRepository;
import com.healthvault.util.HealthVaultIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyMemberRepository familyMemberRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    public List<FamilyMemberResponse> getFamilyMembers(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        PatientProfile profile = patientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return familyMemberRepository.findByPatientProfileIdAndActiveTrue(profile.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FamilyMemberResponse addFamilyMember(String email, FamilyMemberRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        PatientProfile profile = patientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        String uniqueHvId = generateUniqueHvId();

        FamilyMember member = FamilyMember.builder()
                .patientProfile(profile)
                .healthVaultId(uniqueHvId)
                .fullName(request.getFullName())
                .relation(request.getRelation())
                .bloodGroup(request.getBloodGroup())
                .dateOfBirth(request.getDateOfBirth())
                .active(true)
                .build();

        FamilyMember saved = familyMemberRepository.save(member);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteFamilyMember(String email, Long memberId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        PatientProfile profile = patientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        FamilyMember member = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        if (!member.getPatientProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized access to family member");
        }
        
        if ("Self".equalsIgnoreCase(member.getRelation())) {
            throw new RuntimeException("Cannot delete primary Self profile");
        }

        member.setActive(false);
        familyMemberRepository.save(member);
    }

    private FamilyMemberResponse mapToResponse(FamilyMember member) {
        return FamilyMemberResponse.builder()
                .id(member.getId())
                .healthVaultId(member.getHealthVaultId())
                .fullName(member.getFullName())
                .relation(member.getRelation())
                .bloodGroup(member.getBloodGroup())
                .dateOfBirth(member.getDateOfBirth())
                .build();
    }

    private String generateUniqueHvId() {
        String hvId;
        do {
            hvId = HealthVaultIdGenerator.generateId();
        } while (patientProfileRepository.existsByPrimaryHealthVaultId(hvId) || 
                 familyMemberRepository.existsByHealthVaultId(hvId));
        return hvId;
    }
}
