package com.neeraj.patientservice.mapper;

import com.neeraj.patientservice.dto.PatientRequestDTO;
import com.neeraj.patientservice.dto.PatientResponseDTO;
import com.neeraj.patientservice.entity.Patient;

import java.time.LocalDate;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId().toString())
                .name(patient.getName())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .dateOfBirth(patient.getDateOfBirth().toString())
                .build();
    }

    public static Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .dateOfBirth(LocalDate.parse(dto.getDateOfBirth()))
                .registeredDate(LocalDate.parse(dto.getRegisteredDate()))
                .build();
    }
}
