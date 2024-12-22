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
@WebServlet("/productos/index")
public class ControladorIndex extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query = "SELECT p.id_producto as id, p.nombre_producto as nombre, p.descripcion_producto as descripcion, p.precio_producto as precio, MIN(i.img_path) AS imagen, p.id_categoria FROM productos p LEFT JOIN imagenes_productos i ON p.id_producto = i.id_producto WHERE listado = 1 GROUP BY p.id_producto, p.nombre_producto, p.descripcion_producto, p.precio_producto ORDER BY p.id_producto DESC;";

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            List<ProductoIndex> productosIndex = new ArrayList<>();

            while (resultSet.next()) {
                ProductoIndex productoIndex = new ProductoIndex(
                        resultSet.getLong("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("precio"),
                        resultSet.getLong("id_categoria"),
                        resultSet.getString("imagen")
                );
                productosIndex.add(productoIndex);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(productosIndex);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

        
}
