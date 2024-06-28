package Desenho;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * Desenhos com forma pré-definidas, possuem um ponto inicial e final e podem ser coloridas
 *
 */

public abstract class Forma implements Desenhavel{
	
	private Color cor; //cor do traço
	private int xInicial, yInicial; //ponto inicial, onde a figura começa
	private int xFinal, yFinal; //ponto final, onde a figura termina

	
	public Forma() {
		
		cor = Color.BLACK;
		setxFinal(0);
		setxInicial(0);
		setyFinal(0);
		setyInicial(0);
	}
	
	public int getxInicial() {
		return xInicial;
	}

	public void setxInicial(int xInicial) {
		this.xInicial = xInicial;
	}

	public int getyInicial() {
		return yInicial;
	}

	public void setyInicial(int yInicial) {
		this.yInicial = yInicial;
	}

	public int getxFinal() {
		return xFinal;
	}

	public void setxFinal(int xFinal) {
		this.xFinal = xFinal;
	}

	public int getyFinal() {
		return yFinal;
	}

	public void setyFinal(int yFinal) {
		this.yFinal = yFinal;
	}

	public Forma(int xInicial, int yInicial, int xFinal, int yFinal, Color cor) {
		this.cor = cor;
		setxFinal(xFinal);
		setxInicial(xInicial);
		setyFinal(yFinal);
		setyInicial(yInicial);
		
	}

	public abstract void draw(Graphics g);
	
	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}


	
}
