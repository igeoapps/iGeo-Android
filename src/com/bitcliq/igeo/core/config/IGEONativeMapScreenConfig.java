package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Configurações de um ecrã com um mapa.
 * É aqui que vão ser colocadas as associações de cada par (fonte, categoria) a uma cor a apresentar no mapa,
 * na legenda do mapa, nos polígonos do mapa, e nos títulos da lista.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEONativeMapScreenConfig implements IGEOScreenConfig, Serializable 
{


	/**
	 * Contém a associação a cada chave do tipo "srcID_catID" a um Float contendo o HUE da cor
	 * a usar nos pins do Google Maps.
	 */
	public HashMap<String,Float> colorPinsCategory;
	
	/**
	 * Contém a associação a cada chave do tipo "srcID_catID" a uma String contendo a cor em hexadecimal
	 * a usar nos títulos da lista.
	 */
	public HashMap<String,String> colorHTMLCategory;
	
	/**
	 * Contém a associação a cada chave do tipo "srcID_catID" a uma String contendo o url do pin a usar
	 * na legenda do mapa. Caso não seja precedido de "file://" ou "http://" assume-se que estamos a referir-nos
	 * a um ficheiro contido nos resources do projeto.
	 */
	public HashMap<String,String> pinLegendCategory;
	
	/**
	 * Contém a associação a cada chave do tipo "srcID_catID" um Integer contendo a representação hexadecimal
	 * da cor a usar nas linhas dos polígonos.
	 */
	public HashMap<String,Integer> polygonColorCategory;
	
	/**
	 * Contém a associação a cada chave do tipo "srcID_catID" um Integer contendo a representação hexadecimal
	 * da cor a usar no preenchimento dos poligonos.
	 */
	public HashMap<String,Integer> polygonBackgroundColorCategory;




	/**
	 * Construtor por defeito.
	 */
	public IGEONativeMapScreenConfig(){
		super();

		colorPinsCategory = new HashMap<String,Float>();
		colorHTMLCategory = new HashMap<String,String>();
		pinLegendCategory = new HashMap<String,String>();
		polygonColorCategory = new HashMap<String,Integer>();
		polygonBackgroundColorCategory = new HashMap<String,Integer>();
	}





	/**
	 * Obtém a cor do pin a usar no Google Maps para uma determinada fonte e categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return Hue da cor a ser utilizada.
	 */
	public Float getPinColorForCategory(String srcID, String catID){
		try {
			if(colorPinsCategory.containsKey(srcID+"_"+catID)){
				return colorPinsCategory.get(srcID+"_"+catID);
			}
			else {
				if(catID.length()>1){
					if(colorPinsCategory.containsKey(srcID+"_"+catID.substring(catID.length()-1, catID.length())))
						return colorPinsCategory.get(srcID+"_"+catID.substring(catID.length()-1, catID.length()));
					else
						return colorPinsCategory.get("-1");
				}
				else {
					return colorPinsCategory.get("-1");
				}
			}
		}
		catch(Exception e){
			return colorPinsCategory.get("-1");
		}
	}


	/**
	 * Obtém a cor html a ser usada no título da lista para uma determinada fonte e categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return String com a cor a ser utilizada, em hexadecimal.
	 */
	public String getColorHTMLForCategory(String srcID, String catID){
		try{
			if(colorHTMLCategory.containsKey(srcID+"_"+catID)){
				return colorHTMLCategory.get(srcID+"_"+catID);
			}
			else {
				if(catID.length()>1){
					if(colorHTMLCategory.containsKey(srcID+"_"+catID.substring(catID.length()-1, catID.length())))
						return colorHTMLCategory.get(srcID+"_"+catID.substring(catID.length()-1, catID.length()));
					else
						return colorHTMLCategory.get("-1");
				}
				else {
					return colorHTMLCategory.get("-1");
				}
			}
		}
		catch(Exception e){
			return colorHTMLCategory.get("-1");
		}
	}


	/**
	 * Obtém o pin da legenda a ser usado para uma determinada fonte e categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return URL com o pin da legenda a ser usado. Caso não seja precedido de "file://" ou "http://" assume-se que estamos a referir-nos
	 * a um ficheiro contido nos resources do projeto.
	 */
	public String getPinLegendForCategory(String srcID, String catID){
		try {
			if(pinLegendCategory.containsKey(srcID+"_"+catID)){
				return pinLegendCategory.get(srcID+"_"+catID);
			}
			else {
				if(catID.length()>1){
					if(pinLegendCategory.containsKey(srcID+"_"+catID.substring(catID.length()-1, catID.length())))
						return pinLegendCategory.get(srcID+"_"+catID.substring(catID.length()-1, catID.length()));
					else
						return pinLegendCategory.get("-1");
				}
				else {
					return pinLegendCategory.get("-1");
				}
			}
		}
		catch(Exception e){
			return pinLegendCategory.get("-1");
		}
	}


	/**
	 * Obtém a cor da linha a ser usada no desenho de polígonos para uma determinada fonte e categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return Representação em hexadecimal da cor a ser usada.
	 */
	public Integer getPolygonColorForCategory(String srcID, String catID){
		
		try{
			if(polygonColorCategory.containsKey(srcID+"_"+catID)){
				return polygonColorCategory.get(srcID+"_"+catID);
			}
			else {
				if(catID.length()>1){
					if(polygonColorCategory.containsKey(srcID+"_"+catID.substring(catID.length()-1, catID.length())))
						return polygonColorCategory.get(srcID+"_"+catID.substring(catID.length()-1, catID.length()));
					else
						return polygonColorCategory.get("-1");
				}
				else {
					return polygonColorCategory.get("-1");
				}
			}
		}
		catch(Exception e){
			return polygonColorCategory.get("-1");
		}
	}


	/**
	 * Obtém a cor de fundo a ser usada no desenho de polígonos para uma determinada fonte e categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return Representação em hexadecimal da cor a ser usada.
	 */
	public Integer getPolygonBackgroundColorForCategory(String srcID, String catID){
		
		try {
			if(polygonBackgroundColorCategory.containsKey(srcID+"_"+catID)){
				return polygonBackgroundColorCategory.get(srcID+"_"+catID);
			}
			else {
				if(catID.length()>1){
					if(polygonBackgroundColorCategory.containsKey(srcID+"_"+catID.substring(catID.length()-1, catID.length())))
						return polygonBackgroundColorCategory.get(srcID+"_"+catID.substring(catID.length()-1, catID.length()));
					else
						return polygonBackgroundColorCategory.get("-1");
				}
				else {
					return polygonBackgroundColorCategory.get("-1");
				}
			}
		}
		catch(Exception e){
			return polygonBackgroundColorCategory.get("-1");
		}
	}

}

