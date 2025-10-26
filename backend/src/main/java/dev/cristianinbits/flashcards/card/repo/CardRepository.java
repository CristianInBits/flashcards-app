package dev.cristianinbits.flashcards.card.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import dev.cristianinbits.flashcards.card.domain.Card;

/**
 * Repositorio JPA para la entidad {@link Card}.
 * 
 * Proporciona operaciones CRUD y consultas personalizadas relacionadas con las
 * tarjetas almacenadas en la base de datos. Además del comportamiento estándar
 * de {@link JpaRepository}, define métodos específicos para buscar tarjetas por
 * mazo y realizar búsquedas filtradas por texto o etiquetas.
 */
public interface CardRepository extends JpaRepository<Card, UUID> {

  /**
   * Obtiene todas las tarjetas asociadas a un mazo específico.
   *
   * @param deckId   identificador del mazo
   * @param pageable configuración de paginación y ordenación
   * @return página de tarjetas pertenecientes al mazo indicado
   */
  Page<Card> findByDeck_Id(UUID deckId, Pageable pageable);

  /**
   * Realiza una búsqueda avanzada de tarjetas dentro de un mazo según criterios
   * opcionales.
   * 
   * Permite filtrar por coincidencias parciales en el anverso o reverso del
   * texto, así como por etiquetas asociadas. Los filtros son insensibles a
   * mayúsculas/minúsculas.
   *
   * @param deckId     identificador del mazo al que pertenecen las tarjetas
   * @param qPattern   patrón de búsqueda para el texto (puede ser nulo)
   * @param tagPattern patrón de búsqueda para las etiquetas (puede ser nulo)
   * @param pageable   configuración de paginación y ordenación
   * @return página de resultados que cumplen los criterios de búsqueda
   */
  @Query("""
      SELECT c FROM Card c
      WHERE c.deck.id = :deckId
        AND (:qPattern IS NULL OR LOWER(c.front) LIKE :qPattern OR LOWER(c.back) LIKE :qPattern)
        AND (:tagPattern IS NULL OR (c.tags IS NOT NULL AND LOWER(c.tags) LIKE :tagPattern))
      """)
  Page<Card> search(
      @Param("deckId") UUID deckId,
      @Param("qPattern") String qPattern,
      @Param("tagPattern") String tagPattern,
      Pageable pageable);
}
