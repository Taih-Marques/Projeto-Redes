package JogadorFrame;

import ClienteService.ClienteService;
import Mensagem.Mensagem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class JogadorFrame extends JFrame {
    private JPanel barraSuperior;
    private JPanel barraInferior;
    private JButton btnConectar;
    private JButton btnSair;
    private JTextField txtFieldEnviarMsg;
    private JButton btnEnviar;
    private JTextField txtFieldReceber;
    private JPanel principal;
    private JTextPane txtFieldNomeJogador;

    private Mensagem mensagem;
    private Socket socket;
    private ClienteService service;

    private int porta = 5050;
    private String host = "localhost";
    private String nomeUsuario;

    public JFrame rodar() {
        JFrame frame = new JFrame("JogadorFrame");
        frame.setContentPane(new JogadorFrame().principal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        return frame;
    }

    public JogadorFrame() {



        btnConectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                nomeUsuario = txtFieldNomeJogador.getText();

                if(!nomeUsuario.isBlank()){

                    mensagem = new Mensagem();

                    mensagem.setId(nomeUsuario);
                    mensagem.setAcao(Mensagem.Acao.CONECTAR);

                    if(socket == null || socket.isClosed()) {

                        service = new ClienteService();
                        socket = service.connect(host, porta);

                        new Thread(new ListenerSocket(socket)).start();
                    }

                    System.out.println("Solicitando conexão");
                    service.send(mensagem);
                }

            }
        });
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               mensagem = new Mensagem();
                System.out.println("Solicitando desconexão");
                mensagem.setAcao(Mensagem.Acao.DESCONECTAR); //solicita a desconexão com o servidor;
                mensagem.setId(nomeUsuario);
                service.send(mensagem);//envia a solicitação


            }
        });
    }

    private class ListenerSocket implements Runnable {

        private final ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {

            Mensagem mensagem;

            while (true){

                try {

                    if(socket.isClosed()) break;
                    if((mensagem = (Mensagem) input.readObject()) == null) break;

                    Mensagem.Acao acao = mensagem.getAcao();

                    if(acao == Mensagem.Acao.CONECTAR){

                        System.out.println("Conectado");
                        connected(mensagem);
                    } else if (acao == Mensagem.Acao.DESCONECTAR) {

                        System.out.println("Desconectado");
                        disconectado(mensagem); //foi desconectado
                        socket.close();

                    }
                    else if(acao == Mensagem.Acao.RECUSADO){

                        System.out.println("Recusado");
                        txtFieldReceber.setText(mensagem.getConteudo().concat("\n"));
                    } else if (acao == Mensagem.Acao.ROLE_DESENHISTA) {

                        System.out.println("Jogo iniciado");
                        desenhando(mensagem);
                    }
                    else if (acao == Mensagem.Acao.ROLE_ADIVINHADOR){

                        System.out.println("Jogo iniciado");
                        advinhando(mensagem);
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void advinhando(Mensagem mensagem) {

        this.txtFieldReceber.setText(mensagem.getConteudo());
    }

    private void desenhando(Mensagem mensagem) {

        this.txtFieldReceber.setText(mensagem.getConteudo());
    }

    private void disconectado(Mensagem mensagem) {

        this.txtFieldReceber.setText(mensagem.getConteudo());
        btnConectar.setEnabled(true);
        btnSair.setEnabled(false);
        txtFieldNomeJogador.setEnabled(true);


    }

    private void connected(Mensagem mensagem) {

            btnConectar.setEnabled(false);
            btnSair.setEnabled(true);
            txtFieldNomeJogador.setEnabled(false);
            txtFieldReceber.setText(mensagem.getConteudo());

    }
}
