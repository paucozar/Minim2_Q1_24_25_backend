package edu.upc.dsa.models;

import edu.upc.dsa.util.RandomUtils;

public class User_Item {
    String ID;
    String User_ID;
    String Item_ID;
    int quantity;

    public User_Item() {
        this.setID(RandomUtils.getId());
    }

    public User_Item(String User_ID, String Item_ID, int quantity) {
        this();
        this.User_ID = User_ID;
        this.Item_ID = Item_ID;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(String item_ID) {
        Item_ID = item_ID;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String user_Item_ID) {
        ID = user_Item_ID;
    }




}
