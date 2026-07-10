package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ap3.model.Cliente;
import ap3.model.Funcionario;
import ap3.model.Utilizador;

/**
 * Painel que permite aos utilizadores alterarem os dados de uma conta na aplicação.
 * @author Rodrigo Pereira
 */
public class PainelAlterarDados extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTextField textoNome, textoUsername, textoEmail, textoNIF, textoContacto, textoMorada, textoSetorAtividade, textoEscalao, textoEspecializacao, textoDataInicioAtividade;
	private JPasswordField textoPass;
	private JButton btnGuardar, btnCancelar;
	private String tipoUtilizador;
	private Dimension dimLabel = new Dimension(110, 25);
	private JPanel painelCentral;

	// Variáveis de controlo do utilizador alvo e sessão
	private Utilizador utilizadorCarregado;
	private int idUtilizadorAlvo;
	private boolean modoEdicaoGestor;
	private boolean alterouNomeSessao = false;
	private boolean alterouUsernameSessao = false;

	/**
	 * Construtor do Painel de Alteração de Dados.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 * @param aIdUtilizadorAlvo ID do utilizador a alterar
	 * @param aModoEdicaoGestor Indica se é uma alteração efetuada por um gestor a outra conta
	 */
	public PainelAlterarDados(JanelaPrincipal aFrame, int aIdUtilizadorAlvo, boolean aModoEdicaoGestor) {
		setLayout(new BorderLayout());

		frame = aFrame; 
		idUtilizadorAlvo = aIdUtilizadorAlvo;
		modoEdicaoGestor = aModoEdicaoGestor;

		JLabel lblTitulo = new JLabel("Alterar Dados de Conta", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

		utilizadorCarregado = frame.getControladorMetodosComuns().obterDadosUtilizadorCompleto(aIdUtilizadorAlvo);
		if (utilizadorCarregado == null) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar dados do utilizador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		tipoUtilizador = utilizadorCarregado.getTipo();

		if ("gestor".equals(tipoUtilizador)) {
			painelCentral = new JPanel(new GridLayout(5, 1));
			adicionarCamposComuns();

		} else if ("cliente".equals(tipoUtilizador)) {
			painelCentral = new JPanel(new GridLayout(10, 1));
			adicionarCamposComuns();
			adicionarCamposComunsCliFunc();
			adicionarCamposCliente();

		} else if ("funcionario".equals(tipoUtilizador)){
			painelCentral = new JPanel(new GridLayout(10, 1));
			adicionarCamposComuns();
			adicionarCamposComunsCliFunc();
			adicionarCamposFuncionario();
		}

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		btnCancelar.setToolTipText("Clique aqui para cancelar e voltar.");
		btnGuardar = new JButton("Gravar Alterações");
		btnGuardar.addActionListener(this);
		btnGuardar.setToolTipText("Clique aqui para guardar as alterações dos dados.");

		JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
		painelSul.add(btnCancelar);
		painelSul.add(btnGuardar);

		add(lblTitulo, BorderLayout.NORTH);
		add(painelCentral, BorderLayout.CENTER);
		add(painelSul, BorderLayout.SOUTH);

		preencherFormularioComDadosAtuais();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed (ActionEvent e){
		if (e.getSource().equals(btnCancelar))
			frame.trocarPainel(new PainelInicial(frame));

		if (e.getSource().equals(btnGuardar)) 
			efetuarProcessoAlteracao();
	}

	/**
	 * Método responsável por controlar a lógica de alteração, chamar as validações e submeter à base de dados.
	 */
	private void efetuarProcessoAlteracao() {
		if (utilizadorCarregado == null) return;

		String nome = textoNome.getText().trim();
		String username = textoUsername.getText().trim();
		String password = new String(textoPass.getPassword());
		String email = textoEmail.getText().trim();

		alterouNomeSessao = false;
		alterouUsernameSessao = false;

		if (!validarCamposComuns(nome, username, password, email)) 
			return; 

		boolean alterarDadosOK = false;

		if ("gestor".equals(tipoUtilizador)) {
			utilizadorCarregado.setPassword(password);
			alterarDadosOK = frame.getGestorUtilizadores().alterarDados(utilizadorCarregado, idUtilizadorAlvo);

		} else {
			String NIF = textoNIF.getText().trim();
			String contacto = textoContacto.getText().trim();
			String morada = textoMorada.getText().trim();

			if (!validarCamposCliFunc(NIF, contacto, morada)) 
				return;

			if ("cliente".equals(tipoUtilizador)) {
				String setor = textoSetorAtividade.getText().trim();
				String escalao = textoEscalao.getText().trim().toUpperCase();

				if (!validarCamposCliente(setor, escalao)) return;

				Cliente cliente = (Cliente) utilizadorCarregado;
				cliente.setPassword(password);
				cliente.setMorada(morada);
				cliente.setSetorAtividade(setor);
				cliente.setEscalao(escalao);

				alterarDadosOK = frame.getGestorUtilizadores().alterarDados(cliente, idUtilizadorAlvo);

			} else if ("funcionario".equals(tipoUtilizador)) {
				String especializacao = textoEspecializacao.getText().trim();
				String dataInicioTexto = textoDataInicioAtividade.getText().trim();

				if (!validarCamposFuncionario(especializacao, dataInicioTexto)) return;

				Funcionario funcionario = (Funcionario) utilizadorCarregado;
				funcionario.setPassword(password);
				funcionario.setMorada(morada);
				funcionario.setEspecializacao(Integer.parseInt(especializacao));
				
				funcionario.setDataInicioAtividade(LocalDate.parse(dataInicioTexto));

				alterarDadosOK = frame.getGestorUtilizadores().alterarDados(funcionario, idUtilizadorAlvo);
			}
		} 

		//feedback centralizado 
		if (alterarDadosOK) {
			JOptionPane.showMessageDialog(this, "Alteração de dados efetuada com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
			frame.getGestorAcoes().registarAcao("alteração de dados", frame.getIdUtilAutenticado());

			if (!modoEdicaoGestor) {
				if (alterouUsernameSessao) frame.setUsernameUtilAutenticado(username);
				if (alterouNomeSessao) frame.setNomeUtilAutenticado(nome);
			}
			
			if ("gestor".equals(frame.getTipoContaUtilAutenticado())) {
				frame.trocarPainel(new PainelMenuGestor(frame));
			} else if ("cliente".equals(frame.getTipoContaUtilAutenticado())) {
				frame.trocarPainel(new PainelMenuCliente(frame));
			} else {
				frame.trocarPainel(new PainelMenuFuncionario(frame));
			}
			 
		} else {
			JOptionPane.showMessageDialog(this, "Ocorreu um erro na inserção dos dados atualizados na base de dados.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
		}
	} 

	/**
	 * Método responsável por criar os componentes comuns e adicioná-los ao painel central.
	 */
	private void adicionarCamposComuns() {
		JPanel painelNome = new JPanel(new FlowLayout());
		JLabel lblNome = new JLabel("Nome:", SwingConstants.RIGHT);
		lblNome.setPreferredSize(dimLabel);
		textoNome = new JTextField(15); 
		textoNome.setToolTipText("Nome do utilizador");
		painelNome.add(lblNome); painelNome.add(textoNome);

		JPanel painelUsername = new JPanel(new FlowLayout());
		JLabel lblUsername = new JLabel("Username:", SwingConstants.RIGHT);
		lblUsername.setPreferredSize(dimLabel); 
		textoUsername = new JTextField(15); 
		textoUsername.setToolTipText("Username do utilizador");
		painelUsername.add(lblUsername); painelUsername.add(textoUsername);

		JPanel painelPass = new JPanel(new FlowLayout());
		JLabel lblPass = new JLabel("Password:", SwingConstants.RIGHT);
		lblPass.setPreferredSize(dimLabel); 
		textoPass = new JPasswordField(15); 
		textoPass.setToolTipText("Password do utilizador");
		painelPass.add(lblPass); painelPass.add(textoPass);

		JPanel painelEmail = new JPanel(new FlowLayout());
		JLabel lblEmail = new JLabel("Email:", SwingConstants.RIGHT);
		lblEmail.setPreferredSize(dimLabel); 
		textoEmail = new JTextField(15); 
		textoEmail.setToolTipText("Email do utilizador");
		painelEmail.add(lblEmail); painelEmail.add(textoEmail);

		painelCentral.add(painelNome);
		painelCentral.add(painelUsername);
		painelCentral.add(painelPass);
		painelCentral.add(painelEmail);
	}

	/**
	 * Método responsável por criar os componentes comuns aos clientes e funcionários.
	 */
	private void adicionarCamposComunsCliFunc() {
		JPanel painelNIF = new JPanel(new FlowLayout());
		JLabel lblNIF = new JLabel("NIF:", SwingConstants.RIGHT);
		lblNIF.setPreferredSize(dimLabel);
		textoNIF = new JTextField(15); 
		textoNIF.setToolTipText("Número de identificação fiscal do utilizador");
		painelNIF.add(lblNIF); painelNIF.add(textoNIF);

		JPanel painelContacto = new JPanel(new FlowLayout());
		JLabel lblContacto = new JLabel("Contacto:", SwingConstants.RIGHT);
		lblContacto.setPreferredSize(dimLabel);
		textoContacto = new JTextField(15); 
		textoContacto.setToolTipText("Contacto telefónico do utilizador");
		painelContacto.add(lblContacto); painelContacto.add(textoContacto);

		JPanel painelMorada = new JPanel(new FlowLayout());
		JLabel lblMorada = new JLabel("Morada:", SwingConstants.RIGHT);
		lblMorada.setPreferredSize(dimLabel);
		textoMorada = new JTextField(15); 
		textoMorada.setToolTipText("Morada do utilizador");
		painelMorada.add(lblMorada); painelMorada.add(textoMorada);

		painelCentral.add(painelNIF);
		painelCentral.add(painelContacto);
		painelCentral.add(painelMorada);
	}

	/**
	 * Método responsável por criar os componentes relativos aos clientes apenas.
	 */
	private void adicionarCamposCliente() {
		JPanel painelSetorAtividade = new JPanel(new FlowLayout());
		JLabel lblSetorAtividade = new JLabel("Setor de atividade:", SwingConstants.RIGHT);
		lblSetorAtividade.setPreferredSize(dimLabel);
		textoSetorAtividade = new JTextField(15); 
		textoSetorAtividade.setToolTipText("Setor de atividade do utilizador");
		painelSetorAtividade.add(lblSetorAtividade); painelSetorAtividade.add(textoSetorAtividade);

		JPanel painelEscalao = new JPanel(new FlowLayout());
		JLabel lblEscalao = new JLabel("Escalão:", SwingConstants.RIGHT);
		lblEscalao.setPreferredSize(dimLabel);
		textoEscalao = new JTextField(15); 
		textoEscalao.setToolTipText("Escalão do utilizador (A, B, C ou D)");
		painelEscalao.add(lblEscalao); painelEscalao.add(textoEscalao);

		painelCentral.add(painelSetorAtividade);
		painelCentral.add(painelEscalao);
	}

	/**
	 * Método responsável por criar os componentes relativos aos funcionários apenas.
	 */
	private void adicionarCamposFuncionario() {
		JPanel painelEspecializacao = new JPanel(new FlowLayout());
		JLabel lblEspecializacao = new JLabel("Especialização:", SwingConstants.RIGHT);
		lblEspecializacao.setPreferredSize(dimLabel);
		textoEspecializacao = new JTextField(15); 
		textoEspecializacao.setToolTipText("Especialização do utilizador (deverá ser um valor númerico)");
		painelEspecializacao.add(lblEspecializacao); painelEspecializacao.add(textoEspecializacao);

		JPanel painelDataInicioAtividade = new JPanel(new FlowLayout());
		JLabel lblDataInicioAtividade = new JLabel("Data de início:", SwingConstants.RIGHT);
		lblDataInicioAtividade.setPreferredSize(dimLabel);
		textoDataInicioAtividade = new JTextField(15); 
		textoDataInicioAtividade.setToolTipText("Data de início de atividade do utilizador (AAAA-MM-DD)");
		painelDataInicioAtividade.add(lblDataInicioAtividade); painelDataInicioAtividade.add(textoDataInicioAtividade);

		painelCentral.add(painelEspecializacao);
		painelCentral.add(painelDataInicioAtividade);
	}

	/**
	 * Método responsável por carregar os dados guardados no objeto para dentro dos componentes gráficos.
	 */
	private void preencherFormularioComDadosAtuais() {
		textoNome.setText(utilizadorCarregado.getNome());
		textoUsername.setText(utilizadorCarregado.getUsername());
		textoPass.setText(utilizadorCarregado.getPassword());
		textoEmail.setText(utilizadorCarregado.getEmail());

		if (utilizadorCarregado instanceof Cliente) {
			Cliente cliente = (Cliente) utilizadorCarregado;
			textoNIF.setText(String.valueOf(cliente.getNIF()));
			textoContacto.setText(String.valueOf(cliente.getContacto()));
			textoMorada.setText(cliente.getMorada());
			textoSetorAtividade.setText(cliente.getSetorAtividade());
			textoEscalao.setText(cliente.getEscalao());
		} else if (utilizadorCarregado instanceof Funcionario) {
			Funcionario funcionario = (Funcionario) utilizadorCarregado;
			textoNIF.setText(String.valueOf(funcionario.getNIF()));
			textoContacto.setText(String.valueOf(funcionario.getContacto()));
			textoMorada.setText(funcionario.getMorada());
			textoEspecializacao.setText(String.valueOf(funcionario.getEspecializacao()));
			textoDataInicioAtividade.setText(funcionario.getDataInicioAtividade().toString());
		}
	}

	/**
	 * Método que permite validar os campos comuns apenas se estes tiverem sofrido modificações em relação aos atributos original.
	 */
	private boolean validarCamposComuns(String aNome, String aUsername, String aPassword, String aEmail) {
		if (aNome.isEmpty() || aUsername.isEmpty() || aPassword.isEmpty() || aEmail.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!aUsername.equals(utilizadorCarregado.getUsername())) {
			if (!frame.getControladorMetodosComuns().verificarDisponibilidadeUsername(aUsername)) {
				JOptionPane.showMessageDialog(this, "O username '" + aUsername + "' não está disponível.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			utilizadorCarregado.setUsername(aUsername);
			alterouUsernameSessao = true;
		}

		if (!aEmail.equals(utilizadorCarregado.getEmail())) {
			if (!frame.getControladorMetodosComuns().validarFormatoEmail(aEmail)) {
				JOptionPane.showMessageDialog(this, "Formato de email inválido.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (!frame.getControladorMetodosComuns().verificarDisponibilidadeEmail(aEmail)) {
				JOptionPane.showMessageDialog(this, "Erro: O email '" + aEmail + "' não está disponível.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			utilizadorCarregado.setEmail(aEmail);
		}

		if (!aNome.equals(utilizadorCarregado.getNome())) {
			utilizadorCarregado.setNome(aNome);
			alterouNomeSessao = true;
		}
		return true;
	}

	/**
	 * Método que permite validar os campos comuns de Cliente e Funcionário apenas se modificados.
	 */
	private boolean validarCamposCliFunc(String aNIF, String aContacto, String aMorada) {
		if (aNIF.isEmpty() || aContacto.isEmpty() || aMorada.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos básicos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			Integer.parseInt(aNIF);
			Integer.parseInt(aContacto); 
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this, "O NIF e o Contacto devem ser estritamente numéricos.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		int nifAtual = -1;
		int contactoAtual = -1;
		
		if (utilizadorCarregado instanceof Cliente) {
			Cliente cliente = (Cliente) utilizadorCarregado;
			nifAtual = cliente.getNIF();
			contactoAtual = cliente.getContacto();
		} else if (utilizadorCarregado instanceof Funcionario) {
			Funcionario funcionario = (Funcionario) utilizadorCarregado;
			nifAtual = funcionario.getNIF();
			contactoAtual = funcionario.getContacto();
		}

		if (Integer.parseInt(aNIF) != nifAtual) {
			if (!frame.getControladorMetodosComuns().validarFormatoNIF(aNIF) || !frame.getControladorMetodosComuns().verificarDisponibilidadeNIF(aNIF)) {
				JOptionPane.showMessageDialog(this, "NIF inválido ou já registado.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		if (Integer.parseInt(aContacto) != contactoAtual) {
			if (!frame.getControladorMetodosComuns().validarFormatoContacto(aContacto)) {
				JOptionPane.showMessageDialog(this, "O contacto deve começar por 9, 2 ou 3 e ter 9 dígitos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		
		if (utilizadorCarregado instanceof Cliente) {
			Cliente cliente = (Cliente) utilizadorCarregado;
			cliente.setNIF(Integer.parseInt(aNIF));
			cliente.setContacto(Integer.parseInt(aContacto));
		} else if (utilizadorCarregado instanceof Funcionario) {
			Funcionario funcionario = (Funcionario) utilizadorCarregado;
			funcionario.setNIF(Integer.parseInt(aNIF));
			funcionario.setContacto(Integer.parseInt(aContacto));
		}

		return true;
	}

	/**
	 * Método que permite validar os campos específicos de Funcionário apenas se modificados.
	 */
	private boolean validarCamposFuncionario(String aEspecializacao, String aDataInicio) {
		if (aEspecializacao.isEmpty() || aDataInicio.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos do funcionário.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		Funcionario funcionario = (Funcionario) utilizadorCarregado;

		if (Integer.parseInt(aEspecializacao) != funcionario.getEspecializacao()) {
			if (!frame.getControladorMetodosComuns().validarFormatoEspecializacao(aEspecializacao)) {
				JOptionPane.showMessageDialog(this, "A especialização deve ser um valor numérico.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		if (!aDataInicio.equals(funcionario.getDataInicioAtividade().toString())) {
			if (!frame.getControladorMetodosComuns().validarFormatoData(aDataInicio)) {
				JOptionPane.showMessageDialog(this, "Formato de data inválido. Use Ano-Mês-Dia (Ex: 2026-03-28).", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * Método que permite validar os campos específicos de Cliente apenas se modificados.
	 */
	private boolean validarCamposCliente(String aSetor, String aEscalao) {
		if (aSetor.isEmpty() || aEscalao.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos do cliente.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		Cliente cliente = (Cliente) utilizadorCarregado;

		if (!aEscalao.equals(cliente.getEscalao())) {
			if (!aEscalao.matches("^[A-D]$")) {
				JOptionPane.showMessageDialog(this, "O escalão deve ser apenas A, B, C ou D.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		return true;
	}
}