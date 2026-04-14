package com.clinicaapp.ClinicaApp.Attachment.Repo;

import com.clinicaapp.ClinicaApp.Attachment.Entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
    List<Attachment> findByMedicalRecordsId(Long medicalRecordsId);
}
