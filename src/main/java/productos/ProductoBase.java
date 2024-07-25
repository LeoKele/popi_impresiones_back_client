package productos;


public class ProductoBase {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;


    public ProductoBase(Long id, String nombre, String descripcion, Double precio ){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public ProductoBase(long id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    //Constructor vacio necesario para deserializacion de JSON
    public ProductoBase() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    

}
