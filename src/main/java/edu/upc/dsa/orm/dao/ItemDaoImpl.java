package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.Item;
import edu.upc.dsa.models.User;
import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;

import java.util.List;

public class ItemDaoImpl implements ItemDAO{

    @Override
    public String addItem(String id, String name, String description, int price, String imageUrl) {
        Session session = null;

        // Assuming there is a method to save the item to the database
        int result = 0;
        try {
            session = FactorySession.openSession();
            Item item = new Item(id, name, description, price, imageUrl);
            result = session.save(item);
        }
        catch (Exception e) {
            return "Error";
        }
        finally {
            session.close();
        }

        if (result == -1) {
            return "Error";
        }
        else {
            return id;
        }
    }

    @Override
    public void updateItem(String id, String name, String description, int price) {

    }

    @Override
    public void deleteItembyID(String id) {

    }

    @Override
    public List<Item> getItems() {
        Session session = null;
        List<Item> items = null;
        try {
            session = FactorySession.openSession();
            items = session.findAll(Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return items;
    }

    @Override
    public Item getItembyID(String ID) {
        Session session = null;
        Item item = null;
        try {
            session = FactorySession.openSession();
            item = (Item) session.get(Item.class, ID);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return item;
    }

    @Override
    public Item getItembyName(Item item) {
        return null;
    }

}
