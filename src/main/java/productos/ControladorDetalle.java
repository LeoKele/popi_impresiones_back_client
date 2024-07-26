package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Lo necesitamos para que nos devuelva el JSON que utilizamos en la página web, en el index.html
@WebServlet("/productos/detalle")
public class ControladorDetalle extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query = "SELECT p.id_producto AS id, p.nombre_producto AS nombre, p.descripcion_producto AS descripcion, p.precio_producto AS precio, GROUP_CONCAT(i.img_path ORDER BY i.img_path SEPARATOR ',') AS imagenes FROM productos p LEFT JOIN imagenes_productos i ON p.id_producto = i.id_producto WHERE listado = 1 GROUP BY p.id_producto;";

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            List<ProductoDetalle> productosDetalle = new ArrayList<>();

            while (resultSet.next()) {
                ProductoDetalle productoDetalle = new ProductoDetalle(
                        resultSet.getLong("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("precio"),
                        resultSet.getString("imagenes")
                );
                productosDetalle.add(productoDetalle);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(productosDetalle);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

        
}
