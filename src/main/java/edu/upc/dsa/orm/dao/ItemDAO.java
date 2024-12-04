package edu.upc.dsa.orm.dao;

import java.util.List;

import edu.upc.dsa.models.Item;

public interface ItemDAO {
    public String addItem(String id, String name, String description, int price, String imageUrl);
    public void updateItem(String id, String name, String description, int price);
    public void deleteItembyID(String id);
    public List<Item> getItems();
    public Item getItembyID(String ID);
    public Item getItembyName(Item item);
}
