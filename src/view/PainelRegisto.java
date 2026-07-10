package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ap3.model.Cliente;
import ap3.model.Funcionario;
import ap3.model.Utilizador;


/**
 * Painel que permite aos utilizadores registarem uma conta na aplicação.
 * @author Rodrigo Pereira
 */
public class PainelRegisto extends JPanel implements ActionListener{
	private JanelaPrincipal frame;
	private JTextField textoNome, textoUsername, textoEmail, textoNIF, textoContacto, textoMorada, textoSetorAtividade, textoEscalao, textoEspecializacao, textoDataInicioAtividade;
	private JPasswordField textoPass;
	private JLabel lblStatusFoto;
	private File ficheiroFotoSelecionado = null;
	private JButton btnGuardar, btnCancelar, btnSelecionarFoto;
	private String tipoUtilizador;
	private Dimension dimLabel = new Dimension(110, 25);
	private JPanel painelCentral;

	/**
	 * Construtor do Painel de Registo.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 * @param aTipoUtilizador Tipo de conta a ser registada
	 */
	public PainelRegisto(JanelaPrincipal aFrame, String aTipoUtilizador) {
		setLayout(new BorderLayout());

		tipoUtilizador = aTipoUtilizador;
		frame = aFrame; 

		JLabel lblTitulo = new JLabel("Registo de Conta", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));



		//registo de gestor
		if ("gestor".equals(tipoUtilizador)) {
			painelCentral = new JPanel(new GridLayout(6, 1));
			adicionarCamposComuns();

			//registo de cliente
		} else if ("cliente".equals(tipoUtilizador)) {
			painelCentral = new JPanel(new GridLayout(11,1));
			adicionarCamposComuns();
			adicionarCamposComunsCliFunc();
			adicionarCamposCliente();

			//registo de funcionário	
		} else if ("funcionario".equals(tipoUtilizador)){
			painelCentral = new JPanel(new GridLayout(11,1));
			adicionarCamposComuns();
			adicionarCamposComunsCliFunc();
			adicionarCamposFuncionario();
		}

		//botões que vão para o sul
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		btnCancelar.setToolTipText("Clique aqui para cancelar e voltar ao menu inicial.");
		btnGuardar = new JButton("Registar");
		btnGuardar.addActionListener(this);
		btnGuardar.setToolTipText("Clique aqui para registar a conta.");

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
			efetuarProcessoRegisto();

