package edu.upc.dsa.models;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
@XmlRootElement

public class Inventory {
    private String id;
    private List<InventoryItem> inventoryitems = new ArrayList<>();  // Asegúrate de inicializar la lista.

    // Constructor vacío necesario para JAXB
    public Inventory() {}

    public Inventory(String id) {
        this.id = id;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public void addInventoryItem(InventoryItem inventoryItem) {
        this.inventoryitems.add(inventoryItem);
    }

    @JsonProperty("inventoryitems")
    @XmlElement
    public List<InventoryItem> getInventoryitems() {
        return inventoryitems;
    }

    public void setInventoryitems(List<InventoryItem> inventoryitems) {
        this.inventoryitems = inventoryitems;
    }
}