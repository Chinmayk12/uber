# Embedding Bean Conflict Bugfix Design

## Overview

The Spring Boot application fails to start due to a bean conflict where both OpenAI and Ollama autoconfiguration classes create EmbeddingModel beans. The PgVectorStoreAutoConfiguration expects a single EmbeddingModel bean but finds two, causing startup failure. The fix will exclude the OpenAI embedding autoconfiguration while preserving the OpenAI chat model functionality needed for Groq Cloud compatibility.

The solution uses Spring Boot's autoconfiguration exclusion mechanism to prevent OpenAiEmbeddingAutoConfiguration from running, ensuring only the Ollama embedding bean is created. This is a minimal, targeted fix that addresses the root cause without modifying application logic or requiring custom bean definitions.

## Glossary

- **Bug_Condition (C)**: The condition that triggers the bug - when both spring-ai-starter-model-openai and spring-ai-starter-model-ollama are present on the classpath
- **Property (P)**: The desired behavior - application starts successfully with exactly one EmbeddingModel bean (from Ollama)
- **Preservation**: Existing OpenAI ChatModel functionality for Groq Cloud and Ollama embedding functionality must remain unchanged
- **OpenAiEmbeddingAutoConfiguration**: Spring AI autoconfiguration class that creates the openAiEmbeddingModel bean from the spring-ai-starter-model-openai dependency
- **OllamaEmbeddingAutoConfiguration**: Spring AI autoconfiguration class that creates the ollamaEmbeddingModel bean from the spring-ai-starter-model-ollama dependency
- **PgVectorStoreAutoConfiguration**: Spring AI autoconfiguration class that requires a single EmbeddingModel bean for vector store initialization
- **@SpringBootApplication**: The main application annotation that can exclude specific autoconfiguration classes
- **spring.autoconfigure.exclude**: Configuration property that can exclude autoconfiguration classes at runtime

## Bug Details

### Bug Condition

The bug manifests when the Spring Boot application starts with both spring-ai-starter-model-openai and spring-ai-starter-model-ollama dependencies on the classpath. The Spring AI framework's autoconfiguration mechanism activates both OpenAiEmbeddingAutoConfiguration and OllamaEmbeddingAutoConfiguration, each creating an EmbeddingModel bean. When PgVectorStoreAutoConfiguration attempts to autowire an EmbeddingModel, it finds two beans and cannot determine which one to use.

**Formal Specification:**
```
FUNCTION isBugCondition(input)
  INPUT: input of type ApplicationContext
  OUTPUT: boolean
  
  RETURN input.classpathContains("spring-ai-starter-model-openai")
         AND input.classpathContains("spring-ai-starter-model-ollama")
         AND input.beanCount(EmbeddingModel.class) > 1
         AND NOT input.hasExclusion("OpenAiEmbeddingAutoConfiguration")
END FUNCTION
```

### Examples

- **Example 1**: Application starts with both OpenAI and Ollama starters → Expected: Single Ollama embedding bean created, Actual: Both OpenAI and Ollama embedding beans created, application fails with "required a single bean, but 2 were found"
- **Example 2**: Configuration sets spring.ai.openai.embedding.enabled=false → Expected: OpenAI embedding bean not created, Actual: OpenAI embedding bean still created (property is ignored by autoconfiguration)
- **Example 3**: PgVectorStoreAutoConfiguration autowires EmbeddingModel → Expected: Finds exactly one bean (Ollama), Actual: Finds two beans (OpenAI and Ollama), throws NoUniqueBeanDefinitionException
- **Edge Case**: Application uses OpenAI ChatModel for Groq Cloud → Expected: ChatModel bean continues to work after fix, Actual: Must verify ChatModel is unaffected by embedding autoconfiguration exclusion

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- OpenAI ChatModel bean (openAiChatModel) must continue to be created and function correctly for Groq Cloud API calls
- Ollama EmbeddingModel bean (ollamaEmbeddingModel) must continue to be created with mxbai-embed-large:latest model
- PgVectorStoreAutoConfiguration must continue to initialize the vector store correctly with the Ollama embedding model
- AiChatService must continue to use the OpenAI ChatModel for chat functionality with function calling and memory advisors
- All existing AI chat endpoints and ride booking tools must continue to work without modification

**Scope:**
All inputs that do NOT involve the Spring Boot application startup with both embedding starters should be completely unaffected by this fix. This includes:
- Runtime chat operations using the OpenAI ChatModel
- Embedding operations using the Ollama EmbeddingModel
- Vector store operations using PgVector
- All business logic in services, controllers, and repositories

