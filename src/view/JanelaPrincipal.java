package ap3.view;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ap3.controller.ControladorMetodosComuns;
import ap3.model.GereAcoes;
import ap3.model.GereEquipamentos;
import ap3.model.GereFicheiroProperties;
import ap3.model.GereLigacoes;
import ap3.model.GereNotificacoes;
import ap3.model.GereReparacoes;
import ap3.model.GereUtilizadores;

/**
 * Janela principal da aplicação (JFrame) que serve como contentor, contém os gestores e gere as mudanças entre painéis.
 * @author Rodrigo Pereira
 */
public class JanelaPrincipal extends JFrame{
	private Container contentor;
	private String usernameUtilAutenticado = null;
	private String nomeUtilAutenticado = null;
	private String tipoContaUtilAutenticado = null;
	private Integer idUtilAutenticado = null;
	private GereFicheiroProperties gestorFicheiroProperties = new GereFicheiroProperties();
	private GereLigacoes gestorLigacoes = new GereLigacoes();
	private GereAcoes gestorAcoes = new GereAcoes();
	private GereNotificacoes gestorNotificacoes = new GereNotificacoes();
	private GereUtilizadores gestorUtilizadores = new GereUtilizadores();
	private GereReparacoes gestorReparacoes = new GereReparacoes();
	private GereEquipamentos gestorEquipamentos = new GereEquipamentos();
	private ControladorMetodosComuns controladorMetodosComuns;

	/**
	 * Construtor da JanelaPrincipal.
	 * Coloca o título na janela, o componente de encerramento e mostra o painel inicial.
	 */
	public JanelaPrincipal() {
		setTitle("Aplicação Java"); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		controladorMetodosComuns = new ControladorMetodosComuns(this);
		
		contentor = getContentPane(); 

		trocarPainel(new PainelInicial(this));
	}

	/**
	 * Método de entrada da aplicação.
	 * Responsável por instanciar a JFrame, definir as dimensões, centralizar a janela no monitor e mostrá-la.
	 * @param args Argumentos da linha de comandos (não utilizados).
	 */
	public static void main(String[] args) {
		JFrame frame = new JanelaPrincipal();
		frame.setSize (1024, 768); 
		frame.setLocationRelativeTo(null);
		frame.setVisible(true); 
	}

	/**
	 * Método que centraliza a troca de painéis.
	 * @param aNovoPainel Painel a colocar na JFrame.
	 */
	public void trocarPainel(JPanel aNovoPainel) {
		contentor.removeAll(); 
		contentor.add(aNovoPainel);
		contentor.revalidate();
		contentor.repaint();
	}

	/**
	 * Método responsável por verificar se o ficheiro de configuração já existe no sistema de ficheiros.
	 * Permite ao utilizador decidir se pretende criar o ficheiro com os parâmetros de acesso à base de dados, e mostra o painel correspondente à opção escolhida.
	 * @return 'true' se o ficheiro existir, 'false' caso contrário (para impedir que os botões funcionem).
	 */
	public boolean verificarEConfigurarProperties() {

		if (!gestorFicheiroProperties.existeFicheiro()) {

			int resposta = JOptionPane.showConfirmDialog(
					this,
					"Não existe ficheiro de acesso à base de dados.\nPara continuar terá que criar o ficheiro correspondente.\nDeseja continuar?",
					"Configuração Necessária",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
					);
			//se aceitou criar o ficheiro
			if (resposta == JOptionPane.YES_OPTION) {
				trocarPainel(new PainelParametros(this));

				//se não aceitou
			} else {
				trocarPainel(new PainelInicial(this));
			}

			return false; //para parar o evento que chamou o método
		}
		return true; //se ficheiro existe
	}

	/**
	 * Método que permite obter o ID do utilizador autenticado.
	 * @return O ID do utilizador autenticado.
	 */
	public Integer getIdUtilAutenticado() {
		return idUtilAutenticado;
	}

	/**
	 * Método que permite obter o username do utilizador autenticado.
	 * @return O username do utilizador autenticado.
	 */
	public String getUsernameUtilAutenticado() {
		return usernameUtilAutenticado;
	}

