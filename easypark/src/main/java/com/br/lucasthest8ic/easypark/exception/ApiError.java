package com.br.lucasthest8ic.easypark.exception;

import java.time.LocalDateTime;

public record ApiError(int status,
                       String message,
                       LocalDateTime timestamp) {
}
