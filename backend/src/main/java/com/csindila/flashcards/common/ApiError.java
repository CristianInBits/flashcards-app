package com.csindila.flashcards.common;

import java.util.List;
import java.time.OffsetDateTime;

public record ApiError(
                String message,
                int status,
                String path,
                OffsetDateTime timestamp,
                List<String> details) {
}
