package com.MC_656.mobility.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username; // Typically the login identifier, e.g., email
    private String email;    // Contact email
    private String name;
    private String socialName;
    private String phoneNumber;
    private String cpf;
    // private List<String> roles; // Add roles if implementing role-based access

    public JwtResponse(String accessToken, Long id, String username, String email, String name, String socialName, String phoneNumber, String cpf) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.socialName = socialName;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;
        // this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    // public List<String> getRoles() {
    //     return roles;
    // }
}
