/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masuyatest01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author badri
 */
public class DBConnection {
//    private static final String URL = "jdbc:sqlserver://10.0.0.97:1433;databaseName=masuyatest;encrypt=false";
    private static final String URL = "jdbc:sqlserver://10.0.0.97:1433;databaseName=masuyatest;encrypt=false;trustServerCertificate=true";
    private static final String USER = "masuyadev";
    private static final String PASSWORD = "Masuya@Dev123";  

    private static final String CONNECTION_URL = 
        "jdbc:sqlserver://10.0.0.97:1433;"
        + "databaseName=masuyatest;"
        + "user=masuyadev;"
        + "password=Masuya@Dev123;"
        + "encrypt=false;"
        + "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }
}
