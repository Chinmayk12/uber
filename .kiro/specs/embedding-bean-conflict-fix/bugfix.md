# Bugfix Requirements Document

## Introduction

The Spring Boot application fails to start due to a bean conflict where both OllamaEmbeddingAutoConfiguration and OpenAiEmbeddingAutoConfiguration create EmbeddingModel beans. The PgVectorStoreAutoConfiguration expects a single EmbeddingModel bean but finds two, causing the application startup to fail.

The application uses:
- Groq Cloud (via OpenAI starter for API compatibility) for chat model
- Ollama locally for embeddings (mxbai-embed-large:latest)
- PGVector as vector store

The configuration `spring.ai.openai.embedding.enabled: false` in application.yml is not preventing the OpenAI embedding bean from being created.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN the Spring Boot application starts with both spring-ai-starter-model-openai and spring-ai-starter-model-ollama dependencies THEN the application fails with "Parameter 1 of method vectorStore in org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration required a single bean, but 2 were found"

1.2 WHEN spring.ai.openai.embedding.enabled is set to false in application.yml THEN the OpenAiEmbeddingAutoConfiguration still creates an openAiEmbeddingModel bean

1.3 WHEN PgVectorStoreAutoConfiguration attempts to autowire an EmbeddingModel THEN it finds both ollamaEmbeddingModel and openAiEmbeddingModel beans and cannot determine which one to use

### Expected Behavior (Correct)

2.1 WHEN the Spring Boot application starts with both spring-ai-starter-model-openai and spring-ai-starter-model-ollama dependencies THEN the application SHALL start successfully with only one EmbeddingModel bean available

2.2 WHEN spring.ai.openai.embedding.enabled is set to false in application.yml THEN the OpenAiEmbeddingAutoConfiguration SHALL NOT create an openAiEmbeddingModel bean

2.3 WHEN PgVectorStoreAutoConfiguration attempts to autowire an EmbeddingModel THEN it SHALL find exactly one EmbeddingModel bean (from Ollama) and use it successfully

### Unchanged Behavior (Regression Prevention)

3.1 WHEN the application uses OpenAI starter for chat model (Groq Cloud compatibility) THEN the system SHALL CONTINUE TO provide the ChatModel bean for AI chat functionality

3.2 WHEN the application uses Ollama for embeddings THEN the system SHALL CONTINUE TO provide the ollamaEmbeddingModel bean with the mxbai-embed-large:latest model

3.3 WHEN PgVectorStoreAutoConfiguration uses the EmbeddingModel THEN the system SHALL CONTINUE TO initialize the vector store correctly with the provided embedding model

3.4 WHEN the AI chat service uses function calling and vector store advisors THEN the system SHALL CONTINUE TO work correctly with the configured models
