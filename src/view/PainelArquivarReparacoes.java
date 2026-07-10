package ap3.view;

import java.awt.BorderLayout;
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
 * Painel que permite aos Gestores visualizarem as reparações finalizadas pelos funcionários e arquivá-las.
 * @author Rodrigo Pereira
 */
public class PainelArquivarReparacoes extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaReparacoesFinalizadas;
	private JButton btnArquivar;
	private JLabel lblInfo;
	
	/**
	 * Construtor do Painel de Arquivação de Reparações.
	 * @param aFrame Frame Principal
	 */
	public PainelArquivarReparacoes(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblInfo = new JLabel("Reparações Finalizadas prontas para Arquivo:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblInfo, BorderLayout.NORTH);

		atualizarTabelaReparacoes();
	}
	
	/**
	 * Atualiza a tabela com as reparações em estado 'Finalizada'.
	 */
	private void atualizarTabelaReparacoes() {
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Nº Reparação", "Data Criação", "Data Fim", "Tempo", "Custo", "Observações", "Equipamento"};

		String[][] dadosMatriz = frame.getGestorReparacoes().obterMatrizReparacoesPorEstado(5);
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][7];
		}

		tabelaReparacoesFinalizadas = new JTable(dadosMatriz, colunas);
		tabelaReparacoesFinalizadas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelaReparacoesFinalizadas.setRowSelectionAllowed(true);
		tabelaReparacoesFinalizadas.setColumnSelectionAllowed(false);
		
		JScrollPane scrollTabela = new JScrollPane(tabelaReparacoesFinalizadas);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnArquivar = new JButton("Arquivar Processo");
		btnArquivar.addActionListener(this);
		btnArquivar.setToolTipText("Clique para arquivar permanentemente a reparação selecionada.");

		painelBotoesAcao.add(btnArquivar);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			tabelaReparacoesFinalizadas.setEnabled(false);
			btnArquivar.setEnabled(false);
			lblInfo.setText("Não existem reparações finalizadas pendentes de arquivo.");
		} else {
			btnArquivar.setEnabled(true);
			lblInfo.setText("Selecione uma reparação concluída para proceder ao arquivo do processo:");
		}

		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique no botão de arquivação.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnArquivar)) {
			int linhaSelecionada = tabelaReparacoesFinalizadas.getSelectedRow();

			if (linhaSelecionada == -1) {
				JOptionPane.showMessageDialog(this, "Por favor, selecione uma reparação na tabela para poder arquivá-la.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String numReparacao = (String) tabelaReparacoesFinalizadas.getValueAt(linhaSelecionada, 0);
			Integer idReparacao = frame.getGestorReparacoes().procurarIDPorNumeroReparacao(numReparacao);
			int idGestorAutenticado = frame.getIdUtilAutenticado();

			if (idReparacao == null) {
				JOptionPane.showMessageDialog(this, "Reparação não encontrada.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int estadoAtual = frame.getGestorReparacoes().devolverEstado(numReparacao); 
			if (estadoAtual != 5) {
				JOptionPane.showMessageDialog(this, "Esta reparação não se encontra finalizada (Estado 5), a abortar.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int resposta = JOptionPane.showConfirmDialog(
				this, 
				"Confirma que pretende arquivar a reparação com nº '" + numReparacao + "'?", 
				"Confirmar Arquivação", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE
			);

			if (resposta != JOptionPane.YES_OPTION) {
				return; 
			}

			if (frame.getGestorReparacoes().alterarEstado(numReparacao, 6)) {
				frame.getGestorAcoes().registarAcao("arquivou uma reparação", idGestorAutenticado);
				
				JOptionPane.showMessageDialog(this, "A reparação nº '" + numReparacao + "' foi arquivada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				
				atualizarTabelaReparacoes();
			} else {
				JOptionPane.showMessageDialog(this, "Não foi possível arquivar a reparação na Base de Dados, por favor informe o administrador.", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}