package productos;

public class Categoria {
    private Long id;
    private String descripcion;

    public Categoria(){}

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
}
