package com.MC_656.mobility.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Username must be up to 50 characters") // Assuming username is email, aligns with RegisterRequest
    @Email(message = "Username must be a valid email format if it's the email") // Add if username is always email
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$",
             message = "Password must be at least 6 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 50, message = "Email must be up to 50 characters")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Name must contain only letters and spaces")
    private String name;

    @Size(max = 20, message = "Social name must be up to 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]{0,20}$", message = "Social name must contain only letters and spaces")
    @Column(length = 20)
    private String socialName;

    @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?\\d{4,5}-\\d{4}$", message = "Invalid phone number format. Expected (XX) XXXXX-XXXX or (XX) XXXX-XXXX")
    @Column(length = 20) // Max length for (XX) XXXXX-XXXX plus some buffer
    private String phoneNumber;

    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "Invalid CPF format. Expected XXX.XXX.XXX-XX or XXXXXXXXXXX")
    @Column(length = 14) // Max length for XXX.XXX.XXX-XX
    private String cpf;

    // Constructors
    public User() {
    }

    public User(String username, String password, String email, String name, String socialName, String phoneNumber, String cpf) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.socialName = socialName;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", name='" + name + '\'' +
               ", socialName='" + socialName + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", cpf='" + cpf + '\'' +
               '}';
    }
}
