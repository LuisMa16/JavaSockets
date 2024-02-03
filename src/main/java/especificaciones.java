
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;


public class especificaciones {
	
	static ArrayList<String> especificaciones = new ArrayList<String>();
	
    public ArrayList<String> getEspecificaciones() {
        // Modelo del procesador y velocidad
        try {
            Process process;
            if (System.getProperty("os.name").contains("Windows")) { 
                process = Runtime.getRuntime().exec("wmic cpu get name, maxclockspeed");
            } else {
                process = Runtime.getRuntime().exec("lscpu | grep 'Model name\\|MHz'");
            }
            
            StringBuilder resultado = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                resultado.append(line).append("\n");
            }
            
            String informacionDelProcesador = resultado.toString();
            String[] infoSeparada = informacionDelProcesador.split("Name");
            String info = infoSeparada[1];
            String[] informacion = info.split("\\s+");
            String velocidad = informacion[1].trim();
            String procesador = "";
            for (int i = 2; i<informacion.length; i++) {
            	procesador = procesador + informacion[i].trim() + " ";
            }
            especificaciones.add(velocidad);
            especificaciones.add(procesador);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Número de núcleos
        java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        especificaciones.add(""+osBean.getAvailableProcessors());

        // Capacidad del disco duro
        File root = new File("/");
        String espacioTotal = root.getTotalSpace() / (1024 * 1024 * 1024) + " GB";
        String espacioLibre = ""+root.getFreeSpace() / (1024 * 1024 * 1024);
        especificaciones.add(espacioTotal);
        especificaciones.add(espacioLibre);
        
        // Versión del sistema operativo
        String osVersion = System.getProperty("os.version");
        especificaciones.add("Windows "+osVersion);
        
        return especificaciones;
    }
    
    public double getUsoCPU(){
    	SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        return cpuUsage;
    }
    
    public long getMemoriaRAM() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();

        long availableMemory = globalMemory.getAvailable();
    
        return availableMemory / (1024 * 1024);
    }
}
