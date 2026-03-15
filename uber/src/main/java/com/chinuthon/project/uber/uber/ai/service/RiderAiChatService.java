package com.chinuthon.project.uber.uber.ai.service;

import com.chinuthon.project.uber.uber.ai.tools.RiderTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.chinuthon.project.uber.uber.entities.User;

@Service
@Slf4j
public class RiderAiChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RideVectorService rideVectorService;

    private static final String SYSTEM_PROMPT = """
            You are an intelligent Uber ride assistant. You help riders with their transportation needs.
            
            Your capabilities:
            1. **Book rides** — When a user wants to book a ride, extract the pickup and dropoff coordinates 
               (latitude, longitude) and payment method (CASH or WALLET, default to CASH if not specified).
               If the user provides place names instead of coordinates, ask them to provide the coordinates 
               as you cannot look up place names.
            
            2. **Cancel rides** — Cancel a confirmed ride by its ride ID.
            
            3. **Check ride status** — Get details about a specific ride.
            
            4. **View ride history** — Show the user's recent rides.
            
            5. **View profile** — Show the rider's profile information.
            
            6. **Check wallet balance** — Show the rider's wallet balance.
            
            7. **Rate drivers** — Rate a driver (1-5 stars) after a completed ride.
            
            Guidelines:
            - Be friendly, helpful, and concise.
            - Always confirm the action you're about to take before executing it (e.g., "I'll book a ride from X to Y with CASH payment. Let me do that for you!").
            - When a ride is booked successfully, share the ride request ID and fare.
            - If something goes wrong, explain the error in simple terms.
            - Use the Indian Rupee (₹) symbol for fares.
            - If the user's request is unclear, ask clarifying questions.
            - Use your knowledge base to answer questions about past rides and patterns.
            """;

    public RiderAiChatService(@Qualifier("openAiChatModel") ChatModel chatModel,
                         ChatMemory chatMemory,
                         VectorStore vectorStore,
                         RiderTools riderTools,
                         RideVectorService rideVectorService) {
        this.vectorStore = vectorStore;
        this.rideVectorService = rideVectorService;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(riderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        log.info("RiderAiChatService initialized with Groq Cloud ChatModel, VectorStore, and RiderTools");
    }

    public String chat(String message, String conversationId) {
        log.info("Rider AI Chat — conversationId: {}, message: {}", conversationId, message);

        // Get user info
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId().toString();

        // Store user message in vector store
        String userContext = String.format("User %s asked: %s", user.getName(), message);
        rideVectorService.storeConversationContext(conversationId, userId, userContext);

        // Search vector store for relevant past context (RAG)
        String retrievedContext = rideVectorService.searchRelevantContext(userId, message, 5);
        
        // Build prompt with retrieved context
        String enhancedMessage = message;
        if (retrievedContext != null && !retrievedContext.isEmpty()) {
            enhancedMessage = String.format("""
                    Context from your past interactions and rides:
                    %s
                    
                    Current question: %s
                    """, retrievedContext, message);
            log.info("Enhanced message with {} chars of context", retrievedContext.length());
        }

        // Get AI response with context
        String response = chatClient.prompt()
                .user(enhancedMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        // Store AI response in vector store
        String aiContext = String.format("AI responded to %s: %s", user.getName(), response);
        rideVectorService.storeConversationContext(conversationId, userId, aiContext);

        log.info("Rider AI Chat — response: {}", response);
        return response;
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }
}
