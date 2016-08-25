package com.marnun.popularmoviesapp;

/**
 * Created by Marco on 25/08/2016.
 */
public class Review {

    String author;
    String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return Utility.toString(this);
    }
}
