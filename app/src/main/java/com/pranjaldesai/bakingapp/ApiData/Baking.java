package com.pranjaldesai.bakingapp.ApiData;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Baking implements Serializable {

    @SerializedName("[]")
    private ArrayList<BakingResult> bakingResults = new ArrayList<>();

    public ArrayList getResult(){
        return bakingResults;
    }

}
