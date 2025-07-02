# 🧠 DeepSeek R1 AI Chatbot - Direct Groq API Integration

A modern, responsive AI chatbot powered by **DeepSeek R1** with direct **Groq API** integration, built with **Spring Boot** and **Tailwind CSS**.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen?style=flat-square&logo=spring)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.4+-blue?style=flat-square&logo=tailwindcss)
![Groq API](https://img.shields.io/badge/Groq%20API-DeepSeek%20R1-purple?style=flat-square)

## ✨ Features

### 🚀 **AI Capabilities**
- **DeepSeek R1 Model** - Advanced reasoning and problem-solving
- **Complex Analysis** - Detailed data interpretation and research
- **Code Generation** - High-quality programming assistance
- **Creative Writing** - Professional content creation
- **Step-by-step Reasoning** - Logical thinking and planning

### 🎨 **Modern UI/UX**
- **Glassmorphism Design** - Modern blur effects and transparency
- **Responsive Layout** - Perfect on mobile, tablet, and desktop
- **Real-time Typing** - Animated message delivery
- **Smooth Animations** - Professional transitions and effects
- **Dark/Light Themes** - Adaptive color schemes

### 🔧 **Technical Features**
- **Direct API Integration** - No Spring AI dependencies
- **Token Usage Tracking** - Monitor costs and consumption
- **Connection Monitoring** - Real-time API status
- **Conversation Memory** - Persistent chat history
- **Error Recovery** - Comprehensive error handling
- **Mobile Optimized** - Touch-friendly interactions

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   Groq API      │
│   (Tailwind)    │◄──►│   Backend       │◄──►│   (DeepSeek R1) │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Tech Stack**
- **Backend**: Spring Boot 3.2.1, Java 17+
- **Frontend**: Tailwind CSS 3.4+, Vanilla JavaScript
- **API**: Direct Groq API integration with WebClient
- **Model**: `deepseek-r1-distill-llama-70b`

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Maven 3.6+**
- **Groq API Key** ([Get yours here](https://console.groq.com/keys))

### Installation

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd chatbotAI
   ```

2. **Set your Groq API Key**
   
   **Option A: Environment Variable (Recommended)**
   ```bash
   export GROQ_API_KEY=gsk_your-actual-groq-api-key-here
   ```
   
   **Option B: Application Properties**
   ```properties
   # src/main/resources/application.properties
   groq.api.key=gsk_your-actual-groq-api-key-here
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the Application**
   ```
   http://localhost:8081
   ```

## ⚙️ Configuration

### Application Properties
```properties
# Application Settings
spring.application.name=chatbotAI
server.port=8081

# Groq API Configuration
groq.api.key=${GROQ_API_KEY:your-groq-api-key-here}
groq.api.url=https://api.groq.com/openai/v1/chat/completions
groq.model=deepseek-r1-distill-llama-70b
groq.temperature=0.6
groq.max-completion-tokens=4096
groq.top-p=0.95
groq.stream=false

# Logging
logging.level.com.oussama.demo=DEBUG
```

### Available Models
| Model | Description | Use Case |
|-------|-------------|----------|
| `deepseek-r1-distill-llama-70b` | **Default** - Best reasoning | Complex problem-solving |
| `deepseek-r1-distill-qwen-32b` | Faster alternative | Balanced performance |
| `deepseek-r1-distill-qwen-14b` | Lightweight option | Quick responses |

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/oussama/demo/
│   │   ├── ChatbotApplication.java           # Main application
│   │   ├── controller/
│   │   │   └── ChatController.java           # REST endpoints
│   │   ├── service/
│   │   │   └── GroqChatService.java         # API integration
│   │   ├── model/
│   │   │   ├── ChatRequest.java             # Request DTOs
│   │   │   ├── ChatResponse.java            # Response DTOs
│   │   │   ├── GroqRequest.java             # Groq API format
│   │   │   ├── GroqResponse.java            # Groq response
│   │   │   └── Message.java                 # Message model
│   │   └── config/
│   │       └── WebClientConfig.java         # HTTP client config
│   └── resources/
│       ├── application.properties           # Configuration
│       ├── templates/
│       │   └── chat.html                    # Main UI template
│       └── static/
│           └── js/
│               └── chat.js                  # Frontend logic
```

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Chat interface |
| `POST` | `/api/chat` | Send message to AI |
| `DELETE` | `/api/chat/{id}` | Clear conversation |
| `DELETE` | `/api/chat` | Clear all conversations |
| `GET` | `/api/status` | API connection status |
| `GET` | `/api/test` | Test API connectivity |

### Example API Usage

**Send Message:**
```bash
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explain quantum computing",
    "conversationId": "optional-conversation-id"
  }'
```

**Response:**
```json
{
  "response": "Quantum computing is...",
  "conversationId": "uuid-generated-id",
  "timestamp": "2025-01-02T10:30:00",
  "success": true,
  "model": "deepseek-r1-distill-llama-70b",
  "tokenUsage": {
    "promptTokens": 15,
    "completionTokens": 150,
    "totalTokens": 165
  }
}
```

## 🎨 UI Components

### Modern Design Features
- **Glassmorphism Effects** - Backdrop blur and transparency
- **Gradient Backgrounds** - Beautiful color transitions
- **Smooth Animations** - Professional micro-interactions
- **Responsive Design** - Mobile-first approach
- **Custom Modals** - Native-like confirmation dialogs
- **Toast Notifications** - User-friendly feedback system

### Key UI Elements
- **Status Indicators** - Real-time API connection status
- **Token Display** - Usage tracking and cost monitoring
- **Typing Animation** - Realistic message delivery
- **Message Bubbles** - Distinct user/AI styling
- **Loading States** - Visual feedback for all actions

## 📱 Mobile Experience

The application is fully optimized for mobile devices:
- **Touch-friendly** buttons and interactions
- **Responsive layouts** that adapt to screen size
- **Swipe gestures** for enhanced navigation
- **Optimized typography** for readability
- **Fast loading** with efficient animations

## 🔧 Development

### Running in Development Mode
```bash
# With auto-reload
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"

# With debug logging
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.oussama.demo=DEBUG"
```

### Building for Production
```bash
# Build JAR
mvn clean package

# Run production build
java -jar target/demo-0.0.1-SNAPSHOT.jar

# With production profile
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🐳 Docker Support

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8081
```

### Docker Commands
```bash
# Build image
docker build -t deepseek-chatbot .

# Run container
docker run -p 8081:8081 -e GROQ_API_KEY=your-key deepseek-chatbot
```

## 🔒 Security Considerations

### API Key Security
- ✅ Use environment variables for API keys
- ✅ Never commit API keys to version control
- ✅ Use different keys for development/production
- ✅ Monitor API usage and set limits

### Input Validation
- ✅ Request size limits (4000 characters)
- ✅ Input sanitization and validation
- ✅ Rate limiting protection
- ✅ Error message sanitization

## 📊 Monitoring & Logging

### Health Checks
- **API Status**: Real-time Groq API connectivity
- **Token Usage**: Cost tracking and monitoring
- **Error Rates**: Application error monitoring
- **Response Times**: Performance metrics

### Logging Levels
```properties
# Debug mode
logging.level.com.oussama.demo=DEBUG

# Production mode
logging.level.com.oussama.demo=INFO
logging.level.org.springframework.web.reactive.function.client=WARN
```

## 🧪 Testing

### Manual Testing
1. **API Connection**: Use the "Test API" button
2. **Chat Functionality**: Send various message types
3. **Error Handling**: Test with invalid inputs
4. **Mobile Testing**: Verify responsive behavior

### API Testing with curl
```bash
# Test status endpoint
curl http://localhost:8081/api/status

# Test chat endpoint
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, DeepSeek R1!"}'
```

## 🚨 Troubleshooting

### Common Issues

**1. API Key Invalid**
```
Error: 401 Unauthorized
Solution: Check your Groq API key in application.properties
```

**2. Connection Failed**
```
Error: Connection timeout
Solution: Verify internet connection and Groq API status
```

**3. Port Already in Use**
```
Error: Port 8081 was already in use
Solution: Change server.port in application.properties or kill the process
```

**4. Token Limit Exceeded**
```
Error: Rate limit exceeded
Solution: Wait and retry, or upgrade your Groq plan
```

### Debug Mode
Enable comprehensive logging:
```properties
logging.level.com.oussama.demo=DEBUG
logging.level.org.springframework.web.reactive.function.client=DEBUG
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit changes**: `git commit -m 'Add amazing feature'`
4. **Push to branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding standards
- Add unit tests for new features
- Update documentation for API changes
- Test on multiple browsers and devices

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Groq** - For providing fast AI inference API
- **DeepSeek** - For the advanced R1 reasoning model
- **Spring Boot** - For the robust backend framework
- **Tailwind CSS** - For the modern UI framework

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Documentation**: This README and inline code comments
- **API Reference**: [Groq API Docs](https://console.groq.com/docs)

---

**Built with ❤️ using Spring Boot, Tailwind CSS, and DeepSeek R1**

*Perfect for production use with professional-grade AI capabilities* 🚀🧠
