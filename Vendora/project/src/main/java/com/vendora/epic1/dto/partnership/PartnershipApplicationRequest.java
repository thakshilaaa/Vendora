package com.vendora.epic1.dto.partnership;

public class PartnershipApplicationRequest {

    private String applicantType; // "SUPPLIER" or "DELIVERY"
    private String fullName;
    private String email;
    private String phone;
    private String nic;

    // Address
    private String addressLine1;
    private String province;
    private String district;
    private String city;
    private String postalCode;

    // Supplier-only fields (nullable for delivery)
    private String businessName;
    private String businessRegNumber;
    private String tinNumber;
    private String productCategory;
    private String productDetails;

    public String getApplicantType() { return applicantType; }
    public void setApplicantType(String applicantType) { this.applicantType = applicantType; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessRegNumber() { return businessRegNumber; }
    public void setBusinessRegNumber(String businessRegNumber) { this.businessRegNumber = businessRegNumber; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public String getProductDetails() { return productDetails; }
    public void setProductDetails(String productDetails) { this.productDetails = productDetails; }
}
