package com.StayHub.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @Email@NotNull@NotEmpty
    private String email;
    @Size(min = 2,message = "Should be at-least 2 character")@NotEmpty@NotNull
    private String firstName;
    @Size(min = 2,message = "Should be at-least 2 character")
    private String lastName;
    @NotEmpty@NotNull
    private String password;
    @NotNull@NotEmpty
    private String mobile;
}
