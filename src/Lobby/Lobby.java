package Lobby;

import java.io.ObjectOutputStream;
import java.util.*;

public class Lobby {
    private List<String> desenhos = List.of("Gato", "Cachorro", "Cadeira", "Tartaruga");
    public Map<String, ObjectOutputStream> jogadores = new Hashtable<>();

    public String desenhistaId;
    public String desenhoAtual;

    public Collection<String> getIDJogadoresAdivinham() {
        return jogadores.keySet().stream()
                .filter(jogadorID -> !jogadorID.equals(desenhistaId))
                .toList();
    }

    public ObjectOutputStream getDesenhista() {
        return jogadores.get(desenhistaId);
    }

    public void selecionarDesenhista() {
        this.desenhistaId = getIDJogadorAleatorio();
    }

    public void escolherDesenho() {
        Random random = new Random();
        int indiceAleatorio = random.nextInt(desenhos.size());
        desenhoAtual = desenhos.get(indiceAleatorio);
    }

    public  String getIDJogadorAleatorio() {
        Random random = new Random();
        int indiceAleatorio = random.nextInt(jogadores.size());
        return jogadores.keySet().stream().toList().get(indiceAleatorio);
    }

    public boolean temJogadorComID(String id) {
        return jogadores.entrySet().stream()
                .anyMatch(jogador -> jogador.getKey().equals(id));
    }

    public boolean podeComecarJogo() {
        return jogadores.size() > 1;
    }
}
