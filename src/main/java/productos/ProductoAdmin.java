package productos;

import com.fasterxml.jackson.annotation.JsonProperty;

// import java.util.ArrayList;
// import java.util.List;

public class ProductoAdmin extends ProductoBase{
    private Long idCategoria;
    private int listado;


    //Constructor vacio necesario para deserializacion de JSON
    public ProductoAdmin() {}

    public ProductoAdmin(Long id, String nombre, String descripcion, double precio, Long idCategoria, int listado) {
        super(id, nombre, descripcion, precio);
        this.idCategoria = idCategoria;
        this.listado = listado;
    }

    @JsonProperty("idCategoria")
    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }
    
    @JsonProperty("listado")
    public int getListado(){
        return listado;
    }
    public void setListado(int listado){
        this.listado = listado;
    }

}
