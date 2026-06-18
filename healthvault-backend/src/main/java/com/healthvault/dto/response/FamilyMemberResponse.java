package com.healthvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberResponse {
    private Long id;
    private String healthVaultId;
    private String fullName;
    private String relation;
    private String bloodGroup;
    private LocalDate dateOfBirth;
}
