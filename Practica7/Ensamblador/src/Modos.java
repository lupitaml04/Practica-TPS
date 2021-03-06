import java.util.*;
public class Modos {
	Ensamblador e=new Ensamblador();
	Linea l;
	Tabop t;
	int oper,conLoc,i;

	public Modos(Linea l,Tabop t,int conLoc){
		this.l=l;
		this.t=t;
		this.conLoc=conLoc;
	}
	public Modos(){
	}

	public int buscarModo(){
		boolean encontrado=false;
		i=0;
		if(!l.operando.equals("NULL"))
		{
			if(l.operando.indexOf(',') >-1)
			{
				for( i=0;i< t.modir.size() && !encontrado; i++)
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
							for(i=0;i< t.modir.size() && !encontrado; i++)
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
				if(t.modir.elementAt(i).equals("INH")|| t.modir.elementAt(i).equals("IMM")||t.modir.elementAt(i).equals("IDX"))
				{
					validarIns();
					encontrado=true;
				}
		if(!encontrado)
			e.escribirError(l.lin+"\tFormato de operando no valido para ningun modo\r\n",l.archierr);
	return conLoc;
	}

	public int convertirDecimal(String opera){
		int num=0;
		if(opera.startsWith("%")){
			if(opera.charAt(1)=='1')
            num=complementoADos(opera.substring(1));
            else
            {
            	try{
						num = Integer.parseInt(opera.substring(1),2);
					}catch(NumberFormatException ex){
						num=65536;
					}
            }
		}
		else
			if(opera.startsWith("$"))
			{
				if(opera.charAt(1)=='F'||opera.charAt(1)== 'f' )
				{
					num=complementoADos(hexBin(opera.substring(1)));

				}
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
					if(opera.charAt(1)=='7')
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

		public int convertirDecimal2(String opera){
		int num=0;
		if(opera.startsWith("%")){
			try{
				num = Integer.parseInt(opera.substring(1),2);
				}
				catch(NumberFormatException ex){
					num=65536;
				}
		}
		else
			if(opera.startsWith("$"))
			{
				try{
					num = Integer.parseInt(opera.substring(1),16);
					}catch(NumberFormatException ex){
						num=65536;
					}
			}
			else
				if(opera.startsWith("@"))
				{
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
		validarIns();
		return true;
    }

    public boolean  modoImm(String modo){
    	if(!l.validarOperando(l.operando.substring(1),false))
    	{
    		return true;
    	}
    	else
    	{
    		oper=convertirDecimal(l.operando.substring(1));
    		if(modo.equals("IMM8"))
    		{
    			if(oper>= -256 && oper<=255)
    			{
    				validarIns();
    			}
    			else{
    				e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM8\r\n",l.archierr);
    			}
    		}
    		else
    		{
    			if(oper>= -32768 && oper<=65535){
    				validarIns();
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
    	if(!l.validarOperando(l.operando,true))
    	{
    		return true;
    	}
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		return false;
    	}
    	else{
    		oper=convertirDecimal(l.operando);
    		if(oper>=0 && oper<=255)
    		{
    			validarIns();
    			return true;
    		}
    	}
    	return false;
    }

    public boolean modoExt()
    {  	if(!l.validarOperando(l.operando,true))
    	{
    		return true;
    	}
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		validarIns();
    		return true;
    	}
    	oper=convertirDecimal(l.operando);
    	if(oper>=-32768 && oper<= 65535)
    	{
    		validarIns();
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
    	if(!l.validarOperando(l.operando,true))
    	{
    		return true;	
    	}
    	if((l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z') || (l.operando.charAt(0)>='A' && l.operando.charAt(0)<='Z'))
    	{
    		validarIns();
    		return true;
    	}
    	oper=convertirDecimal(l.operando);
    		if(modo.equals("REL8"))
    		{
    			if(oper>=-128 && oper<=127)
    			{
    				validarIns();
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
    				validarIns();
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
    				validarIns();
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
    		e.escribirError(l.lin+"\tFormato de operando invalido para el modo indexado 1\r\n",l.archierr);
    		return true;
    	}
    	if(l.validarOperando(oper1,true))
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
    					validarIns();
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
    			 	{
    			 		validarIns();
    			 	}else
    					return false;
    			}
    			else
    				e.escribirError(l.lin+"\tFormato de registro invalido para el modo indexado  4\r\n",l.archierr);
    			return true;
    		}
    	}
    	else
    		if(oper1.toUpperCase().equals("A") || oper1.toUpperCase().equals("B") || oper1.toUpperCase().equals("D"))
    		{
    			if(oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") ||oper2.toUpperCase().equals("PC") )
    			{
    				validarIns();
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
    	Linea li=new Linea(l.lin,l.archierr,l.archiInst,l.archiT);
    	if(li.validarOperando(oper1,false))
    	if(!((oper1.charAt(0)>='a' && oper1.charAt(0)<='z') || (oper1.charAt(0)>='A' && oper1.charAt(0)<='Z')))
    	{
    		num=convertirDecimal(oper1);
    		if((num>= -256 && num<= 255) && (oper2.toUpperCase().equals("X")|| oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") || oper2.toUpperCase().equals("PC")))
    		{
    			validarIns();
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
    	if(num>= 0 && num<= 65535){
    		if(oper2.toUpperCase().equals("X")|| oper2.toUpperCase().equals("Y") || oper2.toUpperCase().equals("SP") || oper2.toUpperCase().equals("PC"))
    		{
    			validarIns();
    			return true;
    		}		
    	}
    	else{
    		e.escribirError(l.lin+"\tOperando fuera de rango para el modo indexado\r\n",l.archierr);
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
    	    Linea li=new Linea(l.lin,l.archierr,l.archiInst,l.archiT);
    	    if(oper1.length()==0 || oper2.length()==0)
    	    {
    	    	e.escribirError(l.lin+"\tFormato de operando invalido para el modo indexado 2\r\n",l.archierr);
    	    	return true;
    	    }
    	    if(li.validarOperando(oper1,false))
    	    if(!((oper1.charAt(0)>='a' && oper1.charAt(0)<='z') || (oper1.charAt(0)>='A' && oper1.charAt(0)<='Z')))
    	    {
    	    	num=convertirDecimal(oper1);
    	    	if(num>=0 && num<= 65535)
    	    	{
    	    		if(oper2.toUpperCase().equals("X") || oper2.toUpperCase().equals("Y") ||oper2.toUpperCase().equals("SP") ||oper2.toUpperCase().equals("PC")){
    	    			validarIns();
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
    		e.escribirError(l.lin+"Formato operando Invalido para modo indexado 5",l.archierr);
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
    	    	{
    	    		validarIns();
    	    	}else
    	    			e.escribirError(l.lin+"\tFormato de operando invalido para [D,IDX]\r\n",l.archierr);

    	        return true;
    	    }
    	}
    	else{
    		e.escribirError(l.lin+"\tFormato de operando invalido para modo indexado 6\r\n",l.archierr);
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

public void validarIns()
{

    if(!l.etiqueta.equals("NULL"))
    {
    	if(!e.buscarEti(l.etiqueta,l.archiT))
    	{	String cL=conLoc();
    		e.escribirSimbolo(l.etiqueta,cL,l.archiT);
    		e.escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+t.modir.elementAt(i)+"\t"+t.btotal.elementAt(i)+"\r\n",l.archiInst);
    	}
    	else
    		{
    			e.escribirError(l.lin+"\tLa etiqueta se repitio\r\n",l.archierr);
    		}
    }
	else{
			String cL=conLoc();
		e.escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t"+t.modir.elementAt(i)+"\t"+t.btotal.elementAt(i)+"\r\n",l.archiInst);
	}
}

public String conLoc(){
	String cL=Integer.toString(conLoc,16);
    while(cL.length()<4)
    cL="0"+cL;
    conLoc=conLoc+Integer.parseInt(t.btotal.elementAt(i));
    return cL;
}
}