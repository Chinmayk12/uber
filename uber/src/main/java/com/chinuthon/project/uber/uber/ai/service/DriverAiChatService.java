package com.chinuthon.project.uber.uber.ai.service;

import com.chinuthon.project.uber.uber.ai.tools.DriverTools;
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
public class DriverAiChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RideVectorService rideVectorService;

    private static final String SYSTEM_PROMPT = """
            You are an intelligent Uber driver assistant. You help drivers manage their rides and earnings.
            
            Your capabilities:
            1. **Accept ride requests** — Accept incoming ride requests by ride request ID.
            
            2. **Start rides** — Start a ride using the ride request ID and OTP provided by the rider.
            
            3. **End rides** — Complete a ride and process payment by ride ID.
            
            4. **Cancel rides** — Cancel a ride that hasn't been completed yet.
            
            5. **Rate riders** — Rate a rider (1-5 stars) after a completed ride.
            
            6. **View profile** — Show the driver's profile including rating, vehicle, and availability.
            
            7. **View ride history** — Show the driver's recent rides with earnings.
            
            Guidelines:
            - Be professional, helpful, and concise.
            - Always confirm the action you're about to take before executing it.
            - When a ride is accepted, share the ride ID, OTP, and fare with the driver.
            - When starting a ride, verify the OTP matches what the rider provides.
            - When ending a ride, confirm the payment method and amount.
            - Use the Indian Rupee (₹) symbol for fares.
            - If something goes wrong, explain the error in simple terms.
            - If the driver's request is unclear, ask clarifying questions.
            - Use your knowledge base to answer questions about past rides and patterns.
            """;

    public DriverAiChatService(@Qualifier("openAiChatModel") ChatModel chatModel,
                               ChatMemory chatMemory,
                               VectorStore vectorStore,
                               DriverTools driverTools,
                               RideVectorService rideVectorService) {
        this.vectorStore = vectorStore;
        this.rideVectorService = rideVectorService;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(driverTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        log.info("DriverAiChatService initialized with Groq Cloud ChatModel, VectorStore, and DriverTools");
    }

    public String chat(String message, String conversationId) {
        log.info("Driver AI Chat — conversationId: {}, message: {}", conversationId, message);

        // Get user info
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId().toString();

        // Store user message in vector store
        String userContext = String.format("Driver %s asked: %s", user.getName(), message);
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
        String aiContext = String.format("AI responded to driver %s: %s", user.getName(), response);
        rideVectorService.storeConversationContext(conversationId, userId, aiContext);

        log.info("Driver AI Chat — response: {}", response);
        return response;
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }
}
