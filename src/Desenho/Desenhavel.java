package Desenho;

import java.awt.Graphics;
import java.io.Serializable;

/**
 * Todos os objetos desenháveis: como elipses, retângulos implementam essa interface. 
 */

public interface Desenhavel extends Serializable {

	/**
	 * desenha a forma na tela.??
	 * @param g
	 */public void draw(Graphics g);

}
