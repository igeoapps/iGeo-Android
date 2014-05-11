package com.bitcliq.igeo.core.datasource;

import java.io.Serializable;


/**
 * Classe que representa o detalhe de um item.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public abstract class IGEODataItem  implements Serializable
{
	/**
	 * Usado para identificar de forma única o item.
	 */
	public String itemID;
	
	/**
	 * Titulo do item a ser apresentado na lista, balão do mapa e detalhes.
	 */
	public String title;
	
	/**
	 * URL de acesso ao item.
	 */
	public String itemURL;
	
	/**
	 * Pode conter um texto simples ou HTML acerca do item.
	 */
	public String textOrHTML;
	
	/**
	 * ID da categoria a que o item pertence.
	 */
	public String categoryID;

}