## Hypothesized Root Cause

Based on the bug description and Spring AI framework behavior, the most likely issues are:

1. **Autoconfiguration Activation**: Both OpenAiEmbeddingAutoConfiguration and OllamaEmbeddingAutoConfiguration are activated because their conditional checks pass when their respective starters are on the classpath
   - OpenAI starter presence triggers OpenAiEmbeddingAutoConfiguration
   - Ollama starter presence triggers OllamaEmbeddingAutoConfiguration
   - No mutual exclusion mechanism exists between these autoconfiguration classes

2. **Configuration Property Ignored**: The spring.ai.openai.embedding.enabled=false property is not being respected by OpenAiEmbeddingAutoConfiguration
   - The autoconfiguration class may not have a @ConditionalOnProperty annotation checking this property
   - The property may be intended for runtime behavior, not bean creation control
   - Spring AI 1.1.2 may not support this property for autoconfiguration control

3. **Bean Naming Collision**: Both autoconfiguration classes create beans of type EmbeddingModel without using @Primary or @Qualifier annotations
   - PgVectorStoreAutoConfiguration expects exactly one EmbeddingModel bean
   - No disambiguation mechanism exists when multiple EmbeddingModel beans are present
   - Spring's autowiring by type fails with NoUniqueBeanDefinitionException

4. **Dependency Design**: The spring-ai-starter-model-openai dependency includes both chat and embedding autoconfiguration
   - No separate starter exists for OpenAI chat-only functionality
   - Using the OpenAI starter for Groq Cloud compatibility inadvertently enables embedding autoconfiguration
   - The framework assumes users want all model types from a single provider

## Correctness Properties

Property 1: Bug Condition - Single Embedding Bean Creation

_For any_ application startup where both spring-ai-starter-model-openai and spring-ai-starter-model-ollama are on the classpath and OpenAiEmbeddingAutoConfiguration is excluded, the application SHALL start successfully with exactly one EmbeddingModel bean (from Ollama), and PgVectorStoreAutoConfiguration SHALL autowire this bean without ambiguity.

**Validates: Requirements 2.1, 2.2, 2.3**

Property 2: Preservation - OpenAI ChatModel Functionality

_For any_ runtime operation that uses the OpenAI ChatModel for chat functionality (Groq Cloud API calls, function calling, memory advisors), the fixed application SHALL produce exactly the same behavior as the original application, preserving all chat capabilities without any degradation or modification.

**Validates: Requirements 3.1, 3.4, 3.5**

## Fix Implementation

### Changes Required

Assuming our root cause analysis is correct (autoconfiguration activation without mutual exclusion):

**File**: `uber/src/main/java/com/chinuthon/project/uber/uber/UberApplication.java`

**Annotation**: `@SpringBootApplication`

**Specific Changes**:
1. **Add Autoconfiguration Exclusion**: Modify the @SpringBootApplication annotation to exclude OpenAiEmbeddingAutoConfiguration
   - Add exclude parameter: `@SpringBootApplication(exclude = {OpenAiEmbeddingAutoConfiguration.class})`
   - This prevents the OpenAI embedding bean from being created at startup
   - The exclusion is specific to embedding autoconfiguration, not the entire OpenAI starter

2. **Import Statement**: Add import for the excluded class
   - Add: `import org.springframework.ai.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;`
   - This allows the exclusion to reference the autoconfiguration class by type

3. **Verification**: Ensure no other configuration interferes with the exclusion
   - Confirm no spring.autoconfigure.exclude property in application.yml conflicts with the annotation
   - Verify no custom @Configuration classes attempt to create additional EmbeddingModel beans
   - Check that the exclusion doesn't affect OpenAiChatAutoConfiguration (separate class)

**Alternative Approach (if main class modification is not preferred)**:
- Add `spring.autoconfigure.exclude=org.springframework.ai.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration` to application.yml
- This achieves the same result through configuration rather than annotation
- Less type-safe but doesn't require code changes to the main application class

**Why This Fix Works**:
- Spring Boot's autoconfiguration exclusion mechanism prevents the specified class from running
- OpenAiEmbeddingAutoConfiguration is responsible solely for creating the embedding bean
- OpenAiChatAutoConfiguration (separate class) continues to run and create the chat model bean
- Only one EmbeddingModel bean (from Ollama) remains in the application context
- PgVectorStoreAutoConfiguration successfully autowires the single Ollama embedding bean

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the bug on unfixed code, then verify the fix works correctly and preserves existing behavior.

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the bug BEFORE implementing the fix. Confirm or refute the root cause analysis. If we refute, we will need to re-hypothesize.

