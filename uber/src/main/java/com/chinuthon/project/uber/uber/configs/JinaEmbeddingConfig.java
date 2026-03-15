package com.chinuthon.project.uber.uber.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JinaEmbeddingConfig {

    @Value("${spring.ai.openai.embedding.api-key}")
    private String jinaApiKey;

    @Value("${spring.ai.openai.embedding.options.model}")
    private String jinaModel;

    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("=== INITIALIZING CUSTOM JINA AI EMBEDDING MODEL ===");
        log.info("Model: {}", jinaModel);
        log.info("Endpoint: https://api.jina.ai/v1/embeddings");
        
        // Use custom Jina AI implementation that directly calls the API
        JinaAiEmbeddingModel embeddingModel = new JinaAiEmbeddingModel(jinaApiKey, jinaModel);
        
        log.info("Jina AI Embedding Model initialized successfully");
        log.info("Using model: jina-embeddings-v5-text-small");
        log.info("Embedding dimensions: 1024");
        
        return embeddingModel;
    }
}
