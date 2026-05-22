package logic;
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Controlador {
	
	private volatile boolean paradaSolicitada = false;
	
	
	
	public void escanearRango(List<String> ips, int tiempoLimite, DefaultTableModel model, JProgressBar barraProgreso) throws ScanTimeOutException {
        paradaSolicitada = false;
        barraProgreso.setValue(0);
        barraProgreso.setMaximum(ips.size());
        long inicio = System.currentTimeMillis();

        int index = 0;

        for (String ip : ips) {
            if (paradaSolicitada) {
                break;
            }

            long inicioPing = System.currentTimeMillis();
            String respuesta = huboRespuesta(ip);
            long finPing = System.currentTimeMillis();
            String hostname = obtenerNombre(ip);
            
            int progress = ++index;
            
            
            // Este método se utiliza para analizar la interfaz de usuario si es que en la interfaz se esta realizando alguna tarea, si no es asi se modificara la progressbar.
            SwingUtilities.invokeLater(() -> {
            	
                model.addRow(new Object[]{ip, hostname, respuesta, finPing - inicioPing});
                barraProgreso.setValue(progress);
                barraProgreso.setString((progress * 100 / ips.size()) + "%");
            });

            long fin = System.currentTimeMillis();
            if (fin - inicio >= tiempoLimite) {throw new ScanTimeOutException("Se dejará de escanear el rango de IPs. El tiempo de espera está agotado.");}           
        }
	}
   
   		
   
   private static String obtenerNombre(String ip) {
       try {
    	   ProcessBuilder pb = new ProcessBuilder("nslookup", ip); // Arma el comando.
    	   pb.redirectErrorStream(true);
    	   
    	   Process proceso = pb.start(); // Inicia el comando
           
           BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream())); //Agarra el resultado del comando 
          
                          
           String resultado = "El nombre no fue encontrado";
           String line;
                        
           while (true)  {
        	   
        	   line = lector.readLine();
  
               if (line.contains("Nombre:")) {return line.split("Nombre:")[1].trim();}
               else if(line.contains("*")) {break;}
                                                 
           }
           return resultado;
           
       } catch (Exception e) {return "Error";}
      
   }
   private static String huboRespuesta(String ip) {
       try {
    	   
    	   ProcessBuilder pb = new ProcessBuilder("ping", "-n", "1", ip);
    	   pb.redirectErrorStream(true);
    	   Process proceso = pb.start();
           
           BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream())); //Agarra el resultado de
           
           String line;
                        
           while (true){
        	   line = lector.readLine();
        	   
        	   // Cuando el Ping de exactamente lo que esta en el contains es porque esta conectado.
               if (line.contains(ip + ": bytes")) {return "Conectado";}
               
               else if(line.contains(": Host de destino")) {return "Inaccesible";}
               else if(line.contains("(100% perdidos)")) {return "No Conectado";}
           }
          
       } catch (Exception e){return "Error, sin respuesta";}
      
   }
   
// Método que detiene el escaneo, cambiando el estado de una bandera
public void stopScan() { 
    paradaSolicitada = true; // Se marca la variable 'paradaSolicitada' en true para indicar que se debe detener el proceso
} 

// Método para guardar los resultados de la JTable en un archivo CSV
public void guardarResultados(JTable table) { 
    JFileChooser fileChooser = new JFileChooser(); // Se crea un selector de archivos (cuadro de diálogo para elegir ubicación del archivo)
    fileChooser.setDialogTitle("Guardar resultados"); // Se establece el título de la ventana de selección de archivo
    
    int seleccion = fileChooser.showSaveDialog(null); // Se abre la ventana para que el usuario elija la ubicación. 
                                                     // Devuelve un código según la acción: aprobar, cancelar, etc.
    
    // Si el usuario presiona "Guardar" (APPROVE_OPTION), se procede
    if (seleccion == JFileChooser.APPROVE_OPTION) { 
        try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile() + ".csv")) { 
            // Se crea un FileWriter dentro de un bloque try-with-resources para escribir el archivo .csv 
            // (try-with-resources garantiza que se cierre automáticamente al terminar)
            
            DefaultTableModel model = (DefaultTableModel) table.getModel(); 
            // Se obtiene el modelo de la tabla (donde están los datos)
            
            // --------- Escribir encabezados de la tabla ----------
            for (int i = 0; i < model.getColumnCount(); i++) { // Recorre todas las columnas
                writer.write(model.getColumnName(i) + (i < model.getColumnCount()-1 ? "," : "")); 
                // Escribe el nombre de la columna y agrega una coma si no es la última columna
            }
            writer.write("\n"); // Salto de línea al final de los encabezados
            
            // --------- Escribir filas de la tabla ----------
            for (int r = 0; r < model.getRowCount(); r++) { // Recorre cada fila
                for (int c = 0; c < model.getColumnCount(); c++) { // Recorre cada columna dentro de esa fila
                    writer.write(model.getValueAt(r, c).toString() + (c < model.getColumnCount()-1 ? "," : "")); 
                    // Escribe el valor de la celda convertido a String y agrega coma si no es la última columna
                }
                writer.write("\n"); // Al final de cada fila, se agrega un salto de línea
            }
            
            JOptionPane.showMessageDialog(null, "Archivo guardado con éxito."); 
            // Muestra un mensaje emergente indicando que el archivo se guardó correctamente
        } catch (Exception e) { 
            // Si ocurre algún error durante la escritura del archivo...
            JOptionPane.showMessageDialog(null, "Error al guardar archivo: " + e.getMessage()); 
            // Se muestra un mensaje con la descripción del error
        } 
    } 
} 
}