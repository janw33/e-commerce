package dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "First name cannot be blank")
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    )
    @Pattern(
            regexp = "^[\\p{L}]+(?:[-'][\\p{L}]+)*$",
            message = "Last name contains invalid characters"
    )
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    )
    @Pattern(
            regexp = "^[\\p{L}]+(?:[-'][\\p{L}]+)*$",
            message = "Last name contains invalid characters"
    )
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Size(
            max = 100,
            message = "Email cannot exceed 100 characters"
    )
    @Email(message = "Email must be valid")
    private String email;

    @Size (
            max = 30,
            message = "Phone number cannot exceed 30 characters"
    )
    private String phoneNumber;
}
