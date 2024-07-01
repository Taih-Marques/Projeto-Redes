package ServidorService;

import Desenho.Desenhavel;
import Mensagem.Mensagem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;

    private ObjectOutputStream desenhista; //se comunica com quem está desenhando
    private String desenhistaId;

    private Map<String, ObjectOutputStream> jogadoresAdvinham; //jogadores com função de advinhar
    private Map<String, ObjectOutputStream> lobby; //jogadores sem papel

    public ServidorService(int porta){

        this.lobby = new HashMap<>();
        this.jogadoresAdvinham = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(porta);
            System.out.printf("Servidor inicializado na porta %d\n", porta);
            System.out.println( this.serverSocket.getInetAddress());

            while (true){

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

                    //if ((mensagem = (Mensagem)input.readObject()) == null) break;

                    Mensagem.Acao acao = mensagem.getAcao();

                    if (acao == Mensagem.Acao.CONECTAR) { //solicitação para conectar

                        System.out.printf("Solicitação de conexão de %s\n",mensagem.getId());
                        connectar(mensagem, output);
                    } else if (acao == Mensagem.Acao.DESCONECTAR) { //solicitação para desconectar

                        System.out.printf("Jogador %s desconectado\n",mensagem.getId());
                        disconectar(mensagem, output);
                        break;

                    } else if (acao == Mensagem.Acao.DESENHO && mensagem.getId().equals(desenhistaId)) {

                        Mensagem doDesenhista = new Mensagem();

                        doDesenhista.setId(mensagem.getId());
                        doDesenhista.setAcao(Mensagem.Acao.DESENHO);
                        doDesenhista.setDesenhavel(mensagem.getDesenhavel());

                        /*
                        for(Desenhavel d : mensagem.getDesenhavel()){

                            System.out.println("O que recebi: "+d);
                        }*/

                        jogadoresAdvinham.values().forEach(jogador -> sendMenssage(doDesenhista, jogador));

                    } else if (acao == Mensagem.Acao.CHUTE) {

                        Mensagem doAdivinhador = new Mensagem();

                        /*System.out.println("Chute de "+mensagem.getConteudo());
                        System.out.printf("Chute de %s, desenhista %s\n",mensagem.getId(), desenhistaId);*/

                        doAdivinhador.setId(mensagem.getId()); //id do adivinhador
                        doAdivinhador.setAcao(Mensagem.Acao.CHUTE);
                        doAdivinhador.setConteudo(mensagem.getConteudo());

                        jogadoresAdvinham.values().forEach(jogador -> sendMenssage(doAdivinhador, jogador));
                        sendMenssage(doAdivinhador, desenhista);

                    }


                }


            }
            catch (ClassNotFoundException ex){

                ex.printStackTrace();
            }
            catch (IOException e){

               // System.out.println("Jogador Encerrou inesperadamente\n");
                 e.printStackTrace();
            }
        }

    }

    private void disconectar(Mensagem mensagem, ObjectOutputStream output) {
        Mensagem novaMensagem = new Mensagem();

        novaMensagem.setAcao(Mensagem.Acao.DESCONECTAR); //confirma a desconexão
        this.jogadoresAdvinham.remove(mensagem.getId());
        this.lobby.remove(mensagem.getId());

        novaMensagem.setConteudo("Você foi desconectado\n");
        sendMenssage(novaMensagem, output); //envia a desconexão

        if(this.desenhistaId != null && this.desenhistaId.equals(mensagem.getId())){

            System.out.println("Desenhista saiu\n");
            this.desenhistaId = null;
            this.desenhista = null;
            selecionarDesenhista(mensagem.getId());
        }

    }

    //responde a uma solicitação de conexão
    private void connectar(Mensagem mensagem, ObjectOutputStream ouput) {

        if(lobby.containsKey(mensagem.getId())){

            System.out.println("Conexão recusada, nome repetido");
            mensagem.setConteudo("Nome repetido\nEscolha outro\n");
            mensagem.setAcao(Mensagem.Acao.RECUSADO);
            sendMenssage(mensagem, ouput);
        }
        /**/else{

            System.out.printf("Jogador %s no lobby\n",mensagem.getId());
            lobby.put(mensagem.getId(), ouput); //adiciona o jogador no lobby
            mensagem.setAcao(Mensagem.Acao.CONECTAR);
            mensagem.setConteudo("Esperando mais jogadores!\n");
            sendMenssage(mensagem,ouput);

           selecionarDesenhista(mensagem.getId());//verifica se dá para começar o jogo. Caso sim, inicia
        }

    }

    private void selecionarDesenhista(String inicial) {

        //Se houver mais um jogador, podemos começar
        if (this.lobby.size() > 1 || !this.jogadoresAdvinham.isEmpty()) {
            Random random = new Random();

            if(this.desenhista == null){//seleciona um desenhista

                this.desenhistaId = inicial;
                this.desenhista = lobby.remove(this.desenhistaId);

                //seleciona um aleatório para desenhar
                for (String candidato : this.lobby.keySet()) {

                    if (random.nextBoolean()) {

                        this.lobby.put(this.desenhistaId, this.desenhista); //coloca o antigo sorteado

                        this.desenhista = this.lobby.remove(candidato); //remove o desenhista do lobby
                        this.desenhistaId = candidato;
                        break;
                    }
                }

                //informa o desenhista
                Mensagem mensagem = new Mensagem();

                System.out.printf("Jogador %s escolhido como desenhista\n", this.desenhistaId);

                mensagem.setAcao(Mensagem.Acao.ROLE_DESENHISTA);
                mensagem.setConteudo("Voce desenha!\nEscolha um desenho\n");
                mensagem.setId(this.desenhistaId);
                this.sendMenssage(mensagem, desenhista);

            }



            for(String jogador : this.lobby.keySet()){

                ObjectOutputStream jog = this.lobby.remove(jogador);
                this.jogadoresAdvinham.put(jogador, jog);
                Mensagem mensagem = new Mensagem();
                mensagem.setAcao(Mensagem.Acao.ROLE_ADIVINHADOR);
                mensagem.setConteudo("Tente adivinhar o desenho!\n");

                sendMenssage(mensagem,jog);
            }


        }
        else{
            var mensagem= new Mensagem();

            System.out.printf("Jogador %s no lobby\n",mensagem.getId());
            mensagem.setAcao(Mensagem.Acao.CONECTAR);
            mensagem.setConteudo("Esperando mais jogadores!\n");
            // sendMenssage(mensagem,ouput);
        }
    }

    private void sendMenssage(Mensagem mensagem, ObjectOutputStream ouput) {

        try {
            //System.out.println("jao");
            ouput.writeObject(mensagem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

