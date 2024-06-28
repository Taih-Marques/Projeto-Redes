package ClienteService;

import Mensagem.Mensagem;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteService {

    private Socket socket;
    private ObjectOutputStream output;

    public Socket connect(String host, int porta){
        try {
            this.socket = new Socket(host, porta);
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  socket;
    }

    public  void  send(Mensagem mensagem){
        try {

            output.writeObject(mensagem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
