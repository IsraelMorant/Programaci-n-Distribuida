/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_final_banco;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.Random;
/**
 *
 * @author israe
 */




public class ServidorHilo extends Thread {
    private DataInputStream in;
    private DataOutputStream out ;
    private String numeroCliente ;
    
    public ServidorHilo(DataInputStream in, DataOutputStream out, String numeroCliente) {
        this.in = in;
        this.out = out;
        this.numeroCliente = numeroCliente;
    }
    
    
    //Funcion para conectar a la base de datos
    public void conectar() throws SQLException{
    
        java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root","");//Creando la conexion a la base de datos
    }

    
    public void run(){
        Random  r = new Random(System.currentTimeMillis());
        int opcion;
        String mensaje;
        while(true){
        
            try {
                opcion = in.readInt();
                
                switch(opcion){
                    
                    case 1://Para dar de alta en la base de datos al usuario
                        // out.writeUTF("Essfdfsslm");
                   //     System.out.println("odadadadad");
                        try{
                        
                           // Class.forName("com.mysql.jdbc.Driver");//Esoecificando el conector ya se importo en librerias
                            java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root","");//Creando la conexion a la base de datos
                            //Cambiadolo por una funcion
                            
                            if(conexion!=null){
                            
                                System.out.println("Base de datos conectada");
                                
                                String usuario = in.readUTF();
                                String contra = in.readUTF();
                                int cuenta = r.nextInt(9000000)+1000000;
                                String query= "INSERT INTO usuarios (usuario,contra,cuenta) values('"+usuario+"','"+contra+"','"+cuenta+"')";//Query en lenguaje SQL para insertar datos
                                Statement stmt = conexion.createStatement();
                                
                                stmt.executeUpdate(query);//Insertando el usuario en la base de datos
                               
                                conexion.close();
                                
                                out.writeBoolean(true);
                            }
                            
                            
                        } catch(Exception e){
                            out.writeBoolean(false);
                            System.out.println("Error al conectarse a la base de datos");
                            e.printStackTrace();
                        
                        }
                        break;
                    case 2:
                       // Random aleatorio = new Random();
                        
                        int numero = r.nextInt(100)+1;
                        out.writeInt(numero);
                        
                        break;
                    case 3://Cuando el cliente pida validacion para ingresar al menu de usuario con sus credenciales
                          
                    try {
                        
                       java.sql.Connection  conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root",""); //Creando la conexion a la base de datos
                        
                        
                        if(conexion!=null){
                            
                                System.out.println("Base de datos conectada");
                                
                                String usuario = in.readUTF();
                                String contra = in.readUTF();
                                String query= "SELECT * FROM Usuarios WHERE usuario ='"+usuario+"' and contra='"+contra+"'";//Query en lenguaje SQL para insertar datos
                                Statement stmt = conexion.createStatement();
                                
                                ResultSet vali = stmt.executeQuery(query);
                                
                                boolean validacion;
                                validacion = vali.next();
                                
                                out.writeBoolean(validacion);
                                
                                
                                    
                                
                               
                                conexion.close();
                 
                            }
                    } catch (Exception e) {
                         System.out.println("Error al conectarse a la base de datos");
                            e.printStackTrace();
                    }
                            
                            
                            
                        break;
                    case 4://En este caso es para cuando oprima la opcion de depositar a su cuenta
                         
                         try {
                        
                       java.sql.Connection  conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root",""); //Creando la conexion a la base de datos
                        
                        
                        if(conexion!=null){
                            
                                System.out.println("Base de datos conectada Para depositar");
                                
                                String usuario = in.readUTF();
                                int cantidad = in.readInt();
                                
                                String query= "SELECT saldo FROM Usuarios WHERE usuario ='"+usuario+"'";//Query para recuperar el saldo de la cuenta
                               // String queryR= "SELECT saldo FROM Usuarios WHERE usuario ='"+usuario+"'";
                                Statement stmt = conexion.createStatement();
                                
                              ResultSet saldo = stmt.executeQuery(query);
                              int saldoStart=0;//lo de abajo 
                              if(saldo.next()){//Para poder usar el resultado de la query necesitamos incializar o guardar el valor en otra variable inicializada
                                 saldoStart = saldo.getInt("saldo");
                              }
                              int saldo_actual = saldoStart +cantidad;
                              String query2= "UPDATE usuarios SET saldo= '"+saldo_actual+"' WHERE usuario ='"+usuario+"'";//Query en lenguaje SQL para insertar datos
                                stmt = conexion.createStatement();
                                
                                stmt.executeUpdate(query2);//Insertando el usuario en la base de datos
                              
                               // ResultSet saldoReceptor = stmt.executeQuery(queryR);
                                
                              
                                
                                
                                
                                
                                    
                                
                               out.writeBoolean(true);
                                conexion.close();
                 
                            }
                    } catch (Exception e) {
                        out.writeBoolean(false);
                         System.out.println("Error al conectarse a la base de datos al depositar a cuenta propia");
                            e.printStackTrace();
                    }
                            
                        
                        break;
                    case 5://Este caso es para cuando el usuario quiera agregar contactos
                        
                        
                           try {
                        
                       java.sql.Connection  conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root",""); //Creando la conexion a la base de datos
                        
                        
                        if(conexion!=null){
                            
                                System.out.println("Base de datos conectada Para agregar contactos");
                                int cuenta = in.readInt();
                                String del_usuario = in.readUTF();
                                
                                String query0= "SELECT * FROM contactos WHERE del_usuario ='"+del_usuario+"' and cuenta='"+cuenta+"'";//Query en lenguaje SQL para insertar datos
                                Statement stmt = conexion.createStatement();
                                
                                ResultSet vali = stmt.executeQuery(query0);
                                
                                boolean validacion;
                                 validacion = vali.next();
                                
                                if(validacion){
                                
                                    out.writeBoolean(false);
                                }else{
                                
                                String query= "INSERT INTO contactos (cuenta,del_usuario) values('"+cuenta+"','"+del_usuario+"')";
                                
                               // Statement stmt = conexion.createStatement();
                                
                                stmt.executeUpdate(query);
                                out.writeBoolean(true);//Si se agrego correctamente el contacto
                                }
                                
                                
                                
                                
                              
                                
                                
                                
                                
                                    
                                
                               
                                conexion.close();
                 
                            }
                    } catch (Exception e) {
                         out.writeBoolean(false);//Si no se pudo agregar el contacto se le devuelve una respuesta al cliente
                         
                         System.out.println("Error al conectarse a la base de datos al agregar contactos");
                            e.printStackTrace();
                    }
                           
                        break;
                    case 6://Este caso es para cuando el usuario quiera dar de baja a uno de sus contactos dado el numero de cuenta de su contacto
                        
                                 try {
                        
                       java.sql.Connection  conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root",""); //Creando la conexion a la base de datos
                        
                        
                        if(conexion!=null){
                            
                                System.out.println("Base de datos conectada Para eliminar contacto");
                                int cuenta = in.readInt();
                                String del_usuario = in.readUTF();
                                
                                
                                String query= "DELETE FROM contactos WHERE del_usuario ='"+del_usuario+"' AND cuenta ='"+cuenta+"' ";//Query para recuperar el saldo de la cuenta
                                
                                Statement stmt = conexion.createStatement();
                                
                                int vali  = stmt.executeUpdate(query);
                                
                               // System.out.println(vali); Para saber como es el valor que devuelde la consulta delete en este caso 0 si no pudo borrar el elemento 1 en caso contrario
                              
                                 
                                //validacion = vali.next();
                                
                               // out.writeBoolean(validacion);
                                if(vali==1)
                                    out.writeBoolean(true);//Si se elimino correctamente el contacto
                                else
                                    out.writeBoolean(false);
                                    
                                
                               
                                conexion.close();
                 
                            }
                    } catch (Exception e) {
                         out.writeBoolean(false);//Si no se pudo eliminar el contacto se le devuelve una respuesta al cliente
                         
                         System.out.println("Error al conectarse a la base de datos al eliminar un  contacto");
                            e.printStackTrace();
                    }
                        
                        
                        break;
                    case 7://El caso para cuando el usuario quiera depositar/pagar a uno de sus contactos
                        
                          try {
                        
                       java.sql.Connection  conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco2","root",""); //Creando la conexion a la base de datos
                        
                        
                        if(conexion!=null){
                            
                                System.out.println("Base de datos conectada Para depositar/pagar a un contacto");
                                int cuenta = in.readInt();
                                int cantidad = in.readInt();
                                String del_usuario = in.readUTF();
                                 String queryV ="SELECT * FROM contactos WHERE del_usuario= '"+del_usuario+"' AND cuenta='"+cuenta+"'";
                                
                                
                                Statement stmt = conexion.createStatement();
                                
                                
                                
                               ResultSet vali = stmt.executeQuery(queryV);
                                boolean validacion;
                                validacion = vali.next();
                                
                                if(validacion){
                                    String queryE ="SELECT saldo FROM usuarios WHERE usuario= '"+del_usuario+"'";
                                
                                
                                 stmt = conexion.createStatement();
                                
                                
                                
                               ResultSet saldo = stmt.executeQuery(queryE);
                              int saldoStart=0;//lo de abajo 
                              if(saldo.next()){//Para poder usar el resultado de la query necesitamos incializar o guardar el valor en otra variable inicializada
                                 saldoStart = saldo.getInt("saldo");
                              }
                              int saldo_actual = saldoStart -cantidad;
                                if(saldo_actual<0){
                                    out.writeBoolean(false);
                                }else{
                                
                                         String queryU= "UPDATE usuarios SET saldo= '"+saldo_actual+"' WHERE usuario ='"+del_usuario+"'";//Query en lenguaje SQL  para actualizar el saldo del emisor 
                                         stmt = conexion.createStatement();
                                
                                         stmt.executeUpdate(queryU);//Insertando el usuario en la base de datos
                                         //////////////////////////////
                                         ///
                                         ///
                                         ///
                                            String queryR ="SELECT saldo FROM usuarios WHERE cuenta= '"+cuenta+"'";
                                
                                
                                            stmt = conexion.createStatement();
                                
                                
                                
                                            ResultSet saldoR = stmt.executeQuery(queryR);
                                            int saldoStartR=0;//lo de abajo 
                                            if(saldoR.next()){//Para poder usar el resultado de la query necesitamos incializar o guardar el valor en otra variable inicializada
                                            saldoStartR = saldoR.getInt("saldo");
                                            }
                                            int saldo_actualR = saldoStartR +cantidad;
                                            String queryUR = "UPDATE usuarios SET saldo= '"+saldo_actualR+"' WHERE cuenta ='"+cuenta+"'";//Query en lenguaje SQL  para actualizar el saldo del emisor 
                                             stmt = conexion.createStatement();
                                
                                            stmt.executeUpdate(queryUR);//Insertando el usuario en la base de datos
                                         
                                         out.writeBoolean(true);
                                            conexion.close();
                                
                                
                                
                                    
                               
                              /// System.out.println(vali);// Para saber como es el valor que devuelde la consulta delete en este caso 0 si no pudo borrar el elemento 1 en caso contrario
                              
                                 
                                //validacion = vali.next();
                                
                               // out.writeBoolean(validacion);
                              
                                }
                                
                                }else{
                                
                                
                                     out.writeBoolean(false);//Si no se pudo depoositait/pagar el contacto se le devuelve una respuesta al cliente
                                
                                }
                                
                                
                               
                                
                 
                        }
                    } catch (Exception e) {
                         out.writeBoolean(false);//Si no se pudo depositat/pagar el contacto se le devuelve una respuesta al cliente
                         
                         System.out.println("Error al conectarse a la base de datos al eliminar un  contacto");
                            e.printStackTrace();
                    }
                        
                        break;
                    case 8:
                        System.out.println("Tarjeta temporal generada");
                        out.writeInt(r.nextInt(9000000)+1000000);
                        
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                        
                    default:
                       out.writeUTF("Escoje una opcion validad");
                        
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        
        
        
        }
        
    }
}
