import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class Principal {

	public static void main(String[] args) throws IOException {
		//empezar como servidor---------------
		servidor servidor = new servidor();
		servidor.serServidor();
		
		//empezar como cliente----------------
		
		/*cliente cliente = new cliente();
		String ipServidor = buscarServidor();
     	
        int servidorPuerto = 9999; 

        Socket clientSocket = new Socket(ipServidor, servidorPuerto);
         
		cliente.serCliente(clientSocket);*/
	}
	
	public static String buscarServidor() throws IOException {
    	int puertoAnuncio = 8888; // Puerto para mensajes de anuncio

        DatagramSocket socketRecepcion = new DatagramSocket(puertoAnuncio);
        byte[] buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socketRecepcion.receive(packet);

        //packet.getAddress() para obtener la dirección del servidor
        System.out.println("Dirección del servidor: " + packet.getAddress().getHostAddress());
        String ip = packet.getAddress().getHostAddress();

        socketRecepcion.close();
        return ip;
    }
}
