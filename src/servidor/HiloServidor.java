
package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class HiloServidor implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int XO;
    private int G[][];
    private boolean turno;
    private LinkedList<Socket> usuarios = new LinkedList<Socket>();
    
    public HiloServidor(Socket soc, LinkedList users, int xo, int[][] Gato){
        socket = soc;
        usuarios = users;
        XO = xo;
        G = Gato;
    }
    @Override
    public void run(){
        try{
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            turno = XO==1;
            String msg = "";
            msg += "JUEGAS: "+ (turno ?"X;" : "O;");
            msg += turno;
            out.writeUTF(msg);
            
            while(true){
                String recibidos = in.readUTF();
                String recibido[] = recibidos.split(";");
                int f = Integer.parseInt(recibido[0]);
                int c = Integer.parseInt(recibido[1]);
                G[f][c]=XO;
                String cad = "";
                cad += XO + ";";
                cad += f + ";";
                cad += c + ";";
                boolean ganador = gano(XO);
                boolean completo = lleno();
                if (!ganador && !completo){
                    cad += "NADIE";
                }else if (!ganador && completo){
                    cad += "EMPATE";
                }else if (ganador){
                    vaciarMatriz();
                    cad += XO == 1 ? "X" : "O";
                }
                for (Socket usuario : usuarios){
                    out = new DataOutputStream(usuario.getOutputStream());
                    out.writeUTF(cad);
                }
            }
        }catch (Exception e){
            for(int i=0; i<usuarios.size(); i++){
                if (usuarios.get(i)==socket){
                    usuarios.remove(i);
                    break;
                }
            }
            vaciarMatriz();
        }
    }
    
    public boolean gano(int n){
        for(int i=0; i<3;i++){
            boolean gano= true;
            for (int j = 0; j<3;j++){
                gano = gano && (G[i][j]==n);
            }
            if (gano){
                return true;
            }
        }
        for(int i=0; i<3;i++){
            boolean gano= true;
            for (int j = 0; j<3;j++){
                gano = gano && (G[j][i]==n);
            }
            if (gano){
                return true;
            }
        }
        if (G[0][0]==n && G[1][1]==n && G[2][2]==n){
            return true;
        }
        if (G[0][2]==n && G[1][1]==n && G[2][0]==n){
            return true;
        }
        return false;
    }
    
    public boolean lleno(){
        for(int i =0;i<3;i++){
            for (int j=0;j<3;j++){
                if (G[i][j]==-1){
                    return false;
                }
            }
        }
        vaciarMatriz();
        return true;
    }
    
    public void vaciarMatriz(){
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                G[i][j]=-1;
            }
        }
    }
}
