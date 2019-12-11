package org.event.driven.light.kafkaserialize.dbconnection;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBHelper {

    private String name = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "123456";

    public Connection conn;
    public PreparedStatement pst;

    public DBHelper(String url){
        try {
            Class.forName(name);
            conn = DriverManager.getConnection(url,user,password);

        }catch(Exception e){
            System.out.println("cannot connect to MySql.");
            e.printStackTrace();
        }
    }

    public void execute(String sql){
        try {
            pst = conn.prepareStatement(sql);
        }catch(Exception e){
            System.out.println("Error when execute sql query.");
        }
    }

    public void close() {
        try {
            this.conn.close();
            this.pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
