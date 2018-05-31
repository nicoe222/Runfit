package com.example.nicosetiawan.runfit.Models;

public class UserAccountSettings {

    private String birthdate;
    private String fullname;
    private String gender;
    private long height;
    private String profile_photo;
    private long weight;


    public UserAccountSettings(String birthdate, String fullname, String gender, long height, String profile_photo, long weight) {
        this.birthdate = birthdate;
        this.fullname = fullname;
        this.gender = gender;
        this.height = height;
        this.profile_photo = profile_photo;
        this.weight = weight;
    }

    public UserAccountSettings(){

    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "birthdate='" + birthdate + '\'' +
                ", fullname='" + fullname + '\'' +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", profile_photo='" + profile_photo + '\'' +
                ", weight=" + weight +
                '}';
    }
}
