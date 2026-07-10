package ap3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



/**
 * Classe responsável por gerir os utilizadores da aplicação.
 * @author RodrigoPereira
 */
public class GereUtilizadores {
	
	GereLigacoes gestorLigacoes = new GereLigacoes(); 
	GereNotificacoes gestorNotificacoes = new GereNotificacoes();
	GereAcoes gestorAcoes = new GereAcoes();
	
	/**
	* Método responsável por efetuar o registo de um novo utilizador na base de dados.
	* Caso esse utilizador seja cliente ou funcionário, será feita uma inserção na tabela correspondente com os atributos que apenas clientes / funcionários possuem.
	* @param aUtilizador - Utilizador a registar.
	* @return true se as inserções tiverem sucesso, false caso contrário.
	*/
	public boolean registarUtilizador(Utilizador aUtilizador) {
		
		//os gestores apenas precisam de ser notificados acerca dos funcionários e clientes
		boolean notifNecessaria = false;
		//insere os dados comuns a todos os tipos de utilizador 
		int resultadoInsUtil = -1;
		StringBuffer sqlQuery;
		if (aUtilizador.getFoto() == null) {
			sqlQuery = new StringBuffer("INSERT INTO utilizador (util_nome, util_username, util_password, util_estado, util_email, util_tipo)");
			sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?) ");
			
			resultadoInsUtil = gestorLigacoes.executarUpdate(sqlQuery, aUtilizador.getNome(), aUtilizador.getUsername(), aUtilizador.getPassword(), aUtilizador.getEstado(), aUtilizador.getEmail(), aUtilizador.getTipo());
		} else {
			sqlQuery = new StringBuffer("INSERT INTO utilizador (util_nome, util_username, util_password, util_estado, util_email, util_tipo, util_foto)");
			sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?, ?) ");
			
