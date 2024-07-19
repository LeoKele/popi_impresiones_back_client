package productos;
import com.fasterxml.jackson.annotation.JsonProperty;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoDetalle extends ProductoBase{
    private String imagenes;  // Campo para las URLs de las imágenes



    //Constructor vacio necesario para deserializacion de JSON
    public ProductoDetalle() {}

    public ProductoDetalle(Long id, String nombre, String descripcion, double precio, String imagenes) {
        super(id, nombre, descripcion, precio);
        this.imagenes = imagenes;
    }
    
    @JsonProperty("imagenes")
    public String getImagen() {
        return imagenes;
    }

    public void setImagen(String imagenes) {
        this.imagenes = imagenes;
    }


}
