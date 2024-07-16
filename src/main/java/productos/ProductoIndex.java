package productos;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoIndex extends ProductoBase{
    private String imagen;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoIndex() {}

    public ProductoIndex(Long id, String nombre, String descripcion, double precio, String imagen) {
        super(id, nombre, descripcion, precio);
        this.imagen = imagen;
    }
    
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


}
