package com.instify.android.models;

/**
 * Created by krsnv on 01-May-17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestPerformanceResponseModel {

    @SerializedName("Test-Performance")
    @Expose
    private List<TestPerformanceModel> testPerformance = null;

    public List<TestPerformanceModel> getTestPerformance() {
        return testPerformance;
    }

    public void setTestPerformance(List<TestPerformanceModel> testPerformance) {
        this.testPerformance = testPerformance;
    }

}
