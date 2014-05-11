package com.bitcliq.igeo.core.config;

import java.io.Serializable;




/**
 * Configurações de um ecrã para apresentação das fontes numa lista.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOListSourcesScreenConfig implements IGEOScreenConfig, Serializable 
{
	
	/**
	 * URL da imagem de topo.
	 */
	public String urlTopImage;
	
	/**
	 * Cor da seleção dos itens da lista.
	 */
	public String colorSelection;
	
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEOListSourcesScreenConfig(){
		super();
	}
	
}

