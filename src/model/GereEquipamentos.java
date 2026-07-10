package ap3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * Classe responsável por gerir os equipamentos da aplicação.
 * @author RodrigoPereira
 */
public class GereEquipamentos {
	
	GereLigacoes gestorLigacoes = new GereLigacoes(); 
	
	/**
	 * Método responsável por efetuar o registo de um novo equipamento na base de dados.
	 * @param aEquipamento - Equipamento a inserir
	 * @param aIDUtilizador - Utilizador responsável pela inserção do equipamento
	 * @return true caso a inserção tenha tido sucesso, false caso contrário
	 */
	public boolean registarEquipamento(Equipamento aEquipamento, Integer aIDUtilizador) {
		
		
		StringBuffer sqlQuery = new StringBuffer("INSERT INTO equipamento (eq_marca, eq_modelo, eq_sku, eq_data_fabrico, eq_lote, eq_observacoes, eq_data_submissao_pedido, cliente_utilizador_util_id)");
		sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");
		
		int resultadoInsEq = gestorLigacoes.executarUpdate(sqlQuery, aEquipamento.getMarca(), aEquipamento.getModelo(), aEquipamento.getCodigoSKU(), aEquipamento.getDataFabrico(), aEquipamento.getLote(), aEquipamento.getObservacoes(), aEquipamento.getDataSubmissao(), aIDUtilizador);
				
		
		if (resultadoInsEq != -1)
			return true;		
		else
			return false;
	}
	
