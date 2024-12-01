
package edu.upc.dsa.services;

import edu.upc.dsa.*;
import edu.upc.dsa.models.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Api(value = "/users", description = "Endpoint to user Service")
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserService extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new java.util.HashSet<>();
        classes.add(UserService.class);  // El servicio de usuarios
        classes.add(AuthFilter.class);   // El filtro de autenticación
        classes.add(JacksonJsonProvider.class);
        return classes;
    }

    private static final Logger logger = Logger.getLogger(UserService.class);
    private UserManager us;
    private StoreManager sm;
    private InventoryManager im;

    public boolean isUsernameTaken(String username){
        User user = this.us.getUserByUsername(username);
        return user != null;
    }


    public UserService() {

    }

    @GET
    @ApiOperation(value = "Get all Inventories", notes = "Retrieve all inventories for a user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Item.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid username provided"),
            @ApiResponse(code = 404, message = "No inventory found for the user"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Path("/inventories/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInventory(@PathParam("username") String username) {
        if (username == null || username.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username is required\"}")
                    .build();
        }

        String sql = "SELECT i.id as itemId, i.name, i.description, i.price, i.imageUrl, SUM(ui.quantity) as totalQuantity " +
                "FROM user u " +
                "JOIN user_item ui ON u.id = ui.user_id " +
                "JOIN item i ON ui.item_id = i.id " +
                "WHERE u.username = ? " +
                "GROUP BY i.id";

        // Manejo automático de recursos con try-with-resources
        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Item> items = new ArrayList<>();

                while (resultSet.next()) {
                    String itemId = resultSet.getString("itemId");
                    String itemName = resultSet.getString("name");
                    String itemDescription = resultSet.getString("description");
                    int itemPrice = resultSet.getInt("price");
                    String itemImageUrl = resultSet.getString("imageUrl");
                    int totalQuantity = resultSet.getInt("totalQuantity");

                    // Crear y añadir el objeto Item
                    Item item = new Item(itemId, itemName, itemDescription, itemPrice, itemImageUrl);
                    item.setQuantity(totalQuantity);
                    items.add(item);
                }

                // Si no se encontraron items
                if (items.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"No inventory found for user " + username + "\"}")
                            .build();
                }

                // Devolver la lista de items
                GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {};
                return Response.ok(entity).build();
            }
        } catch (SQLException e) {
            logger.error("Database error while retrieving inventory for user: " + username, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Database error occurred\"}")
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Unexpected error occurred\"}")
                    .build();
        }
    }








    @Provider
    @Priority(Priorities.AUTHENTICATION)
    public class AuthFilter implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            // Captura los encabezados personalizados
            String username = requestContext.getHeaderString("X-Username");
            String role = requestContext.getHeaderString("X-Role");

            // Verificar si los encabezados son correctos
            if (username == null || role == null) {
                requestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\": \"Unauthorized: Missing authentication headers\"}")
                        .build());
                return;
            }

            // Crear un contexto de seguridad personalizado basado en el encabezado "X-Role"
            SecurityContext securityContext = new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String r) {
                    // Si "role" es "admin", el usuario es administrador
                    return "admin".equalsIgnoreCase(role) && "admin".equalsIgnoreCase(r);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getUriInfo().getAbsolutePath().toString().startsWith("https");
                }

                @Override
                public String getAuthenticationScheme() {
                    return "CustomAuth";
                }
            };

            // Establecer el contexto de seguridad personalizado
            requestContext.setSecurityContext(securityContext);
        }
    }

    @GET
    @ApiOperation(value = "get all User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class, responseContainer = "List"),
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser() {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String sql = "SELECT * FROM user";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setFullName(resultSet.getString("fullName"));
                user.setEmail(resultSet.getString("email"));
                user.setAge(resultSet.getInt("age"));
                user.setProfilePicture(resultSet.getString("profilePicture"));
                users.add(user);
            }

            GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {};
            return Response.ok(entity).build();
        } catch (SQLException e) {
            logger.error("Error al obtener los usuarios: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"Usuario no encontrado\"}").build();
            }

            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setFullName(resultSet.getString("fullName"));
            user.setEmail(resultSet.getString("email"));
            user.setAge(resultSet.getInt("age"));
            user.setProfilePicture(resultSet.getString("profilePicture"));

            return Response.status(Response.Status.OK).entity(user).build();
        } catch (SQLException e) {
            logger.error("Error al obtener el usuario: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GET
    @ApiOperation(value = "get a User Profile", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class),
            @ApiResponse(code = 404, message = "User not found")
    })
    @Path("/{username}/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProfile(@PathParam("username") String username) {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"Usuario no encontrado\"}").build();
            }

            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setFullName(resultSet.getString("fullName"));
            user.setEmail(resultSet.getString("email"));
            user.setAge(resultSet.getInt("age"));
            user.setProfilePicture(resultSet.getString("profilePicture"));

            return Response.status(Response.Status.OK).entity(user).build();
        } catch (SQLException e) {
            logger.error("Error al obtener el perfil de usuario: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
        } finally {
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
    @ApiOperation(value = "delete a User", notes = "Elimina un usuario específico si es un administrador")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario eliminado exitosamente"),
            @ApiResponse(code = 401, message = "No autenticado"),
            @ApiResponse(code = 403, message = "No autorizado"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("username") String username, @Context SecurityContext securityContext) {
        try {
            // Verificar si el usuario está autenticado
            if (securityContext.getUserPrincipal() == null) {
                logger.info("Usuario no autenticado intentó eliminar un usuario.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\": \"Usuario no autenticado\"}")
                        .build();
            }

            // Verificar si el usuario tiene permisos de administrador
            if (!securityContext.isUserInRole("admin")) {
                logger.info("Usuario sin permisos de admin: " + securityContext.getUserPrincipal().getName());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\": \"No tienes permiso para realizar esta acción\"}")
                        .build();
            }

            // Procede con la eliminación del usuario
            User u = this.us.getUserByUsername(username);
            if (u == null) {
                logger.warn("Usuario no encontrado: " + username);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Usuario no encontrado\"}")
                        .build();
            }

            this.us.deleteUser(username);
            logger.info("Usuario eliminado: " + username);
            return Response.status(Response.Status.OK)
                    .entity("{\"message\": \"Usuario eliminado exitosamente\"}")
                    .build();

        } catch (Exception e) {
            logger.error("Error al eliminar usuario: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error interno del servidor\"}")
                    .build();
        }
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
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Response.status(404).entity("Usuario no encontrado").build();
            }

            String updateSql = "UPDATE user SET isAdmin = ? WHERE username = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, user.getIsAdmin());
            updateStatement.setString(2, username);
            updateStatement.executeUpdate();

            return Response.status(200).entity("{\"message\": \"Usuario actualizado exitosamente\"}").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PUT
    @Path("/{username}/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update a User profile", notes = "Permite a un usuario actualizar su perfil")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Perfil actualizado exitosamente"),
            @ApiResponse(code = 404, message = "Usuario no encontrado")
    })
    public Response updateUserProfile(@PathParam("username") String username, User userProfileUpdate) {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                logger.warn("Usuario no encontrado: " + username);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Usuario no encontrado\"}")
                        .build();
            }

            // Actualizar los parámetros del perfil con los valores del JSON
            String updateSql = "UPDATE user SET fullName = ?, email = ?, age = ?, profilePicture = ? WHERE username = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, userProfileUpdate.getFullName() != null ? userProfileUpdate.getFullName() : resultSet.getString("fullName"));
            updateStatement.setString(2, userProfileUpdate.getEmail() != null ? userProfileUpdate.getEmail() : resultSet.getString("email"));
            updateStatement.setInt(3, userProfileUpdate.getAge() != 0 ? userProfileUpdate.getAge() : resultSet.getInt("age"));
            updateStatement.setString(4, userProfileUpdate.getProfilePicture() != null ? userProfileUpdate.getProfilePicture() : resultSet.getString("profilePicture"));
            updateStatement.setString(5, username);
            updateStatement.executeUpdate();

            logger.info("Perfil actualizado para el usuario: " + username);
            return Response.status(Response.Status.OK)
                    .entity("{\"message\": \"Perfil actualizado exitosamente\"}")
                    .build();
        } catch (SQLException e) {
            logger.error("Error al actualizar el perfil de usuario: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error interno del servidor\"}")
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @POST
    @ApiOperation(value = "create a new User", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response=User.class),
            @ApiResponse(code = 409, message = "Username already exists"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newUser(User user) {
        if (user.getPassword() == null || user.getUsername() == null) {
            return Response.status(500).entity("{\"message\": \"Username or password cannot be null\"}").build();
        }

        Connection connection = null;
        try {
            connection = DBUtils.getConnection();
            String checkUserSql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSql);
            checkUserStmt.setString(1, user.getUsername());
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next()) {
                return Response.status(409).entity("{\"message\": \"Username already exists\"}").build();
            }

            String insertUserSql = "INSERT INTO user (id, username, password, isAdmin) VALUES (?, ?, ?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSql);
            insertUserStmt.setString(1, user.getId());
            insertUserStmt.setString(2, user.getUsername());
            insertUserStmt.setString(3, user.getPassword());
            insertUserStmt.setString(4, user.getIsAdmin());
            insertUserStmt.executeUpdate();

            return Response.status(201).entity("{\"message\": \"User created successfully\"}").build();
        } catch (SQLException e) {
            logger.error("Error creating user: " + e.getMessage(), e);
            return Response.status(500).entity("{\"message\": \"Internal server error\"}").build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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

        Connection connection = null;
            try {
                connection = DBUtils.getConnection();
                String sql = "SELECT * FROM user WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, user.getUsername());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String password = resultSet.getString("password");
                    if (password == null || !user.getPassword().equals(password)) {
                        logger.warn("Credenciales incorrectas para el usuario: " + user.getUsername());
                        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Credenciales incorrectas\"}").build();
                    }
                }

                String role = resultSet.getString("isAdmin").equals("admin") ? "admin" : "user";
                int coins = resultSet.getInt("coins");

                return Response.ok()
                        .entity("{\"message\": \"Login exitoso\", \"role\": \"" + role + "\", \"coins\": " + coins + ", \"redirect\": \"" + (role.equals("admin") ? "admin.html" : "user.html") + "\"}")
                        .build();
            } catch (SQLException e) {
                logger.error("Error al iniciar sesión: " + e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Error interno del servidor\"}").build();
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

    @Path("/purchase")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchase(PurchaseRequest request) {
        Connection connection = null;
        try {
            connection = DBUtils.getConnection();

            // Obtener los valores de username, password, y itemId desde el requestBody
            User user = request.getUser();
            Item item = request.getItem();
            int quantity = request.getQuantity();

            // Buscar el usuario por su nombre de usuario
            String userSql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement userStmt = connection.prepareStatement(userSql);
            userStmt.setString(1, user.getUsername());
            ResultSet userResultSet = userStmt.executeQuery();
            if (userResultSet.next()) {
                String password = userResultSet.getString("password");
                if (password == null || !user.getPassword().equals(password)) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\": \"Credenciales incorrectas\"}")
                            .build();
                }
            }

            // Buscar el item por su id
            String itemSql = "SELECT * FROM item WHERE id = ?";
            PreparedStatement itemStmt = connection.prepareStatement(itemSql);
            itemStmt.setString(1, item.getId());
            ResultSet itemResultSet = itemStmt.executeQuery();

            if (!itemResultSet.next()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Item no encontrado\"}")
                        .build();
            }

            // Verificar si el usuario tiene suficientes monedas para realizar la compra
            int userCoins = userResultSet.getInt("coins");
            int itemPrice = itemResultSet.getInt("price");
            if (userCoins < itemPrice) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"No tienes suficientes monedas para esta compra\"}")
                        .build();
            }

            // Realizar la compra (restar monedas)
            String updateUserSql = "UPDATE user SET coins = ? WHERE username = ?";
            PreparedStatement updateUserStmt = connection.prepareStatement(updateUserSql);
            updateUserStmt.setInt(1, userCoins - itemPrice);
            updateUserStmt.setString(2, user.getUsername());
            updateUserStmt.executeUpdate();

            // Insertar la compra en la tabla user_item
            String insertUserItemSql = "INSERT INTO user_item (id, user_id, item_id, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement insertUserItemStmt = connection.prepareStatement(insertUserItemSql);
            insertUserItemStmt.setString(1, UUID.randomUUID().toString());
            insertUserItemStmt.setString(2, userResultSet.getString("id"));
            insertUserItemStmt.setString(3, item.getId());
            insertUserItemStmt.setInt(4, quantity);
            insertUserItemStmt.executeUpdate();


            // Responder con las monedas restantes y el mensaje de éxito
            return Response.ok()
                    .entity("{\"message\": \"Compra exitosa\", \"coins\": " + (userCoins - itemPrice) + "}")
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error interno del servidor\"}")
                    .build();
        } finally {
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