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

/**
 * Painel que permite apresentar o Painel de decisão (rejeição / aceitação) dos pedidos de remoção de conta aos Gestores da aplicação.
 * @author Rodrigo Pereira
 */
public class PainelDecisaoRemocoes extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaRemocoesPendentes;
	private JButton btnSubAprovar, btnSubRejeitar;
	private JLabel lblInfo;
	
	/**
	 * Construtor do Painel de Decisão de Remoções.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelDecisaoRemocoes(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Lista de Utilizadores com Pedido de Remoção Pendente:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Colocar um espaço para não ficar colado à tabela
		add(lblInfo, BorderLayout.NORTH);

		atualizarTabelaRemocoes();
	}
	
	/**
	 * Método que permite atualizar a tabela com os utilizadores que têm pedido de remoção de conta pendentes (Estado 3).
	 * Isto permite, após uma decisão ser tomada, a tabela ser atualizada e apresentar ao gestor o resultado.
	 */
	private void atualizarTabelaRemocoes() {
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Username", "Nome", "Tipo de Conta"};

		String[][] dadosMatriz = frame.getGestorUtilizadores().obterMatrizUtilizadoresPorEstado(3);
		
		//Caso a base de dados devolva nulo
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][3];
		}



		tabelaRemocoesPendentes = new JTable(dadosMatriz, colunas);
		tabelaRemocoesPendentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Seleção de apenas 1 linha
		tabelaRemocoesPendentes.setRowSelectionAllowed(true);
		tabelaRemocoesPendentes.setColumnSelectionAllowed(false);
		
		JScrollPane scrollTabela = new JScrollPane(tabelaRemocoesPendentes);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnSubRejeitar = new JButton("Rejeitar Remoção");
		btnSubRejeitar.addActionListener(this);
		btnSubRejeitar.setBackground(Color.red);
		btnSubRejeitar.setToolTipText("Clique aqui para rejeitar o pedido de remoção e reativar a conta selecionada.");
		
		btnSubAprovar = new JButton("Aprovar Remoção");
		btnSubAprovar.addActionListener(this);
		btnSubAprovar.setBackground(Color.green);
		btnSubAprovar.setToolTipText("Clique aqui para aprovar e apagar definitivamente os dados do utilizador selecionado.");

		painelBotoesAcao.add(btnSubRejeitar);
		painelBotoesAcao.add(btnSubAprovar);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			tabelaRemocoesPendentes.setEnabled(false);
			btnSubAprovar.setEnabled(false);
			btnSubRejeitar.setEnabled(false);
			lblInfo.setText("Não existem pedidos de remoção de conta pendentes de momento.");
		} else {
			lblInfo.setText("Lista de Utilizadores com Pedido de Remoção Pendente:");
		}

		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		int linhaSelecionada = tabelaRemocoesPendentes.getSelectedRow();

		if (e.getSource().equals(btnSubAprovar) || e.getSource().equals(btnSubRejeitar)) {
			if (linhaSelecionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, selecione um utilizador na tabela.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String usernameSelecionado = (String) tabelaRemocoesPendentes.getValueAt(linhaSelecionada, 0);

			if (e.getSource().equals(btnSubAprovar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, 
					"Tem a certeza que deseja APROVAR a remoção de dados de '" + usernameSelecionado + "'?\nEsta conta será permanentemente apagada.", 
					"Confirmar Eliminação Crítica", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (aprovarRemocao(usernameSelecionado)) {
						JOptionPane.showMessageDialog(this, "Conta com username '" + usernameSelecionado + "' removida com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaRemocoes(); 
					} else {
						JOptionPane.showMessageDialog(this, "Ocorreu um erro na remoção de conta, garanta que o utilizador efetuou o pedido de remoção.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			if (e.getSource().equals(btnSubRejeitar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, 
					"Tem a certeza que deseja REJEITAR a remoção de dados de '" + usernameSelecionado + "'?\nA conta voltará ao estado ativo.", 
					"Confirmar Reativação", JOptionPane.YES_NO_OPTION);
				
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (rejeitarRemocao(usernameSelecionado)) {
						JOptionPane.showMessageDialog(this, "Conta com username '" + usernameSelecionado + "' ativa com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaRemocoes(); 
					} else {
						JOptionPane.showMessageDialog(this, "Ocorreu um erro na ativação de conta, informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	/**
	 * Método responsável por permitir a aprovação do pedido de remoção de conta.
	 * @param aUsername Username da conta a remover
	 * @return true se removido com sucesso, false caso contrário
	 */
	public boolean aprovarRemocao(String aUsername) {
		if (aUsername == null || aUsername.trim().isEmpty()) {
			return false;
		}
		
		int idGestor = frame.getIdUtilAutenticado();
		
		if (frame.getGestorUtilizadores().anularConta(aUsername)) {
			frame.getGestorAcoes().registarAcao("aprovação de remoção de dados", idGestor);
			return true;
		}
		return false;
	}

	/**
	 * Método responsável por permitir a rejeição do pedido de remoção de conta.
	 * @param aUsername Username da conta a reativar
	 * @return true se o estado foi modificado com sucesso, false caso contrário
	 */
	public boolean rejeitarRemocao(String aUsername) {
		if (aUsername == null || aUsername.trim().isEmpty()) {
			return false;
		}
		
		int idGestor = frame.getIdUtilAutenticado();
		
		if (frame.getGestorUtilizadores().alterarEstadoConta(aUsername, 2)) {
			frame.getGestorAcoes().registarAcao("rejeição de remoção de dados", idGestor);
			return true;
		}
		return false;
	}
}