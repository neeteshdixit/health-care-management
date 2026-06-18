package com.healthvault.controller;

import com.healthvault.dto.request.FamilyMemberRequest;
import com.healthvault.dto.response.FamilyMemberResponse;
import com.healthvault.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping
    public ResponseEntity<List<FamilyMemberResponse>> getFamilyMembers(Authentication authentication) {
        return ResponseEntity.ok(familyService.getFamilyMembers(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<FamilyMemberResponse> addFamilyMember(
            @Valid @RequestBody FamilyMemberRequest request, 
            Authentication authentication) {
        return ResponseEntity.ok(familyService.addFamilyMember(authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFamilyMember(
            @PathVariable Long id, 
            Authentication authentication) {
        familyService.deleteFamilyMember(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
