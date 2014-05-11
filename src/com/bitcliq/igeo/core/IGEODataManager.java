package com.bitcliq.igeo.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.bitcliq.igeo.core.datasource.IGEOCategory;
import com.bitcliq.igeo.core.datasource.IGEODataItemsCache;
import com.bitcliq.igeo.core.datasource.IGEOGenericDataItem;
import com.bitcliq.igeo.core.datasource.IGEOJSONServerReader;
import com.bitcliq.igeo.core.datasource.IGEOSource;


/**
 * Esta classe é responsavel pela interação entre os dados e a UI. Nela estão definidos os métodos
 * necessários para a obtenção de dados das fontes, das categorias e dos itens. É através desta classe que são
 * chamados os métodos necessários de outras classes que por sua vez fazem consultas ao servidor quando necessário.
 * São ainda, quando aplicáveis, apresentados resultados contidos em atributos da classe. Isto é, existe um atributo para
 * a fonte atual, para as categorias pelas quais estamos a filtrar os dados, existe uma cache de itens que estao atualmente
 * a ser apresentados, entre outros.
 * Todos os métodos necessários para interagir com os dados a apresentar na aplicação encontram-se aqui.
 * @author Bitcliq, Lda.
 *
 */

public class IGEODataManager
{

	/**
	 * Token necessário para fazer consultas ao servidor.
	 */
	private static final String TOKEN = "A45E9DBA-0AD4-4A1B-AEBD-ED9E58FDF2F0";

	/**
	 * Lista local com as Apps relacionadas.</br>
	 * Nesta versão não foi utilizada.
	 */
	private static ArrayList<IGEORelatedAppInfo> localAppList;

	/**
	 * Fonte que está atualmente a ser utilizada.
	 */
	private static IGEOSource actualSource;

	/**
	 * HashMap de categorias utilizadas na filtragem atual, indexadas pelo seu identificador.
	 */
	public static HashMap<String,IGEOCategory> localHashMapCategories;

	/**
	 * Contém uma lista local com os itens atuais, indexados pelo seu identificador
	 * para que seja possível aceder-lhes facilmente.
	 */
	private static HashMap<String,IGEOGenericDataItem> localHashMapDataItems;

	/**
	 * Trata-se de uma lista de DataItems que estão a ser apresentados no momento.
	 * O facto de existir uma lista e um HashMap melhora a performance da App em certas situações dado que
	 * em alguns casos, como por exemplo a criação da ListView com os itens, é necessária uma Lista e não um
	 * HashMap.
	 */
	private static ArrayList<IGEOGenericDataItem> temporaryDataItemsList;


	//Variáveis usadas na filtragem por distrito, concelho e freguesia
	/**
	 * Código de distrito utilizado na filtragem. Caso o utlizador não filtre por distrito
	 * o seu valor será null.
	 */
	private static String codDist = null;

	/**
	 * Código do concelho utilizado atualmente na filtragem. Caso o utlizador não filtre por concelho
	 * o seu valor será null.
	 */
	private static String codConc = null;

	/**
	 * Código da freguesia utilizada atualmente na filtragem. Caso o utlizador não filtre por freguesia
	 * o seu valor será null.
	 */
	private static String codPar = null;
	//--


	/**
	 * Guarda as palavras-chave pelas quais está a ser feita uma pesquisa. Caso não esteja a ser
	 * feita nenhuma pesquisa esta variável será null.
	 */
	public static String keywords = null;





	/**
	 * Construtor por defeito da classe IGEODataManager
	 */
	public IGEODataManager(){
		super();

		localAppList = new ArrayList<IGEORelatedAppInfo>();
		localHashMapCategories = new HashMap<String, IGEOCategory>();
		localHashMapDataItems = new HashMap<String, IGEOGenericDataItem>();
	}




	//Apps ########################################################################
	/**
	 * Obtém a lista de Apps relacionadas.
	 * Poderá ir ao servidor obter esses dados ou usar apenas a lista local.
	 * Neste momento não está a ser utilizado.
	 */
	public static ArrayList<IGEORelatedAppInfo> getAppsList() {
		return null;
	}




	//Fontes ######################################################################
	/**
	 * Obtém o name de uma fonte através do seu identificador.
	 * @param srcID ID da fonte.
	 * @return Nome da fonte.
	 */
	public static String getSourceName(String srcID){
		if(IGEOConfigsManager.getAppConfigs().getSourcesHashMap()!=null){  //se já existe retorna
			return IGEOConfigsManager.getAppConfigs().getSourcesHashMap().get(srcID).sourceName;
		}

		return null;
	}


