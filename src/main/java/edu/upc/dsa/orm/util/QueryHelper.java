package edu.upc.dsa.orm.util;

import edu.upc.dsa.annotations.CustomAnnotation;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryHelper {

    public static String createQueryINSERT(Object entity) {

        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(entity.getClass().getSimpleName()).append(" ");
        sb.append("(");

        String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

        sb.append("ID");
        for (String field: fields) {
            if (!field.equals("id")) sb.append(", ").append(field);
        }
        sb.append(") VALUES (?");

        for (String field: fields) {
            if (!field.equals("id"))  sb.append(", ?");
        }
        sb.append(")");
        // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (0, ?, ?, ?,?)
        return sb.toString();
    }

    public static String createQuerySELECT(Class<?> entityClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");
        sb.append(entityClass.getSimpleName()).append(" ");
        if (entityClass.getSimpleName().equals("User")) {
            sb.append("WHERE USERNAME = ?");
        }
        else {
            sb.append("WHERE ID = ?");
        }


        return sb.toString();
    }


    public static String createSelectFindAll(Class theClass) {



        StringBuffer sb = new StringBuffer("SELECT * FROM "+theClass.getSimpleName());



        return sb.toString();
    }

    public static String createQueryUPDATE(Object entity) {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(entity.getClass().getSimpleName()).append(" SET ");

        String[] fields = ObjectHelper.getFields(entity);
        for (String field : fields) {
            try {
                Field declaredField = entity.getClass().getDeclaredField(field);

                CustomAnnotation annotation = declaredField.getAnnotation(CustomAnnotation.class);
                if (annotation != null) {
                    String annotationValue = annotation.value();
                    if (annotationValue == null || !annotationValue.equals("id_exclude")) {
                        sb.append(field).append("=?, ");
                    }
                }
                else {
                    sb.append(field).append("=?, ");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(" WHERE ID = ?");
        return sb.toString();
    }
}