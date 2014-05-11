package com.bitcliq.igeo.core.datasource;
import java.util.ArrayList;

import android.location.Location;


/**
 * Detalhes de um item com título, um texto/html e uma imagem. Esta classe representa
 * um item de um tipo genérico, podendo o mesmo ser extendido de futuro para criar itens
 * com caracteristicas específicas mas que terão como base os atributos já aqui definidos.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOGenericDataItem extends IGEODataItem
{
	/**
	 * URL da imagem associada ao item.
	 */
	public String imageURL;

	/**
	 * Contém as coordenadas ou lista de coordenadas do item. No caso do item ser representado por um
	 * polígono ou um grupo de polígonos este ArrayList contém a lista de coordenadas usadas para a sua contrução no mapa.
	 */
	public ArrayList<Location> locationCoordenates;
	
	/**
	 * Indica se estamos perante um item que tem uma lista de polígonos. Útil para desenho no mapa.
	 */
	public boolean multiPolygon;
	
	/**
	 * Este ArrayList é utilizado no caso dos itens que são representados por vários poligonos e indicam em que posição da lista
	 * de coordenadas mudamos de pol'igono. Isto é, se tivermos um item em que as coordenadas da posição 0 até á 3 representam um poligono,
	 * e as coordenadas da posição 4 á 7 outro poligono, este array list terá o seguinte conteúdo: [4, 7].
	 */
	public ArrayList<Integer> lastPolygonCoordenatesList;
	
	/**
	 * Indica o centro do polígono. É neste ponto que será desenhado
	 * o pin que permite o acesso através do mapa à informação do item.
	 */
	public Location centerPoint;


	/**
	 * Construtor por defeito da classe IGEOGenericDataItem.
	 */
	public IGEOGenericDataItem(){
		super();
	}


	/**
	 * Devolve uma representação em texto do item.
	 */
	public String toString(){
		String s = "";

		s = "IGEODataItem (ID:"+this.itemID+", Resume: "+textOrHTML+", CategoryID:"+this.categoryID+", Image_URL:"+this.imageURL+", Titulo:"+this.title+", GPS:";
		if(this.locationCoordenates!=null){
			for(Location l : this.locationCoordenates){
				s+="("+l.getLatitude()+", "+l.getLongitude()+"), ";
			}
		}
		
		s+=")\n";

		return s;
	}

}

