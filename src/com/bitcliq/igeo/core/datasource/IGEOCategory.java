package com.bitcliq.igeo.core.datasource;

import java.io.Serializable;


/**
 * Esta classe contém informação uma categoria de uma fonte de dados
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOCategory implements Serializable
{
	/**
	 * Identificador único de uma categoria.
	 */
	public String categoryID;
	
	/**
	 * Nome da categoria.
	 */
	public String categoryName;
	
	/**
	 * URL usado para aceder à categoria.
	 */
	public String categoryURL;
	
	/**
	 * Nome do ícone usado no ecrã de categorias para representar a mesma. Caso este nome não seja precedido
	 * de "/" será considerado um ficheiro com este nome nos resources.
	 */
	public String icon;
	
	/**
	 * Nome do ícone (modo selecionado) no ecrã de categorias para representar a mesma quando esta está selecionada. Caso este nome não seja precedido
	 * de "/" será considerado um ficheiro com este nome nos resources.
	 */
	public String iconSelected;
	
	
	
	
	/**
	 * Construtor por defeito da classe IGEOCategory.
	 */
	public IGEOCategory(){
		super();
	}
	
	
	
	/**
	 * Devolve numa string uma representação da categoria atual
	 */
	public String toString(){
		String result = "";
		
		result = "IGEOCategory (ID="+categoryID+", name="+categoryName+", icon="+icon+", iconSelected="+iconSelected+")";
		
		return result;
	}
	
	
	/**
	 * Compara o ID de uma outra categoria com a categoria atual. Isto é útil para ordenar os itens por categoria na lista.
	 * @param x ID da categoria com a qual pretendemos fazer a comparação.
	 * @return 0 se a categoria a comparar tiver um ID null, 1 caso o ID da categoria atual seja maior, e -1 caso o id da categoria atual seja menor.
	 */
	public int compareID(String x){
		if(x==null)
			return 0;
		else {
			int id = Integer.parseInt(categoryID);
			int xInt = Integer.parseInt(x);
			
			if(id>xInt)
				return 1;
			else
				return -1;
		}
	}
	
}

