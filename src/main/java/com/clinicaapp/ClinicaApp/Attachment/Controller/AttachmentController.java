package com.clinicaapp.ClinicaApp.Attachment.Controller;


import com.clinicaapp.ClinicaApp.Attachment.DTO.AttachmentDto;
import com.clinicaapp.ClinicaApp.Attachment.Entity.Attachment;
import com.clinicaapp.ClinicaApp.Attachment.Repo.AttachmentRepo;
import com.clinicaapp.ClinicaApp.Attachment.Service.AttachmentService;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records/{medicalRecordsId}/attachments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentRepo attachmentRepo;

    @PostMapping
    public ResponseEntity<Response<AttachmentDto>> upload(
           @PathVariable Long medicalRecordsId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        Response<AttachmentDto> response = attachmentService.upload(medicalRecordsId, arquivo);
        int status = response.getStatusCode() > 0 ? response.getStatusCode() : HttpStatus.OK.value();
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<List<AttachmentDto>>> listByMedicalRecords(
            @PathVariable Long medicalRecordsId
    ) {
        Response<List<AttachmentDto>> response = attachmentService.listByMedicalRecords(medicalRecordsId);
        int status = response.getStatusCode() > 0 ? response.getStatusCode() : HttpStatus.OK.value();
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Response<Void>> delete(
            @PathVariable Long medicalRecordsId,
            @PathVariable Long attachmentId
    ) {
        Response<Void> response = attachmentService.delete(attachmentId);
        int status = response.getStatusCode() > 0 ? response.getStatusCode() : HttpStatus.OK.value();
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long medicalRecordsId,
            @PathVariable Long attachmentId
    ) {
        Attachment attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));

        if (attachment.getMedicalRecords() == null
                || attachment.getMedicalRecords().getId() == null
                || !attachment.getMedicalRecords().getId().equals(medicalRecordsId)) {
            throw new RuntimeException("Attachment does not belong to medical record: " + medicalRecordsId);
        }

        try {
            Path filePath = Path.of(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

            String filename = attachment.getFileName() != null ? attachment.getFileName() : "attachment";
            String safeFilename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            String contentType = attachment.getFileType() != null ? attachment.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Download failed: " + e.getMessage());
        }
    }
}
