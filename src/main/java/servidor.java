import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class servidor {
	static especificaciones mensajero = new especificaciones();
	static double pp;
	static long RAM;
	static DefaultTableModel modeloTabla;
	static ArrayList<Float> calificaciones = new ArrayList<Float>();
	static ArrayList<String> caracteristicas = new ArrayList<String>();
	static int contador;
	static int posicionServidor;
	static ServerSocket serverSocket;
	static hiloCliente[] Clientes;
	static Timer timer;
	static Socket clientSocket1;
	static JFrame frame;
	static int id;

    public void serServidor() throws IOException {
    	calificaciones.clear();
    	Clientes = null;
    	
    	contador = 0;
    	caracteristicas = mensajero.getEspecificaciones();
    	
    	frame = new JFrame("Caracteristicas servidor y clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
       
		calificaciones.add(0.0f);
		
        Object[][] datos = {
            {"Procesador", caracteristicas.get(1)},
            {"Velocidad procesador", caracteristicas.get(0)},
            {"Numero de nucleos", caracteristicas.get(2)},
            {"Capacidad del disco duro", caracteristicas.get(3)},
            {"Espacio disponible", caracteristicas.get(4)+ " GB"},
            {"Version del sistema operativo", caracteristicas.get(5)},
            {"Uso del procesador", pp},
            {"Memoria RAM disponible", RAM},
            {"Ranking", "1"},
            {"Estado", "En linea"}
        };
                
        String[] columnas = {"Nombre", "Caracteristicas del servidor"};

        modeloTabla = new DefaultTableModel(datos, columnas);
        
        JTable tabla = new JTable(modeloTabla);
                
        JScrollPane scrollPane = new JScrollPane(tabla);

        frame.add(scrollPane);
        
        frame.setVisible(true);
    	
        serverSocket = new ServerSocket(9999); 
       
        Timer();
        Clientes = new hiloCliente[4];
        contador = 0;
        while (true) {
            System.out.println("Esperando conexiones de cliente...");
            clientSocket1 = serverSocket.accept();
            System.out.println("Cliente conectado.");
            calificaciones.add(0.0f);
            Clientes[contador] = new hiloCliente(clientSocket1, contador+1);
            Clientes[contador].start();
            
            modeloTabla.addColumn("Caracteristicas del cliente "+(contador+1));
            modeloTabla.setValueAt("Conectado", 9, modeloTabla.findColumn("Caracteristicas del cliente "+(contador+1)));
            contador = contador + 1;
            modeloTabla.setValueAt(contador + 1, 8, modeloTabla.findColumn("Caracteristicas del cliente "+(contador)));
        }
    }
    
    public static void Timer() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                ejecucionPeriodica();
            }
        };
        timer = new Timer("Timer");
        long delay = 100L; 
        long period = 1000L; 
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private static void ejecucionPeriodica() {
    	anunciarServidor();
        pp=mensajero.getUsoCPU();
        modeloTabla.setValueAt(pp+" %", 6, modeloTabla.findColumn("Caracteristicas del servidor"));
        RAM=mensajero.getMemoriaRAM();
        modeloTabla.setValueAt(RAM+" MB", 7, modeloTabla.findColumn("Caracteristicas del servidor"));
        float calificacion;
		float velProcesador = Float.parseFloat(caracteristicas.get(0));
		int nucleos = Integer.parseInt(caracteristicas.get(2));
		float espacioLibre = Float.parseFloat(caracteristicas.get(4));
		velProcesador = velProcesador * 0.015f;
		nucleos = nucleos * 2;
		espacioLibre = espacioLibre * 0.01f;
		calificacion = (float) (velProcesador + nucleos + espacioLibre + ((100-pp)*2) + RAM*0.02f) *0.9f;
		
		establecerRanking(calificacion, 0);
    }
    
    public void actualizarTabla(ArrayList<String> Caracteristicas, int id) {
        modeloTabla.setValueAt(Caracteristicas.get(1), 0, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(0), 1, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(2), 2, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(3), 3, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(4)+ "GB", 4, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(5), 5, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(6)+" %", 6, modeloTabla.findColumn("Caracteristicas del cliente "+id));
        modeloTabla.setValueAt(Caracteristicas.get(7)+" MB", 7, modeloTabla.findColumn("Caracteristicas del cliente "+id));
    }
    
    public void establecerEstado(int id) {
    	modeloTabla.setValueAt("Desconectado", 9, modeloTabla.findColumn("Caracteristicas del cliente "+id));
    }
    
    public static void establecerRanking(float calificacion, int id) {
    	calificaciones.set(id, calificacion);
    	//System.out.println(calificaciones);
    	int posicion = 1;
    	for (int i=0; i<calificaciones.size(); i++) {
    		if (i != id) {
    			if (calificaciones.get(i) > calificaciones.get(id)) {
    				posicion = posicion + 1;
    			}
    		}
    	}
    	
    	if (id == 0) {
    		modeloTabla.setValueAt(""+posicion, 8, modeloTabla.findColumn("Caracteristicas del servidor"));
    		posicionServidor = posicion;
    		if (posicionServidor != 1) {
    			cambiarCliente();
    			frame.dispose();
    		}
    	} else {
    		modeloTabla.setValueAt(""+posicion, 8, modeloTabla.findColumn("Caracteristicas del cliente "+id));
    		Clientes[id-1].setPosicion(posicion);
    	}
    }
    public static void cambiarCliente() {
    	String ip = "";
		try {
			Principal p = new Principal();
			ip = p.buscarServidor();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("Cambiando a cliente");
    	timer.cancel();
    	for (int i=0; i<contador; i++) {
    		if (i!=id-1) {
    			Clientes[i].mandarIp("1"+ip);
    		}
    	}
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	Socket clientSocket;
		frame.dispose();
		try {
			clientSocket = new Socket(ip, 9999);
			cliente cliente = new cliente();
			serverSocket.close();
			cliente.serCliente(clientSocket);
		} catch (UnknownHostException e) {
			System.err.println("Error al resolver la dirección IP: " + e.getMessage());
		    e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void setIdServidor(int id) {
		this.id = id;
	}
	
	public static void anunciarServidor() {
		try {
            int puertoAnuncio = 8888; // Puerto para mensajes de anuncio

            DatagramSocket socketAnuncio = new DatagramSocket();
            socketAnuncio.setBroadcast(true);

            String mensajeAnuncio = "Servidor disponible en la red";
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255"); //broadcast

            DatagramPacket packet = new DatagramPacket(mensajeAnuncio.getBytes(), mensajeAnuncio.length(), broadcastAddress, puertoAnuncio);
            socketAnuncio.send(packet);
            socketAnuncio.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void recibirSolicitud() {
		System.out.println("Recibi la solicitud del cliente");
		List<Object> listaDeObjetos = new ArrayList<>();
		for (int i=0; i<100000000; i++) {
			Object objeto = new Object();
            listaDeObjetos.add(objeto);
		}
	}
	
	
}