	/**
	 * Obtém a lista de fontes.
	 * @return ArrayList com as fontes da App.
	 */
	public static Collection<IGEOSource> getSourcesList() {

		if(IGEOConfigsManager.getAppConfigs().getSourcesHashMap()!=null){  //se já existe retorna
			return IGEOConfigsManager.getAppConfigs().getSourcesHashMap().values();
		}

		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;

		url = IGEOConfigsManager.getAppConfigs().URLRequestsSources + "&imgtype=" + strDensity + "&token="+ TOKEN;
				JSONObject jsonObj = IGEOJSONServerReader.getJSONObject(url);
		if(jsonObj!=null){
			//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
			ArrayList<IGEOSource> listResult = IGEOUtils.getSourcesFromJSONObject(jsonObj);
			
			strDensity = null;

			return listResult;
		}

		return null;	
	}


	/**
	 * Obtém a fonte que está a ser usada neste momento.
	 * @return Fonte que está a ser usada.
	 */
	public static IGEOSource getActualSource() {
		return actualSource;
	}


	/**
	 * Define a fonte selecionada.
	 */
	public static void setActualSource(IGEOSource s) {
		actualSource = s;
	}


	/**
	 * Limpa a fonte selecionada.
	 */
	public static void clearActualSource() {
		actualSource = null;
		if(localHashMapCategories!=null)
			localHashMapCategories.clear();
	}






	//Categorias ##################################################################
	/**
	 * Obtém a lista de categorias da fonte atual.
	 * @return ArrayList com as categorias da fonte atual
	 */
	public static ArrayList<IGEOCategory> getCategoriesListActualSource() {
		if(actualSource!=null){
			if(actualSource.categoryHashMap!=null)
				return new ArrayList<IGEOCategory>(actualSource.categoryHashMap.values());
			else
				return null;
		}

		return null;
	}
	
	
	/**
	 * Limpa a lista de categorias. 
	 */
	public static void clearCategoriesListActualSource() {
		if(actualSource!=null){
			actualSource.categoryHashMap.clear();
		}
	}


	//Variáveis temporárias utilizadas na obtenção das categorias --
	private static String url;
	private static JSONObject jsonObj;
	private static ArrayList<IGEOCategory> listResultCats;
	//--

	/**
	 * Obtém a lista de categorias disponíveis para uma fonte no raio de próximidade pré-definido de uma localização.
	 * @param loc Centro do raio dentro do qual queremos efetuar a pesquisa.
	 * @return Lista de categorias disponíveis.
	 */
	public static ArrayList<IGEOCategory> getCategoriesListActualSourceInLocation(Location loc) {
		if(actualSource!=null){

			//vai obter o JSONObject
			String strDensity = IGEOConfigsManager.densityString;

			url = IGEOConfigsManager.getAppConfigs().URLRequestsCategories + "&imgtype=" + strDensity + "&token="+ TOKEN;
			url = url+
					"&sid="+IGEODataManager.getActualSource().sourceID+
					"&lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+
					"&r="+IGEOConfigsManager.getAppConfigs().getProximityRadius()+
					"&txt=";
			jsonObj = IGEOJSONServerReader.getJSONObject(url);
			if(jsonObj!=null){
				//Faz o parse do JSONObject e transforma-o num ArrayList de Categories
				listResultCats = IGEOUtils.getCategoriesFromJSONObject(jsonObj);

				if(listResultCats!=null){
					IGEODataManager.actualSource.setCategories(listResultCats);
				}
				else {
					IGEODataManager.actualSource.setCategories(null);
				}

				return listResultCats;
			}
			else {
				IGEODataManager.actualSource.setCategories(null);
			}

		}

		return null;
	}


