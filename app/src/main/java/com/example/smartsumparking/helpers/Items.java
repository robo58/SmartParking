package com.example.smartsumparking.helpers;

public class Items {

    private String Titles, Description;
    private String First;
    private int Images;

    public Items(String first, String titles, String description, int images){
        First = first;
        Titles = titles;
        Description = description;
        Images = images;
    }

    String getFirst(){
        return First;
    }
    String getTitles(){
        return Titles;
    }
    String getDescription(){
        return Description;
    }
    int getImages(){
        return Images;
    }

}
