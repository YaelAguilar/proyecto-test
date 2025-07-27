package org.example.models.dtos;

public class UserRegisterDto {
    private String fullName;
    private String email;
    private String password;
    private String userType; // "cliente" o "proveedor"
    // MODIFICADO: Ahora los proveedores seleccionan una empresa existente
    private Integer companyId; // ID de la empresa seleccionada (solo para proveedores)

    // Los campos de empresa ya no son necesarios porque no se crea empresa al registrar
    // Eliminados: companyName, companyEmail, companyPhone, companyAddress, companyWebSite

    public UserRegisterDto() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
}