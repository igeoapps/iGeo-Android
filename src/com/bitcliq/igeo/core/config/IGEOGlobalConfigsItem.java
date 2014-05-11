package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Esta classe é utilizada para guardar um conjunto de dados das configurações que é guardado num ficheiro, para que 
 * ao voltarmos à aplicação, possam ser obtidas na sua versão mais recente.
 * @author Bitcliq, Lda.
 *
 */
@SuppressWarnings("serial")
public class IGEOGlobalConfigsItem implements Serializable {
	public GEOAppConfigs appConfigs;
	public HashMap<String,IGEOScreenConfig> screenHashMapConfigs;
	public HashMap<String, Integer> configsVersionDictionary;

	public IGEOGlobalConfigsItem(GEOAppConfigs appConfigs, HashMap<String,IGEOScreenConfig> screenHashMapConfigs, HashMap<String, Integer> configsVersionDictionary) {
		if(appConfigs.appSourcesHashMap!=null){
			appConfigs.appSourcesHashMap.clear();
			appConfigs.appSourcesHashMap = null;
		}
		this.appConfigs = appConfigs;
		this.screenHashMapConfigs = screenHashMapConfigs;
		this.configsVersionDictionary = configsVersionDictionary;
	}

}
