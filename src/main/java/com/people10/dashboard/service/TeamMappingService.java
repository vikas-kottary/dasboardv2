package com.people10.dashboard.service;

import com.people10.dashboard.dto.AdminTeamMappingResponse;
import com.people10.dashboard.dto.TeamMappingResponse;
import com.people10.dashboard.dto.TeamMappingResponse.OpcoInfo;
import com.people10.dashboard.dto.TeamRequestDto;
import com.people10.dashboard.model.TeamMapping;
import com.people10.dashboard.repository.TeamMappingRepository;
import com.people10.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMappingService {
    private final TeamMappingRepository teamMappingRepository;
    private final UserRepository userRepository;

    public Optional<AdminTeamMappingResponse> createTeamMapping(TeamRequestDto teamRequestDto) {
        var managerOpt = userRepository.findById(teamRequestDto.getManagerId());
        var opcoOpt = userRepository.findById(teamRequestDto.getOpcoId());
        
        if (managerOpt.isEmpty() || opcoOpt.isEmpty()) {
            throw new RuntimeException("Not able to find manager or opco user with given IDs");
        }

        var teamMapping = TeamMapping.builder()
                .name(teamRequestDto.getName())
                .client(teamRequestDto.getClient())
                .manager(managerOpt.get())
                .opco(opcoOpt.get())
                .isActive(true)
                .build();

        return Optional.of(teamMappingRepository.save(teamMapping))
                .map(this::convertToResponse);
    }

    public Optional<AdminTeamMappingResponse> updateTeamMapping(Long teamMappingId, TeamRequestDto teamRequestDto) {
        return teamMappingRepository.findById(teamMappingId)
                .map(existingTeamMapping -> {
                    existingTeamMapping.setName(teamRequestDto.getName());
                    existingTeamMapping.setClient(teamRequestDto.getClient());
                    
                    var managerOpt = userRepository.findById(teamRequestDto.getManagerId());
                    var opcoOpt = userRepository.findById(teamRequestDto.getOpcoId());
                    
                    if (managerOpt.isPresent()) {
                        existingTeamMapping.setManager(managerOpt.get());
                    }
                    if (opcoOpt.isPresent()) {
                        existingTeamMapping.setOpco(opcoOpt.get());
                    }

                    return convertToResponse(teamMappingRepository.save(existingTeamMapping));
                });
    }

    public Optional<AdminTeamMappingResponse> updateTeamMappingStatus(Long teamMappingId, boolean active) {
        return teamMappingRepository.findById(teamMappingId)
                .map(existingTeamMapping -> {
                    existingTeamMapping.setActive(active);
                    return convertToResponse(teamMappingRepository.save(existingTeamMapping));
                });
    }


    public AdminTeamMappingResponse convertToResponse(TeamMapping teamMapping) {
        AdminTeamMappingResponse response = new AdminTeamMappingResponse();
        AdminTeamMappingResponse.OpcoInfo opcoInfo = new AdminTeamMappingResponse.OpcoInfo();
        AdminTeamMappingResponse.ManagerInfo managerInfo = new AdminTeamMappingResponse.ManagerInfo();

        managerInfo.setId(teamMapping.getManager().getId());
        managerInfo.setName(teamMapping.getManager().getName());

        opcoInfo.setId(teamMapping.getOpco().getId());
        opcoInfo.setName(teamMapping.getOpco().getName());

        response.setId(teamMapping.getId());
        response.setName(teamMapping.getName());
        response.setClient(teamMapping.getClient());
        response.setManager(managerInfo);
        response.setOpco(opcoInfo);
        response.setActive(teamMapping.isActive());
        return response;
    }
}
