package ap3.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Classe respons�vel por gerir as repara��es da aplica��o.
 * @author RodrigoPereira
 */
public class GereReparacoes {

	GereLigacoes gestorLigacoes = new GereLigacoes(); 
	GereNotificacoes gestorNotificacoes = new GereNotificacoes();
	
	/**
	 * M�todo respons�vel por efetuar o registo de uma nova repara��o na base de dados.
	 * @param aReparacao - objeto Reparacao com os atributos a inserir na base de dados.
	 * @param aIDEquipamento - ID do equipamento ao qual a repara��o pertence.
	 * @param aIDUtilizador - ID do utilizador respons�vel pelo registo da repara��o.
	 * @return true se conseguir registar a repara��o e a notifica��o correspondente.
	 */
	public boolean registarReparacao(Reparacao aReparacao, int aIDEquipamento, Integer aIDUtilizador) {
		
		StringBuffer sqlQuery = new StringBuffer ("INSERT INTO reparacao (rep_num_reparacao, rep_data_criacao, rep_estado, rep_custo, equipamento_eq_id, rep_observacoes )");
		sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?) ");
		
		int resultadoInsRep = gestorLigacoes.executarUpdate(sqlQuery, aReparacao.getNumeroReparacao(), Timestamp.valueOf(aReparacao.getDataCriacao()), aReparacao.getEstado() ,aReparacao.getCustoProcesso(), aIDEquipamento, aReparacao.getObservacoes());
		
		
		if (resultadoInsRep != -1) {
			
			int maxChavePrimaria = gestorLigacoes.maxChavePrimaria("rep_id", "reparacao");
			
			Notificacao notif = new Notificacao(3, null);
			
			if (maxChavePrimaria != -1 && gestorNotificacoes.registarNotificacao(notif,null,maxChavePrimaria,aIDUtilizador))
				return true;
		}
		return false;
	}
	
	/**
	 * M�todo respons�vel por obter uma matriz bidimensional contendo todas as repara��es com o estado recebido.
	 * @param aEstado Estado da repara��o para filtrar a pesquisa 
	 * @return Uma matriz de Strings onde cada linha representa uma repara��o com os dados do equipamento inclu�dos.
	 */
	public String[][] obterMatrizReparacoesPorEstado(int aEstado) {
		ArrayList<String[]> linhas = new ArrayList<String[]>();
	    
		StringBuffer sqlQuery = new StringBuffer("SELECT r.rep_num_reparacao, r.rep_data_criacao, r.rep_data_fim, r.rep_tempo_decorrido, r.rep_custo, r.rep_observacoes, e.eq_marca, e.eq_modelo ");
		sqlQuery.append("FROM reparacao r, equipamento e ");
		sqlQuery.append("WHERE r.equipamento_eq_id = e.eq_id "); 
		sqlQuery.append("AND r.rep_estado = " + aEstado + " "); 
		sqlQuery.append("ORDER BY r.rep_data_criacao DESC");

		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

		try {
			while (rs != null && rs.next()) {
				String numReparacao = rs.getString("rep_num_reparacao");
				String dataCriacao = rs.getString("rep_data_criacao");
				
				String dataFim = rs.getString("rep_data_fim");
				if (dataFim == null || dataFim.trim().isEmpty()) {
					dataFim = "---";
				}
				
				String tempo = rs.getString("rep_tempo_decorrido");
				if (tempo == null || tempo.trim().isEmpty()) {
					tempo = "---";
				}
				
				String custo = rs.getString("rep_custo") + "�";
				
				String observacoes = rs.getString("rep_observacoes");
				if (observacoes == null || observacoes.trim().isEmpty()) {
					observacoes = "Sem observa��es.";
				}
				
				String equipamento = rs.getString("eq_marca") + " " + rs.getString("eq_modelo");

				linhas.add(new String[]{numReparacao, dataCriacao, dataFim, tempo, custo, observacoes, equipamento});
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}

		return linhas.toArray(new String[0][0]);
	}
	
	/**
	 * M�todo respons�vel por obter as repara��es associadas especificamente ao funcion�rio autenticado, quer pendentes de aceita��o, quer a decorrer.
	 * @param aIDFuncionario ID do funcion�rio autenticado no sistema
	 * @param aEstado Estado da repara��o
	 * @return Uma matriz de Strings onde cada linha representa uma repara��o e os dados do equipamento correspondente.
	 */
	public String[][] obterMatrizReparacoesFuncionarioPorEstado(int aIDFuncionario, int aEstado) {
		ArrayList<String[]> linhas = new ArrayList<String[]>();
	    
		StringBuffer sqlQuery = new StringBuffer("SELECT r.rep_num_reparacao, r.rep_data_criacao, r.rep_data_fim, r.rep_tempo_decorrido, r.rep_custo, r.rep_observacoes, e.eq_marca, e.eq_modelo ");
		sqlQuery.append("FROM reparacao r, equipamento e, funcionario_reparacao fr ");
		sqlQuery.append("WHERE r.equipamento_eq_id = e.eq_id ");
		sqlQuery.append("AND r.rep_id = fr.reparacao_rep_id ");
		sqlQuery.append("AND r.rep_estado = " + aEstado + " ");
		sqlQuery.append("AND fr.funcionario_utilizador_util_id = " + aIDFuncionario + " ");
		sqlQuery.append("ORDER BY r.rep_data_criacao DESC");

		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);

		try {
			while (rs != null && rs.next()) {
				String numReparacao = rs.getString("rep_num_reparacao");
				String dataCriacao = rs.getString("rep_data_criacao");
				
				String dataFim = rs.getString("rep_data_fim");
				if (dataFim == null || dataFim.trim().isEmpty()) { dataFim = "---"; }
				
				String tempo = rs.getString("rep_tempo_decorrido");
				if (tempo == null || tempo.trim().isEmpty()) { tempo = "---"; }
				
				String custo = rs.getString("rep_custo") + "�";
				
				String observacoes = rs.getString("rep_observacoes");
				if (observacoes == null || observacoes.trim().isEmpty()) { observacoes = "Sem observa��es."; }
				
				String equipamento = rs.getString("eq_marca") + " " + rs.getString("eq_modelo");

				linhas.add(new String[]{numReparacao, dataCriacao, dataFim, tempo, custo, observacoes, equipamento});
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			gestorLigacoes.fecharResultSet(rs);
		}

		return linhas.toArray(new String[0][0]);
	}
	
	/**
	 * M�todo respons�vel por efetuar uma pesquisa � base de dados acerca das repara��es por finalizar � mais de dez dias.
	 * Ap�s encontrar as repara��es o m�todo dever� verificar se j� foi criada uma notifica��o para cada uma delas, atualizando ent�o a notifica��o para n�o lida.
	 * Caso n�o exista uma notifica��o, a mesma dever� ser criada.
	 */
	public void verificarReparacoesAtrasadas() {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT rep_id FROM reparacao ");
		sqlQuery.append("WHERE rep_estado NOT IN (5, 6) ");
		sqlQuery.append("AND rep_data_criacao < DATE_SUB(NOW(), INTERVAL 10 DAY)");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery);
		
		try {
	        while (rs != null && rs.next()) {
	            int idRep = rs.getInt("rep_id");
	            

	            if (verificarNotificacaoTipoExistente(idRep, 8)) {
	                // Se existe, marco como n�o lida
	                atualizarNotificacaoParaNaoLida(idRep, 8);
	            } else {
	                // Se n�o existe, crio uma nova
	                criarNotificacaoAtraso(idRep, 8);
	            }
	        }
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	}
	
	/**
	 * M�todo respons�vel por efetuar uma pesquisa � base de dados de forma a verificar se existe uma espec�fica repara��o com um determinado tipo.
	 * @param aIDReparacao - ID da repara��o a verificar.
	 * @param aTipo - tipo da notifica��o para restringir a pesquisa.
	 * @return true se existe uma notifica��o que tenha os requisitos necess�rios, false caso contr�rio.
	 */
	private boolean verificarNotificacaoTipoExistente(int aIDReparacao, int aTipo) {
	    boolean existe = false;
	    StringBuffer sqlQuery = new StringBuffer("SELECT 1 FROM notificacao WHERE reparacao_rep_id = ? AND not_tipo = ?");
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDReparacao, aTipo);

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
	 * M�todo que permite atualizar uma nofica��o, garantido que � do tipo recebido como par�metro, colocando-a de novo no estado 'n�o lida'.
	 * @param aIDReparacao - ID da repara��o a atualizar.
	 * @param aTipo - tipo da repara��o para garantir seguran�a extra.
	 */
	private void atualizarNotificacaoParaNaoLida(int aIDReparacao, int aTipo) {
	    StringBuffer sqlQuery = new StringBuffer("UPDATE notificacao SET not_lida = ? "); 
	    sqlQuery.append("WHERE reparacao_rep_id = ? AND not_tipo = ?");
	    
	    gestorLigacoes.executarUpdate(sqlQuery, false, aIDReparacao, aTipo);
	}
	
	/**
	 * M�todo respons�vel por criar uma nova notifica��o caso uma repara��o esteja � mais de 10 dias sem ser finalizada.
	 * @param aIDReparacao - ID da repara��o a ser colocada na notifica��o.
	 * @param aTipo - tipo da notifica��o que ser� criada.
	 */
	private void criarNotificacaoAtraso(int aIDReparacao, int aTipo) {
	    StringBuffer sqlQuery = new StringBuffer("INSERT INTO notificacao (not_tipo, reparacao_rep_id, not_lida) ");
	    sqlQuery.append("VALUES (?, ?, ?)");
	    
	    gestorLigacoes.executarUpdate(sqlQuery, aIDReparacao, aTipo, false);
	}
	
	/**
	 * M�todo respons�vel por finalizar a repara��o, alterando o estdo da mesma, adicionando a data do momento da finaliza��o e calculando o tempo decorrido.
	 * O c�lculo do tempo decorrido � efetuado na base de dados.
	 * @param aIDReparacao - ID da repara��o a finalizar.
	 * @return true se a repara��o for finalizada, false caso contr�rio.
	 */
	public boolean finalizarReparacao(int aIDReparacao) {
		
		StringBuffer sqlQuery = new StringBuffer ("UPDATE reparacao SET rep_estado = 5, rep_data_fim = ?, rep_tempo_decorrido=TIMEDIFF(?, rep_data_criacao) ");
		sqlQuery.append("WHERE rep_id = ?");
		
		LocalDateTime agora = LocalDateTime.now();
		
		int resultado = gestorLigacoes.executarUpdate(sqlQuery, agora, agora, aIDReparacao);
		
		if (resultado != -1) {
			return true;
		}
		return false;
	}
	
	/**
	 * M�todo respons�vel por incrementar o custo de uma repara��o atrav�s do seu ID.
	 * @param aIDReparacao - ID usado para restringir a atualiza��o a uma repara��o.
	 * @param aCusto - Custo a ser adicionado ao existente da repara��o
	 * @return true caso a altera��o tenha tido sucesso, false caso contr�rio.
	 */
	public boolean alterarCustoReparacao(int aIDReparacao, float aCusto) {
		
		StringBuffer sqlQuery = new StringBuffer("UPDATE reparacao SET rep_custo = rep_custo + ? ");
		sqlQuery.append("WHERE rep_id = ?");
		
		int resultado = gestorLigacoes.executarUpdate(sqlQuery, aCusto, aIDReparacao);
		
		if (resultado != -1) {
			return true;
		}
		return false;
	}
	
	/**
	 * M�todo respons�vel por incrementar o custo de uma repara��o e adicionar observa��es atrav�s do seu ID.
	 * @param aIDReparacao - ID usado para restringir a pesquisa a uma repara��o.
	 * @param aCusto - Custo a ser adicionado ao existente da repara��o.
	 * @param aObservacoes - Observa��es a serem adicionadas � repara��o.
	 * @return true caso a altera��o tenha tido sucesso, false caso contr�rio.
	 */
	public boolean alterarCustoObservacoesReparacao(int aIDReparacao, float aCusto, String aObservacoes) {
		
		StringBuffer sqlQuery = new StringBuffer("UPDATE reparacao SET rep_custo = rep_custo + ?, rep_observacoes = ? ");
		sqlQuery.append("WHERE rep_id = ?");
		
		int resultado = gestorLigacoes.executarUpdate(sqlQuery, aCusto, aObservacoes, aIDReparacao);
		
		if (resultado != -1) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * M�todo respons�vel por permitir ao funcion�rio aceitar a atribui��o de um pedido de repara��o, alterando o estado da mesma para 'aceite'.
	 * @param aIDReparacao - ID da repara��o na atribui��o.
	 * @param aIDFuncionario - ID do funcion�rio a aceitar responsabilidade.
	 * @param aNumReparacao - n� de repara��o usado para alterar o estado da mesma.
	 * @return true caso tenha conseguido atualizar a tabela onde o funcion�rio est� associado � repara��o e o estado da repara��o, false caso contr�rio.
	 */
	public boolean aceitarPedidoReparacao(int aIDReparacao, int aIDFuncionario, String aNumReparacao) {

	    StringBuffer sqlQuery = new StringBuffer("UPDATE funcionario_reparacao SET funcrep_aceite = 2 ");
	    sqlQuery.append("WHERE reparacao_rep_id = ? AND funcionario_utilizador_util_id = ?");
	    
	    int resultado = gestorLigacoes.executarUpdate(sqlQuery, aIDReparacao, aIDFuncionario);

	    if (resultado != -1) {
	        return alterarEstado(aNumReparacao, 4);
	    }
	    
	    return false;
	}
	
	/**
	 * M�todo respons�vel por permitir ao funcion�rio negar a atribui��o de um pedido de repara��o, alterando o estado da mesma para 'n�o aceite'.
	 * @param aIDReparacao - ID da repara��o na atribui��o.
	 * @param aIDFuncionario - ID do funcion�rio a negar responsabilidade.
	 * @return true se foi poss�vel atualizar o estado, false caso contr�rio
	 */
	public boolean negarPedidoReparacao(int aIDReparacao, int aIDFuncionario) {
		
	    StringBuffer sqlQuery = new StringBuffer("UPDATE funcionario_reparacao SET funcrep_aceite = 1 ");
	    sqlQuery.append("WHERE reparacao_rep_id = ? AND funcionario_utilizador_util_id = ?");
	    
	    int resultado = gestorLigacoes.executarUpdate(sqlQuery, aIDReparacao, aIDFuncionario);

	    if (resultado != -1) {
	        return true;
	    }
	    
	    return false;
	}
	
	/**
	 * M�todo que permite verificar se o ID do funcion�rio recebido como par�metro corresponde ao ID na repara��o com ID tamb�m recebido por par�metro.
	 * @param aIDReparacao - ID da repara��o a verificar
	 * @param aIDFuncionario - ID do funcion�rio a comparar com o ID presente na repara��o
	 * @return true se tiver encontrado a repara��o e o ID do funcion�rio corresponda, false caso contr�rio.
	 */
	public boolean verificarResponsavelReparacao(int aIDReparacao, int aIDFuncionario) {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT funcionario_utilizador_util_id AS func_ID FROM funcionario_reparacao ");
		sqlQuery.append("WHERE reparacao_rep_id = ? ");
		sqlQuery.append("AND funcrep_aceite = 2");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDReparacao);
				
		try {
			if (rs != null && rs.next()) {
				if (rs.getInt("func_ID") == aIDFuncionario)
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
	 * M�todo que permite verificar se uma atribui��o de repara��o a funcion�rio ainda tem o estado inicial 'pendente'.
	 * @param aIDReparacao - ID da repara��o a verificar
	 * @param aIDFuncionario - ID do funcion�rio a verificar o estado da resposta.
	 * @return true caso o estado da atribui��o entre o funcion�rio e a repara��o ainda esteja pendente, false caso contr�rio.
	 */
	public boolean verificarReparacaoPendenteParaFuncionario(int aIDReparacao, int aIDFuncionario) {
	    // 0 = Pendente na tabela funcionario_reparacao
	    StringBuffer sqlQuery = new StringBuffer("SELECT funcrep_aceite FROM funcionario_reparacao ");
	    sqlQuery.append("WHERE reparacao_rep_id = ? AND funcionario_utilizador_util_id = ? AND funcrep_aceite = 0");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aIDReparacao, aIDFuncionario);
	    try {
	        if (rs != null && rs.next())
	        	return true;
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return false;
	}
	
	/**
	 * M�todo respons�vel por pesquisar e devolver da base de dados uma repara��o procurando pelo numero de repara��o recebido como par�metro.
	 * @param aNumReparacao - n� de repara��o usado para restringir a pesquisa da base de dados.
	 * @return a repara��o instanciada a partir dos dados obtidos da base de dados, ou null caso tenha ocorrido um erro.
	 */
	public Reparacao devolverReparacao(String aNumReparacao) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT * FROM reparacao WHERE rep_num_reparacao = ? LIMIT 1");

	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aNumReparacao);

	    try {
	        if (rs != null && rs.next()) {
	            return new Reparacao(rs.getString("rep_num_reparacao"),rs.getTimestamp("rep_data_criacao").toLocalDateTime(),rs.getInt("rep_estado"), rs.getString("rep_observacoes"));
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null;
	}
	
	/**
	 * M�todo respons�vel por pesquisar e devolver da base de dados o ID do equipamento sobre o qual a repara��o foi pedida, procurando pelo n� de repara��o recebido como par�metro.
	 * @param aNumReparacao - n� de repara��o usado para restringir a pesquisa da base de dados.
	 * @return o n� inteiro correspondente ao id do equipamento, ou -1 caso tenha ocorrido algum erro.
	 */
	public int devolverIDEquipamento(String aNumReparacao) {
		
		StringBuffer sqlQuery = new StringBuffer("SELECT equipamento_eq_id FROM reparacao WHERE rep_num_reparacao = ? LIMIT 1");
		
		ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aNumReparacao);
		
		try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("equipamento_eq_id");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return -1;
	}
	
	/**
	 * M�todo respons�vel por pesquisar e devolver da base de dados o estado atual de uma repara��o, procurando pelo n� de repara��o recebido como par�metro.
	 * @param aNumReparacao - n� de repara��o usado para restringir a pesquisa da base de dados.
	 * @return o n� inteiro correspondente ao estado da repara��o, ou -1 caso tenha ocorrido algum erro.
	 */
	public int devolverEstado(String aNumReparacao) {
	    StringBuffer sqlQuery = new StringBuffer("SELECT rep_estado FROM reparacao WHERE rep_num_reparacao = ? LIMIT 1");
	    
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aNumReparacao);

	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("rep_estado");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return -1;
	}
	
	/**
	 * M�todo que permite alterar de estado uma repara��o, recebendo o n� da repara��o e o estado como par�metros.
	 * @param aNumReparacao - n� de repara��o usado para restringir a pesquisa da base de dados.
	 * @param aEstado - estado a colocar na repara��o encontrada.
	 * @return true se foi efetuada a altera��o com sucesso, false caso contr�rio.
	 */
	public boolean alterarEstado(String aNumReparacao, int aEstado) {
	    int estadoAtual = devolverEstado(aNumReparacao);

	    if (estadoAtual != -1 && estadoAtual != aEstado) {
	        StringBuffer sqlQuery = new StringBuffer("UPDATE reparacao SET rep_estado = ? WHERE rep_num_reparacao = ?");
	        
	        int resultado = gestorLigacoes.executarUpdate(sqlQuery, aEstado, aNumReparacao);
	        
	        if (resultado != -1)
	        	return true;
	    }

	    return false;
	}

	/**
	 * M�todo que permite devolver o estado da resposta da atribui��o de uma repara��o a um funcion�rio, recebendo ambos os IDs como par�metros.
	 * @param aIdReparacao - ID da repara��o usado para restringir a pesquisa da base de dados.
	 * @param aIdFuncionario - ID do funcion�rio ao qual ser� devolvido o estado de atribui��o.
	 * @return o n� inteiro correspondente ao estado da atribui��o ao funcion�rio, ou -1 caso tenha ocorrido algum erro.
	 */
	public int verificarRespostaFuncionario(int aIdReparacao, int aIdFuncionario) {
	    StringBuffer sql = new StringBuffer("SELECT funcrep_aceite FROM funcionario_reparacao ");
	    sql.append("WHERE reparacao_rep_id = ? AND funcionario_utilizador_util_id = ? LIMIT 1");

	    ResultSet rs = gestorLigacoes.executarSelect(sql, aIdReparacao, aIdFuncionario);

	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("funcrep_aceite");
	        }
	    } catch (SQLException sqle) {
	        sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return -1; 
	}
	
	/**
	 * M�todo que permite atribuir um funcion�rio a uma repara��o pelo gestor, ficando a reposta do funcion�rio pendente.
	 * @param aIDReparacao - ID da repara��o usado para inserir na base de dados.
	 * @param aIDFuncionario - ID do funcionario usado para inserir na base de dados. 
	 * @return true caso a inser��o tenha tido sucesso, false caso contr�rio.
	 */
	public boolean atribuirFuncionarioReparacao(Integer aIDReparacao, Integer aIDFuncionario) {
	    // O atributo funcrep_Aceite come�a em 0 (pendente)
	    StringBuffer sqlQuery = new StringBuffer("INSERT INTO funcionario_reparacao (funcrep_aceite, reparacao_rep_id, funcionario_utilizador_util_id) ");
	    sqlQuery.append("VALUES (0 , ?, ?)");

	    int resultado = gestorLigacoes.executarUpdate(sqlQuery, aIDReparacao, aIDFuncionario);

	    if (resultado != -1)
	    	return true;
	    return false;
	}
	
	/**
	 * M�todo que permite atrav�s do n� de repara��o recebido como par�metro devolver o ID da repara��o encontrada
	 * @param aNumReparacao - n� de repara��o usado para restringir a pesquisa da base de dados.
	 * @return o objeto Integer correspondente ao ID da repara��o, ou null caso tenha ocorrido um erro.
	 */
	public Integer procurarIDPorNumeroReparacao(String aNumReparacao) {
	    if (aNumReparacao == null) 
	        return null;
 
	    StringBuffer sqlQuery = new StringBuffer("SELECT rep_id FROM reparacao WHERE rep_num_reparacao = ?");
	    ResultSet rs = gestorLigacoes.executarSelect(sqlQuery, aNumReparacao);

	    try {
	        if (rs != null && rs.next()) {
	            return rs.getInt("rep_id");
	        }
	    } catch (SQLException sqle) {
	    	sqle.printStackTrace();
	    } finally {
	        gestorLigacoes.fecharResultSet(rs);
	    }
	    return null;
	}
	
	/**
	 * M�todo que permite gerar um n�mero de repara��o, constitu�do pelo seguinte n� ao n� de repara��es existentes concatenado com a data do momento em que � gerado, no formato 'yyyyMMddHHmmss'.
	 * @return a String correspondente ao n� de repara��o gerado.
	 */
	public String gerarNumeroReparacao() {
	    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	    LocalDateTime agora = LocalDateTime.now();
	    
	    StringBuffer sb = new StringBuffer();
	    
	    if (gestorLigacoes.isTabelaVazia("reparacao")) {
	        sb.append("1");
	    } else {
	        int maxChavePrimaria = gestorLigacoes.maxChavePrimaria("rep_id", "reparacao");
	        // Incrementa e adiciona ao buffer
	        sb.append(++maxChavePrimaria);
	    }
	    
	    // Adiciona a data formatada
	    sb.append(agora.format(formatador));
	    
	    return sb.toString();
	}
	
	
}
