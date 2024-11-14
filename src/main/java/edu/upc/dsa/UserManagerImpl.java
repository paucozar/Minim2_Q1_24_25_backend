package edu.upc.dsa;

import edu.upc.dsa.exceptions.UserNotFoundException;
import edu.upc.dsa.models.User;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class UserManagerImpl implements UserManager {
    private static UserManager instance;
    protected List<User> Users;
    final static Logger logger = Logger.getLogger(UserManagerImpl.class);


    private UserManagerImpl() {
        this.Users = new LinkedList<>();
    }

    public static UserManager getInstance() {
        if (instance==null) instance = new UserManagerImpl();
        return instance;
    }

    public int size() {
        int ret = this.Users.size();
        logger.info("size " + ret);

        return ret;
    }

    public User addUsers(User t) {
        logger.info("new User " + t);

        this.Users.add (t);
        logger.info("new User added");
        return t;
    }





    @Override
    public User addUser(String username, String password, boolean isAdmin) {
        // Verificar si la contraseña no está vacía antes de encriptar
        if (password == null || password.trim().isEmpty()) {
            logger.warn("Contraseña vacía para el usuario: " + username);
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return this.addUsers(new User(username, hashedPassword, isAdmin));
    }

    public User getUser(String id) {
        logger.info("getUser("+id+")");

        for (User t: this.Users) {
            if (t.getId().equals(id)) {
                logger.info("getTrack("+id+"): "+t);

                return t;
            }
        }

        logger.warn("not found " + id);
        return null;
    }


    public List<User> findAll() {
        return this.Users;
    }

    @Override
    public void deleteUser(String id) {

        User t = this.getUser(id);
        if (t==null) {
            logger.warn("not found " + t);
        }
        else logger.info(t+" deleted ");

        this.Users.remove(t);

    }

    @Override
    public User updateUser(User p) {
        User t = this.getUser(p.getId());

        if (t!=null) {
            logger.info(p+" rebut!!!! ");

            t.setUsername(p.getUsername());
            t.setPassword(p.getPassword());

            logger.info(t+" updated ");
        }
        else {
            logger.warn("not found "+p);
        }

        return t;
    }

    public void clear() {
        this.Users.clear();
    }

    public User getUserByUsername(String username) {
        for (User user : this.Users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}