	/**
	 * Obtém a lista de categorias disponíveis para uma fonte numa localização anteriormente selecionada através da seleção do
	 * distrito, concelho e freguesia.
	 * @return Lista de categorias disponíveis.
	 */
	public static ArrayList<IGEOCategory> getCategoriesListActualSourceInSearchLocation() {
		if(actualSource!=null){

			//vai obter o JSONObject
			String strDensity = IGEOConfigsManager.densityString;

			url = IGEOConfigsManager.getAppConfigs().URLRequestsCategories + "&imgtype=" + strDensity + "&token="+ TOKEN;
			url = url+"&sid="+IGEODataManager.getActualSource().sourceID+"&ine=";
			if(codPar!=null){
				if(!codPar.equals(""))
					url+=codPar;
				else
					url+=codConc;
			}
			else {
				url+=codConc;
			}

			url+="&txt=";

			jsonObj = IGEOJSONServerReader.getJSONObject(url);
			if(jsonObj!=null){
				//Faz o parse do JSONObject e transforma-o num ArrayList de Categories
				listResultCats = IGEOUtils.getCategoriesFromJSONObject(jsonObj);

				if(listResultCats!=null){
					IGEODataManager.actualSource.setCategories(listResultCats);
				}

				return listResultCats;
			}

		}

		return null;
	}


	/**
	 * Obtém a lista de categorias atualmente usadas na filtragem.
	 * @return ArrayList com as categorias usadas atualmente na filtragem
	 */
	public static ArrayList<IGEOCategory> getActualCategoriesListActualFilter() {
		if(localHashMapCategories!=null){
			return new ArrayList<IGEOCategory>(localHashMapCategories.values());
		}

		return null;
	}


	//variável temporária para colocação dos resultados do método seguinte
	//A criação de variáveis estáticas temporárias é utilizada nestes casos em que as chamadas são frequentes e
	//temos grandes quantidades de dados para que não sejam criadas várias instâncias do objeto e tal situação
	//venha a causar problemas em termos de gestão de memória.
	private static ArrayList<String> resultIDs;
	//
	/**
	 * Obtém a lista de ID's das categorias atualmente usadas na filtragem.
	 * @return ArrayList com os ID's das categorias usadas atualmente na filtragem
	 */
	public static ArrayList<String> getActualCategoriesListActualFilterIDS() {
		if(localHashMapCategories!=null){
			resultIDs = new ArrayList<String>();
			for(IGEOCategory c : new ArrayList<IGEOCategory>(localHashMapCategories.values())){
				resultIDs.add(c.categoryID);
			}

			return resultIDs;
		}

		return null;
	}


	/**
	 * Indica se a categoria passada como argumento está a ser utilizada como filtro atualmente.
	 * @param c Categoria sobre a qual queremos fazer a verificação.
	 * @return true ou false consoante estamos ou não a filtrar sobre essa categoria.
	 */
	public static boolean isFilteringByCategory(IGEOCategory c) {
		if(localHashMapCategories==null){
			return false;
		}

		return localHashMapCategories.containsKey(c.categoryID);
	}


	//variável temporária criada para alojar a sorce obtida
	//A criação de variáveis estáticas temporárias é utilizada nestes casos em que as chamadas são frequentes e
	//temos grandes quantidades de dados para que não sejam criadas várias instâncias do objeto e tal situação
	//venha a causar problemas em termos de gestão de memória.
	private static IGEOSource selectedSource;
	/**
	 * Obtém a lista de categorias para uma source.
	 * @param sourceID ID da categoria da qual queremos obter as sources.
	 * @return Lista de categorias para a fonte cujo ID foi passado como argumento.
	 */
	public static ArrayList<IGEOCategory> getCategoriesForSourceID(String sourceID) {
		selectedSource = IGEOConfigsManager.getAppConfigs().getSourcesHashMap().get(sourceID); 
		if(selectedSource!=null){
			return new ArrayList<IGEOCategory>(selectedSource.categoryHashMap.values());
		}

		return null;
	}


	/**
	 * Adiciona uma categoria da source atual à filtragem atual.
	 * @param cat Categoria a adicionar para filtragem
	 */
	public static void addActualCategory(IGEOCategory cat) {
		if(localHashMapCategories==null)
			localHashMapCategories = new HashMap<String, IGEOCategory>();

		localHashMapCategories.put(cat.categoryID, cat);
	}


	/**
	 * Remove uma categoria da source atual da filtragem atual.
	 * @param catID ID da categoria a remover da filtragem
	 */
	public static void removeActualCategory(String catID) {
		localHashMapCategories.remove(catID);
	}


	/**
	 * Define a lista de categorias da source atual a usar na filtragem
	 * @param categories
	 */
	public static void setLocalCurrentFilterCategories(ArrayList<IGEOCategory> categories) {
		if(localHashMapCategories==null)
			localHashMapCategories = new HashMap<String, IGEOCategory>();

		for(IGEOCategory c : categories){
			localHashMapCategories.put(c.categoryID, c);
		}
	}


