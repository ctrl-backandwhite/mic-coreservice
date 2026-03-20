package com.backandwhite.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class GlobalSwaggerResponseCustomizer implements OpenApiCustomizer {

    public static final String CONFLICT = "Conflict";
    public static final String FORBIDDEN = "Forbidden";
    public static final String NOT_FOUND = "Not Found";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String CONTENT_TYPE = "application/json";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    @Override
    public void customise(OpenAPI openApi) {

        openApi.getPaths().forEach((pathName, pathItem) -> pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
            ApiResponses apiResponses = operation.getResponses();
            if (Objects.isNull(apiResponses)) {
                apiResponses = new ApiResponses();
                operation.setResponses(apiResponses);
            }

            addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED);
            addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);

            switch (httpMethod) {
                case GET, DELETE:
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_NOT_FOUND, NOT_FOUND);
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN);
                break;
                case POST:
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_BAD_REQUEST, BAD_REQUEST);
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_CONFLICT, CONFLICT);
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN);
                break;
                case PUT, PATCH:
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_BAD_REQUEST, BAD_REQUEST);
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_NOT_FOUND, NOT_FOUND);
                    addApiResponseIfNotPresent(apiResponses, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN);
                break;
                    default:
                break;
            }
        }));
    }

        private void addApiResponseIfNotPresent(ApiResponses apiResponses, int code, String description) {
            if (!apiResponses.containsKey(String.valueOf(code))) {
                ApiResponse apiResponse = new ApiResponse().description(description);

                MediaType mediaType = new MediaType()
                .schema(createInlineSchema());
                Content content = new Content().addMediaType(CONTENT_TYPE, mediaType);

                apiResponse.content(content);
                apiResponses.addApiResponse(String.valueOf(code), apiResponse);
            }
        }

        @SuppressWarnings("unchecked")
        private Schema<?> createInlineSchema() {
            Schema<?> schema = new Schema<>();
            schema.setType("object");

            Map<String, Schema<?>> properties = new LinkedHashMap<>();
            properties.put("code", new StringSchema().example("E001"));
            properties.put("message", new StringSchema().example("Message error description."));

            Schema<String> detailItemSchema = new StringSchema();
                Schema<?> detailsSchema = new Schema<>();
                detailsSchema.setType("array");
                detailsSchema.setItems(detailItemSchema);
                properties.put("details", detailsSchema);

                Schema<String> timestampSchema = new StringSchema();
                    timestampSchema.setFormat("date-time");
                    timestampSchema.setExample("2024-05-01T20:35:10Z");
                    properties.put("timeStamp", timestampSchema);

                    schema.setProperties((Map) properties);
                    return schema;
        }
}