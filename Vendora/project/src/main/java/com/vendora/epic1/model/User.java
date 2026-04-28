package com.vendora.epic1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.vendora.epic1.model.enums.District;
import com.vendora.epic1.model.enums.Province;
import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.model.enums.VerificationMethod;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_code", unique = true)
    private String userCode;

    @NotBlank
    @Size(max = 150)
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "first_name")
    private String firstNameLegacy;

    @Column(name = "last_name")
    private String lastNameLegacy;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 15)
    private String phone;

    @Column(name = "phone_number")
    private String phoneNumberLegacy;

    @Size(max = 12)
    private String nic;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    private String city;

    @Enumerated(EnumType.STRING)
    private District district;

    @Enumerated(EnumType.STRING)
    private Province province;

    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "password")
    private String passwordLegacy;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_method")
    private VerificationMethod verificationMethod = VerificationMethod.EMAIL;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private LocalDateTime accountLockedUntil;

    // ================= CONSTRUCTORS =================

    public User() {}

    public User(String fullName, String email, String phone, String passwordHash, RoleType role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.emailVerified = false;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFirstNameLegacy() { return firstNameLegacy; }
    public void setFirstNameLegacy(String firstNameLegacy) { this.firstNameLegacy = firstNameLegacy; }
    public String getLastNameLegacy() { return lastNameLegacy; }
    public void setLastNameLegacy(String lastNameLegacy) { this.lastNameLegacy = lastNameLegacy; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneNumberLegacy() { return phoneNumberLegacy; }
    public void setPhoneNumberLegacy(String phoneNumberLegacy) { this.phoneNumberLegacy = phoneNumberLegacy; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }

    public Province getProvince() { return province; }
    public void setProvince(Province province) { this.province = province; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPasswordLegacy() { return passwordLegacy; }
    public void setPasswordLegacy(String passwordLegacy) { this.passwordLegacy = passwordLegacy; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public VerificationMethod getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(VerificationMethod verificationMethod) { this.verificationMethod = verificationMethod; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (userCode == null || userCode.isBlank()) {
            userCode = "U" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 7).toUpperCase();
        }
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (emailVerified == null) emailVerified = false;
        if (status == null) status = UserStatus.PENDING_VERIFICATION;
        if (verificationMethod == null) verificationMethod = VerificationMethod.EMAIL;
        if (failedLoginAttempts == null) failedLoginAttempts = 0;
        if (addressLine1 == null || addressLine1.isBlank()) {
            addressLine1 = "N/A";
        }
        if (city == null || city.isBlank()) {
            city = "Colombo";
        }
        if (district == null) {
            district = District.COLOMBO;
        }
        if (province == null) {
            province = Province.WESTERN;
        }
        if (nic == null || nic.isBlank()) {
            // Must be unique per row (DB often has UNIQUE on nic) — never derive only from email
            // (shared inbox = multiple users with same email would collide).
            // 12 digits matches typical validation / SQL CHECK for numeric NIC-style values.
            long n12 = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
            nic = String.format("%012d", n12);
        }
        if ((firstNameLegacy == null || firstNameLegacy.isBlank()) && fullName != null) {
            String[] parts = fullName.trim().split("\\s+", 2);
            firstNameLegacy = parts[0];
            lastNameLegacy = parts.length > 1 ? parts[1] : parts[0];
        }
        if ((phoneNumberLegacy == null || phoneNumberLegacy.isBlank()) && phone != null) {
            phoneNumberLegacy = phone;
        }
        if ((passwordLegacy == null || passwordLegacy.isBlank()) && passwordHash != null) {
            passwordLegacy = passwordHash;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ================= COMPATIBILITY METHODS =================

    public String getFirstName() {
        if (this.fullName == null) return null;
        return this.fullName.split(" ")[0];
    }

    public void setFirstName(String firstName) {
        String lastName = getLastName() != null ? getLastName() : "";
        this.fullName = firstName + " " + lastName;
    }

    public String getLastName() {
        if (this.fullName == null || !this.fullName.contains(" ")) return "";
        return this.fullName.substring(this.fullName.indexOf(" ") + 1);
    }

    public void setLastName(String lastName) {
        String firstName = getFirstName() != null ? getFirstName() : "";
        this.fullName = firstName + " " + lastName;
    }

    public String getPhoneNumber() {
        return this.phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public Boolean getIsEmailVerified() {
        return this.emailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.emailVerified = isEmailVerified;
    }
}