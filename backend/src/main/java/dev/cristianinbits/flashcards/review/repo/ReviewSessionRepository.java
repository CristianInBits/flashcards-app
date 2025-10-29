package dev.cristianinbits.flashcards.review.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.cristianinbits.flashcards.review.domain.ReviewSession;

/**
 * Repositorio JPA para la entidad {@link ReviewSession}.
 *
 * Proporciona operaciones de persistencia y consultas sobre las sesiones
 * de repaso registradas en el sistema. Permite filtrar las sesiones por mazo
 * para análisis o listados históricos.
 */
public interface ReviewSessionRepository extends JpaRepository<ReviewSession, UUID> {

    /**
     * Obtiene las sesiones de repaso asociadas a un mazo específico.
     *
     * @param deckId identificador del mazo
     * @param pageable configuración de paginación y ordenación
     * @return página de sesiones pertenecientes al mazo indicado
     */
    Page<ReviewSession> findByDeck_Id(UUID deckId, Pageable pageable);
}
