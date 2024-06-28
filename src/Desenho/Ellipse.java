package Desenho;

import java.awt.Color;
import java.awt.Graphics;

public class Ellipse extends FormaFechada {

	
	public Ellipse() {
		
		super();
	}
	
	public Ellipse(int xInicial, int yInicial, int xFinal, int yFinal, Color cor, Color preenchimento) {
		
		super(xInicial, yInicial, xFinal, yFinal, cor, preenchimento);
	}
	
	@Override
	public void draw(Graphics g) {
		int largura = calcularLargura();
		int altura = calcularAltura();
		


		if(super.getPreenchimento() != null) {
			g.setColor(getPreenchimento());
			g.fillOval(getXEsquerdo(), getYEsquerdo(), largura, altura);
		}
		g.setColor(getCor());
		g.drawOval(getXEsquerdo(), getYEsquerdo(), largura, altura);
	
	}

}
