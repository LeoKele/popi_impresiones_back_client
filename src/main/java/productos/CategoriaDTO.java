package productos;

public class CategoriaDTO {
    private Long id;
    private String descripcion;

    public CategoriaDTO(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
