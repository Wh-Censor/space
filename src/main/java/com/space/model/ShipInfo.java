package com.space.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.space.utils.DateAttributeConverter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ship")
@JsonPropertyOrder({"id"," name", "planet", "shipType", "prodDate", "isUsed", "speed", "crewSize", "rating"})
public class ShipInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "planet")
    private String planet;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipType", length = 9)
    private ShipType shipType;

    @Column(name = "prodDate")
    @Convert(converter = DateAttributeConverter.class)
    private Long prodDate;

    @Column(name = "isUsed")
    private Boolean isUsed;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "crewSize")
    private Integer crewSize;

    @Column(name = "rating")
    private Double rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Long getProdDate() {
        return prodDate;
    }

    public void setProdDate(Long prodDate) {
        this.prodDate = prodDate;
        calcRating();
    }

    @JsonProperty("isUsed")
    public Boolean isUsed() {
        return isUsed;
    }

    @JsonProperty("isUsed")
    public void setUsed(Boolean used) {
        this.isUsed = used;
        calcRating();
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
        calcRating();
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "ShipInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", planet='" + planet + '\'' +
                ", shipType=" + shipType +
                ", prodDate=" + prodDate +
                ", isUsed=" + isUsed +
                ", speed=" + speed +
                ", crewSize=" + crewSize +
                ", rating=" + rating +
                '}';
    }

    private void calcRating() {
        if (speed != null && isUsed!= null && prodDate != null) {
            Double num = 80 * speed * (isUsed ? 0.5 : 1.0);
            Double sign = 3019.0 - (LocalDate.ofEpochDay(prodDate / (1000 * 60 * 60 * 24)).getYear()) + 1.0;
            rating = Math.round((num / sign) * 100.0) / 100.0;
        } else {
            rating = null;
        }
    }
}