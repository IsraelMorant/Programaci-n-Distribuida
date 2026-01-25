/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_final_banco;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
//import java.awt.event.KeyListener;


/**
 *
 * @author israe
 */
public class ClienteHilo extends Thread {
    
    private DataInputStream in;
    private DataOutputStream out ;
    
     public  int opcion;
     public String mensaje;
     public String usuario;
     public String contra;
    public ClienteHilo(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }
    
    
            
    
    public void run (){
       

        Scanner sn = new Scanner(System.in);
        //Creando las  ventanas del programa
        JFrame ventanaInicial = new JFrame ();
        JFrame ventanaUsuario = new JFrame();
        //Atributos de la ventana Principal
        
        ventanaInicial.setSize(1200,800);
        ventanaInicial.setTitle("Menu Principal");
        ventanaInicial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Centrando la ventana
        ventanaInicial.setLocationRelativeTo(null);
        
        //Creando un panel para mostrar el menu
        JPanel panelInicial = new JPanel();
        panelInicial.setLayout(null);
        panelInicial.setBackground(Color.WHITE);
        //Fin de la creacion y configuracion del panel principal
        
        
        /////////////////////
        
        
        //Atributos de la ventana de Ususario
        
        ventanaUsuario.setSize(1200,800);
        ventanaUsuario.setTitle("Menu Usuario");
        ventanaUsuario.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Centrando la ventana
        ventanaUsuario.setLocationRelativeTo(null);
        
        //Creando un panel para mostrar el menu de la ventana usuario
        JPanel panelUsuario = new JPanel();
        panelUsuario.setLayout(null);
        panelUsuario.setBackground(Color.WHITE);
        //Fin de la creacion y configuracion del panel principal
        
        ///////*
        ///
        //*******************************************************************************************************************
        //Atributos de la ventana de usuario
         JLabel lblBienvenidoUsua = new JLabel("Seleccione una opcion");
        lblBienvenidoUsua.setFont(new Font("arial",Font.ITALIC,15));
        lblBienvenidoUsua.setHorizontalAlignment(JLabel.CENTER);
        lblBienvenidoUsua.setBounds(450,0,300,100);
        lblBienvenidoUsua.setForeground(Color.black);
        
        
        //Label de la cantidad de dinero
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("arial",Font.ITALIC,15));
        lblCantidad.setHorizontalAlignment(JLabel.CENTER);
        lblCantidad.setBounds(450,550,250,50);
        lblCantidad.setForeground(Color.black);
        //Fin del label de la cantidad de dinero
        
