package com.CristianInBits.flashcards.controllers;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.services.FlashcardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para gestionar las tarjetas de estudio (flashcards).
 * 
 * Proporciona endpoints para consultar, crear, eliminar y actualizar
 * el progreso de aprendizaje de las flashcards. Está habilitado para
 * peticiones CORS desde cualquier origen.
 */
@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin
public class FlashcardController {

    private final FlashcardService service;

    /**
     * Inyección del servicio de flashcards.
     * 
     * @param service Servicio con lógica de negocio.
     */
    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    /**
     * Endpoint GET para filtrar flashcards por tema y/o colección.
     * 
     * Si no se especifican parámetros, se devuelven todas las flashcards.
     *
     * @param topic      Tema opcional.
     * @param collection Colección opcional.
     * @return Lista de flashcards filtradas o completas.
     */
    @GetMapping("/")
    public List<Flashcard> getFiltered(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String collection) {

        if (topic != null && collection != null) {
            return service.getByCollectionAndTopic(collection, topic);
        } else if (collection != null) {
            return service.getByCollection(collection);
        } else if (topic != null) {
            return service.getByTopic(topic);
        } else {
            return service.getAll();
        }
    }

    /**
     * Endpoint GET para obtener una flashcard por ID.
     * 
     * @param id Identificador de la flashcard.
     * @return Flashcard correspondiente.
     * @throws NoSuchElementException si no se encuentra.
     */
    @GetMapping("/{id}")
    public Flashcard getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Endpoint POST para crear una nueva flashcard.
     * 
     * @param flashcard Objeto recibido en el cuerpo de la petición.
     * @return Flashcard guardada con ID generado.
     */
    @PostMapping("/")
    public Flashcard create(@RequestBody Flashcard flashcard) {
        return service.save(flashcard);
    }

    /**
     * Endpoint POST para actualizar el progreso de aprendizaje.
     * 
     * Este endpoint aplica lógica de repetición espaciada. Si se marcó como
     * recordada, avanza de nivel. Si no, reinicia el nivel.
     *
     * @param id         ID de la flashcard.
     * @param remembered `true` si se recordó, `false` si se olvidó.
     * @return Flashcard actualizada.
     */
    @PostMapping("/{id}/progress")
    public Flashcard updateProgress(@PathVariable Long id, @RequestParam boolean remembered) {
        return service.updateLearningProgress(id, remembered);
    }

    /**
     * Endpoint DELETE para eliminar una flashcard por ID.
     * 
     * @param id ID de la flashcard.
     * @return Flashcard eliminada.
     * @throws NoSuchElementException si no existe.
     */
    @DeleteMapping("/{id}")
    public Flashcard delete(@PathVariable Long id) {
        return service.delete(id)
                .orElseThrow(NoSuchElementException::new);
    }
}
