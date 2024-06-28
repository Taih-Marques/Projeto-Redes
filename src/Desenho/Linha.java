package Desenho;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Um simples segmento de reta
 *
 */

public class Linha extends Forma {

	public Linha(int xInicial, int yInicial, int xFinal, int yFinal, Color cor) {
		super(xInicial, yInicial, xFinal, yFinal, cor);
	}
	
	public Linha() {
		super();
	}

	@Override
	public void draw(Graphics g) {
		
		g.setColor(getCor());
		g.drawLine(getxInicial(), getyInicial(), getxFinal(), getyFinal());
		
	}

}
