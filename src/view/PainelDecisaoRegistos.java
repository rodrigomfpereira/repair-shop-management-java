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
 * Painel que permite apresentar o Painel de decisão (rejeição / aceitação) dos pedidos de registo aos Gestores da aplicação.
 * @author Rodrigo Pereira
 */
public class PainelDecisaoRegistos extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaUtilizadoresPendentes;
	private JButton btnSubAprovar, btnSubRejeitar;
	private JLabel lblInfo;
	
	/**
	 * Construtor do Painel de Decisão de Registos.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelDecisaoRegistos(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Lista de Utilizadores com Registo Pendente de Aprovação:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); //colocar um espaço para não ficar colado à tabela
		add(lblInfo, BorderLayout.NORTH);

		// Constrói a tabela com os dados atuais da Base de Dados
		atualizarTabelaUtilizadores();
	}
	
	/**
	 * Método que permite atualizar a tabela com os utilizadores que têm pedido de registo de conta pendentes.
	 * Isto permite, após uma decisão ser tomada, a tabela ser atualizada e apresentar ao gestor o resultado.
	 */
	private void atualizarTabelaUtilizadores() {
		//remove a tabela antiga antes de redesenhar (para o Refresh)
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Username", "Nome", "Tipo de Conta"};

		String[][] dadosMatriz = frame.getGestorUtilizadores().obterMatrizUtilizadoresPorEstado(0);
		
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][3];
		}

		tabelaUtilizadoresPendentes = new JTable(dadosMatriz, colunas);
		tabelaUtilizadoresPendentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Seleção de apenas 1 linha
		tabelaUtilizadoresPendentes.setRowSelectionAllowed(true);
		tabelaUtilizadoresPendentes.setColumnSelectionAllowed(false);
		
		//coloca a tabela dentro do JScrollPane para ativar as barras de deslocamento
		JScrollPane scrollTabela = new JScrollPane(tabelaUtilizadoresPendentes);
		add(scrollTabela, BorderLayout.CENTER);

		//painel para o sul
		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnSubRejeitar = new JButton("Rejeitar Registo");
		btnSubRejeitar.addActionListener(this);
		btnSubRejeitar.setBackground(Color.red);
		btnSubRejeitar.setToolTipText("Clique aqui para rejeitar o registo do utilizador selecionado.");
		
		btnSubAprovar = new JButton("Aprovar Registo");
		btnSubAprovar.addActionListener(this);
		btnSubAprovar.setBackground(Color.green);
		btnSubAprovar.setToolTipText("Clique aqui para aprovar o registo do utilizador selecionado.");

		painelBotoesAcao.add(btnSubRejeitar);
		painelBotoesAcao.add(btnSubAprovar);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		//se não existirem utilizadores, desativa os controlos e dá feedback
		if (dadosMatriz.length == 0) {
			tabelaUtilizadoresPendentes.setEnabled(false);
			btnSubAprovar.setEnabled(false);
			btnSubRejeitar.setEnabled(false);
			lblInfo.setText("Não existem pedidos de registo pendentes de momento.");
		} else {
			lblInfo.setText("Lista de Utilizadores com Registo Pendente de Aprovação:");
		}

		// Avisa o Java Swing que o ecrã mudou e precisa de ser repintado
		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		// Descobre qual a linha selecionada na JTable
		int linhaSelecionada = tabelaUtilizadoresPendentes.getSelectedRow();

		if (e.getSource().equals(btnSubAprovar) || e.getSource().equals(btnSubRejeitar)) {
			// Validação simples: se clicou num botão sem escolher ninguém na tabela
			if (linhaSelecionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, selecione um utilizador na tabela.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Extrai o Username que está na primeira coluna (índice 0) da linha selecionada
			String usernameSelecionado = (String) tabelaUtilizadoresPendentes.getValueAt(linhaSelecionada, 0);

			if (e.getSource().equals(btnSubAprovar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja APROVAR o registo de '" + usernameSelecionado + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (aprovarRegisto(usernameSelecionado)) {
						JOptionPane.showMessageDialog(this, "Conta '" + usernameSelecionado + "' ativada com sucesso!", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaUtilizadores(); // Atualiza o ecrã recarregando a tabela
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao ativar a conta.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			if (e.getSource().equals(btnSubRejeitar)) {
				int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja REJEITAR o registo de '" + usernameSelecionado + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
				if (confirmacao == JOptionPane.YES_OPTION) {
					if (rejeitarRegisto(usernameSelecionado)) {
						JOptionPane.showMessageDialog(this, "Registo de '" + usernameSelecionado + "' rejeitado com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaUtilizadores(); // Atualiza o ecrã recarregando a tabela
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao rejeitar o registo.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	/**
	 * Método responsável por permitir aprovação de registo de conta.
	 * Aprova um registo de utilizador, alterando o estado para 2 e registando a ação.
	 */
	public boolean aprovarRegisto(String aUsername) {
		if (aUsername == null || aUsername.trim().isEmpty()) {
			return false;
		}
		
		int idGestor = frame.getIdUtilAutenticado();
		
		// Altera estado para 2 (Ativo)
		if (frame.getGestorUtilizadores().alterarEstadoConta(aUsername, 2)) {
			frame.getGestorAcoes().registarAcao("aprovação de pedido de registo", idGestor);
			return true;
		}
		return false;
	}

	/**
	 * Método responsável por permitir rejeição de registo de conta.
	 * Rejeita um registo de utilizador, alterando o estado para 1 e registando a ação.
	 */
	public boolean rejeitarRegisto(String aUsername) {
		if (aUsername == null || aUsername.trim().isEmpty()) {
			return false;
		}
		
		int idGestor = frame.getIdUtilAutenticado();
		
		if (frame.getGestorUtilizadores().alterarEstadoConta(aUsername, 1)) {
			frame.getGestorAcoes().registarAcao("rejeição de pedido de registo", idGestor);
			return true;
		}
		return false;
	}
}
