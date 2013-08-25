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
class Linea{
	String etiqueta, codigo, operando;
	
	public void validarEtiqueta(){
	}
	
	public void validarCodigo(){
		}
		
	public void validarOperando(){
		}
}

public class Practica1 {
	Scanner Leer=new Scanner(System.in);
	String ruta;
	String texto;
	
	public void leerArchivo(){
		//System.out.println("Escribe la ruta del arhivo");
		//ruta=Leer.next();
		try{
		RandomAccessFile archi=new RandomAccessFile("archivo.ASM","r");
		while(archi.getFilePointer()!=archi.length())
		{
			texto+=archi.readUTF();
		}
		archi.close();
		System.out.println(texto);
	}
	catch(IOException ioe)
	{
		System.out.println("Error al abrir el archivo");
	}

	}
		
    
    public static void main(String[] args) {
    	
    	
    	Practica1 obj= new Practica1();
    	obj.leerArchivo();
    }
}
