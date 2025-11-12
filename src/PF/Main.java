package PF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner; // AÑADIDO (para Punto 3)
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	// --- ArrayLists (Punto 2.2 y 2.4) ---
	private static ArrayList<Planta> listaPlantas = new ArrayList<>();
	private static ArrayList<Empleado> listaEmpleados = new ArrayList<>();
	
	//(para Punto 3)
	private static Scanner sc = new Scanner(System.in);

	
	public static float numeroAleatorioPrecio() {
		  
		        float numero = ThreadLocalRandom.current().nextFloat(1, 500);
		        return Math.round(numero * 100f) / 100f; // redondea a 2 decimales
		    
	}
	public static int numeroAleatorioStock() {
		  return ThreadLocalRandom.current().nextInt(1, 501); 
	}
	
	
	public static void EscribirFichero () {
	    
        try (RandomAccessFile raf = new RandomAccessFile("plantas/plantas.dat", "rw")) {
            for(int i=1;i<21;i++) {
            	int codigo = i; //código de la planta
            	float numero= numeroAleatorioPrecio();
      	        int numero1= numeroAleatorioStock();

      	        // AHORA SÍ una mejor salida, que no se entendia nada 9/11.
      	        System.out.println("Generando Planta Cód: " + codigo + " (Precio: " + numero + ", Stock: " + numero1 + ")");
            	
      	        // Escribimos en el fichero
      	        raf.writeInt(codigo);
            	raf.writeFloat(numero);
            	raf.writeInt(numero1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
}
	
	// (para Punto 3.1)
	public static void EscribirEmpleado(){
		ArrayList <Empleado> ListaEmpleados = new ArrayList <>();
		
		try (FileOutputStream FicheroEscritura = new FileOutputStream("empleados/empleados.dat");
	             ObjectOutputStream escritura = new ObjectOutputStream(FicheroEscritura)) {

	            // CAMBIADO: IDs ahora son Strings (texto)
	            Empleado empleado1 = new Empleado("1452","Teresa","asb123","vendedor");
	            Empleado empleado2 = new Empleado("0234","Miguel Angel","123qwe","vendedor"); 
	            Empleado empleado3 = new Empleado("7532","Natalia","xs21qw4","gestor");
	            
	            ListaEmpleados.add(empleado1);
	            ListaEmpleados.add(empleado2);
	            ListaEmpleados.add(empleado3);
	            
	            escritura.writeObject(ListaEmpleados);
	            
	          
	            System.out.println("Objetos escritos correctamente en empleados/empleados.dat");

	        } catch (IOException i) {
	            i.printStackTrace();
	        }
	}

	// --- PUNTO 1 ---
	
	public static boolean comprobarEntorno() {
		System.out.println("--- 1. Comprobación del Directorio de Archivos ---");
		
		new File("plantas").mkdirs();
		new File("empleados/baja").mkdirs();
		new File("tickets").mkdirs();
		new File("devoluciones").mkdirs();
		
		File xml = new File("plantas/plantas.xml");
		if (!xml.exists()) {
			System.err.println("1.5 ERROR CRÍTICO: No se encuentra 'plantas/plantas.xml'.");
			return false;
		}
		
		File pDat = new File("plantas/plantas.dat");
		File eDat = new File("empleados/empleados.dat");
		
		System.out.println("1.4: Directorios OK.");
		System.out.println("1.1: 'plantas.xml' OK.");
		if (!pDat.exists()) System.out.println("1.2: 'plantas/plantas.dat' no existe (se creará).");
		if (!eDat.exists()) System.out.println("1.3: 'empleados/empleados.dat' no existe (se creará).");
		
		return true;
	}

	// --- PUNTO 2 ---
	
	public static boolean cargarPlantas() {
		int posicion=0;
		try (RandomAccessFile raf = new RandomAccessFile("plantas/plantas.dat", "r")) {
			long size = raf.length();
			while (posicion < size) {
			        int codigo = raf.readInt();
			        float precio = raf.readFloat();
			        int stock = raf.readInt();
			        
			        listaPlantas.add(new Planta(codigo, precio, stock));
			        
			        // Avanzar la posición según los bytes leídos (4 + 4 + 4 = 12 bytes)
			        posicion += 12;
			    }
			System.out.println("2.1/2.2: Cargadas " + listaPlantas.size() + " plantas.");
			return true;
		}catch (IOException e) {
            System.err.println("2.5 ERROR al cargar 'plantas.dat': " + e.getMessage());
			return false;
        }
	}
	
	@SuppressWarnings("unchecked")
	public static boolean cargarEmpleados() {
        try (FileInputStream ficherolectura = new FileInputStream("empleados/empleados.dat");
             ObjectInputStream lectura = new ObjectInputStream(ficherolectura)) {

            listaEmpleados = (ArrayList<Empleado>) lectura.readObject();
            
            System.out.println("2.3/2.4: Cargados " + listaEmpleados.size() + " empleados.");
            System.out.println("Objetos leídos correctamente desde empleados.dat"); 
            return true;

        } catch (IOException | ClassNotFoundException e) {
        	System.err.println("2.5 ERROR al cargar 'empleados.dat': " + e.getMessage());
            return false;
        }
    }
	
	public static boolean cargarDatos() {
		System.out.println("\n--- 2. Carga de Datos ---");
		
		boolean todoOk = cargarPlantas() && cargarEmpleados();
		
		if (todoOk) {
			System.out.println("[ÉXITO] Carga de datos completada.");
		} else {
			System.err.println("[ERROR] La carga de datos falló. La aplicación se detendrá.");
		}
		return todoOk;
	}
	
	// --- AÑADIDO: PUNTO 3 ---
	
	/**
	 * 3.1 Comprobación de usuario y contraseña
	 * 3.4 Relleno de códigos
	 * 3.2 Mostrar menu (simulado)
	 * 3.3 Control de errores
	 */
	public static void identificacionUsuario() {
		System.out.println("\n--- 3. Identificación de Usuario ---");
		
		System.out.print("Introduce tu identificación (ID): ");
		String idBuscado = sc.next();
		
		System.out.print("Introduce tu contraseña: ");
		String passBuscado = sc.next();
		
		// --- Punto 3.4: ---
		// Si el usuario escribe "234", se convierte en "0234"
		while (idBuscado.length() < 4) {
			idBuscado = "0" + idBuscado;
		}
		System.out.println("(ID comprobado: " + idBuscado + ")");
		
		
		boolean loginExitoso = false;
		
		// 3.1 Comprobar usuario y contraseña
		for (Empleado emp : listaEmpleados) {
			
			if (emp.getIdentificacion().equals(idBuscado) && emp.getPassword().equals(passBuscado)) {
				loginExitoso = true;
				
				// 3.2 Mostrar menu según el cargo
				System.out.println("\n¡Bienvenido, " + emp.getNombre() + "!");
				
				if (emp.getCargo().equalsIgnoreCase("gestor")) {
					System.out.println("Cargando Menú de Gestor...");
					// Aquí llamaríamos a menuGestor(emp);
				} else if (emp.getCargo().equalsIgnoreCase("vendedor")) {
					System.out.println("Cargando Menú de Vendedor...");
					// Aquí llamaríamos a menuVendedor(emp);
				} else {
					// 3.3 Control de errores (cargo)
					System.err.println("3.3 ERROR: Cargo '" + emp.getCargo() + "' no reconocido.");
				}
				
				break; 
			}
		}
		
		// 3.3 Control de errores (usuario no existe)
		if (!loginExitoso) {
			System.err.println("3.3 ERROR: Identificación o contraseña incorrectos.");
		}
	}
	
	
	public static void main(String[] args) {
		
		// --- PUNTO 1 ---
		if (comprobarEntorno()) {
			
			System.out.println("\n--- Inicializando Datos (esto significa que no están creados) ---");

	        File fPlantasDat = new File("plantas/plantas.dat");
	        if (!fPlantasDat.exists()) {
	        	System.out.println("Creando plantas/plantas.dat...");
	        	EscribirFichero();
	        }
	        
	        File fEmpleadosDat = new File("empleados/empleados.dat");
	        if (!fEmpleadosDat.exists()) {
	        	System.out.println("Creando empleados/empleados.dat...");
	        	EscribirEmpleado();
	        }
	        
	        // --- PUNTO 2 ---
	        if (cargarDatos()) {
	        	System.out.println("\nSistema listo.");
	        	
	        	// --- PUNTO 3 ---
	        	identificacionUsuario();
	        	
	        } else {
	        	System.out.println("La aplicación se detiene debido a errores de carga.");
	        }
			
		} else {
			System.out.println("La aplicación se detiene debido a errores de entorno.");
		}
	}
}