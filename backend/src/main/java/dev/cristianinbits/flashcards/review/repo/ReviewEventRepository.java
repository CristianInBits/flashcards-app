package dev.cristianinbits.flashcards.review.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.cristianinbits.flashcards.review.domain.ReviewEvent;

/**
 * Repositorio JPA para la entidad {@link ReviewEvent}.
 *
 * Gestiona el acceso a los eventos históricos de revisión registrados
 * por tarjeta o por sesión. Además de las operaciones CRUD estándar,
 * ofrece consultas paginadas específicas según el contexto.
 */
public interface ReviewEventRepository extends JpaRepository<ReviewEvent, UUID> {

    /**
     * Obtiene los eventos de revisión asociados a una tarjeta concreta.
     *
     * @param cardId identificador de la tarjeta
     * @param pageable configuración de paginación y ordenación
     * @return página de eventos correspondientes a la tarjeta indicada
     */
    Page<ReviewEvent> findByCard_Id(UUID cardId, Pageable pageable);

    /**
     * Obtiene los eventos de revisión asociados a una sesión concreta.
     *
     * @param reviewId identificador de la sesión de repaso
     * @param pageable configuración de paginación y ordenación
     * @return página de eventos pertenecientes a la sesión indicada
     */
    Page<ReviewEvent> findByReview_Id(UUID reviewId, Pageable pageable);
}
