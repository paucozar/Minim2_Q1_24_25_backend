package edu.upc.dsa.orm.dao;

import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;

public class userItemDAOImpl implements userItemDAO {
    @Override
    public void insertUserItem(String user_Id, String item_Id, int quantity) {
        Session session = null;

        try {
            session = FactorySession.openSession();
            session.InsertUserItems(user_Id, item_Id, quantity);
        } catch (Exception e) {
            // LOG
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
