# Product Requirements Document (PRD)
**Product Name:** HealthVault  
**Tagline:** "One Patient. One Medical History."  
**Document Status:** Draft (Architectural Focus)

---

## 1. Executive Summary

HealthVault is a unified healthcare record management ecosystem. It connects Patients, Doctors, Laboratories, Pharmacies, and Hospital Staff on a single secure platform. By providing a secure digital vault, HealthVault ensures that a patient's complete medical history is always accessible to authorized individuals, improving diagnosis accuracy and empowering families to manage their health records efficiently.

**Note:** HealthVault is a centralized health record management ecosystem, *not* a telemedicine or appointment booking platform.

---

## 2. High-Level System Architecture

The architecture relies on a monolithic backend (Spring Boot) with clear module boundaries, communicating with a modern SPA (React) frontend. It integrates an external OCR Engine for document text extraction and an S3-compatible object store for medical files.

```mermaid
graph TD
    subgraph Frontend["Client Applications (React SPA)"]
        PatientUI[Patient Portal]
        DoctorUI[Doctor Portal]
        LabUI[Lab Portal]
        PharmacyUI[Pharmacy Portal]
    end

    subgraph API_Gateway["API Layer (Spring Boot)"]
        Sec[Spring Security + JWT]
        Gateway[REST Controllers]
    end

    subgraph Business_Logic["Service Layer"]
        AuthSvc[Auth & Roles]
        PatientSvc[Family & Profile]
        DocSvc[Doctor & Access]
        RecordSvc[Record Uploads]
        RxSvc[Prescriptions]
    end

    subgraph External_Services["External integrations"]
        OCR[Tesseract OCR Engine]
        Storage[MinIO / AWS S3 File Storage]
        Email[SMTP / SendGrid]
    end

    subgraph Data_Layer["Persistence"]
        DB[(PostgreSQL Database)]
    end

    PatientUI --> |HTTPS| Sec
    DoctorUI --> |HTTPS| Sec
    LabUI --> |HTTPS| Sec
    PharmacyUI --> |HTTPS| Sec

    Sec --> Gateway
    Gateway --> AuthSvc
    Gateway --> PatientSvc
    Gateway --> DocSvc
    Gateway --> RecordSvc
    Gateway --> RxSvc

    AuthSvc --> DB
    PatientSvc --> DB
    DocSvc --> DB
    RecordSvc --> DB
    RxSvc --> DB

    RecordSvc --> |Extract Text| OCR
    RecordSvc --> |Save Files| Storage
    AuthSvc --> |OTP/Welcome| Email
```

---

## 3. Database Schema (Entity-Relationship Diagram)

The data model isolates authentication (Users) from domain-specific profiles. Family members act as the anchor for medical records to allow one patient account to manage multiple people.

```mermaid
erDiagram
    USERS {
        Long id PK
        String email UK
        String phone UK
        String password
        String role
        Boolean active
    }

    PATIENT_PROFILE {
        Long id PK
        Long user_id FK
        String primary_healthvault_id UK
        String full_name
    }

    FAMILY_MEMBER {
        Long id PK
        Long patient_profile_id FK
        String healthvault_id UK
        String relation "Self, Mother, Father"
        String blood_group
        Date dob
    }

    DOCTOR_PROFILE {
        Long id PK
        Long user_id FK
        String license_number
        String specialization
        String clinic_name
    }

    MEDICAL_RECORD {
        Long id PK
        Long member_id FK
        String category "Blood, XRay"
        Date record_date
        String file_url
        Long uploaded_by FK "Doctor/Lab"
    }

    ACCESS_GRANT {
        Long id PK
        Long member_id FK
        Long doctor_id FK
        Date expires_at
        String status "ACTIVE, REVOKED"
    }

    PRESCRIPTION {
        Long id PK
        Long member_id FK
        Long doctor_id FK
        Date prescribed_date
        String notes
    }

    OCR_METADATA {
        Long id PK
        Long record_id FK
        String extracted_text
        Jsonb key_value_pairs
    }

    USERS ||--o| PATIENT_PROFILE : "has"
    USERS ||--o| DOCTOR_PROFILE : "has"
    PATIENT_PROFILE ||--|{ FAMILY_MEMBER : "manages"
    FAMILY_MEMBER ||--o{ MEDICAL_RECORD : "owns"
    FAMILY_MEMBER ||--o{ PRESCRIPTION : "receives"
    FAMILY_MEMBER ||--o{ ACCESS_GRANT : "grants_access_for"
    DOCTOR_PROFILE ||--o{ ACCESS_GRANT : "receives_access"
    MEDICAL_RECORD ||--o| OCR_METADATA : "analyzed_into"
    DOCTOR_PROFILE ||--o{ PRESCRIPTION : "writes"
```

---

## 4. Core Workflows (Sequence Diagrams)

