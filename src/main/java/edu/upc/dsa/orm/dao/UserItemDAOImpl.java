package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.User_Item;
import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;

public class UserItemDAOImpl implements UserItemDAO {
    @Override
    public void insertUserItem(User_Item userItem) {
        Session session = null;

        try {
            session = FactorySession.openSession();
            session.save(userItem);
        } catch (Exception e) {
            // LOG
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
