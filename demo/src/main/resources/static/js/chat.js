class GroqDirectChatBot {
    constructor() {
        this.conversationId = null;
        this.isTyping = false;
        this.initializeElements();
        this.attachEventListeners();
        this.updateCharCount();
        this.autoResizeTextarea();
        this.showWelcomeMessage();
        this.checkApiStatus();
    }

    initializeElements() {
        this.messageInput = document.getElementById('messageInput');
        this.sendBtn = document.getElementById('sendBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.testBtn = document.getElementById('testBtn');
        this.chatMessages = document.getElementById('chatMessages');
        this.charCount = document.getElementById('charCount');
        this.statusIndicator = document.getElementById('statusIndicator');
        this.tokenUsage = document.getElementById('tokenUsage');
        this.tokenInfo = document.getElementById('tokenInfo');
    }

    attachEventListeners() {
        this.sendBtn.addEventListener('click', () => this.sendMessage());
        this.clearBtn.addEventListener('click', () => this.clearChat());
        this.testBtn.addEventListener('click', () => this.testApiConnection());

        this.messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey && !e.ctrlKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        this.messageInput.addEventListener('input', () => {
            this.updateCharCount();
            this.autoResizeTextarea();
        });

        this.messageInput.addEventListener('paste', () => {
            setTimeout(() => {
                this.updateCharCount();
                this.autoResizeTextarea();
            }, 10);
        });

        // Auto-check API status every 5 minutes
        setInterval(() => this.checkApiStatus(), 5 * 60 * 1000);
    }

    autoResizeTextarea() {
        this.messageInput.style.height = 'auto';
        this.messageInput.style.height = Math.min(this.messageInput.scrollHeight, 120) + 'px';
    }

    updateCharCount() {
        const length = this.messageInput.value.length;
        this.charCount.textContent = `${length}/4000`;

        if (length > 3500) {
            this.charCount.style.color = '#dc2626';
        } else if (length > 3000) {
            this.charCount.style.color = '#f59e0b';
        } else {
            this.charCount.style.color = '#6b7280';
        }
    }

    showWelcomeMessage() {
        const initialTime = document.querySelector('.message-time');
        if (initialTime) {
            initialTime.textContent = new Date().toLocaleTimeString();
        }
    }

    async checkApiStatus() {
        try {
            const response = await fetch('/api/status');
            const data = await response.json();

            this.updateStatusIndicator(data.apiConnected);

            console.log('API Status:', data);
        } catch (error) {
            console.error('Failed to check API status:', error);
            this.updateStatusIndicator(false);
        }
    }

    async testApiConnection() {
        this.testBtn.disabled = true;
        this.testBtn.textContent = 'Testing...';

        try {
            const response = await fetch('/api/test');
            const data = await response.json();

            if (data.connected) {
                this.showToast('✅ API connection successful!', 'success');
                this.updateStatusIndicator(true);
            } else {
                this.showToast('❌ API connection failed!', 'error');
                this.updateStatusIndicator(false);
            }

        } catch (error) {
            console.error('API test failed:', error);
            this.showToast('❌ API test failed!', 'error');
            this.updateStatusIndicator(false);
        } finally {
            this.testBtn.disabled = false;
            this.testBtn.textContent = 'Test API';
        }
    }

    updateStatusIndicator(connected) {
        if (connected) {
            this.statusIndicator.className = 'status-indicator connected';
            this.statusIndicator.title = 'API Connected';
        } else {
            this.statusIndicator.className = 'status-indicator disconnected';
            this.statusIndicator.title = 'API Disconnected';
        }
    }

    async sendMessage() {
        const message = this.messageInput.value.trim();
        if (!message || this.isTyping) return;

        this.addMessage(message, 'user');
        this.messageInput.value = '';
        this.updateCharCount();
        this.autoResizeTextarea();
        this.setLoading(true);

        try {
            const response = await fetch('/api/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    message: message,
                    conversationId: this.conversationId
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.success) {
                this.conversationId = data.conversationId;
                await this.typeMessage(data.response, 'bot');

                // Show token usage if available
                if (data.tokenUsage) {
                    this.showTokenUsage(data.tokenUsage);
                }

                this.updateStatusIndicator(true);
            } else {
                this.addMessage(data.error || 'Sorry, something went wrong.', 'bot', true);
                this.updateStatusIndicator(false);
            }
        } catch (error) {
            console.error('Error sending message:', error);
            this.addMessage('Sorry, I encountered a connection error. Please check your internet connection and try again.', 'bot', true);
            this.updateStatusIndicator(false);
        } finally {
            this.setLoading(false);
        }
    }

    showTokenUsage(tokenUsage) {
        if (tokenUsage.totalTokens) {
            this.tokenInfo.textContent = `Prompt: ${tokenUsage.promptTokens || 0} | Completion: ${tokenUsage.completionTokens || 0} | Total: ${tokenUsage.totalTokens}`;
            this.tokenUsage.style.display = 'block';

            // Hide after 10 seconds
            setTimeout(() => {
                this.tokenUsage.style.display = 'none';
            }, 10000);
        }
    }

    async typeMessage(content, sender, isError = false) {
        const messageDiv = this.createMessageElement(content, sender, isError);
        const messageContent = messageDiv.querySelector('.message-content');

        messageContent.textContent = '';
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();

        // Enhanced typing effect with variable speed
        const sentences = content.split(/(?<=[.!?])\s+/);
        let currentText = '';

        for (let i = 0; i < sentences.length; i++) {
            const sentence = sentences[i];
            const words = sentence.split(' ');

            for (let j = 0; j < words.length; j++) {
                currentText += (currentText ? ' ' : '') + words[j];
                messageContent.textContent = currentText;
                this.scrollToBottom();

                // Variable delay based on content
                let delay = 30;
                if (words[j].includes('```')) delay = 100; // Code blocks
                else if (words[j].match(/[.!?]$/)) delay = 150; // End of sentence
                else if (words[j].match(/[,;:]$/)) delay = 80; // Punctuation
                else if (words[j].length > 8) delay = 50; // Long words

                await this.sleep(delay);
            }

            // Pause at end of sentence
            if (i < sentences.length - 1) {
                await this.sleep(200);
            }
        }
    }

    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    createMessageElement(content, sender, isError = false) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${sender}-message${isError ? ' error-message' : ''}`;

        const messageAvatar = document.createElement('div');
        messageAvatar.className = 'message-avatar';
        if (sender === 'bot') {
            messageAvatar.textContent = '🧠';
        }

        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';
        messageContent.textContent = content;

        const messageTime = document.createElement('div');
        messageTime.className = 'message-time';
        messageTime.textContent = new Date().toLocaleTimeString();

        messageDiv.appendChild(messageAvatar);
        messageDiv.appendChild(messageContent);
        messageDiv.appendChild(messageTime);

        return messageDiv;
    }

    addMessage(content, sender, isError = false) {
        const messageDiv = this.createMessageElement(content, sender, isError);
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }

    setLoading(isLoading) {
        this.isTyping = isLoading;
        this.sendBtn.disabled = isLoading;
        this.messageInput.disabled = isLoading;

        if (isLoading) {
            this.sendBtn.innerHTML = '<span class="loading"></span>';
            this.addTypingIndicator();
        } else {
            this.sendBtn.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="22" y1="2" x2="11" y2="13"></line>
                    <polygon points="22,2 15,22 11,13 2,9"></polygon>
                </svg>
            `;
            this.removeTypingIndicator();
            this.messageInput.focus();
        }
    }

    addTypingIndicator() {
        const typingDiv = document.createElement('div');
        typingDiv.className = 'message bot-message typing-indicator';
        typingDiv.id = 'typing-indicator';

        const avatar = document.createElement('div');
        avatar.className = 'message-avatar';
        avatar.textContent = '🧠';

        const content = document.createElement('div');
        content.className = 'message-content';
        content.innerHTML = `
            <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
            </div>
        `;

        typingDiv.appendChild(avatar);
        typingDiv.appendChild(content);
        this.chatMessages.appendChild(typingDiv);
        this.scrollToBottom();
    }

    removeTypingIndicator() {
        const typingIndicator = document.getElementById('typing-indicator');
        if (typingIndicator) {
            typingIndicator.remove();
        }
    }

    async clearChat() {
        if (!confirm('Are you sure you want to clear the chat? This will remove all conversation history.')) {
            return;
        }

        try {
            if (this.conversationId) {
                await fetch(`/api/chat/${this.conversationId}`, {
                    method: 'DELETE'
                });
            }

            // Clear messages except the initial welcome message
            const messages = this.chatMessages.querySelectorAll('.message');
            messages.forEach((message, index) => {
                if (index > 0) {
                    message.remove();
                }
            });

            this.conversationId = null;
            this.tokenUsage.style.display = 'none';
            this.showToast('🗑️ Chat cleared successfully!', 'success');

        } catch (error) {
            console.error('Error clearing chat:', error);
            this.showToast('❌ Failed to clear chat. Please try again.', 'error');
        }
    }

    showConfirmModal(title, message, confirmText, cancelText) {
        return new Promise((resolve) => {
            // Create modal overlay
            const overlay = document.createElement('div');
            overlay.className = 'fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in';

            // Create modal
            const modal = document.createElement('div');
            modal.className = 'bg-white rounded-2xl shadow-2xl max-w-md w-full transform animate-slide-up';

            modal.innerHTML = `
                <div class="p-6">
                    <div class="flex items-center space-x-3 mb-4">
                        <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                            <span class="text-red-600 text-xl">⚠️</span>
                        </div>
                        <h3 class="text-lg font-semibold text-gray-900">${title}</h3>
                    </div>
                    <p class="text-gray-600 mb-6">${message}</p>
                    <div class="flex space-x-3 justify-end">
                        <button class="cancel-btn px-4 py-2 text-gray-600 hover:text-gray-800 font-medium transition-colors">
                            ${cancelText}
                        </button>
                        <button class="confirm-btn px-6 py-2 bg-red-500 hover:bg-red-600 text-white font-medium rounded-xl transition-all duration-300 transform hover:scale-105">
                            ${confirmText}
                        </button>
                    </div>
                </div>
            `;

            overlay.appendChild(modal);
            document.body.appendChild(overlay);

            // Handle button clicks
            const confirmBtn = modal.querySelector('.confirm-btn');
            const cancelBtn = modal.querySelector('.cancel-btn');

            const cleanup = () => {
                overlay.remove();
            };

            confirmBtn.addEventListener('click', () => {
                cleanup();
                resolve(true);
            });

            cancelBtn.addEventListener('click', () => {
                cleanup();
                resolve(false);
            });

            // Close on overlay click
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    cleanup();
                    resolve(false);
                }
            });

            // Close on escape key
            const handleEscape = (e) => {
                if (e.key === 'Escape') {
                    cleanup();
                    resolve(false);
                    document.removeEventListener('keydown', handleEscape);
                }
            };
            document.addEventListener('keydown', handleEscape);
        });
    }

    showToast(message, type) {
        const toast = document.createElement('div');
        toast.className = `transform translate-x-full transition-all duration-300 ease-out`;

        // Toast styling based on type
        let bgClass, iconClass;
        switch (type) {
            case 'success':
                bgClass = 'bg-green-500';
                iconClass = '✅';
                break;
            case 'error':
                bgClass = 'bg-red-500';
                iconClass = '❌';
                break;
            case 'info':
                bgClass = 'bg-blue-500';
                iconClass = 'ℹ️';
                break;
            default:
                bgClass = 'bg-gray-500';
                iconClass = '📢';
        }

        toast.innerHTML = `
            <div class="${bgClass} text-white px-6 py-4 rounded-xl shadow-lg flex items-center space-x-3 max-w-sm">
                <span class="text-lg">${iconClass}</span>
                <span class="font-medium">${message}</span>
                <button class="ml-auto text-white/80 hover:text-white transition-colors" onclick="this.closest('.transform').remove()">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                    </svg>
                </button>
            </div>
        `;

        this.toastContainer.appendChild(toast);

        // Animate in
        setTimeout(() => {
            toast.classList.remove('translate-x-full');
            toast.classList.add('translate-x-0');
        }, 10);

        // Auto remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.classList.add('translate-x-full');
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.remove();
                    }
                }, 300);
            }
        }, 5000);
    }

    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
}

// Initialize the chatbot when the page loads
document.addEventListener('DOMContentLoaded', () => {
    const chatbot = new GroqDirectChatBot();

    console.log('🚀 DeepSeek R1 Chatbot with Tailwind CSS initialized!');
    console.log('🎨 Modern UI with enhanced animations and effects');
    console.log('🔗 Direct API communication without Spring AI dependency');
    console.log('🧠 Model: deepseek-r1-distill-llama-70b');

    // Make chatbot globally accessible for debugging
    window.chatbot = chatbot;

    // Add some nice entrance animations
    setTimeout(() => {
        document.querySelector('.w-full.max-w-5xl').classList.add('animate-fade-in');
    }, 100);
});