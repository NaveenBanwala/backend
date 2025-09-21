package com.social_portfolio_db.demo.naveen.WebSocketSetUp;

import com.social_portfolio_db.demo.naveen.Entity.ChatMessage;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Jpa.ChatMessageRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserJpa userJpa;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        Users sender = userJpa.findById(chatMessageDTO.getSenderId()).orElse(null);
        Users receiver = userJpa.findById(chatMessageDTO.getReceiverId()).orElse(null);
        if (sender == null || receiver == null) return;
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(chatMessageDTO.getContent())
                .timestamp(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);
        // Send to receiver
        messagingTemplate.convertAndSendToUser(
            receiver.getId() + "", "/queue/messages", 
            new ChatMessageDTO(chatMessage.getId(), sender.getId(), receiver.getId(), chatMessage.getContent(), chatMessage.getTimestamp())
        );
        // Optionally, send to sender as well for confirmation
        messagingTemplate.convertAndSendToUser(
            sender.getId() + "", "/queue/messages", 
            new ChatMessageDTO(chatMessage.getId(), sender.getId(), receiver.getId(), chatMessage.getContent(), chatMessage.getTimestamp())
        );
    }

    // DTO for WebSocket messages
    public static class ChatMessageDTO {
        private Long id;
        private Long senderId;
        private Long receiverId;
        private String content;
        private LocalDateTime timestamp;
        public ChatMessageDTO() {}
        public ChatMessageDTO(Long id, Long senderId, Long receiverId, String content, LocalDateTime timestamp) {
            this.id = id;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.timestamp = timestamp;
        }
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
} 