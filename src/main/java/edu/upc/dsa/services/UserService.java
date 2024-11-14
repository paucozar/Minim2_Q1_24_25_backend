package edu.upc.dsa.services;


import edu.upc.dsa.UserManager;
import edu.upc.dsa.UserManagerImpl;
import edu.upc.dsa.models.Track;
import edu.upc.dsa.models.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

@Api(value = "/users", description = "Endpoint to Track Service")
@Path("/users")

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class);
    private UserManager us;
    public UserService() {
        this.us = UserManagerImpl.getInstance();
        if (us.size()==0) {
            this.us.addUser("Admin", "admin", true);
            this.us.addUser("user1", "User1", false);
            this.us.addUser("user2", "User2", false );
        }
    }

    @GET
    @ApiOperation(value = "get all User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class, responseContainer="List"),
    })
    // Elimina @Path("/") porque ya tienes /tracks a nivel de clase
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser() {
        List<User> users = this.us.findAll();
        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {};
        return Response.ok(entity).build();
    }

    @GET
    @ApiOperation(value = "get a User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class),
            @ApiResponse(code = 404, message = "User not found")
    })
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username) {
        User u = this.us.getUser(username);
        if (u == null) return Response.status(404).build();
        else return Response.status(201).entity(u).build();
    }

    @DELETE
    @ApiOperation(value = "delete a User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @Path("/{username}")
    public Response deleteUser(@PathParam("username") String username, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("admin")) {
            return Response.status(Response.Status.FORBIDDEN).entity("No tienes permiso para realizar esta acción").build();
        }
        User u = this.us.getUser(username);
        if (u == null) return Response.status(404).build();
        else this.us.deleteUser(username);
        return Response.status(201).build();
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update a User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public Response updateUser(@PathParam("username") String username, User user) {
        User existingUser = this.us.getUserByUsername(username);
        if (existingUser == null) {
            return Response.status(404).entity("Usuario no encontrado").build();
        }
        existingUser.setAdmin(user.isAdmin()); // Actualiza el estado de admin
        this.us.updateUser(existingUser);
        return Response.status(200).entity(existingUser).build();
    }

    @POST
    @ApiOperation(value = "create a new User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response=Track.class),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    // Elimina @Path("/") porque ya tienes /tracks a nivel de clase
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newUser(User user) {
        if (user.getPassword() == null || user.getUsername() == null) {
            return Response.status(500).entity(user).build();
        }
        this.us.addUser(user.getUsername(), user.getPassword(), user.isAdmin());
        return Response.status(201).entity(user).build();
    }

    @POST
    @ApiOperation(value = "login a User", notes = "Login a user with username and password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful login"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        try {
            User storedUser = this.us.getUserByUsername(user.getUsername());
            logger.info("Buscando usuario: " + user.getUsername());

            if (storedUser == null) {
                logger.warn("Usuario no encontrado: " + user.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Credenciales incorrectas\"}").build();
            }

            if (!BCrypt.checkpw(user.getPassword(), storedUser.getPassword())) {
                logger.warn("Contraseña incorrecta para el usuario: " + user.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Credenciales incorrectas\"}").build();
            }

            // Si el usuario es admin
            if (storedUser.isAdmin()) {
                return Response.ok().entity("{\"message\": \"Login exitoso, redirigiendo a la página de administración\", \"redirect\": \"admin.html\"}").build();
            }

            // Si el usuario no es admin
            return Response.ok().entity("{\"message\": \"Login exitoso, redirigiendo a la página de usuario\", \"redirect\": \"user.html\"}").build();
        } catch (Exception e) {
            logger.warn("Error al iniciar sesión: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
        }
    }


}
