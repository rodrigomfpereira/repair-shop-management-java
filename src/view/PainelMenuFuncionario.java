package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Painel que permite apresentar o menu com todas as opções disponíveis para os Funcionários da aplicação.
 * @author Rodrigo Pereira
 */
public class PainelMenuFuncionario extends JPanel implements ActionListener {

	private JanelaPrincipal frame;
	private JPanel painelCentroDinamico;

	private JButton btnCaixaEntrada, btnDecidirAtribuicoes, btnGerirReparacoes, btnAlterarDados, btnSolicitarRemocao, btnTerminarSessao;

	/**
	 * Construtor do Painel do Menu do Funcionário.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelMenuFuncionario(JanelaPrincipal aFrame) {
		frame = aFrame;

		setLayout(new BorderLayout());

		JPanel painelNorte = new JPanel(new GridLayout(3, 1));
		JLabel lblTitulo = new JLabel("Painel de Funcionário", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
		painelNorte.add(new JLabel(""));
		painelNorte.add(lblTitulo);
		painelNorte.add(new JLabel(""));

		JPanel painelMenuEsquerdo = new JPanel(new GridLayout(13, 1, 5, 5)); 
		painelMenuEsquerdo.setBorder(BorderFactory.createTitledBorder("Menu")); 
		painelMenuEsquerdo.setPreferredSize(new Dimension(220, 0));

		btnCaixaEntrada = new JButton("Caixa de Entrada");
		btnCaixaEntrada.addActionListener(this);
		btnCaixaEntrada.setToolTipText("Clique aqui para verificar as suas notificações por ler.");

		btnDecidirAtribuicoes = new JButton("Atribuições a Reparações");
		btnDecidirAtribuicoes.addActionListener(this);
		btnDecidirAtribuicoes.setToolTipText("Clique aqui para gerir as atribuições a reparações pendentes.");

		btnGerirReparacoes = new JButton("Gerir as suas Reparações");
		btnGerirReparacoes.addActionListener(this);
		btnGerirReparacoes.setToolTipText("Clique aqui para gerir as reparações à sua responsabilidade.");

		btnAlterarDados = new JButton("Alterar os Seus Dados");
		btnAlterarDados.addActionListener(this);
		btnAlterarDados.setToolTipText("Clique aqui para modificar as informações do seu perfil.");

		btnSolicitarRemocao = new JButton("Solicitar Remoção de Conta");
		btnSolicitarRemocao.addActionListener(this);
		btnSolicitarRemocao.setToolTipText("Clique aqui para pedir a eliminação dos seus dados do sistema.");

		btnTerminarSessao = new JButton("Terminar Sessão");
		btnTerminarSessao.addActionListener(this);
		btnTerminarSessao.setToolTipText("Clique aqui para terminar sessão e voltar ao menu inicial.");

		painelMenuEsquerdo.add(btnCaixaEntrada);
		painelMenuEsquerdo.add(btnDecidirAtribuicoes);
		painelMenuEsquerdo.add(btnGerirReparacoes);
		painelMenuEsquerdo.add(btnAlterarDados);
		painelMenuEsquerdo.add(btnSolicitarRemocao);

		//espaçadores visuais
		for(int i = 0; i < 7; i++) {
			painelMenuEsquerdo.add(new JLabel(""));
		}

		painelMenuEsquerdo.add(btnTerminarSessao);

		painelCentroDinamico = new JPanel(new BorderLayout());
		painelCentroDinamico.setBorder(BorderFactory.createEtchedBorder()); 
		mostrarMensagemBoasVindas();

		add(painelNorte, BorderLayout.NORTH);
		add(painelMenuEsquerdo, BorderLayout.WEST);
		add(painelCentroDinamico, BorderLayout.CENTER);
	}

	/**
	 * Método que altera o painel de conteúdo dinâmico para o estado inicial (com dica sobre selecionar uma opção do menu)
	 */
	private void mostrarMensagemBoasVindas() {
		painelCentroDinamico.removeAll();
		JLabel lblDica = new JLabel("Selecione uma opção no menu lateral esquerdo para começar.", SwingConstants.CENTER);
		painelCentroDinamico.add(lblDica, BorderLayout.CENTER);
		painelCentroDinamico.revalidate();
		painelCentroDinamico.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões, chamando métodos dependendo do botão do menu selecionado.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnCaixaEntrada)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelCaixaEntrada(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}

		if (e.getSource().equals(btnDecidirAtribuicoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelAtribuicoesFuncionario(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}


		if (e.getSource().equals(btnGerirReparacoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelGerirReparacoesFuncionario(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}

		if (e.getSource().equals(btnAlterarDados)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelAlterarDados(frame, frame.getIdUtilAutenticado(), false), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}

		if (e.getSource().equals(btnSolicitarRemocao)) {
			frame.getControladorMetodosComuns().solicitarRemocaoConta(this);
		}

		if (e.getSource().equals(btnTerminarSessao)) {
			frame.setIdUtilAutenticado(null);
			frame.setUsernameUtilAutenticado(null);
			frame.setNomeUtilAutenticado(null);
			frame.setTipoContaUtilAutenticado(null);
			frame.trocarPainel(new PainelInicial(frame));
		}
	}
}