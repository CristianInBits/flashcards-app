package com.csindila.flashcards.review.repo;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.csindila.flashcards.review.domain.Review;

/**
 * Repository para la entidad {@link Review}, encargado de gestionar
 * el acceso y las consultas a la base de datos relacionadas con las revisiones
 * de las tarjetas (flashcards).
 *
 * Permite obtener las revisiones pendientes de estudio, filtrarlas por mazo
 * y seleccionar revisiones de forma aleatoria.
 */
public interface ReviewRepository extends JpaRepository<Review, UUID> {

  /**
   * Devuelve las revisiones cuya fecha de revisión {@code dueAt} es anterior o
   * igual
   * al momento actual, ordenadas por fecha ascendente.
   *
   * @param now      fecha/hora límite para considerar las revisiones como
   *                 pendientes.
   * @param pageable información de paginación (página, tamaño, etc.).
   * @return página de revisiones pendientes.
   */
  Page<Review> findByDueAtLessThanEqualOrderByDueAtAsc(OffsetDateTime now, Pageable pageable);

  /**
   * Devuelve las revisiones pendientes ({@code dueAt <= now}) filtradas
   * opcionalmente
   * por el identificador del mazo ({@code deckId}).
   *
   * Si {@code deckId} es {@code null}, se devuelven revisiones de todos los
   * mazos.
   *
   * @param now      fecha/hora límite.
   * @param deckId   identificador opcional del mazo (puede ser null).
   * @param pageable información de paginación.
   * @return página de revisiones filtradas.
   */
  @Query("""
        SELECT r FROM Review r
        JOIN r.card c
        WHERE r.dueAt <= :now
          AND (:deckId IS NULL OR c.deck.id = :deckId)
        ORDER BY r.dueAt ASC
      """)
  Page<Review> findDueFiltered(@Param("now") OffsetDateTime now, @Param("deckId") UUID deckId, Pageable pageable);

  /**
   * Devuelve una revisión pendiente aleatoria, filtrada opcionalmente por mazo.
   * Usa una consulta nativa para realizar la selección aleatoria mediante
   * {@code ORDER BY random()}.
   *
   * @param now    fecha/hora límite.
   * @param deckId identificador opcional del mazo.
   * @return una revisión pendiente seleccionada aleatoriamente o {@code null} si
   *         no hay ninguna.
   */
  @Query(value = """
        SELECT r.* FROM reviews r
        JOIN cards c ON c.id = r.card_id
        WHERE r.due_at <= :now
          AND (:deckId IS NULL OR c.deck_id = CAST(:deckId AS uuid))
        ORDER BY random()
        LIMIT 1
      """, nativeQuery = true)
  Review findOneDueRandom(@Param("now") OffsetDateTime now, @Param("deckId") UUID deckId);
}
