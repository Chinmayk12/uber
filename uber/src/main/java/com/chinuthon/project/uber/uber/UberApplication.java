package com.chinuthon.project.uber.uber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration.class
})
public class UberApplication {
	public static void main(String[] args) {
		SpringApplication.run(UberApplication.class, args);
	}
}
