package com.marnun.popularmoviesapp;

import java.lang.reflect.Field;

/**
 * Created by Marco on 25/08/2016.
 */
public class Utility {

    public static String toString(Object object) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append( object.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);
        //determine fields declared in the class only (no fields of superclass)
        Field[] fields = object.getClass().getDeclaredFields();
        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(object) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");
        return result.toString();
    }

}
