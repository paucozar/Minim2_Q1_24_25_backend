
package edu.upc.dsa.services;

import edu.upc.dsa.DBUtils;
import edu.upc.dsa.StoreManagerImpl;
import edu.upc.dsa.models.Item;
import edu.upc.dsa.orm.dao.ItemDaoImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/store", description = "Endpoint to Store Service")
@Path("/store")
public class StoreService {
    ItemDaoImpl itemDAO = new ItemDaoImpl();
    private static final Logger logger = Logger.getLogger(StoreService.class);
    private StoreManagerImpl sm;

    public StoreService() {
        //this.sm = StoreManagerImpl.getInstance();
        //if (sm.findAllItems().isEmpty()) {
        //    this.sm.addItem(new Item("1", "Laptop", "High performance laptop", 1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGoQDLRQCgfedvcfRBgWol-dXTJ4IpIGgppg&s"));
        //    this.sm.addItem(new Item("2", "Smartphone", "Latest model smartphone", 800, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6t0zst_7dmMNi-eJBK58VuHLee0Q5PBQatg&s"));
        //    this.sm.addItem(new Item("3", "Headphones", "Noise-cancelling headphones", 150, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2FKeSgIbsF64rqq-7OrmYxyq3k0a-TXnklg&s"));
        //}
    }

    public List<Item> getItemsLocal() {
        return this.sm.findAllItems();
    }

    @GET
    @ApiOperation(value = "get all Items", notes = "Retrieve all items from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Item.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        List<Item> items = null;
        try {
            items = itemDAO.getItems();
        } catch (Exception e) {
            logger.error("Error retrieving items: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if (items == null || items.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {};
        return Response.ok(entity).build();
    }

    @POST
    @ApiOperation(value = "add a new Item", notes = "Add a new item to the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 409, message = "Item already exists")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addItem(Item item, @Context SecurityContext securityContext) {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String checkSql = "SELECT COUNT(*) FROM item WHERE name = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, item.getName());
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        return Response.status(409).entity("Item already exists").build();
                    }
                }
            }

            String insertSql = "INSERT INTO item (id, name, description, price, imageUrl) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, item.getId());
                insertStmt.setString(2, item.getName());
                insertStmt.setString(3, item.getDescription());
                insertStmt.setInt(4, item.getPrice());
                insertStmt.setString(5, item.getImageUrl());
                insertStmt.executeUpdate();
            }
            return Response.status(201).entity("Item added successfully").build();
        } catch (SQLException e) {
            logger.error("Error adding item: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @DELETE
    @ApiOperation(value = "delete an Item", notes = "Delete an item from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @Path("/items/{name}")
    public Response deleteItem(@PathParam("name") String name, @Context SecurityContext securityContext) {
        Connection connection = null;
        try  {
            connection = DBUtils.getConnection();
            String deleteSql = "DELETE FROM item WHERE name = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, name);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected == 0) {
                    return Response.status(404).entity("Item not found").build();
                }
            }
            return Response.status(201).entity("Item deleted successfully").build();
        } catch (SQLException e) {
            logger.error("Error deleting item: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}