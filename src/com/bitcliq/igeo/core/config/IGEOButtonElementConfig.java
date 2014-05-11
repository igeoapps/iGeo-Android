package com.bitcliq.igeo.core.config;

import java.io.Serializable;




/**
 * Configurações de um botão.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOButtonElementConfig implements IGEOScreenElementConfig, Serializable 
{
	/**
	 * Nome do botão.
	 */
	public String name;
	
	/**
	 * URL da imagem do estado normal do botão.
	 */
	public String imageNormalConfigs;
	
	/**
	 * URL da imagem do estado selecionado do botão.
	 */
	public String imageClickConfigs;
	
	
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEOButtonElementConfig(){
		super();
	}
	
	
	
	public String toString(){
		String s = "IGEOButtonElementConfig = (";
		
		s+="normal = "+this.imageNormalConfigs;
		s+=", clicked = "+this.imageClickConfigs;
		s+=")";
		
		return s;
	}
	
}

