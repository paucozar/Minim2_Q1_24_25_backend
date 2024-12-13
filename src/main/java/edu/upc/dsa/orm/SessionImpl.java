package edu.upc.dsa.orm;

import edu.upc.dsa.annotations.CustomAnnotation;
import edu.upc.dsa.models.User;
import edu.upc.dsa.orm.Session;
import edu.upc.dsa.orm.util.ObjectHelper;
import edu.upc.dsa.orm.util.QueryHelper;
import edu.upc.dsa.util.RandomUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SessionImpl implements Session {
    private final Connection conn;

    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public int save(Object entity) {


        // INSERT INTO Partida () ()
        String insertQuery = QueryHelper.createQueryINSERT(entity);
        // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (0, ?, ?, ?,?)


        PreparedStatement pstm = null;

        try {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connection is not established or is closed.");
            }
            pstm = conn.prepareStatement(insertQuery);
            int i = 1;

            for (String field: ObjectHelper.getFields(entity)) {
                pstm.setObject(i++, ObjectHelper.getter(entity, field));
            }

            pstm.executeQuery();
            return 1;

        } catch (SQLException e) {

            e.printStackTrace();
            return -1;
        }

    }

    public void close() {

    }

    @Override
    public Object get(Class theClass, Object ID) {
        String sql = QueryHelper.createQuerySELECT(theClass);
        Object entity = null;
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setObject(1, ID);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    entity = theClass.getDeclaredConstructor().newInstance();
                    for (String field : ObjectHelper.getFields(entity)) {
                        try {
                            Field declaredField = entity.getClass().getDeclaredField(field);
                            CustomAnnotation annotation = declaredField.getAnnotation(CustomAnnotation.class);
                            if (annotation == null || !annotation.value().equals("quantity_exclude")) {
                                ObjectHelper.setter(entity, field, rs.getObject(field));
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
    @Override
    public Object getbyName(Class theClass, Object ID) {
        String sql = QueryHelper.createQuerySELECTWERENAME(theClass);
        Object entity = null;
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setObject(1, ID);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    entity = theClass.getDeclaredConstructor().newInstance();
                    for (String field : ObjectHelper.getFields(entity)) {
                        try {
                            Field declaredField = entity.getClass().getDeclaredField(field);
                            CustomAnnotation annotation = declaredField.getAnnotation(CustomAnnotation.class);
                            if (annotation == null || !annotation.value().equals("quantity_exclude")) {
                                ObjectHelper.setter(entity, field, rs.getObject(field));
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }



    public void update(Object entity) {
        String updateQuery = QueryHelper.createQueryUPDATE(entity);
        try (PreparedStatement pstm = conn.prepareStatement(updateQuery)) {
            int i = 1;
            for (String field : ObjectHelper.getFields(entity)) {
                try {
                    Field declaredField = entity.getClass().getDeclaredField(field);
                    CustomAnnotation annotation = declaredField.getAnnotation(CustomAnnotation.class);
                    if ((annotation == null || !annotation.value().equals("id_exclude"))) {
                        pstm.setObject(i++, ObjectHelper.getter(entity, field));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            pstm.setObject(i, ObjectHelper.getter(entity, "id"));
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(Object object) {
        String deleteQuery = QueryHelper.createQueryDELETE(object);
        try (PreparedStatement pstm = conn.prepareStatement(deleteQuery)) {
            pstm.setObject(1, ObjectHelper.getter(object, "id"));
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object> findAll(Class theClass) {
        List<Object> entities = new ArrayList<>();
        String sql = QueryHelper.createSelectFindAll(theClass);
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Object entity = theClass.getDeclaredConstructor().newInstance();
                for (String field : ObjectHelper.getFields(entity)) {
                    try {
                        Field declaredField = entity.getClass().getDeclaredField(field);
                        CustomAnnotation annotation = declaredField.getAnnotation(CustomAnnotation.class);
                        if (annotation == null || !annotation.value().equals("quantity_exclude")) {
                            ObjectHelper.setter(entity, field, rs.getObject(field));
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                entities.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    public List<Object> findAll(Class theClass, HashMap params) {
        return null;
    }

    public List<Object> query(String query, Class theClass, HashMap params) {
        return null;
    }



}