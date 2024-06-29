package Desenho;

import java.awt.*;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 * Programa em si
 *
 */

public class PaintContainer extends JPanel {

	private final JButton desfazer, refazer, limpar;
	private final JPanel painelSuperior;

	private final String[] coresDisponiveis = {"preto", "vermelho", "azul", "laranja", "branco", "verde", "rosa", "amarelo"};
	private final String[] formasDisponiveis = {"reta", "retangulo", "elipse", "pincel quadrado", "pincel redondo"};
	private final Color[] cores = {Color.black, Color.red, Color.blue, Color.orange, Color.white, Color.GREEN, Color.pink, Color.yellow};
	private final Paint.tiposFormas[] tiposFormas = {Paint.tiposFormas.LINHA, Paint.tiposFormas.RETANGULO, Paint.tiposFormas.ELIPSE, Paint.tiposFormas.CURVA_LIVRE_QUADRADO, Paint.tiposFormas.CURVA_LIVRE_REDONDO};
	private final JComboBox<String> coresMenu, coresPreenchimentoMenu, formas;
	private final JRadioButton removerPreenchimento;
	private boolean preencher = false;


	public PaintContainer() {

		JLabel status = new JLabel();

		removerPreenchimento = new JRadioButton("Remover preenchimento", true); //botões para remover o preenchimento da figura

		Paint tela = new Paint(status);//instancia a tela de desenho
		tela.setPreferredSize(new Dimension(1280, 600));

		add(tela, BorderLayout.CENTER);//posiciona a tela no centro da janela

		painelSuperior = new JPanel(); //painel com os botões e menu de seleção de cores e formas
		painelSuperior.setLayout(new GridLayout(1, 8, 2, 1));


		//cria o menu de seleção de cores
		coresMenu = new JComboBox<String>(coresDisponiveis);
		coresMenu.setMaximumRowCount(3);//determina quantas cores são exibidas por vez

		coresMenu.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				tela.setCorAtual(cores[coresMenu.getSelectedIndex()]);
			}
		});

		//cria o menu de seleção de formas
		formas = new JComboBox<String>(formasDisponiveis);
		formas.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				tela.setTipoForma((tiposFormas[formas.getSelectedIndex()]));
			}
		});

		formas.setMaximumRowCount(3); //determina quantas formas são exibidas por vez


		//cria o menu de seleção de cores de preenchimento
		coresPreenchimentoMenu = new JComboBox<String>(coresDisponiveis);
		coresPreenchimentoMenu.setMaximumRowCount(3);


		coresPreenchimentoMenu.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED && preencher) {
				tela.setPreenchimento(cores[coresPreenchimentoMenu.getSelectedIndex()]);
			}

		});

		//botão para remover o preenchimento
		removerPreenchimento.addItemListener(e -> {
			tela.setPreenchimento(null);
			preencher = !preencher;
			//permite que a cor selecionada no menu de cores de preenchimento seja aplicada de vez
			//sem isso, seria necessários selecioná-la de novo
			if (preencher) {
				tela.setPreenchimento(cores[coresPreenchimentoMenu.getSelectedIndex()]);
			}
		});

		//adiciona os menus e botões

		painelSuperior.add(new JLabel("cor de contorno"));
		painelSuperior.add(coresMenu);
		painelSuperior.add(new JLabel("cor de preenchimento"));
		painelSuperior.add(coresPreenchimentoMenu);
		painelSuperior.add(removerPreenchimento);
		painelSuperior.add(formas);


		desfazer = new JButton("Desfazer");
		refazer = new JButton("Refazer");

		limpar = new JButton("Limpar");

		painelSuperior.add(desfazer);
		painelSuperior.add(refazer);
		painelSuperior.add(limpar);

		add(painelSuperior, BorderLayout.NORTH);

		add(status, BorderLayout.SOUTH);

		desfazer.addActionListener(e -> {
			tela.limparUltimaForma();
		});
		limpar.addActionListener(e -> {
			tela.limparTudo();
		});
		refazer.addActionListener(e -> {
			tela.refazer();
		});


	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}