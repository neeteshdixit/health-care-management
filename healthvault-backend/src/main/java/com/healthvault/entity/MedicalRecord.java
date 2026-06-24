package com.healthvault.entity;

import com.healthvault.enums.RecordCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medical_records")
public class MedicalRecord extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private FamilyMember member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordCategory category;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "hospital_name")
    private String hospitalName;

    private String notes;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Column(name = "original_file_name")
    private String originalFileName;
    
    @Column(name = "content_type")
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;
}
