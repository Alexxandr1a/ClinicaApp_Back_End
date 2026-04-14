package com.clinicaapp.ClinicaApp.Attachment.Service;

import com.clinicaapp.ClinicaApp.Attachment.DTO.AttachmentDto;
import com.clinicaapp.ClinicaApp.Attachment.Entity.Attachment;
import com.clinicaapp.ClinicaApp.Attachment.Repo.AttachmentRepo;
import com.clinicaapp.ClinicaApp.MedicalRecord.Entity.MedicalRecords;
import com.clinicaapp.ClinicaApp.MedicalRecord.Repo.MedicalRecordsRepo;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepo attachmentRepo;
    private final MedicalRecordsRepo medicalRecordsRepo;
    private final ModelMapper modelMapper;

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES =
            List.of("application/pdf", "image/jpeg", "image/jpg", "image/png");

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    private Path resolveUploadBaseDir() {
        // Evita caminho relativo resolver dentro do diretório temporário do Tomcat
        // (work/Tomcat/localhost/ROOT/...), garantindo uma pasta estável na máquina.
        Path configured = Paths.get(uploadDir);
        return configured.isAbsolute()
                ? configured
                : Paths.get(System.getProperty("user.dir")).resolve(configured).normalize();
    }

    @Transactional
    public Response<AttachmentDto> upload(Long medicalRecordsId, MultipartFile arquivo) {
        try {
            MedicalRecords medicalRecords = medicalRecordsRepo.findById(medicalRecordsId)
                    .orElseThrow(() -> new RuntimeException("Medical record not found: " + medicalRecordsId));

            if (arquivo == null || arquivo.isEmpty()) {
                return Response.<AttachmentDto>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Arquivo ausente")
                        .build();
            }

            if (arquivo.getSize() > MAX_SIZE_BYTES) {
                return Response.<AttachmentDto>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Arquivo muito grande. Máximo: 10MB")
                        .build();
            }

            String tipo = arquivo.getContentType();
            if (tipo == null || !ALLOWED_TYPES.contains(tipo)) {
                return Response.<AttachmentDto>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Tipo não permitido. Use: PDF, JPG ou PNG")
                        .build();
            }

            String originalName = arquivo.getOriginalFilename();
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String uniqueName = UUID.randomUUID() + extension;

            Path baseDir = resolveUploadBaseDir();
            Path dir = baseDir.resolve(Paths.get("medical-records", String.valueOf(medicalRecordsId)));
            Files.createDirectories(dir);

            Path savedPath = dir.resolve(uniqueName).normalize();
            // Garante que não escapou da pasta base
            if (!savedPath.startsWith(baseDir)) {
                return Response.<AttachmentDto>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Caminho de upload inválido")
                        .build();
            }

            arquivo.transferTo(savedPath);

            Attachment attachment = Attachment.builder()
                    .medicalRecords(medicalRecords)
                    .fileName(originalName)
                    .filePath(savedPath.toString())
                    .fileType(tipo)
                    .sizeBytes(arquivo.getSize())
                    .build();

            Attachment saved = attachmentRepo.save(attachment);

            AttachmentDto dto = modelMapper.map(saved, AttachmentDto.class);
            dto.setMedicalRecordsId(medicalRecordsId);

            return Response.<AttachmentDto>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Attachment uploaded")
                    .data(dto)
                    .build();

        } catch (Exception e) {
            return Response.<AttachmentDto>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao enviar anexo: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public Response<List<AttachmentDto>> listByMedicalRecords(Long medicalRecordsId) {
        try {
            List<AttachmentDto> dtos = attachmentRepo.findByMedicalRecordsId(medicalRecordsId).stream()
                    .map(a -> {
                        AttachmentDto dto = modelMapper.map(a, AttachmentDto.class);
                        dto.setMedicalRecordsId(medicalRecordsId);
                        return dto;
                    })
                    .toList();

            return Response.<List<AttachmentDto>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .data(dtos)
                    .build();
        } catch (Exception e) {
            return Response.<List<AttachmentDto>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao listar anexos: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<Void> delete(Long attachmentId) {
        try {
            Attachment attachment = attachmentRepo.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));

            try {
                if (attachment.getFilePath() != null) {
                    Files.deleteIfExists(Paths.get(attachment.getFilePath()));
                }
            } catch (IOException ignored) {
                // Mantém o delete do registro mesmo se o arquivo já não existir
            }

            attachmentRepo.deleteById(attachmentId);

            return Response.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .message("Attachment deleted")
                    .build();
        } catch (Exception e) {
            return Response.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao excluir anexo: " + e.getMessage())
                    .build();
        }
    }
}