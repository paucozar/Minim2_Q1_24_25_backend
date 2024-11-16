package edu.upc.dsa;

import edu.upc.dsa.models.Item;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {
    private static StoreManager instance;
    private List<Item> items;

    private StoreManager() {
        items = new ArrayList<>();
        // Initialize with some items if necessary
        items.add(new Item("1", "Laptop", "High performance laptop", 1200.00));
        items.add(new Item("2", "Smartphone", "Latest model smartphone", 800.00));
        items.add(new Item("3", "Headphones", "Noise-cancelling headphones", 150.00));
    }

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }
        return instance;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void deleteItem(String name) {
        items.removeIf(item -> item.getName().equals(name));
    }
}