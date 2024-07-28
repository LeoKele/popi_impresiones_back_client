package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebServlet("/ventas")
public class ControladorVenta extends ControladorBase {

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
            query = "SELECT v.id_venta, v.fecha_venta, v.id_producto, p.nombre_producto, v.cantidad, v.precio_unitario, (v.precio_unitario * v.cantidad) AS total,(v.precio_unitario * v.cantidad) - (p.costo * v.cantidad) AS ganancia FROM ventas v INNER JOIN productos p ON v.id_producto = p.id_producto WHERE id_venta = ? ORDER BY `v`.`fecha_venta` ASC ";
        } else {
            query = "SELECT v.id_venta, v.fecha_venta, v.id_producto, p.nombre_producto, v.cantidad, v.precio_unitario,(v.precio_unitario * v.cantidad) AS total, (v.precio_unitario * v.cantidad) - (p.costo * v.cantidad) AS ganancia FROM ventas v INNER JOIN productos p ON v.id_producto = p.id_producto ORDER BY `v`.`fecha_venta` ASC";
        }
        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }

            ResultSet resultSet = statement.executeQuery();
            
            List<Venta> ventas = new ArrayList<>();

            while (resultSet.next()) {
                //Convertir java.sql.date a java.time.LocalDate
                Date sqlDate = resultSet.getDate("fecha_venta");
                LocalDate fechaVenta = sqlDate.toLocalDate();
                Venta venta = new Venta(
                        resultSet.getLong("id_venta"),
                        resultSet.getLong("id_producto"),
                        resultSet.getString("nombre_producto"),
                        fechaVenta,
                        resultSet.getLong("cantidad"),
                        resultSet.getDouble("precio_unitario"),
                        resultSet.getDouble("total"),
                        resultSet.getDouble("ganancia")
                );
                ventas.add(venta);
            }

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            String json = mapper.writeValueAsString(ventas);

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
        Venta venta = mapper.readValue(request.getInputStream(), Venta.class);

        // Verificar si el producto existe antes de realizar la operación
        if (!productoService.productoExiste(venta.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }

        String query = "INSERT INTO ventas (id_producto, fecha_venta, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS)) {



            statement.setLong(1, venta.getIdProducto());
            statement.setDate(2, Date.valueOf(venta.getFechaVenta()));
            statement.setLong(3, venta.getCantidad());
            statement.setDouble(4, venta.getPrecioUnitario());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idVenta = rs.getLong(1);

                    VentaResponse ventaResponse = new VentaResponse(idVenta, venta.getIdProducto(), venta.getNombre(), venta.getFechaVenta(), venta.getCantidad(), venta.getPrecioUnitario(), venta.getTotal(), venta.getGanancia());
                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(ventaResponse);
                    response.getWriter().write(json);
                } else {
                    throw new SQLException("Error al crear la venta, ningun id obtenido");
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
        String query = "UPDATE ventas SET id_producto = ?, fecha_venta = ?, cantidad = ?, precio_unitario = ? WHERE id_venta = ?";
        try(Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = JsonConfig.createObjectMapper();  // Crear un objeto ObjectMapper para convertir JSON a objetos Java
            Venta venta = mapper.readValue(request.getInputStream(), Venta.class);  // Convertir el JSON de la solicitud a un objeto Pelicula


            // Establecer los parámetros de la consulta de actualización
            statement.setLong(1, venta.getIdProducto());
            statement.setDate(2, Date.valueOf(venta.getFechaVenta()));
            statement.setLong(3, venta.getCantidad());
            statement.setDouble(4, venta.getPrecioUnitario());
            statement.setLong(5, venta.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Venta actualizada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Venta no encontrada.\"}");
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
        String query = "DELETE FROM ventas WHERE id_venta = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de venta no proporcionado.\"}");
                return;
            }

            int idVenta = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idVenta);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Venta eliminada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Venta no encontrada.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de venta inválido.\"}");
        }
    }
        
}
