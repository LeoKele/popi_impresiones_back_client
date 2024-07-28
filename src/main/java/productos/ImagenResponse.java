package productos;


//Respuesta que se enviaria al cliente después de que la imagen se haya insertado correctamente a la bbdd.
//Imagen representa los datos que recibimos en el POST.
public class ImagenResponse {
    private long idImagen;
    private long idProducto;
    private String urlImagen;

    public ImagenResponse(long idImagen, long idProducto, String urlImagen) {
        this.idImagen = idImagen;
        this.idProducto = idProducto;
        this.urlImagen = urlImagen;
    }

    // Getters y setters
    public long getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(long idImagen) {
        this.idImagen = idImagen;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
