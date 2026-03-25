package com.backandwhite.api.util;

import com.backandwhite.api.dto.PageFilterRequest;
import com.backandwhite.api.dto.PaginationDtoOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad central de paginación reutilizable en todos los microservicios.
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>{@link #toPageable} — construye un {@link Pageable} desde un {@link PageFilterRequest}
 *       o desde parámetros individuales.</li>
 *   <li>{@link #toResponse} — convierte un {@link Page} de Spring Data al DTO estándar
 *       {@link PaginationDtoOut} con total de registros, páginas, flags hasNext/hasPrevious.</li>
 *   <li>{@link #toFilterMap} — extrae via <strong>reflexión</strong> todos los campos no nulos
 *       de cualquier DTO de filtros, devolviendo un {@code Map<String, Object>} listo para
 *       pasarse a {@code FilterUtils.buildSpecification()}.
 *       <br>Solo incluye campos con valor; ignora nulos, Strings en blanco y campos estáticos.
 *       Soporta herencia (recorre la jerarquía de clases).</li>
 * </ul>
 *
 * <h3>Ejemplo de uso en un controlador:</h3>
 * <pre>{@code
 * // GET paginado simple
 * Pageable pageable = PageableUtils.toPageable(page, size, sortBy, ascending);
 * Page<Category> result = categoryUseCase.findCategoriesPaged(locale, pageable);
 * return ResponseEntity.ok(PageableUtils.toResponse(result.map(mapper::toDto)));
 *
 * // POST /search con filtros dinámicos (reflexión)
 * public ResponseEntity<PaginationDtoOut<CategoryDtoOut>> search(
 *         @RequestBody PageFilterRequest<CategoryFilterDto> request) {
 *     Pageable pageable = PageableUtils.toPageable(request);
 *     Map<String, Object> filters = PageableUtils.toFilterMap(request.getFilters());
 *     // filters contiene solo los campos no nulos del DTO, ej: {status=PUBLISHED, active=true}
 *     Page<Category> result = categoryUseCase.findFiltered(request.getLocale(), filters, pageable);
 *     return ResponseEntity.ok(PageableUtils.toResponse(result.map(mapper::toDto)));
 * }
 * }</pre>
 */
public final class PageableUtils {

    private static final String DEFAULT_SORT_FIELD = "createdAt";

    private PageableUtils() {
    }

    // ── Pageable builders ────────────────────────────────────────────────────

    /**
     * Construye un {@link Pageable} desde un {@link PageFilterRequest}.
     * Si {@code sortBy} está vacío, usa {@code createdAt} como campo por defecto.
     */
    public static Pageable toPageable(PageFilterRequest<?> request) {
        return toPageable(request.getPage(), request.getSize(),
                request.getSortBy(), request.isAscending());
    }

    /**
     * Construye un {@link Pageable} desde parámetros individuales.
     * Útil para mantener compatibilidad con endpoints GET con {@code @RequestParam}.
     */
    public static Pageable toPageable(int page, int size, String sortBy, boolean ascending) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : DEFAULT_SORT_FIELD;
        Sort sort = ascending ? Sort.by(field).ascending() : Sort.by(field).descending();
        return PageRequest.of(page, size, sort);
    }

    // ── Response converter ───────────────────────────────────────────────────

    /**
     * Convierte un {@link Page}{@code <D>} de Spring Data al DTO estándar
     * {@link PaginationDtoOut}{@code <D>} con total de registros, páginas,
     * {@code hasNext} y {@code hasPrevious}.
     *
     * @param page resultado de Spring Data (ya mapeado al DTO de salida)
     * @param <D>  tipo del contenido (DTO de salida)
     */
    public static <D> PaginationDtoOut<D> toResponse(Page<D> page) {
        return PaginationDtoOut.<D>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Convierte una {@link List}{@code <D>} a {@link PaginationDtoOut}{@code <D>}
     * cuando el total ya es conocido (sin objeto Page de Spring).
     */
    public static <D> PaginationDtoOut<D> toResponse(List<D> content, long totalElements,
            int currentPage, int pageSize) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 1;
        return PaginationDtoOut.<D>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNext(currentPage < totalPages - 1)
                .hasPrevious(currentPage > 0)
                .build();
    }

    // ── Reflection-based filter extractor ───────────────────────────────────

    /**
     * Extrae los campos no nulos de cualquier DTO de filtros usando <strong>reflexión</strong>
     * y los convierte a un {@code Map<nombreCampo, valor>}.
     *
     * <p>El mapa resultante puede pasarse directamente a
     * {@code FilterUtils.buildSpecification(entityClass, filterMap)}
     * para construir un {@link org.springframework.data.jpa.domain.Specification} dinámico.</p>
     *
     * <p>Reglas de extracción:</p>
     * <ul>
     *   <li>Ignora campos {@code null}.</li>
     *   <li>Ignora {@link String} en blanco.</li>
     *   <li>Ignora campos {@code static} y {@code serialVersionUID}.</li>
     *   <li>Recorre la jerarquía de herencia — los campos de subclase
     *       tienen prioridad sobre los de superclase.</li>
     * </ul>
     *
     * @param filterDto instancia del DTO con los campos a filtrar (puede ser {@code null})
     * @return mapa inmutable con los campos no nulos; vacío si el DTO es {@code null}
     */
    public static Map<String, Object> toFilterMap(Object filterDto) {
        if (filterDto == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> clazz = filterDto.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if ("serialVersionUID".equals(field.getName())) continue;

                field.setAccessible(true);
                try {
                    Object value = field.get(filterDto);
                    if (value == null) continue;
                    if (value instanceof String s && s.isBlank()) continue;
                    // putIfAbsent: los campos de subclase tienen prioridad
                    map.putIfAbsent(field.getName(), value);
                } catch (IllegalAccessException ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }

        return Collections.unmodifiableMap(map);
    }
}