	/**
	 * Remove todas as categorias da filtragem
	 */
	public static void clearCurrentFilterCategories() {

		if(localHashMapCategories!=null)
			localHashMapCategories.clear();
	}







	//Dados #######################################################################
	private static ArrayList<IGEOGenericDataItem> listResultSources;
	/**
	 * Obtém a lista de itens para uma ou mais categorias de uma fonte, num raio de proximidade pré-definido
	 * no menu de definições.
	 * @param srcID ID da source.
	 * @param catIDList Lista de ID's das categorias.
	 * @param loc Localização a utilizar como centro do raio de pesquisa pré-definido.
	 * @return ArrayList de DataItems com os dados pretendidos.
	 */
	public static ArrayList<IGEOGenericDataItem> getListForSourceAndCategoriesNear(String srcID, ArrayList<String> catIDList, Location loc) {

		if(catIDList==null){
			return null;
		}

		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;

		url = IGEOConfigsManager.getAppConfigs().URLRequests + "&imgtype=" + strDensity + "&token="+ TOKEN;
		url = url + "&sid="+srcID+"&cats=";

		int i=0;
		for(String catID : catIDList){
			url+=catID+";";
			i++;
		}

		url+="&lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"&r="+IGEOConfigsManager.getAppConfigs().getProximityRadius();

		//permite ir buscar todos os items
		//atenção isto deverá de futuro ser alterado
		url+="&nr=0";

		//Log.i("DGAItems","url = "+url);

		jsonObj = IGEOJSONServerReader.getJSONObject(url);

		if(jsonObj!=null){
			//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
			listResultSources = IGEOUtils.getDataListFromJSONObject(jsonObj);

			return listResultSources;
		}

		return null;
	}


	private static ArrayList<IGEOGenericDataItem> listResultData;
	/**
	 * Obtém a lista de itens para uma ou mais categorias de uma fonte, num raio de proximidade pré-definido
	 * no menu de settings, filtrando por uma string contendo palavras chave.
	 * @param srcID ID da fonte.
	 * @param catIDList Lista de ID's das categorias.
	 * @param filters Palavras-chave usadas na pesquisa.
	 * @param loc Localização a utilizar como centro do raio de pesquisa pré-definido.
	 * @return ArrayList de DataItems com os dados pretendidos.
	 */
	public static ArrayList<IGEOGenericDataItem> getListForSourceAndCategoriesNearFiltered(String srcID, ArrayList<String> catIDList, String filters, Location loc) {
		if(catIDList==null){
			return null;
		}

		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;
		
		url = IGEOConfigsManager.getAppConfigs().URLRequests + "&imgtype=" + strDensity + "&token="+ TOKEN;
		
		url += "&sid="+srcID+"&cats=";

		int i=0;
		for(String catID : catIDList){
			url+=catID+";";
			i++;
		}

		url+="&lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"&r="+IGEOConfigsManager.getAppConfigs().getProximityRadius();

		//Permite ir buscar todos os itens.
		//Atenção isto deverá de futuro ser alterado para que a paginação seja feita logo na
		//obteção dos dados. Neste momento só está a ser feita na construção da ListView.
		url+="&nr=0";

		if(filters!=null)
			url+="&txt="+filters;

		Log.i("DGAItems","url = "+url);
		
		jsonObj = IGEOJSONServerReader.getJSONObject(url);

		if(jsonObj!=null){
			//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
			listResultData = IGEOUtils.getDataListFromJSONObject(jsonObj);

			return listResultData;
		}

		return null;
	}


