package PF;

public class LineaVenta {

    private Planta planta;
    private int cantidad;

    public LineaVenta(Planta planta, int cantidad) {
        this.planta = planta;
        this.cantidad = cantidad;
    }

    public Planta getPlanta() {
        return planta;
    }

    public int getCantidad() {
        return cantidad;
    }
    
    // Método para calcular el total de esta línea
    public float getSubtotal() {
        return planta.getPrecio() * cantidad;
    }
}