         //Label de la cantidad de dinero
        JLabel lblSaldo = new JLabel("");
        lblSaldo.setFont(new Font("arial",Font.ITALIC,15));
        lblSaldo.setHorizontalAlignment(JLabel.CENTER);
        lblSaldo.setBounds(450,100,150,50);
        lblSaldo.setForeground(Color.black);
        //Fin del label de la cantidad de dinero
         //Label de la cantidad de dinero
        JLabel lblSaldoG = new JLabel("Cantidad:");
        lblSaldoG.setFont(new Font("arial",Font.ITALIC,15));
        lblSaldoG.setHorizontalAlignment(JLabel.CENTER);
        lblSaldoG.setBounds(500,1000,150,50);
        lblSaldoG.setForeground(Color.black);
        //Fin del label de la cantidad de dinero
        /*
         //Boton de depositar 
        JButton btnSaldo = new JButton("Depositar a su cuenta");
        btnSaldo.setFont(new Font("arial",Font.ITALIC,15));
        btnSaldo.setHorizontalAlignment(JLabel.CENTER);
        btnSaldo.setBounds(350, 400, 200, 50);
        btnSaldo.addActionListener(new ActionListener() {//Accion del Boton Depositar
        @Override
        public void actionPerformed(ActionEvent e) {
             
            try {
               
             //      if(txtCantidad.getText().strip().isEmpty()){
                        
                        JOptionPane.showMessageDialog(null, "Complete la cantidad a depositar", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                        out.writeInt(4);//Indicando al servidor que desea hacer el usuario 
                        out.writeUTF(usuario);
                      //  out.writeInt(Integer.parseInt(txtNumCuenta.getText()));
                        out.writeInt(Integer.parseInt(txtCantidad.getText()));
                        
                        
                        if(in.readBoolean()){
                        
                            JOptionPane.showMessageDialog(null, "Deposito realizado extiosamente", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                        
                            JOptionPane.showMessageDialog(null, "Error al depositar", "Alerta", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                 
            } catch (IOException ex) {
                Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
                
         }
        });//Fin de la accion de Depositar
        */
        //JTextField de la cantidad ya sea para depositar o pagar cuenta
         JTextField txtCantidad = new JTextField(20);//Creando el txt y limitandolo a 20 caracteres
        txtCantidad.setBounds(500, 600, 150, 50);
        
        
        //Caja de texto de Numero de cuenta 
        JTextField txtNumCuenta = new JTextField(20);//Creando el txt y limitandolo a 20 caracteres
        txtNumCuenta.setBounds(500, 500, 150, 50);
        txtNumCuenta.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
               if(txtNumCuenta.getText().length()>=7)e.consume();
               
                   
            }

            @Override
            public void keyPressed(KeyEvent e) {
               // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void keyReleased(KeyEvent e) {
               // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            

           

          
        
        
        });
        
        
        
        
        
        
        
        
        
        //Boton de depositar 
        JButton btnDepositar = new JButton("Depositar a su cuenta");
        btnDepositar.setFont(new Font("arial",Font.ITALIC,15));
        btnDepositar.setHorizontalAlignment(JLabel.CENTER);
        btnDepositar.setBounds(350, 400, 200, 50);
        btnDepositar.addActionListener(new ActionListener() {//Accion del Boton Depositar
        @Override
        public void actionPerformed(ActionEvent e) {
             
            try {
               
                   if(txtCantidad.getText().strip().isEmpty()){
                        
                        JOptionPane.showMessageDialog(null, "Complete la cantidad a depositar", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                        out.writeInt(4);//Indicando al servidor que desea hacer el usuario 
                        out.writeUTF(usuario);
                      //  out.writeInt(Integer.parseInt(txtNumCuenta.getText()));
                        out.writeInt(Integer.parseInt(txtCantidad.getText()));
                        
                        
                        if(in.readBoolean()){
                        
                            JOptionPane.showMessageDialog(null, "Deposito realizado extiosamente", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                        
                            JOptionPane.showMessageDialog(null, "Error al depositar", "Alerta", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                 
            } catch (IOException ex) {
                Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
                
         }
        });//Fin de la accion de Depositar
        
        //Fin del boton de depositar
        
         
        
         //Boton pagar cuenta
        JButton btnPagar = new JButton("<html>Pagar/Depositar a un contacto");
        btnPagar.setFont(new Font("arial",Font.ITALIC,19));
        btnPagar.setBounds(650, 400, 250, 100);
        btnPagar.addActionListener(new ActionListener() {//Accion del Boton Darse de alta
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                     if(txtNumCuenta.getText().strip().isEmpty() || txtNumCuenta.getText().length() <7 || txtCantidad.getText().strip().isEmpty()  ){
                        
                        JOptionPane.showMessageDialog(null, "Complete el campo Numero de cuenta y Cantidad", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                        out.writeInt(7);//Lo que desea hacer el usuario en este caso depositar a un cotacto
                        out.writeInt(Integer.parseInt(txtNumCuenta.getText()));
                        out.writeInt(Integer.parseInt(txtCantidad.getText()));
                     
                        out.writeUTF(usuario);
                        
                        if(in.readBoolean()){
                        
                            JOptionPane.showMessageDialog(null, "Movimiento realizado extiosamente", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                        
                            JOptionPane.showMessageDialog(null, "Error al realizar el movimiento", "Alerta", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//Fin de la accion Pagar/Depositar  a una cuenta de los contactos del usuario
        
        
         //Boton Agregar contactos
        JButton btnContacto = new JButton("Agregar Contacto");
        btnContacto.setFont(new Font("arial",Font.ITALIC,19));
        btnContacto.setBounds(650, 500, 250, 50);
        btnContacto.addActionListener(new ActionListener() {//Accion del Boton Darse de alta
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    
                    if(txtNumCuenta.getText().strip().isEmpty() || txtNumCuenta.getText().length() <7 ){
                        
                        JOptionPane.showMessageDialog(null, "Complete el campo Numero de cuenta", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                        out.writeInt(5);//Lo que desea hacer el usuario en este caso agregar un cotacto
                        out.writeInt(Integer.parseInt(txtNumCuenta.getText()));
                        out.writeUTF(usuario);
                        
                        if(in.readBoolean()){
                        
                            JOptionPane.showMessageDialog(null, "Contacto agregado extiosamente", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                        
                            JOptionPane.showMessageDialog(null, "Error al agregar el contacto", "Alerta", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//Fin de la accion Pagar a una cuenta
        
        
         //Boton Dar de baja cuetna suponemos que de los contactos
        JButton btnBaja = new JButton("Dar de baja contacto");
        btnBaja.setFont(new Font("arial",Font.ITALIC,19));
        btnBaja.setBounds(650, 600, 250, 50);
        btnBaja.addActionListener(new ActionListener() {//Accion del Boton Darse de alta
            @Override
            public void actionPerformed(ActionEvent e) {
                  try {
                    
                    if(txtNumCuenta.getText().strip().isEmpty() || txtNumCuenta.getText().length() <7 ){
                        
                        JOptionPane.showMessageDialog(null, "Complete el campo Numero de cuenta", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                        out.writeInt(6);//Lo que desea hacer el usuario en este caso dar de baja un cotacto
                        out.writeInt(Integer.parseInt(txtNumCuenta.getText()));
                        out.writeUTF(usuario);
                        
                        if(in.readBoolean()){
                        
                            JOptionPane.showMessageDialog(null, "Contacto eliminado extiosamente", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                        
                            JOptionPane.showMessageDialog(null, "Error al eliminar el contacto", "Alerta", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//Fin de la accion Dar de baja cuenta
        
        //Label del la tarjeta temporal
        JLabel lblTemporal = new JLabel("");
        lblTemporal.setFont(new Font("arial",Font.ITALIC,15));
        lblTemporal.setHorizontalAlignment(JLabel.CENTER);
        lblTemporal.setBounds(750, 700, 150, 50);
        
        //Fin del label de la tarjeta temporal
        
        
        //Boton de tarjeta temporal
        JButton btnTemporal = new JButton("Tarjeta temporal");
        btnTemporal.setFont(new Font("arial",Font.ITALIC,19));
        btnTemporal.setBounds(500, 700, 250, 50);
        btnTemporal.addActionListener(new ActionListener() {//Accion del Boton Tomar Turno
        @Override
        public void actionPerformed(ActionEvent e) {
             
            try {
                out.writeInt(8);
                lblTemporal.setText(Integer.toString(in.readInt()));
                JOptionPane.showMessageDialog(null, "Tarjeta temporal creada con exito","Aviso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });//Fin de la accion tarjeta temporal
        
         //Label de Num de cuenta
        JLabel lblNum = new JLabel("Numero de Cuenta:");
        lblNum.setFont(new Font("arial",Font.ITALIC,15));
        lblNum.setHorizontalAlignment(JLabel.CENTER);
        lblNum.setBounds(450,450,250,50);
        lblNum.setForeground(Color.black);
        //Fin del label de Num de cuenta
        
        //*****************************************************************************************************
        //Label del turno dado
        JLabel lblTurno = new JLabel("");
        lblTurno.setFont(new Font("arial",Font.ITALIC,15));
        lblTurno.setHorizontalAlignment(JLabel.CENTER);
        lblTurno.setBounds(625, 700, 150, 50);
        
        //Fin del label del turno 
        
        
        //Boton de Toma turno
        JButton btnTurno = new JButton("Tomar Turno:");
        btnTurno.setFont(new Font("arial",Font.ITALIC,19));
        btnTurno.setBounds(500, 700, 150, 50);
        btnTurno.addActionListener(new ActionListener() {//Accion del Boton Tomar Turno
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                   // opcion = 2;
                    out.writeInt(2);
                   int turno = in.readInt();
                   
                   lblTurno.setText(Integer.toString(turno));
                   
                    JOptionPane.showMessageDialog(null, "Turno creado con exito","Aviso", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
            }
        });//Fin de la accion de tomar turno
        
         //Caja de texto de usuario 
        JTextField txtUsuario = new JTextField(20);//Creando el txt y limitandolo a 20 caracteres
        txtUsuario.setBounds(500, 500, 150, 50);
        
        //Fin de caja
        
        
         //Caja de texto de CONTRASEÑA 
        JTextField txtContra = new JTextField(20);//Creando el txt y limitandolo a 20 caracteres
        txtContra.setBounds(500, 600, 150, 50);
        
        //Fin de caja
        
        //Elementos del panel inicial del menu incial
        JLabel lblBienvenido = new JLabel("Bienvenido seleccione una opcion");
        lblBienvenido.setFont(new Font("arial",Font.ITALIC,15));
        lblBienvenido.setHorizontalAlignment(JLabel.CENTER);
        lblBienvenido.setBounds(450,0,300,100);
        lblBienvenido.setForeground(Color.black);
        //Btn ingresar
        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("arial",Font.ITALIC,19));
        btnIngresar.setBounds(350, 400, 150, 50);
        btnIngresar.addActionListener(new ActionListener() {//Accion del Boton Ingresar
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    
                     usuario = txtUsuario.getText();
                     contra = txtContra.getText();
                    if(txtContra.getText().strip().isEmpty() || txtUsuario.getText().strip().isEmpty()){
                        JOptionPane.showMessageDialog(null, "Rellene los campos", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                        
                         out.writeInt(3);
                    out.writeUTF(usuario);
                    out.writeUTF(contra);
                    boolean validacion = false;
                    validacion = in.readBoolean();
                    txtUsuario.setText("");
                    txtContra.setText("");
                    if(validacion){
                    
                        ventanaInicial.setVisible(false);
                        ventanaUsuario.setVisible(true);
                        
                    
                    }else{
                    
                        JOptionPane.showMessageDialog(null, "El Usuario o Contraseña son incorrectos", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            
        });//Fin de la accion del boton Ingresar
        
        
        
         //Boton darse de alta;
        JButton btnAlta = new JButton("Darse de Alta");
        btnAlta.setFont(new Font("arial",Font.ITALIC,19));
        btnAlta.setBounds(650, 400, 150, 50);
        btnAlta.addActionListener(new ActionListener() {//Accion del Boton Darse de alta
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                     usuario = txtUsuario.getText();
                     contra = txtContra.getText();
                    String Valusuario = txtUsuario.getText().strip();
                    String Valcontra = txtContra.getText().strip();
                   // System.out.println();
                    if( Valusuario.isEmpty()|| Valcontra.isEmpty() ){
                        JOptionPane.showMessageDialog(null, "Rellene los campos", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }else{
                         //opcion = 1;
                    out.writeInt(1);
                   //  mensaje = in.readUTF();
                     out.writeUTF(usuario);
                     out.writeUTF(contra);
                    // txtUsuario.setText(mensaje);
                    txtUsuario.setText("");
                    txtContra.setText("");
                    if(in.readBoolean()){
                     JOptionPane.showMessageDialog(null, "Usuario dado de alta exitosamente", "Alerta", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                      JOptionPane.showMessageDialog(null, "Error al dar de alta al usuario", "Alerta", JOptionPane.ERROR_MESSAGE);  
                    }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//Fin de la accion de dar de alta
        
        //Label de usuario
        JLabel lblUsua = new JLabel("Usuario:");
        lblUsua.setFont(new Font("arial",Font.ITALIC,19));
        lblUsua.setHorizontalAlignment(JLabel.CENTER);
        lblUsua.setBounds(500,450,150,50);
        lblUsua.setForeground(Color.black);
        //Fin del label de usuario
       
        
         //Label de contraseña
        JLabel lblContra = new JLabel("Contraseña:");
        lblContra.setFont(new Font("arial",Font.ITALIC,19));
        lblContra.setHorizontalAlignment(JLabel.CENTER);
        lblContra.setBounds(500,550,150,50);
        lblContra.setForeground(Color.black);
        //Fin del label de contraseña
        
        

        //Añadiendo elmentos al panel y ala ventana
        panelInicial.add(lblBienvenido);
        panelInicial.add(btnIngresar);
        panelInicial.add(btnTurno);
        panelInicial.add(btnAlta);
        panelInicial.add(lblUsua);
        panelInicial.add(txtUsuario);
        panelInicial.add(lblContra);
        panelInicial.add(txtContra);
        panelInicial.add(lblTurno);
        //Añadiendo elmentos al panel de usuario
        panelUsuario.add(lblTemporal);
        panelUsuario.add(btnTemporal);
        panelUsuario.add(btnDepositar);
        panelUsuario.add(lblBienvenidoUsua);
        panelUsuario.add(btnPagar);
        panelUsuario.add(btnBaja);
        panelUsuario.add(btnContacto);
        panelUsuario.add(txtNumCuenta);
        panelUsuario.add(lblNum);
        panelUsuario.add(lblCantidad);
        panelUsuario.add(txtCantidad);
        //Agregando el panel ala ventana Inicial 
        
        ventanaInicial.add(panelInicial);
        
        //Agregando el panel ala ventana de Usuario
        
        ventanaUsuario.add(panelUsuario);
        //Mostrando la ventna 
        ventanaInicial.setVisible(true);
        
        
       
              boolean salir= false;
        /*
        while(!salir){
            try {
                System.out.println("1.-Tomar turno");
                System.out.println("2.-Ir a ventanilla");
                System.out.println("3.-Ir con asesor");
                System.out.println("4.-Depositar");
                System.out.println("5.-Pagar a una cuenta");
                System.out.println("6.-Agregar Contactos");
                System.out.println("7.-Dar de baja");
                System.out.println("8.-Tarjeta temporal");
                System.out.println("9.-Pago");
                System.out.println("10.-Tomar turno");
                System.out.println("11.-Salir");
                opcion = sn.nextInt();
                out.writeInt(opcion);
                switch(opcion){
                    
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                    default:
                        mensaje = in.readUTF();
                        System.out.println(mensaje);
                        
                }
            } catch (IOException ex) {
                Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
*/
    }
}

   
