package Desenho;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * 
 * Curva composta por círculos.
 *
 */

public class PincelRedondo extends CurvaLivre{

	public PincelRedondo() {
		super();
	}
	
	PincelRedondo(Color cor, int tamanho){
		
		super(cor, tamanho);
	}
	
	@Override
	public void draw(Graphics g) {
		int tam = getTamanho();
		for(Point ponto : getPontos()) {
			g.setColor(cor);
			g.fillOval(ponto.x, ponto.y, tam, tam);
		}
		
	}

}
