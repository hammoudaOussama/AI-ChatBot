package com.oussama.demo.service;

import com.oussama.demo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroqChatService {
    private static final Logger logger = LoggerFactory.getLogger(GroqChatService.class);

    private final WebClient webClient;
    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    @Value("${groq.temperature}")
    private Double temperature;

    @Value("${groq.max-completion-tokens}")
    private Integer maxCompletionTokens;

    @Value("${groq.top-p}")
    private Double topP;

    @Value("${groq.stream}")
    private Boolean stream;

    // System prompt optimized for DeepSeek R1
    private static final String SYSTEM_PROMPT = """
        You are a helpful, intelligent, and friendly AI assistant powered by DeepSeek R1.
        You provide accurate, thoughtful, and detailed responses while being conversational and engaging.
        You excel at reasoning, analysis, problem-solving, coding, and creative tasks.
        Always aim to be helpful, clear, and provide valuable insights in your responses.
        """;

    public GroqChatService(WebClient webClient) {
        this.webClient = webClient;
        logger.info("GroqChatService initialized with model: {}", model);
    }

    public ChatResponse chat(ChatRequest request) {
        logger.debug("Processing chat request: {}", request);

        try {
            String conversationId = request.getConversationId();
            if (conversationId == null || conversationId.trim().isEmpty()) {
                conversationId = UUID.randomUUID().toString();
                logger.debug("Generated new conversation ID: {}", conversationId);
            }

            // Get or create conversation history
            List<Message> messageHistory = conversations.computeIfAbsent(
                    conversationId,
                    k -> {
                        List<Message> newHistory = new ArrayList<>();
                        // Add system message for new conversations
                        newHistory.add(new Message("system", SYSTEM_PROMPT));
                        logger.debug("Created new conversation with system prompt");
                        return newHistory;
                    }
            );

            // Add user message to history
            Message userMessage = new Message("user", request.getMessage());
            messageHistory.add(userMessage);
            logger.debug("Added user message to conversation history");

            // Create Groq request
            GroqRequest groqRequest = new GroqRequest(
                    new ArrayList<>(messageHistory), // Create a copy
                    model,
                    temperature,
                    maxCompletionTokens,
                    topP,
                    stream,
                    null
            );

            logger.debug("Sending request to Groq API: {}", groqRequest);

            // Call Groq API
            GroqResponse groqResponse = callGroqApi(groqRequest);

            if (groqResponse == null || groqResponse.getChoices() == null || groqResponse.getChoices().isEmpty()) {
                throw new RuntimeException("Empty response from Groq API");
            }

            String aiResponse = groqResponse.getChoices().get(0).getMessage().getContent();
            logger.debug("Received response from Groq API: {} characters", aiResponse.length());

            // Add AI response to history
            messageHistory.add(new Message("assistant", aiResponse));

            // Limit conversation history to prevent memory issues
            if (messageHistory.size() > 31) { // Keep system message + 30 messages
                List<Message> newHistory = new ArrayList<>();
                newHistory.add(messageHistory.get(0)); // Keep system message
                newHistory.addAll(messageHistory.subList(messageHistory.size() - 25, messageHistory.size()));
                conversations.put(conversationId, newHistory);
                logger.debug("Trimmed conversation history to prevent memory issues");
            }

            // Create response with token usage
            ChatResponse response = new ChatResponse(aiResponse, conversationId);
            if (groqResponse.getUsage() != null) {
                response.setTokenUsage(new ChatResponse.TokenUsage(
                        groqResponse.getUsage().getPromptTokens(),
                        groqResponse.getUsage().getCompletionTokens(),
                        groqResponse.getUsage().getTotalTokens()
                ));
            }

            logger.info("Successfully processed chat request for conversation: {}", conversationId);
            return response;

        } catch (Exception e) {
            logger.error("Error processing chat request: {}", e.getMessage(), e);
            return ChatResponse.error("Sorry, I encountered an error while processing your request. Please try again.");
        }
    }

    private GroqResponse callGroqApi(GroqRequest request) {
        try {
            logger.debug("Calling Groq API at: {}", apiUrl);

            Mono<GroqResponse> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "SpringBoot-GroqClient/1.0")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .doOnNext(body -> logger.error("Groq API error response: {}", body))
                                    .then(Mono.error(new RuntimeException("Groq API error: " + response.statusCode())))
                    )
                    .bodyToMono(GroqResponse.class)
                    .timeout(Duration.ofSeconds(30));

            GroqResponse response = responseMono.block();
            logger.debug("Successfully received response from Groq API");
            return response;

        } catch (WebClientResponseException e) {
            logger.error("WebClient error calling Groq API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Groq API call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Groq API", e);
            throw new RuntimeException("Failed to communicate with Groq API: " + e.getMessage(), e);
        }
    }

    public void clearConversation(String conversationId) {
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            conversations.remove(conversationId);
            logger.info("Cleared conversation: {}", conversationId);
        }
    }

    public void clearAllConversations() {
        int count = conversations.size();
        conversations.clear();
        logger.info("Cleared all {} conversations", count);
    }

    public int getActiveConversationsCount() {
        return conversations.size();
    }

    public boolean hasConversation(String conversationId) {
        return conversationId != null && conversations.containsKey(conversationId);
    }

    // Test method to verify API connectivity
    public boolean testApiConnection() {
        try {
            List<Message> testMessages = List.of(new Message("user", "Hello"));
            GroqRequest testRequest = new GroqRequest(
                    testMessages, model, 0.1, 10, 1.0, false, null
            );

            GroqResponse response = callGroqApi(testRequest);
            logger.info("API connection test successful");
            return response != null;

        } catch (Exception e) {
            logger.error("API connection test failed", e);
            return false;
        }
    }
}