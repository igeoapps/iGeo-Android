package com.bitcliq.igeo.core.datasource;

import java.util.HashMap;


/**
 * Esta classe é útil na medida em que é utilizada para guardar os ultimos itens visitados. Ao tentar aceder
 * a um item a primeira coisa a fazer é verificar se ele já se encontra nesta estrutura, e em caso afirmativo,
 * é apresentada a informação em cache.
 * A limpeza desta cache é feita sempre que se atinge um valor máximo. No entanto,
 * de futuro seria preferível implementar um que limpasse apenas os itens mais antigos deixando
 * os mais recentes disponíveis.
 * @author Bitcliq, Lda
 *
 */
public class IGEODataItemsCache {
	
	/**
	 * Número máximo de elementos a serem guardados
	 */
	private static final int MAX_ELEMENTS = 50;
	
	/**
	 * HashMap que contém os itens, tendo como chave o seu identificador
	 */
	private static HashMap<String, IGEOGenericDataItem> items;
	
	
	/**
	 * Método usado para obter um item passando o seu identificador.
	 * @param ID ID do item a obter.
	 * @return Devolve o item pretendido caso este se encontre guardado na estrutura ou
	 * null caso contrário.
	 */
	public static IGEOGenericDataItem getItem(String ID){
		if(items!=null){
			return items.get(ID);
		}
		
		return null;
	}
	
	
	/**
	 * Adiciona um item à lista de itens.
	 * @param item Item a adicionar.
	 */
	public static void addItem(IGEOGenericDataItem item){
		
		if(items==null)
			items = new HashMap<String, IGEOGenericDataItem>();
		
		//O ideal será posteriormente só apagar um certo numero de itens
		if(items.size()>MAX_ELEMENTS){
			clear();
		}
		
		items.put(item.itemID, item);
	}
	
	
	/**
	 * Apaga da estrutura todos os itens carregados até ao momento.
	 */
	public static void clear(){
		items.clear();
	}

}
