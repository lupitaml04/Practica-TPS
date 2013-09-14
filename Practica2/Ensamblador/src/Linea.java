
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Linea{
	String etiqueta, codigo, operando, archierr;
	int lin;
	Ensamblador p=new Ensamblador(); 
		
	public Linea(int linea, String archie){
		lin=linea;
		archierr=archie;
	}
	
	public boolean  validarEtiqueta(String eti){
		etiqueta=eti;
		if(etiqueta.length()>8)
		{
			p.escribirError(lin+"\tLa etiqueta se excedio en longitud\r\n",archierr);
			return false;}
			else
			{
				Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,7}");
        		Matcher mat = pat.matcher(etiqueta);
               	if (mat.matches()) 
               		{
               			return true;
         			} 
         			else 
         				{
        					p.escribirError(lin+"\tLa etiqueta no es valida\r\n",archierr);
        					return false;
          				}
			}
	}
	
	public boolean validarCodigo(String cod){
		codigo=cod;
		String error;
		if(codigo.length() >5)
		{
			p.escribirError(lin+"\tEl codop  se excedio en longitud\r\n",archierr);       	    
			return false;
		}
		else
			{
				Pattern pat = Pattern.compile("[a-zA-Z]{1,}[.]?[a-zA-Z]*");
                Matcher mat = pat.matcher(codigo);
               	if (mat.matches())
               		{
               			return true;       	
               		}
               		else
               			{      	
			     			p.escribirError(lin+"\tEl codop  no   es valido\r\n",archierr);
        	     			return false;
                		}
			}	
		}
		
	public boolean validarOperando(String oper){
		operando=oper;		
	    Pattern pat = Pattern.compile(".*");
        Matcher mat = pat.matcher(operando);
        if (mat.matches()) 
        	{
               	return true;       	
            } 
            else
            	{      	
			     	p.escribirError(lin+"\tEl operando  no   es valido\r\n",archierr);
        	     	return false;
                }
		}
}
