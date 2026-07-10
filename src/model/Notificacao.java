package ap3.model;

/**
 * Classe que representa as notificações da aplicação. 
 * @author RodrigoPereira
 */
public class Notificacao {
	private int tipo; // 1 = novos registos | 2 = solicitação de remoção de dados | 3 = nova reparação | 4 = reparação negada por gestor | 5 = reparação atribuida a func | 6 = reparação negada por func | 7 = reparação finalizada por func | 8 = reparação mais de 10 dias sem ser finalizado
	private boolean estadoLeitura;
	private Utilizador utilizador;
	private Reparacao reparacao;
	
	
	/**
	 * Instancia uma notificação recebendo apenas um tipo e utilizador.
	 * Notificação destinada a novos pedidos de registo de utilizadores.
	 * @param aTipo - nº inteiro com o tipo da notificação.
	 * @param aUtilizador - objeto Utilizador a inserir na notificação.
	 */
	public Notificacao (int aTipo, Utilizador aUtilizador) {
		tipo = aTipo;
		estadoLeitura = false;
		utilizador = aUtilizador;
		reparacao = null;
	}
	
	/**
	 * Instancia uma notificação recebendo um tipo, utilizador e reparação.
	 * @param aTipo - nº inteiro com o tipo da notificação.
	 * @param aUtilizador - objeto Utilizador a inserir na notificação.
	 * @param aReparacao - objeto Reparação a inserir na notificação.
	 */
	public Notificacao (int aTipo, Utilizador aUtilizador, Reparacao aReparacao) {
		tipo = aTipo;
		estadoLeitura = false;
		utilizador = aUtilizador;
		reparacao = aReparacao;
	}
	
	/**
	 * Método responsável por devolver o tipo da Notificação.
	 * @return o nº inteiro com o tipo da notificação.
	 */
	public int getTipo() {
		return tipo;
	}
	
	/**
	 * Método responsável por devolver o estado de leitura da notificação.
	 * @return o boolean com o estado de leitura da notificação.
	 */
	public boolean getEstadoLeitura() {
		return estadoLeitura;
	}
	
	/**
	 * Método responsável por devolver o Utilizador presente na notificação.
	 * @return o objeto Utilizador na notificação.
	 */
	public Utilizador getUtilizador() {
		return utilizador;
	}
	
	/**
	 * Método responsável por devolver a Reparação na notificação.
	 * @return o objeto Reparacao na notificação.
	 */
	public Reparacao getReparacao() {
		return reparacao;
	}
}
