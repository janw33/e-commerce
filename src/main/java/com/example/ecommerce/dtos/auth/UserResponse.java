package com.example.ecommerce.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String externalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
