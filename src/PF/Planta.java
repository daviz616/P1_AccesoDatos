package PF;

public class Planta {
    
    private int codigo;
    private float precio;
    private int stock;
    
    public Planta(int codigo, float precio, int stock) {
        this.codigo = codigo;
        this.precio = precio;
        this.stock = stock;
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

    @Override
    public String toString() {
        return "Planta [codigo=" + codigo + ", precio=" + precio + ", stock=" + stock + "]";
    }
}