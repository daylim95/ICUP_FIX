package com.example.hhj73.fix;

public class Family {
    String phone_student;
    String phone_senior;
    String name_student;
    String name_senior;
    String id_student;
    String id_senior;

    public Family(){

    }

    public Family(String phoneStudent, String phoneSenior, String nameStudent, String nameSenior, String idStudent, String idSenior){
        this.phone_senior = phoneSenior;
        this.phone_student = phoneStudent;
        this.name_senior = nameSenior;
        this.name_student = nameStudent;
        this.id_senior = idSenior;
        this.id_student = idStudent;
    }

    // get

    public String getPhone_senior() { return phone_senior; }

    public String getPhone_student() { return phone_student; }

    public String getId_senior() { return id_senior; }

    public String getId_student() { return id_student; }

    public String getName_senior() { return name_senior; }

    public String getName_student() { return name_student; }

    // set


    public void setId_senior(String id_senior) {
        this.id_senior = id_senior;
    }

    public void setId_student(String id_student) {
        this.id_student = id_student;
    }

    public void setName_senior(String name_senior) {
        this.name_senior = name_senior;
    }

    public void setName_student(String name_student) {
        this.name_student = name_student;
    }

    public void setPhone_senior(String phone_senior) {
        this.phone_senior = phone_senior;
    }

    public void setPhone_student(String phone_student) {
        this.phone_student = phone_student;
    }
}
