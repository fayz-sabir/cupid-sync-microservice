package com.nuitee.adapterpersistencejpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "hotel_i18n",
    uniqueConstraints = @UniqueConstraint(name = "hotel_lang", columnNames = {"hotel_id", "lang"})
)
public class HotelI18nEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Column(name = "important_info")
    private String importantInfo;

    @Lob
    @Column(name = "markdown_description")
    private String markdownDescription;

    @Lob
    @Column(name = "facilities")
    private String facilities;

    @Lob
    @Column(name = "rooms")
    private String rooms;

    @Column(length = 2, nullable = false)
    private String lang;

    @Column(name = "hotel_type")
    private String hotelType;

    @Lob
    @Column(name = "description_short")
    private String description;

    @Lob
    @Column(name = "policies")
    private String policiesJson;

    @Lob
    @Column(name = "photos")
    private String photosJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HotelEntity getHotel() {
        return hotel;
    }

    public void setHotel(HotelEntity hotel) {
        this.hotel = hotel;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoliciesJson() {
        return policiesJson;
    }

    public void setPoliciesJson(String policiesJson) {
        this.policiesJson = policiesJson;
    }

    public String getHotelType() {
        return hotelType;
    }

    public void setHotelType(String hotelType) {
        this.hotelType = hotelType;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getImportantInfo() {
        return importantInfo;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public void setImportantInfo(String importantInfo) {
        this.importantInfo = importantInfo;
    }

    public String getMarkdownDescription() {
        return markdownDescription;
    }

    public void setMarkdownDescription(String markdownDescription) {
        this.markdownDescription = markdownDescription;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getFacilities() {
        return facilities;
    }

    public String getPhotosJson() {
        return photosJson;
    }

    public void setPhotosJson(String photosJson) {
        this.photosJson = photosJson;
    }
}
