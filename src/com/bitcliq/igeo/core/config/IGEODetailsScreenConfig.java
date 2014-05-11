package com.bitcliq.igeo.core.config;

import java.io.Serializable;




/**
 * Configurações de um ecrã para apresentação dos detalhes de um item.
 * @author Bitcliq, Lda.
 *
 */
public class IGEODetailsScreenConfig implements IGEOScreenConfig, Serializable 
{

	/**
	 * URL da imagem por defeito
	 */
	public String defaultImageURL;
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEODetailsScreenConfig(){
		super();
	}
	
}

