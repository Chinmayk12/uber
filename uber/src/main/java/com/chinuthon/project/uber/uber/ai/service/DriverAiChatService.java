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
            1. **Update availability** — Set driver online (available=true) or offline (available=false).
            
            2. **Accept ride requests** — Accept incoming ride requests by ride request ID.
            
            3. **Start rides** — Start a ride using the ride request ID and OTP provided by the rider.
            
            4. **End rides** — Complete a ride and process payment by ride ID.
            
            5. **Cancel rides** — Cancel a ride that hasn't been completed yet.
            
            6. **Rate riders** — Rate a rider (1-5 stars) after a completed ride.
            
            7. **View profile** — Show the driver's profile including rating, vehicle, and availability.
            
            8. **View ride history** — Show the driver's recent rides with earnings.
            
            Guidelines:
            - Be professional, helpful, and concise.
            - If a driver tries to accept a ride but is offline, suggest they go online first using updateAvailability.
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

        // Only search vector store if user is asking about past data
        // Don't retrieve for action commands (accept, start, end, etc.)
        String retrievedContext = "";
        if (isHistoricalQuery(message)) {
            retrievedContext = rideVectorService.searchRelevantContext(userId, message, 3);
            log.info("Historical query detected - retrieved context");
        } else {
            log.info("Action command detected - skipping vector search");
        }
        
        // Build prompt with retrieved context (only if relevant)
        String enhancedMessage = message;
        if (retrievedContext != null && !retrievedContext.isEmpty()) {
            enhancedMessage = String.format("""
                    Relevant information from your past:
                    %s
                    
                    Current question: %s
                    
                    Note: Use the above context ONLY to answer questions about past rides. 
                    For new actions (accepting, starting, ending rides), use the tools directly without referencing past data.
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

    /**
     * Determine if the message is asking about historical data
     * vs performing a new action
     */
    private boolean isHistoricalQuery(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Historical query keywords
        String[] historicalKeywords = {
            "what", "when", "where", "which", "who", "how many", "how much",
            "show me", "tell me", "list", "history", "past", "previous", 
            "last", "yesterday", "ago", "before", "earlier", "did i", "have i",
            "my rides", "my trips", "earned", "total", "average", "earnings"
        };
        
        // Action keywords (should NOT trigger vector search)
        String[] actionKeywords = {
            "accept", "start", "end", "cancel", "rate", "check status"
        };
        
        // If it's an action command, don't search vector store
        for (String actionKeyword : actionKeywords) {
            if (lowerMessage.contains(actionKeyword)) {
                return false;
            }
        }
        
        // If it contains historical keywords, search vector store
        for (String historicalKeyword : historicalKeywords) {
            if (lowerMessage.contains(historicalKeyword)) {
                return true;
            }
        }
        
        return false;
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }
}
