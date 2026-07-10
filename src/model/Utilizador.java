package ap3.model;


/**
 * Super classe que representa os utilizadores de forma geral na aplicação. 
 * @author RodrigoPereira
 */
public class Utilizador {
	protected String nome;
	protected String username;
	protected String password;
	protected int estado; //0 - por ativar | 1 - rejeitado | 2 - ativa | 3 - por apagar | 4 - apagada
	protected String email;
	protected String tipo; //gestor, cliente ou funcionário
	protected String foto;
	
	/**
	 * Instancia um utilizador recebendo todos os atributos definidos na classe, excepto a foto.
	 * @param aNome - Nome do utilizador
	 * @param aUsername - Username do utilizador
	 * @param aPassword - Password do utilizador
	 * @param aEstado - Estado da conta
	 * @param aEmail - Email do utilizador
	 * @param aTipo - Tipo de conta
	 */
	public Utilizador (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo) {
		nome = aNome;
		username = aUsername;
		password = aPassword;
		estado = aEstado;
		email = aEmail;
		tipo = aTipo;
		foto = null;
	}
	
	/**
	 * Instancia um utilizador recebendo todos os atributos definidos na classe.
	 * @param aNome - Nome do utilizador
	 * @param aUsername - Username do utilizador
	 * @param aPassword - Password do utilizador
	 * @param aEstado - Estado da conta
	 * @param aEmail - Email do utilizador
	 * @param aTipo - Tipo de conta
	 * @param aFoto - Foto de perfil
	 */
	public Utilizador (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo, String aFoto) {
		nome = aNome;
		username = aUsername;
		password = aPassword;
		estado = aEstado;
		email = aEmail;
		tipo = aTipo;
		foto = aFoto;
	}
	
	/**
	 * Método responsável por devolver o nome do Utilizador.
	 * @return a String com o nome do utilizador.
	 */
	public String getNome() {
		return nome;
	}
	
	/**
	 * Método responsável por devolver o username do Utilizador.
	 * @return a String com o username do utilizador.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Método responsável por devolver a password do Utilizador.
	 * @return a String com a password do utilizador.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Método responsável por devolver o estado da conta do Utilizador.
	 * @return o nº inteiro com o estado da conta do utilizador.
	 */
	public int getEstado() {
		return estado;
	}
	
	/**
	 * Método responsável por devolver o email do Utilizador.
	 * @return a String com o email do utilizador.
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Método responsável por devolver o tipo do Utilizador.
	 * @return a String com o tipo de conta do utilizador.
	 */
	public String getTipo() {
		return tipo;
	}
	
	/**
	 * Método responsável por devolver a foto de perfil do Utilizador.
	 * @return a String com a foto de perfil do utilizador.
	 */
	public String getFoto() {
		return foto;
	}
	
	/**
	 * Método responsável por atualizar o nome do Utilizador.
	 * @param aNome - nome a introduzir no atributo correspondente.
	 */
	public void setNome(String aNome) {
		nome = aNome;
	}
	
	/**
	 * Método responsável por atualizar o username do Utilizador.
	 * @param aUsername - username a introduzir no atributo correspondente.
	 */
	public void setUsername(String aUsername) {
		username = aUsername;
	}
	
	/**
	 * Método responsável por atualizar a password do Utilizador.
	 * @param aPassword - password a introduzir no atributo correspondente.
	 */
	public void setPassword(String aPassword) {
		password = aPassword;
	}
	
	/**
	 * Método responsável por atualizar o email do Utilizador.
	 * @param aEmail - email a introduzir no atributo correspondente.
	 */
	public void setEmail(String aEmail) {
		email = aEmail;
	}
	
	/**
	 * Método responsável por atualizar a foto de perfil do Utilizador.
	 * @param aFoto - foto a introduzir no atributo correspondente.
	 */
	public void setFoto(String aFoto) {
		foto = aFoto;
	}
	
}