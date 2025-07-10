package com.people10.dashboard.service;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.Prompt;


import com.people10.dashboard.dto.TeamDashboardRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {
    
    private final OpenAiChatModel openAiChatModel;

    public String summarizeReport(TeamDashboardRequest reportData) {
        if (reportData == null) {
            log.warn("Received null report data for summarization");
            return "No report data provided.";
        }
        String promptText = buildPrompt(reportData);
        log.info("Generated prompt: {}", promptText);
        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));
        ChatResponse response = openAiChatModel.call(prompt);
        String summaryResponse = response.getResult().getOutput().getText();
        log.info("Generated summary: {}", summaryResponse);
        return summaryResponse;
    }

    public static String buildPrompt(TeamDashboardRequest request) {
        StringBuilder prompt = new StringBuilder();

 /*
        // Priority 1 prompt
        prompt.append("You are an expert in summarizing team performance reports. Your task is to provide a concise, factual summary for management based on the provided data.\n")
            .append("\nFocus only on high-priority items:\n")
            .append("    Include updates marked with RAG 1 (Exceptional) or RAG 4 (Requires urgent attention).\n")
            .append("    Mention RAG 3 (Average) only if the update is critical or provides essential context.\n")
            .append("    Exclude all RAG 2 (Good) items unless they highlight an important exception or contrast.\n")
            .append("\nThe tone should be formal, objective, and free from unnecessary elaboration or exaggeration. Present only high-impact updates that warrant leadership attention.\n")
            .append("Workload Visibility in weeks represents the team's visibility on the work coming up in near future. A higher value for weeks its better for the team.\n")
            .append("Including Billability details only if the overall billability percentage is less then 90% or greater then 98%.\n")
            .append("\nReturn the summary as a short paragraph.\n")
            .append("Dont include RAG key words in the summary, just mention the status as Exceptional, Requires urgent attention, Average, Good.\n\n");
       
        */
        // Priority 2 prompt
        prompt.append("You are an expert in summarizing team performance reports. Your task is to provide a formal and balanced summary for management that includes important and moderately important updates.\n")
            .append("Prioritize RAG 1 (Exceptional) and RAG 4 (Requires urgent attention) items.\n")
            .append("Include RAG 3 (Average).\n")
            .append("Include RAG 2 (Good) only if the update provides measurable value or shows process improvement.\n")
            .append("The tone must remain professional and fact-based—avoid marketing language, praise, or generalizations. Focus on clear, meaningful insights that are present in the report.\n")
            .append("Workload Visibility in weeks represents the team's visibility on the work coming up in near future. A higher value for weeks its better for the team.\n")
            .append("Including Billability details only if the overall billability percentage is less then 90% or greater then 98%.\n")
            .append("Dont include RAG key words in the summary, if required just mention the status as Exceptional, Requires urgent attention, Average, Good.\n")
            .append("Structure the output as a brief paragraph summarizing key performance points.\n\n");
      /*
        // // Priority 3 prompt
        prompt.append("You are an expert in summarizing team performance reports. Your task is to provide a structured and comprehensive summary for management, covering insights from all RAG levels, while maintaining a formal, objective tone.\n")
            .append("Start with RAG 1 (Exceptional) and RAG 4 (Requires urgent attention).\n")
            .append("Include RAG 3 (Average) and RAG 2 (Good) items as needed to provide a complete and accurate overview of team performance.\n")
            .append("Do not exaggerate or add subjective opinions. Avoid filler language or motivational phrases.\n")
            .append("Focus on measurable outcomes, observed risks, training updates, improvements, and compliance. Return the summary as a clear paragraph suitable for senior leadership review.\n\n")
            .append("Workload Visibility in weeks represents the team's visibility on the work coming up in near future. A higher value for weeks its better for the team.\n")
            .append("Including Billability details only if the overall billability percentage is less then 90% or greater then 98%.\n")
            .append("Dont include RAG key words in the summary, just mention the status as Exceptional, Requires urgent attention, Average, Good.\n");
*/ 
       
            //   .append("Quality reflects the standard of work delivered by the team, with higher RAG statuses indicating better quality outcomes.\n")
            //   .append("Escalations indicate issues that require immediate attention, with higher RAG statuses signaling more critical situations.\n")
            //   .append("Trainings reflect the team's commitment to skill development, with higher RAG statuses indicating more effective training programs.\n")
            //   .append("Billability shows how effectively team resources are utilized, with higher RAG statuses indicating better utilization rates.\n")
            //   .append("Improvements highlight areas where the team has made significant progress, with higher RAG statuses indicating more impactful improvements.\n")
            //   .append("Non-Adherence indicates compliance issues, with higher RAG statuses signaling more significant non-compliance risks.\n")
            //   .append("Timesheets reflect adherence to time tracking policies, with higher RAG statuses indicating better compliance.\n")
            //   .append("Innovation showcases the team's creativity and problem-solving capabilities, with higher RAG statuses indicating more innovative contributions.\n")
            //   .append("Risks represent potential threats to project success, with higher RAG statuses indicating more significant risks.\n");
            
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
        prompt.append("\n### Billability:\n")
                .append("- [RAG ").append(request.getBillability().getRagStatusId()).append("] ")
                .append("Billed Resources: ").append(request.getBillability().getBilledResources()).append("\n")
                .append("Unbilled Resources: ").append(request.getBillability().getUnbilledResources()).append("\n")
                .append("Leaves (Billed): ").append(request.getBillability().getLeavesBilled()).append("\n")
                .append("Leaves (Unbilled): ").append(request.getBillability().getLeavesUnbilled()).append("\n")
                .append("Holidays: ").append(request.getBillability().getHolidays()).append("\n")
                .append("Overall Billability (%): ").append(request.getBillability().getOverallBillabilityPercent()).append("\n");

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

        prompt.append("\nReturn a concise summary in a short paragraph. Only include the most meaningful highlights. Omit sections with no significant updates.");
        return prompt.toString();
    }

}
