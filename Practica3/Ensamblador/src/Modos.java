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
		if(l.operando.charAt(0)=='#')
	    	{
	    		if(t.modir.elementAt(0).equals("IMM")||t.modir.elementAt(0).equals("IMM8")||t.modir.elementAt(0).equals("IMM16"))
	    		{
	    			encontrado=true;
	    		}
	    		else
	    			e.escribirError(l.lin+"El codop no tiene direccionamiento IMM",l.archierr);
	    	}
	    	for(int i=0;i<= t.modir.size() && !encontrado; i++)
	    	{
	    		if(t.modir.elementAt(i).equals("INH"))
	    		{
	    			encontrado=modoInh();
	    		}
	    		/*else
	    			if(t.modir.equals())*/
	    	}
	    	return "modo";
	}

	/*public void convertirDecimal(String oper){
		int num;
		if(oper.charAt[0]=='%'){
			String n = oper.substring(oper.indexOf(%));
		}
		else
			if(oper.charAt[0]=='$'){
				String n = oper.substring(oper.indexOf($));
			}
			else
				if(oper.charAt[0]==''){
				}
				else
					{
					}

	}*/

	/*public void complementoADos(){

	}*/

	public boolean modoInh(){
		return true;
    }

    public boolean  modoImm(String modo){
    	if(modo.equals("IMM8"))
    	{
    		if(oper>= -256 && oper<=255){
    			return true;
    		}		
    	}
    	else
    		{
    			if(oper>= -32768 && oper<=65535){
    				return true;
    			}
    		}
    	e.escribirError(l.lin+"\tOperando fuera de rango para el direccionamiento IMM8",l.archierr);
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
}


