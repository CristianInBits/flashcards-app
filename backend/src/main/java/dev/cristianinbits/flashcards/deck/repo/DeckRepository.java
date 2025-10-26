package dev.cristianinbits.flashcards.deck.repo;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.cristianinbits.flashcards.deck.model.Deck;

/**
 * Repositorio JPA para la entidad Deck.
 * 
 * Proporciona operaciones CRUD y soporte para paginación y ordenación
 * sin necesidad de implementar lógica adicional.
 * 
 * Al extender JpaRepository, hereda métodos como:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * 
 * Los métodos personalizados se pueden añadir en el futuro según las
 * necesidades del dominio (por ejemplo, búsqueda por nombre o por usuario
 * creador).
 */
public interface DeckRepository extends JpaRepository<Deck, UUID> {
}
