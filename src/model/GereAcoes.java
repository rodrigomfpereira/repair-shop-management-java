package ap3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Classe responsável por gerir as ações da aplicação.
 * @author RodrigoPereira
 */
public class GereAcoes {

	GereLigacoes gestorLigacoes = new GereLigacoes(); 
	
	
	/**
	 * Método responsável por efetuar o registo de uma nova ação na base de dados.
	 * @param aDescricao - String com a descrição da ação a registar.
	 * @param aIDUtilizador - ID do utilizador responsável por efetuar a ação a registar.
	 * @return true se conseguiu efetuar a inserção, false caso contrário.
	 */
	public boolean registarAcao(String aDescricao, Integer aIDUtilizador) {
		
		StringBuffer sqlQuery = new StringBuffer("INSERT INTO acao (ac_data, ac_descricao, utilizador_util_id)");
		sqlQuery.append(" VALUES (?, ?, ?) ");
		
		int resultadoIns = gestorLigacoes.executarUpdate(sqlQuery, LocalDateTime.now(), aDescricao, aIDUtilizador);
		if (resultadoIns != -1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver todas as ações estruturadas em formato de matriz.
	 * @return String[][] Matriz de Strings onde cada linha representa uma ação
	 */
	public String[][] obterMatrizTodasAcoes() {
		ArrayList<String[]> linhas = new ArrayList<String[]>();
		
		StringBuffer sqlQuery = new StringBuffer("SELECT a.ac_descricao, a.ac_data, u.util_nome ");
		sqlQuery.append("FROM acao a, utilizador u ");
		sqlQuery.append("WHERE a.utilizador_util_id = u.util_id "); 
		sqlQuery.append("ORDER BY a.ac_data DESC");

		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

		try {
			while (rs != null && rs.next()) {			
				String descricao = rs.getString("ac_descricao");
				if (descricao == null || descricao.trim().isEmpty()) {
					descricao = "---";
				}
				
				String dataHora = rs.getString("ac_data");
				if (dataHora == null || dataHora.trim().isEmpty()) {
					dataHora = "---";
				}
				
				String utilizador = rs.getString("util_nome");
				if (utilizador == null || utilizador.trim().isEmpty()) {
					utilizador = "---";
				}

				linhas.add(new String[]{descricao, dataHora, utilizador});
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}

		return linhas.toArray(new String[0][0]);
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver as ações filtradas por utilizador em formato de matriz.
	 * @param aNomePesquisa - Nome ou parte do nome do utilizador a pesquisar
	 * @return String[][] Matriz de Strings onde cada linha representa uma ação
	 */
	public String[][] obterMatrizAcoesPorNomeUtilizador(String aNomePesquisa) {
		ArrayList<String[]> linhas = new ArrayList<String[]>();
		
		StringBuffer sqlQuery = new StringBuffer("SELECT a.ac_descricao, a.ac_data, u.util_nome ");
		sqlQuery.append("FROM acao a, utilizador u ");
		sqlQuery.append("WHERE a.utilizador_util_id = u.util_id ");
		sqlQuery.append("AND u.util_nome LIKE ? ");
		sqlQuery.append("ORDER BY a.ac_data DESC");

		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, "%" + aNomePesquisa + "%");

		try {
			while (rs != null && rs.next()) {
				String descricao = rs.getString("ac_descricao");
				if (descricao == null || descricao.trim().isEmpty()) {
					descricao = "---";
				}
				
				String dataHora = rs.getString("ac_data");
				if (dataHora == null || dataHora.trim().isEmpty()) {
					dataHora = "---";
				}
				
				String utilizador = rs.getString("util_nome");
				if (utilizador == null || utilizador.trim().isEmpty()) {
					utilizador = "---";
				}

				linhas.add(new String[]{descricao, dataHora, utilizador});
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}

		return linhas.toArray(new String[0][0]);
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver formatado todo o conteúdo de logs da aplicação.
	 * @return a ArrayList de Strings com todos os logs presentes na base de dados.
	 */
	public ArrayList<String> devolverListaLogsFormatada() {
	    ArrayList<String> logs = new ArrayList<String>();
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT a.ac_data, a.ac_descricao, u.util_nome ");
	    sqlQuery.append("FROM acao a LEFT JOIN utilizador u ON a.utilizador_util_id = u.util_id ");
	    sqlQuery.append("ORDER BY a.ac_data DESC");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        
	        while (rs != null && rs.next()) {
	            String dataHora = sdf.format(rs.getTimestamp("ac_data"));
	            String nome = rs.getString("util_nome");
	            if (nome == null) nome = "";
	            String acao = rs.getString("ac_descricao");


	            String linha = "[" + dataHora + "] " + nome + ": " + acao;
	            logs.add(linha);
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return logs;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver formatado todo o conteúdo de logs da aplicação correspondente a um ou mais utilizadores.
	 * @param aNomePesquisa - Nome de utilizador ou parte do mesmo a pesquisar
	 * @return a ArrayList de Strings com todos os logs presentes na base de dados correspondentes à restrição.
	 */
	public ArrayList<String> pesquisarLogsPorUtilizador(String aNomePesquisa) {
	    ArrayList<String> logs = new ArrayList<String>();
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT a.ac_data, a.ac_descricao, u.util_nome ");
	    sqlQuery.append("FROM acao a INNER JOIN utilizador u ON a.utilizador_util_id = u.util_id ");
	    sqlQuery.append("WHERE u.util_nome LIKE ? ORDER BY a.ac_data DESC");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, "%" + aNomePesquisa + "%");

	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        while (rs != null && rs.next()) {
	            String linha = "[" + sdf.format(rs.getTimestamp("ac_data")) + "] " 
	                         + rs.getString("util_nome") + ": " 
	                         + rs.getString("ac_descricao");
	            logs.add(linha);
	        }
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return logs;
	}
	
	/**
	 * Método responsável por registar uma ação de abertura de aplicação e devolver a contagem do número de execuções da aplicação, inclusive a atual.
	 * @return o nº inteiro correspondente à quantidade de execuções da aplicação. Caso não consiga aceder à base de dados, devolve 1, correspondente à execução atual.
	 */
	public int novaExecucao() {
		
		registarAcao("abertura da aplicação", null);
		
		StringBuffer sqlQuery = new StringBuffer("SELECT COUNT(*) AS numAcao FROM acao WHERE ac_descricao = 'abertura da aplicação'");
		
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);
	    
	    try {
	        if (rs != null && rs.next()) {
	    
	            return rs.getInt("numAcao");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	gestorLigacoes.fecharResultSet(rs);
	    }
	    return 1; 
	}
	
	/**
	 * Método responsável por pesquisar na base de dados e devolver a maior data do tipo de ação 'abertura da aplicação'.
	 * @return a LocalDateTime correspondente à data da ultima abertura da aplicação, ou null caso ocorra um erro.
	 */
	public LocalDateTime devolverUltimaAberturaAplicacao() {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT MAX(ac_data) AS ultima_data FROM acao WHERE ac_descricao = 'abertura da aplicação'");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);
		
		try {
			if (rs != null && rs.next()) {
				
				return rs.getTimestamp("ultima_data").toLocalDateTime();
				
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}
		return null;
	}
}
