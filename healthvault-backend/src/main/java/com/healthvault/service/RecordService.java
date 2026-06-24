package com.healthvault.service;

import com.healthvault.dto.response.RecordResponse;
import com.healthvault.entity.FamilyMember;
import com.healthvault.entity.MedicalRecord;
import com.healthvault.entity.User;
import com.healthvault.enums.RecordCategory;
import com.healthvault.repository.FamilyMemberRepository;
import com.healthvault.repository.MedicalRecordRepository;
import com.healthvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public RecordResponse uploadRecord(
            String uploaderEmail, 
            Long memberId, 
            MultipartFile file,
            RecordCategory category,
            LocalDate recordDate,
            String doctorName,
            String hospitalName,
            String notes) {
        
        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMember member = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        // Save File
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/records/download/")
                .path(fileName)
                .toUriString();

        MedicalRecord record = MedicalRecord.builder()
                .member(member)
                .category(category)
                .recordDate(recordDate != null ? recordDate : LocalDate.now())
                .doctorName(doctorName)
                .hospitalName(hospitalName)
                .notes(notes)
                .fileUrl(fileDownloadUri)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .uploadedBy(uploader)
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToResponse(saved);
    }

    public List<RecordResponse> getRecordsByMemberId(String email, Long memberId) {
        // Here we could add authorization checks:
        // Patient checks if this is their family member
        // Doctor checks if they have an active grant for this member
        
        return medicalRecordRepository.findByMemberIdOrderByRecordDateDesc(memberId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Resource downloadFile(String fileName) {
        return fileStorageService.loadFileAsResource(fileName);
    }

    private RecordResponse mapToResponse(MedicalRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .memberId(record.getMember().getId())
                .category(record.getCategory())
                .recordDate(record.getRecordDate())
                .doctorName(record.getDoctorName())
                .hospitalName(record.getHospitalName())
                .notes(record.getNotes())
                .fileUrl(record.getFileUrl())
                .originalFileName(record.getOriginalFileName())
                .contentType(record.getContentType())
                .uploadedBy(record.getUploadedBy().getEmail())
                .build();
    }
}
