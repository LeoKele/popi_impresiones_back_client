package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/pedidos")
public class ControladorPedido extends ControladorBase{

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
        //String tituloParam = request.getParameter("titulo") //titulo like %tituloParam%
        if (idParam != null){
            query = "SELECT pe.*, p.nombre_producto " +
                       "FROM pedidos pe " +
                       "INNER JOIN productos p ON pe.id_producto = p.id_producto " +
                       "WHERE id = ? " +
                       "ORDER BY pe.fecha_recibido ASC";
        } else {
            query = "SELECT pe.*, p.nombre_producto " +
                       "FROM pedidos pe " +
                       "INNER JOIN productos p ON pe.id_producto = p.id_producto " +
                       "ORDER BY pe.fecha_recibido ASC";
        }
        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }

            ResultSet resultSet = statement.executeQuery();
            
            List<Pedido> pedidos = new ArrayList<>();

            while (resultSet.next()) {
                //Convertir java.sql.date a java.time.LocalDate
                Date sqlDate = resultSet.getDate("fecha_recibido");
                LocalDate fechaRecibido = sqlDate.toLocalDate();
                Pedido pedido = new Pedido(
                        resultSet.getLong("id"),
                        fechaRecibido,
                        resultSet.getLong("id_producto"),
                        resultSet.getString("descripcion"),
                        resultSet.getBoolean("producto_listo"),
                        resultSet.getBoolean("pagado"),
                        resultSet.getBoolean("entregado"),
                        resultSet.getString("nombre_producto")
                );
                pedidos.add(pedido);
            }

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            String json = mapper.writeValueAsString(pedidos);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        ObjectMapper mapper = JsonConfig.createObjectMapper();
        Pedido pedido = mapper.readValue(request.getInputStream(), Pedido.class);

        // Verificar si el producto existe antes de realizar la operación
        if (!productoService.productoExiste(pedido.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }

        String query = "INSERT INTO pedidos (fecha_recibido, id_producto, descripcion, producto_listo, pagado, entregado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS)) {



            statement.setDate(1, Date.valueOf(pedido.getFechaRecibido()));
            statement.setLong(2, pedido.getIdProducto());
            statement.setString(3, pedido.getDescripcion());
            statement.setBoolean(4, pedido.getProductoListo());
            statement.setBoolean(5, pedido.getPagado());
            statement.setBoolean(6, pedido.getEntregado());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idPedido = rs.getLong(1);

                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(idPedido);
                    response.getWriter().write(json);
                } else {
                    throw new SQLException("Error al crear el pedido, ningun id obtenido");
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
        String query = "UPDATE pedidos SET fecha_recibido = ?, id_producto = ?, descripcion = ?, producto_listo = ?, pagado = ?, entregado = ? WHERE id = ?";
        try(Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            Pedido pedido = mapper.readValue(request.getInputStream(), Pedido.class);

            // Verificar si el producto existe antes de realizar la operación
            if (!productoService.productoExiste(pedido.getIdProducto())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
                return;
            }

            // Establecer los parámetros de la consulta de actualización
            statement.setDate(1, Date.valueOf(pedido.getFechaRecibido()));
            statement.setLong(2, pedido.getIdProducto());
            statement.setString(3, pedido.getDescripcion());
            statement.setBoolean(4, pedido.getProductoListo());
            statement.setBoolean(5, pedido.getPagado());
            statement.setBoolean(6, pedido.getEntregado());
            statement.setLong(7, pedido.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Pedido actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Pedido no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas de entrada/salida
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar cabeceras CORS
        configurarCORS(response);
        String query = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de pedido no proporcionado.\"}");
                return;
            }

            int idPedido = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idPedido);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Pedido eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Pedido no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de pedido inválido.\"}");
        }
    }
}
