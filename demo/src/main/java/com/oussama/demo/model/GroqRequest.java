package com.oussama.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GroqRequest {
    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("model")
    private String model;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("max_completion_tokens")
    private Integer maxCompletionTokens;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("stream")
    private Boolean stream;

    @JsonProperty("stop")
    private String stop;

    public GroqRequest() {}

    public GroqRequest(List<Message> messages, String model, Double temperature,
                       Integer maxCompletionTokens, Double topP, Boolean stream, String stop) {
        this.messages = messages;
        this.model = model;
        this.temperature = temperature;
        this.maxCompletionTokens = maxCompletionTokens;
        this.topP = topP;
        this.stream = stream;
        this.stop = stop;
    }

    // Getters and setters
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxCompletionTokens() { return maxCompletionTokens; }
    public void setMaxCompletionTokens(Integer maxCompletionTokens) { this.maxCompletionTokens = maxCompletionTokens; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    public String getStop() { return stop; }
    public void setStop(String stop) { this.stop = stop; }

    @Override
    public String toString() {
        return "GroqRequest{" +
                "messages=" + messages +
                ", model='" + model + '\'' +
                ", temperature=" + temperature +
                ", maxCompletionTokens=" + maxCompletionTokens +
                ", topP=" + topP +
                ", stream=" + stream +
                ", stop='" + stop + '\'' +
                '}';
    }
}