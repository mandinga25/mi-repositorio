
package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Servidor {
    private final int puerto = 2027;
    private final int noConexiones = 2;
    private LinkedList<Socket> usuarios = new LinkedList<Socket>();
    private boolean turno = true;
    private int G[][]= new int[3][3];
    private int turnos = 1;
    
    public void escuchar(){
        try{
            for(int i =0;i<3;i++){
                for(int j =0;j<3;j++){
                    G[i][j]=-1;
                }
            }
            ServerSocket servidor = new ServerSocket(puerto, noConexiones);
            System.out.println("Esperando jugadores...");
            while(true){
                Socket cliente = servidor.accept();
                usuarios.add(cliente);
                int xo=turnos % 2 == 0 ? 1:0;
                turnos++;
                Runnable run = new HiloServidor(cliente, usuarios, xo, G);
                Thread hilo = new Thread(run);
                hilo.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        Servidor servidor = new Servidor();
        servidor.escuchar();
    }
    
}
