/**
 * @(#)Linea.java
 *
 * Linea application
 *
 * @author 
 * @version 1.00 2013/9/2
 */
 import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Linea{
	String etiqueta, codigo, operando, archierr;
	int lin;
	Practica1 p=new Practica1(); 
		
	public Linea(int linea, String archie){
		lin=linea;
		archierr=archie;
	}
	
	public boolean  validarEtiqueta(String eti){
		etiqueta=eti;
		Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,7}");
        Matcher mat = pat.matcher(etiqueta);
        if (mat.matches()) {
        	return true;
         } else {
        	p.escribirError(lin+"	la etiqueta	"+ eti+"	no es valida	\r\n",archierr);
        	return false;
          }
	}
	
	public boolean validarCodigo(String cod){
		codigo=cod;
		Pattern pat = Pattern.compile("[a-zA-Z][a-zA-Z.?]{1,4}");
        Matcher mat = pat.matcher(codigo);
               if (mat.matches()) {
               	return true;       	
               } else {
               	p.escribirError(lin+"	El codigo de operacion	"+ codigo +"	no es valido\r\n",archierr);
        	    return false;
                }
		}
		
	public boolean validarOperando(String oper){
		operando=oper;		
		return true;
		}
}
