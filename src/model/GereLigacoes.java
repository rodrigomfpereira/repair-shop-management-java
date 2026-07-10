package ap3.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe responsável por estabelecer ligações e manipular a base de dados.
 * @author RodrigoPereira
 */
public class GereLigacoes {
	private GereFicheiroProperties gestorFicheiroProperties = new GereFicheiroProperties();
	

	/**
	 * Efetua conexão à base de dados usando as propriedades presentes no ficheiro properties.
	 * Devolve a conexão para todos os métodos que necessitarem sem ter as propriedades hardcoded.
	 * @return - Um objeto Connection se a ligação for bem-sucedida, null caso contrário.
	 */
	public Connection efetuarConexao() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			Properties props = gestorFicheiroProperties.carregarDefinicoes();
			
			String url = "jdbc:mysql://" + props.getProperty("db.ip") + ":" + props.getProperty("db.porto") + "/" + props.getProperty("db.nome") 
					   + "?useTimezone=true&serverTimezone=Europe/Lisbon" + "&user=" + props.getProperty("db.user") + "&password=" + props.getProperty("db.pass");
			
			return DriverManager.getConnection(url);
		} catch(ClassNotFoundException cnfe) {
			return null;
		} catch(SQLException sqle) {
			return null;
		}
	}
	
	
	/**
	 * Método responsável por executar na base de dados queries do tipo DML.
	 * @param aSQLQuery - Query a ser enviada para a base de dados
	 * @param parametros - Parâmetros a inserir no prepared statement 
	 * @return	o número de linhas afetadas, ou -1 caso ocorra um erro.
	 */
	
	public int executarUpdate (StringBuffer aSQLQuery, Object...parametros) {
		try (Connection conn = efetuarConexao()) {
			if (conn == null)
				return -1;
			try (PreparedStatement ps = conn.prepareStatement(aSQLQuery.toString())) {
				
				if (parametros != null) {
	                for (int index = 0; index < parametros.length; index++) {
	              
	                    ps.setObject(index + 1, parametros[index]);
	                }
	            }
				
                return ps.executeUpdate();
            } catch (SQLException sqle) {
                return -1;
            }
		} catch (SQLException sqle) {
            return -1;
        }
	}
	
	/**
	 * Método responsável por executar na base de dados queries do tipo DQL.
	 * @param aSQLQuery - Query a ser enviada para a base de dados
	 * @param parametros - Parâmetros a inserir no prepared statement 
	 * @return	o objeto ResultSet com o resultado da query se esta obtiver sucesso, null caso contrário.
	 */
	public ResultSet executarSelect (StringBuffer aSQLQuery, Object...parametros) {
		//não podemos usar ARM, precisamos que o ResultSet continue ativo fora desta classe
		Connection conn = efetuarConexao();
		
		if (conn == null)
			return null;
		
		try {
			PreparedStatement ps = conn.prepareStatement(aSQLQuery.toString());
			
			if (parametros != null) {
                for (int index = 0; index < parametros.length; index++) {
                    ps.setObject(index + 1, parametros[index]);
                }
            }
			
			return ps.executeQuery();
			
		} catch (SQLException sqle) {        
	        fecharConexao(conn);
	        return null;
		}
	}
	
	/**
	 * Método que permite verificar se conseguimos chegar a uma tabela.
	 * Este método permite garantirmos que a tabela existe e informar o utilizador caso contrário, evitando assim encontrar exceções.
	 * @param aNomeTabela - Nome da tabela a verificar
	 * @return true caso for possível aceder à tabela, false caso contrário.
	 */
	public boolean verificarAcessoTabela(String aNomeTabela) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM " + aNomeTabela + " LIMIT 1");

	    ResultSet rs = executarSelect(sqlQuery);

	    if (rs != null) {
	        fecharResultSet(rs); 
	        return true;
	    }

	    return false;
	}
	

	/**
	 * Método que permite verificar se uma tabela está vazia.
	 * @param aNomeTabela - Nome da tabela a verificar.
	 * @return true se a tabela estiver vazia, false caso contrário.
	 */
	public boolean isTabelaVazia(String aNomeTabela) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM "+ aNomeTabela +" LIMIT 1");
	    
	    ResultSet rs = executarSelect(sqlQuery);
	    
	    boolean vazia = true;

	    if (rs != null) {
	        try {
	            if (rs.next())
	                vazia = false;
	            
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	            
	        } finally {
	        	fecharResultSet(rs);
	        }
	    }
	    return vazia;
	}
	
	/**
	 * Método que permite encontrar o número máximo do atributo sequencial de uma tabela à base de dados.
	 * Este método facilita a aquisição da ultima chave primária inserida.
	 * @param aNomeAtributo - Nome da chave primária da tabela.
	 * @param aNomeTabela - Nome da tabela.
	 * @return o número inteiro correspondente à chave primária, ou -1 caso ocorra algum erro.
	 */
	public int maxChavePrimaria (String aNomeAtributo, String aNomeTabela) {
		StringBuffer sql = new StringBuffer("SELECT MAX("+aNomeAtributo+") AS IDMax FROM "+aNomeTabela);
		
		ResultSet rs = executarSelect(sql);
		
		if (rs != null) {
			try {
				if (rs.next()) 
					return rs.getInt("IDMax");
				
			}  catch (SQLException sqle) {
	            sqle.printStackTrace();
	            
			} finally {
	        	fecharResultSet(rs);
	        }
		}
		return -1;
	}
	
	/**
	 * Método responsável por fechar a conexão à base de dados.
	 * @param aConn - A Connection a fechar.
	 */
	public void fecharConexao(Connection aConn) {
		try {
	        if (aConn != null && !aConn.isClosed()) {
	            aConn.close();
	        }
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    }
	}
	
	/**
	 * Método responsável por fechar a conexão à base de dados a partir de um ResultSet.
	 * @param aRS - O ResultSet a fechar.
	 */
	public void fecharResultSet(ResultSet aRS) {
		try {
			if (aRS != null) {
				Connection conn = aRS.getStatement().getConnection();
				fecharConexao(conn);
			}
		}  catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    }
	}
}
