package productos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection connection; 

    public Conexion() {
        try {
            // Paso 1: Cargar dinámicamente el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Paso 2: Establecer la conexión con la base de datos 'peliculas_cac_java' en localhost
            this.connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/popi_impresiones",  // URL de conexión JDBC para MySQL
                "root",  // Nombre de usuario de la base de datos (cambia según tu configuración)
                ""  // Contraseña de la base de datos (cambia según tu configuración)
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //error en caso de no encontrar el driver
        } catch (SQLException e) {
            e.printStackTrace();  //error con la conexión a la base de datos
        }
    }

    public Connection getConnection() {
        return connection;  
    }

    //cerrar la conexión
    public void close() {
        try {
            // Verificar si la conexión no es nula y está abierta, entonces cerrarla
            if (connection != null && !connection.isClosed()) {
                connection.close();  // Cierra la conexión a la base de datos
            }
        } catch (SQLException e) {
            e.printStackTrace();  
        }
    }
}
