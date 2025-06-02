package com.CristianInBits.flashcards.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CristianInBits.flashcards.models.Flashcard;

/**
 * Repositorio JPA para la entidad {@link Flashcard}.
 * 
 * Proporciona métodos personalizados para consultar tarjetas de estudio
 * por tema, colección, combinación de ambos, y revisión pendiente.
 * 
 * Spring Data JPA generará automáticamente las implementaciones basadas en
 * los nombres de los métodos declarados.
 */
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    /**
     * Devuelve todas las flashcards que pertenecen a un tema específico.
     * 
     * @param topic Tema por el que se desea filtrar (ej. "memoria comaprtida").
     * @return Lista de flashcards del tema indicado.
     */
    List<Flashcard> findByTopic(String topic);

    /**
     * Devuelve todas las flashcards de una colección concreta.
     * 
     * @param collection Nombre de la colección (ej. "programacion-concurrente").
     * @return Lista de flashcards dentro de la colección.
     */
    List<Flashcard> findByCollection(String collection);

    /**
     * Devuelve todas las flashcards de una colección y tema concretos.
     * 
     * @param collection Nombre de la colección.
     * @param topic      Tema específico.
     * @return Lista de flashcards que coinciden con ambos filtros.
     */
    List<Flashcard> findByCollectionAndTopic(String collection, String topic);

    /**
     * Devuelve todas las flashcards de una colección cuya fecha de
     * revisión es anterior o igual a una fecha dada.
     * 
     * Ideal para aplicar lógica de repetición espaciada.
     * 
     * @param collection Nombre de la colección.
     * @param date       Fecha límite para la revisión.
     * @return Lista de flashcards que deben ser revisadas.
     */
    List<Flashcard> findByCollectionAndNextReviewDateLessThanEqual(String collection, LocalDate date);
}
