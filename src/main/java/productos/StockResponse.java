package productos;

public class StockResponse {
    private long idStock;
    private Long idProducto;
    private Long cantidad;

    public StockResponse() {}

    public StockResponse(long idStock, Long idProducto, Long cantidad) {
        this.idStock = idStock;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public long getIdStock() {
        return idStock;
    }

    public void setIdStock(long idStock) {
        this.idStock = idStock;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
