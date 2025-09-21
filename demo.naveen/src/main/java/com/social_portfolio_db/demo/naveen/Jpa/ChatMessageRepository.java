package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.ChatMessage;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrderByTimestampAsc(Users sender, Users receiver);
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
    List<ChatMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
} 