package productos;


//Respuesta que se enviaria al cliente después de que la imagen se haya insertado correctamente a la bbdd.
//Imagen representa los datos que recibimos en el POST.
public class ImagenResponse {
    private long id;
    private long idProducto;
    private String imgPath;

    public ImagenResponse(long id, long idProducto, String imgPath) {
        this.id = id;
        this.idProducto = idProducto;
        this.imgPath = imgPath;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public String getimgPath() {
        return imgPath;
    }

    public void setimgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
