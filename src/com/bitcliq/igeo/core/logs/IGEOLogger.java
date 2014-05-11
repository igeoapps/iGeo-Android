package com.bitcliq.igeo.core.logs;


/**
 * Classe com métodos estáticos usada para registar e fazer upload para o servidor dos ultimos logs da aplicação.</br>
 * Nesta versão esta funcionalidade não está implementada.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOLogger
{
	/**
	 * Define se os logs estão ou não ativados.
	 */
	public static boolean isLogsAtivated;
	
	
	/**
	 * Construtor por defeito da classe IGEOLogger
	 */
	public IGEOLogger(){
		super();
	}

	
	/**
	 * Regista a data, o título do ecrã, a fonte, a categoria, o identificador do item (se aplicável)
	 * e a ação efetuada. No caso de algum dos parâmetros não se aplicar á ação, o seu valor é null.
	 * @param screenName Nome do ecrã atual
	 * @param sourceID ID da fonte atual
	 * @param categoryID ID da categoria atual
	 * @param itemID ID do item atual (se aplicável)
	 * @param action Nome da ação efetuada
	 */
	public static void logAppEvent(String screenName, String sourceID, String categoryID, String itemID, String action) {
		
	}
	
	
	/**
	 * Faz o upload dos logs existentes no dispositivo para o servidor.
	 * @return true ou false consoante o upload foi bem sucedido ou não
	 */
	public boolean uploadLastLogs() {
		
		return false;	
	}
	
}

