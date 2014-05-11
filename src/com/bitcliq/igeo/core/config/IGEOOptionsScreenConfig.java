package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Configurações de um ecrã de opções onde são escolhidas as categorias a apresentar.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOOptionsScreenConfig implements IGEOScreenConfig, Serializable 
{
	
	/**
	 * Configuração usada para os botões de opções.
	 * A cada chave do tipo srcID_catID corresponde a configuração de um botão
	 */
	public HashMap<String,IGEOButtonElementConfig> buttonsOptionConf;
	
	/**
	 * Configuração do botão para ida para o mapa ou lista.
	 */
	public IGEOButtonElementConfig okButtonConfig;
	
	/**
	 * URL da imagem de topo
	 */
	public String urlTopImage;
	
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEOOptionsScreenConfig(){
		super();
		
		buttonsOptionConf = new HashMap<String,IGEOButtonElementConfig>();
	}
	
}

