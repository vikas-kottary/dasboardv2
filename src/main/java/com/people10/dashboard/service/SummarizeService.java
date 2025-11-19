package com.people10.dashboard.service;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.ai.chat.prompt.Prompt;

import com.people10.dashboard.dto.SummaryResponseDto;
import com.people10.dashboard.dto.TeamDashboardRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {
    
    private final OpenAiChatModel openAiChatModel;

    public SummaryResponseDto summarizeReport(TeamDashboardRequest reportData) {
        SummaryResponseDto summaryResponse = new SummaryResponseDto();
        if (reportData == null) {
            log.warn("Received null report data for summarization");
            summaryResponse.setDetailedSummary("No report data provided.");
            summaryResponse.setBriefSummary("No report data provided.");
            return summaryResponse;
        }
        
        String promptText = buildBriefPrompt(reportData);
        log.info("Generated Brief prompt: {}", promptText);
        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));
        
        ChatResponse response = openAiChatModel.call(prompt);
        String briefSummaryResponse = response.getResult().getOutput().getText();
        log.info("Generated Brief summary: {}", summaryResponse);
        summaryResponse.setBriefSummary(briefSummaryResponse);

        String detailedPromptText = buildDetailedPrompt(reportData);
        log.info("Generated Detailed prompt: {}", detailedPromptText);
        Prompt detailedPrompt = new Prompt(List.of(new UserMessage(detailedPromptText)));

        ChatResponse detailedResponse = openAiChatModel.call(detailedPrompt);
        String detailedSummaryResponse = detailedResponse.getResult().getOutput().getText();
        log.info("Generated Detailed summary: {}", summaryResponse);
        summaryResponse.setDetailedSummary(detailedSummaryResponse);

        return summaryResponse;
    }

    public static String buildBriefPrompt(TeamDashboardRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert in summarizing team performance reports. Your task is to provide a concise, factual summary for management based on the provided data.\n")
            .append("\nFocus only on high-priority items:\n")
            .append("    Include updates marked with RAG 1 (Exceptional) or RAG 4 (Requires urgent attention).\n")
            .append("    Mention RAG 3 (Average) only if the update is critical or provides essential context.\n")
            .append("    Exclude all RAG 2 (Good) items unless they highlight an important exception or contrast.\n")
            .append("The tone should be formal, objective, and free from unnecessary elaboration or exaggeration. Present only high-impact updates that warrant leadership attention.\n")
            .append("Workload Visibility in weeks represents the team's visibility on the work coming up in near future. A higher value for weeks its better for the team.\n")
            .append("Include showcases if any are present.\n")
            .append("Return the summary as a short paragraph.\n")
            .append("Dont include RAG key words in the summary, just mention the status as Exceptional, Requires urgent attention, Average, Good.\n");
       
        prompt.append(getCommonSection(request));

        prompt.append("\nReturn a concise summary in a short paragraph. Only include the most meaningful highlights. Omit sections with no significant updates.");
        return prompt.toString();
    }

    
    public static String buildDetailedPrompt(TeamDashboardRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert in summarizing team performance reports. Your task is to provide a formal and balanced summary for management that includes important and moderately important updates.\n")
            .append("Prioritize RAG 1 (Exceptional) and RAG 4 (Requires urgent attention) items.\n")
            .append("Include RAG 3 (Average).\n")
            .append("Include RAG 2 (Good) only if the update provides measurable value or shows process improvement.\n")
            .append("The tone must remain professional and fact-based—avoid marketing language, praise, or generalizations. Focus on clear, meaningful insights that are present in the report.\n")
            .append("Workload Visibility in weeks represents the team's visibility on the work coming up in near future. A higher value for weeks its better for the team.\n")
            .append("Include showcases if any are present.\n")
            .append("Include Comments if any are present and are relevant to the project, if it is irrelevant then dont add it.\n")
            .append("Dont include RAG key words in the summary, if required just mention the status as Exceptional, Requires urgent attention, Average, Good.\n")
            .append("Structure the output as a brief paragraph summarizing key performance points.\n\n");
     
        prompt.append(getCommonSection(request));

        prompt.append("\nReturn a summary in a paragraph. Only include the most meaningful highlights. Dont unnecessary elaborate or exaggerate");
        return prompt.toString();
    }

    private static String getCommonSection(TeamDashboardRequest request){
        StringBuilder prompt = new StringBuilder();
         // Milestones
        prompt.append("### Milestones:\n");
        request.getMilestones().stream()
            .forEach(m -> prompt.append("- [RAG ").append(m.getRagStatusId()).append("] ")
                                .append(m.getProjectName()).append(": ")
                                .append(m.getDetail()).append("\n"));

        // Workload Visibility
        prompt.append("\n### Workload Visibility in weeks:\n")
                .append("- [RAG ").append(request.getWorkloadVisibility().getRagStatusId()).append("] ")
                .append(request.getWorkloadVisibility().getValue()).append("\n");                               

        // Quality
        prompt.append("\n### Quality:\n")
                .append("- [RAG ").append(request.getAdequateQuality().getRagStatusId()).append("] ")
                .append(request.getAdequateQuality().getValue()).append("\n");

        // Escalations
        prompt.append("\n### Escalations:\n")
                .append("- [RAG ").append(request.getEscalations().getRagStatusId()).append("] ")
                .append(request.getEscalations().getDetails()).append("\n");

        // Trainings
        prompt.append("\n### Trainings:\n")
                .append("- [RAG ").append(request.getTrainings().getRagStatusId()).append("] ")
                .append(request.getTrainings().getTrainingDetails()).append("\n");

        // Billability
        double overallBillability = request.getBillability().getOverallBillabilityPercent();
        if (overallBillability < 80.0 || overallBillability > 98.0) {
            prompt.append("Overall Billability is the percentage of time spent on billable tasks compared to total working hours. Higher the billability the better.\n")
                  .append("Include Billability details if there are exceptional scenarios where billability is > 99% or if there are any alarming scenarios where billability is <80%.\n");

            prompt.append("\n### Billability:\n")
                .append("- [RAG ").append(request.getBillability().getRagStatusId()).append("] ")
                .append("Billed Resources: ").append(request.getBillability().getBilledResources()).append("\n")
                .append("Unbilled Resources: ").append(request.getBillability().getUnbilledResources()).append("\n")
                .append("Leaves (Billed): ").append(request.getBillability().getLeavesBilled()).append("\n")
                .append("Leaves (Unbilled): ").append(request.getBillability().getLeavesUnbilled()).append("\n")
                .append("Holidays: ").append(request.getBillability().getHolidays()).append("\n")
                .append("Overall Billability (%): ").append(request.getBillability().getOverallBillabilityPercent()).append("\n");
        }
        // Improvements
        var improvementList = request.getImprovements().stream()
            .toList();
        if (!improvementList.isEmpty()) {
            prompt.append("\n### Improvements:\n");
            improvementList.forEach(i ->
                prompt.append("- [RAG ").append(i.getRagStatusId()).append("] ")
                      .append(i.getArea()).append(": ").append(i.getValueAddition()).append("\n")
            );
        }

        // Non-Adherence
        prompt.append("\n### Non-Adherence:\n")
                .append("- [RAG ").append(request.getNonAdherence().getRagStatusId()).append("] ")
                .append("Count: ").append(request.getNonAdherence().getCount())
                .append("Impact of Non adherence: ").append(request.getNonAdherence().getImpact())
                .append("Time To Resolve: ").append(request.getNonAdherence().getTimeToResolve()).append("\n");

        // Timesheets
        prompt.append("\n### Timesheet:\n")
                .append("- [RAG ").append(request.getTimesheets().getRagStatusId()).append("] ")
                .append("Client Defaulters: ").append(request.getTimesheets().getClientDefaulters())
                .append(", ERP Defaulters: ").append(request.getTimesheets().getErpDefaulters()).append("\n");

        // Innovation
        prompt.append("\n### Innovation:\n")
                .append("- [RAG ").append(request.getInnovation().getRagStatusId()).append("] ")
                .append(request.getInnovation().getDetails()).append(" — ")
                .append(request.getInnovation().getValueAdded()).append("\n");

        // Risks
        prompt.append("\n### Risk:\n")
                .append("- [RAG ").append(request.getRisk().getRagStatusId()).append("] ")
                .append(request.getRisk().getRiskValue()).append(": ")
                .append(request.getRisk().getDetails()).append("\n");

        // Showcases
        if (!request.getShowcases().isEmpty()) {
            prompt.append("\n### Showcase:\n");
            request.getShowcases().forEach(s ->
                prompt.append("- ").append(s.getDetail()).append("\n")
            );
        }

        if(!CollectionUtils.isEmpty(request.getComments())) {
            prompt.append("\n### Comments:\n");
            request.getComments().forEach(c ->
                prompt.append(c.getComment()).append("\n")
            );
        }

        return prompt.toString();
    }
}
