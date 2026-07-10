package ap3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe responsável por gerir as notificações da aplicação.
 * @author RodrigoPereira
 */
public class GereNotificacoes {

	GereLigacoes gestorLigacoes = new GereLigacoes(); 
	
	/**
	 * Método responsável por efetuar o registo de uma nova notificação na base de dados.
	 * @param aNotificacao - Objeto Notificação a ser inserida na base de dados.
	 * @param aIDPeca - ID da peça a inserir na notificação.
	 * @param aIDReparacao - ID da reparação a inserir na notificação.
	 * @param aIDUtilizador - ID do utilizador a inserir na notificação.
	 * @return true se a inserção tiver sucesso, false caso contrário.
	 */
	public boolean registarNotificacao(Notificacao aNotificacao, Integer aIDPeca, Integer aIDReparacao, Integer aIDUtilizador) {
		
		StringBuffer sqlQuery = new StringBuffer("INSERT INTO notificacao (not_tipo, not_lida, reparacao_rep_id, utilizador_util_id)");
		sqlQuery.append(" VALUES (?, ?, ?, ?) ");
		
		int resultadoIns = gestorLigacoes.executarUpdate(sqlQuery, aNotificacao.getTipo(), aNotificacao.getEstadoLeitura(), aIDReparacao, aIDUtilizador);
		if (resultadoIns != -1) {
			return true;
		}
		return false;
	}	
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver formatado todo o conteúdo de notificações relevante a um único funcionário.
	 * @param aIDFuncionario - ID do funcionário usado para restringir a pesquisa.
	 * @return a ArrayList de Strings com todas as notificações presentes na base de dados relevantes a um funcionário.
	 */
	public ArrayList<String> consultarNotificacoesFuncionario(int aIDFuncionario) {
	    ArrayList<String> listaFormatada = new ArrayList<String>();
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT r.rep_num_reparacao ");
	    sqlQuery.append("FROM notificacao n, reparacao r ");
	    sqlQuery.append("WHERE n.reparacao_rep_id = r.rep_id ");
	    sqlQuery.append("AND n.not_tipo = 5 ");
	    sqlQuery.append("AND n.not_lida = false ");
	    sqlQuery.append("AND n.utilizador_util_id = ? ");
	    sqlQuery.append("ORDER BY n.not_id DESC");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDFuncionario);

