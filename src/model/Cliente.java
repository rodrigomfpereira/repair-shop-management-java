package ap3.model;


/**
 * Subclasse de utilizadores destinada a representar os clientes na aplicação.
 * @author RodrigoPereira
 */
public class Cliente extends Utilizador{
	private int NIF;
	private int contacto;
	private String morada;
	private String setorAtividade;
	private String escalao;
	
	/**
	 * Instancia um cliente recebendo todos os atributos definidos na classe, excepto a foto. 
	 * @param aNome - Nome do cliente
	 * @param aUsername - Username do cliente
	 * @param aPassword - Password do cliente
	 * @param aEstado - Estado da conta 
	 * @param aEmail - Email do cliente
	 * @param aTipo - Tipo de conta
	 * @param aNIF - NIF do cliente
	 * @param aContacto - Contacto do cliente
	 * @param aMorada - Morada do cliente
	 * @param aSetorAtividade - Setor em que o cliente trabalha
	 * @param aEscalao - Escalão do cliente
	 */
	public Cliente (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo, int aNIF, int aContacto, String aMorada, String aSetorAtividade, String aEscalao) {
		super(aNome, aUsername, aPassword, aEstado, aEmail, aTipo);
		NIF = aNIF;
		contacto = aContacto;
		morada = aMorada;
		setorAtividade = aSetorAtividade;
		escalao = aEscalao;
	}
	
	/**
	 * Instancia um cliente recebendo todos os atributos definidos na classe. 
	 * @param aNome - Nome do cliente
	 * @param aUsername - Username do cliente
	 * @param aPassword - Password do cliente
	 * @param aEstado - Estado da conta 
	 * @param aEmail - Email do cliente
	 * @param aTipo - Tipo de conta
	 * @param aFoto - Foto de perfil do cliente
	 * @param aNIF - NIF do cliente
	 * @param aContacto - Contacto do cliente
	 * @param aMorada - Morada do cliente
	 * @param aSetorAtividade - Setor em que o cliente trabalha
	 * @param aEscalao - Escalão do cliente
	 */
	public Cliente (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo, String aFoto, int aNIF, int aContacto, String aMorada, String aSetorAtividade, String aEscalao) {
		super(aNome, aUsername, aPassword, aEstado, aEmail, aTipo, aFoto);
		NIF = aNIF;
		contacto = aContacto;
		morada = aMorada;
		setorAtividade = aSetorAtividade;
		escalao = aEscalao;
	}
	
	/**
	 * Método responsável por devolver o NIF do Cliente.
	 * @return o nº inteiro com o NIF do Cliente.
	 */
	public int getNIF() {
		return NIF;
	}
	
	/**
	 * Método responsável por devolver o contacto do Cliente.
	 * @return o nº inteiro com o contacto do Cliente.
	 */
	public int getContacto() {
		return contacto;
	}
	
	/**
	 * Método responsável por devolver a morada do Cliente.
	 * @return a String com a morada do Cliente.
	 */
	public String getMorada() {
		return morada;
	}
	
	/**
	 * Método responsável por devolver o setor de atividade do Cliente.
	 * @return a String  com o setor de atividade do Cliente.
	 */
	public String getSetorAtividade() {
		return setorAtividade;
	}
	
	/**
	 * Método responsável por devolver o escalão do Cliente.
	 * @return a String com o escalão do Cliente. 
	 */
	public String getEscalao() {
		return escalao;
	}
	
	/**
	 * Método responsável por atualizar o NIF do Cliente.
	 * @param aNIF - NIF a introduzir no atributo correspondente.
	 */
	public void setNIF(int aNIF) {
		NIF = aNIF;
	}
	
	/**
	 * Método responsável por atualizar o contacto do Cliente.
	 * @param aContacto - contacto a introduzir no atributo correspondente.
	 */
	public void setContacto(int aContacto) {
		contacto = aContacto;
	}
	
	/**
	 * Método responsável por atualizar a morada do Cliente.
	 * @param aMorada - morada a introduzir no atributo correspondente.
	 */
	public void setMorada(String aMorada) {
		morada = aMorada;
	}
	
	/**
	 * Método responsável por atualizar o setor de atividade do Cliente.
	 * @param aSetorAtividade - setor de atividade a introduzir no atributo correspondente.
	 */
	public void setSetorAtividade(String aSetorAtividade) {
		setorAtividade = aSetorAtividade;
	}
	
	/**
	 * Método responsável por atualizar o escalão do Cliente.
	 * @param aEscalao - escalao a introduzir no atributo correspondente.
	 */
	public void setEscalao(String aEscalao) {
		escalao = aEscalao;
	}
}
