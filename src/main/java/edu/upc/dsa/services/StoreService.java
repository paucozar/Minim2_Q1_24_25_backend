package edu.upc.dsa.services;

import edu.upc.dsa.StoreManager;
import edu.upc.dsa.models.Item;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Api(value = "/store", description = "Endpoint to Store Service")
@Path("/store")
public class StoreService {
    private static final Logger logger = Logger.getLogger(StoreService.class);
    private StoreManager sm;

    public StoreService() {
        this.sm = StoreManager.getInstance();
        if (sm.getAllItems().isEmpty()) {
            this.sm.addItem(new Item("1", "Laptop", "High performance laptop", 1200.00));
            this.sm.addItem(new Item("2", "Smartphone", "Latest model smartphone", 800.00));
            this.sm.addItem(new Item("3", "Headphones", "Noise-cancelling headphones", 150.00));
        }
    }

    @GET
    @ApiOperation(value = "get all Items", notes = "Retrieve all items from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Item.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        List<Item> items = this.sm.getAllItems();
        if (items.isEmpty()) {
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
        if (!securityContext.isUserInRole("admin")) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tienes permiso para realizar esta acción").build();
        }

        for (Item existingItem : this.sm.getAllItems()) {
            if (existingItem.getName().equals(item.getName())) {
                return Response.status(409).entity("Item already exists").build();
            }
        }

        this.sm.addItem(item);
        return Response.status(201).entity("Item added successfully").build();
    }

    @DELETE
    @ApiOperation(value = "delete an Item", notes = "Delete an item from the store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @Path("/items/{name}")
    public Response deleteItem(@PathParam("name") String name, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("admin")) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tienes permiso para realizar esta acción").build();
        }

        Item itemToRemove = null;
        for (Item item : this.sm.getAllItems()) {
            if (item.getName().equals(name)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove == null) {
            return Response.status(404).entity("Item not found").build();
        }

        this.sm.deleteItem(name);
        return Response.status(201).entity("Item deleted successfully").build();
    }
}