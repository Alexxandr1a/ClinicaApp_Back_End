package com.clinicaapp.ClinicaApp.MedicalRecord.Controller;


import com.clinicaapp.ClinicaApp.MedicalRecord.DTO.MedicalRecordsDto;
import com.clinicaapp.ClinicaApp.MedicalRecord.Service.MedicalRecordsService;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicalRecordsController {
    private final MedicalRecordsService medicalRecordsService;

    @GetMapping
    public ResponseEntity<Response<Page<MedicalRecordsDto>>> listar(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "date") Pageable pageable
    ) {
        return ResponseEntity.ok(medicalRecordsService.listar(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MedicalRecordsDto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordsService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Response<MedicalRecordsDto>> criar(@RequestBody MedicalRecordsDto dto) {
        return ResponseEntity.ok(medicalRecordsService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<MedicalRecordsDto>> atualizar(
            @PathVariable Long id,
            @RequestBody MedicalRecordsDto dto
    ) {
        return ResponseEntity.ok(medicalRecordsService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> excluir(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordsService.excluir(id));
    }
}
