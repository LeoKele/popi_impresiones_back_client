package productos;

import java.time.LocalDate;

public class Pedido {
    private Long id;
    private LocalDate fechaRecibido;
    private Long idProducto;
    private String descripcion;
    private boolean productoListo;
    private boolean pagado;
    private boolean entregado;
    private String nombreProducto;

    // Constructor completo
    public Pedido(Long id, LocalDate fechaRecibido, Long idProducto, String descripcion,
                  boolean productoListo, boolean pagado, boolean entregado, String nombreProducto) {
        this.id = id;
        this.fechaRecibido = fechaRecibido;
        this.idProducto = idProducto;
        this.descripcion = descripcion;
        this.productoListo = productoListo;
        this.pagado = pagado;
        this.entregado = entregado;
        this.nombreProducto = nombreProducto;
    }

    // Constructor vacío
    public Pedido() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(LocalDate fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean getProductoListo() {
        return productoListo;
    }

    public void setProductoListo(boolean productoListo) {
        this.productoListo = productoListo;
    }

    public boolean getPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public boolean getEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
