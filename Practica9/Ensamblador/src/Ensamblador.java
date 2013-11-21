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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

 class Ensamblador {
        String ruta, texto, archivo,archivoInst,archivoErr,archivoT;
        Vector<Tabop> tabop=new Vector<Tabop>();
        int linea =0;
        int conLoc=0;
        boolean end,eOrg=false, dir_ini=false;
        Vector dir= new Vector();
        Vector<String> tabSim=new Vector();
        public Ensamblador(String r){
                ruta=r;
                dir.add("DB");
                dir.add("DC.B");
                dir.add("FCB");
                dir.add("DW");
                dir.add("DC.W");
                dir.add("FDB");
                dir.add("FCC");
                dir.add("DS");
                dir.add("DS.B");
                dir.add("RMB");
                dir.add("DS.W");
                dir.add("RMW");
        }

        public Ensamblador(){
        }

        public void validarRuta(){
        	File fichero = new File(ruta);
        	if (fichero.exists())
        	{
        		if(fichero.getName().toUpperCase().endsWith(".ASM")){
        			leerTabop();
                    crearArchivoErr();
                    crearArchivoTds();
                    crearArchvoInst();
                    leerArchivo();
                }
              else{
              	System.out.println("la extension es incorrecta");
              }
        }
        else
        	System.out.println("La ruta no existe");
     }

     public void crearArchivoErr(){
     	archivoErr=ruta.substring(0,ruta.indexOf('.'))+".ERR";
     	File err = new File(archivoErr);
     	if(err.exists())
     		err.delete();
     	try{
     		BufferedWriter archierr = new BufferedWriter(new FileWriter(new File(archivoErr), true));
     		archierr.write("\tERRORES\r\n............................................\r\n");
     		archierr.close();
     		}
            catch(IOException e2){
            	System.out.println("Error al crear archivo de errores");
            	}
     }

     public void crearArchivoTds(){
     	archivoT=ruta.substring(0,ruta.indexOf('.'))+".TDS";
     	File tds =new File(archivoT);
     	 if(tds.exists())
     	 {
     	 	tds.delete();
     	 }
     	  try{
     	  	BufferedWriter archiT = new BufferedWriter(new FileWriter(new File(archivoT), true));
     	  	archiT.write("ETIQUETA\tVALOR\r\n............................................\r\n");
     	  	archiT.close();
     	  	}
            catch(IOException e3){
            	System.out.println("Error al crear archivo de tabla de simbolos");
            }
     }

        public void crearArchvoInst(){
        	archivoInst=ruta.substring(0,ruta.indexOf('.'))+".INST";
                File inst = new File(archivoInst);
                 if(inst.exists())
                         inst.delete();
                 try{
                        BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archivoInst), true));
                        archinst.write("LINEA\tCONTLOC\tETQ\tCODOP\tOPER\tMODOS\tCOMAQ");
                        archinst.write("\r\n...........................................................\r\n");
                        archinst.close();
                     }
            catch(IOException e){
            	System.out.println("Error al crear archivo de instrucciones");
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
                		escribirError("        No hay END",archivoErr);
                		archi.close();
                    }
                catch(IOException e){
                	System.out.println("Error");
                }
                Vector<String> ins=guardaInst();
                guardaTabSim();
                int errores=contError(), mal=ins.size();
                ins=revisarInst(ins);
                mal=mal-ins.size();
                ins=recalConLoc(ins,mal);
                //if(mal==0 && errores ==0)
                	ins=calcularCM(ins);   
                //else{
                	escribirError("No se puede pasar al paso 2 del ensamblador",archivoErr);
                //}
                	
		crearArchvoInst();
		for(int i=0;i<ins.size();i++)
			escribirInstruccion(ins.elementAt(i),archivoInst);
		crearArchivoTds();
		for(int i=0;i<tabSim.size();i++){
			StringTokenizer st = new StringTokenizer(tabSim.elementAt(i),"\t\t");
			escribirSimbolo(st.nextToken(),st.nextToken(),archivoT);
		}
			
        }

    public void revisarLinea(){
    	Linea lin= new Linea(linea,archivoErr,archivoInst,archivoT);
    	String eti=null, codop=null, oper=null;
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
                         		else{
                         				cont++;
                         				edo=2;
                                    }
                      break;
                      case 2:
                      	if(tam==cont)
                      		edo=10;
                            else{
                            	cont++;
                            	edo=2;
                            	}
                      break;
                      case 3:
                      	if(cont==tam)
                      	{
                      		escribirError(linea +"\tNo hay Codigo de operacion\r\n",archivoErr);
                      		edo=10;
                      	}else
                      		if(cad[cont]==';')
                      		{
                      			escribirError(linea+"\tLinea incorrecta\r\n",archivoErr);
                      			edo=1;
                      			cont++;
                      		}else
                      			if(cad[cont]==' ' || cad[cont]=='\t')
                      			{
                      				etiq=lin.validarEtiqueta(eti);
                      				cont++;
                      				edo=5;
                                }else{
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
                      							edo=6;
                      							codop=subStr;
                      							cod=lin.validarCodigo(codop);
                      							op=true;
                      							cont+=3;
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
                      		escribirError(linea+"\tNo hay codigo de operacion\r\n",archivoErr);
                      		edo=10;
                      	}else
                      		if(cad[cont]==';')
                      		{
                      			escribirError(linea+"\tNo hay codigo de operacion \r\n",archivoErr);
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
                      							edo=6;
                      							codop=subStr;
                      							cod=lin.validarCodigo(codop);
                      							op=true;
                      							cont+=3;
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
                      					if(cad[cont]=='\"')
                      					{
                      						boolean fc=false, com=false;
                      						int c=cont;
                      						while(!fc && c<texto.length())
                      						{
                      							c=texto.indexOf("\"",c+1);
                      							if(c<0)
                      								c=texto.length();
                      								else
                      									if(texto.charAt(c-1)!='\\')
                      									{
                      										fc=true;
                      										lin.opera(texto.substring(cont,c+1));
                      										cont+=texto.substring(cont,c+1).length();
                      										edo=9;
                      										op=true;
                      									}
                      									else{
                      										lin.opera(texto.substring(cont,c+1));
                      										edo=9;
                      									}
                      								}
                      								if(!fc)
                      								{
                      									edo=10;
                      								}
                      						}
                      						else{
                      							cont++;
                      							edo=8;
                      						}
                      				}
                      break;
                      case 8:
                      	if(tam==cont)
                      	{
                      		lin.opera(oper);
                      		edo=10;
                      	}else
                      		if(cad[cont]==';')
                      		{
                      			lin.opera(oper);
                      			edo=1;
                      			cont++;
                      		}else
                      			if(cad[cont]==' ' || cad[cont]=='\t')
                      			{
                      				lin.opera(oper);
                      				cont++;
                      				edo=9;
                      			}
                      			else
                      				if(cad[cont]==';')
                      				{
                      					lin.opera(oper);
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
                      			else{
                      					escribirError(linea+"        Exceso de tokens\r\n",archivoErr);
                      					op=false;
                      					edo=10;
                      				}
                      break;
     }
    }
    if(etiq && cod){
    	buscarCodop(lin);
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

    public void escribirInstruccion(String instruccion,String archiI){
            try{
                    BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archiI), true));
                    archinst.write(instruccion);
                    archinst.close();
                   }
              catch(IOException e){
                    System.out.println("Error");
               }
    }

    public void escribirSimbolo(String eti,String valor, String archi){
            archivoT=archi;
            try{
                    BufferedWriter archiT = new BufferedWriter(new FileWriter(new File(archivoT), true));  
                    archiT.write(eti);
                    archiT.write("\t\t");
                    archiT.write(valor.toUpperCase());
                    archiT.write("\t\t\r\n");
                    archiT.close();
                }
              catch(IOException e){
                    System.out.println("Error");
              }
    }

    public void leerTabop(){
            String l,rt, cod,codant=null;
            Scanner Leer=new Scanner(System.in);
            System.out.println("¿Cual es la ruta del archivo tabop?");
            rt=Leer.nextLine();

            try{
                    RandomAccessFile archi=new RandomAccessFile(new File(rt),"r");
                    while(archi.getFilePointer()!=archi.length())
                    {
                    	l=archi.readLine();
                    	StringTokenizer st = new StringTokenizer(l,"|");
                    	cod=st.nextToken();
                    	Tabop t=new Tabop();
                    	if(cod.equals(codant))
                    	{
                    		t=tabop.elementAt(tabop.size()-1);
                    		t.agregar(cod, st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken());
                    	}
                    	else
                    		{
                    			t.agregar(cod,st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken());
                    			tabop.addElement(t);
                    			codant=cod;
                    		}
                    	}
                    	archi.close();
                    }
                catch(IOException e){
                	System.out.println("Error");
                }
    }

    public void buscarCodop(Linea l){
            boolean bcod=false;
            if(l.etiqueta==null)
            	l.etiqueta="NULL";
            if(l.operando==null)
            	l.operando="NULL";
            bcod=directivas(l);
            for(int i=0; i<tabop.size() && !bcod; i++)
            {
            	Tabop t=new Tabop();
            	t=tabop.elementAt(i);
            	if(l.codigo.toUpperCase().equals(t.cod))
            	{
            		bcod=true;
            		if(t.boper.elementAt(0).equals("1")&& l.operando.equals("NULL"))
            		{
            			escribirError(linea+"\tEl codigo de operacion requiere operando\r\n",archivoErr);
            		}
            		else
            			if(t.boper.elementAt(0).equals("0")&& !l.operando.equals("NULL"))
            			{
            				escribirError(linea+"\tEl codigo de operacion no requiere operando\r\n",archivoErr);
            			}
            			else
            				{
            					Modos m=new Modos(l,t,conLoc);
            					int conLoca=m.buscarModo();
            					if(!dir_ini)
            					{
            						if(conLoca>conLoc)
            						{
            							dir_ini=true;
            							escribirError(linea+"\tFalto especificar la direccion inicial con un org\r\n",archivoErr);
            						}
            					}
            					conLoc=conLoca;
            					if(conLoc>65535)
            					{
            						escribirError(l.lin+"\tEl CONLOC  sobrepaso su rango\r\n",archivoErr);
            						conLoc=0;
            					}
            				}
            			}
            }
            if(!bcod)
            {
            	escribirError(l.lin+ "\tEl codigo de operacion no se encontro en el tabop\r\n",archivoErr);
            }
    }

    public boolean directivas(Linea l){
        boolean encon=false, dirC=false;
        int j,op=0,inC=0;
        Modos d=new Modos(l,new Tabop(),conLoc);
        if(l.operando.startsWith("\"") && !l.codigo.toUpperCase().equals("FCC"))
        {
            escribirError(l.lin+"\tFormato de operando invalido\r\n",l.archierr);
            return true;
        }
        if(l.codigo.toUpperCase().equals("ORG"))
        {
        	encon=true;
        	if(eOrg){
        		escribirError(l.lin+"\tSolo debe existir un ORG\r\n",archivoErr);
        	}
        	else
        		{
        			if(!(l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z' )||!(l.operando.charAt(0)>='A'&& l.operando.charAt(0)<='z'))
        				{
        					Modos mo=new Modos(l,new Tabop(),conLoc);
        					conLoc=mo.convertirDecimal(l.operando);
        					if(conLoc>=0 &&conLoc<=65535)
        					{
        						dirC=true;
        						dir_ini=true;
        						eOrg=true;
        					}
        					else{
        						escribirError(l.lin+"\nOperando de ORG fuera de rango\r\t",archivoErr);
        						return true;
        					}
        				}
        				else{
        					escribirError(l.lin+"\tLa directiva org no debe tener etiqueta como operando\r\n",archivoErr);
                            }
                    }
            }
            else
                    if(l.codigo.toUpperCase().equals("END"))
                    {
                    	encon=true;
                    	if(l.operando.equals("NULL")){
                    		dirC=true;
                    	}
                    	else{
                    		escribirError(l.lin+"\tEl END no debe tener operando\r\n",archivoErr);
                    		return true;
                    	}
                    }
                    else
                    	if(l.codigo.toUpperCase().equals("EQU"))
                    	{
                    		if(l.etiqueta.equals("NULL"))
                    		{
                    			escribirError(l.lin+"\tLa directiva EQU debe tener etiqueta\r\n",archivoErr);
                    			return true;
                    		}
                    		else
                    			{
                    				if(noNULL(l.operando,l.codigo))
                    					if(l.validarOperando(l.operando,false))
                    					{
                    						op=d.convertirDecimal2(l.operando);
                    						if(op>=0 && op<=65535)
                    						{
                    							String cL= Integer.toString(op,16);
                    							while(cL.length()<4)
                    								cL="0"+cL;
                    							if(!l.etiqueta.equals("NULL"))
                    							{
                    								if(!buscarEti(l.etiqueta,l.archiT))
                    								{
                    									escribirSimbolo(l.etiqueta,cL,l.archiT);
                    									escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t \t \t\r\n",l.archiInst);
                    								}
                    								else
                    									escribirError(l.lin+"\tLa etiqueta se repitio\r\n",l.archierr);
                    							}
                    							else{
                    								escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t \t \t\r\n",l.archiInst);
                    							}
                    							}
                    							else{
                    								escribirError(l.lin+"\tEl operando se paso de rango para la directiva "+l.codigo +"\r\n",l.archierr);
                    							}
                    						}
                    					}
                                    }
                            else
                            {
                                    for(j=0;j<dir.size()&&!encon;j++)
                                    {
                                    	if(l.codigo.toUpperCase().equals(dir.elementAt(j)))
                                    	{
                                    		encon=true;
                                    	}
                                    }
                                    if(!encon)
                                    	return false;
                                    j--;
                                    if(j>=0&&j<=2)
                                    {
                                    	if(noNULL(l.operando,l.codigo))
                                    		if(l.validarOperando(l.operando,false))
                                    		{
                                    			op=d.convertirDecimal(l.operando);
                                    			if(op>=0 && op<=255)
                                    			{
                                    				dirC=true;
                                    				inC=1;
                                    			}
                                    			else{
                                    				escribirError(l.lin+"\tEl operando se paso de rango para la directiva "+l.codigo +"\r\n",l.archierr);
                                    			}
                                    		}
                                    }
                                    else
                                    	if(j>=3 &&j<=5)
                                    	{
                                    		if(noNULL(l.operando,l.codigo))
                                    			if(l.validarOperando(l.operando,false))
                                    			{
                                    				op=d.convertirDecimal2(l.operando);
                                    				if(op>=0 && op<=65535)
                                    				{
                                    					dirC=true;
                                    					inC=2;
                                    				}
                                    				else{
                                    					escribirError(l.lin+"\tEl operando se paso de rango para la directiva "+l.codigo +"\r\n",l.archierr);
                                    				}
                                    			}
                                            }
                                            else
                                            	if(j==6)
                                            	{
                                            		if(!(l.operando.startsWith("\"")&&l.operando.endsWith("\"")))
                                            		{
                                            			escribirError(l.lin+"\tFormato de operando invalido para "+l.codigo +" ,"+l.operando+",\r\n",l.archierr);
                                            			return true;
                                                     }
                                                     int c=0,tam=l.operando.length()-2;
                                                     char ca;
                                                     while(c!=l.operando.length())
                                                     {
                                                     	c=l.operando.indexOf("\\",c+1);
                                                     	if(c<0)
                                                     		c=l.operando.length();
                                                     	else{
                                                     		ca=l.operando.charAt(c+1);
                                                     		if(ca=='\"'|| ca=='a'|| ca=='n' || ca=='t' || ca=='s'|| ca=='r'|| ca=='b'||ca=='\\')
                                                     		{
                                                     			c++;
                                                     			tam--;
                                                     		}
                                                     	}
                                                     	}
                                                     	dirC=true;
                                                     	inC=tam;
                                                    }
                                                    else
                                                    	if(j>=7 && j<=9)
                                                    	{
                                                    		if(noNULL(l.operando,l.codigo))
                                                    			if(l.validarOperando(l.operando,false))
                                                    			{
                                                    				op=d.convertirDecimal2(l.operando);
                                                    				if(op>=0 && op<=65535)
                                                    				{
                                                    					dirC=true;
                                                    					inC=op;
                                                    				}
                                                    				else{
                                                    					escribirError(l.lin+"\tEl operando se paso de rango para la directiva "+l.codigo +"\r\n",l.archierr);
                                                    				}
                                                    			}
                                                            }
                                                            if(j==10 || j==11)
                                                            {
                                                            	if(noNULL(l.operando,l.codigo))
                                                            		if(l.validarOperando(l.operando,false))
                                                            		{
                                                            			op=d.convertirDecimal2(l.operando);
                                                            			if(op>=0 && op<=65535)
                                                            			{
                                                            				dirC=true;
                                                            				inC=op*2;
                                                            			}
                                                            			else
                                                            				{
                                                            					escribirError(l.lin+"\tEl operando se paso de rango para la directiva "+l.codigo +"\r\n",l.archierr);
                                                            				}
                                                            		}
                                                            }
                            }
        if(dirC)
        {
        	String cL= Integer.toString(conLoc,16);
            while(cL.length()<4)
            	cL="0"+cL;
            if(!dir_ini && !l.codigo.toUpperCase().equals("EQU"))
            {
            	escribirError(linea+"\tFalto especificar la direccion inicial con un Org esta inicio con 0\r\n",archivoErr);
            	dir_ini=true;
            }
            if(!l.etiqueta.equals("NULL"))
            {
            	if(!buscarEti(l.etiqueta,l.archiT))
            	{
            		escribirSimbolo(l.etiqueta,cL,l.archiT);
            		escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t \t"+inC+"\t\r\n",l.archiInst);
            		conLoc+=inC;
            	}
            	else
                    escribirError(l.lin+"\tLa etiqueta se repitio\r\n",l.archierr);
            }
            else{
                    escribirInstruccion(l.lin+"\t"+cL+"\t"+ l.etiqueta+"\t"+l.codigo+"\t"+l.operando+"\t \t"+inC+"\t\r\n",l.archiInst);
                    conLoc+=inC;
                    if(conLoc>65535)
                    {
                    	escribirError(l.lin+"\tEL conLoc sobrepaso su rango\r\n",archivoErr);
                    	conLoc=0;
                    }
            }
        }
        return true;
}

public boolean noNULL(String operando,String dir)
{
	if(operando.equals("NULL"))
    {
    	escribirError(linea+"\tLa directiva "+dir+" debe tener operando\r\n",archivoErr);
    	return false;
    }
    return true;
}

public boolean buscarEti(String eti,String archiT)
{
        boolean encon=false;
        try{
                RandomAccessFile archi=new RandomAccessFile(new File(archiT),"r");
                while(archi.getFilePointer()!=archi.length() && !encon)
                {
                	String l=archi.readLine();
                	StringTokenizer st = new StringTokenizer(l,"\t");
                	String etiq=st.nextToken();
                	if(etiq.equals(eti))
                		encon=true;
                }
                archi.close();
        }
        catch(IOException e)
        {
                System.out.println("Error al abrir archivo TDS");
        }
        return encon;
}

public boolean buscarEti2(String eti)
{
	for(int i=0;i<tabSim.size();i++)
	{
		String l=tabSim.elementAt(i);
		StringTokenizer st = new StringTokenizer(l,"\t");
		String etiq=st.nextToken();
		if(etiq.equals(eti))
			return true;
	}       
        return false;
}

public Vector<String> revisarInst(Vector<String> ins)
{
	String lIns,linea,valor,etiqueta,codop,operando,modir,tam,cL="0000";
	int mal=0;
		boolean continua=true;
		while(continua){
			continua=false;
			for(int j=0; j< ins.size() && !continua; j++)
			{
				lIns=ins.elementAt(j);
				StringTokenizer st = new StringTokenizer(lIns,"\t");
				linea=st.nextToken();
				valor=st.nextToken();
				etiqueta=st.nextToken();
				codop=st.nextToken();
				operando=st.nextToken();
				modir=st.nextToken();
				tam=st.nextToken();
				Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,}");
            	Matcher mat = pat.matcher(operando);
            	while(cL.length()<4)
            		cL="0"+cL;
            	if (mat.matches()&& !operando.equals("NULL"))
            	{
            		if(!buscarEti2(operando))
            		{
            			mal++;
            			escribirError(linea+"\tLa etiqueta del operando no existe\r\n",archivoErr);
            			if(elimEti(etiqueta))
            			{
            				continua=true;
            			}
            			ins.remove(j);
            			j--;
            		}
            	}
			}
		}
		return ins;
}

public Vector<String> guardaInst(){
	Vector<String> ins= new Vector<String>();
	String lIns;
	try
		{
			RandomAccessFile archi=new RandomAccessFile(new File(archivoInst),"r");
			archi.readLine();
			archi.readLine();
			while(archi.getFilePointer()!=archi.length())
			{
				lIns=archi.readLine();
				ins.add(lIns);
			}
			archi.close();
		}
		catch(IOException e){
			System.out.println("Error al abrir archivo de instrucciones");
		}
		
		return ins;
}

public void guardaTabSim(){
	String ts;
	try
		{
			RandomAccessFile archi=new RandomAccessFile(new File(archivoT),"r");
			archi.readLine();
			archi.readLine();
			while(archi.getFilePointer()!=archi.length())
			{
				ts=archi.readLine();
				tabSim.add(ts);
			}
			archi.close();
		}
		catch(IOException e){
			System.out.println("Error al abrir archivo de instrucciones");
		}
}

public Vector<String> recalConLoc(Vector<String> inst,int mal){
	Vector<String> ins=new Vector<String>();
	String cL="0000";
	tabSim.clear();	
	for(int i=0; i<inst.size();i++)
	{
		int tam=0;
		String lIns=inst.elementAt(i);
		StringTokenizer st = new StringTokenizer(lIns,"\t");
		String linea=st.nextToken();
		String valor=st.nextToken();
		String etiqueta=st.nextToken();
		String codop=st.nextToken();
		String operando=st.nextToken();
		String modir=st.nextToken();
		while(cL.length()<4)
			cL="0"+cL;		
		if(mal==0)
		{
            lIns=linea+"\t"+ valor.toUpperCase()+"\t" +etiqueta+"\t"+codop+"\t"+operando+"\t"+ modir+"\t\r\n";
            ins.add(lIns);
            if(!etiqueta.equals("NULL"))
           		tabSim.add(etiqueta+"\t\t"+valor+"\t\t\r\n");
        }
           else
           	{
           		tam=tamIns(codop,operando,modir);
           		if(!codop.toUpperCase().equals("ORG") && !codop.toUpperCase().equals("EQU")){
           		valor=cL;
           		}
           		lIns=linea+"\t"+ valor.toUpperCase()+"\t" +etiqueta+"\t"+codop+"\t"+operando+"\t"+ modir+"\t\r\n";
           		if(codop.equals("ORG"))
           			cL=valor;	
           			else
           				if(!codop.equals("EQU"))
           					cL=Integer.toHexString(Integer.parseInt(valor,16)+tam);
           				ins.add(lIns);
           				if(!etiqueta.equals("NULL"))
           					tabSim.add(etiqueta+"\t\t"+valor+"\t\t\r\n");
           		}
	}
	return ins;		
}

public int tamIns(String codop,String operando,String modir){
	boolean bcod=false;
	int j, tam=0;
	for(int i=0;i<dir.size()&&!bcod;i++)
	{
		if(codop.toUpperCase().equals(dir.elementAt(i)))
		{
			if(i>=0&&i<=2)
				tam=1;
			if(i>=3 && i<=5)
				tam=2;
			if(i==6)
			{
				int c=0,tama=operando.length()-2;
				char ca;
				while(c!=operando.length())
				{
					c=operando.indexOf("\\",c+1);
					if(c<0)
						c=operando.length();
						else{
							ca=operando.charAt(c+1);
							if(ca=='\"'|| ca=='a'|| ca=='n' || ca=='t' || ca=='s'|| ca=='r'|| ca=='b'||ca=='\\')
							{
								c++;
								tama--;
							}
							tam=tama;
						}
				}
			}
			else
					if(i>=7 && i<=9)
					{
						Modos m=new Modos();
						tam=m.convertirDecimal(operando);
					}
					else
						if(i==10 || i==11)
						{
							Modos m=new Modos();
							tam=m.convertirDecimal(operando)*2;
						}
				
			bcod=true;
		}
	}
	for(int i=0; i<tabop.size() && !bcod; i++)
	{
		Tabop t=new Tabop();
		t=tabop.elementAt(i);
		boolean encon=false;
		if(codop.toUpperCase().equals(t.cod))
		{
			for(int k=0; k<t.modir.size()&& !encon;k++)
			{
				if(t.modir.elementAt(k).equals(modir))
				{
					encon=true;
					tam=Integer.parseInt(t.btotal.elementAt(k));
				}
			}
			bcod=true;
		}
	}
	return tam;
}
public String valorEti(String eti)
{
	String l,etiq,valor;
	for(int i=0; i<tabSim.size();i++)
	{
		l=tabSim.elementAt(i);
		StringTokenizer st = new StringTokenizer(l,"\t\t");
		etiq=st.nextToken();
		valor=st.nextToken();
		if(etiq.equals(eti))
			return valor;
	}
      return null;
}

public Vector<String> calcularCM(Vector<String> ins){
		Vector<String> inst= new Vector<String>();	
		Modos m= new Modos();
		for(int k=0;k<ins.size();k++)
		{
			boolean mal=false, bcod=false;
			String lIns,comaq=null,linea_, valor,etiqueta, codop, operando, modir;
			Tabop ta=new Tabop();
			lIns=ins.elementAt(k);
			System.out.println(lIns);
			StringTokenizer st = new StringTokenizer(lIns,"\t");
			linea_=st.nextToken();
			valor=st.nextToken();
			etiqueta=st.nextToken();
			codop=st.nextToken();
			operando=st.nextToken();
			modir=st.nextToken();
			comaq="";
			if(codop.toUpperCase().equals("DB")||codop.toUpperCase().equals("DB")||codop.toUpperCase().equals("DB"))
			{
				comaq=convertirHexa(operando,1);
			}
			else
				if(codop.toUpperCase().equals("DW")||codop.toUpperCase().equals("DC.W")||codop.toUpperCase().equals("FDB"))
				{
					comaq=convertirHexa(operando,2);
				}
				else
					if(codop.toUpperCase().equals("FCC"))
					{
						for(int i=1;i<(operando.length()-1);i++)
						{
							if(operando.charAt(i)=='\\')
							{
								char car;
								if(operando.charAt(i+1)=='n')
									car='\n';
								else
									if(operando.charAt(i+1)=='t')
										car='\t';
										else
											if(operando.charAt(i+1)=='r')
												car='\r';
												else
													if(operando.charAt(i+1)=='\"')
														car='\"';
														else
															if(operando.charAt(i+1)=='\\')
																car='\\';
																else
																	car=operando.charAt(i+1);
								comaq+=convertirHexa(""+(int) car,1);
								System.out.println(operando.charAt(i)+","+convertirHexa(""+(int) car,1));
					
								i++;
							}
							else{
								comaq+=convertirHexa(""+(int) operando.charAt(i),1);
								System.out.println(operando.charAt(i)+","+convertirHexa(""+(int) operando.charAt(i),1));
					
							}
								}
					}
			for(int i=0; i<tabop.size() && !bcod; i++)
			{
				Tabop t=new Tabop();
				t=tabop.elementAt(i);
				if(codop.toUpperCase().equals(t.cod))
				{
					ta=t;
					bcod=true;
				}
			}
			if(bcod)
			{
				boolean encon=false;
				int j=0;
				for(int i=0; i<ta.modir.size()&& !encon;i++)
				{
					if(ta.modir.elementAt(i).equals(modir))
					{
						j=i;
						encon=true;
					}
				}
				if(modir.equals("INH") || modir.equals("IMM")){
					comaq=ta.comaq.elementAt(j);
					}
					else
						if(modir.equals("DIR")){
							comaq=ta.comaq.elementAt(j)+convertirHexa(operando,1);
						}
						else
							if(modir.equals("EXT"))
							{
								Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]{0,}");
								Matcher mat = pat.matcher(operando);
								if (mat.matches()){
									comaq=ta.comaq.elementAt(j)+valorEti(operando);
								}
								else{
									comaq=ta.comaq.elementAt(j)+convertirHexa(operando,2);
								}
							}
							else
								if(modir.equals("IMM8")){
									comaq=ta.comaq.elementAt(j)+convertirHexa(operando.substring(1),1);
								}
								else
									if(modir.equals("IMM16")){
										comaq=ta.comaq.elementAt(j)+convertirHexa(operando.substring(1),2);
									}
									else
										if(modir.equals("IDX"))
										{
											String rr=null,aa=null,xb=null,nnnnn=null,p=null;
											String oper1=operando.substring(0,operando.indexOf(','));
											String oper2=operando.substring(operando.indexOf(',')+1);
											if(oper1.toUpperCase().equals("A")||oper1.toUpperCase().equals("B")||oper1.toUpperCase().equals("D"))
											{
												rr=regresaRr(oper2);
												if(oper1.toUpperCase().equals("A"))
													aa="00";
													else
														if(oper1.toUpperCase().equals("B"))
															aa="01";
															else
																if(oper1.toUpperCase().equals("D"))
																	aa="10";
												xb="111"+rr+"1"+aa;
											}
											else
												if(oper2.startsWith("-") || oper2.startsWith("+")||oper2.endsWith("-") || oper2.endsWith("+"))
												{
													if(oper2.startsWith("-") || oper2.startsWith("+"))
													{
														rr=regresaRr(oper2.substring(1));
														p="0";
													}
													else{
														rr=regresaRr(oper2.substring(0,oper2.length()-1));
														p="1";
													}
														int s;
													if(oper2.startsWith("-")||oper2.endsWith("-"))
													{
														nnnnn=Integer.toBinaryString((m.convertirDecimal(oper1))*(-1));
													}
													else
													{
														nnnnn=Integer.toBinaryString((m.convertirDecimal(oper1)-1));
													}

													while(nnnnn.length()<4)
														nnnnn="0"+nnnnn;
													while(nnnnn.length()>4)
														nnnnn=nnnnn.substring(1);
													xb=	rr+"1"+p+nnnnn;
												}
												else{
													rr=regresaRr(oper2);
													nnnnn=Integer.toBinaryString(m.convertirDecimal(oper1));
													while(nnnnn.length()<5)
														nnnnn="0"+nnnnn;
													while(nnnnn.length()>5)
														nnnnn=nnnnn.substring(1);
													xb=rr+"0"+nnnnn;
													}
											comaq=ta.comaq.elementAt(j)+convertirHexa("%"+xb,1);
									}
									else
										if(modir.equals("IDX1"))
										{
											String xb, rr,z="0",s,oper1=operando.substring(0,operando.indexOf(',')), oper2=operando.substring(operando.indexOf(',')+1);
											rr=regresaRr(oper2);
											if(m.convertirDecimal(oper1)<0)
												s="1";
											else
												s="0";
											xb="111"+rr+"0"+z+s;
											comaq=ta.comaq.elementAt(j)+convertirHexa("%"+xb,1)+convertirHexa(oper1,1);
										}
										else
											if(modir.equals("IDX2"))
											{
												String xb, rr,z="1",s,oper1=operando.substring(0,operando.indexOf(',')), oper2=operando.substring(operando.indexOf(',')+1);
													rr=regresaRr(oper2);
												if(m.convertirDecimal(oper1)<0)
													s="1";
													else
														s="0";
												xb="111"+rr+"0"+z+s;
												comaq=ta.comaq.elementAt(j)+convertirHexa("%"+xb,1)+convertirHexa(oper1,2);
											}
											else
												if(modir.equals("[IDX2]"))
												{
													String rr,xb, oper1=operando.substring(1,operando.indexOf(',')), oper2=operando.substring(operando.indexOf(',')+1, operando.length()-1);
													rr=regresaRr(oper2);
													xb="111"+rr+"011";
													comaq=ta.comaq.elementAt(j)+convertirHexa("%"+xb,1)+convertirHexa(oper1,2);
												}
												else
													if(modir.equals("[D,IDX]"))
													{
														String xb,rr, oper1=operando.substring(1,operando.indexOf(',')), oper2=operando.substring(operando.indexOf(',')+1, operando.length()-1);
														rr=regresaRr(oper2);
														xb="111"+rr+"111";
														comaq=ta.comaq.elementAt(j)+convertirHexa("%"+xb,1);

													}
													else
														if(modir.equals("REL8"))
														{
															int rr;
															System.out.println(valorEti(operando)+"  "+valor);
															if(operando.charAt(0)>='a' && operando.charAt(0)<='z' || operando.charAt(0)>='A' && operando.charAt(0)<='Z')
															{
																rr=Integer.parseInt(valorEti(operando),16);
																rr=rr-(Integer.parseInt(valor,16)+2);
															}
																
															else
																rr=m.convertirDecimal(operando)-(Integer.parseInt(valor,16)+2);
																if(rr>=-128 && rr<=127){
																	comaq=comaq=ta.comaq.elementAt(j)+convertirHexa(""+rr,1);	
																}
																else{
																	escribirError(linea_+"\tEl desplazamiento se salio de rango\r\n",archivoErr);
																	inst.clear();
																	ins.remove(k);
																	elimEti(etiqueta);
																	ins=revisarInst(ins);
																	ins=recalConLoc(ins,1);
																	
																	k=-1;	
																	mal=true;
																}						
														}
														else
															if(modir.equals("REL16"))
															{
																int rr;
																System.out.println(valorEti(operando)+"  "+valor+ " ,"+operando+",");
																if(operando.charAt(0)>='a' && operando.charAt(0)<='z' || operando.charAt(0)>='A' && operando.charAt(0)<='Z')
																	rr=Integer.parseInt(valorEti(operando),16)-(Integer.parseInt(valor,16)+4);
																	else
																		rr=m.convertirDecimal(operando)-(Integer.parseInt(valor,16)+4);
																if(rr>=-32768 && rr<=32767){
																	comaq=comaq=ta.comaq.elementAt(j)+convertirHexa(""+rr,2);	
																}
																else{
																	escribirError(linea_+"\tEl desplazamiento se salio de rango\r\n",archivoErr);
																	ins.remove(k);
																	elimEti(etiqueta);
																	ins=revisarInst(ins);
																	ins=recalConLoc(ins,1);
																	
																	k=-1;
																	mal=true;
																}	
															}

							lIns=Integer.parseInt(linea_)+"\t"+valor+"\t"+etiqueta+"\t"+codop+"\t"+operando+"\t"+modir+"\t"+comaq+"\r\n";
				}
				else
					lIns=Integer.parseInt(linea_)+"\t"+valor+"\t"+etiqueta+"\t"+codop+"\t"+operando+"\t"+modir+"\t"+comaq+"\r\n";
				inst.add(lIns);
				if(mal)
					inst.clear();
			}
		return inst;
}