	    try {
	        while (rs != null && rs.next()) {
	            listaFormatada.add("Foi-lhe atribuída a reparação: " + rs.getString("rep_num_reparacao"));
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return listaFormatada;
	}

	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver a quantidade de notificações relevantes a um funcionário.
	 * @param aIDFuncionario - ID do funcionário usado para restringir a pesquisa.
	 * @return o nº inteiro correspondente ao nº de notificações encontradas.
	 */
	public int contarNotificacoesFuncionario(int aIDFuncionario) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT COUNT(*) AS total FROM notificacao ");
	    sqlQuery.append("WHERE not_lida = false AND utilizador_util_id = ? AND not_tipo = 5");
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDFuncionario);
	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("total");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return 0;
	}

	/**
	 * Método responsável por colocar na base de dados todas as notificações relevantes a um funcionário como lidas.
	 * @param aIDFuncionario - ID do funcionário usado para restringir a pesquisa.
	 * @return true caso tenha conseguido atualizar as notificações, false caso contrário.
	 */
	public boolean limparNotificacoesFuncionario(int aIDFuncionario) {
	    StringBuffer sqlQuery = new StringBuffer("UPDATE notificacao SET not_lida = true ");
	    sqlQuery.append("WHERE utilizador_util_id = ? AND not_tipo = 5 AND not_lida = false");

	    int resultado = gestorLigacoes.executarUpdate(sqlQuery, aIDFuncionario);

	    if (resultado != -1)
	    	return true;
	    
	    return false;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver a quantidade de notificações relevantes a um cliente.
	 * @param aIDCliente - ID do cliente usado para restringir a pesquisa.
	 * @return o nº inteiro correspondente ao nº de notificações encontradas.
	 */
	public int contarNotificacoesCliente(int aIDCliente) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT COUNT(*) AS total FROM notificacao ");
	    sqlQuery.append("WHERE not_lida = false AND utilizador_util_id = ? AND not_tipo IN (4, 7)");
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDCliente);
	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("total");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return 0;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver formatado todo o conteúdo de notificações relevante a um único cliente.
	 * @param aIDCliente - ID do cliente usado para restringir a pesquisa.
	 * @return a ArrayList de Strings com todas as notificações presentes na base de dados relevantes a um cliente.
	 */
	public ArrayList<String> consultarNotificacoesCliente(int aIDCliente) {
	    ArrayList<String> listaFormatada = new ArrayList<String>();
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT n.not_tipo, r.rep_num_reparacao ");
	    sqlQuery.append("FROM notificacao n ");
	    sqlQuery.append("LEFT JOIN reparacao r ON n.reparacao_rep_id = r.rep_id ");
	    sqlQuery.append("WHERE n.not_lida = false AND n.utilizador_util_id = ? ");
	    sqlQuery.append("AND n.not_tipo IN (4, 7) ");
	    sqlQuery.append("ORDER BY n.not_id DESC");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDCliente);

	    try {
	        while (rs != null && rs.next()) {
	            int tipo = rs.getInt("not_tipo");
	            String numRep = rs.getString("rep_num_reparacao");
	            String linha = "";

	            switch (tipo) {
	                case 4:
	                    linha = "A sua reparação " + numRep + " foi negada pelo gestor.";
	                    break;
	                case 7:
	                    linha = "A sua reparação " + numRep + " está finalizada.";
	                    break;
	            }
	            
	            if (!linha.isEmpty()) {
	                listaFormatada.add(linha);
	            }
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return listaFormatada;
	}

	/**
	 * Método responsável por colocar na base de dados todas as notificações relevantes a um cliente como lidas.
	 * @param aIDCliente - ID do cliente usado para restringir a pesquisa.
	 * @return true caso tenha conseguido atualizar as notificações, false caso contrário.
	 */
	public boolean limparNotificacoesCliente(int aIDCliente) {
	    StringBuffer sqlQuery = new StringBuffer("UPDATE notificacao SET not_lida = true ");
	    sqlQuery.append("WHERE utilizador_util_id = ? AND not_tipo IN (4, 7) AND not_lida = false");

	    int resultado = gestorLigacoes.executarUpdate(sqlQuery, aIDCliente);
	    
	    if (resultado != -1)
	    	return true;
	    
	    return false;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver a quantidade de notificações relevantes aos gestores.
	 * @return o nº inteiro correspondente ao nº de notificações encontradas.
	 */
	public int contarNotificacoesGestor() {
	    StringBuffer sqlQuery = new StringBuffer("SELECT COUNT(*) AS total FROM notificacao ");
	    sqlQuery.append("WHERE not_lida = false AND not_tipo IN (1, 2, 3, 6, 8)");
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);
	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("total");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return 0;
	}
	
	/**
	 * Método responsável por efetuar uma pesquisa à base de dados e devolver formatado todo o conteúdo de notificações relevante aos gestores.
	 * @return a ArrayList de Strings com todas as notificações presentes na base de dados relevantes aos gestores.
	 */
	public ArrayList<String> consultarNotificacoesGestor() {
	    ArrayList<String> listaFormatada = new ArrayList<String>();
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT n.not_tipo, n.not_lida, u.util_username, r.rep_num_reparacao ");
	    sqlQuery.append("FROM notificacao n ");
	    sqlQuery.append("LEFT JOIN utilizador u ON n.utilizador_util_id = u.util_id ");
	    sqlQuery.append("LEFT JOIN reparacao r ON n.reparacao_rep_id = r.rep_id ");
	    sqlQuery.append("WHERE n.not_lida = false AND n.not_tipo IN (1, 2, 3, 6, 8) ");
	    sqlQuery.append("ORDER BY n.not_id DESC");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

	    try {
	        while (rs != null && rs.next()) {
	            int tipo = rs.getInt("not_tipo");
	            String username = rs.getString("util_username");
	            String numRep = rs.getString("rep_num_reparacao");
	            String linha = "";

	            switch (tipo) {
	                case 1:
	                    linha = "O utilizador '" + username + "' registou-se no sistema e aguarda aprovação.";
	                    break;
	                case 2:
	                    linha = "O utilizador '" + username + "' solicitou a remoção de dados.";
	                    break;
	                case 3:
	                    linha = "Novo pedido de reparação (" + numRep + ") por: '" + username+"'.";
	                    break;
	                case 6:
	                    linha = "Atribuição da reparação " + numRep + " negada pelo funcionário: " + username;
	                    break;
	                case 8:
	                    linha = "A reparação " + numRep + " está pendente há mais de 10 dias.";
	                    break;
	            }
	            
	            if (!linha.isEmpty()) {
	                listaFormatada.add(linha);
	            }
	        }
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    
	    return listaFormatada;
	}
	
	/**
	 * Método responsável por colocar na base de dados todas as notificações relevantes aos gestores como lidas.
	 * @return true caso tenha conseguido atualizar as notificações, false caso contrário.
	 */
	public boolean limparNotificacoesGestor() {
	    StringBuffer sqlQuery = new StringBuffer("UPDATE notificacao SET not_lida = true ");
	    sqlQuery.append("WHERE not_tipo IN (1, 2, 3, 6, 8) AND not_lida = false");

	    int resultado = gestorLigacoes.executarUpdate(sqlQuery);

	    if (resultado != -1) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Método responsável por obter uma matriz bidimensional contendo as notificações dos funcionários.
	 * @param aIDFuncionario - ID do funcionário usado para filtrar.
	 * @return Matriz de Strings com as notificações.
	 */
	public String[][] obterMatrizNotificacoesFuncionario(int aIDFuncionario) {
		ArrayList<String> lista = consultarNotificacoesFuncionario(aIDFuncionario);
		String[][] matriz = new String[lista.size()][1];
		
		for (int i = 0; i < lista.size(); i++) {
			matriz[i][0] = lista.get(i);
		}
		return matriz;
	}

	/**
	 * Método responsável por obter uma matriz bidimensional contendo as notificações dos clientes.
	 * @param aIDCliente - ID do cliente usado para filtrar.
	 * @return Matriz de Strings com as notificações.
	 */
	public String[][] obterMatrizNotificacoesCliente(int aIDCliente) {
		ArrayList<String> lista = consultarNotificacoesCliente(aIDCliente);
		String[][] matriz = new String[lista.size()][1];
		
		for (int i = 0; i < lista.size(); i++) {
			matriz[i][0] = lista.get(i);
		}
		return matriz;
	}

	/**
	 * Método responsável por obter uma matriz bidimensional contendo as notificações dos gestores.
	 * @return Matriz de Strings com as notificações.
	 */
	public String[][] obterMatrizNotificacoesGestor() {
		ArrayList<String> lista = consultarNotificacoesGestor();
		String[][] matriz = new String[lista.size()][1];
		
		for (int i = 0; i < lista.size(); i++) {
			matriz[i][0] = lista.get(i);
		}
		return matriz;
	}
}
