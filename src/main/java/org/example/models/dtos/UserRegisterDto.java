package org.example.models.dtos;

public class UserRegisterDto {
    private String fullName;
    private String email;
    private String password;
    private String userType; // "cliente" o "proveedor"
    // Opcional: Si el proveedor puede registrar una compañía al mismo tiempo
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyWebSite;


    public UserRegisterDto() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public String getCompanyWebSite() { return companyWebSite; }
    public void setCompanyWebSite(String companyWebSite) { this.companyWebSite = companyWebSite; }
}