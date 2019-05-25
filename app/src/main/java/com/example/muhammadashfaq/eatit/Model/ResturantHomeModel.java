package com.example.muhammadashfaq.eatit.Model;

public class ResturantHomeModel {

    String name,latitude,longitude,image,status;


    public ResturantHomeModel() {

    }

    public ResturantHomeModel(String name, String latitude, String longitude, String image, String status) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
