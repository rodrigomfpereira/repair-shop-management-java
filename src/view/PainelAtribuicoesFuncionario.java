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

import ap3.model.Notificacao;

/**
 * Painel que permite ao Funcionário aceitar ou rejeitar as atribuições de reparação enviadas pelo Gestor.
 * @author Rodrigo Pereira
 */
public class PainelAtribuicoesFuncionario extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaAtribuicoes;
	private JButton btnAceitar, btnRejeitar;
	private JLabel lblInfo;
	
	/**
	 * Construtor do Painel de Atribuições do Funcionário.
	 * @param aFrame Frame Principal
	 */
	public PainelAtribuicoesFuncionario(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Pedidos de Reparação Atribuídos a Aguardar a sua Resposta:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblInfo, BorderLayout.NORTH);

		atualizarTabelaAtribuicoes();
	}
	
	/**
	 * Atualiza a tabela com as reparações direcionadas ao funcionário autenticado.
	 */
	private void atualizarTabelaAtribuicoes() {
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Nº Reparação", "Data Criação", "Data Fim", "Tempo", "Custo", "Observações", "Equipamento"};

		int idFuncionarioAutenticado = frame.getIdUtilAutenticado();
		String[][] dadosMatriz = frame.getGestorReparacoes().obterMatrizReparacoesFuncionarioPorEstado(idFuncionarioAutenticado, 3);
		
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][7];
		}

		tabelaAtribuicoes = new JTable(dadosMatriz, colunas);
		tabelaAtribuicoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelaAtribuicoes.setRowSelectionAllowed(true);
		tabelaAtribuicoes.setColumnSelectionAllowed(false);
		
		JScrollPane scrollTabela = new JScrollPane(tabelaAtribuicoes);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnRejeitar = new JButton("Rejeitar Trabalho");
		btnRejeitar.addActionListener(this);
		btnRejeitar.setBackground(Color.red);
		btnRejeitar.setToolTipText("Clique para recusar esta reparação. O pedido voltará para o Gestor.");
		
		btnAceitar = new JButton("Aceitar Trabalho");
		btnAceitar.addActionListener(this);
		btnAceitar.setBackground(Color.green);
		btnAceitar.setToolTipText("Clique para aceitar a responsabilidade e iniciar a reparação.");

		painelBotoesAcao.add(btnRejeitar);
		painelBotoesAcao.add(btnAceitar);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			btnAceitar.setEnabled(false);
			btnRejeitar.setEnabled(false);
			lblInfo.setText("Não tem nenhuma atribuição de reparação pendente de momento.");
		} else {
			btnAceitar.setEnabled(true);
			btnRejeitar.setEnabled(true);
			lblInfo.setText("Pedidos de Reparação Atribuídos a Aguardar a sua Resposta:");
		}

		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		int linhaSelecionada = tabelaAtribuicoes.getSelectedRow();

		if (e.getSource().equals(btnAceitar) || e.getSource().equals(btnRejeitar)) {
			if (linhaSelecionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, selecione uma reparação na tabela.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String numReparacao = (String) tabelaAtribuicoes.getValueAt(linhaSelecionada, 0);
			Integer idReparacao = frame.getGestorReparacoes().procurarIDPorNumeroReparacao(numReparacao);
			int idFuncionario = frame.getIdUtilAutenticado();

			if (e.getSource().equals(btnAceitar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Confirma que ACEITA ficar responsável pela reparação nº " + numReparacao + "?", "Confirmar Aceitação", JOptionPane.YES_NO_OPTION);
				if (confirmacao == JOptionPane.YES_OPTION) {
					
					if (frame.getGestorReparacoes().aceitarPedidoReparacao(idReparacao, idFuncionario, numReparacao)) {
						frame.getGestorAcoes().registarAcao("aceitou realizar a reparação", idFuncionario);
						JOptionPane.showMessageDialog(this, "Ficou oficialmente responsável pela reparação nº " + numReparacao + ".", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaAtribuicoes();
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao processar a aceitação da reparação.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			if (e.getSource().equals(btnRejeitar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja REJEITAR a reparação nº " + numReparacao + "?", "Confirmar Rejeição", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirmacao == JOptionPane.YES_OPTION) {
					
					if (frame.getGestorReparacoes().negarPedidoReparacao(idReparacao, idFuncionario)) {

						frame.getGestorReparacoes().alterarEstado(numReparacao, 1);
						
						frame.getGestorAcoes().registarAcao("rejeitou realizar a reparação", idFuncionario);
						
						Notificacao notif = new Notificacao(6, null, frame.getGestorReparacoes().devolverReparacao(numReparacao));
						frame.getGestorNotificacoes().registarNotificacao(notif, null, idReparacao, idFuncionario);
						
						JOptionPane.showMessageDialog(this, "Pedido rejeitado com sucesso. A reparação foi devolvida ao painel do Gestor.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaAtribuicoes();
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao processar a rejeição do pedido.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
}