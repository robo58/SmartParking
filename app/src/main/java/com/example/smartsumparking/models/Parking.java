package com.example.smartsumparking.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.example.smartsumparking.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class Parking {

    private int id;
    private String name;
    private String address;
    private LatLng location;
    private int capacity;
    private int occupied;
    private ParkingSpace[] parkingSpaces;
    private Marker parkingMarker;

    public Parking() {
    }

    public Parking(int id, String name, String address,  LatLng location, int capacity, int occupied, ParkingSpace[] parkingSpaces) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
        this.capacity = capacity;
        this.occupied = occupied;
        this.parkingSpaces = parkingSpaces;
        this.parkingMarker = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    public ParkingSpace[] getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(ParkingSpace[] parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Marker getParkingMarker() {
        return parkingMarker;
    }

    public void setParkingMarker(Marker parkingMarker) {
        this.parkingMarker = parkingMarker;
    }

    public int getIcon(){
        return R.drawable.ic_local_parking_black_24dp;
    }

}
