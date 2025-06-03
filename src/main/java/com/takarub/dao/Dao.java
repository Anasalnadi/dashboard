
package com.takarub.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author aduraidi
 */
public class Dao {

    Context initContext;
    Context envContext;
    DataSource ds;
    Connection conn;
    
    public Dao() {
    }

    //if I have one resource(schema)
    public Connection getConnection() throws Exception {
        initContext = new InitialContext();
        envContext = (Context) initContext.lookup("java:/comp/env");// path of context.xml
        ds = (DataSource) envContext.lookup("takarub_competition");// name of recource(schema)

        conn = ds.getConnection();
        if (conn != null) {
            return conn;
        }
        return null;
    }

    //if I have more than one resource(schema)
    public Connection getConnection(String connName) throws Exception {
        initContext = new InitialContext();
        envContext = (Context) initContext.lookup("java:/comp/env");
        ds = (DataSource) envContext.lookup(connName);
        conn = ds.getConnection();
        if (conn != null) {
            return conn;
        }
        return null;
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

