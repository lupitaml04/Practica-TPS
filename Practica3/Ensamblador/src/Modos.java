import java.util.*;
public class Modos {
	Ensamblador e=new Ensamblador();
	Linea l;
	Tabop t;
	int oper;
	public Modos(Linea l,Tabop t){
		this.l=l;
		this.t=t;	
	}
	
	public void buscarModo(){
		boolean encontrado=false;
		if(!l.operando.equals("NULL"))
		{
			if(l.operando.indexOf(',') >-1)
			{
				for(int i=0;i< t.modir.size() && !encontrado; i++)
				{
					if(t.modir.elementAt(i).equals("IDX"))
					{
						encontrado=modoIdx();
					}
					else
						if(t.modir.elementAt(i).equals("IDX1"))
						{
							encontrado=modoIdx1();
						}
						else
							if(t.modir.elementAt(i).equals("IDX2"))
							{
								encontrado=modoIdx2();
							}
							else
								if(t.modir.elementAt(i).equals("[IDX2]"))
								{
									encontrado=modoIdx2C();
								}
								else
									if(t.modir.elementAt(i).equals("[D,IDX]"))
									{
										encontrado=modoIdxD();
									}			
					}
					if(!encontrado)
					{
						e.escribirError(l.lin+"\tFormato de operando no valido para ningun modo de direccionamiento\r\n",l.archierr);
						encontrado=true;
					}		
				}
				else
					if(t.modir.elementAt(0).equals("IMM8")||t.modir.elementAt(0).equals("IMM16"))
					{
						if(l.operando.startsWith("#")){
							encontrado=modoImm(t.modir.elementAt(0));	
						}				
					}
					else
						if(l.operando.startsWith("#"))
						{
							e.escribirError(l.lin+"\tEl codop no tiene direccionamiento IMM\r\n",l.archierr);
							encontrado=true;	
						}
						else
							if(t.modir.elementAt(0).equals("REL8")|| t.modir.elementAt(0).equals("REL9")|| t.modir.elementAt(0).equals("REL16"))
							{
								encontrado=modoRel(t.modir.elementAt(0));
							}
							else
								if(t.modir.elementAt(0).equals("INH"))
								{
									encontrado=modoInh();
								}	
						if(!encontrado)
						{
								for(int i=0;i< t.modir.size() && !encontrado; i++)
								{
										if(t.modir.elementAt(i).equals("DIR"))
										{
											encontrado=modoDir();
										}
										else
											if(t.modir.elementAt(i).equals("EXT"))
											{
												encontrado=modoExt();
											}
								}
					}								
			}
			else
				if(t.modir.elementAt(0).equals("INH")|| t.modir.elementAt(0).equals("IMM")||t.modir.elementAt(0).equals("IDX"))
				{
					e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+t.modir.elementAt(0)+"\r\n",l.archiInst);
					encontrado=true;
				}
		if(!encontrado)
			e.escribirError(l.lin+"\tFormato de operando no valido para ningun modo\r\n",l.archierr);			
	}

	public int convertirDecimal(String opera){
		int num=0;
		if(opera.startsWith("%")){
			if(opera.charAt(1)=='1')
            num=complementoADos(opera.substring(1));
            else
            	try{
						num = Integer.parseInt(opera);
					}catch(NumberFormatException ex){
						num=65536;
					}
            	
		}
		else
			if(opera.startsWith("$"))
			{
				if(opera.charAt(1)=='F')
				num=complementoADos(hexBin(opera.substring(1)));
				else
					try{
						num = Integer.parseInt(opera.substring(1),16);
					}catch(NumberFormatException ex){
						num=65536;
					}				
			}
			else
				if(opera.startsWith("@"))
				{
					if(opera.charAt(1)==7)
					num=complementoADos(octBin(opera.substring(1)));
					else
						try{
							num = Integer.parseInt(opera.substring(1),8);
						}catch(NumberFormatException ex){
							num=65536;
						}
				}
				else
				{
					try{
						num = Integer.parseInt(opera);
					}catch(NumberFormatException ex){
						num=65536;
					}
				}
	return num;
	}

	public int complementoADos(String nb){
		String c2="";
		int num=0;
		if(nb.charAt(0)=='1')
		{
			for(int i=0;i<nb.length();i++)
			{
				if(nb.charAt(i)=='1')
					c2+="0";
				if(nb.charAt(i)=='0')
					c2+="1";
			}
			try{
				num=Integer.parseInt(c2,2);
				num=(num+1)*(-1);
			}
			catch(NumberFormatException ex){
						num=65536;
			}								
		}
		else
			num=Integer.parseInt(nb,2);
		return num;
	}

	public boolean modoInh(){
		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tINH\r\n",l.archiInst);
		return true;
    }

    public boolean  modoImm(String modo){
    	if((l.operando.charAt(1)>='a' && l.operando.charAt(1)<='z') || (l.operando.charAt(1)>='A' && l.operando.charAt(1)<='Z'))
    	{
    		e.escribirError(l.lin+"\tEl modo inmediato no acepta etiquetas como operando\r\n",l.archierr);
    		return true;
    	}
    	else
    	{
    		oper=convertirDecimal(l.operando.substring(1));
    		if(modo.equals("IMM8"))
    		{
    			if(oper>= -256 && oper<=255)
    			{
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIMM8\r\n",l.archiInst);
    			}
    			else{
    				e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM8\r\n",l.archierr);
    			}  				
    		}
    		else
    		{
    			if(oper>= -32768 && oper<=65535){
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIMM16\r\n",l.archiInst);
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM16\r\n",l.archierr);
    			}
    		}
    	}	
     	return true;
    }
    
    public boolean modoDir()
    {
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		return false;
    	}
    	else{
    		oper=convertirDecimal(l.operando);
    		if(oper>=0 && oper<=255)
    		{
    			e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tDIR\r\n",l.archiInst);
    			return true;
    		}
    	}	
    	return false;
    }
    
    public boolean modoExt()
    {
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tEXT\r\n",l.archiInst);
    		return true;
    	}
    	oper=convertirDecimal(l.operando);
    	if(oper>=-32768 && oper<= 65535)
    	{
    		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tEXT\r\n",l.archiInst);
    		return true;
    	}
    	else
    	{
    		e.escribirError(l.lin+"\tOperando fuera de rango para el modo ext\r\n",l.archierr);
    		return true;
    	}
    }
    
    public boolean modoRel(String modo)
    {
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+modo+"\r\n",l.archiInst);
    		return true;
    	}
    	oper=convertirDecimal(l.operando);
    		if(modo.equals("REL8"))
    		{
    			if(oper>=-128 && oper<=127)
    			{
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tREL8\r\n",l.archiInst);
    			}
    			else{
    				e.escribirError(l.lin+"\tEl operando fuera de rango para el modo REL8\r\n",l.archierr);
    				}
    			return true;
    		}
    		else
    		if(modo.equals("REL16"))
    		{
    			if(oper>=-32768 && oper<=65535)
    			{
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+modo+"\r\n",l.archiInst);
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tEl operando fuera de rango para el modo REL16\r\n",l.archierr);
    			}
    			return true;
    		}
    		if(modo.equals("REL9"))
    		{
    			if(oper>=-256 && oper<=255)
    			{
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+modo+"\r\n",l.archiInst);
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tEl operando fuera de rango para el modo REL9\r\n",l.archierr);
    			}
    			return true;
    		}
    return false;
    }
    
    public boolean modoIdx()
    {
      	String oper1, oper2 ,operando=l.operando;
      	Linea li=new Linea(l.lin,l.archierr,l.archiInst);
    	int num;
    	if(l.operando.startsWith("["))
    	{
    		return false;
    	}
    	if(l.operando.startsWith(","))
    		operando="0"+operando;
    	oper1=operando.substring(0,operando.indexOf(','));
    	oper2=operando.substring(operando.indexOf(',')+1);
    	if(operando.length()<3)
    	{
    		e.escribirError(l.lin+"\tFormato de operando invalido para el modo indexado\r\n",l.archierr);
    		return true;
    	}
    	if(li.validarOperando(oper1))
    	if(!((oper1.charAt(0)>='a' && oper1.charAt(0)<='z') || (oper1.charAt(0)>='A' && oper1.charAt(0)<='Z')))
    	{
    		num=convertirDecimal(oper1);
    		if(oper2.startsWith("-") || oper2.startsWith("+")||oper2.endsWith("-") || oper2.endsWith("+"))
    		{
    			if(oper2.startsWith("-") || oper2.startsWith("+"))
    				oper2=oper2.substring(1);
    			else
    				if(oper2.endsWith("-") || oper2.endsWith("+"))
    					oper2=oper2.substring(0,oper2.length()-1);
    			if(oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP"))
    			{
    				if(num>=1 && num<=8){
    					e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIDX\r\n",l.archiInst);
    			    }
    			    else{
    			    	e.escribirError(l.lin+"\tOperando fuera de rango para el modo IDX\r\n",l.archierr);
    				}
    			}
    			else{
    				e.escribirError(l.lin+"\tFormato de registro invalido para modo IDX\r\n",l.archierr);
    				return true;	
    			}
    		return true;				
    		}
    		else{
    			 if((oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") || oper2.toUpperCase().equals("PC")))
    			 {
    			 	if((num>=-16 && num<=15))
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIDX\r\n",l.archiInst);
    				else 
    					return false;	
    			}
    			else
    				e.escribirError(l.lin+"\tFormato de registro invalido para el modo indexado\r\n",l.archierr);
    			return true;
    		}								
    	}
    	else
    		if(oper1.toUpperCase().equals("A") || oper1.toUpperCase().equals("B") || oper1.toUpperCase().equals("D"))
    		{
    			if(oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") ||oper2.toUpperCase().equals("PC") )
    			{
    				e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIDX\r\n",l.archiInst);
    				return true;
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tFormato de registro invalido para el modo IDX\r\n",l.archierr);
    				return true;
    			}
    		}
    		else
    		{
    			e.escribirError(l.lin+"\tFormato de operando invalido para el modo IDX\r\n",l.archierr);
    			return true;
    		}
    	return true;		
    }
    
    public boolean modoIdx1(){
    	String  oper1, oper2;
    	int num;
    	if(l.operando.startsWith("["))
    	{
    		return false;
    	}
    	oper1=l.operando.substring(0,l.operando.indexOf(','));
    	oper2=l.operando.substring(l.operando.indexOf(',')+1);
    	Linea li=new Linea(l.lin,l.archierr,l.archiInst);
    	if(li.validarOperando(oper1))
    	if(!((oper1.charAt(0)>='a' && oper1.charAt(0)<='z') || (oper1.charAt(0)>='A' && oper1.charAt(0)<='Z')))
    	{		
    		num=convertirDecimal(oper1);
    		if((num>= -256 && num<= 255) && (oper2.toUpperCase().equals("X")|| oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") || oper2.equals("PC")))
    		{
    			e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIDX1\r\n",l.archiInst);
    			return true;				
    		}
    	} 
    	else
    	{
    		e.escribirError(l.lin+"\tFormato de operando invalido para IDX1\r\n",l.archierr);
    		return true;
    	}
    	return false;
    }
    
    public boolean modoIdx2(){
    	String oper1, oper2;
    	int num;
    	if(l.operando.startsWith("["))
    	{
    		return false;
    	}
    	oper1=l.operando.substring(0,l.operando.indexOf(','));
    	oper2=l.operando.substring(l.operando.indexOf(',')+1);		
    	num=convertirDecimal(oper1);
    	if((num>= 0 && num<= 65535) && (oper2.toUpperCase().equals("X")|| oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") || oper2.equals("PC")))
    	{
    		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\tIDX2\r\n",l.archiInst);
    		return true;				
    	} 
    	return false;   	
    }
    public boolean modoIdx2C(){
    	String oper1, oper2;
    	int num;
    	if(l.operando.startsWith("[") && l.operando.endsWith("]"))
    	{
  		    oper1=l.operando.substring(1,l.operando.indexOf(','));
    	    oper2=l.operando.substring(l.operando.indexOf(',')+1, l.operando.length()-1);
    	    Linea li=new Linea(l.lin,l.archierr,l.archiInst);
    	    if(oper1.length()==0 || oper2.length()==0)
    	    {
    	    	e.escribirError(l.lin+"\tFormato de operando invalido para el modo indexado\r\n",l.archierr);
    	    	return true;
    	    }	
    	    if(li.validarOperando(oper1))
    	    if(!((oper1.charAt(0)>='a' && oper1.charAt(0)<='z') || (oper1.charAt(0)>='A' && oper1.charAt(0)<='Z')))
    	    {
    	    	num=convertirDecimal(oper1);
    	    	if(num>=0 && num<= 65535)
    	    	{
    	    		if(oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") ||oper2.toUpperCase().equals("SP") ||oper2.toUpperCase().equals("PC")){ 
    	    			e.escribirInstruccion(l.lin, "\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t[IDX2]\r\n",l.archiInst);	
    	    		}
    	    		else
    	    		    e.escribirError(l.lin+"\tOperando Invalido para el modo [IDX2]\r\n",l.archierr);
    	    	}
    	    	else{
    	    		e.escribirError(l.lin+"\tOperando fuera de rango para el modo [IDX2]\r\n",l.archierr);
    	    	}
    	    }
    	    else{
    	    	e.escribirError(l.lin+"\tOperando invalido para el modo [IDX2]\r\n",l.archierr);
    	    }   
    	}
    	else{
    		e.escribirError(l.lin+"Formato operando Invalido para modo indexado",l.archierr);
    	}
    	return true;
    }
    
    public boolean modoIdxD()
    {
    	String oper1, oper2;
    	int num;
    	if(l.operando.startsWith("[") && l.operando.endsWith("]"))
    	{
    		oper1=l.operando.substring(1,l.operando.indexOf(','));
    	    oper2=l.operando.substring(l.operando.indexOf(',')+1, l.operando.length()-1);
    	    if(oper1.toUpperCase().equals("D"))
    	    {
    	    	if(oper2.toUpperCase().equals("X")|| oper2.toUpperCase().equals("Y")|| oper2.toUpperCase().equals("SP")|| oper2.toUpperCase().equals("PC"))
    	    		e.escribirInstruccion(l.lin,"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t[D,IDX]\r\n",l.archiInst);
    	    		else
    	    			e.escribirError(l.lin+"\tFormato de operando invalido para [D,IDX]\r\n",l.archierr);
    	    	
    	        return true;
    	    }	
    	}
    	else{
    		e.escribirError(l.lin+"\tFormato de operando invalido para modo indexado\r\n",l.archierr);
    	return true;
    	}
    	return false;	
    }
    
public static String hexBin(String hex){
    	String nu,bin="";
    	for(int i=0;i<hex.length();i++)
    	{
    		nu=Integer.toBinaryString(Integer.parseInt(hex.charAt(i)+"",16));
    		while(nu.length()<4)
    		nu="0"+nu;
    		bin+=nu;
    	}
    	return bin;
}

public static String octBin(String oct){
    	String nu,bin="";
    	for(int i=0;i<oct.length();i++)
    	{
    		nu=Integer.toBinaryString(Integer.parseInt(oct.charAt(i)+"",8));
    		while(nu.length()<3)
    		nu="0"+nu;
    		bin+=nu;
    	}
    	return bin;
}   
}