package com.hack36.Models;

import java.util.List;

public class BigFiveTraits {
    String traitID;
    String name;
    String category;
    boolean significant;
    float percentile;
    List<Trait> children;

    public String getTraitID() {
        return traitID;
    }

    public void setTraitID(String traitID) {
        this.traitID = traitID;
    }

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

    public List<Trait> getChildren() {
        return children;
    }

    public void setChildren(List<Trait> children) {
        this.children = children;
    }
}
