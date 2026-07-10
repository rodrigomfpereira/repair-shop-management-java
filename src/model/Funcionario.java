package ap3.model;

import java.time.LocalDate;

/**
 * Subclasse de utilizadores destinada a representar os funcionários.
 * @author RodrigoPereira
 */
public class Funcionario extends Utilizador{
	private int NIF;
	private int contacto;
	private String morada;
	private int especializacao;
	private LocalDate dataInicioAtividade;
	
	/**
	 * Instancia um funcionário recebendo todos os atributos definidos na classe.
	 * @param aNome - Nome do funcionário
	 * @param aUsername - Username do funcionário
	 * @param aPassword - Password do funcionário
	 * @param aEstado - Estado da conta
	 * @param aEmail - Email do funcionário
	 * @param aTipo - Tipo de conta
	 * @param aNIF - NIF do funcionário
	 * @param aContacto - Contacto do funcionário
	 * @param aMorada - Morada do funcionário
	 * @param aEspecializacao - Nível de especialização do funcionário
	 * @param aDataInicioAtividade - Data de inicio de atividade do funcionário
	 */
	public Funcionario (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo, int aNIF, int aContacto, String aMorada, int aEspecializacao, LocalDate aDataInicioAtividade) {
		super(aNome, aUsername, aPassword, aEstado, aEmail, aTipo);
		NIF = aNIF;
		contacto = aContacto;
		morada = aMorada;
		especializacao = aEspecializacao;
		dataInicioAtividade = aDataInicioAtividade;
	}
	
	/**
	 * Instancia um funcionário recebendo todos os atributos definidos na classe.
	 * @param aNome - Nome do funcionário
	 * @param aUsername - Username do funcionário
	 * @param aPassword - Password do funcionário
	 * @param aEstado - Estado da conta
	 * @param aEmail - Email do funcionário
	 * @param aTipo - Tipo de conta
	 * @param aFoto - Foto de perfil do funcionário
	 * @param aNIF - NIF do funcionário
	 * @param aContacto - Contacto do funcionário
	 * @param aMorada - Morada do funcionário
	 * @param aEspecializacao - Nível de especialização do funcionário
	 * @param aDataInicioAtividade - Data de inicio de atividade do funcionário
	 */
	public Funcionario (String aNome, String aUsername, String aPassword, int aEstado, String aEmail, String aTipo, String aFoto, int aNIF, int aContacto, String aMorada, int aEspecializacao, LocalDate aDataInicioAtividade) {
		super(aNome, aUsername, aPassword, aEstado, aEmail, aTipo, aFoto);
		NIF = aNIF;
		contacto = aContacto;
		morada = aMorada;
		especializacao = aEspecializacao;
		dataInicioAtividade = aDataInicioAtividade;
	}
	
	/**
	 * Método responsável por devolver o NIF do Funcionário.
	 * @return o nº inteiro com o NIF do Funcionário.
	 */
	public int getNIF() {
		return NIF;
	}
	
	/**
	 * Método responsável por devolver o contacto do Funcionário.
	 * @return o nº inteiro com o contacto do Funcionário.
	 */
	public int getContacto() {
		return contacto;
	}
	
	/**
	 * Método responsável por devolver a morada do Funcionário.
	 * @return a String com a morada do Funcionário.
	 */
	public String getMorada() {
		return morada;
	}
	
	/**
	 * Método responsável por devolver a especialização do Funcionário.
	 * @return o nº inteiro com a especialização do Funcionário.
	 */
	public int getEspecializacao() {
		return especializacao;
	}
	
	/**
	 * Método responsável por devolver a data de ínicio de atividade do Funcionário.
	 * @return a LocalDate correspondente à data de inicio de atividade do Funcionário. 
	 */
	public LocalDate getDataInicioAtividade() {
		return dataInicioAtividade;
	}
	
	/**
	 * Método responsável por atualizar o NIF do Funcionario.
	 * @param aNIF - NIF a introduzir no atributo correspondente.
	 */
	public void setNIF(int aNIF) {
		NIF = aNIF;
	}
	
	/**
	 * Método responsável por atualizar o contacto do Funcionario.
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
	 * Método responsável por atualizar a especialização do Funcionario.
	 * @param aEspecializacao - especializacao a introduzir no atributo correspondente.
	 */
	public void setEspecializacao(int aEspecializacao) {
		especializacao = aEspecializacao;
	}
	
	/**
	 * Método responsável por atualizar a data de inicio de atividade do Funcionario.
	 * @param aDataInicioAtividade - data de inicio de atividade a introduzir no atributo correspondente.
	 */
	public void setDataInicioAtividade(LocalDate aDataInicioAtividade) {
		dataInicioAtividade = aDataInicioAtividade;
	}	
}
