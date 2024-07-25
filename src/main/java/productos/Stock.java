package productos;

public class Stock {
    private long id;
    private Long idProducto;
    private String nombre;
    private Long cantidad;


    public Stock(){};

    public Stock(long id, Long idProducto, String nombre, Long cantidad){
        this.id = id;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    //getter y setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getIdProducto(){
        return idProducto;
    }
    public void setIdProducto(Long idProducto){
        this.idProducto = idProducto;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Long getCantidad(){
        return cantidad;
    }
    public void setCantidad(Long cantidad){
        this.cantidad = cantidad;
    }

}

