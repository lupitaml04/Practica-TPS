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
	
	public String buscarModo(){
		boolean encontrado=false;
		String modo=null;
		if(l.operando!=null)
			{
					if(t.modir.elementAt(0).equals("IMM")||t.modir.elementAt(0).equals("IMM8")||t.modir.elementAt(0).equals("IMM16"))
						{
							if(l.operando.charAt(0)=='#')
							{
								oper=convertirDecimal(l.operando.substring(1));
								encontrado=modoImm(t.modir.elementAt(0));
								if(encontrado)
									modo=t.modir.elementAt(0);
								return modo;	
							}				
						}
						else
						{
							if(t.modir.elementAt(0).equals("REL8")||t.modir.elementAt(0).equals("REL16"))
							{
								oper=convertirDecimal(l.operando);
								encontrado=modoRel(t.modir.elementAt(0),oper);
								if(encontrado)
								{
									modo=t.modir.elementAt(0);
								}
								return modo;
							}
						}
						if(!encontrado)
							if(l.operando.charAt(0)=='#')
							{
								e.escribirError(l.lin+"\tEl codop no tiene direccionamiento IMM\r\n",l.archierr);
								return null;	
							}
						if(!encontrado)				
						oper=convertirDecimal(l.operando);
						
						for(int i=0;i< t.modir.size() && !encontrado; i++)
						{
							if(t.modir.elementAt(i).equals("INH"))
							{
								encontrado=modoInh();
	    		            }
	    		            else
	    		            	if(t.modir.elementAt(i).equals("DIR"))
	    		            	{
	    		            		encontrado=modoDir(oper);
	    		            	}
	    		            	else
	    		            		if(t.modir.elementAt(i).equals("EXT"))
	    		            		{
	    		            			encontrado=modoExt(oper);
	    		            		}
	    		            if(encontrado)
	    		            	modo=t.modir.elementAt(i);
	    		        }
					
			}
			else
	
		if(t.modir.elementAt(0).equals("INH")|| t.modir.elementAt(0).equals("IMM"))
		{
			encontrado=true;
			modo=t.modir.elementAt(0);
		}		
	      return modo; 
	      	
	
	}

	public int convertirDecimal(String opera){
		int num=0;
		if(opera.charAt(0)=='%'){
			String n = opera.substring(1);
			System.out.println(n);
            num=complementoADos(n);
			System.out.println(num);
		}
		else
			if(opera.charAt(0)=='$')
			{
				String n = opera.substring(1);
				num=Integer.parseInt(n,16);
				System.out.println(num);
				num=complementoADos(Integer.toBinaryString(num));
				System.out.println(num);
				System.out.println("Error formato de operando invalido");			
			}
			else
				if(opera.charAt(0)=='@')
				{
					String n= opera.substring(1);
					num=Integer.parseInt(n,8);
					System.out.println(num);
					num=complementoADos(Integer.toBinaryString(num));
					System.out.println(num);
				}
				else
				{
					System.out.println(opera+"\t"+num);
					 num = Integer.parseInt(opera);		
			    }
	return num;

	}

	public int complementoADos(String nb){
		String c2="";
		int num=0;
		System.out.println(nb);
		if(nb.charAt(0)=='1')
		{
			for(int i=0;i<nb.length();i++)
			{
				if(nb.charAt(i)=='1')
					c2+="0";
				if(nb.charAt(i)=='0')
					c2+='1';
			}
			
				num=Integer.parseInt(c2,2);
			num=(num+1)*(-1);						
		}
		else
			num=Integer.parseInt(nb);
		return num;
	}

	public boolean modoInh(){
		return true;
    }

    public boolean  modoImm(String modo){
    	if(modo.equals("IMM8"))
    	{
    		if(oper>= -256 && oper<=255){
    			System.out.println("Modo inmediato 8 correcto");			
    			return true;
    		}
    		else
    		{
    			e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM8\r\n",l.archierr);
    		}  				
    	}
    	else
    		{
    			if(oper>= -32768 && oper<=65535){
    				System.out.println("Modo inmediato 16 correcto");
    				return true;
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM16\r\n",l.archierr);
    			}
    		}
     	return false;
    }
    
    public boolean modoDir(int oper)
    {
    	if(oper>=0 && oper<=255)
    	{
    		return true;
    	}
    	return false;
    }
    
    public boolean modoExt(int oper)
    {
    	if(oper>=-32768 && oper<= 65535)
    	{
    		return true;
    	}
    	else
    	{
    		e.escribirError(l.lin+"\tOperando fuera de rango para el modo ext\r\n",l.archierr);
    	}
    	return false;
    }
    
    public    boolean modoRel(String modo, int oper)
    {
    	if(modo.equals("REL8"))
    	{
    		if(oper>=-128 && oper<=127)
    		{
    			return true;
    		}
    		else{
    			e.escribirError(l.lin+"\tEl operando fuera de rango para el modo REL8\r\n",l.archierr);
    		}
    	}
    	else
    		if(modo.equals("REL16"))
    		{
    			if(oper>=-32768 && oper<=65535)
    			{
    				return true;
    			}
    			else
    			{
    				e.escribirError(l.lin+"\tEl operando fuera de rango para el modo REL16\r\n",l.archierr);
    			}
    		}
    return false;
    }
}