public String regresaRr(String oper){
	String rr=null;
	if(oper.toUpperCase().equals("X"))
		rr= "00";
		else
			if(oper.toUpperCase().equals("Y"))
				rr= "01";
				else
					if(oper.toUpperCase().equals("SP"))
						rr= "10";
						else
							if(oper.toUpperCase().equals("PC"))
								rr="11";
	return rr;
}

public String convertirHexa(String cad, int bt)
{
	Modos m=new Modos();
	cad=Integer.toHexString(m.convertirDecimal(cad));
	while(cad.length()<bt*2)
		cad="0"+cad;

	while(cad.length()>bt*2)
		cad=cad.substring(1);
			return cad.toUpperCase();
}

public boolean elimEti(String eti)
{
	String l,etiq;
	for(int i=0;i<tabSim.size();i++)
	{
		l=tabSim.elementAt(i);
		StringTokenizer st = new StringTokenizer(l,"\t");
		etiq=st.nextToken();
		if(etiq.equals(eti)){
			tabSim.remove(i);
			return true;
		}
	}
	return false;
}

public int contError()
{
	int error=0;
	try{
		RandomAccessFile archi=new RandomAccessFile(new File(archivoErr),"r");
		archi.readLine();
		archi.readLine();
		while(archi.getFilePointer()!=archi.length())
		{
			archi.readLine();
			error++;
		}
		archi.close();
		}
		catch(IOException e){
			System.out.println("Error");
			}
	return error;
}
    public static void main(String[] args){
            Scanner Leer=new Scanner(System.in);
            String ruta="";
            System.out.println("¿Cual es la ruta del archivo?");
            ruta=Leer.nextLine();
            Ensamblador obj= new Ensamblador(ruta);
            obj.validarRuta();
    }
}