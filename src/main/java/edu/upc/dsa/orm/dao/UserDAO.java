package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.User;

import java.util.List;

public interface UserDAO {

    public String addUser(String id, String surname, String password);
    public User getUserbyID(String ID);
    public User getUserbyName(User user);

    public void updateUser(User user);
    public void deleteUserbyID(String ID);
    public List<User> getUsers();
    public int updateUserCoins(String name, int coins);
    //public List <User> getEmployeeByDept(int deptId);

}
