package ap3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import ap3.model.Cliente;
import ap3.model.Equipamento;
import ap3.model.Notificacao;
import ap3.model.Reparacao;

/**
 * Painel que permite apresentar o Painel de decisão (rejeição / aceitação) dos pedidos de reparação aos Gestores da aplicação.
 * @author Rodrigo Pereira
 */
public class PainelDecisaoReparacoes extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaReparacoesPendentes;
	private JButton btnSubAprovar, btnSubRejeitar;
	private JLabel lblInfo;
	
	/**
	 * Construtor do Painel de Decisão de Reparações.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelDecisaoReparacoes(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Lista de Pedidos de Reparação Pendentes de Avaliação:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblInfo, BorderLayout.NORTH);

		// Constrói a tabela com os dados atuais da Base de Dados
		atualizarTabelaReparacoes();
	}
	
	/**
	 * Método que permite atualizar a tabela com as reparações em estado 'Criada' (Estado 1).
	 */
	private void atualizarTabelaReparacoes() {
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Nº Reparação", "Data Criação", "Data Fim", "Tempo", "Custo", "Observações", "Equipamento"};

		String[][] dadosMatriz = frame.getGestorReparacoes().obterMatrizReparacoesPorEstado(1);
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][7];
		}

		tabelaReparacoesPendentes = new JTable(dadosMatriz, colunas);
		tabelaReparacoesPendentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelaReparacoesPendentes.setRowSelectionAllowed(true);
		tabelaReparacoesPendentes.setColumnSelectionAllowed(false);
		
		JScrollPane scrollTabela = new JScrollPane(tabelaReparacoesPendentes);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnSubRejeitar = new JButton("Rejeitar Pedido");
		btnSubRejeitar.addActionListener(this);
		btnSubRejeitar.setBackground(Color.red);
		btnSubRejeitar.setToolTipText("Clique aqui para negar e rejeitar o pedido de reparação selecionado.");
		
		btnSubAprovar = new JButton("Aceitar Pedido");
		btnSubAprovar.addActionListener(this);
		btnSubAprovar.setBackground(Color.green);
		btnSubAprovar.setToolTipText("Clique aqui para aceitar o pedido e atribuir um funcionário responsável.");

		painelBotoesAcao.add(btnSubRejeitar);
		painelBotoesAcao.add(btnSubAprovar);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			tabelaReparacoesPendentes.setEnabled(false);
			btnSubAprovar.setEnabled(false);
			btnSubRejeitar.setEnabled(false);
			lblInfo.setText("Não existem pedidos de reparação pendentes de momento.");
		} else {
			lblInfo.setText("Lista de Pedidos de Reparação Pendentes de Avaliação:");
		}

		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		int linhaSelecionada = tabelaReparacoesPendentes.getSelectedRow();

		if (e.getSource().equals(btnSubAprovar) || e.getSource().equals(btnSubRejeitar)) {
			if (linhaSelecionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, selecione um pedido de reparação na tabela.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// O número da reparação encontra-se na primeira coluna (índice 0)
			String numReparacaoSelecionado = (String) tabelaReparacoesPendentes.getValueAt(linhaSelecionada, 0);

			if (e.getSource().equals(btnSubAprovar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja aceitar o pedido de reparação nº '" + numReparacaoSelecionado + "'?", "Confirmar Aceitação", JOptionPane.YES_NO_OPTION);
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (processarAceitacaoReparacao(numReparacaoSelecionado)) {
						atualizarTabelaReparacoes();
					}
				}
			}

			if (e.getSource().equals(btnSubRejeitar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja NEGAR o pedido de reparação nº '" + numReparacaoSelecionado + "'?", "Confirmar Rejeição", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (negarReparacaoGestor(numReparacaoSelecionado)) {
						JOptionPane.showMessageDialog(this, "Pedido de reparação negado com sucesso e cliente notificado.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaReparacoes();
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao negar o pedido de reparação.\nPor favor informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	/**
	 * Método intermédio para gerir a validação do funcionário, alterar o estado da reparação e gerar a notificação / ação.
	 */
	private boolean processarAceitacaoReparacao(String aNumReparacao) {
		if (frame.getGestorLigacoes().isTabelaVazia("funcionario")) {
			JOptionPane.showMessageDialog(this, "Não existem funcionários registados no sistema.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		String usernameFunc = JOptionPane.showInputDialog(this, "Insira o username do funcionário a responsabilizar:", "Atribuir Funcionário", JOptionPane.QUESTION_MESSAGE);
		
		if (usernameFunc == null || usernameFunc.trim().isEmpty()) {
			return false; 
		}
		usernameFunc = usernameFunc.trim();

		String tipoUtilizador = frame.getGestorUtilizadores().procurarTipoPorUsername(usernameFunc);
		if (tipoUtilizador == null || !tipoUtilizador.equalsIgnoreCase("funcionario")) {
			JOptionPane.showMessageDialog(this, "O utilizador '" + usernameFunc + "' não é um funcionário válido.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Integer idReparacao = frame.getGestorReparacoes().procurarIDPorNumeroReparacao(aNumReparacao);
		Integer idFuncionario = frame.getGestorUtilizadores().procurarIDPorUsername(usernameFunc);

		int respostaAnterior = frame.getGestorReparacoes().verificarRespostaFuncionario(idReparacao, idFuncionario);
		if (respostaAnterior == 1) {
			JOptionPane.showMessageDialog(this, "O funcionário '" + usernameFunc + "' já negou este pedido anteriormente.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (respostaAnterior == 0) {
			JOptionPane.showMessageDialog(this, "Este pedido já foi enviado ao funcionário '" + usernameFunc + "' e aguarda resposta.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		if (frame.getGestorReparacoes().atribuirFuncionarioReparacao(idReparacao, idFuncionario)) {
			if (frame.getGestorReparacoes().alterarEstado(aNumReparacao, 3)) {
				
				Notificacao notif = new Notificacao(5, frame.getGestorUtilizadores().devolverFuncionario(idFuncionario), frame.getGestorReparacoes().devolverReparacao(aNumReparacao));
				
				if (frame.getGestorNotificacoes().registarNotificacao(notif, null, idReparacao, idFuncionario)) {
					frame.getGestorAcoes().registarAcao("atribuiu funcionário a reparação", frame.getIdUtilAutenticado());
					JOptionPane.showMessageDialog(this, "Reparação nº '" + aNumReparacao + "' aceite. Notificação enviada a " + usernameFunc + ".", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(this, "Atribuição concluída, mas falhou o registo da notificação.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
					return true;
				}
			}
		}
		JOptionPane.showMessageDialog(this, "Erro ao vincular o funcionário à reparação na Base de Dados.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * Método que permite negar a reparação (alterar para Estado 2) e notificar o Cliente dono do equipamento.
	 */
	public boolean negarReparacaoGestor(String aNumReparacao) {
		Integer idReparacao = frame.getGestorReparacoes().procurarIDPorNumeroReparacao(aNumReparacao);
		int idEquipamento = frame.getGestorReparacoes().devolverIDEquipamento(aNumReparacao);
		
		if (idReparacao == null || idEquipamento == -1) {
			return false;
		}

		int sku = frame.getGestorEquipamentos().devolverSKUEq(idEquipamento);
		Equipamento equipamento = frame.getGestorEquipamentos().devolverEquipamento(sku);
		
		if (equipamento == null) {
			return false;
		}

		int idCliente = frame.getGestorEquipamentos().devolverIDCliente(equipamento.getCodigoSKU());

		if (frame.getGestorReparacoes().alterarEstado(aNumReparacao, 2)) {
			Cliente cliente = frame.getGestorUtilizadores().devolverCliente(idCliente);
			Reparacao reparacaoObj = frame.getGestorReparacoes().devolverReparacao(aNumReparacao);

			if (cliente != null && reparacaoObj != null) {
				Notificacao notif = new Notificacao(4, cliente, reparacaoObj);
				frame.getGestorNotificacoes().registarNotificacao(notif, null, idReparacao, idCliente);
			}
			frame.getGestorAcoes().registarAcao("negou pedido de reparação", frame.getIdUtilAutenticado());
			return true;
		}
		return false;
	}
}