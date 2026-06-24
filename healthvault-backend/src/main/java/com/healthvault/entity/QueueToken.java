package com.healthvault.entity;

import com.healthvault.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "queue_tokens")
public class QueueToken extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenNumber; // e.g., "T-101"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private FamilyMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // Assuming User entity holds the doctor role

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;

    private LocalDateTime checkInTime;
    private LocalDateTime checkoutTime;
}
