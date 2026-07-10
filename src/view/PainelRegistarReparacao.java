package ap3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ap3.model.Equipamento;
import ap3.model.Reparacao;
import ap3.model.Utilizador;

/**
 * Painel que apresenta o formulário para o Cliente registar uma nova reparação.
 * @author Rodrigo Pereira
 */
public class PainelRegistarReparacao extends JPanel implements ActionListener {

	private JanelaPrincipal frame;

	private JTextField txtCodigoSKU; 
	private JTextArea txtObservacoes; 
	private JButton btnGravar;

	/**
	 * Construtor do Painel de Registo de Reparação.
	 * @param aFrame Frame Principal
	 */
	public PainelRegistarReparacao(JanelaPrincipal aFrame) {
		this.frame = aFrame;
		setLayout(new BorderLayout());

		JLabel lblTitulo = new JLabel("Registar Nova Reparação", SwingConstants.LEFT);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblTitulo, BorderLayout.NORTH);

		JPanel painelFormulario = new JPanel();
		painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
		painelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dados do Pedido"));

		JPanel painelCamposNormais = new JPanel(new GridLayout(3, 2, 10, 10));
		painelCamposNormais.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		painelCamposNormais.add(new JLabel(""));painelCamposNormais.add(new JLabel("")); //para forçar o jtextfield a diminuir de tamanho
		painelCamposNormais.add(new JLabel("Código SKU do Equipamento * :"));
		txtCodigoSKU = new JTextField(); 
		txtCodigoSKU.setToolTipText("Insira o código numérico SKU do equipamento que pretende reparar.");
		
		painelCamposNormais.add(txtCodigoSKU);
		painelCamposNormais.add(new JLabel(""));painelCamposNormais.add(new JLabel("")); //para forçar o jtextfield a diminuir de tamanho
		JPanel painelObservacoes = new JPanel(new GridLayout(1, 2, 10, 10));
		painelObservacoes.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		painelObservacoes.add(new JLabel("Descrição da Avaria / Observações:"));
		txtObservacoes = new JTextArea(4, 20); 
		txtObservacoes.setLineWrap(true);
		txtObservacoes.setWrapStyleWord(true);
		txtObservacoes.setToolTipText("Descreva detalhadamente os problemas detetados no equipamento.");
		
		JScrollPane scrollObservacoes = new JScrollPane(txtObservacoes);
		painelObservacoes.add(scrollObservacoes);

		//Empilhar os dois sub-painéis independentes no BoxLayout
		painelFormulario.add(painelCamposNormais);
		painelFormulario.add(painelObservacoes);
		
		add(painelFormulario, BorderLayout.CENTER);

		JPanel painelBotoes = new JPanel();
		btnGravar = new JButton("Submeter Pedido de Reparação");
		btnGravar.setBackground(Color.GREEN); 
		btnGravar.setToolTipText("Clique aqui para validar os dados e submeter o pedido de reparação.");
		btnGravar.addActionListener(this);
		painelBotoes.add(btnGravar);
		add(painelBotoes, BorderLayout.SOUTH);

		verificarAcessoBaseDados();
	}

	/**
	 * Verifica se as tabelas da base de dados estão acessíveis.
	 */
	private void verificarAcessoBaseDados() {
		if (!frame.getGestorLigacoes().verificarAcessoTabela("equipamento") || 
				!frame.getGestorLigacoes().verificarAcessoTabela("reparacao") || 
				!frame.getGestorLigacoes().verificarAcessoTabela("notificacao") || 
				!frame.getGestorLigacoes().verificarAcessoTabela("acao")) {

			JOptionPane.showMessageDialog(this, "Não foi possível aceder à base de dados.\nPor favor, informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			txtCodigoSKU.setEnabled(false);
			txtObservacoes.setEnabled(false);
			btnGravar.setEnabled(false);
		}
	}

	/**
	 * Método responsável por tratar os eventos de clique no botão de gravar.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnGravar)) {
			String skuString = txtCodigoSKU.getText().trim();
			String obsString = txtObservacoes.getText().trim();

			if (skuString.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Por favor, introduza o código SKU do equipamento.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int codigoSKU;
			try {
				codigoSKU = Integer.parseInt(skuString);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "O código SKU tem de ser um valor numérico válido.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int idUtilAutenticado = frame.getIdUtilAutenticado();

			if (!frame.getGestorEquipamentos().verificarDonoEquipamento(codigoSKU, idUtilAutenticado)) {
				JOptionPane.showMessageDialog(this, "Garanta que inseriu o código correto e que tem permissões para aceder a esse equipamento.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Equipamento equipamento = frame.getGestorEquipamentos().devolverEquipamento(codigoSKU);
			int idEquipamento = frame.getGestorEquipamentos().devolverIDEquipamento(codigoSKU);

			if (equipamento == null || idEquipamento == -1) {
				JOptionPane.showMessageDialog(this, "Não foi possível encontrar os dados do equipamento com o código '" + codigoSKU + "'.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int confirmacao = JOptionPane.showConfirmDialog(this, 
					"Tem a certeza que pretende criar um pedido de reparação para o " + equipamento.getMarca() + " " + equipamento.getModelo() + " ?", 
					"Confirmar Pedido", JOptionPane.YES_NO_OPTION);

			if (confirmacao != JOptionPane.YES_OPTION) {
				return;
			}

			Utilizador util = frame.getGestorUtilizadores().devolverUtilizador(idUtilAutenticado);
			if (util == null) {
				JOptionPane.showMessageDialog(this, "Ocorreu um erro no acesso à base de dados.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String numReparacao = frame.getGestorReparacoes().gerarNumeroReparacao();
			Reparacao reparacao = new Reparacao(numReparacao, obsString);

			boolean sucesso = frame.getGestorReparacoes().registarReparacao(reparacao, idEquipamento, idUtilAutenticado);

			if (!sucesso) {
				JOptionPane.showMessageDialog(this, "Ocorreu um erro a registar a reparação.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			frame.getGestorAcoes().registarAcao("registo de reparação", idUtilAutenticado);

			JOptionPane.showMessageDialog(this, "Reparação registada com sucesso!", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
			txtCodigoSKU.setText("");
			txtObservacoes.setText("");
		}
	}
}