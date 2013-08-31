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
        Practica1 p=new Practica1();
        if (mat.matches()) {
        	System.out.println("SI "+lin);
        	p.escribirInstruccion(etiqueta);
        } else {
        	System.out.println("NO "+lin);
        	p.escribirError("la etiqueta no es valida\n");
          }
	}
	
	public void validarCodigo(){
		}
		
	public void validarOperando(){
		}
}

public class Practica1 {
	Scanner Leer=new Scanner(System.in);
	String ruta, texto, archivo,archivoInst,archivoErr;
	int linea =-1;
	
	public Practica1(String r){
		ruta=r;
	}
	
	public Practica1(){
	}
	
	public void crearArchivos(){
		
		File fichero = new File(ruta);
		archivoInst=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".INST";
		archivoErr=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".ERR";
		try{
			RandomAccessFile archinst=new RandomAccessFile(archivoInst,"rw");
			archinst.writeUTF(" LINEA\t\tETQ\t\tOPER\n");
			archinst.writeUTF("\n...........................................................\n");	
			archinst.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }
	       
	    try{
			RandomAccessFile archierr=new RandomAccessFile(archivoErr,"rw");
			archierr.writeUTF("ERRORES\n.........................................");	
			archierr.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }
	}
	
	public void validarRuta(){
		File fichero = new File(ruta);
		
        if (fichero.exists())
        {
        	if(fichero.getName().endsWith(".ASM"))
        	{
        	    crearArchivos();
        	    leerArchivo();  
        	}
              else{
        		System.out.println("la extension es incorrecta");
        	}       	
        }          
          else
        	System.out.println("La ruta no existe");
     }
	
	
	public void leerArchivo() {		
		try{
			RandomAccessFile archi=new RandomAccessFile(new File(ruta),"r");
			while(archi.getFilePointer()!=archi.length()){
			texto=archi.readLine();
			linea++;
			revisarLinea(texto);
			System.out.println(texto);
		    }
		
		    archi.close();
		    }
	        catch(IOException e){
		      System.out.println("Error");
	        }		
	}
	
	
    public void revisarLinea(String tex){
     Linea lin= new Linea(texto,linea);
     lin.validarEtiqueta();      	
    }
    
    public void escribirError(String error){
    	try{
			RandomAccessFile archierr=new RandomAccessFile(archivoErr,"rw");
			archierr.writeUTF(error);	
			archierr.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }
    }
    
    public void escribirInstruccion(String instruccion){
    	try{
    		RandomAccessFile archinst=new RandomAccessFile(archivoInst,"rw");
			archinst.writeUTF(instruccion);	
			archinst.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }
    	
    }
    
    public static void main(String[] args){
    	Scanner Leer=new Scanner(System.in);
    	String ruta;
    	System.out.println("¿Cual es la ruta del archivo?");
    	ruta=Leer.next();
    	Practica1 obj= new Practica1(ruta);
    	obj.validarRuta();	
    }
}
