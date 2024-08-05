package productos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Categoria {
    private Long id;
    private String descripcion;
    @JsonIgnore
    private int listado;

    public Categoria(){}

    public Categoria(Long id, String descripcion, int listado){
        this.id = id;
        this.descripcion = descripcion;
        this.listado = listado;
    }

    //Constructor para el filtro
    public Categoria(Long id, String descripcion){
        this.id = id;
        this.descripcion = descripcion;
    }

    //getters y setters
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public int getListado(){return listado;}
    public void setListado(int listado){this.listado = listado;}
}