	/**
	 * Método que permite obter o nome do utilizador autenticado.
	 * @return O nome do utilizador autenticado.
	 */
	public String getNomeUtilAutenticado() {
		return nomeUtilAutenticado;
	}
	
	/**
	 * Método que permite obter o tipo de conta do utilizador autenticado.
	 * @return O tipo de conta do utilizador autenticado.
	 */
	public String getTipoContaUtilAutenticado() {
		return tipoContaUtilAutenticado;
	}

	/**
	 * Método que permite obter o gestor do ficheiro de propriedades, podendo assim manipular os parâmetros de acesso à base de dados a partir dos painéis.
	 * @return O gestor de ficheiro properties.
	 */
	public GereFicheiroProperties getGestorFicheiroProperties() {
		return gestorFicheiroProperties;
	}

	/**
	 * Método que permite obter o gestor de ações de forma a registar na base de dados as ações efetuadas pelos utilizadores a partir dos painéis.
	 * @return O gestor de ações.
	 */
	public GereAcoes getGestorAcoes() {
		return gestorAcoes;
	}

	/**
	 * Método que permite obter o gestor de notificações de forma a registar na base de dados as notificações geradas pelos utilizadores a partir dos painéis.
	 * @return O gestor de notificações.
	 */
	public GereNotificacoes getGestorNotificacoes() {
		return gestorNotificacoes;
	}

	/**
	 * Método que permite obter o gestor de ligações, podendo aceder e manipular a base de dados a partir dos painéis.
	 * @return O gestor de ações.
	 */
	public GereLigacoes getGestorLigacoes() {
		return gestorLigacoes;
	}

	/**
	 * Método que permite obter o gestor de utilizadores, podendo assim utilizar os respetivos métodos a partir dos painéis.
	 * @return O gestor de utilizadores.
	 */
	public GereUtilizadores getGestorUtilizadores() {
		return gestorUtilizadores;
	}

	/**
	 * Método que permite obter o gestor de reparações, podendo assim utilizar os respetivos métodos a partir dos painéis.
	 * @return O gestor de reparações.
	 */
	public GereReparacoes getGestorReparacoes() {
		return gestorReparacoes;
	}
	
	/**
	 * Método que permite obter o gestor de equipamentos, podendo assim utilizar os respetivos métodos a partir dos painéis.
	 * @return O gestor de equipamentos.
	 */
	public GereEquipamentos getGestorEquipamentos() {
		return gestorEquipamentos;
	}

	/**
	 * Método que permite obter o controlador de métodos comuns aos diferentes tipos de utilizadores, podendo assim reutilizar código para os diferentes painéis.
	 * @return O controlador de métodos comuns.
	 */
	public ControladorMetodosComuns getControladorMetodosComuns() {
	    return controladorMetodosComuns;
	}
	
	/**
	 * Método responsável por permitir atualizar o Id do utilizador que está autenticado.
	 * @param aIdUtilAutenticado ID do utilizador autenticado
	 */
	public void setIdUtilAutenticado(Integer aIdUtilAutenticado) {
		idUtilAutenticado = aIdUtilAutenticado;
	}

	/**
	 * Método responsável por permitir atualizar o username do utilizador que está autenticado.
	 * @param aUsernameUtilAutenticado Username do utilizador autenticado
	 */
	public void setUsernameUtilAutenticado(String aUsernameUtilAutenticado) {
		usernameUtilAutenticado = aUsernameUtilAutenticado;
	}

	/**
	 * Método responsável por permitir atualizar o nome do utilizador que está autenticado.
	 * @param aNomeUtilAutenticado Nome do utilizador autenticado
	 */
	public void setNomeUtilAutenticado(String aNomeUtilAutenticado) {
		nomeUtilAutenticado = aNomeUtilAutenticado;
	}
	
	/**
	 * Método responsável por permitir atualizar o tipo de conta do utilizador que está autenticado.
	 * @param aTipoContaUtilAutenticado Tipo de conta do utilizador autenticado
	 */
	public void setTipoContaUtilAutenticado(String aTipoContaUtilAutenticado) {
		tipoContaUtilAutenticado = aTipoContaUtilAutenticado;
	}
}