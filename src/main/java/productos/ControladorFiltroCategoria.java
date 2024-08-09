package productos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/productos/categorias")
public class ControladorFiltroCategoria extends ControladorBase {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query = "SELECT * FROM categoria_productos WHERE listado = 1 ORDER BY descripcion_categoria ASC;";


        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            List<CategoriaDTO> categorias = new ArrayList<>();

            while (resultSet.next()) {
                CategoriaDTO categoriaDTO = new CategoriaDTO(
                        resultSet.getLong("id_categoria"),
                        resultSet.getString("descripcion_categoria")
                );
                categorias.add(categoriaDTO);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(categorias);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }
}
