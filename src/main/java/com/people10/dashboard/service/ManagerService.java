package com.people10.dashboard.service;

import com.people10.dashboard.model.Manager;
import com.people10.dashboard.repository.ManagerRepository;

import lombok.RequiredArgsConstructor;

import com.people10.dashboard.dto.ManagerRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public Manager getManager(Long id) {
        return managerRepository.findById(id).orElse(null);
    }

    public Manager createManager(ManagerRequestDto dto) {
        var manager = new Manager();
        manager.setName(dto.getName());
        return managerRepository.save(manager);
    }

    public Manager updateManager(Long id, ManagerRequestDto dto) {
        var manager = managerRepository.findById(id).orElse(null);
        if (manager == null) return null;
        manager.setName(dto.getName());
        return managerRepository.save(manager);
    }

    public void deleteManager(Long id) {
        managerRepository.deleteById(id);
    }
}
