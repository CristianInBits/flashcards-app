package com.CristianInBits.flashcards.services;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.repository.FlashcardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión de tarjetas de estudio (flashcards).
 * 
 * Encapsula la lógica de acceso a datos y operaciones relacionadas con
 * el aprendizaje espaciado, actualización de progreso, y recuperación de
 * tarjetas.
 */
@Service
public class FlashcardService {

    private final FlashcardRepository repository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param repository Repositorio de flashcards.
     */
    public FlashcardService(FlashcardRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera una flashcard por su ID.
     * 
     * @param id Identificador de la flashcard.
     * @return Flashcard encontrada.
     * @throws NoSuchElementException si no existe.
     */
    public Flashcard getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Flashcard con id " + id + " no encontrada"));
    }

    /**
     * Devuelve todas las flashcards existentes.
     * 
     * @return Lista completa de flashcards.
     */
    public List<Flashcard> getAll() {
        return repository.findAll();
    }

    /**
     * Devuelve las flashcards asociadas a un tema específico.
     * 
     * @param topic Tema a consultar.
     * @return Lista de tarjetas con dicho tema.
     */
    public List<Flashcard> getByTopic(String topic) {
        return repository.findByTopic(topic);
    }

    /**
     * Devuelve las flashcards de una colección concreta.
     * 
     * @param collection Nombre de la colección.
     * @return Lista de tarjetas de esa colección.
     */
    public List<Flashcard> getByCollection(String collection) {
        return repository.findByCollection(collection);
    }

    /**
     * Devuelve flashcards filtradas por colección y tema.
     * 
     * @param collection Nombre de la colección.
     * @param topic      Tema específico.
     * @return Lista filtrada.
     */
    public List<Flashcard> getByCollectionAndTopic(String collection, String topic) {
        return repository.findByCollectionAndTopic(collection, topic);
    }

    /**
     * Guarda una nueva flashcard o actualiza una existente.
     * 
     * @param flashcard Flashcard a guardar.
     * @return Flashcard persistida.
     */
    public Flashcard save(Flashcard flashcard) {
        return repository.save(flashcard);
    }

    /**
     * Elimina una flashcard si existe.
     * 
     * @param id ID de la flashcard.
     * @return Flashcard eliminada (opcional).
     */
    public Optional<Flashcard> delete(Long id) {
        return repository.findById(id).map(card -> {
            repository.deleteById(id);
            return card;
        });
    }

    /**
     * Actualiza el progreso de aprendizaje de una tarjeta específica según
     * si el usuario la recordó correctamente o no.
     * 
     * Usa un algoritmo de repetición espaciada con intervalos en días:
     * [0, 1, 3, 7, 30].
     * 
     * @param id         ID de la flashcard.
     * @param remembered `true` si se recordó correctamente, `false` si se olvidó.
     * @return Flashcard actualizada y persistida.
     */
    public Flashcard updateLearningProgress(Long id, boolean remembered) {
        Flashcard card = repository.findById(id).orElseThrow();
        int[] intervals = { 0, 1, 3, 7, 30 };

        if (remembered) {
            // Aumenta el nivel (máximo al último índice)
            card.setLevel(Math.min(card.getLevel() + 1, intervals.length - 1));
        } else {
            // Reinicia el nivel de aprendizaje
            card.setLevel(0);
        }

        // Calcula la próxima fecha de repaso
        LocalDate nextReview = LocalDate.now().plusDays(intervals[card.getLevel()]);
        card.setNextReviewDate(nextReview);

        return repository.save(card);
    }

    /**
     * Recupera todas las tarjetas de una colección que están listas para repasar,
     * es decir, aquellas cuya fecha de revisión es hoy o anterior.
     * 
     * @param collection Nombre de la colección.
     * @return Lista de tarjetas listas para revisión.
     */
    public List<Flashcard> getDueCardsByCollection(String collection) {
        return repository.findByCollectionAndNextReviewDateLessThanEqual(collection, LocalDate.now());
    }
}
