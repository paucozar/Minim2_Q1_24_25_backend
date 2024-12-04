package edu.upc.dsa.orm.util;

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

        StringBuffer sb = new StringBuffer("UPDATE ");
        sb.append(entity.getClass().getSimpleName()).append(" ");
        sb.append("SET ");

        String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

        for (String field: fields) {
            if (!field.equals("id")) sb.append(field).append("=?, ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append(" WHERE USERNAME=?");

        return sb.toString();
    }
}
