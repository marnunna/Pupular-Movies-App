package com.marnun.popularmoviesapp;

/**
 * Created by Marco on 25/08/2016.
 */
public class Trailer {

    String key;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return Utility.toString(this);
    }

}
