package com.chinuthon.project.uber.uber.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom Jina AI Embedding Model implementation
 * Directly calls Jina AI API at https://api.jina.ai/v1/embeddings
 * Using model: jina-embeddings-v5-text-small
 */
@Slf4j
public class JinaAiEmbeddingModel extends AbstractEmbeddingModel {

    private final WebClient webClient;
    private final String model;
    private static final String JINA_EMBEDDINGS_URL = "https://api.jina.ai/v1/embeddings";

    public JinaAiEmbeddingModel(String apiKey, String model) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(JINA_EMBEDDINGS_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        
        log.info("=== JINA AI EMBEDDING MODEL INITIALIZED ===");
        log.info("Endpoint: {}", JINA_EMBEDDINGS_URL);
        log.info("Model: {}", model);
    }

    @Override
    public float[] embed(Document document) {
        List<Double> embedding = callJinaApi(List.of(document.getText())).get(0);
        return convertToFloatArray(embedding);
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        log.debug("Calling Jina AI embeddings API with {} inputs", request.getInstructions().size());
        
        try {
            // getInstructions() returns List<String>
            List<String> inputs = request.getInstructions();

            List<List<Double>> embeddings = callJinaApi(inputs);

            // Convert to Spring AI format
            List<Embedding> embeddingList = embeddings.stream()
                    .map(emb -> new Embedding(convertToFloatArray(emb), 0))
                    .collect(Collectors.toList());

            log.debug("✅ Received {} embeddings from Jina AI", embeddingList.size());

            return new EmbeddingResponse(embeddingList);

        } catch (Exception e) {
            log.error("❌ Failed to get embeddings from Jina AI", e);
            throw new RuntimeException("Failed to get embeddings from Jina AI: " + e.getMessage(), e);
        }
    }

    private List<List<Double>> callJinaApi(List<String> inputs) {
        // Create request body matching Jina AI API spec
        JinaEmbeddingRequest jinaRequest = new JinaEmbeddingRequest(
                model,
                inputs,
                true,  // normalized
                "retrieval.query"  // task
        );

        log.debug("Sending request to Jina AI: model={}, inputs={}", model, inputs.size());

        // Call Jina AI API
        JinaEmbeddingResponse response = webClient.post()
                .bodyValue(jinaRequest)
                .retrieve()
                .bodyToMono(JinaEmbeddingResponse.class)
                .block();

        if (response == null || response.data == null) {
            throw new RuntimeException("Empty response from Jina AI");
        }

        log.debug("Received response from Jina AI: {} embeddings, {} tokens", 
                response.data.size(), response.usage.total_tokens);

        return response.data.stream()
                .map(data -> data.embedding)
                .collect(Collectors.toList());
    }

    private float[] convertToFloatArray(List<Double> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).floatValue();
        }
        return array;
    }

    @Override
    public int dimensions() {
        // jina-embeddings-v5-text-small produces 1024-dimensional embeddings
        return 1024;
    }

    // Request/Response DTOs matching Jina AI API specification
    private record JinaEmbeddingRequest(
            String model,
            List<String> input,
            boolean normalized,
            String task
    ) {}

    private record JinaEmbeddingResponse(
            String model,
            String object,
            Usage usage,
            List<EmbeddingData> data
    ) {}

    private record Usage(
            int total_tokens
    ) {}

    private record EmbeddingData(
            String object,
            int index,
            List<Double> embedding
    ) {}
}
