package com.hack36.Models;

public class Trait {
    String traitID;
    String name;
    String category;
    boolean significant;
    float percentile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSignificant() {
        return significant;
    }

    public void setSignificant(boolean significant) {
        this.significant = significant;
    }

    public float getPercentile() {
        return percentile;
    }

    public void setPercentile(float percentile) {
        this.percentile = percentile;
    }

    public String getTraitID() {
        return traitID;
    }

    public void setTraitID(String traitID) {
        this.traitID = traitID;
    }
}
