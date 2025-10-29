package dev.cristianinbits.flashcards.review.repo;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.cristianinbits.flashcards.card.domain.Card;
import dev.cristianinbits.flashcards.review.domain.CardSrsState;

/**
 * Repositorio JPA para la entidad {@link CardSrsState}.
 *
 * Proporciona operaciones CRUD y consultas personalizadas sobre los estados SRS
 * de las tarjetas. Permite obtener las tarjetas cuyo repaso está pendiente
 * (es decir, aquellas cuyo campo {@code dueAt} ya ha vencido).
 */
public interface CardSrsStateRepository extends JpaRepository<CardSrsState, UUID> {

        /**
         * Obtiene las tarjetas de un mazo cuyo repaso está pendiente, ordenadas
         * por la fecha de vencimiento más próxima.
         *
         * @param deckId   identificador del mazo
         * @param now      instante de referencia para comparar con {@code dueAt}
         * @param pageable configuración de paginación y ordenación
         * @return         página de tarjetas pendientes de repaso, ordenadas por
         *                 {@code dueAt ASC}
         */
        @Query("""
            SELECT c FROM CardSrsState s
            JOIN s.card c
            WHERE c.deck.id = :deckId
              AND s.dueAt <= :now
            ORDER BY s.dueAt ASC
            """)
        Page<Card> findDueCardsByDeck(
                @Param("deckId") UUID deckId,
                @Param("now") OffsetDateTime now,
                Pageable pageable);
}
