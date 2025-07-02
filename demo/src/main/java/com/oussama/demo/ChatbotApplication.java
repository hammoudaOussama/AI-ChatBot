package com.oussama.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatbotApplication.class, args);
		System.out.println("🚀 Groq DeepSeek R1 Chatbot started successfully!");
		System.out.println("🌐 Access your chatbot at: http://localhost:8081");
		System.out.println("🤖 Model: deepseek-r1-distill-llama-70b");
	}
}