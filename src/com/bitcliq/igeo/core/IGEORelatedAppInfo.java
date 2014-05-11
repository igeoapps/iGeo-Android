package com.bitcliq.igeo.core;


/**
 * Para guardar a informação sobre uma App relacionada com a App atual.
 * Ainda não impelmentado.
 * @author Bitcliq, Lda.
 *
 */

public class IGEORelatedAppInfo
{
	/**
	 * Nome da App.
	 */
	public String appName;
	
	/**
	 * URL do intent usado para a abertura da App.
	 */
	public String appURL;
	
	
	/**
	 * Construtor por defeito da classe IGEORelatedAppInfo.
	 */
	public IGEORelatedAppInfo(){
		super();
	}
	
	/**
	 * Construtor da classe IGEORelatedAppInfo em que passamos como argumentos o title da App e o intent
	 * utilizado para a abrir.
	 * @param appName
	 * @param appURL
	 */
	public IGEORelatedAppInfo(String appName, String appURL){
		super();
		
		this.appName = appName;
		this.appURL = appURL;
	}

}

