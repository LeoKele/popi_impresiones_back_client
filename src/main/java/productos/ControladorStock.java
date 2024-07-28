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




@WebServlet("/stock")
public class ControladorStock extends ControladorBase{

    private ProductoService productoService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");

        if (idParam != null){
            query = "SELECT s.id_stock, s.id_producto, p.nombre_producto, s.cantidad FROM stock s INNER JOIN productos p ON s.id_producto = p.id_producto WHERE id_stock = ? ORDER BY s.id_producto ASC";
        } else {
            query = "SELECT s.id_stock, s.id_producto, p.nombre_producto, s.cantidad FROM stock s INNER JOIN productos p ON s.id_producto = p.id_producto ORDER BY s.id_producto ASC";
        }

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }
            ResultSet resultSet = statement.executeQuery();
            List<Stock> stocks = new ArrayList<>();

            while (resultSet.next()) {
                Stock stock = new Stock(
                        resultSet.getLong("id_stock"),
                        resultSet.getLong("id_producto"),
                        resultSet.getString("nombre_producto"),
                        resultSet.getLong("cantidad")
                );
                stocks.add(stock);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(stocks);

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
        Stock stock = mapper.readValue(request.getInputStream(), Stock.class);

        //* */ Verificar si el producto existe antes de insertar en stock
        if (!productoService.productoExiste(stock.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }
        String query = "INSERT INTO stock (id_producto, cantidad) VALUES (?, ?)";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            

            
            statement.setLong(1, stock.getIdProducto());
            statement.setLong(2, stock.getCantidad());
            statement.executeUpdate();
            
            // Obtener el id generado automáticamente
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long idStock = generatedKeys.getLong(1);
                    
                    // Crear la instancia de respuesta con los campos necesarios
                    StockResponse stockResponse = new StockResponse(idStock, stock.getIdProducto(), stock.getCantidad());
                    
                    // Configuramos la respuesta como JSON
                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(stockResponse);
                    response.getWriter().write(json);
                } else {
                    throw new SQLException("Creating stock failed, no ID obtained.");
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
        String query = "UPDATE stock SET id_producto = ? ,cantidad = ? WHERE id_stock = ?";
        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {
            ObjectMapper mapper = new ObjectMapper();
            Stock stock = mapper.readValue(request.getInputStream(), Stock.class);

            //* */ Verificar si el producto existe antes de insertar en stock
            if (!productoService.productoExiste(stock.getIdProducto())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
                return;
            }
    
            // Establecer los parámetros de la consulta de actualización
            statement.setLong(1, stock.getIdProducto());
            statement.setLong(2, stock.getCantidad());
            statement.setLong(3, stock.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Cantidad/ID Producto actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
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
        String query = "DELETE FROM stock WHERE id_stock = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de stock no proporcionado.\"}");
                return;
            }

            int idStock = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idStock);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Producto eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de stock inválido.\"}");
        }
    }
      
}
