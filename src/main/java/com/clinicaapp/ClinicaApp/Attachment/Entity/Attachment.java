package com.clinicaapp.ClinicaApp.Attachment.Entity;

import com.clinicaapp.ClinicaApp.MedicalRecord.Entity.MedicalRecords;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 2000)
    private String filePath;

    @Column(length = 100)
    private String fileType;

    private Long sizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_records_id", nullable = false)
    private MedicalRecords medicalRecords;
}
