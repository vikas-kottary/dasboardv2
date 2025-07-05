package com.people10.dashboard.service;

import com.people10.dashboard.model.Team;
import com.people10.dashboard.model.Manager;
import com.people10.dashboard.model.Opco;
import com.people10.dashboard.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

import com.people10.dashboard.repository.ManagerRepository;
import com.people10.dashboard.repository.OpcoRepository;
import com.people10.dashboard.dto.TeamRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final ManagerRepository managerRepository;
    private final OpcoRepository opcoRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team createTeam(TeamRequestDto dto) {
        var team = new Team();
        team.setName(dto.getName());
        team.setClient(dto.getClient());
        Manager manager = managerRepository.findById(dto.getManagerId()).orElse(null);
        Opco opco = opcoRepository.findById(dto.getOpcoId()).orElse(null);
        team.setManager(manager);
        team.setOpco(opco);
        return teamRepository.save(team);
    }

    public Team updateTeam(Long id, TeamRequestDto dto) {
        var team = teamRepository.findById(id).orElse(null);
        if (team == null) return null;
        team.setName(dto.getName());
        team.setClient(dto.getClient());
        Manager manager = managerRepository.findById(dto.getManagerId()).orElse(null);
        Opco opco = opcoRepository.findById(dto.getOpcoId()).orElse(null);
        team.setManager(manager);
        team.setOpco(opco);
        return teamRepository.save(team);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}
