package productos;
import java.time.LocalDate;


public class Gasto {
    private long id;
    private LocalDate fecha;
    private String descripcion;
    private Double costo;

    public Gasto(){};

    public Gasto(Long id, LocalDate fecha, String descripcion, Double costo){
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    //
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public LocalDate getFecha(){
        return fecha;
    }
    public void setFecha(LocalDate fecha){
        this.fecha = fecha;
    }

    public String getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    
    public Double getCosto(){
        return costo;
    }
    public void setCosto(Double costo){
        this.costo = costo;
    }
}

