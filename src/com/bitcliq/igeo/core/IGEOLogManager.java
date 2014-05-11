package com.bitcliq.igeo.core;

import com.bitcliq.igeo.core.logs.IGEOLogger;


/**
 * Classe usada para gerir os registos da App. Ainda não implementada
 * O objetivo é que esta classe seja utilizada para registar
 * logs, bem como os seus uploads para o servidor
 * @author Bitcliq, Lda.
 *
 */

public class IGEOLogManager
{
	/**
	 * Objeto usado para registar eventos.
	 */
	private IGEOLogger appLogger;
	
	
	/**
	 * Construtor por defeito da classe IGEOLogManager
	 */
	public IGEOLogManager(){
		super();
	}

	
	/**
	 * Regista acções sobre conteúdos.
	 * @param screenName Nome do ecrã atual
	 * @param sourceID ID da source atual
	 * @param categoryID ID da categoria atual
	 * @param itemID ID do item atual caso se aplique
	 * @param action Nome da ação efetuada
	 */
	public static void logAppEvent(String screenName, String sourceID, String categoryID, String itemID, String action) {
		
	}
	
}

