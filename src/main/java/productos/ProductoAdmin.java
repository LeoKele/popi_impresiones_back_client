package productos;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoAdmin extends ProductoBase{
    private long idCategoria;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoAdmin() {}

    public ProductoAdmin(Long id, String nombre, String descripcion, double precio, long idCategoria) {
        super(id, nombre, descripcion, precio);
        this.idCategoria = idCategoria;
    }
    
    public long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(long idCategoria) {
        this.idCategoria = idCategoria;
    }


}
