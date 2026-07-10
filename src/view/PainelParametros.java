package ap3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/**
 * Painel que permite criar / substituir o ficheiro properties que contém os parâmetros de acesso à base de dados.
 * @author Rodrigo Pereira
 */
public class PainelParametros extends JPanel implements ActionListener{
	private JanelaPrincipal frame;
	private JTextField textoIP, textoPorto, textoNomeDB, textoUsername; 
	private JPasswordField textoPass;
	private JButton btnGuardar, btnCancelar;

	/**
	 * Construtor do Painel de Parâmetros.
	 * Organiza todos os componentes e mostra-os no painel.
	 * @param aFrame Frame Principal
	 */
	public PainelParametros(JanelaPrincipal aFrame) {
		setLayout(new BorderLayout());

		frame = aFrame; 

		JLabel lblTitulo = new JLabel("Configuração de Parâmetros de Acesso à Base de Dados", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

		Dimension dimLabel = new Dimension(110, 25);


		//IP
		JPanel painelIP = new JPanel(new FlowLayout());
		JLabel lblIP = new JLabel("IP:", SwingConstants.RIGHT);
		lblIP.setPreferredSize(dimLabel);
		textoIP = new JTextField(15); 
		textoIP.setToolTipText("IP do Servidor (e.g. localhost)");
		painelIP.add(lblIP);
		painelIP.add(textoIP);

		//Porto
		JPanel painelPorto = new JPanel(new FlowLayout());
		JLabel lblPorto = new JLabel("Porto:", SwingConstants.RIGHT);
		lblPorto.setPreferredSize(dimLabel); 
		textoPorto = new JTextField(15); 
		textoPorto.setToolTipText("Porto (e.g. 3306)");
		painelPorto.add(lblPorto);
		painelPorto.add(textoPorto);

		//NomeDB
		JPanel painelNomeDB = new JPanel(new FlowLayout());
		JLabel lblNomeDB = new JLabel("Nome da BD:", SwingConstants.RIGHT);
		lblNomeDB.setPreferredSize(dimLabel); 
		textoNomeDB = new JTextField(15); 
		textoNomeDB.setToolTipText("Nome da Base de Dados (e.g. db_pa_ap3)");
		painelNomeDB.add(lblNomeDB);
		painelNomeDB.add(textoNomeDB);

		//Username
		JPanel painelUsername = new JPanel(new FlowLayout());
		JLabel lblUsername = new JLabel("User:", SwingConstants.RIGHT);
		lblUsername.setPreferredSize(dimLabel); 
		textoUsername = new JTextField(15); 
		textoUsername.setToolTipText("Utilizador (e.g. root)");
		painelUsername.add(lblUsername);
		painelUsername.add(textoUsername);

		//Password
		JPanel painelPass = new JPanel(new FlowLayout());
		JLabel lblPass = new JLabel("Password:", SwingConstants.RIGHT);
		lblPass.setPreferredSize(dimLabel); 
		textoPass = new JPasswordField(15); 
		textoPass.setToolTipText("Password de acesso à base de dados");
		painelPass.add(lblPass);
		painelPass.add(textoPass);

		//Adicionar ao painel que vai para o centro
		JPanel painelCentral = new JPanel(new GridLayout(6, 1));
		painelCentral.add(new JLabel(""));
		painelCentral.add(painelIP);
		painelCentral.add(painelPorto);
		painelCentral.add(painelNomeDB);
		painelCentral.add(painelUsername);
		painelCentral.add(painelPass);

		//botões que vão para o sul
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		btnCancelar.setToolTipText("Clique aqui para cancelar e voltar ao menu inicial.");
		btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(this);
		btnGuardar.setToolTipText("Clique aqui para guardar as configurações inseridas.");

		//Adicionar ao painel que vai para o sul
		JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
		painelSul.add(btnCancelar);
		painelSul.add(btnGuardar);


		//adicionar às áreas do BorderLayout
		add(lblTitulo, BorderLayout.NORTH);
		add(painelCentral, BorderLayout.CENTER);
		add(painelSul, BorderLayout.SOUTH);

	}

	/**
	 * Método responsável por tratar os eventos de clique nos botões.
	 */
	public void actionPerformed (ActionEvent e){

		if (e.getSource().equals( btnCancelar ))
			frame.trocarPainel(new PainelInicial(frame));

		if (e.getSource().equals( btnGuardar )) {
			//colocamos o trim para não ter problemas com espaços no início/fim
			String ipIntroduzido = textoIP.getText().trim();
			String portoIntroduzido = textoPorto.getText().trim();
			String nomeDBIntroduzido = textoNomeDB.getText().trim();
			String usernameIntroduzido = textoUsername.getText().trim();
			String passIntroduzida = new String(textoPass.getPassword());

			if (ipIntroduzido.length() == 0 || portoIntroduzido.length() == 0 || nomeDBIntroduzido.length() == 0 || usernameIntroduzido.length() == 0 || passIntroduzida.length() == 0) {
				JOptionPane.showMessageDialog( this, "Por favor, preencha todos os campos.", "Mensagem de Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}

			boolean sucesso = frame.getGestorFicheiroProperties().gravarDefinicoes(ipIntroduzido, portoIntroduzido, nomeDBIntroduzido, usernameIntroduzido, passIntroduzida);

			if (sucesso) {
				JOptionPane.showMessageDialog( this, "Os parâmetros foram gravados com sucesso.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
				frame.getGestorAcoes().registarAcao("escrita no ficheiro de propriedades", frame.getIdUtilAutenticado());
				frame.trocarPainel(new PainelInicial(frame));
			} else { 
				JOptionPane.showMessageDialog( this, "Não foi possível gravar os parâmetros da base de dados.", "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}



