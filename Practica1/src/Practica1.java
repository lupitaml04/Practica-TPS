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

public class Practica1 {
	Scanner Leer=new Scanner(System.in);
	String ruta, texto, archivo,archivoInst,archivoErr;
	int linea =-1;
	boolean end;
	
	public Practica1(String r){
		ruta=r;
	}
	
	public Practica1(){
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
    
	public void crearArchivos(){		
		File fichero = new File(ruta);
		archivoInst=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".INST";
		archivoErr=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".ERR";
		File inst = new File(archivoInst);
		File err = new File(archivoErr);
		 if(inst.exists())
		 	inst.delete();
		 if(err.exists())
		 	err.delete();		
		try{
			RandomAccessFile archinst=new RandomAccessFile(archivoInst,"rw");
			archinst.writeUTF("	LINEA	ETQ	CODOP	OPER");
			archinst.writeUTF("\r\n...........................................................\r\n");	
			archinst.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }	       
	    try{
			RandomAccessFile archierr=new RandomAccessFile(archivoErr,"rw");
			 archierr.writeUTF("	ERRORES\r\n.........................................\r\n");	
			archierr.close();
		}
	      catch(IOException e2){
		    System.out.println("Error");
	       }
	}
	
	public void leerArchivo() {		
		try{
			RandomAccessFile archi=new RandomAccessFile(new File(ruta),"r");
			while(archi.getFilePointer()!=archi.length() && !end){
			texto=archi.readLine();
			linea++;
			revisarLinea();
		    }
		    if(!end)
		    	escribirError("No hay END",archivoErr);
		    archi.close();
		    }
	        catch(IOException e){
		      System.out.println("Error");
	        }		
	}
		
    public void revisarLinea(){
    	Linea lin= new Linea(linea,archivoErr);     		   	
    	String eti,codop,oper;
        int cont, edo, tam;
        char[] cad = texto.toCharArray();
        boolean etiq, cod, op;
        
        eti=null; 
        codop=null; 
        oper=null;
        cont=0; 
        edo=0; 
        tam=texto.length();
        etiq=false;
        cod=false;
        op=false;
        
         while(edo != 10){
        	switch(edo){
        		case 0:
        			if(cont==tam){
        				edo=10;
        			}else
        			if(cad[cont]==';'){
        				edo=1;
    				    cont++;
    			    }
    				else
    					if(cad[cont]==' ' || cad[cont]=='	'){
    						edo=4;
    						eti="NULL";
    						etiq=true;
    						cont++;
    					}
    					else{	
    						eti=cad[cont]+"";
    						cont++;
    						edo=3;
    					}		
    		     break;
    		     case 1:
    		     	if(tam==cont)
    		     		edo=10;
    		     		else{
    		     			cont++;
    					    edo=2;
    				    }  				
     	         break;
     	         case 2:
    		     	if(tam==cont)
    		     		edo=10;
    		     		else{
    						cont++;
    					    edo=2;
    				    }
     	         break;
     	         case 3:
     	         	if(cont==tam){
     	         		escribirError(linea +" No hay Codigo de operacion \r\n",archivoErr);
     	         		edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==';'){
     	         			escribirError("Linea "+ linea+ "No hay codigo de operacion",archivoErr);
     	         			edo=1;
     	         			cont++;
     	         		}
     	         		else
     	         	if(cad[cont]==' ' || cad[cont]=='	'){
     	         		etiq=lin.validarEtiqueta(eti);
     	         		cont++;
     	         		edo=5;
     	         	}
     	         	else{
     	         		eti+=cad[cont];
     	         		        cont++;
     	         		        edo=3;
     	         	}		
     	         break;
     	         case 4:
     	         	if(tam==cont){
     	         		edo=10;
     	         	}
     	         	else
     	         	if(cad[cont]==';'){
     	         	edo=1;
     	         	cont++;
     	         	}
     	         	else
     	         		if(cad[cont]==' ' || cad[cont]=='	'){
     	         			cont++;
     	         			edo=4;
     	         		}
     	         		else{
     	         			if((cad[cont]=='E'||cad[cont]=='e')&& (cont+3)<=tam){
     	         				String subStr=texto.substring(cont, cont+3);
                                if(subStr.toUpperCase().equals("END")){
                                	end=true;
                                	edo=10;
                                }   	
     	         			}
     	         			if(!end){
     	         			codop=cad[cont]+"";
     	         			cont++;
     	         			edo=6;
     	         			}	
     	         		}   	         			
     	         break;
     	         case 5:
     	         	if(tam==cont){
     	         		escribirError("Linea "+ linea+" No hay codigo de operacion ",archivoErr);
     	                edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==';'){
     	         			escribirError("Linea "+ linea+" No hay codigo de operacion ",archivoErr);
     	                    edo=1;
     	                    cont++;	
     	         		}
     	         		else	
     	         	if(cad[cont]==' '|| cad[cont]=='	'){
     	         		cont++;
     	         		edo=5;
     	         	}
     	         	else{
     	         		codop=cad[cont]+"";
     	         		edo=6;
     	         		cont++;
     	         	}		
     	         break;
     	         case 6:
     	         	if(tam==cont){
     	         		cod=lin.validarCodigo(codop);
     	         		edo=10;
     	         		op=true;
     	         		oper="NULL";
     	         	}
     	         	else
     	         	if(cad[cont]==';'){
     	         		edo=1;
     	         		cod=lin.validarCodigo(codop);
     	         		op=true;
     	         		oper="NULL";
     	         	}	
     	         	else
     	         	if(cad[cont]==' '|| cad[cont]=='	'){
     	         		cod=lin.validarCodigo(codop);
     	         		cont++;
     	         		edo=7;
     	         	}
     	         	else{
     	         		codop=codop+cad[cont];
     	         		edo=6;
     	         		cont++;
     	         	}
     	         break;
     	         case 7:
     	         	if(tam==cont){
     	         		oper="NULL";
     	         		op=true;
     	         	}
     	         	else
     	         	if(cad[cont]==' '|| cad[cont]=='	'){
     	         		cont++;
     	         		edo=7;
     	         	}
     	         	else
     	         		if(cad[cont]==';'){
     	         			cont++;
     	         			edo=1;
     	         			op=true;
     	         			oper="NULL";
     	         		}
     	         	else{
     	         		
     	         		oper=cad[cont]+"";
     	         		cont++;
     	         		edo=8;
     	         	}    	         	
     	         break;
     	         case 8:
     	         	if(tam==cont){
     	         		op=lin.validarOperando(oper);
     	         		edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==';'){
     	         			op=lin.validarOperando(oper);
     	         			edo=1;
     	         			cont++;
     	         		}
     	         		else	
     	         	if(cad[cont]==' ' || cad[cont]=='	'){
     	         		op=lin.validarOperando(oper);
     	         		cont++;
     	         		edo=9;
     	         	}
     	         	else
     	         	if(cad[cont]==';'){
     	         		op=lin.validarOperando(oper);
     	         		cont++;
     	         		edo=1;
     	         	}
     	         	else{
     	         		oper+=cad[cont];
     	         		cont++;
     	         	}		
     	         break;
     	         case 9:
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
    if((etiq && cod) && op){
       escribirInstruccion("	"+ linea+  "	"+ eti+"	"+codop+"	"+oper+"\r\n");
    }      	
    }
    
    public void escribirError(String error, String archi){
    	archivoErr=archi;
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
    		archinst.seek(archinst.length());
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
    	System.out.println("�Cual es la ruta del archivo?");
    	ruta=Leer.nextLine();
    	Practica1 obj= new Practica1(ruta);
    	obj.validarRuta();	
    }
}
