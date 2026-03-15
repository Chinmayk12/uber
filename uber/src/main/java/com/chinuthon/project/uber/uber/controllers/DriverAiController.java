package com.chinuthon.project.uber.uber.controllers;

import com.chinuthon.project.uber.uber.ai.service.DriverAiChatService;
import com.chinuthon.project.uber.uber.dto.AiChatRequestDto;
import com.chinuthon.project.uber.uber.dto.AiChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drivers/ai")
@RequiredArgsConstructor
@Secured("ROLE_DRIVER")
public class DriverAiController {

    private final DriverAiChatService driverAiChatService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponseDto> chat(@RequestBody AiChatRequestDto request) {
        // If no conversationId provided, generate one for new conversations
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }

        String response = driverAiChatService.chat(request.getMessage(), conversationId);

        return ResponseEntity.ok(new AiChatResponseDto(response, conversationId));
    }
}
