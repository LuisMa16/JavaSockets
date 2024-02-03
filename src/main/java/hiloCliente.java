import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class hiloCliente extends Thread {
	private Socket clientSocket;
	private int id;
	private int posicion;
	PrintWriter out;
	boolean continuar = true;
	ArrayList<String> caracteristicasCliente;
	
	public hiloCliente(Socket clientSocket, int id) {
		this.clientSocket = clientSocket;
		this.id = id;
	}
	
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	
	public void mandarIp(String ip) {
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println(ip);
			continuar = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run(){
		servidor mensajero = new servidor();
		try (ObjectInputStream datosEntrada = new ObjectInputStream(clientSocket.getInputStream());
			    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
			    
			    out.println("Conectado al servidor con exito");
			    ObjectOutputStream lugar = new ObjectOutputStream(clientSocket.getOutputStream());  

			    while (continuar) {
			    	lugar.writeObject(posicion);
			    	
			        try {
			            caracteristicasCliente = (ArrayList<String>) datosEntrada.readObject();
			            if (caracteristicasCliente.get(8).equals("1")) {
			            	servidor servidor = new servidor();
							servidor.recibirSolicitud();
			            }
			            float calificacion = puntuacion(caracteristicasCliente);
			            mensajero.actualizarTabla(caracteristicasCliente, id);
			            mensajero.establecerRanking(calificacion, id);
			            if (posicion == 1) {
			            	servidor servidor = new servidor();
							servidor.setIdServidor(id);
			            }
			            Thread.sleep(300); 
			        } catch (ClassNotFoundException e) {
			            System.out.println("Clase no encontrada: " + e.getMessage());
			            continuar = false;
			        } catch (InterruptedException e) {
			            System.out.println("Hilo interrumpido: " + e.getMessage());
			            continuar = false;
			        } catch (IOException e) {
			            //System.out.println("Se ha desconectado el cliente: " + e.getMessage());
			            mensajero.establecerEstado(id);
			            clientSocket.close();
			            continuar = false;
			        }
			    }
			} catch (IOException e) {
			    System.out.println("Se ha desconectado el cliente: " + e.getMessage());
			    mensajero.establecerEstado(id);
			    try {
					clientSocket.close();
				} catch (IOException e1) {
				}
	            continuar = false;
			}
	}
	
	public float puntuacion(ArrayList<String> car) {
		float calificacion;
		Double UsoProcesador = Double.parseDouble(car.get(6));
		float velProcesador = Float.parseFloat(car.get(0));
		int nucleos = Integer.parseInt(car.get(2));
		float espacioLibre = Float.parseFloat(car.get(4));
		float RAM = Float.parseFloat(car.get(7));
		
		UsoProcesador = (100 - UsoProcesador)*2;
		velProcesador = velProcesador * 0.015f;
		nucleos = nucleos * 2;
		espacioLibre = espacioLibre * 0.01f;
		RAM = RAM * 0.02f;
		calificacion = (float) (UsoProcesador + velProcesador + nucleos + espacioLibre + RAM);
		return calificacion;
	}
}