package edu.upc.dsa.orm.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectHelper {
    public static String[] getFields(Object entity) {

        Class theClass = entity.getClass();

        Field[] fields = theClass.getDeclaredFields();

        String[] sFields = new String[fields.length];
        int i=0;

        for (Field f : fields) {
            f.setAccessible(true);
            sFields[i++] = f.getName();
        }

        return sFields;

    }


    public static void setter(Object object, String property, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(property);
            field.setAccessible(true);
            if (field.getType().isPrimitive() && field.getType().equals(int.class)) {
                if (value == null) {
                    field.setInt(object, 0);
                } else {
                    field.setInt(object, (Integer) value);
                }
            } else {
                field.set(object, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getter(Object object, String property) {
        try {
            Field field = object.getClass().getDeclaredField(property);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}

