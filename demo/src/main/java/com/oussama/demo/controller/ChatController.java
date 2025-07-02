package com.oussama.demo.controller;

import com.oussama.demo.model.ChatRequest;
import com.oussama.demo.model.ChatResponse;
import com.oussama.demo.service.GroqChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final GroqChatService groqChatService;

    public ChatController(GroqChatService groqChatService) {
        this.groqChatService = groqChatService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("modelName", "DeepSeek R1 Distill Llama 70B");
        model.addAttribute("activeConversations", groqChatService.getActiveConversationsCount());
        logger.debug("Serving chat interface");
        return "chat";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        logger.debug("Received chat API request from conversation: {}", request.getConversationId());

        try {
            ChatResponse response = groqChatService.chat(request);
            logger.debug("Returning chat response: success={}", response.isSuccess());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in chat API endpoint", e);
            return ResponseEntity.ok(ChatResponse.error("An unexpected error occurred. Please try again."));
        }
    }

    @DeleteMapping("/api/chat/{conversationId}")
    @ResponseBody
    public ResponseEntity<Void> clearConversation(@PathVariable String conversationId) {
        logger.debug("Clearing conversation: {}", conversationId);
        groqChatService.clearConversation(conversationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Void> clearAllConversations() {
        logger.debug("Clearing all conversations");
        groqChatService.clearAllConversations();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean apiConnected = groqChatService.testApiConnection();

        return ResponseEntity.ok(Map.of(
                "status", apiConnected ? "connected" : "disconnected",
                "model", "deepseek-r1-distill-llama-70b",
                "activeConversations", groqChatService.getActiveConversationsCount(),
                "timestamp", LocalDateTime.now(),
                "apiConnected", apiConnected
        ));
    }

    @GetMapping("/api/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testApi() {
        logger.debug("Testing Groq API connection");
        boolean connected = groqChatService.testApiConnection();

        return ResponseEntity.ok(Map.of(
                "connected", connected,
                "timestamp", LocalDateTime.now(),
                "message", connected ? "API connection successful" : "API connection failed"
        ));
    }
}