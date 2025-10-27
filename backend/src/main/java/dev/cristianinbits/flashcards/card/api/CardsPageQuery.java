package dev.cristianinbits.flashcards.card.api;

import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;

import java.util.Set;

/**
 * Parámetros de consulta para listar y buscar tarjetas con paginación,
 * ordenación y filtros.
 *
 * Este record encapsula número y tamaño de página, criterio de orden, y filtros
 * opcionales por texto y etiqueta. Proporciona utilidades para obtener valores
 * por defecto, normalizar entradas y construir un {@link Pageable}.
 *
 * @param page número de página (base 0); si es nulo se usará 0
 * @param size tamaño de página; si es nulo se usará 20
 * @param sort criterio de orden en el formato "campo,asc|desc"; por defecto
 *             "createdAt,desc"
 * @param q    filtro de texto parcial para buscar en el contenido (opcional)
 * @param tag  filtro por etiqueta (opcional)
 */
public record CardsPageQuery(
        @PositiveOrZero Integer page,
        @Min(1) @Max(100) Integer size,
        @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_]*(,(?i)(asc|desc))?$", message = "Formato de orden inválido. Usa 'campo,asc' o 'campo,desc'") String sort,
        @Size(max = 200) String q,
        @Size(max = 200) String tag) {

    /**
     * Devuelve el número de página o el valor por defecto si no se especifica.
     *
     * @return número de página, por defecto 0
     */
    public int pageOrDefault() {
        return page == null ? 0 : page;
    }

    /**
     * Devuelve el tamaño de página o el valor por defecto si no se especifica.
     *
     * @return tamaño de página, por defecto 20
     */
    public int sizeOrDefault() {
        return size == null ? 20 : size;
    }

    /**
     * Devuelve el criterio de ordenación o el valor por defecto si no se
     * especifica.
     *
     * @return cadena "campo,asc|desc"; por defecto "createdAt,desc"
     */
    public String sortOrDefault() {
        return (sort == null || sort.isBlank()) ? "createdAt,desc" : sort.trim();
    }

    /**
     * Devuelve el filtro de texto normalizado o {@code null} si está vacío.
     *
     * @return texto normalizado o {@code null}
     */
    public String qOrNull() {
        return normOrNull(q);
    }

    /**
     * Devuelve el filtro de etiqueta normalizado o {@code null} si está vacío.
     *
     * @return etiqueta normalizada o {@code null}
     */
    public String tagOrNull() {
        return normOrNull(tag);
    }

    /**
     * Construye un {@link Pageable} a partir de los parámetros de la consulta y
     * valida el campo de ordenación.
     *
     * @param allowedSortProps conjunto de nombres de propiedades permitidas para
     *                         ordenar
     * @return objeto {@link Pageable} configurado con página, tamaño y orden
     * @throws IllegalArgumentException si la propiedad de orden no está permitida
     */
    public Pageable toPageable(Set<String> allowedSortProps) {
        String[] parts = sortOrDefault().split(",", 2);
        String property = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "asc";

        if (!allowedSortProps.contains(property)) {
            throw new IllegalArgumentException("Campo de orden inválido: " + property);
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(pageOrDefault(), sizeOrDefault(), Sort.by(new Sort.Order(direction, property)));
    }

    /**
     * Normaliza una cadena recortando espacios; devuelve {@code null} si queda
     * vacía o si es nula.
     *
     * @param s cadena de entrada
     * @return cadena normalizada o {@code null} si es nula o vacía
     */
    private static String normOrNull(String s) {
        if (s == null)
            return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
