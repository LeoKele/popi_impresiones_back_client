package productos;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoIndex extends ProductoBase{
    private Long idCategoria;
    private String imagen;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoIndex() {}

    public ProductoIndex(Long id, String nombre, String descripcion, double precio, Long idCategoria, String imagen) {
        super(id, nombre, descripcion, precio);
        this.idCategoria = idCategoria;
        this.imagen = imagen;
    }

    public Long getIdCategoria(){return idCategoria;}
    public void setIdCategoria(Long idCategoria){this.idCategoria = idCategoria;}
    
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


}