			resultadoInsUtil = gestorLigacoes.executarUpdate(sqlQuery, aUtilizador.getNome(), aUtilizador.getUsername(), aUtilizador.getPassword(), aUtilizador.getEstado(), aUtilizador.getEmail(), aUtilizador.getTipo(), aUtilizador.getFoto());
		}
		
				
		//se a primeira inserção funcionar
		if (resultadoInsUtil != -1) {
			
			//vai buscar a chave primária do util inserido
			int chavePrimaria = gestorLigacoes.maxChavePrimaria("util_id","utilizador");
			
			//para inserir os dados restantes caso seja cliente / funcionário
			if (chavePrimaria!=-1 && aUtilizador instanceof Cliente ) {
				notifNecessaria = true;
				Cliente aCliente = (Cliente) aUtilizador;
				sqlQuery = new StringBuffer("INSERT INTO cliente (cli_nif, cli_contacto, cli_morada, cli_setor_atividade, cli_escalao, utilizador_util_id)");
				sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?)");
				
				int resultadoInsCli = gestorLigacoes.executarUpdate(sqlQuery, aCliente.getNIF(), aCliente.getContacto(), aCliente.getMorada(), aCliente.getSetorAtividade(), aCliente.getEscalao(), chavePrimaria);
				
				//se falhar remove o utilizador 
				if (resultadoInsCli == -1) {
					removerUtilizador(chavePrimaria);
					return false;
				}
					
				
			} else if (chavePrimaria!=-1 &&	aUtilizador instanceof Funcionario) {
				Funcionario aFuncionario = (Funcionario) aUtilizador;
				notifNecessaria = true;
				sqlQuery = new StringBuffer("INSERT INTO funcionario (func_nif, func_contacto, func_morada, func_especializacao, func_data_inicio_atividade, utilizador_util_id)");
				sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?)");
				
				int resultadoInsFunc = gestorLigacoes.executarUpdate(sqlQuery, aFuncionario.getNIF(), aFuncionario.getContacto(), aFuncionario.getMorada(), aFuncionario.getEspecializacao(), aFuncionario.getDataInicioAtividade(), chavePrimaria);
				
				//se falhar remove o utilizador 
				if (resultadoInsFunc == -1) {
					removerUtilizador(chavePrimaria);
					return false;
				}
					
			}
			if (notifNecessaria) {
				Notificacao notif = new Notificacao(1, aUtilizador);
				gestorNotificacoes.registarNotificacao(notif,null,null,chavePrimaria);
			}
			gestorAcoes.registarAcao("registo de conta", chavePrimaria);
			
			return true;
		} else
			return false;
	}
	
	/**
	 * Método responsável por obter uma matriz bidimensional contendo todos os utilizadores com o estado recebido.
	 * @param aEstado Estado de conta para filtrar a pesquisa
	 * @return Uma matriz de Strings onde cada linha representa um utilizador.
	 */
	public String[][] obterMatrizUtilizadoresPorEstado(int aEstado) {
		ArrayList<String[]> linhas = new ArrayList<String[]>();
	    
		StringBuffer sqlQuery = new StringBuffer("SELECT util_username, util_nome, util_tipo ");
		sqlQuery.append("FROM utilizador ");
		sqlQuery.append("WHERE util_estado = " + aEstado + " "); 
		sqlQuery.append("ORDER BY util_nome ASC");

		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

		try {
			while (rs != null && rs.next()) {
				String username = rs.getString("util_username");
				String nome = rs.getString("util_nome");
				String tipo = rs.getString("util_tipo");

				linhas.add(new String[]{username, nome, tipo.toUpperCase()});
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}

		return linhas.toArray(new String[0][0]);
	}
	
	/**
	 * Método responsável por alterar o estado de um utilizador recebendo o username do mesmo. 
	 * É verificado se o utilizador existe, se o estado recebido é diferente do presente na tabela da base de dados, e apenas aí é enviado para ser feita a alteração.
	 * @param aUsername - String com o Username do utilizador a alterar o estado.
	 * @param aEstado - boolean com o estado que será inserido na tabela.
	 * @return true se a inserção teve sucesso, false caso contrário.
	 */
	public boolean alterarEstadoConta(String aUsername, int aEstado) {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT util_estado FROM utilizador WHERE util_username = ? LIMIT 1");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aUsername);
		
		try {
	        if (rs != null && rs.next()) {
	    
	            if (rs.getInt("util_estado") != aEstado) {
	            	sqlQuery = new StringBuffer("UPDATE utilizador SET util_estado = ? WHERE util_username = ?");
	                
	                int resultado = gestorLigacoes.executarUpdate(sqlQuery, aEstado, aUsername);
	                
	                if (resultado != -1)
	                	return true;
	            }
	            	
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	gestorLigacoes.fecharResultSet(rs);
	    }
	    return false; 
		
	}
	
	/**
	 * Método responsável por permitir através do username (único) recebido como parâmetro anular uma conta, garantindo que a conta tenha estado 'pendente para anulação'.
	 * O que este método irá fazer é colocar todos os campos na tabela utilizador, e se necessário depedendendo do tipo de utilizador, na tabela cliente / funcionário a null.
	 * @param aUsername - username do utilizador a anular.
	 * @return true caso a anulação tenha tido sucesso, false caso contrário.
	 */
	public boolean anularConta(String aUsername) {
	    Integer idUtilizador = procurarIDPorUsername(aUsername);
	    
	    if (idUtilizador == null) {
	        return false;
	    }


	    int estadoAtual = devolverEstadoConta(aUsername); 
	    String tipo = procurarTipoPorUsername(aUsername);


	    if (estadoAtual == 3) {

	        if (tipo != null) {
	            if (tipo.equalsIgnoreCase("cliente")) {
	                StringBuffer sqlCli = new StringBuffer("UPDATE cliente SET ");
	                sqlCli.append("cli_nif = NULL, cli_contacto = NULL, cli_morada = NULL, ");
	                sqlCli.append("cli_setor_atividade = NULL, cli_escalao = NULL ");
	                sqlCli.append("WHERE utilizador_util_id = ?");
	                gestorLigacoes.executarUpdate(sqlCli, idUtilizador);
	                
	            } else if (tipo.equalsIgnoreCase("funcionario")) {
	                StringBuffer sqlFunc = new StringBuffer("UPDATE funcionario SET ");
	                sqlFunc.append("func_nif = NULL, func_contacto = NULL, func_morada = NULL, ");
	                sqlFunc.append("func_especializacao = NULL, func_data_inicio_atividade = NULL ");
	                sqlFunc.append("WHERE utilizador_util_id = ?");
	                gestorLigacoes.executarUpdate(sqlFunc, idUtilizador);
	            }
	        }

	        StringBuffer sqlUtil = new StringBuffer("UPDATE utilizador SET ");
	        sqlUtil.append("util_nome = NULL, util_username = NULL, util_password = NULL, ");
	        sqlUtil.append("util_email = NULL, util_tipo = NULL, util_estado = 4 ");
	        sqlUtil.append("WHERE util_id = ?");

	        int resultado = gestorLigacoes.executarUpdate(sqlUtil, idUtilizador);

	        if (resultado != -1)
	        	return true;
	    }

	    return false;
	}
	
	/**
	 * Método que permite devolver o estado da conta a partir do username (único) recebido como parâmetro.
	 * @param aUsername - username do utilizador usado para restringir a pesquisa do estado.
	 * @return o nº inteiro referente ao estado da conta do utilizador, ou -1 caso ocorra algum erro.
	 */
	public int devolverEstadoConta(String aUsername) {
			
			StringBuffer sqlQuery = new StringBuffer("SELECT util_estado FROM utilizador WHERE util_username = ? LIMIT 1");
			
			ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aUsername);
			
			try {
		        if (rs != null && rs.next()) {
		    
		            return rs.getInt("util_estado");
		        }
		    } catch (SQLException sqle) {
		        sqle.printStackTrace();
		    } finally {
		    	gestorLigacoes.fecharResultSet(rs);
		    }
		    return -1; 
	}
	
	/**
	 * Método responsável por verificar se na base de dados existe um utilizador com o username e password recebido.
	 * @param aUsername - String com o username do utilizador a verificar.
	 * @param aPassword - String com a password do utilizador a verificar.
	 * @return o Objeto Utilizador construído a partir dos dados presentes na base de dados, ou null caso não tenha encontrado utilizador correspondente.
	 */
	public Utilizador validarLogin(String aUsername, String aPassword) {
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM utilizador WHERE util_username = ? AND util_password = ? LIMIT 1");
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aUsername, aPassword);
	    
	    try {
	        if (rs != null && rs.next()) {
	        	
	            return new Utilizador(rs.getString("util_nome"), rs.getString("util_username"), rs.getString("util_password"), rs.getInt("util_estado"), 
	            			rs.getString("util_email"), rs.getString("util_tipo"), rs.getString("util_foto"));
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	    	gestorLigacoes.fecharResultSet(rs);
	    }
	    return null; 
	}
	
	/**
	 * Método responsável por encontrar e devolver o tipo de conta do utilizador a partir do username recebido como parâmetro.
	 * @param aUsername - username do utilizador a encontrar na base de dados.
	 * @return a String referente ao tipo de conta do utilizador, ou null caso algum erro ocorra.
	 */
	public String procurarTipoPorUsername(String aUsername) {
	    if (aUsername == null) 
	    	return null;

	    StringBuffer sqlQuery = new StringBuffer("SELECT util_tipo FROM utilizador WHERE util_username = ?");
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aUsername);

	    try {
	        if (rs != null && rs.next()) {
	            return rs.getString("util_tipo");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null; 
	}
	
	/**
	 * Método responsável por efetuar a junção das tabelas 'cliente' e 'utilizador' e devolver um cliente instanciado caso o encontre pelo ID recebido como parâmetro.
	 * @param aID - ID do cliente a encontrar na base de dados.
	 * @return o objeto Cliente instanciado, ou null caso algum erro ocorra.
	 */
	public Cliente devolverCliente(int aID) {
	    // Selecionamos de ambas as tabelas ao mesmo tempo
	    StringBuffer sql = new StringBuffer("SELECT * FROM utilizador, cliente ");
	    sql.append(" WHERE utilizador.util_id = cliente.utilizador_util_id ");
	    sql.append(" AND utilizador.util_id = ? LIMIT 1 ");

	    ResultSet rs = gestorLigacoes.executarSelect(sql, aID);

	    try {
	        if (rs != null && rs.next()) {
	            
	            return new Cliente(rs.getString("util_nome"), rs.getString("util_username"), rs.getString("util_password"), rs.getInt("util_estado"), rs.getString("util_email"), 
	                rs.getString("util_tipo"), rs.getString("util_foto"), rs.getInt("cli_nif"), rs.getInt("cli_contacto"), rs.getString("cli_morada"), rs.getString("cli_setor_atividade"), rs.getString("cli_escalao"));
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null; 
	}
	
	/**
	 * Método responsável por efetuar a junção das tabelas 'funcionario' e 'utilizador' e devolver um funcionário instanciado caso o encontre pelo ID recebido como parâmetro.
	 * @param aID - ID do funcionário a encontrar na base de dados.
	 * @return o objeto Funcionario instanciado, ou null caso algum erro ocorra.
	 */
	public Funcionario devolverFuncionario(int aID) {
	    // Selecionamos de ambas as tabelas ao mesmo tempo
	    StringBuffer sql = new StringBuffer("SELECT * FROM utilizador, funcionario ");
	    sql.append(" WHERE utilizador.util_id = funcionario.utilizador_util_id ");
	    sql.append(" AND utilizador.util_id = ? LIMIT 1");

	    ResultSet rs = gestorLigacoes.executarSelect(sql, aID);

	    try {
	        if (rs != null && rs.next()) {
	            
	            return new Funcionario(rs.getString("util_nome"), rs.getString("util_username"), rs.getString("util_password"), rs.getInt("util_estado"), rs.getString("util_email"), 
	                rs.getString("util_tipo"), rs.getString("util_foto"), rs.getInt("func_nif"), rs.getInt("func_contacto"), rs.getString("func_morada"), rs.getInt("func_especializacao"), rs.getDate("func_data_inicio_atividade").toLocalDate());
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null; 
	}
	
	/**
	 * Método responsável por pesquisar na base de dados e devolver um utilizador instanciado caso o encontre pelo ID recebido como parâmetro.
	 * @param aID - ID do utilizador a encontrar na base de dados.
	 * @return o objeto Utilizador instanciado, ou null caso algum erro ocorra.
	 */
	public Utilizador devolverUtilizador(int aID) {
		
		StringBuffer sql = new StringBuffer("SELECT * FROM utilizador WHERE util_id = ?");
		
		ResultSet rs = gestorLigacoes.executarSelect(sql, aID);
		
		try {
	        if (rs != null && rs.next()) {
	            return new Utilizador(rs.getString("util_nome"), rs.getString("util_username"), rs.getString("util_password"), rs.getInt("util_estado"), 
            			rs.getString("util_email"), rs.getString("util_tipo"), rs.getString("util_foto"));
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null; 
		
	}
	
	/**
	 * Método que permite alterar na base de dados os dados de um utilizador recebendo o ID sobre o qual irá alterar os dados, e o objeto Utilizador com os dados que serão colocados na base de dados.
	 * Este método usa polimorfismo para garantir a atualização de todos os dados dependendo do tipo de conta do utilizador.
	 * @param aUtilizador - objeto Utilizador com os dados atualizados.
	 * @param aID - ID do utilizador onde serão aplicadas as alterações.
	 * @return true caso as alterações tenham tido sucesso, false caso contrário.
	 */
	public boolean alterarDados(Utilizador aUtilizador, int aID) {
	    
	    StringBuffer sqlQuery = new StringBuffer("UPDATE utilizador SET util_nome = ?, util_username = ?, util_password = ?, util_email = ?, util_foto = ? ");
	    sqlQuery.append(" WHERE util_id = ?");
	    int resultadoUpdateUtil = gestorLigacoes.executarUpdate(sqlQuery, aUtilizador.getNome(), aUtilizador.getUsername(), aUtilizador.getPassword(), aUtilizador.getEmail(), aUtilizador.getFoto(), aID);

	    if (resultadoUpdateUtil == -1) 
	    	return false; 
	    
	    if (aUtilizador instanceof Cliente) {
	    	Cliente aCliente = (Cliente) aUtilizador;
	        StringBuffer sqlCliente = new StringBuffer("UPDATE cliente SET cli_nif = ?, cli_contacto = ?, cli_morada = ?, cli_setor_atividade = ?, cli_escalao = ? ");
	        sqlCliente.append(" WHERE utilizador_util_id = ?");
	        
	       	int resultado = gestorLigacoes.executarUpdate(sqlCliente, aCliente.getNIF(), aCliente.getContacto(), aCliente.getMorada(), aCliente.getSetorAtividade(), aCliente.getEscalao(), aID);
	       	
	       	if (resultado == -1)
	       		return false;

	    } else if (aUtilizador instanceof Funcionario) {
	    	Funcionario aFuncionario = (Funcionario) aUtilizador;
	        StringBuffer sqlFuncionario = new StringBuffer("UPDATE funcionario SET func_nif = ?, func_contacto = ?, func_morada = ?, func_especializacao = ?, func_data_inicio_atividade = ? ");
	        sqlFuncionario.append(" WHERE utilizador_util_id = ?");
	        
	        int resultado = gestorLigacoes.executarUpdate(sqlFuncionario, aFuncionario.getNIF(), aFuncionario.getContacto(), aFuncionario.getMorada(), aFuncionario.getEspecializacao(), 
	        		aFuncionario.getDataInicioAtividade(),aID);
	        
	        if (resultado == -1)
	       		return false;
	    }
	    return true;
	}	
	
	/**
	 * Método responsável por verificar na bases de dados se já existe um username igual ao recebido como parâmetro.
	 * @param aUsername - String com o username recebido para verificar na base de dados.
	 * @return true se encontrar o username na base de dados, false caso contrário.
	 */
	public boolean existeUsername(String aUsername) {
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM utilizador WHERE util_username = ? LIMIT 1");
	    
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aUsername);
	    
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
	
	/**
	 * Método responsável por verificar na bases de dados se já existe um email igual ao recebido como parâmetro.
	 * @param aEmail - String com o email recebido para verificar na base de dados.
	 * @return true se encontrar o email na base de dados, false caso contrário.
	 */
	public boolean existeEmail(String aEmail) {
	    
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM utilizador WHERE util_email = ? LIMIT 1");
	    
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aEmail);
	    
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
	
	/**
	 * Método responsável por verificar na bases de dados se já existe um NIF igual ao recebido como parâmetro.
	 * @param aNIF - nº inteiro com o NIF recebido para verificar na base de dados.
	 * @return true se encontrar o NIF na base de dados, false caso contrário.
	 */
	public boolean existeNIF(int aNIF) {
	    boolean existe = false;
	    
	    
	    StringBuffer sqlCli = new StringBuffer("SELECT cli_nif FROM cliente WHERE cli_nif = ? LIMIT 1");
	    ResultSet rsCli = gestorLigacoes.executarSelect(sqlCli, aNIF);
	    
	    try {
	        if (rsCli != null && rsCli.next()) {
	            existe = true;
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rsCli);
	    }

	    if (!existe) {
	        StringBuffer sqlFunc = new StringBuffer("SELECT func_nif FROM funcionario WHERE func_nif = ? LIMIT 1");
	        ResultSet rsFunc = gestorLigacoes.executarSelect(sqlFunc, aNIF);
	        
	        try {
	            if (rsFunc != null && rsFunc.next()) {
	                existe = true;
	            }
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	        } finally {
	        	gestorLigacoes.fecharResultSet(rsFunc);
	        }
	    }

	    return existe;
	}
	
	/**
	 * Método responsável por encontrar e devolver o ID do utilizador pelo seu username.
	 * @param aUsername - String com o username do utilizador a verificar.
	 * @return o nº inteiro com o ID do utilizador se encontrar, null caso contrário.
	 */
	public Integer procurarIDPorUsername(String aUsername) {
	    if (aUsername == null) 
	    	return null;

	    StringBuffer sql = new StringBuffer("SELECT util_id FROM utilizador WHERE util_username = ?");
	    ResultSet rs = gestorLigacoes.executarSelect(sql, aUsername);

	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("util_id");
	        }
	    } catch (SQLException sqle) {
	    	return null;
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null;
	}
	
	/**
	 * Método que permite remover um utilizador caso a inserção posterior dos dados de funcionário / cliente não tenha sucesso.
	 * @param aID - ID do utilizador a remover
	 */
	private void removerUtilizador(int aID) {
	    StringBuffer sqlQuery = new StringBuffer("DELETE FROM utilizador WHERE util_id = ?");
	    
	    gestorLigacoes.executarUpdate(sqlQuery, aID);
	    
	}
}



