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
        prompt.append("You are an expert in summarizing team performance reports. ")
              .append("Your task is to provide a concise summary of the biweekly report based on the provided data.\n")
              .append("Focus on the most important insights based on the RAG status values:\n")
              .append("- RAG 1 = Exceptional\n")
              .append("- RAG 4 = Requires urgent attention\n")
              .append("- RAG 3 = Average\n")
              .append("- RAG 2 = Good\n\n")
              .append("Focus first on items with RAG 1 or 4, then include relevant items from RAG 3 if noteworthy, and only include RAG 2 if it adds meaningful insight. Avoid repeating everything. The goal is to deliver a short, impactful summary of the team's performance.\n");

        // Milestones
        prompt.append("### Milestones:\n");
        request.getMilestones().stream()
            .forEach(m -> prompt.append("- [RAG ").append(m.getRagStatusId()).append("] ")
                                .append(m.getProjectName()).append(": ")
                                .append(m.getDetail()).append("\n"));

        // Quality
        prompt.append("\n### Quality:\n")
                .append("- [RAG ").append(request.getAdequateQuality().getRagStatusId()).append("] ")
                .append(request.getAdequateQuality().getValue()).append("\n");

        // Escalations
        prompt.append("\n### Escalations:\n")
                .append("- [RAG ").append(request.getEscalations().getRagStatusId()).append("] ")
                .append(request.getEscalations().getDetails()).append("\n");

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

        // Trainings
        prompt.append("\n### Trainings:\n")
                .append("- [RAG ").append(request.getTrainings().getRagStatusId()).append("] ")
                .append(request.getTrainings().getTrainingDetails()).append("\n");

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

        // Timesheets
        prompt.append("\n### Timesheet:\n")
                .append("- [RAG ").append(request.getTimesheets().getRagStatusId()).append("] ")
                .append("Client Defaulters: ").append(request.getTimesheets().getClientDefaulters())
                .append(", ERP Defaulters: ").append(request.getTimesheets().getErpDefaulters()).append("\n");

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
