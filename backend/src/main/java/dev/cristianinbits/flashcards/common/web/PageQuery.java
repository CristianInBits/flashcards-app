package dev.cristianinbits.flashcards.common.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Representa los parámetros de consulta de paginación y ordenación utilizados en endpoints REST.
 * 
 * Este record encapsula la información necesaria para construir un objeto {@link Pageable},
 * incluyendo número de página, tamaño de página y criterio de ordenación.
 * 
 * También proporciona valores por defecto y validaciones para garantizar que las
 * solicitudes cumplan con las restricciones esperadas.
 */
public record PageQuery(

        @Min(value = 0, message = "El número de página no puede ser negativo")
        Integer page,

        @Min(value = 1, message = "El tamaño mínimo es 1")
        @Max(value = 100, message = "El tamaño máximo es 100")
        Integer size,

        @Pattern(
            regexp = "^[A-Za-z_][A-Za-z0-9_]*(,(?i)(asc|desc))?$",
            message = "Formato de orden inválido. Usa 'campo,asc' o 'campo,desc'"
        )
        String sort
) {

    /**
     * Devuelve el número de página solicitado o un valor por defecto si no se especifica.
     *
     * @return número de página o 0 si no se proporcionó ninguno
     */
    public int pageOrDefault() {
        return page == null ? 0 : page;
    }

    /**
     * Devuelve el tamaño de página solicitado o un valor por defecto si no se especifica.
     *
     * @return tamaño de página o 20 si no se proporcionó ninguno
     */
    public int sizeOrDefault() {
        return size == null ? 20 : size;
    }

    /**
     * Devuelve el criterio de ordenación solicitado o uno por defecto si no se especifica.
     *
     * @return cadena con el campo y la dirección de ordenación (por defecto: "createdAt,desc")
     */
    public String sortOrDefault() {
        return (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;
    }

    /**
     * Convierte los parámetros de paginación y ordenación en un objeto {@link Pageable}.
     * 
     * Verifica que el campo de ordenación esté permitido antes de crear la instancia.
     *
     * @param allowedSortProps conjunto de nombres de propiedades que pueden usarse para ordenar
     * @return objeto Pageable correspondiente a la consulta
     * @throws IllegalArgumentException si el campo de ordenación no está permitido
     */
    public Pageable toPageable(Set<String> allowedSortProps) {
        String[] parts = sortOrDefault().split(",", 2);
        String property = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";

        if (!allowedSortProps.contains(property)) {
            throw new IllegalArgumentException("Campo de orden inválido: " + property);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(dir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(pageOrDefault(), sizeOrDefault(), Sort.by(new Sort.Order(direction, property)));
    }
}
