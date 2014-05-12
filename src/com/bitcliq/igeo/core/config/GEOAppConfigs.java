package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import com.bitcliq.igeo.core.datasource.IGEOSource;


/**
 * Classe usada para guardar as configurações da App atual.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class GEOAppConfigs implements Serializable 
{

	/**
	 * Contém o raio pré-definido usado na pesquisa por proximidade.
	 */
	private int proximityRadius;

	/**
	 * Versão das configurações.
	 * Não é usado nesta versão mas pode posteriormente ser usada para comparar com uma versão das configurações
	 * existentes num servidor.
	 */
	private int configsVersion;

	/**
	 * URL usado para update e verificação de updates.
	 * Nesta versão a funcionalidade de update das configurações não se encontra ainda implementada,
	 * mas poderá vir a ser implementada brevemente.
	 */
	public String updateURL;

	/**
	 * HashMap de fontes disponíveis na App, indexadas pelo seu ID.
	 */
	public HashMap<String,IGEOSource> appSourcesHashMap;

	/**
	 * Nome da App.
	 */
	public String appName;

	/**
	 * Nome do package da App.
	 */
	public String appPackageName;

	/**
	 * URL usado para pedidos ao servidor.
	 */
	public String URLRequests;

	/**
	 * URL usado para pedir ao servidor as fontes disponíveis para a App.
	 */
	public String URLRequestsSources;

	/**
	 * URL usado para pedir ao servidor as categorias disponíveis na App para uma determinada fonte.
	 */
	public String URLRequestsCategories;
	
	/**
	 * URL usado para pedir ao servidor as configurações por defeito para esta App.
	 */
	public String URLRequestAppDefaults;

	/**
	 * Cor base da App.
	 */
	public String appColor;




	/**
	 * Construtor por defeito da classe GEOAppConfigs
	 */
	public GEOAppConfigs(){
		super();
	}

	/**
	 * Construtor da classe GEOAppConfigs em que são passados como argumentos o nome da app, o nome do package e o URL
	 * para a alteração das configurações quando aplicáveis.
	 * @param appName Nome da App
	 * @param packageName Nome do package da App
	 * @param URLUpdate URL para update das configurações quando aplicáveis
	 */
	public GEOAppConfigs(String appName, String packageName, String URLUpdate){
		super();

		this.appName = appName;
		this.appPackageName = packageName;
		this.updateURL = URLUpdate;
	}


	/**
	 * Devolve o raio pré-definido usado na pesquisa por proximidade.
	 * @return Raio pré-definido usado na pesquisa por proximidade.
	 */
	public int getProximityRadius() {
		return proximityRadius;
	}


	/**
	 * Define o raio pré-definido usado na pesquisa por proximidade.
	 * @param radius Raio usado na pesquisa por proximidade
	 */
	public void setProximityRadius(int radius) {
		proximityRadius = radius;
	}


	/**
	 * Obtém um HashMap com as fontes disponíveis na App indexadas pelo seu ID.
	 * @return HashMap com as fontes disponíveis na App indexadas pelo seu ID.
	 */
	public HashMap<String, IGEOSource> getSourcesHashMap(){
		return appSourcesHashMap;
	}

	
	/**
	 * Define as fontes disponíveis para a App.
	 * @param list Lista de fontes a incluir na App.
	 */
	public void setSourcesList(Collection<IGEOSource> list){

		appSourcesHashMap = new HashMap<String, IGEOSource>();
		for(IGEOSource s : list){
			appSourcesHashMap.put(s.sourceID, s);
		}
	}




	/**
	 * Representação em texto das configurações da App.
	 */
	public String toString(){
		String s = "GEOAppConfigs: (";

		s+="Name: "+this.appName;
		s+=", URLUpdate: "+this.updateURL;
		s+=", PackageName: "+this.appPackageName;
		s+=", URLRequests: "+this.URLRequests;
		s+=", URLRequestsSources: "+this.URLRequestsSources;
		s+=", URLRequestsCategories: "+this.URLRequestsCategories;
		s+=", URLRequestAppDefaults: "+this.URLRequestAppDefaults;
		s+=", AppColor: "+this.appColor+" )";

		return s;
	}

}

