package com.nuitee.adapterpersistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel")
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id")
    private long hotelId;

    private String name;
    private long chainId;
    private int stars;
    private double rating;
    private String heroPhoto;

    private double lat;
    private double lng;

    @Column(name = "child_allowed")
    private Boolean childAllowed;
    @Column(name = "pets_allowed")
    private Boolean petsAllowed;

    @Lob
    @Column(name = "contact")
    private String contactJson;

    @Lob
    @Column(name = "checkin_policy")
    private String checkinPolicyJson;

    @Lob
    @Column(name = "address")
    private String addressJson;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getHeroPhoto() {
        return heroPhoto;
    }

    public void setHeroPhoto(String heroPhoto) {
        this.heroPhoto = heroPhoto;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getContactJson() {
        return contactJson;
    }

    public void setContactJson(String contactJson) {
        this.contactJson = contactJson;
    }

    public String getCheckinPolicyJson() {
        return checkinPolicyJson;
    }

    public void setCheckinPolicyJson(String checkinPolicyJson) {
        this.checkinPolicyJson = checkinPolicyJson;
    }

    public String getAddressJson() {
        return addressJson;
    }

    public void setAddressJson(String addressJson) {
        this.addressJson = addressJson;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isChildAllowed() {
        return childAllowed;
    }

    public void setChildAllowed(Boolean childAllowed) {
        this.childAllowed = childAllowed;
    }

    public Boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
}