		if (e.getSource().equals(btnSelecionarFoto)) {
			//Inicia e aponta para a pasta raiz do projeto
			JFileChooser dLerFich = new JFileChooser(".");
			dLerFich.setDialogTitle("Selecionar Foto de Perfil");
			dLerFich.setFileSelectionMode(JFileChooser.FILES_ONLY);

			//Desativa todos os tipos de ficheiro
			dLerFich.setAcceptAllFileFilterUsed(false);

			//Cria o filtro específico para as imagens
			FileNameExtensionFilter filtroImagens = new FileNameExtensionFilter("Imagens (JPG, JPEG, PNG)", "jpg", "jpeg", "png");

			//Adiciona o filtro ao componente
			dLerFich.addChoosableFileFilter(filtroImagens);

			//Abre a janela de diálogo e verifica se o utilizador confirmou a seleção
			if (dLerFich.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				ficheiroFotoSelecionado = dLerFich.getSelectedFile();
				lblStatusFoto.setText(ficheiroFotoSelecionado.getName()); //Atualiza o feedback
			}
		}
	}

	/**
	 * Método responsável por controlar a lógica de registo, chamar os métodos que validam os dados e devolver o feedback ao utilizador após inserção na base de dados.
	 */
	private void efetuarProcessoRegisto() {
		//extrai e limpa os campos comuns
		String nome = textoNome.getText().trim();
		String username = textoUsername.getText().trim();
		String password = new String(textoPass.getPassword());
		String email = textoEmail.getText().trim();

		Utilizador novoUtilizador = null;

		//valida os campos
		if (!validarCamposComuns(nome, username, password, email)) 
			return; 


		//criação de gestor
		if ("gestor".equals(tipoUtilizador)) {
			novoUtilizador = new Utilizador(nome, username, password, 2, email, "gestor");	

			//cliente e funcionário
		} else {
			String NIF = textoNIF.getText().trim();
			String contacto = textoContacto.getText().trim();
			String morada = textoMorada.getText().trim();

			if (!validarCamposCliFunc(NIF, contacto, morada)) 
				return;


			int nifFinal = Integer.parseInt(NIF);
			int contactoFinal = Integer.parseInt(contacto);
			int estadoPendente = 0;

			//criação de cliente 
			if ("cliente".equals(tipoUtilizador)) {
				String setor = textoSetorAtividade.getText().trim();
				String escalao = textoEscalao.getText().trim().toUpperCase();


				if (!validarCamposCliente(setor, escalao)) {
					return;
				}

				novoUtilizador = new Cliente(nome, username, password, estadoPendente, email, "cliente", nifFinal, contactoFinal, morada, setor, escalao);

				//criação de funcionário
			} else if ("funcionario".equals(tipoUtilizador)) {
				String especializacao = textoEspecializacao.getText().trim();
				String dataInicioTexto = textoDataInicioAtividade.getText().trim();

				if (!validarCamposFuncionario(especializacao, dataInicioTexto)) {
					return;
				}

				int especializacaoFinal = Integer.parseInt(especializacao);
				LocalDate dataInicioFinal = LocalDate.parse(dataInicioTexto);

				novoUtilizador = new Funcionario(nome, username, password, estadoPendente, email, "funcionario", nifFinal, contactoFinal, morada, especializacaoFinal, dataInicioFinal);
			}
		} 

		//feedback centralizado
		if (novoUtilizador != null) {
			try {
				boolean registoOK = frame.getGestorUtilizadores().registarUtilizador(novoUtilizador);

				if (registoOK) {

					if ("gestor".equals(tipoUtilizador)) {
						JOptionPane.showMessageDialog(this, "Conta registada com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "Conta registada com sucesso, pode iniciar sessão aquando da aprovação de registo por parte dos gestores.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
					}

					frame.trocarPainel(new PainelInicial(frame));

				} else {
					JOptionPane.showMessageDialog(this, "Ocorreu um erro no registo de conta.\nPor favor informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "A ligação à base de dados falhou.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				frame.trocarPainel(new PainelInicial(frame));
			}
		}
	} 


	/**
	 * Método responsável por criar os componentes comuns a todos os tipos de conta e adicioná-los ao painel central.
	 */
	private void adicionarCamposComuns() {
		//Nome
		JPanel painelNome = new JPanel(new FlowLayout());
		JLabel lblNome = new JLabel("Nome:", SwingConstants.RIGHT);
		lblNome.setPreferredSize(dimLabel);
		textoNome = new JTextField(15); 
		textoNome.setToolTipText("Primeiro e último nome (e.g. João Gomes)");
		painelNome.add(lblNome);
		painelNome.add(textoNome);

		//Username
		JPanel painelUsername = new JPanel(new FlowLayout());
		JLabel lblUsername = new JLabel("Username:", SwingConstants.RIGHT);
		lblUsername.setPreferredSize(dimLabel); 
		textoUsername = new JTextField(15); 
		textoUsername.setToolTipText("Username a utilizar dentro da aplicação");
		painelUsername.add(lblUsername);
		painelUsername.add(textoUsername);

		//Password
		JPanel painelPass = new JPanel(new FlowLayout());
		JLabel lblPass = new JLabel("Password:", SwingConstants.RIGHT);
		lblPass.setPreferredSize(dimLabel); 
		textoPass = new JPasswordField(15); 
		textoPass.setToolTipText("Password pessoal de acesso à aplicação");
		painelPass.add(lblPass);
		painelPass.add(textoPass);

		//Email
		JPanel painelEmail = new JPanel(new FlowLayout());
		JLabel lblEmail = new JLabel("Email:", SwingConstants.RIGHT);
		lblEmail.setPreferredSize(dimLabel); 
		textoEmail = new JTextField(15); 
		textoEmail.setToolTipText("Email (e.g. xpto@gmail.com)");
		painelEmail.add(lblEmail);
		painelEmail.add(textoEmail);

		//Foto de perfil
		JPanel painelFoto = new JPanel(new FlowLayout());
		JLabel lblFoto = new JLabel("Foto de Perfil:", SwingConstants.RIGHT);
		lblFoto.setPreferredSize(dimLabel);

		btnSelecionarFoto = new JButton("Escolher Imagem...");
		btnSelecionarFoto.addActionListener(this);
		btnSelecionarFoto.setToolTipText("Selecione uma imagem de perfil (Opcional).");

		lblStatusFoto = new JLabel("Nenhuma selecionada");
		lblStatusFoto.setFont(new Font("Arial", Font.ITALIC, 11));
		painelFoto.add(lblFoto);
		painelFoto.add(btnSelecionarFoto);
		painelFoto.add(lblStatusFoto);

		//Adicionar ao painel que vai para o centro
		painelCentral.add(new JLabel(""));
		painelCentral.add(painelNome);
		painelCentral.add(painelUsername);
		painelCentral.add(painelPass);
		painelCentral.add(painelEmail);
		painelCentral.add(painelFoto);
	}

	/**
	 * Método responsável por criar os componentes comuns aos clientes e funcionários e adicioná-los ao painel central.
	 */
	private void adicionarCamposComunsCliFunc() {
		//NIF
		JPanel painelNIF = new JPanel(new FlowLayout());
		JLabel lblNIF = new JLabel("NIF:", SwingConstants.RIGHT);
		lblNIF.setPreferredSize(dimLabel);
		textoNIF = new JTextField(15); 
		textoNIF.setToolTipText("Número de identificação fiscal (deve conter 9 números)");
		painelNIF.add(lblNIF);
		painelNIF.add(textoNIF);

		//Contacto
		JPanel painelContacto = new JPanel(new FlowLayout());
		JLabel lblContacto = new JLabel("Contacto:", SwingConstants.RIGHT);
		lblContacto.setPreferredSize(dimLabel);
		textoContacto = new JTextField(15); 
		textoContacto.setToolTipText("Número de telemóvel (deve conter 9 números e ser um número de telemóvel válido)");
		painelContacto.add(lblContacto);
		painelContacto.add(textoContacto);

		//Morada
		JPanel painelMorada = new JPanel(new FlowLayout());
		JLabel lblMorada = new JLabel("Morada:", SwingConstants.RIGHT);
		lblMorada.setPreferredSize(dimLabel);
		textoMorada = new JTextField(15); 
		textoMorada.setToolTipText("Morada (e.g. Rua XPTO nº 123, Coimbra)");
		painelMorada.add(lblMorada);
		painelMorada.add(textoMorada);

		//Adicionar ao painel que vai para o centro
		painelCentral.add(painelNIF);
		painelCentral.add(painelContacto);
		painelCentral.add(painelMorada);
	}

	/**
	 * Método responsável por criar os componentes relativos aos clientes apenas, e adicioná-los ao painel central.
	 */
	private void adicionarCamposCliente() {
		//Setor de atividade
		JPanel painelSetorAtividade = new JPanel(new FlowLayout());
		JLabel lblSetorAtividade = new JLabel("Setor de atividade:", SwingConstants.RIGHT);
		lblSetorAtividade.setPreferredSize(dimLabel);
		textoSetorAtividade = new JTextField(15); 
		textoSetorAtividade.setToolTipText("Setor de atividade (e.g. Contabilidade)");
		painelSetorAtividade.add(lblSetorAtividade);
		painelSetorAtividade.add(textoSetorAtividade);

		//Escalão
		JPanel painelEscalao = new JPanel(new FlowLayout());
		JLabel lblEscalao = new JLabel("Escalão:", SwingConstants.RIGHT);
		lblEscalao.setPreferredSize(dimLabel);
		textoEscalao = new JTextField(15); 
		textoEscalao.setToolTipText("Escalão (A, B, C ou D)");
		painelEscalao.add(lblEscalao);
		painelEscalao.add(textoEscalao);

		//Adicionar ao painel que vai para o centro
		painelCentral.add(painelSetorAtividade);
		painelCentral.add(painelEscalao);
	}

	/**
	 * Método responsável por criar os componentes relativos aos funcionários apenas, e adicioná-los ao painel central.
	 */
	private void adicionarCamposFuncionario() {
		//Especialização
		JPanel painelEspecializacao = new JPanel(new FlowLayout());
		JLabel lblEspecializacao = new JLabel("Especialização:", SwingConstants.RIGHT);
		lblEspecializacao.setPreferredSize(dimLabel);
		textoEspecializacao = new JTextField(15); 
		textoEspecializacao.setToolTipText("Especialização (deverá ser um valor númerico)");
		painelEspecializacao.add(lblEspecializacao);
		painelEspecializacao.add(textoEspecializacao);

		//Data de início de atividade
		JPanel painelDataInicioAtividade = new JPanel(new FlowLayout());
		JLabel lblDataInicioAtividade = new JLabel("Data de início:", SwingConstants.RIGHT);
		lblDataInicioAtividade.setPreferredSize(dimLabel);
		textoDataInicioAtividade = new JTextField(15); 
		textoDataInicioAtividade.setToolTipText("Data de início de atividade (AAAA-MM-DD)");
		painelDataInicioAtividade.add(lblDataInicioAtividade);
		painelDataInicioAtividade.add(textoDataInicioAtividade);

		//Adicionar ao painel que vai para o centro
		painelCentral.add(painelEspecializacao);
		painelCentral.add(painelDataInicioAtividade);
	}



	/**
	 * Método responsável por validar os campos comuns a todos os tipos de conta, verificando  se estão preenchidos e o formato corresponde ao esperado e se, no caso do username e email, estes são únicos para os utilizadores.
	 * @param aNome Primeiro e último nome
	 * @param aUsername Username
	 * @param aPassword Password
	 * @param aEmail Email
	 * @return false caso alguma das validações dê errado, true caso contrário.
	 */
	private boolean validarCamposComuns(String aNome, String aUsername, String aPassword, String aEmail) {
		if (aNome.isEmpty() || aUsername.isEmpty() || aPassword.isEmpty() || aEmail.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().verificarDisponibilidadeUsername(aUsername)) {
			JOptionPane.showMessageDialog(this, "O username '" + aUsername + "' não está disponível.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().validarFormatoEmail(aEmail)) {
			JOptionPane.showMessageDialog(this, "Formato de email inválido, garanta tem pelo menos 4 dígitos seguido de entidade e domínio válidos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().verificarDisponibilidadeEmail(aEmail)) {
			JOptionPane.showMessageDialog(this, "Erro: O email '" + aEmail + "' não está disponível.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Método responsável por validar os campos comuns a funcionários e clientes, verificando se estão preenchidos e o formato corresponde ao esperado e se, no caso do NIF, este é único para os utilizadores.
	 * @param aNIF NIF
	 * @param aContacto Contacto
	 * @param aMorada Morada
	 * @return false caso alguma das validações dê errado, true caso contrário.
	 */
	private boolean validarCamposCliFunc(String aNIF, String aContacto, String aMorada) {
		if (aNIF.isEmpty() || aContacto.isEmpty() || aMorada.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().validarFormatoNIF(aNIF)) {
			JOptionPane.showMessageDialog(this, "O NIF deve conter exatamente 9 números.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().validarFormatoContacto(aContacto)) {
			JOptionPane.showMessageDialog(this, "O contacto deve começar por 9, 2 ou 3 e ter 9 dígitos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			Integer.parseInt(aNIF);
			Integer.parseInt(aContacto); 
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this, "O NIF e o Contacto devem ser estritamente numéricos.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().verificarDisponibilidadeNIF(aNIF)) {
			JOptionPane.showMessageDialog(this, "Este NIF não está disponível.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Método responsável por validar os campos relativos aos funcionários, verificando se estão preenchidos e o formato corresponde ao esperado.
	 * @param aEspecializacao Especialização
	 * @param aDataInicio Data de Início de Atividade
	 * @return false caso alguma das validações dê errado, true caso contrário.
	 */
	private boolean validarCamposFuncionario(String aEspecializacao, String aDataInicio) {
		if (aEspecializacao.isEmpty() || aDataInicio.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().validarFormatoEspecializacao(aEspecializacao)) {
			JOptionPane.showMessageDialog(this, "A especialização deve ser um valor numérico.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!frame.getControladorMetodosComuns().validarFormatoData(aDataInicio)) {
			JOptionPane.showMessageDialog(this, "Formato de data inválido. Use o padrão Ano-Mês-Dia (Ex: 2026-03-28).", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Método responsável por validar os campos relativos aos funcionários, verificando se estão preenchidos e o formato corresponde ao esperado. 
	 * @param aSetor Setor de atividade
	 * @param aEscalao Escalão
	 * @return false caso alguma das validações dê errado, true caso contrário.
	 */
	private boolean validarCamposCliente(String aSetor, String aEscalao) {
		if (aSetor.isEmpty() || aEscalao.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!aEscalao.matches("^[A-D]$")) {
			JOptionPane.showMessageDialog(this, "O escalão deve ser apenas A, B, C ou D.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

}



