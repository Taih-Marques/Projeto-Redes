package Desenho;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * Figuras 2d. A largura e a altura é calculada através do ponto final e inicial de acordo com a fórmula:
 * largura = |x_inicial - x_final|
 * altura = |y_inicial - y_inicial|
 * O ponto inicial da figura é definido pela menor coordenada entre os pontos passados
 * Podem ser preenchidas com cor
 */

public abstract class FormaFechada extends Forma{

	private Color preenchimento; //cor de preenchimento. Se for null, a figura não é preenchida
	private int xEsquerdo, yEsquerdo; //ponto inicial da figura
	
	public FormaFechada() {
		super();
		preenchimento = null;

	}
	
	public FormaFechada(int xInicial, int yInicial, int xFinal, int yFinal, Color cor, Color preenchimento) {
		
		super(xInicial, yInicial, xFinal, yFinal, cor);
		this.preenchimento = preenchimento;
	}
	
	public Color getPreenchimento() {
		return preenchimento;
	}

	public void setPreenchimento(Color preenchimento) {
		this.preenchimento = preenchimento;
	}
	
	public Color getPreenchimento(Color preenchimento) {
		return this.preenchimento;
	}

	/**
	 * Cálcula a largura da figura como |x_inicial - x_final| e define a coordenada x inicial
	 * o menor x dos pontos passados
	 * @return largura da figura
	 */
	public int calcularLargura() {
		
		if(getxInicial() < getxFinal()) {
			
			xEsquerdo = getxInicial();
			return getxFinal() - getxInicial();
		}
		else {
			
			xEsquerdo = getxFinal();
			return getxInicial()- getxFinal();
		}
		
	}
	/**
	 * Cálcula a altura da figura como |y_inicial - y_final| e define a coordenada y inicial
	 * o menor y dos pontos passados
	 * @return altura da figura
	 */	
	public int calcularAltura() {
		
		if(getyInicial() < getyFinal()) {
			
			yEsquerdo = getyInicial();
			return  getyFinal() - getyInicial();
		}
		else {
			
			yEsquerdo = getyFinal();
			return getyInicial()- getyFinal();
		}
		
	}
	
	public int getXEsquerdo() {return xEsquerdo;};
	public int getYEsquerdo(){return yEsquerdo;};
	

	public abstract void draw(Graphics g);

}
