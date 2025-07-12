package com.people10.dashboard.service;

import com.people10.dashboard.dto.ReportDto;
import com.people10.dashboard.dto.ReportResponseDto;
import com.people10.dashboard.dto.ReportStatus;
import com.people10.dashboard.dto.RiskDto;
import com.people10.dashboard.dto.ShowcaseDto;
import com.people10.dashboard.dto.TimesheetsDto;
import com.people10.dashboard.dto.TrainingsDto;
import com.people10.dashboard.dto.WorkloadVisibilityDto;
import com.people10.dashboard.dto.AdequateQualityDto;
import com.people10.dashboard.dto.BillabilityDto;
import com.people10.dashboard.dto.EscalationsDto;
import com.people10.dashboard.dto.ImprovementDto;
import com.people10.dashboard.dto.InnovationDto;
import com.people10.dashboard.dto.MilestoneDto;
import com.people10.dashboard.dto.NonAdherenceDto;
import com.people10.dashboard.model.Comment;
import com.people10.dashboard.model.Report;
import com.people10.dashboard.model.User;
import com.people10.dashboard.model.report.Summary;
import com.people10.dashboard.model.report.Improvement;
import com.people10.dashboard.model.report.Showcase;
import com.people10.dashboard.model.report.WorkloadVisibility;
import com.people10.dashboard.model.report.AdequateQuality;
import com.people10.dashboard.model.report.Escalation;
import com.people10.dashboard.model.report.Training;
import com.people10.dashboard.model.report.Billability;
import com.people10.dashboard.model.report.NonAdherence;
import com.people10.dashboard.model.report.ReportHistory;
import com.people10.dashboard.model.report.Timesheet;
import com.people10.dashboard.model.report.Innovation;
import com.people10.dashboard.model.report.Milestone;
import com.people10.dashboard.model.report.Risk;
import com.people10.dashboard.repository.ReportRepository;
// import com.people10.dashboard.repository.TeamRepository;
// import com.people10.dashboard.repository.ManagerRepository;
// import com.people10.dashboard.repository.OpcoRepository;
import com.people10.dashboard.repository.MilestoneRepository;
import com.people10.dashboard.repository.WorkloadVisibilityRepository;
import com.people10.dashboard.repository.AdequateQualityRepository;
import com.people10.dashboard.repository.EscalationRepository;
import com.people10.dashboard.repository.TrainingRepository;
import com.people10.dashboard.repository.UserRepository;
import com.people10.dashboard.repository.BillabilityRepository;
import com.people10.dashboard.repository.CommentRepository;
import com.people10.dashboard.repository.ImprovementRepository;
import com.people10.dashboard.repository.NonAdherenceRepository;
import com.people10.dashboard.repository.TimesheetRepository;
import com.people10.dashboard.repository.InnovationRepository;
import com.people10.dashboard.repository.RiskRepository;
import com.people10.dashboard.repository.ShowcaseRepository;
import com.people10.dashboard.repository.SummaryRepository;
import com.people10.dashboard.repository.TeamMappingRepository;
import com.people10.dashboard.repository.ReportHistoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    // private final TeamRepository teamRepository;
    // private final ManagerRepository managerRepository;
    // private final OpcoRepository opcoRepository;
    private final MilestoneRepository milestoneRepository;
    private final WorkloadVisibilityRepository workloadVisibilityRepository;
    private final AdequateQualityRepository adequateQualityRepository;
    private final EscalationRepository escalationRepository;
    private final TrainingRepository trainingRepository;
    private final BillabilityRepository billabilityRepository;
    private final ImprovementRepository improvementRepository;
    private final NonAdherenceRepository nonAdherenceRepository;
    private final TimesheetRepository timesheetRepository;
    private final InnovationRepository innovationRepository;
    private final RiskRepository riskRepository;
    private final ShowcaseRepository showcaseRepository;
    private final SummaryRepository summaryRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TeamMappingRepository teamMappingRepository;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ReportResponseDto> getAllReports() {
        List<ReportResponseDto> reportDtos = new ArrayList<>();
        for (Report report : reportRepository.findAll()) {
            reportDtos.add(convertToDto(report));
        }
        return reportDtos;
    }

    public ReportResponseDto getReport(Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        return convertToDto(report);
    }

    @Transactional
    public ReportResponseDto createReport(ReportDto dto) {
        // Create and save the main report
        Report report = new Report();
        report.setTeamMapping(teamMappingRepository.findById(dto.getTeamId()).orElse(null));
        report.setManager(userRepository.findById(dto.getManagerId()).orElse(null));
        report.setOpco(userRepository.findById(dto.getOpcoId()).orElse(null));
        report.setClientName(dto.getClientName());
        report.setProcessStatus("SUBMITTED");
        report.setOpcoNameSnapshot(userRepository.findById(dto.getOpcoId()).map(User::getName).orElse(null));
        report.setManagerNameSnapshot(userRepository.findById(dto.getManagerId()).map(User::getName).orElse(null));
        report.setTeamNameSnapshot(teamMappingRepository.findById(dto.getTeamId())
                .map(team -> team.getName()).orElse(null));

        try {
            report.setStartDate(dto.getStartDate());
            report.setEndDate(dto.getEndDate());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format. Use yyyy-MM-dd", e);
        }

        report.setCreatedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);

        if (dto.getSummary() != null) {
            Summary summary = new Summary();
            summary.setReport(savedReport);
            summary.setDetail(dto.getSummary());
            summaryRepository.save(summary);
            savedReport.setSummary(summary);
        }

        if(dto.getComment() != null) {
            Comment comment = new Comment();
            comment.setReport(savedReport);
            comment.setComment(dto.getComment().getComment());
            comment.setUser(userRepository.findById(dto.getComment().getUserId()).orElse(null));
            commentRepository.save(comment);
            savedReport.getComments().add(comment);
        }

        // Process milestones
        if (dto.getMilestones() != null) {
            List<Milestone> milestones = new ArrayList<>();
            for (MilestoneDto milestoneDto : dto.getMilestones()) {
                Milestone milestone = new Milestone();
                milestone.setReport(savedReport);
                milestone.setSequenceNo(milestoneDto.getSequenceNo());
                milestone.setProjectName(milestoneDto.getProjectName());
                milestone.setDetail(milestoneDto.getDetail());
                milestone.setMilestoneDate(milestoneDto.getMilestoneDate());
                milestone.setRagStatusId(milestoneDto.getRagStatusId());
                milestones.add(milestone);
            }
            milestoneRepository.saveAll(milestones);
            savedReport.setMilestones(milestones);
        }

        // Process workload visibility
        if (dto.getWorkloadVisibility() != null) {
            WorkloadVisibility workloadVisibility = new WorkloadVisibility();
            workloadVisibility.setReport(savedReport);
            workloadVisibility.setValue(dto.getWorkloadVisibility().getValue());
            workloadVisibility.setRagStatusId(dto.getWorkloadVisibility().getRagStatusId());
            workloadVisibilityRepository.save(workloadVisibility);
            savedReport.setWorkloadVisibility(workloadVisibility);
        }

        // Process adequate quality
        if (dto.getAdequateQuality() != null) {
            AdequateQuality adequateQuality = new AdequateQuality();
            adequateQuality.setReport(savedReport);
            adequateQuality.setValue(dto.getAdequateQuality().getValue());
            adequateQuality.setRagStatusId(dto.getAdequateQuality().getRagStatusId());
            adequateQualityRepository.save(adequateQuality);
            savedReport.setAdequateQuality(adequateQuality);
        }

        // Process escalations
        if (dto.getEscalations() != null) {
            Escalation escalation = new Escalation();
            escalation.setReport(savedReport);
            escalation.setHasEscalation(dto.getEscalations().isHasEscalation());
            escalation.setDetails(dto.getEscalations().getDetails());
            escalation.setRagStatusId(dto.getEscalations().getRagStatusId());
            escalationRepository.save(escalation);
            savedReport.setEscalation(escalation);
        }

        // Process trainings
        if (dto.getTrainings() != null) {
            Training training = new Training();
            training.setReport(savedReport);
            training.setTrainingDetails(dto.getTrainings().getTrainingDetails());
            training.setTotalHours((float) dto.getTrainings().getTotalHours());
            training.setRagStatusId(dto.getTrainings().getRagStatusId());
            trainingRepository.save(training);
            savedReport.setTraining(training);
        }

        // Process billability
        if (dto.getBillability() != null) {
            Billability billability = new Billability();
            billability.setReport(savedReport);
            billability.setBilledResources(dto.getBillability().getBilledResources());
            billability.setUnbilledResources(dto.getBillability().getUnbilledResources());
            billability.setLeavesBilled(dto.getBillability().getLeavesBilled());
            billability.setLeavesUnbilled(dto.getBillability().getLeavesUnbilled());
            billability.setHolidays(dto.getBillability().getHolidays());
            billability.setOverallBillabilityPercent(dto.getBillability().getOverallBillabilityPercent());
            billability.setRagStatusId(dto.getBillability().getRagStatusId());
            billabilityRepository.save(billability);
            savedReport.setBillability(billability);
        }

        // Process improvements
        if (dto.getImprovements() != null) {
            List<Improvement> improvements = new ArrayList<>();
            for (ImprovementDto improvementDto : dto.getImprovements()) {
                Improvement improvement = new Improvement();
                improvement.setReport(savedReport);
                improvement.setSequenceNo(improvementDto.getSequenceNo());
                improvement.setArea(improvementDto.getArea());
                improvement.setValueAddition(improvementDto.getValueAddition());
                improvement.setRagStatusId(improvementDto.getRagStatusId());
                improvements.add(improvement);
            }
            improvementRepository.saveAll(improvements);
            savedReport.setImprovements(improvements);
        }

        // Process non-adherence
        if (dto.getNonAdherence() != null) {
            NonAdherence nonAdherence = new NonAdherence();
            nonAdherence.setReport(savedReport);
            nonAdherence.setNonAdherenceValue(dto.getNonAdherence().getNonAdherenceValue());
            nonAdherence.setCount(dto.getNonAdherence().getCount());
            nonAdherence.setImpact(dto.getNonAdherence().getImpact());
            nonAdherence.setTimeToResolve(dto.getNonAdherence().getTimeToResolve());
            nonAdherence.setRagStatusId(dto.getNonAdherence().getRagStatusId());
            nonAdherenceRepository.save(nonAdherence);
            savedReport.setNonAdherence(nonAdherence);
        }

        // Process timesheets
        if (dto.getTimesheets() != null) {
            Timesheet timesheet = new Timesheet();
            timesheet.setReport(savedReport);
            timesheet.setClientDefaulters(dto.getTimesheets().getClientDefaulters());
            timesheet.setErpDefaulters(dto.getTimesheets().getErpDefaulters());
            timesheet.setRagStatusId(dto.getTimesheets().getRagStatusId());
            timesheetRepository.save(timesheet);
            savedReport.setTimesheet(timesheet);
        }

        // Process innovation
        if (dto.getInnovation() != null) {
            Innovation innovation = new Innovation();
            innovation.setReport(savedReport);
            innovation.setDetails(dto.getInnovation().getDetails());
            innovation.setValueAdded(dto.getInnovation().getValueAdded());
            innovation.setRagStatusId(dto.getInnovation().getRagStatusId());
            innovationRepository.save(innovation);
            savedReport.setInnovation(innovation);
        }

        // Process risk
        if (dto.getRisk() != null) {
            Risk risk = new Risk();
            risk.setReport(savedReport);
            risk.setRiskValue(dto.getRisk().getRiskValue());
            risk.setDetails(dto.getRisk().getDetails());
            risk.setRagStatusId(dto.getRisk().getRagStatusId());
            riskRepository.save(risk);
            savedReport.setRisk(risk);
        }

        // Process showcases
        if (dto.getShowcases() != null) {
            List<Showcase> showcases = new ArrayList<>();
            for (ShowcaseDto showcaseDto : dto.getShowcases()) {
                Showcase showcase = new Showcase();
                showcase.setReport(savedReport);
                showcase.setSequenceNo(showcaseDto.getSequenceNo());
                showcase.setDetail(showcaseDto.getDetail());
                showcases.add(showcase);
            }
            showcaseRepository.saveAll(showcases);
            savedReport.setShowcases(showcases);
        }

        if (savedReport != null) {
            ReportHistory history = new ReportHistory();
            history.setReportId(savedReport.getId());
            history.setChangedBy("Default"); 
            history.setNewStatus(ReportStatus.SUBMITTED);
            history.setComment("New report created");
            reportHistoryRepository.save(history);
        }

        return convertToDto(savedReport);
    }

    @Transactional
    public ReportResponseDto updateReport(Long id, ReportDto dto) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            throw new RuntimeException("Report not found with id: " + id);
        }

        // Update main report details
        report.setTeamMapping(teamMappingRepository.findById(dto.getTeamId()).orElse(null));
        report.setManager(userRepository.findById(dto.getManagerId()).orElse(null));
        report.setOpco(userRepository.findById(dto.getOpcoId()).orElse(null));
        report.setClientName(dto.getClientName());

        report.setStartDate(dto.getStartDate());
        report.setEndDate(dto.getEndDate());

        // Clear existing related entities
        if (report.getMilestones() != null) {
            milestoneRepository.deleteAll(report.getMilestones());
            report.getMilestones().clear();
        }

        if (report.getImprovements() != null) {
            improvementRepository.deleteAll(report.getImprovements());
            report.getImprovements().clear();
        }

        if (report.getShowcases() != null) {
            showcaseRepository.deleteAll(report.getShowcases());
            report.getShowcases().clear();
        }

        if (report.getWorkloadVisibility() != null) {
            workloadVisibilityRepository.delete(report.getWorkloadVisibility());
            report.setWorkloadVisibility(null);
        }

        if (report.getAdequateQuality() != null) {
            adequateQualityRepository.delete(report.getAdequateQuality());
            report.setAdequateQuality(null);
        }

        if (report.getEscalation() != null) {
            escalationRepository.delete(report.getEscalation());
            report.setEscalation(null);
        }

        if (report.getTraining() != null) {
            trainingRepository.delete(report.getTraining());
            report.setTraining(null);
        }

        if (report.getBillability() != null) {
            billabilityRepository.delete(report.getBillability());
            report.setBillability(null);
        }

        if (report.getNonAdherence() != null) {
            nonAdherenceRepository.delete(report.getNonAdherence());
            report.setNonAdherence(null);
        }

        if (report.getTimesheet() != null) {
            timesheetRepository.delete(report.getTimesheet());
            report.setTimesheet(null);
        }

        if (report.getInnovation() != null) {
            innovationRepository.delete(report.getInnovation());
            report.setInnovation(null);
        }

        if (report.getRisk() != null) {
            riskRepository.delete(report.getRisk());
            report.setRisk(null);
        }

        Report savedReport = reportRepository.save(report);

        if (dto.getSummary() != null) {
            Summary summary = new Summary();
            summary.setReport(savedReport);
            summary.setDetail(dto.getSummary());
            summaryRepository.save(summary);
            savedReport.setSummary(summary);
        }

        // Create new related entities (same as create method)
        // Process milestones
        if (dto.getMilestones() != null) {
            List<Milestone> milestones = new ArrayList<>();
            for (MilestoneDto milestoneDto : dto.getMilestones()) {
                Milestone milestone = new Milestone();
                milestone.setReport(savedReport);
                milestone.setSequenceNo(milestoneDto.getSequenceNo());
                milestone.setProjectName(milestoneDto.getProjectName());
                milestone.setDetail(milestoneDto.getDetail());
                milestone.setMilestoneDate(milestoneDto.getMilestoneDate());
                milestone.setRagStatusId(milestoneDto.getRagStatusId());
                milestones.add(milestone);
            }
            milestoneRepository.saveAll(milestones);
            savedReport.setMilestones(milestones);
        }

        // Process workload visibility
        if (dto.getWorkloadVisibility() != null) {
            WorkloadVisibility workloadVisibility = new WorkloadVisibility();
            workloadVisibility.setReport(savedReport);
            workloadVisibility.setValue(dto.getWorkloadVisibility().getValue());
            workloadVisibility.setRagStatusId(dto.getWorkloadVisibility().getRagStatusId());
            workloadVisibilityRepository.save(workloadVisibility);
            savedReport.setWorkloadVisibility(workloadVisibility);
        }

        // Process adequate quality
        if (dto.getAdequateQuality() != null) {
            AdequateQuality adequateQuality = new AdequateQuality();
            adequateQuality.setReport(savedReport);
            adequateQuality.setValue(dto.getAdequateQuality().getValue());
            adequateQuality.setRagStatusId(dto.getAdequateQuality().getRagStatusId());
            adequateQualityRepository.save(adequateQuality);
            savedReport.setAdequateQuality(adequateQuality);
        }

        // Process escalations
        if (dto.getEscalations() != null) {
            Escalation escalation = new Escalation();
            escalation.setReport(savedReport);
            escalation.setHasEscalation(dto.getEscalations().isHasEscalation());
            escalation.setDetails(dto.getEscalations().getDetails());
            escalation.setRagStatusId(dto.getEscalations().getRagStatusId());
            escalationRepository.save(escalation);
            savedReport.setEscalation(escalation);
        }

        // Process trainings
        if (dto.getTrainings() != null) {
            Training training = new Training();
            training.setReport(savedReport);
            training.setTrainingDetails(dto.getTrainings().getTrainingDetails());
            training.setTotalHours((float) dto.getTrainings().getTotalHours());
            training.setRagStatusId(dto.getTrainings().getRagStatusId());
            trainingRepository.save(training);
            savedReport.setTraining(training);
        }

        // Process billability
        if (dto.getBillability() != null) {
            Billability billability = new Billability();
            billability.setReport(savedReport);
            billability.setBilledResources(dto.getBillability().getBilledResources());
            billability.setUnbilledResources(dto.getBillability().getUnbilledResources());
            billability.setLeavesBilled(dto.getBillability().getLeavesBilled());
            billability.setLeavesUnbilled(dto.getBillability().getLeavesUnbilled());
            billability.setHolidays(dto.getBillability().getHolidays());
            billability.setOverallBillabilityPercent(dto.getBillability().getOverallBillabilityPercent());
            billability.setRagStatusId(dto.getBillability().getRagStatusId());
            billabilityRepository.save(billability);
            savedReport.setBillability(billability);
        }

        // Process improvements
        if (dto.getImprovements() != null) {
            List<Improvement> improvements = new ArrayList<>();
            for (ImprovementDto improvementDto : dto.getImprovements()) {
                Improvement improvement = new Improvement();
                improvement.setReport(savedReport);
                improvement.setSequenceNo(improvementDto.getSequenceNo());
                improvement.setArea(improvementDto.getArea());
                improvement.setValueAddition(improvementDto.getValueAddition());
                improvement.setRagStatusId(improvementDto.getRagStatusId());
                improvements.add(improvement);
            }
            improvementRepository.saveAll(improvements);
            savedReport.setImprovements(improvements);
        }

        // Process non-adherence
        if (dto.getNonAdherence() != null) {
            NonAdherence nonAdherence = new NonAdherence();
            nonAdherence.setReport(savedReport);
            nonAdherence.setNonAdherenceValue(dto.getNonAdherence().getNonAdherenceValue());
            nonAdherence.setCount(dto.getNonAdherence().getCount());
            nonAdherence.setImpact(dto.getNonAdherence().getImpact());
            nonAdherence.setTimeToResolve(dto.getNonAdherence().getTimeToResolve());
            nonAdherence.setRagStatusId(dto.getNonAdherence().getRagStatusId());
            nonAdherenceRepository.save(nonAdherence);
            savedReport.setNonAdherence(nonAdherence);
        }

        // Process timesheets
        if (dto.getTimesheets() != null) {
            Timesheet timesheet = new Timesheet();
            timesheet.setReport(savedReport);
            timesheet.setClientDefaulters(dto.getTimesheets().getClientDefaulters());
            timesheet.setErpDefaulters(dto.getTimesheets().getErpDefaulters());
            timesheet.setRagStatusId(dto.getTimesheets().getRagStatusId());
            timesheetRepository.save(timesheet);
            savedReport.setTimesheet(timesheet);
        }

        // Process innovation
        if (dto.getInnovation() != null) {
            Innovation innovation = new Innovation();
            innovation.setReport(savedReport);
            innovation.setDetails(dto.getInnovation().getDetails());
            innovation.setValueAdded(dto.getInnovation().getValueAdded());
            innovation.setRagStatusId(dto.getInnovation().getRagStatusId());
            innovationRepository.save(innovation);
            savedReport.setInnovation(innovation);
        }

        // Process risk
        if (dto.getRisk() != null) {
            Risk risk = new Risk();
            risk.setReport(savedReport);
            risk.setRiskValue(dto.getRisk().getRiskValue());
            risk.setDetails(dto.getRisk().getDetails());
            risk.setRagStatusId(dto.getRisk().getRagStatusId());
            riskRepository.save(risk);
            savedReport.setRisk(risk);
        }

        // Process showcases
        if (dto.getShowcases() != null) {
            List<Showcase> showcases = new ArrayList<>();
            for (ShowcaseDto showcaseDto : dto.getShowcases()) {
                Showcase showcase = new Showcase();
                showcase.setReport(savedReport);
                showcase.setSequenceNo(showcaseDto.getSequenceNo());
                showcase.setDetail(showcaseDto.getDetail());
                showcases.add(showcase);
            }
            showcaseRepository.saveAll(showcases);
            savedReport.setShowcases(showcases);
        }


        if (savedReport != null) {
            ReportHistory history = new ReportHistory();
            history.setReportId(savedReport.getId());
            history.setChangedBy("Default"); 
            history.setNewStatus(ReportStatus.SUBMITTED);
            history.setComment("Report updated");
            reportHistoryRepository.save(history);
        }

        return convertToDto(savedReport);
    }

    @Transactional
    public void deleteReport(Long id) {
        // Cascade delete will handle related entities
        reportRepository.deleteById(id);
    }

    private ReportResponseDto convertToDto(Report report) {
        if (report == null) {
            return null;
        }

        ReportResponseDto dto = new ReportResponseDto();
        //dto.setId(report.getId());
        dto.setTeamName(report.getTeamMapping() != null ? report.getTeamMapping().getName() : null);
        dto.setManagerName(report.getManager() != null ? report.getManager().getName() : null);
        dto.setOpcoName(report.getOpco() != null ? report.getOpco().getName() : null);
        dto.setClientName(report.getClientName());
        dto.setStartDate(report.getStartDate());
        dto.setEndDate(report.getEndDate());
        //dto.setCreatedAt(report.getCreatedAt());

        dto.setSummary(report.getSummary() != null ? report.getSummary().getDetail() : null);
        
        // Milestones
        if (report.getMilestones() != null) {
            List<MilestoneDto> milestoneDtos = new ArrayList<>();
            for (Milestone m : report.getMilestones()) {
                MilestoneDto mDto = new MilestoneDto();
                //mDto.setId(m.getId());
                mDto.setSequenceNo(m.getSequenceNo());
                mDto.setProjectName(m.getProjectName());
                mDto.setDetail(m.getDetail());
                mDto.setMilestoneDate(m.getMilestoneDate());
                mDto.setRagStatusId(m.getRagStatusId());
                milestoneDtos.add(mDto);
            }
            dto.setMilestones(milestoneDtos);
        }

        // Improvements
        if (report.getImprovements() != null) {
            List<ImprovementDto> improvementDtos = new ArrayList<>();
            for (Improvement i : report.getImprovements()) {
                ImprovementDto iDto = new ImprovementDto();
                //  iDto.setId(i.getId());
                iDto.setSequenceNo(i.getSequenceNo());
                iDto.setArea(i.getArea());
                iDto.setValueAddition(i.getValueAddition());
                iDto.setRagStatusId(i.getRagStatusId());
                improvementDtos.add(iDto);
            }
            dto.setImprovements(improvementDtos);
        }

        // Showcases
        if (report.getShowcases() != null) {
            List<ShowcaseDto> showcaseDtos = new ArrayList<>();
            for (Showcase s : report.getShowcases()) {
                ShowcaseDto sDto = new ShowcaseDto();
                // sDto.setId(s.getId());
                sDto.setSequenceNo(s.getSequenceNo());
                sDto.setDetail(s.getDetail());
                showcaseDtos.add(sDto);
            }
            dto.setShowcases(showcaseDtos);
        }

        // One-to-one relationships
        if (report.getWorkloadVisibility() != null) {
            WorkloadVisibilityDto wvDto = new WorkloadVisibilityDto();
            //  wvDto.setId(report.getWorkloadVisibility().getId());
            wvDto.setValue(report.getWorkloadVisibility().getValue());
            wvDto.setRagStatusId(report.getWorkloadVisibility().getRagStatusId());
            dto.setWorkloadVisibility(wvDto);
        }
        if (report.getAdequateQuality() != null) {
            AdequateQualityDto aqDto = new AdequateQualityDto();
            //    aqDto.setId(report.getAdequateQuality().getId());
            aqDto.setValue(report.getAdequateQuality().getValue());
            aqDto.setRagStatusId(report.getAdequateQuality().getRagStatusId());
            dto.setAdequateQuality(aqDto);
        }
        if (report.getEscalation() != null) {
            EscalationsDto eDto = new EscalationsDto();
            //eDto.setId(report.getEscalation().getId());
            eDto.setHasEscalation(report.getEscalation().isHasEscalation());
            eDto.setDetails(report.getEscalation().getDetails());
            eDto.setRagStatusId(report.getEscalation().getRagStatusId());
            dto.setEscalations(eDto);
        }
        if (report.getTraining() != null) {
            TrainingsDto tDto = new TrainingsDto();
            //   tDto.setId(report.getTraining().getId());
            tDto.setTrainingDetails(report.getTraining().getTrainingDetails());
            tDto.setTotalHours(report.getTraining().getTotalHours());
            tDto.setRagStatusId(report.getTraining().getRagStatusId());
            dto.setTrainings(tDto);
        }
        if (report.getBillability() != null) {
            BillabilityDto bDto = new BillabilityDto();
            // bDto.setId(report.getBillability().getId());
            bDto.setBilledResources(report.getBillability().getBilledResources());
            bDto.setUnbilledResources(report.getBillability().getUnbilledResources());
            bDto.setLeavesBilled(report.getBillability().getLeavesBilled());
            bDto.setLeavesUnbilled(report.getBillability().getLeavesUnbilled());
            bDto.setHolidays(report.getBillability().getHolidays());
            bDto.setOverallBillabilityPercent(report.getBillability().getOverallBillabilityPercent());
            bDto.setRagStatusId(report.getBillability().getRagStatusId());
            dto.setBillability(bDto);
        }
        if (report.getNonAdherence() != null) {
            NonAdherenceDto nDto = new NonAdherenceDto();
            // nDto.setId(report.getNonAdherence().getId());
            nDto.setNonAdherenceValue(report.getNonAdherence().getNonAdherenceValue());
            nDto.setCount(report.getNonAdherence().getCount());
            nDto.setImpact(report.getNonAdherence().getImpact());
            nDto.setTimeToResolve(report.getNonAdherence().getTimeToResolve());
            nDto.setRagStatusId(report.getNonAdherence().getRagStatusId());
            dto.setNonAdherence(nDto);
        }
        if (report.getTimesheet() != null) {
            TimesheetsDto tsDto = new TimesheetsDto();
            //tsDto.setId(report.getTimesheet().getId());
            tsDto.setClientDefaulters(report.getTimesheet().getClientDefaulters());
            tsDto.setErpDefaulters(report.getTimesheet().getErpDefaulters());
            tsDto.setRagStatusId(report.getTimesheet().getRagStatusId());
            dto.setTimesheets(tsDto);
        }
        if (report.getInnovation() != null) {
            InnovationDto inDto = new InnovationDto();
            //inDto.setId(report.getInnovation().getId());
            inDto.setDetails(report.getInnovation().getDetails());
            inDto.setValueAdded(report.getInnovation().getValueAdded());
            inDto.setRagStatusId(report.getInnovation().getRagStatusId());
            dto.setInnovation(inDto);
        }
        if (report.getRisk() != null) {
            RiskDto rDto = new RiskDto();
            //  rDto.setId(report.getRisk().getId());
            rDto.setRiskValue(report.getRisk().getRiskValue());
            rDto.setDetails(report.getRisk().getDetails());
            rDto.setRagStatusId(report.getRisk().getRagStatusId());
            dto.setRisk(rDto);
        }
        return dto;
    }

    public List<ReportResponseDto> getReportsByOpco(Long id) {
        List<ReportResponseDto> reportDtos = new ArrayList<>();

        reportRepository.findAll().stream()
            .filter(report -> report.getOpco() != null && report.getOpco().getId().equals(id))
            .forEach(report -> reportDtos.add(convertToDto(report)));

        return reportDtos;
    }

    public List<ReportResponseDto> getReportsByManager(Long id, String reportStatus) {
        List<ReportResponseDto> reportDtos = new ArrayList<>();

        reportRepository.findAll().stream()
            .filter(report -> report.getManager() != null && report.getManager().getId().equals(id))
            .filter(report -> reportStatus == null || reportStatus.isEmpty() || 
                (report.getProcessStatus() != null && report.getProcessStatus().equalsIgnoreCase(reportStatus)))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .forEach(report -> reportDtos.add(convertToDto(report)));

        return reportDtos;
    }

}
