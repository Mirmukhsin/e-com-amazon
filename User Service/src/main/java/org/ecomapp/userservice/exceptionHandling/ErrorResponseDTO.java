package org.ecomapp.userservice.exceptionHandling;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ErrorResponseDTO {
    private String title;
    private int status;
    private String detail;
    private String path;
    private Instant timestamp;
}
