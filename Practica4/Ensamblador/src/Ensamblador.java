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
        String ruta, texto, archivo,archivoInst,archivoErr,archivoT;
        Vector<Tabop> tabop=new Vector<Tabop>();
        int linea =0;
        int conLoc=0;
        boolean end,eOrg=false, dir_ini=false;
        Vector dir= new Vector();

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
                archivoT=ruta.substring(0,ruta.indexOf('.'))+".TDS";
                File inst = new File(archivoInst);
                File err = new File(archivoErr);
                File tds =new File(archivoT);
                 if(inst.exists())
                         inst.delete();
                 if(err.exists())
                         err.delete();
                 if(tds.exists())
                         tds.delete();
                try{
                        BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archivoInst), true));
                        archinst.write("LINEA\t\tCONTLOC\t\tETQ\t\tCODOP\t\tOPER\t\tMODOS");
                        archinst.write("\r\n...........................................................\r\n");
                        archinst.close();
                }
            catch(IOException e){
                                        System.out.println("Error al crear archivo de instrucciones");
                    }
            try{
                    BufferedWriter archierr = new BufferedWriter(new FileWriter(new File(archivoErr), true));
                         archierr.write("\tERRORES\r\n............................................\r\n");
                        archierr.close();
                }
            catch(IOException e2){
                           System.out.println("Error al crear archivo de errores");
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
                catch(IOException e)
                        {
                                      System.out.println("Error");
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
                                          escribirError(linea +"        No hay Codigo de operacion\r\n",archivoErr);
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
                                                                                                      System.out.println(c);
                                                                                                      if(c<0)
                                                                                                              c=texto.length();
                                                                                                              else
                                                              
                                                                                                              if(texto.charAt(c-1)!='\\'){
                                                                                                              fc=true;
                                                                                                              lin.opera(texto.substring(cont,c+1));
                                                                                                              System.out.println(texto.substring(cont,c+1));
                                                                                                              cont+=texto.substring(cont,c+1).length();
                                                                                                              edo=9;
                                                                                                              op=true;
                                                                                                      }                                        
                                                                                      }
                                                                                      if(!fc)
                                                                                              {
                                                                                                      op=false;
                                                                                                      escribirError(linea+"\tOperando invalido\r\n",archivoErr);
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
                                                              escribirError(linea+"        Exceso    de    tokens\r\n",archivoErr);
                                                              op=false;
                                                              edo=10;
                                                      }
                      break;
     }
    }
            if((etiq && cod) && op)
            {
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

    public void escribirInstruccion(int lin, String instruccion,String archiI){
         String l= Integer.toString(lin);
            try{
                    BufferedWriter archinst = new BufferedWriter(new FileWriter(new File(archiI), true));
                    archinst.write(l);
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
                        archiT.write(valor);
                        archiT.write("\r\n");
                        archiT.close();
                }
              catch(IOException e){
                    System.out.println("Error");
              }
    }

    public void leerTabop(){
            String l,rt, cod,codant=null;
            Scanner Leer=new Scanner(System.in);
            System.out.println("�Cual es la ruta del archivo tabop?");
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
                catch(IOException e)
                        {
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
                    if(eOrg)
                    {
                            escribirError(l.lin+"\tSolo debe existir un ORG\r\n",archivoErr);
                    }
                    else
                    {
                            if(!(l.operando.charAt(0)>='a' && l.operando.charAt(0)<='z' )||!(l.operando.charAt(0)>='A'&& l.operando.charAt(0)<='z'))
                            {
                                    Modos mo=new Modos(l,new Tabop(),conLoc);
                                    conLoc=mo.convertirDecimal2(l.operando);
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
                                                    if(noEti(l.operando,l.codigo))
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
                                                                                    escribirInstruccion(l.lin,"\t\t"+cL+"\t\t"+ l.etiqueta+"\t\t"+l.codigo+"\t\t"+l.operando+"\r\n",l.archiInst);                                
                                                                            }
                                                                            else
                                                                                    escribirError(l.lin+"\tLa etiqueta se repitio\r\n",l.archierr);                                
                                                                    }
                                                                    else{
                                                                            escribirInstruccion(l.lin,"\t\t"+cL+"\t\t"+ l.etiqueta+"\t\t"+l.codigo+"\t\t"+l.operando+"\r\n",l.archiInst);
                                                                    }
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
                                                    if(noEti(l.operando,l.codigo))
                                                    {
                                                            op=d.convertirDecimal2(l.operando);
                                                            if(op>=0 && op<=255)
                                                            {
                                                                    dirC=true;
                                                                    inC=1;
                                                            }
                                                    }
                                    }
                                    else
                                            if(j>=3 &&j<=5)
                                            {
                                                    if(noNULL(l.operando,l.codigo))
                                                            if(noEti(l.operando,l.codigo))
                                                            {
                                                                    op=d.convertirDecimal2(l.operando);
                                                                    if(op>=0 && op<=65535)
                                                                    {
                                                                            dirC=true;
                                                                            inC=2;
                                                                    }
                                                            }
                                            }
                                            else
                                                    if(j==6)
                                                    {
                                                            if(noNULL(l.operando,l.codigo))
                                                                    if(noEti(l.operando,l.codigo))
                                                                    {
                                                                            int c=0,tam=l.operando.length()-2;
                                                                            System.out.println(tam+"\t"+l.operando.length());
                                                                            char ca;
                                                                            while(c!=l.operando.length())
                                                                            {
                                                                                    c=l.operando.indexOf("\\",c+1);
                                                                                    if(c<0)
                                                                                            c=l.operando.length();
                                                                                            else
                                                                                                    {
                                                                                                            ca=l.operando.charAt(c+1);
                                                                                                            if(ca=='\"'|| ca=='a'|| ca=='n' || ca=='t' || ca=='s'|| ca=='r'|| ca=='b'||ca=='\\')
                                                                                                            {
                                                                                                                    c++;
                                                                                                                    tam--;        
                                                                                                            }System.out.println(tam);
                                                                                                    }
                                                                                    }
                                                                                    dirC=true;
                                                                                    inC=tam;        
                                                                    }        
                                                    }
                                                    else
                                                            if(j>=7 && j<=9)
                                                            {
                                                                    if(noNULL(l.operando,l.codigo))
                                                                            if(noEti(l.operando,l.codigo))
                                                                            {
                                                                                    op=d.convertirDecimal2(l.operando);
                                                                                    if(op>=0 && op<=65535)
                                                                                    {
                                                                                            dirC=true;
                                                                                            inC=op;
                                                                                    }
                                                                            }
                                                            }
                                                            if(j==10 || j==11)
                                                            {
                                                                    if(noNULL(l.operando,l.codigo))
                                                                            if(noEti(l.operando,l.codigo))
                                                                            {
                                                                                    op=d.convertirDecimal2(l.operando);
                                                                                    if(op>=0 && op<=65535)
                                                                                    {
                                                                                            dirC=true;
                                                                                            inC=op*2;
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
                            escribirInstruccion(l.lin,"\t\t"+cL+"\t\t"+ l.etiqueta+"\t\t"+l.codigo+"\t\t"+l.operando+"\r\n",l.archiInst);                                
                            conLoc+=inC;                    
                    }
                    else
                    escribirError(l.lin+"\tLa etiqueta se repitio\r\n",l.archierr);                                
            }
            else{
                    escribirInstruccion(l.lin,"\t\t"+cL+"\t\t"+ l.etiqueta+"\t\t"+l.codigo+"\t\t"+l.operando+"\r\n",l.archiInst);
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

public boolean noEti(String operando,String dir)
{
        if((operando.charAt(0)>='a'&&operando.charAt(0)<='z')||(operando.charAt(0)>='A'&&operando.charAt(0)<='Z'))
    {
            escribirError(linea+"\tLa directiva "+dir+" no acepta etiquetas como operando\r\n",archivoErr);
            return false;
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
    public static void main(String[] args){
            Scanner Leer=new Scanner(System.in);
            String ruta="";
            System.out.println("�Cual es la ruta del archivo?");
            ruta=Leer.nextLine();
            Ensamblador obj= new Ensamblador(ruta);
            obj.validarRuta();
    }
}