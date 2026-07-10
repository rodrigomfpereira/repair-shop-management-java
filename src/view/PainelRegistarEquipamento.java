package ap3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

/**
 * Painel que permite apresentar o formulário para o Cliente registar um novo equipamento.
 * @author Rodrigo Pereira
 */
public class PainelRegistarEquipamento extends JPanel implements ActionListener {

	private JanelaPrincipal frame;
	
	private JTextField txtMarca, txtModelo, txtDataFabrico, txtLote;
	private JTextArea txtObservacoes;
	private JButton btnGravar;

	/**
	 * Construtor do Painel de Registo de Equipamento.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelRegistarEquipamento(JanelaPrincipal aFrame) {
		
		frame = aFrame;
		setLayout(new BorderLayout());

		JLabel lblTitulo = new JLabel("Registar Novo Equipamento", SwingConstants.LEFT);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(lblTitulo, BorderLayout.NORTH);

		JPanel painelFormulario = new JPanel();
		//boxlayout permite empilhar verticalmente os sub-painéis
		painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
		painelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dados do Equipamento"));

		JPanel painelCamposNormais = new JPanel(new GridLayout(4, 2, 10, 10)); //com margem entre componentes
		painelCamposNormais.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //margem externa para não colar ao textareas
		
		painelCamposNormais.add(new JLabel("Marca * :"));
		txtMarca = new JTextField();
		txtMarca.setToolTipText("Introduza a marca do fabricante do equipamento.");
		painelCamposNormais.add(txtMarca);

		painelCamposNormais.add(new JLabel("Modelo * :"));
		txtModelo = new JTextField();
		txtModelo.setToolTipText("Introduza o modelo específico do equipamento.");
		painelCamposNormais.add(txtModelo);

		painelCamposNormais.add(new JLabel("Data de Fabrico * (AAAA-MM-DD):"));
		txtDataFabrico = new JTextField();
		txtDataFabrico.setToolTipText("Introduza a data no formato Ano-Mês-Dia (ex: 2026-05-18).");
		painelCamposNormais.add(txtDataFabrico);

		painelCamposNormais.add(new JLabel("Lote * :"));
		txtLote = new JTextField();
		txtLote.setToolTipText("Introduza o código do lote de fabrico do equipamento.");
		painelCamposNormais.add(txtLote);


		JPanel painelObservacoes = new JPanel(new GridLayout(1, 2, 10, 10));

		painelObservacoes.add(new JLabel("Observações (Opcional):"));
		txtObservacoes = new JTextArea(4, 20); 
		txtObservacoes.setLineWrap(true);
		txtObservacoes.setWrapStyleWord(true);
		txtObservacoes.setToolTipText("Introduza detalhes adicionais ou observações acerca do equipamento.");
		
		//caso as observações sejam maiores que a jtextarea
		JScrollPane scrollObservacoes = new JScrollPane(txtObservacoes);
		painelObservacoes.add(scrollObservacoes);

		painelFormulario.add(painelCamposNormais);
		painelFormulario.add(painelObservacoes);
		
		add(painelFormulario, BorderLayout.CENTER);

		JPanel painelBotoes = new JPanel();
		btnGravar = new JButton("Submeter Equipamento");
		btnGravar.setBackground(Color.GREEN); 
		btnGravar.setToolTipText("Clique aqui para validar e registar o equipamento na sua conta.");
		btnGravar.addActionListener(this);
		painelBotoes.add(btnGravar);
		add(painelBotoes, BorderLayout.SOUTH);

		verificarRestricoesIniciais();
	}

	/**
	 * Método responsável por verificar se as tabelas relativas à inserção de equipamento estão acessíveis, e se ainda existem códigos SKU livres.
	 */
	private void verificarRestricoesIniciais() {
		if (!(frame.getGestorLigacoes().verificarAcessoTabela("equipamento") && frame.getGestorLigacoes().verificarAcessoTabela("acao"))) {
			JOptionPane.showMessageDialog(this, "Não foi possível aceder à base de dados.\nPor favor, informe o administrador.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			desativarFormulario();
			return;
		}

		if (frame.getGestorEquipamentos().StockEquipamentoCheio()) {
			JOptionPane.showMessageDialog(this, "Não é possível inserir mais equipamentos. Foi atingido o limite máximo.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
			desativarFormulario();
		}
	}

	/**
	 * Método que permite desabilitar a inserção de informação caso não seja possível aceder à base de dados ou os código SKUs tenham esgotado.
	 */
	private void desativarFormulario() {
		txtMarca.setEnabled(false);
		txtModelo.setEnabled(false);
		txtDataFabrico.setEnabled(false);
		txtLote.setEnabled(false);
		txtObservacoes.setEnabled(false);
		btnGravar.setEnabled(false);
	}

	/**
	 * Método responsável por tratar os eventos de clique no botão de gravar.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnGravar)) {
			
			String marca = txtMarca.getText().trim();
			String modelo = txtModelo.getText().trim();
			String dataString = txtDataFabrico.getText().trim();
			String lote = txtLote.getText().trim();
			String obs = txtObservacoes.getText().trim();

			if (marca.isEmpty() || modelo.isEmpty() || dataString.isEmpty() || lote.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios (*).", "Mensagme de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			LocalDate dataFabrico = null;
			try {
				dataFabrico = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException dtpe) {
				JOptionPane.showMessageDialog(this, "A data introduzida é inválida.\nUse o formato exato AAAA-MM-DD.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int codigoSKU = frame.getGestorEquipamentos().gerarCodigoSKU();
			Equipamento equipamento = new Equipamento(marca, modelo, codigoSKU, dataFabrico, lote, obs);

			int idUtilAutenticado = frame.getIdUtilAutenticado();
			boolean sucesso = frame.getGestorEquipamentos().registarEquipamento(equipamento, idUtilAutenticado);

			if (!sucesso) {
				JOptionPane.showMessageDialog(this, "Ocorreu um erro ao registar o equipamento na base de dados.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			frame.getGestorAcoes().registarAcao("submissão de equipamento", idUtilAutenticado);

			JOptionPane.showMessageDialog(this, "Equipamento registado com sucesso!\nCódigo SKU: " + codigoSKU, "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
			
			txtMarca.setText("");
			txtModelo.setText("");
			txtDataFabrico.setText("");
			txtLote.setText("");
			txtObservacoes.setText("");
		}
	}
}