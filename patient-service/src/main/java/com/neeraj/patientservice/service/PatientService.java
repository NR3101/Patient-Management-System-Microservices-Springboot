package com.neeraj.patientservice.service;

import com.neeraj.patientservice.dto.PatientRequestDTO;
import com.neeraj.patientservice.dto.PatientResponseDTO;
import com.neeraj.patientservice.entity.Patient;
import com.neeraj.patientservice.exception.EmailAlreadyExistsException;
import com.neeraj.patientservice.exception.PatientNotFoundException;
import com.neeraj.patientservice.mapper.PatientMapper;
import com.neeraj.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<PatientResponseDTO> getAllPatients() {
        log.info("Fetching all patients from the database");

        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO getPatient(UUID id) {
        log.info("Fetching patient with ID {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new PatientNotFoundException("Patient not found with ID " + id));
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO createPatient(PatientRequestDTO dto) {
        log.info("Creating PatientResponseDTO from PatientRequestDTO");
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("User with email " + dto.getEmail() + " already exists");
        }

        Patient createdPatient = patientRepository.save(PatientMapper.toEntity(dto));
        return PatientMapper.toDTO(createdPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO dto) {
        log.info("Updating patient with ID {}", id);
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() ->
                new PatientNotFoundException("Patient not found with ID " + id));
        if (dto.getEmail() != null && patientRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("User with email " + dto.getEmail() + " already exists");
        }

        existingPatient.setName(dto.getName());
        existingPatient.setEmail(dto.getEmail());
        existingPatient.setAddress(dto.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(existingPatient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        log.info("Deleting patient with ID {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new PatientNotFoundException("Patient not found with ID " + id));
        patientRepository.delete(patient);
    }


}
