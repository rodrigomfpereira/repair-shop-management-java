package ap3.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe responsável pela gestão do ficheiro de configuração da base de dados.
 * Permite manipular o acesso à base de dados através de um ficheiro de propriedades. 
 * @author RodrigoPereira
 */
public class GereFicheiroProperties {
	private Properties props = new Properties();
    private final String ficheiro = "config.properties";
    
    
    /**
     * Verifica se o ficheiro de configuração já existe no sistema de ficheiros.
     * @return true se o ficheiro existir e for um ficheiro válido, false caso contrário.
     */
    public boolean existeFicheiro() {
        File f = new File(ficheiro);
        return f.exists() && f.isFile();
    }
    
    
    /**
     * Carrega as definições do ficheiro na memória.
     * @return O objeto Properties contendo os parametros de acesso.
     */
    public Properties carregarDefinicoes() {
    	try (FileInputStream in = new FileInputStream(ficheiro)) {
            props.load(in);
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        	props.clear();
        }
        return props;
    }
    
 
    /**
     * Grava os parâmetros de acesso à base de dados no ficheiro de configuração.
     * @param aIP - Endereço IP ou Host do servidor da base de dados.
     * @param aPorto - Porto de ligação
     * @param aDB - Nome da base de dados
     * @param aUser - Utilizador para autenticação
     * @param aPass - Password para autenticação
     * @return true se gravou com sucesso, false caso contrário.
     */
    public boolean gravarDefinicoes(String aIP, String aPorto, String aDB, String aUser, String aPass) {
        try (FileOutputStream fos = new FileOutputStream(ficheiro)) {
            props.setProperty("db.ip", aIP);
            props.setProperty("db.porto", aPorto);
            props.setProperty("db.nome", aDB);
            props.setProperty("db.user", aUser);
            props.setProperty("db.pass", aPass);
            
            props.store(fos, "Configuracoes BD");
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
    
}
