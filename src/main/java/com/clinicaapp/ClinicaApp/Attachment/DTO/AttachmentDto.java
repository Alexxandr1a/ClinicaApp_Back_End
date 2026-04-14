package com.clinicaapp.ClinicaApp.Attachment.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentDto {

    private Long id;
    private Long medicalRecordsId;

    private String fileName;
    private String filePath;
    private String fileType;
    private Long sizeBytes;
}
