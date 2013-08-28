/**
 * @(#)Practica1.java
 *
 * Practica1 application
 *
 * @author 
 * @version 1.00 2013/8/24
 */
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Linea{
	String etiqueta, codigo, operando;
	int lin;
	
	public Linea(String eti,int l)
	{
		etiqueta=eti;
		lin=l;
	}
	
	public void validarEtiqueta(){
		
		Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,8}");
        Matcher mat = pat.matcher(etiqueta);
        if (mat.matches()) {
        	System.out.println("SI "+lin);
        } else {
        	System.out.println("NO "+lin);
          }
	}
	
	public void validarCodigo(){
		}
		
	public void validarOperando(){
		}
}

public class Practica1 {
	Scanner Leer=new Scanner(System.in);
	String ruta, texto, archivo;
	int linea =-1;
	
	public Practica1(String r){
		ruta=r;
	}
	
	public void validarRuta(){
		File fichero = new File(ruta);
        if (fichero.exists())
        {
        	archivo=fichero.getName();
        	System.out.println(archivo);
        	 leerArchivo();
        }
        	
           
        else
        	System.out.println("La ruta no existe");
     }
	
	
	public void leerArchivo() {
		
		
		try{
			RandomAccessFile archi=new RandomAccessFile(new File(ruta),"r");
			while(archi.getFilePointer()!=archi.length())
		{
			texto=archi.readLine();
			linea++;
			revisarLinea(texto);
			System.out.println(texto);
		}
		
		archi.close();
		}
	catch(IOException e)
	{
		System.out.println("Error");
	}
		
	}
	
    public void revisarLinea(String tex){
     Linea lin= new Linea(texto,linea);
     lin.validarEtiqueta();
     
         	
    }
    
    public static void main(String[] args){
    	
    	
    	
    	Scanner Leer=new Scanner(System.in);
    	
    	String ruta="hola.txt";
    	
    	System.out.println("¿Cual es la ruta del archivo?");
    	ruta=Leer.next();
    	
    	Practica1 obj= new Practica1(ruta);
    	obj.validarRuta();
    	
    	
    }
}
