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

// Importação assumida da classe Notificacao baseada no seu método antigo
import ap3.model.Notificacao; 

/**
 * Painel que permite ao Funcionário gerir as reparações em curso da sua responsabilidade.
 * @author Rodrigo Pereira
 */
public class PainelGerirReparacoesFuncionario extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaReparacoesEmCurso;
	private JButton btnAlterarObs, btnAdicionarCusto, btnFinalizarReparacao;
	private JLabel lblInfo;

	/**
	 * Construtor do Painel de Gestão de Reparações do Funcionário.
	 * @param aFrame Frame Principal
	 */
	public PainelGerirReparacoesFuncionario(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());

		lblInfo = new JLabel("Reparações em Curso ao seu encargo:", SwingConstants.LEFT);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 13));
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblInfo, BorderLayout.NORTH);

		atualizarTabelaReparacoes();
	}

	/**
	 * Permite atualizar a tabela com as reparações a decorrer da responsabilidade do funcionário autenticado.
	 */
	private void atualizarTabelaReparacoes() {
		this.removeAll();
		add(lblInfo, BorderLayout.NORTH);

		String[] colunas = {"Nº Reparação", "Data Criação", "Data Fim", "Tempo", "Custo", "Observações", "Equipamento"};

		int idFuncionarioAutenticado = frame.getIdUtilAutenticado();

		String[][] dadosMatriz = frame.getGestorReparacoes().obterMatrizReparacoesFuncionarioPorEstado(idFuncionarioAutenticado, 4);

		if (dadosMatriz == null) {
			dadosMatriz = new String[0][7];
		}

		tabelaReparacoesEmCurso = new JTable(dadosMatriz, colunas);
		tabelaReparacoesEmCurso.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelaReparacoesEmCurso.setRowSelectionAllowed(true);
		tabelaReparacoesEmCurso.setColumnSelectionAllowed(false);

		JScrollPane scrollTabela = new JScrollPane(tabelaReparacoesEmCurso);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btnAlterarObs = new JButton("Alterar Observações");
		btnAlterarObs.addActionListener(this);
		btnAlterarObs.setToolTipText("Clique para atualizar as observações do diagnóstico desta reparação.");

		btnAdicionarCusto = new JButton("Incrementar Custo (€)");
		btnAdicionarCusto.addActionListener(this);
		btnAdicionarCusto.setToolTipText("Clique para somar um valor ao custo atual da reparação.");

		btnFinalizarReparacao = new JButton("Finalizar Reparação");
		btnFinalizarReparacao.addActionListener(this);
		btnFinalizarReparacao.setToolTipText("Clique para concluir definitivamente esta reparação (Muda para o Estado 5).");

		painelBotoesAcao.add(btnAlterarObs);
		painelBotoesAcao.add(btnAdicionarCusto);
		painelBotoesAcao.add(btnFinalizarReparacao);
		add(painelBotoesAcao, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			btnAlterarObs.setEnabled(false);
			btnAdicionarCusto.setEnabled(false);
			btnFinalizarReparacao.setEnabled(false);
			lblInfo.setText("Não tem nenhuma reparação a decorrer de momento.");
		} else {
			btnAlterarObs.setEnabled(true);
			btnAdicionarCusto.setEnabled(true);
			btnFinalizarReparacao.setEnabled(true);
			lblInfo.setText("Selecione uma reparação para alterar dados ou finalizar o processo:");
		}

		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		int linhaSelecionada = tabelaReparacoesEmCurso.getSelectedRow();

		if (linhaSelecionada == -1) {
			JOptionPane.showMessageDialog(this, "Por favor, selecione uma reparação na tabela para executar esta ação.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String numReparacao = (String) tabelaReparacoesEmCurso.getValueAt(linhaSelecionada, 0);
		Integer idReparacao = frame.getGestorReparacoes().procurarIDPorNumeroReparacao(numReparacao);
		int idFuncionario = frame.getIdUtilAutenticado();

		if (e.getSource().equals(btnAlterarObs)) {
			String obsAtual = (String) tabelaReparacoesEmCurso.getValueAt(linhaSelecionada, 5);
			if (obsAtual == null || obsAtual.equals("Sem observações.")) {
				obsAtual = ""; 
			}

			String novasObs = JOptionPane.showInputDialog(this, "Insira as novas observações para a reparação " + numReparacao + ":", obsAtual);

			if (novasObs != null) { 
				novasObs = novasObs.trim();
				if (frame.getGestorReparacoes().alterarCustoObservacoesReparacao(idReparacao, 0, novasObs)) {
					frame.getGestorAcoes().registarAcao("alterou dados de uma reparação", idFuncionario);
					JOptionPane.showMessageDialog(this, "Observações atualizadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
					atualizarTabelaReparacoes();
				} else {
					JOptionPane.showMessageDialog(this, "Erro ao atualizar as observações na Base de Dados.", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		if (e.getSource().equals(btnAdicionarCusto)) {
			String inputCusto = JOptionPane.showInputDialog(this, "Insira o valor do custo a ADICIONAR (será somado ao total atual):", "0.00");

			if (inputCusto != null && !inputCusto.trim().isEmpty()) {
				try {
					float custoAIncrementar = Float.parseFloat(inputCusto.trim().replace(',', '.')); 

					if (custoAIncrementar < 0) {
						JOptionPane.showMessageDialog(this, "Por favor, insira um valor de custo positivo.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (frame.getGestorReparacoes().alterarCustoReparacao(idReparacao, custoAIncrementar)) {
						frame.getGestorAcoes().registarAcao("alterou dados de uma reparação", idFuncionario);
						JOptionPane.showMessageDialog(this, "Custo incrementado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
						atualizarTabelaReparacoes();
					} else {
						JOptionPane.showMessageDialog(this, "Erro ao atualizar o custo na Base de Dados.", "Erro", JOptionPane.ERROR_MESSAGE);
					}

				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Formato numérico inválido. Introduza um número decimal (Ex: 12.50).", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		if (e.getSource().equals(btnFinalizarReparacao)) {
			if (idReparacao == null) {
				JOptionPane.showMessageDialog(this, "Reparação não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!frame.getGestorReparacoes().verificarResponsavelReparacao(idReparacao, idFuncionario)) {
				JOptionPane.showMessageDialog(this, "Garanta que tem permissões para aceder a essa reparação.", "Acesso Negado", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int estadoAtual = frame.getGestorReparacoes().devolverEstado(numReparacao); 
			if (estadoAtual != 4) {
				JOptionPane.showMessageDialog(this, "Esta reparação não se encontra a decorrer (Estado 4), a abortar.", "Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int resposta = JOptionPane.showConfirmDialog(
					this, 
					"Confirma que pretende finalizar a reparação com nº '" + numReparacao + "'?", 
					"Confirmar Finalização", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE
					);

			if (resposta != JOptionPane.YES_OPTION) {
				return; 
			}

			if (frame.getGestorReparacoes().finalizarReparacao(idReparacao)) {

				frame.getGestorAcoes().registarAcao("finalizou uma reparação", idFuncionario);

				try {
					Notificacao notif = new Notificacao(7, null, frame.getGestorReparacoes().devolverReparacao(numReparacao));

					int idCliente = frame.getGestorEquipamentos().devolverIDCliente(
							frame.getGestorEquipamentos().devolverSKUEq(
									frame.getGestorReparacoes().devolverIDEquipamento(numReparacao)
									)
							);

					if (idCliente != -1) {
						if (!frame.getGestorNotificacoes().registarNotificacao(notif, null, idReparacao, idCliente)) {
							JOptionPane.showMessageDialog(this, "Reparação finalizada, mas houve um erro ao enviar a notificação ao cliente.", "Aviso de Notificação", JOptionPane.WARNING_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(this, "Não foi possível registar a notificação: Cliente não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Reparação concluída, mas ocorreu uma falha no sistema de notificações.", "Aviso", JOptionPane.WARNING_MESSAGE);
				}

				JOptionPane.showMessageDialog(this, "A reparação nº '" + numReparacao + "' foi finalizada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				atualizarTabelaReparacoes();

			} else {
				JOptionPane.showMessageDialog(this, "Ocorreu um erro a finalizar a reparação na Base de Dados, por favor informe o administrador.", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}