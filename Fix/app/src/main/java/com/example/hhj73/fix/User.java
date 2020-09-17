package com.example.hhj73.fix;

/**
 * Created by hhj73 on 2018-04-17.
 */

public class User {
    boolean type; // true: 노인, false: 학생
    String id;
    String pw;
    String name;
    String bday;
    boolean gender; // true: 여성, false: 남성
    String phone;
    String address;
    String cost;
    boolean smoking, curfew, pet, help;
    String unique;
    String location;
    String profileMsg;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(boolean type, String id, String pw, String name, String bday, boolean gender,
                String phone, String address, String cost, boolean smoking, boolean curfew,
                boolean pet, boolean help, String unique, String location, String profileMsg) {

        this.type = type;
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.bday = bday;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.cost = cost;
        this.smoking = smoking;
        this.curfew = curfew;
        this.pet = pet;
        this.help = help;
        this.unique = unique;
        this.location = location;
        this.profileMsg = profileMsg;
    }

    public boolean getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getPw() {
        return this.pw;
    }

    public String getName() {
        return this.name;
    }

    public String getBday() {
        return this.bday;
    }

    public boolean getGender() {
        return this.gender;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCost() {
        return this.cost;
    }

    public boolean getSmoking() {
        return this.smoking;
    }

    public boolean getCurfew() {
        return this.curfew;
    }

    public boolean getPet() {
        return this.pet;
    }

    public boolean getHelp() {
        return this.help;
    }

    public String getUnique() {
        return this.unique;
    }

    public  String getLocation() {return  this.location;}

    public  String getProfileMsg() {return  this.profileMsg;}

    // set

    public void setType(boolean type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPw(String Pw) {
        this.pw = pw;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    public void setPet(boolean pet) {
        this.pet = pet;
    }

    public void setCurfew(boolean curfew) {
        this.curfew = curfew;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public void setLocation(String location){this.location = location;}

    public void setProfileMsg(String profileMsg){this.profileMsg = profileMsg;}

}
