package com.example.smartsumparking.models;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class ParkingSpace {

    private int id;
    private int idParking;
    private boolean occupied;
    private LatLng location;
    private String name;
    private Marker parkingSpaceMarker;

    public ParkingSpace(int id, int idParking, boolean occupied, LatLng location, String name) {
        this.id = id;
        this.idParking = idParking;
        this.occupied = occupied;
        this.location = location;
        this.name = name;
        this.parkingSpaceMarker = null;
    }

    public int getId() {
        return id;
    }

    public int getIdParking() {
        return idParking;
    }

    public void setIdParking(int idParking) {
        this.idParking = idParking;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Marker getParkingSpaceMarker() {
        return parkingSpaceMarker;
    }

    public void setParkingSpaceMarker(Marker parkingSpaceMarker) {
        this.parkingSpaceMarker = parkingSpaceMarker;
    }

    public BitmapDescriptor getIcon(){
        return this.occupied ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    }
}
