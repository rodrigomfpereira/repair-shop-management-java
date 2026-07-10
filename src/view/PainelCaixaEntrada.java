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
 * Painel que permite apresentar aos utilizadores a Caixa de Entrada de notificações.
 * @author Rodrigo Pereira
 */
public class PainelCaixaEntrada extends JPanel implements ActionListener {
	
	private JanelaPrincipal frame;
	private JTable tabelaNotificacoes;
	private JLabel lblTitulo;
	private JButton btnLimparNotificacoes;

	/**
	 * Construtor do Painel de Caixa de Entrada.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelCaixaEntrada(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		lblTitulo = new JLabel("Caixa de Entrada", SwingConstants.LEFT);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblTitulo, BorderLayout.NORTH);

		// Constrói a tabela com os dados iniciais
		carregarNotificacoes();
	}

	/**
	 * Método responsável por obter da base de dados e apresentar as notificações relevantes ao utilizador / tipo de conta, caso existam notificações por ler.
	 */
	private void carregarNotificacoes() {
		//Remove qualquer componente antigo do ecrã antes de redesenhar (essencial para o Refresh)
		this.removeAll();
		add(lblTitulo, BorderLayout.NORTH);

		// Cabeçalho simples
		String[] colunas = {"Mensagem"};
		String[][] dadosMatriz = null;

		String tipo = frame.getTipoContaUtilAutenticado(); 
		int idUtilizador = frame.getIdUtilAutenticado();

		// Chamar o método específico da classe GereNotificacoes conforme o tipo de conta
		if (tipo.equalsIgnoreCase("gestor")) {
			dadosMatriz = frame.getGestorNotificacoes().obterMatrizNotificacoesGestor();
		} else if (tipo.equalsIgnoreCase("cliente")) {
			dadosMatriz = frame.getGestorNotificacoes().obterMatrizNotificacoesCliente(idUtilizador);
		} else if (tipo.equalsIgnoreCase("funcionario")) {
			dadosMatriz = frame.getGestorNotificacoes().obterMatrizNotificacoesFuncionario(idUtilizador);
		}

		//Salvaguarda
		if (dadosMatriz == null) {
			dadosMatriz = new String[0][1];
		}

		tabelaNotificacoes = new JTable(dadosMatriz, colunas);
		tabelaNotificacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollTabela = new JScrollPane(tabelaNotificacoes);
		add(scrollTabela, BorderLayout.CENTER);

		JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnLimparNotificacoes = new JButton("Marcar Todas como Lidas");
		btnLimparNotificacoes.addActionListener(this);
		btnLimparNotificacoes.setToolTipText("Clique aqui para marcar todas as notificações como lidas.");
		painelBotoes.add(btnLimparNotificacoes);
		add(painelBotoes, BorderLayout.SOUTH);

		if (dadosMatriz.length == 0) {
			lblTitulo.setText("A sua Caixa de Entrada não tem novas notificações.");
			tabelaNotificacoes.setEnabled(false);
			btnLimparNotificacoes.setEnabled(false); //Se não há notificações, desativa o botão
		} else {
			lblTitulo.setText("Notificações Pendentes:");
			tabelaNotificacoes.setEnabled(true);
			btnLimparNotificacoes.setEnabled(true);
		}

		//Redesenha a interface
		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões, chamando métodos dependendo do botão do menu selecionado.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnLimparNotificacoes)) {
			
			//Pede uma confirmação ao utilizador
			int resposta = JOptionPane.showConfirmDialog(this, "Deseja marcar todas as notificações atuais como lidas?", "Confirmar", JOptionPane.YES_NO_OPTION);
			
			if (resposta == JOptionPane.YES_OPTION) {
				String tipo = frame.getTipoContaUtilAutenticado(); 
				int idUtilizador = frame.getIdUtilAutenticado();
				boolean sucesso = false;

				//Dispara o método correspondente baseado no tipo de utilizador autenticado
				if (tipo.equalsIgnoreCase("gestor")) {
					sucesso = frame.getGestorNotificacoes().limparNotificacoesGestor();
				} else if (tipo.equalsIgnoreCase("cliente")) {
					sucesso = frame.getGestorNotificacoes().limparNotificacoesCliente(idUtilizador);
				} else if (tipo.equalsIgnoreCase("funcionario")) {
					sucesso = frame.getGestorNotificacoes().limparNotificacoesFuncionario(idUtilizador);
				}

				if (sucesso) {
					JOptionPane.showMessageDialog(this, "Notificações limpas com sucesso!", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
					carregarNotificacoes(); //Recarrega o ecrã (que agora vai aparecer vazio)
				} else {
					JOptionPane.showMessageDialog(this, "Erro ao atualizar o estado das notificações.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}