### 4.1 Doctor Access Grant Flow
This sequence demonstrates how a patient grants a doctor access to their family member's records.

```mermaid
sequenceDiagram
    actor Patient
    participant Frontend
    participant API
    participant AccessService
    participant DB
    actor Doctor

    Patient->>Frontend: Select Family Member & Doctor ID
    Patient->>Frontend: Set Duration (e.g., 30 Days) & Click "Grant"
    Frontend->>API: POST /api/access/grant (MemberID, DoctorID, Duration)
    API->>AccessService: validateGrantRequest()
    AccessService->>DB: Check existing active grants
    AccessService->>DB: Create new AccessGrant Record
    AccessService->>API: return Success
    API->>Frontend: display "Access Granted"
    
    AccessService-->>Doctor: Trigger Notification (Email/In-App)
    
    Doctor->>Frontend: Login & View Assigned Patients
    Frontend->>API: GET /api/doctors/patients
    API->>DB: Query Active Grants for DoctorID
    DB-->>API: Return granted Patient Details
    API-->>Frontend: Display Patient in Doctor's List
```

### 4.2 Medical Record Upload & OCR Extraction Flow
This sequence shows the asynchronous background processing of a medical report upload.

```mermaid
sequenceDiagram
    actor Lab_or_Doctor
    participant API
    participant FileStorage
    participant DB
    participant OCR_Queue
    participant OCR_Engine

    Lab_or_Doctor->>API: POST /api/records/upload (PDF, PatientID)
    API->>FileStorage: saveFile(PDF)
    FileStorage-->>API: return file_url
    API->>DB: insert MedicalRecord(file_url, status='PROCESSING')
    API->>OCR_Queue: push(record_id, file_url)
    API-->>Lab_or_Doctor: HTTP 202 Accepted (Upload Success)

    OCR_Queue->>OCR_Engine: pull task
    OCR_Engine->>FileStorage: fetch PDF
    OCR_Engine->>OCR_Engine: Extract Text (Tesseract)
    OCR_Engine->>OCR_Engine: Parse Key-Value (e.g., Glucose: 90)
    OCR_Engine->>DB: insert OCR_METADATA
    OCR_Engine->>DB: update MedicalRecord(status='COMPLETED')
```

---

## 5. Module Specific Requirements

### Module 1: Family Health Profiles
*   **Hierarchical Management:** The primary account holder registers using Email/Phone. They then create `Family Member` profiles.
*   **HealthVault ID Assignment:** Every member is assigned a system-generated ID (e.g., `HV-938210`). This ID is the primary key for sharing data.

### Module 2: Access Management (The Vault)
*   **Default State:** All records are private. Doctors, Labs, and Pharmacies cannot search for or view a patient's history without an explicit `ACCESS_GRANT`.
*   **Time-To-Live (TTL):** Access grants must have an expiration timestamp. A scheduled cron job will mark expired grants as `EXPIRED`, instantly revoking access.

### Module 3: OCR & Smart Search
*   **Ingestion:** When a PDF/Image is uploaded, it is queued for OCR processing.
*   **Searchability:** Patients and authorized doctors can use a global search bar. The backend performs a full-text search across `OCR_METADATA.extracted_text`, `PRESCRIPTION.notes`, and `MEDICAL_RECORD.category`.

### Module 4: Medical Timeline Builder
*   **Data Aggregation:** The timeline API fetches data from `MEDICAL_RECORD`, `PRESCRIPTION`, and `PHARMACY_RECORDS` sorting them chronologically descending.
*   **UI Representation:** Visual nodes connected by a line, color-coded by category (e.g., Red for Blood Test, Green for Prescription).

---

## 6. Dashboard Layouts (Wireframe Concepts)

### Patient Dashboard
1.  **Header:** Welcome message, active profile selector (Self, Mother, Father).
2.  **Top Cards:** "Total Records", "Active Doctors", "Recent Uploads".
3.  **Main View (Timeline):** Chronological feed of medical events.
4.  **Sidebar:** Navigation (My Records, Share Access, Prescriptions, Settings).

### Doctor Dashboard
1.  **Header:** Doctor Profile, Clinic Name.
2.  **Search Bar:** Global search for "HealthVault ID".
3.  **Main View (Assigned Patients):** List of patients who have granted active access, with a countdown timer showing days left for access.
4.  **Quick Actions:** "Upload Prescription", "Add Clinical Note".

---

## 7. Security & Compliance Guardrails

1.  **No Telemedicine:** Do not build video calls or chat features.
2.  **No Booking System:** Do not build doctor availability calendars or appointment slots.
3.  **Strict RBAC:** API Gateway must strictly intercept and validate roles. A Doctor ID requesting a Patient ID's record must be verified against the `ACCESS_GRANT` table on every single request.
4.  **Audit Logs:** Every read/write operation on a Medical Record must insert a row into an `AUDIT_LOG` table containing Timestamp, UserID, IP Address, and Action.
