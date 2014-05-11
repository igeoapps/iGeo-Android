package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.HashMap;



/**
 * Configurações de um ecrã para apresentação de itens numa lista.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOListScreenConfig implements IGEOScreenConfig, Serializable 
{
	
	/**
	 * URL da imagem de fundo 
	 */
	public HashMap<String,String> bgForSource;
	
	/**
	 * Cor da seleção dos itens da lista
	 */
	public String colorSelection;
	
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEOListScreenConfig(){
		super();
	}
	
}

