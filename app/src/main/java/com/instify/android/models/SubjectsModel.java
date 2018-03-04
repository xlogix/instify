package com.instify.android.models;

/**
 * Created by krsnv on 01-May-17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubjectsModel {
    @SerializedName("CODE")
    @Expose
    private String cODE;

    @SerializedName("NAME")
    @Expose
    private String nAME;

    @SerializedName("MARKS")
    @Expose
    private String mARKS;

    public SubjectsModel(String cODE, String nAME, String ARKS) {
        this.cODE = cODE;
        this.nAME = nAME;
        mARKS = ARKS;
    }

    public String getCODE() {
        return cODE;
    }

    public void setCODE(String cODE) {
        this.cODE = cODE;
    }

    public String getNAME() {
        return nAME;
    }

    public void setNAME(String nAME) {
        this.nAME = nAME;
    }

    public String getMARKS() {
        return mARKS;
    }

    public void setMARKS(String mARKS) {
        this.mARKS = mARKS;
    }
}
