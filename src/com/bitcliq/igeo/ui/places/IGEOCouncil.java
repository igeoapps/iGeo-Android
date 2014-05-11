package com.bitcliq.igeo.ui.places;

import java.util.HashMap;

/**
 * Classe usada para representar a informação sobre um concelho.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOCouncil {
	/**
	 * Nome do concelho.
	 */
	String name;
	
	/**
	 * HashMap com as freguesias do conselho indexadas pelo seu código ine.
	 */
	HashMap<String,IGEOParish> parishes;
	
	/**
	 * Construtor por defeito.
	 * @param name Nome do concelho
	 */
	public IGEOCouncil(String name){
		this.name=name;
		parishes=new HashMap<String,IGEOParish>();
	}
	
	
	/**
	 * Adiciona uma freguesia ao conselho
	 * @param cod Código da freguesia.
	 * @param p Freguesia a adicionar.
	 */
	public void addParish(String cod,IGEOParish p){
			parishes.put(cod,p);
	}
	
	
	/**
	 * Obtém as freguesias do concelho.
	 * @return HashMap com as freguesias do concelho indexadas pelo seu código.
	 */
	public HashMap<String,IGEOParish> getParishes(){
		return parishes;
	}
}
