package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ap3.model.Utilizador;

/**
 * Painel que permite aos utilizadores se autenticarem na aplicação.
 * @author Rodrigo Pereira
 */
public class PainelLogin extends JPanel implements ActionListener{
	private JanelaPrincipal frame;
	private JTextField textoUsername; 
	private JPasswordField textoPass;
	private JButton btnGuardar, btnCancelar;

	/**
	 * Construtor do Painel de Login.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelLogin(JanelaPrincipal aFrame){
		setLayout(new BorderLayout());

		frame = aFrame;

		JLabel lblTitulo = new JLabel("Autenticação de conta", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

		Dimension dimLabel = new Dimension(110, 25);

		//User
		JPanel painelUsername = new JPanel(new FlowLayout());
		JLabel lblUsername = new JLabel("Username:", SwingConstants.RIGHT);
		lblUsername.setPreferredSize(dimLabel); 
		textoUsername = new JTextField(15); 
		textoUsername.setToolTipText("Username da conta");
		painelUsername.add(lblUsername);
		painelUsername.add(textoUsername);

		//Password
		JPanel painelPass = new JPanel(new FlowLayout());
		JLabel lblPass = new JLabel("Password:", SwingConstants.RIGHT);
		lblPass.setPreferredSize(dimLabel); 
		textoPass = new JPasswordField(15); 
		textoPass.setToolTipText("Password de autenticação de conta");
		painelPass.add(lblPass);
		painelPass.add(textoPass);

		//Adicionar ao painel que vai para o centro
		JPanel painelCentral = new JPanel(new GridLayout(8, 1));
		painelCentral.add(new JLabel(""));painelCentral.add(new JLabel(""));painelCentral.add(new JLabel("")); //separadores para juntar os painéis verticalmente
		painelCentral.add(painelUsername);
		painelCentral.add(painelPass);
		painelCentral.add(new JLabel(""));painelCentral.add(new JLabel(""));painelCentral.add(new JLabel("")); //separadores para juntar os painéis verticalmente

		//botões que vão para o sul
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		btnCancelar.setToolTipText("Clique aqui para cancelar e voltar ao menu inicial.");
		btnGuardar = new JButton("Iniciar Sessão");
		btnGuardar.addActionListener(this);
		btnGuardar.setToolTipText("Clique aqui para efetuar a autenticação.");

		//Adicionar ao painel que vai para o sul
		JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
		painelSul.add(btnCancelar);
		painelSul.add(btnGuardar);

		//adicionar às áreas do BorderLayout
		add(lblTitulo, BorderLayout.NORTH);
		add(painelCentral, BorderLayout.CENTER);
		add(painelSul, BorderLayout.SOUTH);
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed (ActionEvent e){
		if (e.getSource().equals( btnCancelar ))
			frame.trocarPainel(new PainelInicial(frame));

		if (e.getSource().equals( btnGuardar )) 
			efetuarProcessoLogin();
	}

	/**
	 * Método responsável por controlar a lógica de autenticação, chamar os métodos que validam os dados e devolver o feedback ao utilizador após verificação da base de dados.
	 */
	private void efetuarProcessoLogin() {
		String username = textoUsername.getText().trim();
		String password = new String(textoPass.getPassword());

		//validar o preenchimento
		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		//validar na base de dados
		Utilizador utilizadorAutenticado = frame.getGestorUtilizadores().validarLogin(username, password);

		if (utilizadorAutenticado == null) {
			JOptionPane.showMessageDialog(this, "Username ou Password incorretos.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int estadoConta = utilizadorAutenticado.getEstado();
		String usernameAutenticado = utilizadorAutenticado.getUsername();
		Integer idAutenticado = frame.getGestorUtilizadores().procurarIDPorUsername(usernameAutenticado);

		if (idAutenticado == null) {
			JOptionPane.showMessageDialog(this, "Erro ao obter o ID do utilizador.\nPor favor, informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (estadoConta == 1) {
			JOptionPane.showMessageDialog(this, "O seu pedido de registo foi rejeitado pelos gestores.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (estadoConta == 0) {
			JOptionPane.showMessageDialog(this, "A sua conta ainda aguarda aprovação de um Gestor.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (estadoConta != 2 && estadoConta != 3) {
			JOptionPane.showMessageDialog(this, "Esta conta foi removida, não é possível efetuar autenticação.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		frame.setIdUtilAutenticado(idAutenticado);
		frame.setUsernameUtilAutenticado(usernameAutenticado);
		frame.setNomeUtilAutenticado(utilizadorAutenticado.getNome());


		frame.getGestorReparacoes().verificarReparacoesAtrasadas();

		String tipoConta = utilizadorAutenticado.getTipo();
		frame.setTipoContaUtilAutenticado(tipoConta);
		int numNotificacoes = 0;
		if (tipoConta.equalsIgnoreCase("gestor")) {
			numNotificacoes = frame.getGestorNotificacoes().contarNotificacoesGestor();
		} else if (tipoConta.equalsIgnoreCase("funcionario")) {
			numNotificacoes = frame.getGestorNotificacoes().contarNotificacoesFuncionario(idAutenticado);
		} else {
			numNotificacoes = frame.getGestorNotificacoes().contarNotificacoesCliente(idAutenticado);
		}

		if (numNotificacoes > 0) {
			JOptionPane.showMessageDialog(this,"Atenção: Tem " + numNotificacoes + " novas notificações por ler na sua Caixa de Entrada.", "Notificações Pendentes", JOptionPane.WARNING_MESSAGE);
		} 

		//envia o utilizador para os respetivos painéis após a validação
		if (tipoConta.equalsIgnoreCase("gestor")) {
			frame.trocarPainel(new PainelMenuGestor(frame));
			} else if (tipoConta.equalsIgnoreCase("funcionario")) {
			frame.trocarPainel(new PainelMenuFuncionario(frame));
		} else {
			frame.trocarPainel(new PainelMenuCliente(frame));
		}
	}
}