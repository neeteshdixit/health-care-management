package com.healthvault.service;

import com.healthvault.dto.response.PatientProfileResponse;
import com.healthvault.entity.PatientProfile;
import com.healthvault.entity.User;
import com.healthvault.repository.PatientProfileRepository;
import com.healthvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    public PatientProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        PatientProfile profile = patientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return PatientProfileResponse.builder()
                .id(profile.getId())
                .primaryHealthVaultId(profile.getPrimaryHealthVaultId())
                .fullName(profile.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
