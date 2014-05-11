package com.bitcliq.igeo.core.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Esta classe contém a informação sobre uma fonte de dados.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOSource implements Serializable
{
	/**
	 * Usado para identificar a fonte de forma única
	 */
	public String sourceID;

	/**
	 * Nome da categoria.
	 */
	public String sourceName;

	/**
	 * URL usado para aceder à lista de categorias da fonte.
	 */
	public String sourceURL;

	/**
	 * hashMap de categorias da source indexadas pelo seu identificador.
	 */
	public HashMap<String,IGEOCategory> categoryHashMap;
	
	/**
	 * Cor associada à fonte (quando aplicável). Neste momento não existem fontes com uma cor associada
	 * mas poderá vir a ser útil de futuro.
	 */
	public String color;
	
	/**
	 * Usada para receber do servidor o url da imagem a usar como fundo da home quando
	 * estamos a vizualizar dados de uma fonte.
	 */
	public String imageURL;
	
	/**
	 * Subtítulo / legenda da fonte. Este atributo será usado para apresentar a informação
	 * sobre a fonte de forma a indicar ao utilizador a que ela se refere. Esta informação será apresentada
	 * no subtítulo da lista de fontes.
	 */
	public String srcSubTitle;


	
	
	


	/**
	 * Construtor por defeito da classe IGEOSource
	 */
	public IGEOSource(){
		super();
	}

	/**
	 * Construtor da classe IGEOSource com um id, título, o url de acesso à mesma e subtítulo.
	 * @param id Identificador único da fonte
	 * @param srcName Nome da fonte
	 * @param url URL usado para aceder à fonte
	 * @param subTitle Subtitulo ou legenda da fonte
	 */
	public IGEOSource(String id, String srcName, String url, String subTitle){
		super();

		this.sourceID = id;
		this.sourceName = srcName;
		this.sourceURL = url;
		this.categoryHashMap = new HashMap<String,IGEOCategory>();
		this.srcSubTitle = subTitle;
	}

	/**
	 * Construtor da classe IGEOSource com um id, título e o url de acesso. 
	 * @param id Identificador único da fonte
	 * @param srcName Nome da fonte
	 * @param url URL usado para aceder à fonte
	 * @param catList Lista de categorias da fonte
	 * @param subTitle Subtitulo ou legenda da fonte
	 */
	public IGEOSource(String id, String srcName, String url, ArrayList<IGEOCategory> catList, String subTitle){
		super();

		this.sourceID = id;
		this.sourceName = srcName;
		this.sourceURL = url;
		this.categoryHashMap = new HashMap<String,IGEOCategory>();

		for(IGEOCategory cat : catList){
			categoryHashMap.put(cat.categoryID, cat);
		}
		
		this.srcSubTitle = subTitle;
		
	}








	//OBTENÇÃO DAS CATEGORIAS #######################################################
	/**
	 * Retorna uma categoria da lista de categorias da source através do seu ID
	 * @param ID ID da categoria
	 * @return Categoria da sorce
	 */
	public IGEOCategory getCategoryByID(String ID){

		if(categoryHashMap!=null){
			if(categoryHashMap.containsKey(ID)){
				return categoryHashMap.get(ID);
			}
		}

		return null;
	}
	
	
	/**
	 * Associa á fonte uma lista de categorias indexando-as num HashMap pelo seu ID.
	 * @param categories ArrayList de categorias a adicionar.
	 */
	public void setCategories(ArrayList<IGEOCategory> categories){
		
		categoryHashMap = new HashMap<String, IGEOCategory>();
		
		for(IGEOCategory c : categories){
			categoryHashMap.put(c.categoryID, c);
		}
		
	}
	
	
	/**
	 * Retorna uma representação em texto da fonte.
	 */
	public String toString(){
		String result = "";
		
		result = "IGEOSource (ID="+sourceID+", name="+sourceName+", color="+color+", imageURL="+imageURL+"\nCategories:\n";
		for(IGEOCategory c : categoryHashMap.values()){
			result += "\t"+c.toString()+"\n";
		}
		
		result+=")\n\n";
		
		return result;
	}
	
	
	/**
	 * Faz a comparação entre duas fontes. Isto permite fazer uma ordenação das fontes por ordem de ID
	 * na lista de sources.
	 * @param x ID da fonte com a qual queremos comparar a fonte atual.
	 * @return 0 se a fonte a comparar tem um ID null, 1 se a fonte atual tem um ID maior que a fonte
	 * com a qual estamos a comparar, e -1 se a fonte atual tem um id menor do que a fonte com a qual estamos
	 * a comparar.
	 */
	public int compareID(String x){
		if(x==null)
			return 0;
		else {
			int id = Integer.parseInt(sourceID);
			int xInt = Integer.parseInt(x);
			
			if(id>xInt)
				return 1;
			else
				return -1;
		}
	}

}