**Test Plan**: Attempt to start the application with the current configuration (both starters present, no exclusion). Observe the startup failure and examine the exception stack trace to confirm the root cause is bean ambiguity in PgVectorStoreAutoConfiguration.

**Test Cases**:
1. **Startup Failure Test**: Start application with current configuration (will fail on unfixed code)
   - Expected: NoUniqueBeanDefinitionException with message "required a single bean, but 2 were found"
   - Confirms: Both OpenAI and Ollama embedding beans are being created
2. **Bean Count Test**: Query application context for EmbeddingModel beans before PgVectorStoreAutoConfiguration runs (will fail on unfixed code)
   - Expected: Two beans found (openAiEmbeddingModel, ollamaEmbeddingModel)
   - Confirms: Both autoconfiguration classes are activated
3. **Configuration Property Test**: Verify spring.ai.openai.embedding.enabled=false has no effect (will fail on unfixed code)
   - Expected: OpenAI embedding bean still created despite property being false
   - Confirms: Property is not used for autoconfiguration control
4. **ChatModel Isolation Test**: Verify OpenAI ChatModel bean is created independently of embedding beans (should pass on unfixed code)
   - Expected: openAiChatModel bean exists and is functional
   - Confirms: Chat and embedding autoconfiguration are separate

**Expected Counterexamples**:
- Application fails to start with NoUniqueBeanDefinitionException
- Possible causes: Both autoconfiguration classes active, no @Primary annotation, configuration property ignored

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds, the fixed function produces the expected behavior.

**Pseudocode:**
```
FOR ALL input WHERE isBugCondition(input) DO
  result := startApplication_fixed(input)
  ASSERT result.started = true
  ASSERT result.beanCount(EmbeddingModel.class) = 1
  ASSERT result.getBean(EmbeddingModel.class).name = "ollamaEmbeddingModel"
END FOR
```

### Preservation Checking

**Goal**: Verify that for all inputs where the bug condition does NOT hold, the fixed function produces the same result as the original function.

**Pseudocode:**
```
FOR ALL input WHERE NOT isBugCondition(input) DO
  ASSERT startApplication_original(input).chatModelBehavior = startApplication_fixed(input).chatModelBehavior
  ASSERT startApplication_original(input).embeddingModelBehavior = startApplication_fixed(input).embeddingModelBehavior
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It generates many test cases automatically across the input domain
- It catches edge cases that manual unit tests might miss
- It provides strong guarantees that behavior is unchanged for all non-buggy inputs

**Test Plan**: Observe behavior on UNFIXED code first for chat operations and embedding operations, then write property-based tests capturing that behavior.

**Test Cases**:
1. **Chat Model Preservation**: Verify OpenAI ChatModel continues to work for Groq Cloud API calls
   - Observe: Chat operations work correctly on unfixed code (when application can start)
   - Test: After fix, chat operations produce identical results
2. **Embedding Model Preservation**: Verify Ollama EmbeddingModel continues to work with same configuration
   - Observe: Embedding operations work correctly on unfixed code (when application can start)
   - Test: After fix, embedding operations produce identical results
3. **Vector Store Preservation**: Verify PgVectorStoreAutoConfiguration initializes correctly with Ollama embeddings
   - Observe: Vector store initialization works on unfixed code (when application can start)
   - Test: After fix, vector store initialization produces identical results
4. **AI Service Preservation**: Verify AiChatService functionality remains unchanged
   - Observe: Chat service with function calling and memory works on unfixed code
   - Test: After fix, chat service produces identical responses for same inputs

### Unit Tests

- Test application startup succeeds with exclusion in place
- Test exactly one EmbeddingModel bean exists in application context after startup
- Test the EmbeddingModel bean is from Ollama (ollamaEmbeddingModel)
- Test OpenAI ChatModel bean (openAiChatModel) exists and is functional
- Test PgVectorStoreAutoConfiguration successfully autowires the Ollama embedding bean

### Property-Based Tests

- Generate random chat messages and verify AiChatService produces consistent responses before and after fix
- Generate random embedding inputs and verify Ollama EmbeddingModel produces consistent embeddings before and after fix
- Test that vector store operations work correctly across many scenarios with the single embedding bean

### Integration Tests

- Test full application startup with both starters and exclusion configured
- Test AI chat flow with function calling (ride booking) works end-to-end
- Test vector store initialization and query operations work correctly
- Test that removing the exclusion causes the original bug to reappear (regression test)
