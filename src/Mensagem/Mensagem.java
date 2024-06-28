package Mensagem;

import Desenho.Desenhavel;

import java.io.Serializable;
import java.util.ArrayList;

public class Mensagem implements Serializable {

    private String conteudo;
    private Acao acao;
    private String id;
    private ArrayList<Desenhavel> formasDesenhadas;

    public Mensagem(){


        this.formasDesenhadas = new ArrayList<>();

    }

    public void setConteudo(String conteudo){

        this.conteudo = conteudo;
    }

    public String getConteudo(){

        return this.conteudo;
    }

    public void setId(String id){

        this.id = id;
    }

    public String getId(){

        return this.id;
    }

    public void setAcao(Acao acao){

        this.acao = acao;
    }

    public Acao getAcao() {
        return this.acao;
    }

    //sobre o que o conteúdo da mensagem
    public enum Acao {

        DESENHO,
        CHUTE,
        ACERTO,
        ERRO,
        CONECTAR,
        RECUSADO,
        DESCONECTAR,
        ROLE_DESENHISTA,
        ROLE_ADIVINHADOR
    }
}
