package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/imagenes")
public class ControladorImagen extends ControladorBase{

    private ProductoService productoService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productoService = new ProductoService();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        // String query = "SELECT * FROM imagenes_productos ORDER BY id_producto ASC";
        String query;
        String idParam = request.getParameter("id");

        if (idParam != null){
            query = "SELECT * FROM imagenes_productos WHERE id = ?";
        } else {
            query = "SELECT * FROM imagenes_productos ORDER BY `imagenes_productos`.`id_producto` DESC;";
        }

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }
            ResultSet resultSet = statement.executeQuery();
            List<Imagen> imagenes = new ArrayList<>();

            while (resultSet.next()) {
                Imagen imagen = new Imagen(
                        resultSet.getLong("id"),
                        resultSet.getLong("id_producto"),
                        resultSet.getString("img_path")
                );
                imagenes.add(imagen);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(imagenes);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        ObjectMapper mapper = new ObjectMapper();
        Imagen imagen = mapper.readValue(request.getInputStream(), Imagen.class);

        //* */ Verificar si el producto existe antes de insertar en stock
        if (!productoService.productoExiste(imagen.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }

        String query = "INSERT INTO imagenes_productos (id_producto, img_path) VALUES (?,?)";
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                query, 
                Statement.RETURN_GENERATED_KEYS)) {


            
            statement.setLong(1, imagen.getIdProducto());
            statement.setString(2, imagen.getImgPath());
            statement.executeUpdate();

            // Configuramos la respuesta como JSON
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idImg = rs.getLong(1);

                    ImagenResponse imagenResponse = new ImagenResponse(idImg, imagen.getIdProducto(), imagen.getImgPath());

                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(imagenResponse);
                    response.getWriter().write(json);
                } else {
                    throw new SQLException("Error al crear imagen, ningún ID fue obtenido.");
                }
            }
            response.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            manejarError(response, e);
        } catch (IOException e) {
            manejarError(response, e);
        }
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE imagenes_productos SET id_producto = ?, img_path = ? WHERE id = ?";

        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();
            Imagen imagen = mapper.readValue(request.getInputStream(), Imagen.class);
            
            // Verificar si el nuevo idProducto existe
            if (!productoService.productoExiste(imagen.getIdProducto())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
                return;
            }
            
            // Establecer los parámetros de la consulta de actualización
            statement.setLong(1, imagen.getIdProducto());
            statement.setString(2, imagen.getImgPath());
            statement.setLong(3, imagen.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Imagen actualizada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Imagen no encontrada.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}"); // Devolver el mensaje de error de la base de datos
        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas de entrada/salida
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}"); // Devolver el mensaje de error de entrada/salida
        }
    }
    
    
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar cabeceras CORS
        configurarCORS(response);
        String query = "DELETE FROM imagenes_productos WHERE id = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de producto no proporcionado.\"}");
                return;
            }

            int idProducto = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idProducto);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Imagen eliminada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Imagen no encontrada.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de imagen inválido.\"}");
        }
    }
      
}
