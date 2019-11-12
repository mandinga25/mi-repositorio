
package jugador;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Cliente implements Runnable {
    private Socket cliente;
    private DataOutputStream out;
    private DataInputStream in;
    private int puerto=2027;
    //Si estamos en nuestra misma maquina usamos localhost si no la direccion IP de la maquina servidor
    private String host = "localhost";
    private String mensaje;
    private Main frame;
    private JButton[][] botones;
    private ActionListener ac;
    private Image X;
    private Image O;
    private boolean turno;
    
    public Cliente(Main frame){
        try{
            this.frame = frame;
            X = ImageIO.read(getClass().getResource("X.png"));
            O = ImageIO.read(getClass().getResource("O.png"));
            cliente = new Socket(host, puerto);
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
            botones = this.frame.getBotones();
                              
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try{
            mensaje = in.readUTF();
            String split[] = mensaje.split(";");
            frame.cambioTexto(split[0]);
            String XO=split[0].split(" ")[1];
            turno = Boolean.valueOf(split[1]);
            while(true){
                mensaje = in.readUTF();
                String[] mensajes = mensaje.split(";");
                int xo=Integer.parseInt(mensajes[0]);
                int f=Integer.parseInt(mensajes[1]);
                int c=Integer.parseInt(mensajes[2]);
                if (xo==1){
                    botones[f][c].setIcon(new ImageIcon(X));
                }else{
                    botones[f][c].setIcon(new ImageIcon(O));
                }
                botones[f][c].removeActionListener(botones[f][c].getActionListeners()[0]);
                turno = !turno;
                if(XO.equals(mensajes[3])){
                    JOptionPane.showMessageDialog(frame, "GANASTE!!!!");
                    new Main().setVisible(true);
                    frame.dispose();
                }else if ("EMPATE".equals(mensajes[3])){
                    JOptionPane.showMessageDialog(frame, "EMPATE!");
                    new Main().setVisible(true);
                    frame.dispose();
                }else if(!"NADIE".equals(mensajes[3])&&!mensajes[3].equals(mensajes[0])){
                    JOptionPane.showMessageDialog(frame, "PERDISTE BUUU!!");
                    new Main().setVisible(true);
                    frame.dispose();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void enviarTurno(int f, int c){
        try{
            if (turno){
                String datos = "";
                datos += f + ";";
                datos += c + ";";
                out.writeUTF(datos);
            }else {
                JOptionPane.showMessageDialog(frame, "Espera tu turno!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
