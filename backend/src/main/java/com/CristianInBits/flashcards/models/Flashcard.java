package com.CristianInBits.flashcards.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Representa una tarjeta de estudio (flashcard) utilizada en el sistema
 * para facilitar el aprendizaje mediante repetición espaciada.
 * 
 * Cada flashcard contiene una pregunta, una respuesta, un tema, y una colección
 * a la que pertenece. También se almacena su nivel de aprendizaje actual (nivel)
 * y la fecha en la que debe revisarse por última vez (nextReviewDate).
 * 
 * Esta entidad está mapeada a una tabla en la base de datos mediante JPA.
 */
@Entity
public class Flashcard {

    /**
     * Identificador único de la flashcard.
     * Se genera automáticamente mediante la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pregunta de la flashcard.
     */
    private String question;

    /**
     * Respuesta de la flashcard.
     * El texto puede tener hasta 500 caracteres.
     */
    @Column(length = 500)
    private String answer;

    /**
     * Tema al que pertenece la flashcard (ej. "Bases de Datos").
     */
    private String topic;

    /**
     * Nombre de la colección de tarjetas (ej. "Examen Final").
     */
    private String collection;

    /**
     * Nivel de aprendizaje (0 = nuevo, 1 = intermedio, 2 = aprendido).
     */
    private int level;

    /**
     * Fecha prevista para la próxima revisión de la tarjeta.
     * El formato JSON para serialización es "dd-MM-yyyy".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate nextReviewDate;

    /**
     * Constructor vacío necesario para JPA.
     */
    public Flashcard() {
    }

    /**
     * Constructor completo para crear una flashcard con valores iniciales.
     * 
     * @param id        Identificador único.
     * @param question  Pregunta de la tarjeta.
     * @param answer    Respuesta de la tarjeta.
     * @param topic     Tema asociado.
     * @param collection Colección a la que pertenece.
     * @param level     Nivel de aprendizaje.
     */
    public Flashcard(Long id, String question, String answer, String topic, String collection, int level) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.topic = topic;
        this.collection = collection;
        this.level = level;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(LocalDate nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }
}
