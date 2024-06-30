package JogadorFrame;

import ClienteService.ClienteService;
import Desenho.Desenhavel;
import Desenho.Paint;
import Desenho.PaintContainer;
import Mensagem.Mensagem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class JogadorFrame extends JFrame {
    private JPanel barraInferior;
    private JPanel barraSuperior;
    private JButton btnConectar;
    private JButton btnSair;
    private JTextField txtFieldEnviarMsg;
    private JButton btnEnviar;
    private JTextField txtFieldReceber;
    private JPanel principal;
    private JTextPane txtFieldNomeJogador;
    private Paint paint;
    private JPanel BarraDeDesenho;
    private JComboBox comboFormasDisponveis;
    private JLabel labelFormas;
    private JLabel labelCorContorno;
    private JComboBox comboCoresContorno;
    private JComboBox comboCoresPreenchimento;
    private JRadioButton radioPreenchimento;
    private JButton botaoRefazer;
    private JButton botaoDesfazer;
    private JButton botaoLimpar;
    private PaintContainer paintContainer;

    private Mensagem mensagem;
    private Socket socket;
    private ClienteService service;

    private final Color[] cores = {Color.black, Color.red, Color.blue, Color.orange, Color.white, Color.GREEN, Color.pink, Color.yellow};
    private final Paint.tiposFormas[] tiposFormas = {Paint.tiposFormas.LINHA, Paint.tiposFormas.RETANGULO, Paint.tiposFormas.ELIPSE, Paint.tiposFormas.CURVA_LIVRE_QUADRADO, Paint.tiposFormas.CURVA_LIVRE_REDONDO};
    private boolean preencher = false;

    private int porta = 5050;
    private String host = "localhost";
    private String nomeUsuario;
    private Mensagem.Acao papel;

    private final MouseInput mouseInput;

    private void createUIComponents() {
       this.paint = new Paint(new JLabel());
       this.paint.setPreferredSize(new Dimension(1280, 600));
    }

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

        this.mouseInput = new MouseInput();

        comboFormasDisponveis.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if(e.getStateChange() == ItemEvent.SELECTED){

                    paint.setTipoForma(tiposFormas[comboFormasDisponveis.getSelectedIndex()]);
                }
            }
        });
        comboCoresContorno.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                paint.setCorAtual(cores[comboCoresContorno.getSelectedIndex()]);
            }
        });
        comboCoresPreenchimento.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if(e.getStateChange() == ItemEvent.SELECTED && preencher){
                    paint.setPreenchimento(cores[comboCoresPreenchimento.getSelectedIndex()]);
                }

            }
        });
        radioPreenchimento.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                preencher =! preencher;

                paint.setPreenchimento(null);
                if(preencher){
                    paint.setPreenchimento(cores[comboCoresPreenchimento.getSelectedIndex()]);
                }
            }
        });
        botaoDesfazer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paint.limparUltimaForma();
            }
        });
        botaoRefazer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                paint.refazer();
            }
        });
        botaoLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                paint.limparTudo();
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

                    else if(acao == Mensagem.Acao.DESENHO){

                      //  mensagem.getDesenhavel().forEach(System.out::println);

                        //(ArrayList<Desenhavel>) Arrays.asList(mensagem.getDesenhavel())
                        paint.setFormasDesenhadas(new ArrayList<>(Arrays.asList(mensagem.getDesenhavel())));

                        paint.getFormasDesenhadas().forEach(System.out::println);

                        paint.repaint();
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public class MouseInput extends MouseAdapter implements MouseMotionListener {

        /**
         * exibe as coordenadas do mouse conforme ele se move
         */
        @Override
        public void mouseMoved(MouseEvent e) {

            paint.setStatusLabel(e.getX(), e.getY());

        }

        /**
         * deixa de exibir as coordenadas quando o mouse sai da tela
         */
        @Override
        public void mouseExited(MouseEvent e) {
            paint.esconderStatus();
        }

        /**
         * quando um dos botões do mouse é pressionado, começa a criar a figura selecionada
         */
        @Override
        public void mousePressed(MouseEvent e) {
            paint.inicializarDesenho(e);

        }

        /**
         * quando um botão do mouse é solto, a figura que começou a ser criada é de fato desenhada na tela
         * e adicionada ao array
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            paint.finalizarDesenho(e);

            Mensagem mensagem = new Mensagem();

            mensagem.setAcao(Mensagem.Acao.DESENHO);
            mensagem.setId(nomeUsuario);
            mensagem.setDesenhavel(paint.getFormasDesenhadas());

            /*
            for(Desenhavel d : paint.getFormasDesenhadas()){

                System.out.println("O que desenhei: "+d);
            }*/

            service.send(mensagem);



        }

        /**
         * Desenha a figura na tela conforme ela é feita
         * A figura não é adicionada ao array, apenas quando o usuário termina (soltando o botão do mouse)
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            paint.tracarCurva(e);

        }
    }

    private void advinhando(Mensagem mensagem) {

        this.txtFieldReceber.setText(mensagem.getConteudo());

        ativarDesenho(false);

    }

    private void desenhando(Mensagem mensagem) {

        this.txtFieldReceber.setText(mensagem.getConteudo());

        ativarDesenho(true);

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

    private void ativarDesenho(Boolean ativar){

        if(ativar){

            this.paint.addMouseListener(mouseInput);
            this.paint.addMouseMotionListener(mouseInput);

            comboFormasDisponveis.setEnabled(true);
            comboCoresContorno.setEnabled(true);
            comboCoresPreenchimento.setEnabled(true);
            radioPreenchimento.setEnabled(true);
        }
        else {
            this.paint.removeMouseListener(mouseInput);
            this.paint.removeMouseMotionListener(mouseInput);

            comboFormasDisponveis.setEnabled(false);
            comboCoresContorno.setEnabled(false);
            comboCoresPreenchimento.setEnabled(false);
            radioPreenchimento.setEnabled(false);
        }
    }
}
