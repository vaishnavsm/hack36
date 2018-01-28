package com.hack36.Models;

import java.util.List;

public class Personality {
    int word_count;
    List<BigFiveTraits> personality;
    List<Trait> needs;
    List<Trait> values;
    String warning;

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public List<BigFiveTraits> getPersonality() {
        return personality;
    }

    public void setPersonality(List<BigFiveTraits> personality) {
        this.personality = personality;
    }

    public List<Trait> getNeeds() {
        return needs;
    }

    public void setNeeds(List<Trait> needs) {
        this.needs = needs;
    }

    public List<Trait> getValues() {
        return values;
    }

    public void setValues(List<Trait> values) {
        this.values = values;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
