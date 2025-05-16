import java.sql.*;
public class TestConexion {
    public static void main(String[] args) throws Exception {
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/biblioteca_donbosco_v2",
                "root", "kenny3.01"
        );
        System.out.println("¡Conexión exitosa!");
        c.close();
    }
}