	/**
	 * Método responsável por pesquisar e devolver da base de dados um equipamento procurando pelo codigoSKU recebido como parâmetro.
	 * @param aCodigoSKU - CodigoSKU usado para restringir a pesquisa da base de dados.
	 * @return o equipamento instanciado a partir dos dados obtidos da base de dados, ou null caso tenha ocorrido um erro.
	 */
	public Equipamento devolverEquipamento(int aCodigoSKU) {
		
		StringBuffer sqlQuery = new StringBuffer ("SELECT * FROM equipamento WHERE eq_sku = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aCodigoSKU);
		
		try {
			if (rs != null && rs.next()) {

				return new Equipamento (rs.getString("eq_marca"), rs.getString("eq_modelo"), rs.getInt("eq_sku"), rs.getDate("eq_data_submissao_pedido").toLocalDate(),rs.getString("eq_lote"), rs.getString("eq_observacoes"));
						
			} 
		    
		}	catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
		return null;
		
	}
	
	/**
	 * Método responsável por pesquisar e devolver da base de dados o ID de um equipamento procurando pelo codigoSKU recebido como parâmetro.
	 * @param aCodigoSKU - CodigoSKU usado para restringir a pesquisa da base de dados.
	 * @return o nº inteiro correspondente ao id do equipamento, ou -1 caso tenha ocorrido algum erro.
	 */
	public int devolverIDEquipamento(int aCodigoSKU) {
		
		StringBuffer sqlQuery = new StringBuffer ("SELECT eq_id FROM equipamento WHERE eq_sku = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aCodigoSKU);
		
		try {
			if (rs != null && rs.next()) {

				return rs.getInt("eq_id");
						
			} 
		    
		}	catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
		return -1;
		
	}
	
	/**
	 * Método responsável por pesquisar e devolver da base de dados o ID do cliente responsável pelo equipamento, procurando pelo codigoSKU recebido como parâmetro.
	 * @param aCodigoSKU - CodigoSKU usado para restringir a pesquisa da base de dados.
	 * @return o nº inteiro correspondente ao id do cliente, ou -1 caso tenha ocorrido algum erro.
	 */
	public int devolverIDCliente(int aCodigoSKU) {
		
		StringBuffer sqlQuery = new StringBuffer ("SELECT cliente_utilizador_util_id FROM equipamento WHERE eq_sku = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aCodigoSKU);
		
		try {
			if (rs != null && rs.next()) {
				
				return rs.getInt("cliente_utilizador_util_id");
			}
		} 	catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
		return -1;
			
	}
	
	/**
	 * Método responsável por pesquisar e devolver da base de dados o codigoSKU procurando pelo ID do equipamento recebido como parâmetro.
	 * @param aEquipamentoID - ID do equipamento usado para restringir a pesquisa da base de dados.
	 * @return o nº inteiro correspondente ao codigoSKU do equipamento, ou -1 caso tenha ocorrido algum erro.
	 */
	public int devolverSKUEq(int aEquipamentoID) {
		
		StringBuffer sqlQuery = new StringBuffer ("SELECT eq_sku FROM equipamento WHERE eq_id = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aEquipamentoID);
		
		try {
			if (rs != null && rs.next()) {
				
				return rs.getInt("eq_sku");
			}
		} 	catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
		return -1;
			
	}
	
	/**
	 * Métoo responsável por pesquisar um equipamento na base de dados por codigoSKU e verificar se o ID do utilizador recebido como parâmetro corresponde ao dono desse equipamento.
	 * @param aCodigoSKU - CodigoSKU usado para restringir a pesquisa da base de dados.
	 * @param aIDUtilizador - ID do utilizador usado para comparar com o ID recebido da base de dados.
	 * @return true se os IDs de utilizador corresponderem, false caso contrário.
	 */
	public boolean verificarDonoEquipamento(int aCodigoSKU, int aIDUtilizador) {
		
		StringBuffer sqlQuery = new StringBuffer ("SELECT cliente_utilizador_util_id AS cli_ID FROM equipamento WHERE eq_sku = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aCodigoSKU);
		
		try {
			if (rs != null && rs.next()) {
				if (rs.getInt("cli_ID") == aIDUtilizador)
					return true;
						
			} 
		    
		}	catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
		return false;
	}
	
	/**
	 * Método responsável por verificar se um utilizador tem equipamentos registados na base de dados.
	 * @param aIDUtilizador - ID do utilizador usado para restringir a pesquisa.
	 * @return true caso tenha equipamentos registados, false caso contrário
	 */
	public boolean temEquipamentosRegistados(int aIDUtilizador) {
	    boolean temEquipamentos = false;
	    StringBuffer sqlQuery = new StringBuffer("SELECT eq_sku FROM equipamento WHERE cliente_utilizador_util_id = ? LIMIT 1");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDUtilizador);

	    try {
	        if (rs != null && rs.next()) {
	           
	            temEquipamentos = true;
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }

	    return temEquipamentos;
	}
	
	/**
	 * Método responsável por verificar se o número máximo de equipamentos foi atingido, de forma a impedir um novo registo.
	 * @return true caso tenha sido atingido o limite, false caso contrário.
	 */
	public boolean StockEquipamentoCheio() {
	    
	    int limite_max = 1000000;
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT COUNT(*) AS quant_equipamento FROM equipamento");
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);
	    
	    try {
	        if (rs != null && rs.next()) {
	            int totalEquipamentos = rs.getInt("quant_equipamento");
	            return totalEquipamentos >= limite_max;
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return false; 
	}
	
	/**
	 * Método responsável por gerar um número entre 1 e 1 000 000.
	 * @return o nº inteiro gerado.
	 */
	public int gerarCodigoSKU() {
		int numAleatorioSKU;
		Random nAleatorio = new Random();
		do {
			numAleatorioSKU = nAleatorio.nextInt(1000000)+1;
		}while(existeCodigoSKU(numAleatorioSKU));
		
		return numAleatorioSKU;
	}
	
	/**
	 * Método responsável por verificar na base de dados se o codigoSKU recebido como parâmetro já está a ser usado.
	 * @param aCodigoSKU - CodigoSKU a verificar na base de dados.
	 * @return true caso este código exista na base de dados, false caso contrário.
	 */
	public boolean existeCodigoSKU(int aCodigoSKU) {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT * FROM equipamento WHERE eq_sku = ? LIMIT 1");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aCodigoSKU);
		
		boolean existe = false;
	    try {
	        
	        if (rs != null && rs.next()) {
	            existe = true;
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	gestorLigacoes.fecharResultSet(rs);
	    }
	    
	    return existe;
	}
	
}
