package Desenho;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Representa um traço livre, como os pinceis do Paint
 * 
 *
 */

public abstract class CurvaLivre implements Desenhavel{
	
	private ArrayList<Point> pontos = new ArrayList<>();
	private int tamanho; //grossura do pincel
	Color cor; //cor do pincel
	
	public CurvaLivre() {
		cor = Color.black;
		tamanho = 4;
	}
	
	public CurvaLivre(Color cor, int tamanho) {
		this.cor = cor;
		this.tamanho = tamanho;
	}
	
	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public ArrayList<Point> getPontos() {
		return pontos;
	}

	public void addPonto(Point ponto) {
		this.pontos.add(ponto);
	}

	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}

	
	public abstract void draw(Graphics g);
	
}
