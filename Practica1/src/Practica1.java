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
	
	public void  validarEtiqueta(String eti,int l){
		etiqueta=eti;
		lin=l;
		Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,8}");
        Matcher mat = pat.matcher(etiqueta);
//        Practica1 p=new Practica1();
        if (mat.matches()) {
        	System.out.println("eti SI "+lin);
        	//p.escribirInstruccion(etiqueta);
        } else {
        	System.out.println("eti NO "+lin);
        	//p.escribirError("la etiqueta no es valida\n "+lin);
          }
	}
	
	public void validarCodigo(String cod,int l){
		codigo=cod;
		lin=l;
		Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,8}");
        Matcher mat = pat.matcher(codigo);
               if (mat.matches()) {
        	System.out.println("codigo SI "+lin);
        	
        } else {
        	System.out.println("codigo NO "+lin);
        
          }
		}
		
	public void validarOperando(String oper,int l){
		operando=oper;
		lin=l;
		Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,8}");
        Matcher mat = pat.matcher(operando);
               if (mat.matches()) {
        	System.out.println("oper SI "+lin);
        	
        } else {
        	System.out.println("oper NO "+lin);
        
          }
		}
}

public class Practica1 {
	Scanner Leer=new Scanner(System.in);
	String ruta, texto,eti,codop,oper, archivo,archivoInst,archivoErr;
	int linea =-1;
	boolean end;
	
	public Practica1(String r){
		ruta=r;
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
			revisarLinea();
			System.out.println(texto);
		    }
		
		    archi.close();
		    }
	        catch(IOException e){
		      System.out.println("Error");
	        }		
	}
	
	
    public void revisarLinea(){
    	Linea lin= new Linea();
        int cont=0,tam,edo=0;
        char[] cad;
        cad = texto.toCharArray();
        tam=texto.length();
        System.out.println("tamaño "+tam);
        while(edo != 10){
        	switch(edo){
        		case 0:
        			System.out.println("edo "+edo);
        			if(cad[cont]==';'){
        				edo=1;
    				    cont++;
    			    }
    				else
    					if(cad[cont]==' ' || cad[cont]=='	'){
    						edo=4;
    						eti=cad[cont]+"";
    						cont++;
    					}
    					else{
    						cont++;
    						eti=cad[cont]+"";
    						edo=3;
    					}		
    		     break;
    		     
    		     case 1:
    		     	System.out.println("edo "+edo);
    		     	if(tam==cont)
    		     		edo=10;
    		     		else{
    		     			cont++;
    					    edo=2;
    				    }
    				
     	         break;
     	         
     	         case 2:
     	         	System.out.println("edo "+edo);
    		     	if(tam==cont)
    		     		edo=10;
    		     		else{
    						 cont++;
    					    edo=2;
    				    }
     	         break;
     	         case 3:
     	         	System.out.println("edo "+edo+" cont "+cont+"tam "+tam);
     	         	
     	         	if(cont==tam){
     	         		escribirError("No hay Codigo de operacion en la linea " +linea);
     	         		edo=10;
     	         	}
     	         	else
     	         	if(cad[cont]==' ' || cad[cont]=='	'){
     	         		lin.validarEtiqueta(eti,linea);
     	         		cont++;
     	         		edo=5;
     	         	}
     	         	else{
     	         		eti.concat(cad[cont]+"");
     	         		cont++;
     	         		edo=3;
     	         	}		
     	         break;
     	         case 4:
     	         	System.out.println("edo "+edo);
     	         	if(tam==cont){
     	         		edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==' ' || cad[cont]=='	'){
     	         			cont++;
     	         			edo=4;
     	         		}
     	         		else{
     	         			codop=cad[cont]+"";
     	         			cont++;
     	         			edo=6;
     	         		}   	         			
     	         break;
     	         case 5:
     	         	System.out.println("edo "+edo);
     	         	if(tam==cont){
     	         		escribirError("No hay codigo de operacion "+ linea);
     	         		edo=10;
     	         	}
     	         	else	
     	         	if(cad[cont]==' '||cad[cont]=='	'){
     	         		cont++;
     	         		edo=5;
     	         	}
     	         	else{
     	         		cont++;
     	         		codop.concat(cad[cont]+"");
     	         		edo=6;
     	         	}		
     	         break;
     	         case 6:
     	         	System.out.println("edo "+edo);
     	         	if(tam==cont){
     	         		lin.validarCodigo(codop,linea);
     	         		edo=10;
     	         	}
     	         	else
     	         	if(cad[cont]==' '|| cad[cont]=='	'){
     	         		lin.validarCodigo(codop,linea);
     	         		cont++;
     	         		edo=7;
     	         	}
     	         	else{
     	         		codop.concat(cad[cont]+"");
     	         		edo=6;
     	         			cont++;
     	         	}
     	         break;
     	         case 7:
     	         	System.out.println("edo "+edo);
     	         	if(cad[cont]==' '|| cad[cont]=='	'){
     	         		cont++;
     	         		edo=7;
     	         	}
     	         	else
     	         		if(cad[cont]==';'){
     	         			cont++;
     	         			edo=1;
     	         		}
     	         	else{
     	         		cont++;
     	         		oper=cad[cont]+"";
     	         		edo=8;
     	         	}    	         	
     	         break;
     	         case 8:
     	         	System.out.println("edo "+edo);
     	         	if(tam==cont){
     	         		edo=10;
     	         	}
     	         	else	
     	         	if(cad[cont]==' ' || cad[cont]=='	'){
     	         		lin.validarOperando(oper,linea);
     	         		cont++;
     	         		edo=9;
     	         	}
     	         	else
     	         	if(cad[cont]==';'){
     	         		cont++;
     	         		edo=1;
     	         	}
     	         	else{
     	         		oper.concat(cad[cont]+"");
     	         		cont++;
     	         	}		
     	         break;
     	         case 9:
     	         	System.out.println("edo "+edo);
     	         	if(tam==cont){
     	         		edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==' ' || cad[cont]=='	'){
     	         			cont++;
     	         		}
     	         		else
     	         			if(cad[cont]==';'){
     	         				cont++;
     	         				edo=1;
     	         			}
     	         break;       
     }	
    }      	
    }
    
    public void escribirError(String error){
    	try{
			RandomAccessFile archierr=new RandomAccessFile(archivoErr,"rw");
			archierr.seek(archierr.length());
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
