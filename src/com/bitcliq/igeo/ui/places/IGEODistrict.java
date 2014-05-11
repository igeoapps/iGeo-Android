package com.bitcliq.igeo.ui.places;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Esta classe contém as informações sobre um distrito.
 * @author Bitcliq, Lda.
 *
 */
@SuppressWarnings("serial")
public class IGEODistrict implements Serializable {
	/**
	 * Nome do distrito.
	 */
	String name;
	
	/**
	 * HashMap com os concelhos do distrito indexados pelo seu ID.
	 */
	HashMap<String,IGEOCouncil> councils;
	
	/**
	 * Construtor por defeito.
	 * @param name Nome do distrito.
	 */
	public IGEODistrict(String name){
		this.name=name;
		councils=new HashMap<String,IGEOCouncil>();
	}
	
	
	/**
	 * Adiciona um concelho ao distrito.
	 * @param cod Código do concelho a adicionar.
	 * @param c Concelho a adicionar
	 */
	public void addCouncil(String cod,IGEOCouncil c){
			councils.put(cod,c);
	}
	
	
	/**
	 * Obtém os concelhos do distrito.
	 * @return HashMap com os concelhos do distrito indexados pelo seu ID.
	 */
	public HashMap<String,IGEOCouncil> getConcelhos(){
		return councils;
	}
}
