package com.oussama.demo.model;

import java.time.LocalDateTime;

public class ChatResponse {
    private String response;
    private String conversationId;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;
    private String model;
    private TokenUsage tokenUsage;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.success = true;
        this.model = "deepseek-r1-distill-llama-70b";
    }

    public ChatResponse(String response, String conversationId) {
        this();
        this.response = response;
        this.conversationId = conversationId;
    }

    public static ChatResponse error(String error) {
        ChatResponse response = new ChatResponse();
        response.success = false;
        response.error = error;
        return response;
    }

    // Getters and setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public TokenUsage getTokenUsage() { return tokenUsage; }
    public void setTokenUsage(TokenUsage tokenUsage) { this.tokenUsage = tokenUsage; }

    public static class TokenUsage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;

        public TokenUsage(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = totalTokens;
        }

        // Getters and setters
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }

        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "response='" + response + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", success=" + success +
                ", model='" + model + '\'' +
                '}';
    }
}