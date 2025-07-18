package com.people10.dashboard.service;

import com.people10.dashboard.dto.AdminReportResponseMeta;
import com.people10.dashboard.dto.ReportMetaDto;
import com.people10.dashboard.dto.UserRequestDto;
import com.people10.dashboard.dto.UserResponseDto;
import com.people10.dashboard.dto.UserResponseDto.RoleInfo;
import com.people10.dashboard.model.Report;
import com.people10.dashboard.model.Role;
import com.people10.dashboard.model.User;
import com.people10.dashboard.repository.UserRepository;
import com.people10.dashboard.repository.ReportRepository;
import com.people10.dashboard.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ReportRepository reportRepository;

    public Optional<UserResponseDto> setUserRole(Long userId, String roleName) {
        var userOpt = userRepository.findById(userId);
        var roleOpt = roleRepository.findByName(roleName);
        if (userOpt.isEmpty() || roleOpt.isEmpty()) {
            return Optional.empty();
        }
        var user = userOpt.get();
        user.setRole(roleOpt.get());
        userRepository.save(user);
        return Optional.of(convertToUserResponse(user));
    }

    public Optional<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            return Optional.empty();
        }
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setName(userRequestDto.getName());

        Optional<Role> role = roleRepository.findById(userRequestDto.getRoleId());

        if(role.isEmpty()){
            throw new RuntimeException("Role not found with ID: " + userRequestDto.getRoleId());
        }

        user.setRole(role.get());
        user.setActive(true);
        return Optional.of(convertToUserResponse(userRepository.save(user)));
    }

    public Optional<UserResponseDto> updateUser(Long userId, UserRequestDto userRequestDto) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setName(userRequestDto.getName());
                    existingUser.setEmail(userRequestDto.getEmail());
                    //existingUser.setRole(user.getRole());
                    return convertToUserResponse(userRepository.save(existingUser));
                });
    }

    public Optional<UserResponseDto> updateUserStatus(Long userId, boolean active) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setActive(active);
                    return convertToUserResponse(userRepository.save(existingUser));
                });
    }

    public AdminReportResponseMeta getReportsMeta() {
        Map<String, List<ReportMetaDto>> reports = reportRepository.findAll()
                .stream()
                .filter(report -> "OPCO_APPROVED".equalsIgnoreCase(report.getProcessStatus()))
                .collect(groupingBy(
                    report -> report.getStartDate() + "-" + report.getEndDate(),
                    mapping(this::mapToReportMetaDto, toList())
                ));

        AdminReportResponseMeta responseMeta = new AdminReportResponseMeta();
        responseMeta.setReports(reports);
        return responseMeta;
    }

    private ReportMetaDto mapToReportMetaDto(Report report) {
        ReportMetaDto metaDto = new ReportMetaDto();
        metaDto.setReportId(report.getId());
        metaDto.setClientName(report.getClientName());
        metaDto.setManagerName(report.getManagerNameSnapshot());
        metaDto.setOpcoName(report.getOpcoNameSnapshot());
        metaDto.setProcessStatus(report.getProcessStatus());
        if (report.getSummary() != null) {
            metaDto.setDetailedSummary(report.getSummary().getDetail());
            metaDto.setBriefSummary(report.getSummary().getBrief());
        } else {
            metaDto.setDetailedSummary(null);
            metaDto.setBriefSummary(null);
        }
        metaDto.setTeamName(report.getTeamNameSnapshot());
        return metaDto;
    }

    public UserResponseDto convertToUserResponse(User user) {
        UserResponseDto response = new UserResponseDto();
        RoleInfo roleInfo = new RoleInfo();

        roleInfo.setId(user.getRole() != null ? user.getRole().getId() : null);
        roleInfo.setName(user.getRole() != null ? user.getRole().getName() : null);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setActive(user.isActive());
        if (user.getRole() != null) {
            response.setRole(roleInfo);
        }
        return response;
    }
}
