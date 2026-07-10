package ap3.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Painel inicial da aplicação que permite aos utilizadores autenticarem-se, registarem uma conta ou alterarem os parâmetros de acesso à base de dados.
 * @author Rodrigo Pereira
 */
public class PainelInicial extends JPanel  implements ActionListener{
	private JanelaPrincipal frame;
	private JButton btnRegisto, btnLogin, btnParametros;


	/**
	 * Construtor do Painel Inicial.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelInicial(JanelaPrincipal aFrame) {
		setLayout(new BorderLayout());

		frame = aFrame; 

		JLabel lblTitulo = new JLabel("Departamento de apoio ao cliente - Aplicação Java", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));

		JPanel painelCentral = new JPanel(new GridLayout(3, 1));

		JPanel painelBotoes = new JPanel(new GridLayout(2, 1));

		//Criar botões
		btnLogin = new JButton("Iniciar Sessão");
		btnRegisto = new JButton("Registar Conta");
		btnLogin.addActionListener(this);
		btnLogin.setToolTipText("Clique aqui para entrar no sistema com as suas credenciais.");
		btnRegisto.setToolTipText("Clique aqui para registar uma nova conta no sistema.");
		btnRegisto.addActionListener(this);

		//adicionar botões ao painel para controlar o tamanho
		JPanel painelbtnLogin = new JPanel(new FlowLayout());
		painelbtnLogin.add(btnLogin);

		JPanel painelbtnRegisto = new JPanel(new FlowLayout());
		painelbtnRegisto.add(btnRegisto);

		painelBotoes.add(painelbtnLogin);
		painelBotoes.add(painelbtnRegisto);

		//adicionar célula vazia na grid antes e depois do painel com os botões
		painelCentral.add(new JLabel(""));
		painelCentral.add(painelBotoes);
		painelCentral.add(new JLabel(""));

		//botões que vão para o sul
		btnParametros = new JButton("Configurar Parâmetros de Acesso à Base de Dados");
		btnParametros.addActionListener(this);
		btnParametros.setToolTipText("Clique aqui para configurar os parâmetros de acesso à base de dados (IP, Porto, etc.).");

		//Adicionar ao painel que vai para o sul
		JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		painelSul.add(btnParametros);

		//adicionar às áreas do BorderLayout
		add(lblTitulo, BorderLayout.NORTH);
		add(painelCentral, BorderLayout.CENTER);
		add(painelSul, BorderLayout.SOUTH);

	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 * Prepara o necessário para passar ao painel de registo, início de sessão ou alteração de parâmetros.
	 */
	public void actionPerformed (ActionEvent e){  

		//Alteração de parâmetros
		if (e.getSource().equals( btnParametros ))
			frame.trocarPainel(new PainelParametros(frame));

		//Registo de conta
		if (e.getSource().equals( btnRegisto )) {
			if (!frame.verificarEConfigurarProperties()) {
				return; //não altera de painel
			}

			if (!(frame.getGestorLigacoes().verificarAcessoTabela("utilizador") && frame.getGestorLigacoes().verificarAcessoTabela("cliente") && frame.getGestorLigacoes().verificarAcessoTabela("funcionario") &&
					frame.getGestorLigacoes().verificarAcessoTabela("notificacao") && frame.getGestorLigacoes().verificarAcessoTabela("acao"))) {

				JOptionPane.showMessageDialog(this, "Não foi possível aceder à base de dados.\nPor favor informe o administrador ou tente corrigir os parâmetros de acesso à mesma.",
						"Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return; //não altera de painel
			}

			//criação de gestor caso seja o primeiro utilizador
			if (frame.getGestorLigacoes().isTabelaVazia("utilizador")) {
				frame.trocarPainel(new PainelRegisto(frame, "gestor"));
				return;
			}

			String[] opcoes = { "Cliente", "Funcionário" };

			int escolha = JOptionPane.showOptionDialog(
					this,                               
					"Selecione o tipo de conta que pretende registar:",
					"Tipo de Conta",                      
					JOptionPane.DEFAULT_OPTION,           
					JOptionPane.QUESTION_MESSAGE,         
					null,                                 
					opcoes,                               
					null                             
					);

			//regista com o tipo de conta escolhido
			if (escolha == 0) {
				frame.trocarPainel(new PainelRegisto(frame, "cliente"));
			}
			if (escolha == 1) {
				frame.trocarPainel(new PainelRegisto(frame, "funcionario"));
			}
		}
		
		//Autenticação de conta
		if (e.getSource().equals( btnLogin )) {
			if (!frame.verificarEConfigurarProperties()) {
				return; //não altera de painel
			}
			
			if (!(frame.getGestorLigacoes().verificarAcessoTabela("utilizador") && frame.getGestorLigacoes().verificarAcessoTabela("notificacao") && frame.getGestorLigacoes().verificarAcessoTabela("acao") && frame.getGestorLigacoes().verificarAcessoTabela("reparacao"))) {
				JOptionPane.showMessageDialog(this, "Não foi possível aceder à base de dados.\nPor favor informe o administrador ou tente corrigir os parâmetros de acesso à mesma.",
						"Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return; //não altera de painel
			}
			
			frame.trocarPainel(new PainelLogin(frame));
		}
	}
}
