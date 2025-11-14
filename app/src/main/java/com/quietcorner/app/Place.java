package com.quietcorner.app;

public class Place implements java.io.Serializable {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;
    private String category;
    private boolean wifi;
    private boolean sockets;
    private String cost;
    private double rating;
    private String image;

    private String noiseLevel;

    public Place() {}

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isWifi() { return wifi; }
    public boolean isSockets() { return sockets; }
    public String getCost() { return cost; }
    public double getRating() { return rating; }
    public String getImage() { return image; }

    public String getNoiseLevel() { return noiseLevel; }
}
