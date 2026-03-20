package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuestas paginadas estándar.
 * Encapsula datos paginados con información de navegación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDtoOut<T> {

    @Schema(description = "Contenido de la página actual", example = "[]")
    private List<T> content;

    @Schema(description = "Número total de elementos", example = "100")
    private long totalElements;

    @Schema(description = "Número total de páginas", example = "10")
    private int totalPages;

    @Schema(description = "Página actual (0-indexed)", example = "0")
    private int currentPage;

    @Schema(description = "Tamaño de la página", example = "10")
    private int pageSize;

    @Schema(description = "Indica si hay siguiente página", example = "true")
    private boolean hasNext;

    @Schema(description = "Indica si hay página anterior", example = "false")
    private boolean hasPrevious;
}
