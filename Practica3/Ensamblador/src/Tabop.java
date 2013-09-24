
class Tabop {
	String cod, boper,modir, comaq,bcalcu, bxcalcu, btotal;	
		
	public Tabop(){
	}	
		
	public void agregar(String cod,String boper,String modir, String comaq,String bcalcu,String bxcalcu, String btotal)
	{
		this.cod=cod;
		this.boper=boper;
		this.modir=modir;
		this.comaq=comaq;
		this.bcalcu=bcalcu;
		this.bxcalcu=bxcalcu; 
		this.btotal=btotal;
	}
	
	public void mostrar()
	{
		System.out.println(cod+"\t"+boper+"\t"+modir+"\t"+ comaq+"\t"+bcalcu+"\t"+bxcalcu+"\t"+btotal);
	}
}
