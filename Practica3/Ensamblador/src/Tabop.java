import java.util.*;
class Tabop {
	String cod;
	Vector<String> boper= new Vector<String>();
	Vector<String> modir= new Vector();
	Vector<String> comaq= new Vector<String>();
	Vector<String> bcalcu= new Vector<String>();
	Vector<String> bxcalcu= new Vector<String>();
	Vector<String> btotal= new Vector<String>();
			
	public Tabop(){
	}	
		
	public void agregar(String cod,String bop,String mdir, String cmaq,String bcal,String bxcal, String btot)
	{
		this.cod=cod;
		boper.add(bop);
		modir.add(mdir);
		comaq.add(cmaq);
		bcalcu.add(bcal);
		bxcalcu.add(bxcal); 
		btotal.add(btot);
	}
	
	public void mostrar(){
		System.out.println(""+cod);
		for(int i=0;i<boper.size();i++)
		{
			System.out.println(modir.elementAt(i)+"\t"+boper.elementAt(i)+"\t"+bxcalcu.elementAt(i)+"\t"+bcalcu.elementAt(i)+"\t"+ btotal.elementAt(i));
		}
	}
	
}
