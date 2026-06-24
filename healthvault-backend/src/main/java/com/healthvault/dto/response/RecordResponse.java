package com.healthvault.dto.response;

import com.healthvault.enums.RecordCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private Long memberId;
    private RecordCategory category;
    private LocalDate recordDate;
    private String doctorName;
    private String hospitalName;
    private String notes;
    private String fileUrl;
    private String originalFileName;
    private String contentType;
    private String uploadedBy;
}
