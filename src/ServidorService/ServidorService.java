package ServidorService;

import Desenho.Desenhavel;
import Lobby.Lobby;
import Mensagem.Mensagem;
import Mensagem.Mensagem.Acao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;

    private Lobby lobby = new Lobby();

    public ServidorService(int porta) {

        try {
            this.serverSocket = new ServerSocket(porta);
            System.out.printf("Servidor inicializado na porta %d\n", porta);
            System.out.println(this.serverSocket.getInetAddress());

            while (true) {

                this.socket = this.serverSocket.accept();//esperando uma nova conexão

                //cria uma thread para escutar o cliente conectado
                new Thread(new ListenerSocket(socket)).start();

            }

        } catch (IOException e) {

            System.err.println("Erro: não foi possível inicializar o servidor");
            System.err.println(e.getMessage());
        }


    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output; //envia p/ o cliente
        private ObjectInputStream input; // recebe do cliente

        public ListenerSocket(Socket socket) {

            try {
                this.input = new ObjectInputStream(socket.getInputStream());
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void run() {

            Mensagem mensagem = null;

            try {

                while ((mensagem = (Mensagem) input.readObject()) != null) {
                    Mensagem.Acao acao = mensagem.getAcao();

                    if (acao == Mensagem.Acao.CONECTAR) { //solicitação para conectar
                        conectar(mensagem, output);
                        comecarJogo();//verifica se dá para começar o jogo. Caso sim, inicia
                    } else if (acao == Mensagem.Acao.DESCONECTAR) { //solicitação para desconectar
                        desconectar(mensagem);
                    } else if (acao == Mensagem.Acao.DESENHO && mensagem.getId().equals(lobby.desenhistaId)) {
                        transmitirDesenho(mensagem);
                    } else if (acao == Mensagem.Acao.CHUTE) {
                        verificarChute(mensagem);
                    }
                }


            } catch (ClassNotFoundException ex) {
                System.out.println("Mensagem não reconhecida\n");
            } catch (IOException e) {
                System.out.println("Jogador Encerrou inesperadamente\n");
            }
        }
    }

    private void transmitirDesenho(Mensagem mensagem) {
        lobby.getIDJogadoresAdivinham().forEach(destinatario -> sendMessage(Acao.DESENHO, null, destinatario, null, mensagem.getDesenhavel()));
    }

    private void verificarChute(Mensagem mensagem) {
        String idJogador = mensagem.getId();
        lobby.jogadores.keySet().forEach(destinatario -> sendMessage(Acao.CHUTE, mensagem.getConteudo(), destinatario, idJogador, null));
        if (mensagem.getConteudo().equalsIgnoreCase(lobby.desenhoAtual)) {
            sendMessage(Acao.GANHOU, lobby.desenhoAtual, idJogador, idJogador, null);

            lobby.getIDJogadoresAdivinham().stream()
                    .filter(outroJogador -> !outroJogador.equals(idJogador))
                    .forEach(perdedor ->
                            sendMessage(Acao.PERDEU, mensagem.getConteudo(), perdedor, idJogador, null)
                    );
        }
    }

    private void desconectar(Mensagem mensagem) throws IOException {
        System.out.printf("Jogador %s desconectado\n", mensagem.getId());
        var jogadorRemovido = this.lobby.jogadores.remove(mensagem.getId());

        sendMessage(Acao.DESCONECTAR, "Você foi desconectado\n", mensagem.getId()); //envia a desconexão
        jogadorRemovido.close();
        if (this.lobby.desenhistaId != null && this.lobby.desenhistaId.equals(mensagem.getId())) {
            System.out.println("Desenhista saiu\n");
            this.lobby.desenhistaId = null;
            comecarJogo();
        }
    }

    //responde a uma solicitação de conexão
    private void conectar(Mensagem mensagem, ObjectOutputStream ouput) {
        String idJogador = mensagem.getId();
        System.out.printf("Solicitação de conexão de %s\n", idJogador);
        if (lobby.temJogadorComID(idJogador)) {
            System.out.println("Conexão recusada, nome repetido");
            sendMessage(Acao.RECUSADO, "Nome repetido\nEscolha outro\n", idJogador);
        } else {
            System.out.printf("Jogador %s no lobby\n", idJogador);
            lobby.jogadores.put(idJogador, ouput); //adiciona o jogador no lobby
            sendMessage(Acao.CONECTAR, "Esperando mais jogadores!\n", idJogador);
        }

    }

    private void comecarJogo() {
        //Se houver mais um jogador, podemos começar
        if (!lobby.podeComecarJogo()) {
            return;
        }

        lobby.selecionarDesenhista();
        lobby.escolherDesenho();
        System.out.printf("Jogador %s escolhido como desenhista\n", this.lobby.desenhistaId);

        String mensagemDesenho = "Voce desenha!\nDesenhe um: \n" + lobby.desenhoAtual;

        this.sendMessage(Acao.ROLE_DESENHISTA, mensagemDesenho, lobby.desenhistaId);

        for (String destinatario : this.lobby.getIDJogadoresAdivinham()) {
            sendMessage(Acao.ROLE_ADIVINHADOR, "Tente adivinhar o desenho!\n", destinatario);
        }
    }


    private void sendMessage(Acao acao, String conteudo, String destinatario) {
        this.sendMessage(acao, conteudo, destinatario, null, null);
    }

    private void sendMessage(Acao acao, String conteudo, String destinatario, String idJogador, Desenhavel[] desenhavel) {
        try {
            var mensagem = new Mensagem();
            mensagem.setAcao(acao);
            mensagem.setConteudo(conteudo);
            mensagem.setId(idJogador);
            mensagem.setDesenhavel(desenhavel);
            ObjectOutputStream destino = lobby.jogadores.get(destinatario);
            destino.writeObject(mensagem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

