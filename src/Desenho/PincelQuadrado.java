package Desenho;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * 
 * Curva composta por quadrados
 *
 */

public class PincelQuadrado extends CurvaLivre {

	public PincelQuadrado() {
		super();
	}
	
	PincelQuadrado(Color cor, int tamanho){
		
		super(cor, tamanho);
	}
	
	@Override
	public void draw(Graphics g) {
		int tam = getTamanho();
		for(Point ponto : getPontos()) {
			g.setColor(cor);
			g.fillRect(ponto.x, ponto.y, tam, tam);
		}
		
		
	}

}
