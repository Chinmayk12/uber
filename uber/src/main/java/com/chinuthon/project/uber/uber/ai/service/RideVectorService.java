package com.chinuthon.project.uber.uber.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideVectorService {

    private final VectorStore vectorStore;

    /**
     * Store ride information in vector store for semantic search
     */
    public void storeRideInfo(Long rideId, String userId, String description, Map<String, Object> metadata) {
        try {
            log.info("=== ATTEMPTING TO STORE RIDE INFO ===");
            log.info("RideId: {}, UserId: {}, Description: {}", rideId, userId, description);
            log.info("Metadata: {}", metadata);
            
            Document document = new Document(
                description,
                Map.of(
                    "rideId", rideId.toString(),
                    "userId", userId,
                    "type", "ride",
                    "timestamp", System.currentTimeMillis()
                )
            );
            
            // Add additional metadata if provided
            if (metadata != null && !metadata.isEmpty()) {
                document.getMetadata().putAll(metadata);
            }

            log.info("Document created, calling vectorStore.add()...");
            vectorStore.add(List.of(document));
            log.info("✅ SUCCESS: Stored ride info in vector store: rideId={}", rideId);
        } catch (Exception e) {
            log.error("❌ FAILED to store ride info in vector store", e);
            log.error("Error details: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Store conversation context in vector store
     */
    public void storeConversationContext(String conversationId, String userId, String context) {
        try {
            log.info("=== ATTEMPTING TO STORE CONVERSATION ===");
            log.info("ConversationId: {}, UserId: {}, Context: {}", conversationId, userId, context);
            
            Document document = new Document(
                context,
                Map.of(
                    "conversationId", conversationId,
                    "userId", userId,
                    "type", "conversation",
                    "timestamp", System.currentTimeMillis()
                )
            );

            log.info("Document created, calling vectorStore.add()...");
            vectorStore.add(List.of(document));
            log.info("✅ SUCCESS: Stored conversation context in vector store");
        } catch (Exception e) {
            log.error("❌ FAILED to store conversation context", e);
            log.error("Error details: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Search vector store for relevant context based on user query
     * This enables long-term memory and RAG (Retrieval Augmented Generation)
     */
    public String searchRelevantContext(String userId, String query, int maxResults) {
        try {
            log.info("=== SEARCHING VECTOR STORE ===");
            log.info("UserId: {}, Query: {}, MaxResults: {}", userId, query, maxResults);
            
            // Search vector store using simple similarity search
            List<Document> similarDocuments = vectorStore.similaritySearch(query);
            
            if (similarDocuments == null || similarDocuments.isEmpty()) {
                log.info("No relevant context found in vector store");
                return "";
            }

            // Filter by userId and limit results
            List<Document> userDocuments = similarDocuments.stream()
                    .filter(doc -> userId.equals(doc.getMetadata().get("userId")))
                    .limit(maxResults)
                    .toList();

            if (userDocuments.isEmpty()) {
                log.info("No relevant context found for user {}", userId);
                return "";
            }

            // Build context string from retrieved documents
            StringBuilder contextBuilder = new StringBuilder();
            for (int i = 0; i < userDocuments.size(); i++) {
                Document doc = userDocuments.get(i);
                contextBuilder.append(String.format("%d. %s\n", i + 1, doc.getText()));
            }

            String context = contextBuilder.toString();
            log.info("✅ Retrieved {} relevant documents for user {}", userDocuments.size(), userId);
            log.debug("Context: {}", context);
            
            return context;

        } catch (Exception e) {
            log.error("❌ FAILED to search vector store", e);
            log.error("Error details: {}", e.getMessage());
            return "";
        }
    }
}
