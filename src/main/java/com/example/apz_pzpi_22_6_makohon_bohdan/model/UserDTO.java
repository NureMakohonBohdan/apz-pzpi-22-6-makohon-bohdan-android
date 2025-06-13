package com.example.apz_pzpi_22_6_makohon_bohdan.model;

public class UserDTO {
    private Integer id;
    private String email;
    private String password;

    // empty constructor for Gson
    public UserDTO() {}

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
