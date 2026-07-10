package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Painel que permite apresentar o menu com todas as opções disponíveis para os Gestores da aplicação.
 * @author Rodrigo Pereira
 *
 */
public class PainelMenuGestor extends JPanel implements ActionListener {
	
	private JanelaPrincipal frame;
	private JPanel painelCentroDinamico;

	private JButton btnCaixaEntrada, btnDecidirRegistos, btnDecidirRemocoes, btnDecidirReparacoes, btnArquivarReparacoes, btnConsultarAcoes, btnAlterarDados, btnTerminarSessao;

	/**
	 * Construtor do Painel de Registo.
	 * Organiza todos os componentes e coloca-os no contentor para serem visualizados.
	 * @param aFrame Frame Principal
	 */
	public PainelMenuGestor(JanelaPrincipal aFrame) {
		frame = aFrame;
		
		setLayout(new BorderLayout());

		
		JPanel painelNorte = new JPanel(new GridLayout(3, 1));
		JLabel lblTitulo = new JLabel("Painel de Administração (Gestor)", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
		painelNorte.add(new JLabel(""));
		painelNorte.add(lblTitulo);
		painelNorte.add(new JLabel(""));


		//menu lateral de opções (para ir para a esquerda)
		JPanel painelMenuEsquerdo = new JPanel(new GridLayout(13, 1, 5, 5)); //com espaçamento
		painelMenuEsquerdo.setBorder(BorderFactory.createTitledBorder("Menu")); //borda com título
		painelMenuEsquerdo.setPreferredSize(new Dimension(220, 0));

		btnCaixaEntrada = new JButton("Caixa de Entrada");
		btnCaixaEntrada.addActionListener(this);
		btnCaixaEntrada.setToolTipText("Clique aqui para verificar as suas notificações por ler.");

		btnDecidirRegistos = new JButton("Gerir Pedidos de Registo");
		btnDecidirRegistos.addActionListener(this);
		btnDecidirRegistos.setToolTipText("Clique aqui para gerir os pedidos de registo pendentes.");
		
		btnDecidirRemocoes = new JButton("Gerir Pedidos de Remoção");
		btnDecidirRemocoes.addActionListener(this);
		btnDecidirRemocoes.setToolTipText("Clique aqui para gerir os pedidos de remoção de conta pendentes.");
		
		btnDecidirReparacoes = new JButton("Gerir Pedidos de Reparação");
		btnDecidirReparacoes.addActionListener(this);
		btnDecidirReparacoes.setToolTipText("Clique aqui para gerir os pedidos de reparacão pendentes.");
		
		btnArquivarReparacoes = new JButton("Arquivar Reparações");
		btnArquivarReparacoes.addActionListener(this);
		btnArquivarReparacoes.setToolTipText("Clique aqui para arquivar os processos de reparacão finalizados.");
		
		btnConsultarAcoes = new JButton("Consultar Ações");
		btnConsultarAcoes.addActionListener(this);
		btnConsultarAcoes.setToolTipText("Clique aqui para consultar as ações efetuadas por todos os utilizadorea.");
		
		btnAlterarDados = new JButton("Alterar os Dados");
		btnAlterarDados.addActionListener(this);
		btnAlterarDados.setToolTipText("Clique aqui para modificar as informações do seu perfil, ou de outro utilizador.");

		btnTerminarSessao = new JButton("Terminar Sessão");
		btnTerminarSessao.addActionListener(this);
		btnTerminarSessao.setToolTipText("Clique aqui para terminar sessão e voltar ao menu inicial.");

		painelMenuEsquerdo.add(btnCaixaEntrada);
		painelMenuEsquerdo.add(btnDecidirRegistos);
		painelMenuEsquerdo.add(btnDecidirRemocoes);
		painelMenuEsquerdo.add(btnDecidirReparacoes);
		painelMenuEsquerdo.add(btnArquivarReparacoes);
		painelMenuEsquerdo.add(btnConsultarAcoes);
		painelMenuEsquerdo.add(btnAlterarDados);

		//espaçadores visuais
		for(int i=0; i<5; i++) {
			painelMenuEsquerdo.add(new JLabel(""));
		}
		painelMenuEsquerdo.add(btnTerminarSessao);

		//Painel centro que vai alterar de conteúdo
		painelCentroDinamico = new JPanel(new BorderLayout());
		painelCentroDinamico.setBorder(BorderFactory.createEtchedBorder()); //borda simples
		mostrarMensagemBoasVindas();

		//adicionar ao BorderLayout Principal
		add(painelNorte, BorderLayout.NORTH);
		add(painelMenuEsquerdo, BorderLayout.WEST);
		add(painelCentroDinamico, BorderLayout.CENTER);
	}

	/**
	 * Método que altera o painel de conteúdo dinâmico para o estado inicial (com dica sobre selecionar uma opção do menu)
	 */
	private void mostrarMensagemBoasVindas() {
		painelCentroDinamico.removeAll();
		JLabel lblDica = new JLabel("Selecione uma opção no menu lateral esquerdo para começar.", SwingConstants.CENTER);
		painelCentroDinamico.add(lblDica, BorderLayout.CENTER);
		painelCentroDinamico.revalidate();
		painelCentroDinamico.repaint();
	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões, chamando métodos dependendo do botão do menu selecionado.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnCaixaEntrada)) {
			//limpa e coloca o novo painel no centro
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelCaixaEntrada(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}

		if (e.getSource().equals(btnDecidirRegistos)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelDecisaoRegistos(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}
		
		if (e.getSource().equals(btnAlterarDados)) {
			painelCentroDinamico.removeAll();
			
			int resposta = JOptionPane.showConfirmDialog(this, 
		            "Pretende alterar os dados de outro utilizador?", 
		            "Alteração de dados", 
		            JOptionPane.YES_NO_OPTION);
		            
		        if (resposta == JOptionPane.YES_OPTION) {
		        	String usernameAlvo = JOptionPane.showInputDialog(this, 
		                    "Introduza o Username do utilizador que pretende editar:", 
		                    "Procurar Utilizador", 
		                    JOptionPane.QUESTION_MESSAGE);
		                    
		                //se o utilizador carregou em cancelar ou deixou vazio, interrompe
		                if (usernameAlvo == null || usernameAlvo.trim().isEmpty()) {
		                    mostrarMensagemBoasVindas(); 
		                    return;
		                }
		                
		                Integer idAlvo = frame.getGestorUtilizadores().procurarIDPorUsername(usernameAlvo.trim());
		                
		                if (idAlvo == null) { 
		                    JOptionPane.showMessageDialog(this, 
		                        "Utilizador não encontrado no sistema.", 
		                        "Mensagem de Erro", 
		                        JOptionPane.ERROR_MESSAGE);
		                    mostrarMensagemBoasVindas();
		                    return;
		                }
		                
		                painelCentroDinamico.add(new PainelAlterarDados(frame, idAlvo, true), BorderLayout.CENTER);
		        } else {
		        	painelCentroDinamico.add(new PainelAlterarDados(frame, frame.getIdUtilAutenticado(), false), BorderLayout.CENTER);
		        }
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}

		if (e.getSource().equals(btnDecidirRemocoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelDecisaoRemocoes(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}
		
		if (e.getSource().equals(btnDecidirReparacoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelDecisaoReparacoes(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}
		
		if (e.getSource().equals(btnArquivarReparacoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelArquivarReparacoes(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}
		
		if (e.getSource().equals(btnConsultarAcoes)) {
			painelCentroDinamico.removeAll();
			painelCentroDinamico.add(new PainelConsultasAcoes(frame), BorderLayout.CENTER);
			painelCentroDinamico.revalidate();
			painelCentroDinamico.repaint();
		}
		
		if (e.getSource().equals(btnTerminarSessao)) {
			frame.setIdUtilAutenticado(null);
			frame.setUsernameUtilAutenticado(null);
			frame.setNomeUtilAutenticado(null);
			frame.setTipoContaUtilAutenticado(null);
			frame.trocarPainel(new PainelInicial(frame));
		}
	}
}
