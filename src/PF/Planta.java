package PF;

public class Planta {
    
    // CAMPOS DEL .DAT
    private int codigo;
    private float precio;
    private int stock;
    
    // CAMPOS DEL .XML (Añadidos para Punto 4)
    private String nombre;
    private String descripcion;
    
    
    public Planta(int codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = 0.0f; // Se rellenará después
        this.stock = 0;     //
    }

   
    public int getCodigo() {
        return codigo;
    }
    public float getPrecio() {
        return precio;
    }
    public int getStock() {
        return stock;
    }
    public String getNombre() {
        return nombre;
    }

   
    public void setPrecio(float precio) {
        this.precio = precio;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    // toString() 
    @Override
    public String toString() {
        return "Planta [Cód=" + codigo + ", Nombre=" + nombre + ", Precio=" + precio + ", Stock=" + stock + "]";
    }
}