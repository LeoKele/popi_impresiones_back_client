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


@WebServlet("/gastos")
public class ControladorGastos extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");
        //String tituloParam = request.getParameter("titulo") //titulo like %tituloParam%
        if (idParam != null){
            query = "SELECT * FROM gastos WHERE id = ? ORDER BY fecha ASC";
        } else {
            query = "SELECT * FROM gastos ORDER BY fecha ASC";
        }
        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }

            ResultSet resultSet = statement.executeQuery();
            
            List<Gasto> gastos = new ArrayList<>();

            while (resultSet.next()) {
                //Convertir java.sql.date a java.time.LocalDate
                Date sqlDate = resultSet.getDate("fecha");
                LocalDate fecha = sqlDate.toLocalDate();
                Gasto gasto = new Gasto(
                        resultSet.getLong("id"),
                        fecha,
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("costo")
                );
                gastos.add(gasto);
            }

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            String json = mapper.writeValueAsString(gastos);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO gastos (fecha, descripcion,costo) VALUES (?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS)) {

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            Gasto gasto = mapper.readValue(request.getInputStream(), Gasto.class);

            statement.setDate(1, Date.valueOf(gasto.getFecha()));
            statement.setString(2, gasto.getDescripcion());
            statement.setDouble(3, gasto.getCosto());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);

                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(id);
                    response.getWriter().write(json);
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
        String query = "UPDATE gastos SET fecha = ?, descripcion = ?, costo = ? WHERE id = ?";
        try(Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = JsonConfig.createObjectMapper();  // Crear un objeto ObjectMapper para convertir JSON a objetos Java
            Gasto gasto = mapper.readValue(request.getInputStream(), Gasto.class);  // Convertir el JSON de la solicitud a un objeto Pelicula


            // Establecer los parámetros de la consulta de actualización
            statement.setDate(1, Date.valueOf(gasto.getFecha()));
            statement.setString(2, gasto.getDescripcion());
            statement.setDouble(3, gasto.getCosto());
            statement.setLong(4, gasto.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Gasto actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Gasto no encontrado.\"}");
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
        String query = "DELETE FROM gastos WHERE id = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de gasto no proporcionado.\"}");
                return;
            }

            int id = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, id);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Gasto eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Gasto no encontrado.\"}");
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
