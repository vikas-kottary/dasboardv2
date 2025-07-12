package com.people10.dashboard.service;

// import com.people10.dashboard.model.Opco;
// import com.people10.dashboard.repository.OpcoRepository;

import lombok.RequiredArgsConstructor;

import com.people10.dashboard.dto.OpcoRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpcoService {
    // private final OpcoRepository opcoRepository;

    // public List<Opco> getAllOpcos() {
    //     return opcoRepository.findAll();
    // }

    // public Opco getOpco(Long id) {
    //     return opcoRepository.findById(id).orElse(null);
    // }

    // public Opco createOpco(OpcoRequestDto dto) {
    //     var opco = new Opco();
    //     opco.setName(dto.getName());
    //     return opcoRepository.save(opco);
    // }

    // public Opco updateOpco(Long id, OpcoRequestDto dto) {
    //     var opco = opcoRepository.findById(id).orElse(null);
    //     if (opco == null) return null;
    //     opco.setName(dto.getName());
    //     return opcoRepository.save(opco);
    // }

    // public void deleteOpco(Long id) {
    //     opcoRepository.deleteById(id);
    // }
}
