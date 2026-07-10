package ap3.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ap3.model.Notificacao;
import ap3.model.Utilizador;
import ap3.view.JanelaPrincipal;
import ap3.view.PainelInicial;

/**
 * Classe que permite partilhar métodos de controlo de dados entre diferentes painéis, reutilizando assim o código.
 * @author Rodrigo Pereira
 */
public class ControladorMetodosComuns {
	private JanelaPrincipal frame;

	/**
	 * Construtor do Controlador de Métodos Comuns.
	 * Recebe  a janela principal e guarda no respetivo atributo.
	 * @param aFrame Frame Principal
	 */
	public ControladorMetodosComuns(JanelaPrincipal aFrame) {
		frame = aFrame;
	}

	/**
	 * Devolve o objeto Utilizador correto (ou uma das suas subclasses) com base no ID.
	 * @param aIdUtilizador ID do utilizador recebido
	 * @return
	 */
	public Utilizador obterDadosUtilizadorCompleto(int aIdUtilizador) {

		Utilizador base = frame.getGestorUtilizadores().devolverUtilizador(aIdUtilizador);
		if (base == null) return null;

		if ("cliente".equals(base.getTipo())) {
			return frame.getGestorUtilizadores().devolverCliente(aIdUtilizador);
		} else if ("funcionario".equals(base.getTipo())) {
			return frame.getGestorUtilizadores().devolverFuncionario(aIdUtilizador);
		}

		//Se for gestor, não tem tabela intermédia, devolvemos o objeto base
		return base;
	}

	/**
	 * Método que permite validar se o email recebido está no formato esperado.
	 * @param aEmail Email recebido
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoEmail(String aEmail) {
		return aEmail.matches("^[a-zA-Z0-9.->%+-]{4,}@[a-zA-Z0-9._%+-]{2,}\\.[a-zA-Z]{2,}$");
	}

	/**
	 * Método que permite verificar se o email recebido está a ser usado 
	 * @param aEmail Email recebido
	 * @return true caso o email esteja disponível, false caso contrário
	 */
	public boolean verificarDisponibilidadeEmail(String aEmail) {
		return !frame.getGestorUtilizadores().existeEmail(aEmail);
	}

	/**
	 * Método que permite verificar se o username recebido está a ser usado 
	 * @param aUsername Username recebido
	 * @return true caso o username esteja disponível, false caso contrário
	 */
	public boolean verificarDisponibilidadeUsername(String aUsername) {
		return !frame.getGestorUtilizadores().existeUsername(aUsername);
	}

	/**
	 * Método que permite validar se o NIF recebido está no formato esperado.
	 * @param aNIF NIF recebido
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoNIF(String aNIFString) {
		return aNIFString.matches("^[0-9]{9}$"); 
	}

	public boolean verificarDisponibilidadeNIF(String aNIFString) {
		try {
			int nifInt = Integer.parseInt(aNIFString);
			return !frame.getGestorUtilizadores().existeNIF(nifInt);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Método que permite validar se o NIF recebido está no formato esperado.
	 * @param aNIF NIF recebido
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoContacto(String aContactoString) {
		return aContactoString.matches("^[923][0-9]{8}$");
	}

	/**
	 * Método que permite validar se o escalão recebido está no formato esperado.
	 * @param aEscalao Escalão recebido
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoEscalao(String aEscalao) {
		return aEscalao.matches("^[A-D]$");
	}

	/**
	 * Método que permite validar se a data recebida está no formato esperado.
	 * @param aDataString Data recebida
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoData(String aDataString) {
	    try {
	        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate.parse(aDataString, formatador);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	/**
	 * Método que permite validar se a especialização recebida está no formato esperado.
	 * @param aEspecializacaoString Especialização recebida
	 * @return true caso o formato seja correto, false caso contrário.
	 */
	public boolean validarFormatoEspecializacao(String aEspecializacaoString) {
		try {
			Integer.parseInt(aEspecializacaoString);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	/**
	 * Método responsável por gerir o fluxo de solicitação de remoção de conta,
	 * alterando o estado para 3 e gerando a respetiva notificação aos gestores.
	 * * @param frame A JanelaPrincipal para aceder à sessão e gestores de dados.
	 * @param painelPai O JPanel que invocou o método (para centrar os diálogos).
	 */
	public void solicitarRemocaoConta(JPanel aPainelAtual) {
	    String usernameAtivo = frame.getUsernameUtilAutenticado();
	    int idAtivo = frame.getIdUtilAutenticado();

	    //Verifica se está pendente
	    if (frame.getGestorUtilizadores().devolverEstadoConta(usernameAtivo) == 3) {
	        JOptionPane.showMessageDialog(aPainelAtual, "O seu pedido de remoção de dados já se encontra pendente de aprovação.", "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }

	    //Pede confirmação
	    int resposta = JOptionPane.showConfirmDialog(aPainelAtual,"Tem a certeza que pretende solicitar a remoção dos seus dados?\nEsta ação terá de ser aprovada por um gestor.","Confirmar Remoção de Conta",
	        JOptionPane.YES_NO_OPTION,
	        JOptionPane.WARNING_MESSAGE);

	    if (resposta != JOptionPane.YES_OPTION) {
	        return;
	    }


	    Notificacao notif = new Notificacao(2, frame.getGestorUtilizadores().devolverUtilizador(idAtivo));

	    boolean estadoAlteradoOK = frame.getGestorUtilizadores().alterarEstadoConta(usernameAtivo, 3);
	    boolean notificacaoRegistadaOK = frame.getGestorNotificacoes().registarNotificacao(notif, null, null, idAtivo);

	    if (estadoAlteradoOK && notificacaoRegistadaOK) {
	        frame.getGestorAcoes().registarAcao("solicitação de remoção de conta", idAtivo);
	        
	        JOptionPane.showMessageDialog(aPainelAtual,
	            "O seu pedido de remoção de dados foi efetuado com sucesso.\nPor favor aguarde por uma decisão dos gestores.",
	            "Mensagem Informativa", JOptionPane.INFORMATION_MESSAGE);
	            
	        frame.trocarPainel(new PainelInicial(frame));
	    } else {
	        JOptionPane.showMessageDialog(aPainelAtual,
	            "Ocorreu um erro na solicitação de remoção de conta / envio da notificação aos gestores.\nPor favor informe o administrador.",
	            "Erro", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
}
