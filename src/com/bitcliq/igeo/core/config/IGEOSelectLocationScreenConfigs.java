package com.bitcliq.igeo.core.config;

import java.io.Serializable;



/**
 * Configurações de um ecrã para a escolha da localização no botão "Explore".
 * @author Bitcliq, Lda.
 *
 */

public class IGEOSelectLocationScreenConfigs implements IGEOScreenConfig, Serializable {
	
	/**
	 * URL da imagem de topo
	 */
	public String urlTopImage;
	
	/**
	 * Configurações do botão de seleção do distrito, concelho e freguesia
	 */
	public IGEOButtonElementConfig bSelection;
	
	/**
	 * Configurações do botão de pesquisa
	 */
	public IGEOButtonElementConfig bSearch;
	
	
	
	

	/**
	 * Construtor por defeito.
	 */
	public IGEOSelectLocationScreenConfigs(){
		super();
		
	}
	
}

