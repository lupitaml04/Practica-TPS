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
							}	
							
							if(!encontrado)
							if(l.operando.charAt(0)=='#')
							{
								e.escribirError(l.lin+"El codop no tiene direccionamiento IMM\r\n",l.archierr);	
							}			
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
			num=Integer.parseInt(n,2);
			System.out.println(num);
		}
		else
			if(opera.charAt(0)=='$'){
				String n = opera.substring(1);
				num=Integer.parseInt(n,16);
				System.out.println(num);		
			}
			else
				{
					 num = Integer.parseInt(opera);
					
			    }
	return num;

	}

	/*public void complementoADos(){

	}*/

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
    		e.escribirError(l.lin+"Operando fuera de rango para el modo ext\r\n",l.archierr);
    	}
    	return false;
    }
}


