package com.MC_656.mobility.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username (email) cannot be blank")
    @Email(message = "Invalid email format for username")
    @Size(max = 50, message = "Username (email) must be up to 50 characters") // Keep max size for DB compatibility if username is email
    private String username; // This is used as the login identifier, typically email

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$",
             message = "Password must be at least 6 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 50, message = "Email must be up to 50 characters")
    @Email(message = "Invalid email format")
    private String email; // User's contact/primary email

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Name must contain only letters and spaces")
    private String name;

    @Size(max = 20, message = "Social name must be up to 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]{0,20}$", message = "Social name must contain only letters and spaces, or be empty")
    private String socialName;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?\\d{4,5}-\\d{4}$", message = "Invalid phone number format. Expected (XX) XXXXX-XXXX or (XX) XXXX-XXXX")
    private String phoneNumber;

    @NotBlank(message = "CPF cannot be blank")
    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "Invalid CPF format. Expected XXX.XXX.XXX-XX or XXXXXXXXXXX")
    private String cpf;


    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