	/**
	 * Obtém a lista de itens para uma ou mais categorias de uma fonte, numa localização definida pela filtragem por distrito,
	 * concelho e freguesia.
	 * @param srcID ID da fonte.
	 * @param catIDList Lista de ID's das categorias.
	 * @return ArrayList de DataItems com os dados pretendidos.
	 */
	public static ArrayList<IGEOGenericDataItem> getListForSourceAndCategoriesInSearchLocation(String srcID, ArrayList<String> catIDList) {

		if(catIDList==null){
			return null;
		}

		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;
		
		url = IGEOConfigsManager.getAppConfigs().URLRequests + "&imgtype=" + strDensity + "&token="+ TOKEN;
		
		url += "&sid="+srcID+"&cats=";

		int i=0;
		for(String catID : catIDList){
			url+=catID+";";
			i++;
		}

		//Permite ir buscar todos os itens.
		//Deverá, no futuro, ser alterado para que a paginação seja feita logo na
		//obtenção dos dados. Neste momento só está a ser feita na construção da ListView.
		url+="&nr=0";

		if(codPar!=null){
			if(!codPar.equals(""))
				url+="&ine="+codPar;
			else {
				if(codConc!=null){
					if(!codConc.equals("")){
						url+="&ine="+codConc;
					}
					else {
						url+="&ine="+codDist;
					}
				}
				else {
					url+="&ine="+codDist;
				}
			}
		}
		else {
			if(codConc!=null){
				if(!codConc.equals("")){
					url+="&ine="+codConc;
				}
				else {
					url+="&ine="+codDist;
				}
			}
			else {
				url+="&ine="+codDist;
			}
		}

		Log.i("DGAItems","url = "+url);

		jsonObj = IGEOJSONServerReader.getJSONObject(url);

		if(jsonObj!=null){

			//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
			listResultData = IGEOUtils.getDataListFromJSONObject(jsonObj);

			jsonObj = null;
			url = null;

			System.gc();

			return listResultData;
		}

		jsonObj = null;
		url = null;

		System.gc();

		return null;
	}



	/**
	 * Obtém a lista de itens para uma ou mais categorias de uma fonte, numa localização definida pela filtragem por distrito,
	 * concelho e freguesia e por palavras chave.
	 * @param srcID ID da fonte.
	 * @param catIDList Lista de ID's das categorias.
	 * @param filters Palavras-chave usadas na pesquisa.
	 * @return ArrayList de DataItems com os dados pretendidos.
	 */
	public static ArrayList<IGEOGenericDataItem> getListForSourceAndCategoriesInSearchLocationFiltered(String srcID, ArrayList<String> catIDList, String filters) {

		if(catIDList==null){
			return null;
		}

		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;
		
		url = IGEOConfigsManager.getAppConfigs().URLRequests + "&imgtype=" + strDensity + "&token="+ TOKEN;
		
		url += "&sid="+srcID+"&cats=";

		int i=0;
		for(String catID : catIDList){
			url+=catID+";";
			i++;
		}

		//permite ir buscar todos os itens
		//atenção isto deverá de futuro ser alterado
		url+="&nr=0";

		if(codPar!=null){
			if(!codPar.equals(""))
				url+="&ine="+codPar;
			else
				url+="&ine="+codConc;
		}
		else {
			url+="&ine="+codConc;
		}

		if(filters!=null)
			url+="&txt="+filters;

		//Log.i("DGAItems","url = "+url);

		jsonObj = IGEOJSONServerReader.getJSONObject(url);

		if(jsonObj!=null){

			//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
			listResultData = IGEOUtils.getDataListFromJSONObject(jsonObj);

			jsonObj = null;
			url = null;

			System.gc();

			return listResultData;
		}

		jsonObj = null;
		url = null;

		System.gc();

		return null;
	}




	private static IGEOGenericDataItem di_geral = null;
	/**
	 * Obtém um item através apenas do seu ID.
	 * @param ID ID do item a obter.
	 * @return Item pretendido.
	 */
	public static IGEOGenericDataItem getDataItemForID(String ID) {

		if(IGEODataItemsCache.getItem(ID)!=null)
			return IGEODataItemsCache.getItem(ID);
		else {
			//Obter o JSON do servidor


			//vai obter o JSONObject
			String strDensity = IGEOConfigsManager.densityString;
			
			url = IGEOConfigsManager.getAppConfigs().URLRequests + "&imgtype=" + strDensity + "&token="+ TOKEN;
			
			url += "&sdid="+ID;

			//Log.i("DGAItems","url = "+url);

			jsonObj = IGEOJSONServerReader.getJSONObject(url);

			if(jsonObj!=null){
				//Faz o parse do JSONObject e transforma-o num ArrayList de Sources
				di_geral = IGEOUtils.getDataItemFromJSONObject(jsonObj);
			}

			if(di_geral!=null)
				IGEODataItemsCache.addItem(di_geral);

			url = null;
			jsonObj = null;

			System.gc();

			return di_geral;
		}

	}




