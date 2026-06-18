package com.healthvault.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FamilyMemberRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Relation is required")
    private String relation;

    private String bloodGroup;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
