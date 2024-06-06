package ru.petrov.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
public class CalculatorApplicationTests {

	@Test
	void contextLoads() {
	}

	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
	public Object getObjectFromFile(String path, Class clazz) {
		try {
			File file = new ClassPathResource(path).getFile();
			return getObjectMapper().readValue(file, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getStringFromFile(String path) {
		try {
			File file = new ClassPathResource(path).getFile();
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
