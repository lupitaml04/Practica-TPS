/**
 * @(#)Ensamblador.java
 *
 * Ensamblador application
 *
 * @author 
 * @version 1.00 2013/9/13
 */
 
import java.util.*;
import java.io.*;

 class Ensamblador {
	String ruta, texto, archivo,archivoInst,archivoErr;
	Vector<Tabop> tabop=new Vector<Tabop>();
	int linea =0;
	boolean end;

	public Ensamblador(String r){
		ruta=r;
	}

	public Ensamblador(){
	}

	public void validarRuta(){
		File fichero = new File(ruta);
        if (fichero.exists())
        {
        	if(fichero.getName().toUpperCase().endsWith(".ASM")){
        		leerTabop();
        	    crearArchivos();
        	    leerArchivo();
        	}
              else
              	{
        			System.out.println("la extension es incorrecta");
        		}
        }
        else
        	System.out.println("La ruta no existe");
     }

	public void crearArchivos(){
		archivoInst=ruta.substring(0,ruta.indexOf('.'))+".INST";
		archivoErr=ruta.substring(0,ruta.indexOf('.'))+".ERR";
		File inst = new File(archivoInst);
		File err = new File(archivoErr);
		 if(inst.exists())
		 	inst.delete();
		 if(err.exists())
		 	err.delete();
		try{
			BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archivoInst), true));
			archinst.write("LINEA\tETQ\tCODOP\tOPER\tMODOS");
			archinst.write("\r\n...........................................................\r\n");
			archinst.close();
		}
	    catch(IOException e){
					System.out.println("Error");
	    	}
	    try{
	    	BufferedWriter archierr = new BufferedWriter(new FileWriter(new File(archivoErr), true));
		 	archierr.write("\tERRORES\r\n............................................\r\n");
			archierr.close();
		}
	    catch(IOException e2){
		   	System.out.println("Error");
	    }
	}

	public void leerArchivo() {
		try{
				RandomAccessFile archi=new RandomAccessFile(new File(ruta),"r");
				while(archi.getFilePointer()!=archi.length() && !end)
					{
						texto=archi.readLine();
						linea++;
						revisarLinea();
		    		}
		    	if(!end)
		    		escribirError("	No hay END",archivoErr);

		    	archi.close();
		    }
	        catch(IOException e)
	        	{
		      		System.out.println("Error");
	        	}
	}

    public void revisarLinea(){
    	Linea lin= new Linea(linea,archivoErr);
    	String eti=null, codop=null, oper=null, modos=null;
        int cont=0, edo=0, tam=texto.length();
        char[] cad = texto.toCharArray();
        boolean etiq=false, cod=false, op=false;

         while(edo != 10)
         {
        	switch(edo)
        	{
        		case 0:
        			if(cont==tam)
        				{
        					edo=10;
        				}else
        					if(cad[cont]==';')
        						{
        							edo=1;
    			        			cont++;
    		    				}else
    								if(cad[cont]==' ' || cad[cont]=='\t')
    									{
    										edo=4;
    										etiq=true;
    										cont++;
    									}else
    										{
    											eti=cad[cont]+"";
    											cont++;
    											edo=3;
    										}
    		     break;
    		     case 1:
    		     	if(tam==cont)
    		     		edo=10;
    		     		else
    		     			{
    		     				cont++;
    					    	edo=2;
    				    	}
     	         break;
     	         case 2:
    		     	if(tam==cont)
    		     		edo=10;
    		     		else
    		     			{
    							cont++;
    					    	edo=2;
    				    	}
     	         break;
     	         case 3:
     	         	if(cont==tam)
     	         		{
     	             		escribirError(linea+"	No hay Codigo de operacion\r\n",archivoErr);
     	         			edo=10;
     	         		}else
     	         			if(cad[cont]==';')
     	         				{
     	         					escribirError(linea+"	Linea incorrecta\r\n",archivoErr);
     	         					edo=1;
     	         					cont++;
     	         				}else
     	         					if(cad[cont]==' ' || cad[cont]=='\t')
     	         						{
     	         							etiq=lin.validarEtiqueta(eti);
     	         							cont++;
     	         							edo=5;
     	         						}else
     	         							{
     	         								eti+=cad[cont];
     	         		    					cont++;
     	         		    					edo=3;
     	         	    					}
     	         break;
     	         case 4:
     	         	if(tam==cont)
     	         		{
     	         			edo=10;
     	         		}else
     	         			if(cad[cont]==';')
     	         				{
     	         					edo=1;
     	         					cont++;
     	         				}else
     	         					if(cad[cont]==' ' || cad[cont]=='\t')
     	         						{
     	         							cont++;
     	         							edo=4;
     	         						}else
     	         							{
     	         								if((cad[cont]=='E'||cad[cont]=='e')&& (cont+3)<=tam)
     	         									{
     	         										String subStr=texto.substring(cont, cont+3);
                                						if(subStr.toUpperCase().equals("END"))
                                							{
                                								end=true;
                                								edo=10;
                                								codop=subStr;
                                								cod=true;
                                								op=true;
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
     	         	if(tam==cont)
     	         		{
     	         			escribirError("Linea "+ linea+" No hay codigo de operacion ",archivoErr);
     	                	edo=10;
     	         		}else
     	         			if(cad[cont]==';')
     	         				{
     	         					escribirError("Linea "+ linea+" No hay codigo de operacion ",archivoErr);
     	                    		edo=1;
     	                    		cont++;
     	         				}else
     	         					if(cad[cont]==' '|| cad[cont]=='\t')
     	         						{
     	         							cont++;
     	         							edo=5;
     	         						}else
     	         							{
     	         								if((cad[cont]=='E'||cad[cont]=='e')&& (cont+3)<=tam)
     	         									{
     	         										String subStr=texto.substring(cont, cont+3);
                                						if(subStr.toUpperCase().equals("END"))
                                							{
                                								end=true;
                                								edo=10;
                                								codop=subStr;
                                								cod=true;
                                								op=true;
                                							}
     	         									}
     	         								if(!end){
     	         									codop=cad[cont]+"";
     	         									cont++;
     	         									edo=6;
     	         								}
     	         						}
     	         break;
     	         case 6:
     	         	if(tam==cont)
     	         		{
     	         			cod=lin.validarCodigo(codop);
     	         			edo=10;
     	         			op=true;
     	         		}else
     	         			if(cad[cont]==';')
     	         				{
     	         					edo=1;
     	         					cod=lin.validarCodigo(codop);
     	         					op=true;
     	         				}else
     	         					if(cad[cont]==' '|| cad[cont]=='\t')
     	         						{
     	         							cod=lin.validarCodigo(codop);
     	         							cont++;
     	         							edo=7;
     	         						}else
     	         							{
     	         								codop=codop+cad[cont];
     	         								edo=6;
     	         								cont++;
     	         							}
     	         break;
     	         case 7:
     	         	if(tam==cont)
     	         		{
     	         			op=true;
     	         			edo=10;
     	         		}else
     	         			if(cad[cont]==' '|| cad[cont]=='\t')
     	         				{
     	         					cont++;
     	         					edo=7;
     	         				}else
     	         					if(cad[cont]==';')
     	         						{
     	         							cont++;
     	         							edo=1;
     	         							op=true;
     	         						}else
     	         							{
     	         								oper=cad[cont]+"";
     	         								cont++;
     	         								edo=8;
     	         							}
     	         break;
     	         case 8:
     	         	if(tam==cont)
     	         		{
     	         			op=lin.validarOperando(oper);
     	         			edo=10;
     	         		}else
     	         			if(cad[cont]==';')
     	         				{
     	         					op=lin.validarOperando(oper);
     	         					edo=1;
     	         					cont++;
     	         				}else
     	         					if(cad[cont]==' ' || cad[cont]=='\t')
     	         						{
     	         							op=lin.validarOperando(oper);
     	         							cont++;
     	         							edo=9;
     	         						}
     	         						else
     	         							if(cad[cont]==';')
     	         								{
     	         									op=lin.validarOperando(oper);
     	         									cont++;
     	         									edo=1;
     	         								}else
     	         									{
     	         										oper+=cad[cont];
     	         										cont++;
     	         									}
     	         break;
     	         case 9:
     	         	if(tam==cont){
     	         		edo=10;
     	         	}
     	         	else
     	         		if(cad[cont]==' ' || cad[cont]=='\t'){
     	         			cont++;
     	         		}
     	         		else
     	         			if(cad[cont]==';'){
     	         				cont++;
     	         				edo=1;
     	         			}
     	         			else
     	         				{
     	         					escribirError(linea+"	Exceso    de    tokens\r\n",archivoErr);
     	         					op=false;
     	         					edo=10;
     	         				}
     	         break;
     }
    }
    	if((etiq && cod) && op)
    	{
    		modos=buscarCodop(codop,oper);
    	    if(modos != null)
    			{
    				if(eti==null)
    					eti="NULL";
    				if(oper==null)
    					oper="NULL";
    				escribirInstruccion(linea,"\t"+ eti+"\t"+codop+"\t"+oper+"\t"+modos+"\r\n");
    			}		
    	}
    }

    public void escribirError(String error, String archi){
    	archivoErr=archi;
    	try{
    		BufferedWriter archierr = new BufferedWriter(new FileWriter(new File(archivoErr), true));
			archierr.write(error);
			archierr.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	      }
    }

    public void escribirInstruccion(int lin, String instruccion){
         String l= Integer.toString(lin);  
    	try{
    		BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archivoInst), true));
    		archinst.write(l);
			archinst.write(instruccion);
			archinst.close();
		}
	      catch(IOException e){
		    System.out.println("Error");
	       }
    }
    
    public void leerTabop(){
    	String l,rt;
    	Scanner Leer=new Scanner(System.in);
    	System.out.println("¿Cual es la ruta del archivo tabop?");
    	rt=Leer.nextLine(); 	
				
    	try{
    		RandomAccessFile archi=new RandomAccessFile(new File(rt),"r");
			while(archi.getFilePointer()!=archi.length())
				{
					Tabop t=new Tabop();
					l=archi.readLine();
					StringTokenizer st = new StringTokenizer(l,"|");
					t.agregar(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken());
					tabop.addElement(t);
		   		}
		   		
		    	archi.close();
		    }
	        catch(IOException e)
	        	{
		      		System.out.println("Error");
	        	}
	       
    }
    
    public String buscarCodop(String c,String op){
    	boolean bcod=false,band=true;
    	int cont=0;
    	String modos=null;
    	if(c.toUpperCase().equals("ORG") || c.toUpperCase().equals("END"))
    	{
    		return " ";
    	}
    	
    	for(int i=0; i<tabop.size() && band; i++)
    		{
    			Tabop t=new Tabop();
    			t=tabop.elementAt(i);
				if(c.toUpperCase().equals(t.cod))
				{
					bcod=true;
					cont++;
					if(cont==1)
					{
						modos=t.modir;
						if(t.boper.equals("1")&& op==null)
						{
							escribirError(linea+"\tEl codigo de operacion requiere operando\r\n",archivoErr);
							return null;
						}
						else
							if(t.boper.equals("0")&& op!=null)
							{
								escribirError(linea+"\tEl codigo de operacion no requiere operando\r\n",archivoErr);
								return null;
							}					
					}
					else
					{
						modos+=","+t.modir;
					}
				}
				else
				{
					if(cont>1)
						band=false;
				}
			} 
	    if(!bcod)
	    {
	    	escribirError(linea+ "\tEl codigo de operacion no se encontra en el tabop\r\n",archivoErr);
	    }
		return modos; 	
    }

    public static void main(String[] args){
    	Scanner Leer=new Scanner(System.in);
    	String ruta;
    	System.out.println("¿Cual es la ruta del archivo?");
    	ruta=Leer.nextLine();
    	Ensamblador obj= new Ensamblador(ruta);
    	obj.validarRuta();   	
    }
}

