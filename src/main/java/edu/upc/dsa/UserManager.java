package edu.upc.dsa;

import edu.upc.dsa.exceptions.UserNotFoundException;
    import edu.upc.dsa.models.User;

import java.util.List;

public interface UserManager {



    public User addUser(String username, String password, boolean isAdmin);
    public User addUsers(User user);
    public User getUser(String id);


    public List<User> findAll();
    public void deleteUser(String id);
    public User updateUser(User t);

    public void clear();
    public int size();
    public User getUserByUsername(String username);
}
