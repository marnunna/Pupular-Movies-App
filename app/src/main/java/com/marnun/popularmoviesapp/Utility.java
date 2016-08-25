package com.marnun.popularmoviesapp;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Marco on 25/08/2016.
 */
public class Utility {

    public static String toString(Object object) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(object.getClass().getName());
        result.append(" Object {");
        result.append(newLine);
        //determine fields declared in the class only (no fields of superclass)
        Field[] fields = object.getClass().getDeclaredFields();
        //print field names paired with their values
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            result.append("  ");
            result.append(field.getName());
            result.append(": ");
            try {
                result.append(field.get(object));
            } catch (IllegalAccessException e) {
                Log.e(object.getClass().getSimpleName(), "to string method exception", e);
            }
            result.append(newLine);
        }
        result.append("}");
        return result.toString();
    }

}
