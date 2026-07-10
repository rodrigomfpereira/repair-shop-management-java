package ap3.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/**
 * Painel permite aos Gestores listar todos os logs de ações ou pesquisar ações por nome (ou parte do nome) do utilizador.
 * @author Rodrigo Pereira
 */
public class PainelConsultasAcoes extends JPanel implements ActionListener {
	private JanelaPrincipal frame;
	private JTable tabelaAcoes;
	private JScrollPane scrollTabela;
	
	private JTextField txtPesquisaNome;
	private JButton btnPesquisar, btnListarTodos;
	private JLabel lblDica;
	
	/**
	 * Construtor do Painel de Consultas e Pesquisas de Ações.
	 * @param aFrame Frame Principal
	 */
	public PainelConsultasAcoes(JanelaPrincipal aFrame) {
		frame = aFrame;
		setLayout(new BorderLayout());
		
		JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		painelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros e Consultas de Histórico"));
		
		btnListarTodos = new JButton("Listar Todas as Ações");
		btnListarTodos.addActionListener(this);
		btnListarTodos.setToolTipText("Clique para ver o histórico completo de logs do sistema.");
		
		JLabel lblPesquisa = new JLabel(" Pesquisar por Utilizador:");
		txtPesquisaNome = new JTextField(15);
		txtPesquisaNome.setToolTipText("Introduza o nome ou parte do nome do utilizador.");
		
		btnPesquisar = new JButton("Pesquisar");
		btnPesquisar.addActionListener(this);
		
		painelFiltros.add(btnListarTodos);
		painelFiltros.add(lblPesquisa);
		painelFiltros.add(txtPesquisaNome);
		painelFiltros.add(btnPesquisar);
		
		add(painelFiltros, BorderLayout.NORTH);
		
		lblDica = new JLabel("Selecione uma das opções acima para carregar os dados das ações.", SwingConstants.CENTER);
		add(lblDica, BorderLayout.CENTER);
	}

	/**
	 * Método auxiliar que reconstrói a tabela dinamicamente com base na matriz recebida.
	 * @param aDadosMatriz Dados vindos do Gestor de Ações
	 * @param aMensagemVazio Mensagem a apresentar caso não existam registos
	 */
	private void preencherTabelaDados(String[][] aDadosMatriz, String aMensagemVazia) {
		//Remove o componente que estiver atualmente no centro
		if (scrollTabela != null) {
			this.remove(scrollTabela);
		}
		this.remove(lblDica);
		
		//Se não houver dados
		if (aDadosMatriz == null || aDadosMatriz.length == 0) {
			lblDica.setText(aMensagemVazia);
			add(lblDica, BorderLayout.CENTER);
		} else {
			String[] colunas = {"Descrição da Ação", "Data/Hora", "Utilizador"};
			
			tabelaAcoes = new JTable(aDadosMatriz, colunas);
			tabelaAcoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tabelaAcoes.setRowSelectionAllowed(true);
			tabelaAcoes.setColumnSelectionAllowed(false);
			tabelaAcoes.setAutoCreateRowSorter(true);
			
			scrollTabela = new JScrollPane(tabelaAcoes);
			add(scrollTabela, BorderLayout.CENTER);
		}
		
		this.revalidate();
		this.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(btnListarTodos)) {
			String[][] dados = frame.getGestorAcoes().obterMatrizTodasAcoes(); 
			
			preencherTabelaDados(dados, "Não existem ações registadas no histórico do sistema.");
			txtPesquisaNome.setText(""); //limpa o campo de pesquisas para evitar confusão
		}
		
		if (e.getSource().equals(btnPesquisar)) {
			String txtPesquisaFinal = txtPesquisaNome.getText().trim();
			
			if (txtPesquisaFinal.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Por favor, introduza um nome ou parte dele para efetuar a pesquisa.", "Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			String[][] dadosFiltrados = frame.getGestorAcoes().obterMatrizAcoesPorNomeUtilizador(txtPesquisaFinal);
			
			preencherTabelaDados(dadosFiltrados, "Nenhuma ação encontrada para o utilizador '" + txtPesquisaFinal + "'.");
		}
	}
}