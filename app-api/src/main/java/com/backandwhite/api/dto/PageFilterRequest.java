package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper genérico para peticiones de listado paginado con filtros dinámicos.
 *
 * <p>Uso:</p>
 * <pre>
 *   PageFilterRequest&lt;CategoryFilterDto&gt; request = PageFilterRequest.&lt;CategoryFilterDto&gt;builder()
 *       .page(0).size(20).sortBy("createdAt").ascending(true)
 *       .locale("es")
 *       .filters(new CategoryFilterDto("PUBLISHED", true, null))
 *       .build();
 * </pre>
 *
 * @param <F> tipo del DTO de filtros — cualquier clase con los campos a filtrar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición de listado paginado con filtros dinámicos")
public class PageFilterRequest<F> {

    @Min(0)
    @Builder.Default
    @Schema(description = "Número de página (0-based)", example = "0")
    private int page = 0;

    @Min(1)
    @Max(200)
    @Builder.Default
    @Schema(description = "Tamaño de página (máx 200)", example = "20")
    private int size = 20;

    @Builder.Default
    @Schema(description = "Campo de ordenamiento", example = "createdAt")
    private String sortBy = "createdAt";

    @Builder.Default
    @Schema(description = "Orden ascendente", example = "true")
    private boolean ascending = true;

    @Schema(description = "Código de idioma para filtrado de traducciones (ej: es, en, pt-BR). Null = sin filtro de idioma")
    private String locale;

    @Schema(description = "Filtros dinámicos: cualquier campo no nulo del DTO se usa como predicado de igualdad")
    private F filters;
}
