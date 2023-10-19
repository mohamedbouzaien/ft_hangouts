package com.mbouzaie.ft_hangouts;

public class Contact {
    private int id;
    private String fullName;
    private String phone;
    private String email;
    private String street;
    private String postalCode;


    public Contact(String fullName, String phone, String email, String street, String postalCode) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.postalCode = postalCode;
    }
    public Contact(int id, String fullName, String phone, String email, String street, String postalCode) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
