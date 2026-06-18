package com.healthvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse {
    private Long id;
    private String primaryHealthVaultId;
    private String fullName;
    private String email;
    private String phone;
}