	/**
	 * Guarda num HashMap os itens que estão a ser utilizados atualmente como itens temporários de forma a que se possa ter
	 * um rápido e fácil acesso aos mesmos.
	 * @param listDataItems Lista de DataItems a carregar.
	 */
	public static void addTemporaryDataItems(ArrayList<IGEOGenericDataItem> listDataItems){

		if(localHashMapDataItems==null){
			localHashMapDataItems = new HashMap<String, IGEOGenericDataItem>();
		}

		for(IGEOGenericDataItem di : listDataItems){
			localHashMapDataItems.put(di.itemID, di);
		}

		temporaryDataItemsList = listDataItems;
	}


	/**
	 * Limpa a lista e HashMap de DataItems temporários.
	 */
	public static void clearTemporaryDataItems(){

		if(localHashMapDataItems!=null){
			localHashMapDataItems.clear();
		}
		localHashMapDataItems = null;

		if(temporaryDataItemsList!=null)
			temporaryDataItemsList.clear();
		temporaryDataItemsList = null;

		System.gc();

	}


	/**
	 * Obtém um HashMap com os DataItems que estão atualmente a ser apresentados, indexados pelo seu ID.
	 * @return HashMap com os DataItems.
	 */
	public static HashMap<String,IGEOGenericDataItem> getLocalHashMapDataItems(){
		return localHashMapDataItems;
	}


	/**
	 * Obtém um ArrayList com os DataItems que estão a ser apresentados.
	 * @return ArrayList com os DataItems.
	 */
	public static ArrayList<IGEOGenericDataItem> getLocalListDataItems(){
		return temporaryDataItemsList;
	}


	/**
	 * Altera os dados atuais da filtragem por distrito, concelho e freguesia.
	 * @param codDistrict Códido do distrito.
	 * @param codCouncil Código do concelho.
	 * @param codParish Código da freguesia, ou null caso não estejamos a filtrar por freguesia.
	 */
	public static void setLocationSearch(String codDistrict, String codCouncil, String codParish){
		codDist = codDistrict;
		codConc = codCouncil;
		codPar = codParish;
	}


	/**
	 * Obtém o código usado na filtragem atual por distrito, concelho e freguesia.
	 * @return Caso não estejamos a filtrar por freguesia retorna o código do concelho, caso contrário,
	 * retorna o código da freguesia.
	 */
	public static String getLocationSearch(){
		if(codDist!=null)
			return codDist + codConc + (codPar!=null?codPar:"");
		else
			return null;
	}


	/**
	 * Limpa os dados relativos à filtragem por distrito, concelho e freguesia.
	 */
	public static void clearLocationSearch(){
		codDist = null;
		codConc = null;
		codPar = null;
	}


	/**
	 * Limpa os dados da filtragem por distrito.
	 */
	public static void clearDist(){
		codDist = null;
	}


	/**
	 * Limpa os dados da filtragem por concelho.
	 */
	public static void clearConc(){
		codConc = null;
	}


	/**
	 * Limpa os dados da filtragem por freguesia.
	 */
	public static void clearPar(){
		codPar = null;
	}


	/**
	 * Limpa os dados da pesquisa por palavras-chave.
	 */
	public static void clearKeywords(){
		keywords = null;
	}


	/**
	 * Obtém a lista de palavras chave que estão a ser utilizadas na pesquisa.
	 * @return String contendo as palavras-chave.
	 */
	public static String getKeywords(){
		return keywords;
	}


	/**
	 * Altera as palavras chave a serem usadas na pesquisa.
	 * @param s String com as palavras-chave a serem usadas na pesquisa.
	 */
	public static void setKeywords(String s){
		keywords = s;
	}


	/**
	 * Obtém o número atual de resultados.
	 * @return Número de DataItems obtidos na pesquisa.
	 */
	public static int getActualNumResults(){
		if(temporaryDataItemsList!=null){
			return temporaryDataItemsList.size();
		}
		else {
			return 0;
		}
	}
	
	
	
	
	
	/**
	 * Obtém as configurações por defeito, ou seja, a imagem por defeito da home, a imagem por defeito da lista, entre outras.
	 */
	public static void readJSONAppDefaultsFromServer() {
		//vai obter o JSONObject
		String strDensity = IGEOConfigsManager.densityString;

		url = IGEOConfigsManager.getAppConfigs().URLRequestAppDefaults + "&imgtype=" + strDensity + "&token="+ TOKEN;
				JSONObject jsonObj = IGEOJSONServerReader.getJSONObject(url);
		if(jsonObj!=null){
			try {
				IGEOUtils.readAppDefaultsFromJSONObject(jsonObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			strDensity = null;
		}
		
	}

}

