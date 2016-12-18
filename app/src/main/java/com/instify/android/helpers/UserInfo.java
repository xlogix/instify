package com.instify.android.helpers;

import java.util.Calendar;

/**
 * Created by Arjun Mahishi on 18-Dec-16.
 */

public class UserInfo {

    public String regno, section, dept;
    public int year;
    boolean cr;

    public UserInfo(String regno, String section){
        this.regno = regno.toLowerCase();
        this.section = section.toLowerCase();
        this.year = getYear();
        this.dept = getDept();
        this.cr = false;
    }

    public String getRegno(){
        return this.regno;
    }

    public String getSection(){
        return this.section;
    }

    public String getDept(){
        switch (this.regno.charAt(8)){
            case '1' : return "civil";
            case '2' : return "mech";
            case '3' : return "cse";
            case '4' : return "ece";
            case '8' : return "it";
            default: return null;
        }
    }

    public int getYear(){
        Calendar c = Calendar.getInstance();
        int userYear = c.get(Calendar.YEAR) - (2000 + (getDigit(this.regno.charAt(2))*10 + getDigit(this.regno.charAt(3))));
        if(c.get(Calendar.MONTH) +1 >= 7){
            return userYear + 1;
        }
        return userYear;
    }

    private int getDigit(char c){
        switch(c){
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case '0': return 0;
            default: return -1;
        }
    }

}
