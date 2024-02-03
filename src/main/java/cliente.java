import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
  
public class cliente {
	static Integer lugar2;
	static boolean solicitud = false;
	static JFrame frame;
    
    public static void serCliente(Socket clientSocket) throws IOException {
    	try {
    	ArrayList<String> caracteristicas = new ArrayList<String>();
    	especificaciones car = new especificaciones();
    	caracteristicas = car.getEspecificaciones();
    	caracteristicas.add("");
    	caracteristicas.add("");
    	caracteristicas.add("0");
        
    	ObjectOutputStream caracteristicasCliente = new ObjectOutputStream(clientSocket.getOutputStream());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String respuestaServidor = in.readLine();
        System.out.println("Servidor dice: " + respuestaServidor);
        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
     
        frame = new JFrame("Solicitud");

        JButton button = new JButton("Enviar solicitud");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	solicitud = true;
                System.out.println("¡solicitud enviada!");
            }
        });

        frame.getContentPane().add(button);
        frame.setSize(300, 200);
        frame.setVisible(true);
        
        while (true) {
        	caracteristicas.set(8, "0");
        	Integer lugar = null;
			try {
				lugar = (Integer) inputStream.readObject();
				lugar2 = lugar;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				frame.dispose();
				String IP = in.readLine();
		        System.out.println("Nuevo servidor: " + IP);
		        Socket nuevoSocket = new Socket(IP, 9999);
		        serCliente(nuevoSocket);
			}
            System.out.println("lugar: " + lugar);
            if (lugar == 1) {
            	servidor servidor = new servidor();
				servidor.serServidor();
            }
        	caracteristicas.set(6, ""+car.getUsoCPU());
        	caracteristicas.set(7, ""+car.getMemoriaRAM());
        	if (solicitud == true) {
        		caracteristicas.set(8, "1");
        		solicitud = false;
        	}
        	caracteristicasCliente.reset();
            caracteristicasCliente.writeObject(caracteristicas);
            caracteristicasCliente.flush();
            try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    	} catch (SocketException e) {
    		System.out.println("El servidor se ha desconectado");
    		if (lugar2 == 2) {
    			servidor servidor = new servidor();
				servidor.serServidor();
    		} else {
    			Principal p = new Principal();
    			String ipServidor = p.buscarServidor();
    			Socket nuevoSocket = new Socket(ipServidor, 9999);
    			serCliente(nuevoSocket);
    		}
    	}
    }
}