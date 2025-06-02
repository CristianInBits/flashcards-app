package com.CristianInBits.flashcards.bootstrap;

import com.CristianInBits.flashcards.repository.FlashcardRepository;
import com.CristianInBits.flashcards.models.Flashcard;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Cargador inicial de tarjetas (flashcards) desde archivos JSON al iniciar
 * la aplicación.
 * 
 * Esta clase utiliza Jackson y Spring Boot para leer archivos JSON ubicados en
 * `src/main/resources/data/` y precargar la base de datos solo si está vacía.
 * 
 * Es útil durante el desarrollo o despliegue inicial del sistema.
 */
@Component
public class JsonFlashcardLoader {

    private final FlashcardRepository repository;
    private final ObjectMapper mapper;

    /**
     * Inyección del repositorio y del deserializador JSON.
     *
     * @param repository Repositorio de Flashcards.
     * @param mapper     Jackson ObjectMapper configurado por Spring Boot.
     */
    public JsonFlashcardLoader(FlashcardRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Método que se ejecuta automáticamente después de construir el bean.
     * 
     * Verifica si la base de datos está vacía y, en ese caso, carga tarjetas desde
     * archivos JSON (uno por asignatura o colección).
     *
     * @throws Exception si ocurre algún error al leer los archivos.
     */
    @PostConstruct
    public void loadFromJson() throws Exception {
        if (repository.count() == 0) {
            String[] files = {
                    "data/flashcardsBBDD.json", // Flashcards de Bases de Datos
                    "data/flashcardsASO.json", // Flashcards de Ampliación de SO
                    "data/flashcardsPC.json" // Flashcards de Programación Concurrente
            };

            for (String file : files) {
                InputStream input = new ClassPathResource(file).getInputStream();
                List<Flashcard> cards = mapper.readValue(input, new TypeReference<>() {
                });
                repository.saveAll(cards);
                System.out.println("Cargadas desde: " + file + " (" + cards.size() + " tarjetas)");
            }
        } else {
            System.out.println("Ya existen tarjetas en la base de datos, no se cargan desde JSON.");
        }
    }
}
