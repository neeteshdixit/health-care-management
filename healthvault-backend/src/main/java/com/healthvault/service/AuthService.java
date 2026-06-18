package com.healthvault.service;

import com.healthvault.dto.request.LoginRequest;
import com.healthvault.dto.request.RegisterRequest;
import com.healthvault.dto.response.AuthResponse;
import com.healthvault.entity.FamilyMember;
import com.healthvault.entity.PatientProfile;
import com.healthvault.entity.User;
import com.healthvault.enums.Role;
import com.healthvault.repository.FamilyMemberRepository;
import com.healthvault.repository.PatientProfileRepository;
import com.healthvault.repository.UserRepository;
import com.healthvault.security.JwtTokenProvider;
import com.healthvault.util.HealthVaultIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);

        // If the registering user is a PATIENT, initialize their Profile and "Self" Family Member
        if (request.getRole() == Role.PATIENT) {
            String primaryHvId = generateUniqueHvId();
            
            PatientProfile profile = PatientProfile.builder()
                    .user(user)
                    .primaryHealthVaultId(primaryHvId)
                    .fullName(request.getEmail().split("@")[0]) // Default name from email
                    .build();
            patientProfileRepository.save(profile);

            FamilyMember self = FamilyMember.builder()
                    .patientProfile(profile)
                    .healthVaultId(primaryHvId)
                    .fullName(profile.getFullName())
                    .relation("Self")
                    .active(true)
                    .build();
            familyMemberRepository.save(self);
        }

        String jwtToken = jwtTokenProvider.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
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
