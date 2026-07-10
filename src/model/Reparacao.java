package ap3.model;

import java.time.LocalDateTime;


/**
 * Classe que representa as reparações nos equipamentos na aplicação. 
 * @author RodrigoPereira
 */
public class Reparacao {
	private String numeroReparacao;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataFimReparacao;
	private long tempoDecorrido;
	private float custoProcesso = 0;
	private int estado; //1 = criada,2 = rejeitada,3 = aceite pelo gestor ,4 = aceite pelo funcionário (decorrer), 5 = finalizada pelo func, 6 = arquivada pelo gestor
	private String observacoes;
	private Funcionario funcResponsavel;

	/**
	 * Instancia uma reparação recebendo apenas o nº de reparação e as observações.
	 * @param aNumeroReparacao - String com o nº da reparação a instanciar. 
	 */
	public Reparacao (String aNumeroReparacao, String aObservacoes) {
		estado = 1;
		numeroReparacao = aNumeroReparacao;
		dataCriacao = LocalDateTime.now();
		observacoes = aObservacoes;
	}

	/**
	 * Instancia uma reparação recebendo o nº de reparação, data de criação e estado da reparação.
	 * @param aNumeroReparacao - String com o nº de reparação a instanciar.
	 * @param aDataCriacao - LocalDateTime com a data de criação da reparação.
	 * @param aEstado - nº inteiro com o estado da reparação a instanciar.
	 */
	public Reparacao (String aNumeroReparacao, LocalDateTime aDataCriacao, int aEstado, String aObservacoes) {
		numeroReparacao = aNumeroReparacao;
		estado = aEstado;
		dataCriacao = aDataCriacao;
		observacoes = aObservacoes;
	}

	/**
	 * Instancia uma reparação recebendo todos os atributos da mesma.
	 * @param aNumeroReparacao - String com o nº de reparação a instanciar.
	 * @param aDataCriacao - LocalDateTime com a data de criação da reparação.
	 * @param aDataFim - LocalDateTime com a data de fim da reparação.
	 * @param aTempoDecorrido - long com o tempo decorrido desde o início da reparação.
	 * @param aCusto - custo da reparação.
	 * @param aEstado - nº inteiro com o estado da reparação a instanciar.
	 * @param aObservacoes - Observações da reparação.
	 * @param aFunc - Funcionario responsável pela reparação.
	 */
	public Reparacao(String aNumeroReparacao, LocalDateTime aDataCriacao, LocalDateTime aDataFim, long aTempoDecorrido, float aCusto, int aEstado, String aObservacoes, Funcionario aFunc) {
		numeroReparacao = aNumeroReparacao;
		dataCriacao = aDataCriacao;
		dataFimReparacao = aDataFim;
		tempoDecorrido = aTempoDecorrido;
		custoProcesso = aCusto;
		estado = aEstado;
		observacoes = aObservacoes;
		funcResponsavel = aFunc;
	}

	/**
	 * Método responsável por devolver o nº de reparação da Reparação.
	 * @return o nº inteiro com o º de reparação da Reparação.
	 */
	public String getNumeroReparacao() {
		return numeroReparacao;
	}

	/**
	 * Método responsável por devolver a data de criação da Reparação.
	 * @return a LocalDateTime com a data de criação da Reparação.
	 */
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	/**
	 * Método responsável por devolver a data de fim da Reparação.
	 * @return a LocalDateTime com a data de fim da Reparação.
	 */
	public LocalDateTime getDataFimReparacao() {
		return dataFimReparacao;
	}

	/**
	 * Método responsável por devolver o tempo decorrido entre a criação e o fim da Reparação.
	 * @return o nº inteiro long com o tempo decorrido entre a criação e o fim da Reparação.
	 */
	public long getTempoDecorrido() {
		return tempoDecorrido;
	}

	/**
	 * Método responsável por devolver o custo da Reparação.
	 * @return o nº decimal com o custo da Reparação.
	 */
	public float getCustoProcesso() {
		return custoProcesso;
	}

	/**
	 * Método responsável por devolver o estado da Reparação.
	 * @return o nº inteiro com o estado da Reparação.
	 */
	public int getEstado() {
		return estado;
	}

	/**
	 * Método responsável por devolver as observações da Reparação.
	 * @return a String com as observações da Reparação.
	 */
	public String getObservacoes() {
		return observacoes;
	}
}
