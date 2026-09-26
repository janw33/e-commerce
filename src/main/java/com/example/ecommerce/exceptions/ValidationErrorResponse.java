package exceptions;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> validationErrors;
    private String path;
}
