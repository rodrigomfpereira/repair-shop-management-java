package ap3.model;

import java.time.LocalDate;
import java.util.ArrayList;



/**
 * Classe que representa os equipamentos na aplicação. 
 * @author RodrigoPereira
 */
public class Equipamento {
	private String marca;
	private String modelo;
	private int codigoSKU;
	private LocalDate dataFabrico;
	private String lote;
	private LocalDate dataSubmissao;
	private Cliente donoEquipamento;
	private ArrayList <Reparacao> listaReparacoes;
	private String observacoes;
	
	/**
	 * Instancia um equipamento recebendo todos os atributos excepto a data de submissão que será inserida automaticamente, e a lista de reparações.
	 * @param aMarca - Marca do equipamento
	 * @param aModelo - Modelo do equipamento
	 * @param aCodigoSKU - CodigoSKU do equipamento
	 * @param aDataFabrico - data de fabrico do equipamento
	 * @param aLote - lote do equipamento
	 * @param aDonoEquipamento - Cliente responsável pela criação do equipamento
	 */
	public Equipamento (String aMarca, String aModelo, int aCodigoSKU, LocalDate aDataFabrico, String aLote, Cliente aDonoEquipamento, String aObservacoes) {
		marca = aMarca;
		modelo = aModelo;
		codigoSKU = aCodigoSKU;
		dataFabrico = aDataFabrico;
		lote = aLote;
		dataSubmissao = LocalDate.now();
		donoEquipamento = aDonoEquipamento;
		listaReparacoes = new ArrayList <Reparacao>();
		observacoes = aObservacoes;
	}
	
	/**
	 * Instancia um equipamento sem receber um dono do equipamento, a data de submissão e a lista de reparações. 
	 * @param aMarca - Marca do equipamento
	 * @param aModelo - Modelo do equipamento
	 * @param aCodigoSKU - CodigoSKU do equipamento
	 * @param aDataFabrico - data de fabrico do equipamento
	 * @param aLote - lote do equipamento
	 */
	public Equipamento (String aMarca, String aModelo, int aCodigoSKU, LocalDate aDataFabrico, String aLote, String aObservacoes) {
		marca = aMarca;
		modelo = aModelo;
		codigoSKU = aCodigoSKU;
		dataFabrico = aDataFabrico;
		lote = aLote;
		dataSubmissao = LocalDate.now();
		listaReparacoes = new ArrayList <Reparacao>();
		observacoes = aObservacoes;
	}
	
	/**
	 * Método responsável por devolver a marca do Equipamento.
	 * @return a String com a marca do Equipamento.
	 */
	public String getMarca() {
		return marca;
	}
	
	/**
	 * Método responsável por devolver o modelo do Equipamento.
	 * @return a String com o modelo do Equipamento.
	 */
	public String getModelo() {
		return modelo;
	}
	
	/**
	 * Método responsável por devolver o codigoSKU do Equipamento.
	 * @return o nº inteiro com o codigoSKU do Equipamento.
	 */
	public int getCodigoSKU() {
		return codigoSKU;
	}
	
	/**
	 * Método responsável por devolver a data de fabrico do Equipamento.
	 * @return a LocalDate com a data de fabrico do Equipamento.
	 */
	public LocalDate getDataFabrico() {
		return dataFabrico;
	}
	
	/**
	 * Método responsável por devolver o lote do Equipamento.
	 * @return a String com o lote do Equipamento.
	 */
	public String getLote() {
		return lote;
	}
	
	/**
	 * Método responsável por devolver a data de submissão do Equipamento.
	 * @return a LocalDate com a data de submissão do Equipamento.
	 */
	public LocalDate getDataSubmissao() {
		return dataSubmissao;
	}
	
	/**
	 * Método responsável por devolver as observações do Equipamento.
	 * @return a String com as observações do Equipamento.
	 */
	public String getObservacoes() {
	    return observacoes;
	}
	
	/**
	 * Método responsável por adicionar uma reparação à lista de reparações do equipamento.
	 * @param aReparacao - Reparação a adicionar ao equipamento
	 * @return true se conseguiu adicionar a reparação, false caso contrário.
	 */
	public boolean adicionarReparacao(Reparacao aReparacao) {
		if (listaReparacoes != null)
			return listaReparacoes.add(aReparacao);
		return false;
	}
	
}
