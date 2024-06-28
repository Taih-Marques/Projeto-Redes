package Desenho;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * Representa um local de desenho, como uma tela.
 * Os desenhos são feitos via mouse
 *
 */

public class Paint extends JPanel{

	private ArrayList<Desenhavel> formasDesenhadas = new ArrayList<>();//formas desenhadas
	private ArrayList<Desenhavel> formasOutput = new ArrayList<>();
	private tiposFormas tipoForma; //tipo de forma desenhada
	private Color corAtual; //cor atual da forma
	private Color preenchido; //a forma é preenchida?
	private int quantasFormas; //quantas formas devem ser exibidas
	private Desenhavel formaAtual; //desenho sendo feito atualmente
	
	private JLabel statusLabel; //exibe as coordenadas do mouse

	private Forma f;
	private CurvaLivre cL;
	
	public Paint(JLabel statusLabel) throws IOException {
		
		setTipoForma(tiposFormas.LINHA);
		setBackground(Color.white);
		corAtual = Color.black;
		preenchido = null;
		
		addMouseListener(new MouseInput());
		addMouseMotionListener(new MouseInput());
		
		this.statusLabel = statusLabel;
		
	}
	
	//tipo de formas disponíveis para desenho
	public enum tiposFormas{
		
		LINHA, ELIPSE, RETANGULO, CURVA_LIVRE_QUADRADO, CURVA_LIVRE_REDONDO
	}
	
	//setters e getters
	public void setPreenchimento(Color preenchimento) {
		preenchido = preenchimento;
	}
	
	public void setCorAtual(Color corAtual) {
		
		this.corAtual = corAtual;
	}
	
	public void setTipoForma(tiposFormas tipoForma ) {
		
		this.tipoForma = tipoForma;
	}
	
	public tiposFormas getTipoForma() {
		return this.tipoForma;
	}
	
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(int x, int y) {
		this.statusLabel.setText("( "+x+" , "+y+" )");
	}
	
	//fim dos setters e getters
	
	/**
	 * permite excluir a última figura desenhada
	 * A função não exclui os elementos de formasDesenhadas, apenas decrementa o ponteiro do topo
	 */
	public void limparUltimaForma() {
		
		quantasFormas = (quantasFormas == 0 ? 0 : quantasFormas - 1);
		repaint();
	
	}
	
	/**
	 * adiciona uma nova figura ao array de desenhados
	 * o array funciona como uma pilha, "quantasFormas" é usado como ponteiro para o topo
	 * @param d figura para adicionar
	 */
	public void addAosDesenhados(Desenhavel d) {
		formasDesenhadas.add(quantasFormas++, d);
	}
	
	/**
	 * limpa todos os desenhos da tela
	 */
	public void limparTudo() {
		
		quantasFormas = 0;
		repaint();
		formaAtual = null;
	}
	
	/**
	 * limpa as coordenadas do mouse quando ele sai da tela
	 */
	public void esconderStatus() {
		this.statusLabel.setText("");
	}
	
	/**
	 * redezenha uma figura previamente excluida.
	 * A função simplismente incrementa o ponteiro para o topo
	 */
	public void refazer() {
		
		++quantasFormas;
		if(quantasFormas > formasDesenhadas.size()) {
			quantasFormas = formasDesenhadas.size();
		}
		repaint();
		
	}

	public ArrayList<Desenhavel> getFormasDesenhadas(){

		return this.formasDesenhadas;
	}
	
	/**
	 * pinta todos os desenhos na tela
	 */
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		

		for(int a = 0; a < quantasFormas; ++a) {
			formasDesenhadas.get(a).draw(g);


		}
		
		if(formaAtual != null) {
			formaAtual.draw(g);
		}
		
		
		formaAtual = null;
		
	}
	/**
	 * lida com a entrada do mouse
	 *
	 */
	public class MouseInput extends MouseAdapter implements MouseMotionListener{
		
		/**
		 * exibe as coordenadas do mouse conforme ele se move
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			
			setStatusLabel(e.getX(), e.getY());
		}
		
		/**
		 * deixa de exibir as coordenadas quando o mouse sai da tela
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			esconderStatus();
		}
		
		/**
		 * quando um dos botões do mouse é pressionado, começa a criar a figura selecionada
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			tiposFormas tipo = getTipoForma();//pega qual figura foi selecionada
			switch(tipo) {
			
			case LINHA:
				
				f = new Linha();
				f.setxInicial(e.getX());
				f.setyInicial(e.getY());
				f.setCor(corAtual);
				
				break;
			case RETANGULO:
				f = new Retangulo(e.getX(), e.getY(), 0, 0, corAtual, preenchido);
				
				break;
			case ELIPSE:
				f = new Ellipse(e.getX(), e.getY(), 0, 0, corAtual, preenchido);
				
				break;
			case CURVA_LIVRE_QUADRADO:
				cL = new PincelQuadrado(corAtual, 4);
				cL.addPonto(e.getPoint());
				
				
				break;
			case CURVA_LIVRE_REDONDO:
				cL = new PincelRedondo(corAtual, 4);
				cL.addPonto(e.getPoint());
				
				break;
				
			}
			
		}
		
		/**
		 * quando um botão do mouse é solto, a figura que começou a ser criada é de fato desenhada na tela
		 * e adicionada ao array
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			tiposFormas tipo = getTipoForma();
			if(tipo == tiposFormas.CURVA_LIVRE_QUADRADO || tipo == tiposFormas.CURVA_LIVRE_REDONDO)
			{
				//formasDesenhadas.add(cL);
				addAosDesenhados(cL);
				if(tipo == tiposFormas.CURVA_LIVRE_QUADRADO){

					//formasOutput.add()
					PincelQuadrado p = new  PincelQuadrado();
					//Point t = new Point();

				//	t.setLocation(cL.getPontos().get(cL.getTamanho()-1).getX(), cL.getPontos().get(cL.getTamanho()-1).getY());

//					System.out.println(cL);

                }
				repaint();
			}
			else {
				f.setxFinal(e.getX());
				f.setyFinal(e.getY());
				addAosDesenhados(f);

			}


			formaAtual = null;

			
		}
		
		/**
		 * Desenha a figura na tela conforme ela é feita
		 * A figura não é adicionada ao array, apenas quando o usuário termina (soltando o botão do mouse)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			tiposFormas tipo = getTipoForma();
			setStatusLabel(e.getX(), e.getY());
			if(tipo == tiposFormas.CURVA_LIVRE_QUADRADO || tipo == tiposFormas.CURVA_LIVRE_REDONDO)
			{
				cL.addPonto(e.getPoint());
				formaAtual = cL;
			}
			else {
				
				f.setxFinal(e.getX());
				f.setyFinal(e.getY());
				formaAtual = f;
			}
            repaint();
			
		}
}
}
