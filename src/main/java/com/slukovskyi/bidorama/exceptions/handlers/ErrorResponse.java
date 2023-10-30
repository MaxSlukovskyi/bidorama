package com.slukovskyi.bidorama.exceptions.handlers;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String code;
    private String status;
    private String message;
    private String details;
    private LocalDateTime time;
}
