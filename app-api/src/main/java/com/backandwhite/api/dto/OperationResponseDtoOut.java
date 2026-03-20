package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDtoOut {

    @Schema(example = "code")
    private String code;
    @Schema(example = "message")
    private String message;
    @Schema(example = "details")
    private List<String> details;
    @Schema(example = "dateTime")
    private ZonedDateTime dateTime;
}
