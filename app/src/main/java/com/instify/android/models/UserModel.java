package com.instify.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by krsnv on 21-Apr-17.
 */

public class UserModel {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("regno")
    @Expose
    private String regno;

    @SerializedName("course")
    @Expose
    private String course;

    @SerializedName("dept")
    @Expose
    private String dept;

    @SerializedName("studentid")
    @Expose
    private String studentid;

    @SerializedName("folio_no")
    @Expose
    private String folioNo;

    @SerializedName("semester")
    @Expose
    private Integer semester;

    @SerializedName("year")
    @Expose
    private Integer year;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("pincode")
    @Expose
    private String pincode;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("error_msg")
    @Expose
    private String errorMsg;

    @SerializedName("error")
    @Expose
    private Boolean error;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getFolioNo() {
        return folioNo;
    }

    public void setFolioNo(String folioNo) {
        this.folioNo = folioNo;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

}
