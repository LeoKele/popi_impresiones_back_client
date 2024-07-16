package productos;

public class Imagen {
    private long id;
    private Long idProducto;
    private String img_path;


    public Imagen(){};

    public Imagen(long id, Long idProducto, String img_path){
        this.id = id;
        this.idProducto = idProducto;
        this.img_path = img_path;
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

    public String getImgPath(){
        return img_path;
    }
    public void setImgPath(String img_path){
        this.img_path = img_path;
    }

}

