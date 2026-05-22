package util;

import java.net.InetAddress; //Proporciona métodos para obtener la dirección IP de cualquier nombre de host.
import java.util.ArrayList; //Es una implementación de array redimensionable.
import java.util.stream.Collectors;

import logic.IPRangeOutofBoundsException;

public class IPv4 {
	
    public static boolean validarIP(String ip) { //Sirve para comprobar si lo que se escribio es una IP
    	
		try {
	        InetAddress.getByName(ip);
	        return ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");} //Expresión regular para ver que haya un . después de 3 numeros
	    
	    catch (Exception e) {return false;}
    }

 //Métodos: ---------------------------------------
    
    public static ArrayList<String> generarRangoIP(String inicioIP, String finIP) throws IPRangeOutofBoundsException { 
    	
    	//Crea las Arrays lists necesarias, se guardaran las IPS inicial, final y su respectivo rango
		ArrayList<Integer> ListaIPInicio = new ArrayList<>();
	    ArrayList<Integer> ListaIPFin = new ArrayList<>();
	    ArrayList<String> rango = new ArrayList<>();

	  //Divide las IPS en 4 pedazos separados por el .
        String[] inicio = inicioIP.split("\\."); 
        String[] fin = finIP.split("\\.");

        //Hace un for por cada parte del split y lo guarda en una lista convirtiendola a numerica.
        for(String numero: inicio) {ListaIPInicio.add(Integer.parseInt(numero));}
        
        for(String numero: fin) {ListaIPFin.add(Integer.parseInt(numero));}
        
        int cantVuel = 0;
        
        // Crea una lista:   2**24       2**16 2**8 2**0
        int[]multis = new int[]{16777216,65536,256,1}; // Es una forma de simplificar, la cantidad de vueltas que debera hacer el for.
        
        
        // este for ira de la parte 3 del IP hasta la 0.
        for(int i = 3; i >= 0; i--) {
        	//Se restara las partes de la IP final(la mas alta)con cada parte de la inicial(la mas baja).
        	int diferencia = ListaIPFin.get(i) - ListaIPInicio.get(i); 
    	 
        	if(Math.abs(diferencia) > 0) { // Se verifica si la diferencia absoluta es mayor a 0.
        		cantVuel += diferencia*multis[i];} //Se multiplica la diferencia con el respectivo multiplicador para obtener la cantidad de vueltas.
    	   
        }
        
        if(cantVuel  > 10000) {
        	throw new IPRangeOutofBoundsException("El rango de IP es muy grande");
        }
        
        for(int i = 0 ; i <= cantVuel ; i++) {
        	rango.add(ListaIPInicio.stream().map(String::valueOf).collect(Collectors.joining("."))); //Se convierte la lista de números en un String de cada valor separado por "."
        	ListaIPInicio.set(3, ListaIPInicio.get(3) + 1);

        	// Si el último número es mayor a 255, se pone en 0, y al siguiente número se le suma 1, y se comprueba lo mismo hasta el número final
        	if (ListaIPInicio.get(3) > 255){
        		
        	    ListaIPInicio.set(3,0);
        	    ListaIPInicio.set(2, ListaIPInicio.get(2) + 1);
        	    
        	    if (ListaIPInicio.get(2) > 255){
        	    	
        	    	ListaIPInicio.set(2,0);
        	        ListaIPInicio.set(1, ListaIPInicio.get(1) + 1);
        	        
        	        if (ListaIPInicio.get(1) > 255){
        	        	
        	        	ListaIPInicio.set(1,0);
        	            ListaIPInicio.set(0, ListaIPInicio.get(0) + 1); }
        	    } 
        	}
        }
        
        return rango;}
    
    public static long IPNumerica(String ip) {return Long.parseLong(ip.replace(".", ""));}
    
    public static boolean validarMayorIp(String Ipinicio, String Ipfin) {
    	
    	String[] listainicio = Ipinicio.split("\\.");
        String[] listafin = Ipfin.split("\\.");

        for (int i = 0; i < listainicio.length; i++) {
        	
            if(Integer.parseInt(listainicio[i]) < Integer.parseInt(listafin[i])) {return false;}
            if(Integer.parseInt(listainicio[i]) > Integer.parseInt(listafin[i])) {return true;}
        }

        return false;}
    

    public static int compararIPs(String Ipinicio, String Ipfin) {
    	
    	
    	String[] listainicio = Ipinicio.split("\\.");
        String[] listafin = Ipfin.split("\\.");

        for (int i = 0; i < listainicio.length; i++) {
            if(Integer.parseInt(listainicio[i]) < Integer.parseInt(listafin[i])) {return -1;}
            if(Integer.parseInt(listainicio[i]) > Integer.parseInt(listafin[i])) {return 1;}
        }

        return 0;}
   
}
