package PF;

import java.io.BufferedReader; //(para 6.1)
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader; //(para 6.1)
import java.io.FileWriter; 
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter; 
import java.io.RandomAccessFile;
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.InputMismatchException; // (Punto 4.3)
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

//IMPORTS PARA LEER XML (necesario para 4.1)
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Main {
	
	// --- ArrayLists (Punto 2.2 y 2.4) ---
	private static ArrayList<Planta> listaPlantas = new ArrayList<>();
	private static ArrayList<Empleado> listaEmpleados = new ArrayList<>();
	
	// (para Punto 8.2)
	private static ArrayList<Empleado> listaEmpleadosBaja = new ArrayList<>();
	
	//(Para Punto 3)
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
	
	
	public static void EscribirEmpleado(){
		ArrayList <Empleado> ListaEmpleados = new ArrayList <>();
		
		try (FileOutputStream FicheroEscritura = new FileOutputStream("empleados/empleados.dat");
	             ObjectOutputStream escritura = new ObjectOutputStream(FicheroEscritura)) {
	 
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

	// --- PUNTO 2 (Modificado para Punto 4) ---
	
	public static boolean cargarCatalogoXML() {
		// Limpiamos la lista por si acaso
		listaPlantas.clear();
		try {
			File xmlFile = new File("plantas/plantas.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("planta");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					int codigo = Integer.parseInt(eElement.getElementsByTagName("codigo").item(0).getTextContent());
					String nombre = eElement.getElementsByTagName("nombre").item(0).getTextContent();
					String desc = eElement.getElementsByTagName("descripcion").item(0).getTextContent();
					
					listaPlantas.add(new Planta(codigo, nombre, desc));
				}
			}
			System.out.println("2.1/2.2: Cargadas " + listaPlantas.size() + " plantas (desde XML).");
			return true;
		} catch (Exception e) {
			System.err.println("2.5 ERROR al cargar 'plantas.xml': " + e.getMessage());
			return false;
		}
	}
	
	public static boolean cargarPreciosStockDAT() {
		try (RandomAccessFile raf = new RandomAccessFile("plantas/plantas.dat", "r")) {
			int posicion=0;
			long size = raf.length();
			
			while (posicion < size) {
			        int codigo = raf.readInt();
			        float precio = raf.readFloat();
			        int stock = raf.readInt();
			        
			        // Buscar la planta en la lista y ponerle el precio y stock
			        for (Planta p : listaPlantas) {
			        	if (p.getCodigo() == codigo) {
			        		p.setPrecio(precio);
			        		p.setStock(stock);
			        		break;
			        	}
			        }
			        posicion += 12; // Avanzar la posición
			    }
			System.out.println("2.1/2.2: Precios y Stock actualizados (desde .dat).");
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
	
	/**
	 * (Punto 8.2.3.1)
	 */
	@SuppressWarnings("unchecked")
	public static boolean cargarEmpleadosBaja() {
		File f = new File("empleados/baja/empleadosBaja.dat");
		// Si el fichero de bajas no existe, no es un error, solo no cargamos nada
		if (!f.exists()) {
			System.out.println("No se encontró 'empleadosBaja.dat' (normal si no hay bajas).");
			return true;
		}
		
        try (FileInputStream ficherolectura = new FileInputStream(f);
             ObjectInputStream lectura = new ObjectInputStream(ficherolectura)) {
            listaEmpleadosBaja = (ArrayList<Empleado>) lectura.readObject();
            System.out.println("Cargados " + listaEmpleadosBaja.size() + " empleados de baja.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
        	System.err.println("ERROR al cargar 'empleadosBaja.dat': " + e.getMessage());
            return false;
        }
    }
	
	public static boolean cargarDatos() {
		System.out.println("\n--- 2. Carga de Datos ---");
		
		// MODIFICADO (para 8.2)
		boolean todoOk = cargarCatalogoXML() && cargarPreciosStockDAT() && cargarEmpleados() && cargarEmpleadosBaja();
		
		if (todoOk) {
			System.out.println("[ÉXITO] Carga de datos completada.");
		} else {
			System.err.println("[ERROR] La carga de datos falló. La aplicación se detendrá.");
		}
		return todoOk;
	}
	
	// --- PUNTO 3 ---
	
	public static void identificacionUsuario() {
		System.out.println("\n--- 3. Identificación de Usuario ---");
		
		System.out.print("Introduce tu identificación (ID): ");
		String idBuscado = sc.next();
		
		System.out.print("Introduce tu contraseña: ");
		String passBuscado = sc.next();
		
		// --- Punto 3.4 ---
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
					//(Punto 8)
					menuGestor(emp);
				} else if (emp.getCargo().equalsIgnoreCase("vendedor")) {
					// --- LLAMADA AL PUNTO 4 ---
					menuVendedor(emp);
				} else {
					// 3.3 Control de errores (carga)
					System.out.println("3.3 ERROR: Cargo '" + emp.getCargo() + "' no reconocido.");
				}
				
				break; 
			}
		}
		
		// 3.3 Control de errores (usuario no existe)
		if (!loginExitoso) {
			System.out.println("3.3 ERROR: Identificación o contraseña incorrectos.");
		}
	}
	
	// --- PUNTO 4 (MODIFICADO para Punto 7) ---
	
	public static void menuVendedor(Empleado vendedor) {
		boolean salir = false;
		while (!salir) {
			System.out.println("\n--- Menú Vendedor (" + vendedor.getNombre() + ") ---");
			System.out.println("1. Listar Catálogo (Unificado)");
			System.out.println("2. Realizar Venta");
			System.out.println("3. Realizar Devolución"); //(Punto 6)
			System.out.println("4. Buscar Ticket"); //(Punto 7)
			System.out.println("0. Cerrar Sesión");
			
			// 4.3 (Validación de número)
			int opcion = leerNumeroValidado("Elige una opción: ");
			
			switch(opcion) {
				case 1:
					listarCatalogo(vendedor);
					break;
				case 2:
					realizarVenta(vendedor); 
					break;
				case 3:
					//(Punto 6)
					realizarDevolucion();
					break;
				case 4:
					//(Punto 7)
					buscarTicket();
					break;
				case 0:
					salir = true;
					System.out.println("Cerrando sesión...");
					break;
				default:
					System.err.println("Opción no válida.");
			}
		}
	}

	public static void listarCatalogo(Empleado vendedor) {
		System.out.println("\n--- 4.1 Catálogo de Plantas (Unificado) ---");
		
		// Imprimimos catálogo unificado
		for (Planta p : listaPlantas) {
			
			System.out.printf("Cód: %-3d | Nombre: %-12s | Precio: %6.2f€ | Stock: %d%n",
					p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
		}
		
		// 4.2 Redirigir a venta
		System.out.print("\n¿Deseas comprar? (s/n): ");
		String resp = sc.next();
		if (resp.equalsIgnoreCase("s")) {
			realizarVenta(vendedor);
		}
	}

	// --- PUNTO 5  ---

	public static void realizarVenta(Empleado vendedor) {
		//"cesta"
		ArrayList<LineaVenta> cesta = new ArrayList<>();
		boolean seguirComprando = true;

		System.out.println("\n--- 5. Realizar Venta ---");
		
		// 5.0 añadir productos
		while (seguirComprando) {
			
			// 5.1 Introducir datos (uso 0 para salir)
			int cod = leerNumeroValidado("Introduce Cód. Planta (0 para finalizar): ");
			if (cod == 0) {
				seguirComprando = false;
				continue;
			}
			
			// Busco la planta en la lista (para nombre y precio)
			Planta p = buscarPlantaEnLista(cod);
			if (p == null) {
				System.err.println("Error: Código de planta no existe.");
				continue;
			}
			
			int cant = leerNumeroValidado("Introduce cantidad: ");
			if (cant == 0) {
				System.out.println("Cantidad 0, no se añade.");
				continue;
			}
			
			// 5.2 Comprobar el stock
			// 5.2.1 Lectura del campo stock (fichero acceso directo)
			int stockActual = getStockPlanta(cod);
			
			if (stockActual == -1) {
				System.err.println("Error fatal al leer el stock de plantas.dat.");
				continue;
			}
			
			if (stockActual >= cant) {
				cesta.add(new LineaVenta(p, cant));
				System.out.println("-> Añadido: " + cant + "x " + p.getNombre());
			} else {
				System.err.println("Error: Stock insuficiente. Stock actual: " + stockActual);
			}
		} // Fin del bucle 5.0
		
		
		// Si la cesta no está vacía, mostramos el resumen
		if (!cesta.isEmpty()) {
			// 5.3 Mostrar resumen compra antes finalizar
			mostrarResumen(cesta, vendedor);
			
			// 5.4 Aceptar venta
			System.out.print("\n¿Confirmar la compra? (s/n): ");
			String resp = sc.next();
			
			if (resp.equalsIgnoreCase("s")) {
				// 5.4.1 Modificación del fichero de acceso directo (y en el temporal)
				boolean stockOk = actualizarStockFicheroYLista(cesta, false); // false = restar
				
				if (stockOk) {
					// 5.5 Generar ticket
					generarTicket(cesta, vendedor);
					System.out.println("¡Venta realizada con éxito!");
				} else {
					System.err.println("Error fatal al actualizar el stock. Venta cancelada.");
				}
				
			} else {
				System.out.println("Venta cancelada por el usuario.");
			}
			
		} else {
			System.out.println("Venta cancelada (cesta vacía).");
		}
	}

	public static Planta buscarPlantaEnLista(int codigo) {
		for (Planta p : listaPlantas) {
			if (p.getCodigo() == codigo) {
				return p;
			}
		}
		return null; // No se encontró
	}
	
	public static int getStockPlanta(int codigo) {
		// El registro es: int (4) + float (4) + int (4) = 12 bytes
		// El stock empieza en el byte 8 (4+4)
		long posInicioRegistro = (long)(codigo - 1) * 12;
		long posStock = posInicioRegistro + 8;
		
		try (RandomAccessFile raf = new RandomAccessFile("plantas/plantas.dat", "r")) {
			
			if (posStock >= raf.length()) {
				System.err.println("Error getStock: Código no encontrado en .dat");
				return -1; //Código no existe
			}
			
			raf.seek(posStock);
			return raf.readInt();
			
		} catch (IOException e) {
			e.printStackTrace();
			return -1; // Error
		}
	}
	
	public static void mostrarResumen(ArrayList<LineaVenta> cesta, Empleado vendedor) {
		System.out.println("\n--- 5.3 Resumen de Compra ---");
		System.out.println("Empleado que atiende: " + vendedor.getNombre());
		System.out.println("---------------------------------");
		
		float total = 0;
		
		for (LineaVenta linea : cesta) {
			Planta p = linea.getPlanta();
			float subtotal = linea.getSubtotal();
			System.out.printf("Producto: %-12s | %d uds x %.2f€ = %.2f€%n",
					p.getNombre(),
					linea.getCantidad(),
					p.getPrecio(),
					subtotal);
			total += subtotal;
		}
		
		System.out.println("---------------------------------");
		// 5.4.2 Calcular el total
		System.out.printf("TOTAL A PAGAR: %.2f€%n", total);
	}

	public static int leerNumeroValidado(String mensaje) {
		while(true) {
			System.out.print(mensaje);
			try {
				int num = sc.nextInt();
				if (num >= 0) { //0 = opción de salir del menú
					return num;
				} else {
					System.err.println("Error: El número no puede ser negativo.");
				}
			} catch (InputMismatchException e) {
				// Error "texto"
				System.err.println("Error: Debes introducir un número válido.");
				sc.next(); 
			}
		}
	}
	
	
	/**
	 * 5.4.1 Modificación de stock (Cambiado para 6.4)
	 */
	public static boolean actualizarStockFicheroYLista(ArrayList<LineaVenta> cesta, boolean sumar) {
		try (RandomAccessFile raf = new RandomAccessFile("plantas/plantas.dat", "rw")) {
			
			for (LineaVenta linea : cesta) {
				int codigo = linea.getPlanta().getCodigo();
				int cantMovida = linea.getCantidad();
				
				long posStock = (long)(codigo - 1) * 12 + 8;
				
				// Lee el stock actual
				raf.seek(posStock);
				int stockActual = raf.readInt();
				
				int nuevoStock;
				if (sumar) {
					nuevoStock = stockActual + cantMovida; // Sumar para devolución
				} else {
					nuevoStock = stockActual - cantMovida; // Restar para venta
				}
				
				// Escribe el nuevo stock en el .dat
				raf.seek(posStock);
				raf.writeInt(nuevoStock);
				
				// Actualizamos también la lista temporal (listaPlantas)
				Planta plantaEnLista = buscarPlantaEnLista(codigo);
				if (plantaEnLista != null) {
					plantaEnLista.setStock(nuevoStock);
				}
			}
			
			if (sumar) {
				System.out.println("Stock (devolución) actualizado en 'plantas.dat' y en memoria.");
			} else {
				System.out.println("Stock (venta) actualizado en 'plantas.dat' y en memoria.");
			}
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getSiguienteNumeroTicket() {
		File dirTickets = new File("tickets");
		File[] tickets = dirTickets.listFiles();
		int maxNum = 0;
		
		if (tickets != null) {
			for (File ticket : tickets) {
				try {
					String nombre = ticket.getName().replace(".txt", "");
					int num = Integer.parseInt(nombre);
					if (num > maxNum) {
						maxNum = num;
					}
				} catch (NumberFormatException e) {
					// Ignora archivos que no sean números
				}
			}
		}
		//Comprobar también en devoluciones
		File dirDevoluciones = new File("devoluciones");
		File[] ticketsDevueltos = dirDevoluciones.listFiles();
		if (ticketsDevueltos != null) {
			for (File ticket : ticketsDevueltos) {
				try {
					String nombre = ticket.getName().replace(".txt", "");
					int num = Integer.parseInt(nombre);
					if (num > maxNum) {
						maxNum = num;
					}
				} catch (NumberFormatException e) {}
			}
		}
		
		return maxNum + 1;
	}
	
	public static void generarTicket(ArrayList<LineaVenta> cesta, Empleado vendedor) {
		int numTicket = getSiguienteNumeroTicket();
		String nombreFichero = "tickets/" + numTicket + ".txt";
		
		try (PrintWriter pw = new PrintWriter(new FileWriter(nombreFichero))) {
			
			pw.println("Número Ticket: " + numTicket);
			pw.println("——————————————//———————————------------------------");
			pw.println("Empleado que ha atendido: " + vendedor.getIdentificacion());
			pw.println("Nombre del empleado: " + vendedor.getNombre());
			pw.println("Fecha de venta: " + LocalDate.now());
			pw.println("-------------------------------------------------");
			pw.println("CodigoProducto Cantidad PrecioUnitario Subtotal");
			pw.println("-------------------------------------------------");
			
			float total = 0;
			for (LineaVenta linea : cesta) {
				Planta p = linea.getPlanta();
				float subtotal = linea.getSubtotal();
				
				pw.printf("%-14d %-8d %-13.2f %.2f%n", 
						p.getCodigo(), 
						linea.getCantidad(), 
						p.getPrecio(), 
						subtotal);
				
				total += subtotal;
			}
			
			pw.println("——————————————//———————————------------------------");
			pw.printf("Total: %.2f €%n", total);
			
			System.out.println("Ticket " + numTicket + ".txt generado correctamente.");
			
		} catch (IOException e) {
			System.err.println("Error al generar el ticket: " + e.getMessage());
		}
	}
	
	
	// --- PUNTO 6  ---

		public static ArrayList<LineaVenta> leerProductosDeTicket(File fTicket) {
			ArrayList<LineaVenta> productos = new ArrayList<>();
			boolean zonaProductos = false;
			
			try (BufferedReader br = new BufferedReader(new FileReader(fTicket))) {
				String linea;
				while ((linea = br.readLine()) != null) {
					
					if (linea.startsWith("-----------------")) {
						zonaProductos = !zonaProductos; 
						continue; 
					}
					
					if (zonaProductos && !linea.startsWith("CodigoProducto")) {
						try {
							String[] partes = linea.trim().split("\\s+");
							int cod = Integer.parseInt(partes[0]);
							int cant = Integer.parseInt(partes[1]);
							
							Planta p = buscarPlantaEnLista(cod);
							if (p != null) {
								productos.add(new LineaVenta(p, cant));
							}
						} catch (Exception e) {
							System.err.println("Error al leer línea de producto: " + linea);
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Error leyendo ticket para devolución: " + e.getMessage());
			}
			return productos;
		}
		
		public static void realizarDevolucion() {
			System.out.println("\n--- 6. Realizar Devolución ---");
			
			// 6.1 Buscar el ticket
			int numTicket = leerNumeroValidado("Introduce el Nº de Ticket a devolver: ");
			String nombreFichero = numTicket + ".txt";
			
			File fTicket = new File("tickets/" + nombreFichero);
			File fDevolucion = new File("devoluciones/" + nombreFichero);

			if (fDevolucion.exists()) {
				System.err.println("Error: El ticket " + numTicket + " ya ha sido devuelto.");
				return;
			}
			if (!fTicket.exists()) {
				System.err.println("Error: El ticket " + numTicket + " no se encuentra en la carpeta 'tickets'.");
				return;
			}
			
			// --- 1ª LECTURA: Sacar los productos --- (Punto 6.4)
			ArrayList<LineaVenta> productosDevueltos = leerProductosDeTicket(fTicket);

			// 6.4 Modificar el stock
			if (!productosDevueltos.isEmpty()) {
				actualizarStockFicheroYLista(productosDevueltos, true); // true = sumar
			} else {
				System.err.println("Error: No se encontraron productos en el ticket.");
				return;
			}
			
			// --- 2ª LECTURA: Copiar, modificar total y mover ---
			try (BufferedReader br = new BufferedReader(new FileReader(fTicket));
				 PrintWriter pw = new PrintWriter(new FileWriter(fDevolucion))) {
				
				String linea;
				while ((linea = br.readLine()) != null) {
					
					if (linea.startsWith("Total:")) {
						// 6.3 Leer total y ponerlo en negativo
						try {
							String[] partesTotal = linea.split(" ");
							float total = Float.parseFloat(partesTotal[1]);
							pw.println(String.format("Total: %.2f €", -Math.abs(total)));
						} catch (Exception e) {
							pw.println(linea); // Si falla, copia la línea original
						}
					} else {
						pw.println(linea); // Copia la línea tal cual
					}
				}
				
				// 6.2 Escribir -- DEVOLUCIÓN--
				pw.println("-- DEVOLUCIÓN--");
				
			} catch (IOException e) {
				System.err.println("Error al re-escribir el ticket de devolución: " + e.getMessage());
				return;
			}
			
			// 6.4 Mover el ticket (borrando el viejo)
			fTicket.delete();
			System.out.println("Devolución completada. Ticket " + numTicket + " movido a 'devoluciones'.");
		}
	
	// --- PUNTO 7 ---
	
	/**
	 * 7.1 Leer el fichero de caracteres indicado por teclado
	 */
	public static void buscarTicket() {
		System.out.println("\n--- 7. Búsqueda de Tickets ---");
		int numTicket = leerNumeroValidado("Introduce el Nº de Ticket a buscar: ");
		String nombreFichero = numTicket + ".txt";
		
		File fTicket = new File("tickets/" + nombreFichero);
		File fDevolucion = new File("devoluciones/" + nombreFichero);
		File fParaLeer = null;
		
		if (fTicket.exists()) {
			fParaLeer = fTicket;
			System.out.println("--- Mostrando Ticket " + numTicket + " (Venta) ---");
		} else if (fDevolucion.exists()) {
			fParaLeer = fDevolucion;
			System.out.println("--- Mostrando Ticket " + numTicket + " (Devolución) ---");
		} else {
			System.err.println("Error: El ticket " + numTicket + " no se encuentra.");
			return;
		}
		
		// Si encontramos el archivo, lo leemos y lo mostramos
		try (BufferedReader br = new BufferedReader(new FileReader(fParaLeer))) {
			String linea;
			System.out.println(""); // Línea en blanco para separar
			while ((linea = br.readLine()) != null) {
				System.out.println(linea);
			}
		} catch (IOException e) {
			System.err.println("Error al leer el archivo del ticket: " + e.getMessage());
		}
	}
	
	// ---PUNTO 8 (Solo 8.2 Gestión Empleados) ---
	
	/**
	 * 8. Menu de Gestores
	 */
	public static void menuGestor(Empleado gestor) {
		boolean salir = false;
		while (!salir) {
			System.out.println("\n--- Menú Gestor (" + gestor.getNombre() + ") ---");
			System.out.println("1. Gestión de Plantas");
			System.out.println("2. Gestión de Empleados");
			System.out.println("3. Calcular Estadísticas");
			System.out.println("0. Cerrar Sesión");
			
			int opcion = leerNumeroValidado("Elige una opción: ");
			
			switch(opcion) {
				case 1:
					System.out.println("... (Punto 8.1: Gestión de Plantas - PENDIENTE) ...");
					break;
				case 2:
					// (Punto 8.2)
					gestionEmpleados(); 
					break;
				case 3:
					System.out.println("... (Punto 8.3: Estadísticas - PENDIENTE) ...");
					break;
				case 0:
					salir = true;
					System.out.println("Cerrando sesión...");
					break;
				default:
					System.err.println("Opción no válida.");
			}
		}
	}
	
	/**
	 * 8.2 Submenú de Gestión Empleados
	 */
	public static void gestionEmpleados() {
		boolean salir = false;
		while (!salir) {
			System.out.println("\n--- 8.2 Gestión de Empleados ---");
			System.out.println("1. Dar de Alta Empleado");
			System.out.println("2. Dar de Baja Empleado");
			System.out.println("3. Rescatar Empleado");
			System.out.println("0. Volver al Menú Gestor");
			
			int opcion = leerNumeroValidado("Elige una opción: ");
			
			switch(opcion) {
				case 1:
					altaEmpleado();
					break;
				case 2:
					bajaEmpleado();
					break;
				case 3:
					rescatarEmpleado();
					break;
				case 0:
					salir = true;
					break;
				default:
					System.err.println("Opción no válida.");
			}
		}
	}
	
	/**
	 * 8.2.1 Dar de alta empleado
	 */
	public static void altaEmpleado() {
		System.out.println("\n--- 8.2.1 Alta Empleado ---");
		
		// 8.2.1.2 Control de errores
		System.out.print("Introduce ID (4 dígitos): ");
		String id = sc.next();
		while (id.length() != 4 || !id.matches("\\d+")) {
			System.err.println("Error: El ID debe tener 4 números.");
			System.out.print("Introduce ID (4 dígitos): ");
			id = sc.next();
		}
		
		System.out.print("Introduce Nombre: ");
		String nombre = sc.next(); 
		
		System.out.print("Introduce Contraseña: ");
		String pass = sc.next();
		
		System.out.print("Introduce Cargo (vendedor/gestor): ");
		String cargo = sc.next().toLowerCase();
		while (!cargo.equals("vendedor") && !cargo.equals("gestor")) {
			System.err.println("Error: El cargo debe ser 'vendedor' o 'gestor'.");
			System.out.print("Introduce Cargo (vendedor/gestor): ");
			cargo = sc.next().toLowerCase();
		}
		
		// 8.2.1.1 Añadir empleado al ArrayList
		Empleado nuevoEmp = new Empleado(id, nombre, pass, cargo);
		listaEmpleados.add(nuevoEmp);
		
		// 8.2.1.3 Escribir al empleado en el fichero
		guardarListaEmpleados();
		System.out.println("Empleado " + nombre + " dado de alta.");
	}
	
	/**
	 * 8.2.2 Dar de baja Empleado
	 */
	public static void bajaEmpleado() {
		System.out.println("\n--- 8.2.2 Baja Empleado ---");
		System.out.print("Introduce ID del empleado a dar de baja: ");
		String idBaja = sc.next();
		idBaja = String.format("%4s", idBaja).replace(' ', '0'); 
		
		Empleado empEncontrado = null;
		for (Empleado emp : listaEmpleados) {
			if (emp.getIdentificacion().equals(idBaja)) {
				empEncontrado = emp;
				break;
			}
		}
		
		if (empEncontrado != null) {
			// 8.2.2.1 Mover
			listaEmpleados.remove(empEncontrado);
			listaEmpleadosBaja.add(empEncontrado);
			
			// 8.2.2.2 y 8.2.2.3 Sobreescribir ficheros
			guardarListaEmpleados();
			guardarListaEmpleadosBaja();
			System.out.println("Empleado " + empEncontrado.getNombre() + " dado de baja.");
		} else {
			System.err.println("Error: No se encontró un empleado activo con ese ID.");
		}
	}
	
	/**
	 * 8.2.3 Rescatar empleado
	 */
	public static void rescatarEmpleado() {
		System.out.println("\n--- 8.2.3 Rescatar Empleado ---");
		System.out.print("Introduce ID del empleado a rescatar (de la baja): ");
		String idRescate = sc.next();
		idRescate = String.format("%4s", idRescate).replace(' ', '0');
		
		// 8.2.3.1 (Ya están cargados en listaEmpleadosBaja)
		Empleado empEncontrado = null;
		for (Empleado emp : listaEmpleadosBaja) {
			if (emp.getIdentificacion().equals(idRescate)) {
				empEncontrado = emp;
				break;
			}
		}
		
		if (empEncontrado != null) {
			// 8.2.3.2 Mover
			listaEmpleadosBaja.remove(empEncontrado);
			listaEmpleados.add(empEncontrado);
			
			// 8.2.3.3 y 8.2.3.4 Sobreescribir ficheros
			guardarListaEmpleadosBaja();
			guardarListaEmpleados();
			System.out.println("Empleado " + empEncontrado.getNombre() + " rescatado.");
		} else {
			System.err.println("Error: No se encontró un empleado de baja con ese ID.");
		}
	}
	
	/**
	 * Helper para 8.2 Sobrescribe empleados.dat
	 */
	public static void guardarListaEmpleados() {
		try (FileOutputStream FicheroEscritura = new FileOutputStream("empleados/empleados.dat");
	             ObjectOutputStream escritura = new ObjectOutputStream(FicheroEscritura)) {
			
			escritura.writeObject(listaEmpleados);
			System.out.println("(Info: Fichero 'empleados.dat' actualizado.)");
			
		} catch (IOException e) {
			System.err.println("ERROR al guardar 'empleados.dat': " + e.getMessage());
		}
	}
	
	/**
	 * Helper para 8.2 Sobrescribe empleadosBaja.dat
	 */
	public static void guardarListaEmpleadosBaja() {
		try (FileOutputStream FicheroEscritura = new FileOutputStream("empleados/baja/empleadosBaja.dat");
	             ObjectOutputStream escritura = new ObjectOutputStream(FicheroEscritura)) {
			
			escritura.writeObject(listaEmpleadosBaja);
			System.out.println("(Info: Fichero 'empleadosBaja.dat' actualizado.)");
			
		} catch (IOException e) {
			System.err.println("ERROR al guardar 'empleadosBaja.dat': " + e.getMessage());
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