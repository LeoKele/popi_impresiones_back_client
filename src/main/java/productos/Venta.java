package productos;

import java.time.LocalDate;

public class Venta {
    private Long id;
    private Long idProducto;
    private String nombre;
    private LocalDate fechaVenta;
    private Long cantidad;
    private Double precioUnitario;
    private Double total;
    private Double ganancia;

    public Venta(){};

    public Venta(Long id, Long idProducto, String nombre,LocalDate fechaVenta, Long cantidad, Double precioUnitario,Double total, Double ganancia){
        this.id = id;
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.fechaVenta = fechaVenta;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.ganancia = ganancia;
    }

    //getter y setter
    public Long getId(){
        return id;
    }
    public void setId(Long id){
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

    public LocalDate getFechaVenta(){
        return fechaVenta;
    }
    public void setFechaVenta(LocalDate fechaVenta){
        this.fechaVenta = fechaVenta;
    }

    public Long getCantidad(){
        return cantidad;
    }
    public void setCantidad(Long cantidad){
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario(){
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario){
        this.precioUnitario = precioUnitario;
    }
    
    public Double getTotal(){
        return total;
    }

    public void setTotal(Double total){
        this.total = total;
    }

    public Double getGanancia(){
        return ganancia;
    }

    public void setGanancia(Double ganancia){
        this.ganancia = ganancia;
    }


}
