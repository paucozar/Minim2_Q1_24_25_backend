package edu.upc.dsa.models;

public class PurchaseRequest {
    private User user;
    private Item item;

    // Getters y setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
}
