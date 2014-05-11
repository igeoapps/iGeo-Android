package com.bitcliq.igeo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.bitcliq.igeo.core.config.GEOAppConfigs;
import com.bitcliq.igeo.core.config.IGEOButtonElementConfig;
import com.bitcliq.igeo.core.config.IGEODetailsScreenConfig;
import com.bitcliq.igeo.core.config.IGEOGlobalConfigsItem;
import com.bitcliq.igeo.core.config.IGEOHomeScreenConfigs;
import com.bitcliq.igeo.core.config.IGEOListScreenConfig;
import com.bitcliq.igeo.core.config.IGEOListSourcesScreenConfig;
import com.bitcliq.igeo.core.config.IGEONativeMapScreenConfig;
import com.bitcliq.igeo.core.config.IGEOOptionsScreenConfig;
import com.bitcliq.igeo.core.config.IGEOScreenConfig;
import com.bitcliq.igeo.core.config.IGEOSelectLocationScreenConfigs;
import com.bitcliq.igeo.core.datasource.IGEOXMLParser;
import com.bitcliq.igeo.threads.IGEOConfigDownloadItem;
import com.bitcliq.igeo.threads.IGEOConfigDownloadItem.IGEODonwloadItemType;
import com.bitcliq.igeo.ui.IGEOApplication;


/**
 * Esta classe é responsável por ler e gerir as configurações internas da App e da UI
 * Contém métodos estáticos que retornam configurações para a App e para cada ecrã.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOConfigsManager
{
	/**
	 * Hashmap que contém para cada tipo de ecrã as suas configurações
	 */
	private static HashMap<String,IGEOScreenConfig> screenHashMapConfigs;

	/**
	 * Contém configurações da app.
	 */
	private static GEOAppConfigs myAppConfigs;

	/**
	 * Nome do parâmetro utilizado nas definições para guardar o raio de pesquisa pré-definido.
	 */
	private static final String AUTO_DETECTION_POIS_RADIUS = "search_radius";

	/**
	 * Indica quais as categorias em que são desenhados polígonos.
	 */
	public static ArrayList<String> nonDrawPolygonSources = new ArrayList<String>(); 

	/**
	 * Variável que indica qual o ficheiro de configurações iniciais que vamos utilizar nesta App.
	 * Ao alterarmos para outro ficheiro de configurações, a aparência, o name da App, as fontes de dados entre
	 * outros aspetos são alterados.
	 * Para gerar uma App diferente, além da alteração desta variável deverá ser alterado o ícone da App no ficheiro
	 * AndroidManifest.xml, alterado o nome da App em res/values/strings.xml e o packagename da aplicação.
	 */
	private static final String CONFIGS_FILE_NAME_XML = "app_patrimonio_configs.xml";
	//private static final String CONFIGS_FILE_NAME_XML = "app_natureza_configs.xml";
	//private static final String CONFIGS_FILE_NAME_XML = "app_ordenamento_configs.xml";



	
	//keys usadas para os ecrãs
	private static final String HOME = "home";
	private static final String OPTIONS = "options";
	private static final String MAP = "map";
	private static final String LIST = "list";
	private static final String DETAILS = "details";
	private static final String LIST_SOURCES = "list_sources";
	private static final String EXPLORE_SELECT = "explore_select";


	//gestão de versões das configurações
	private static String FILE_PREFIX = null;
	private static final String APP_DEFAULTS_KEY = "app_defaults";
	private static final String SOURCE_KEY = "src_";
	private static final String CATEGORY_KEY = "cat_";
	private static final String CONFIGS_FILE_NAME = "configs_app.dat";

	//nomes dos ficheiros por defeito
	private static final String HOME_DEFAULT_BG = "home_default_bg";
	private static final String LIST_DEFAULT_BG = "list_default_bg";
	private static final String HOME_BG = "home_bg_src_";
	private static final String LIST_BG = "list_bg_src_";
	private static final String CAT_DEFAULT_IMG = "cat_default_img";
	private static final String CAT_SELECTED_DEFAULT_IMG = "cat_selected_default_img";
	private static final String CAT_IMG = "cat_img_";
	private static final String CAT_SELECTED_IMG = "cat_selected_img_";
	private static final String CAT_PIN_ = "cat_pin_";

	/**
	 * Este hashmap é utilizado para guardar a versão das configurações das imagens por defeito (home), de cada fonte
	 * e de cada categoria. É através dela que podemos verificar se as configurações relativas a imagens e cores
	 * para um dado item (home, fontes ou categorias) que são recebidas no JSON, são mais recentes que as que temos
	 * no dispositivo.
	 */
	private static HashMap<String, Integer> configsVersionDictionary;
	//-------------------------------------------------------------------------------------------------------


	/**
	 * Esta string é utilizada quando passamos como parâmetro ao servidor a string indicando que tipo de imagem
	 * devemos obter. A instanciação desta variável é feita na classe IGEOHomeActicity, visto que é necessário um contexto
	 * para obter o ser valor, e é usada ao longo da App.
	 */
	public static String densityString;
	
	/**
	 * Dado um contexto define a string representativa da densidade de ecrã do dispositivo
	 * em que estamos.
	 * @param c Contexto a ser utilizado para obtensão da densidade de ecrã.
	 */
	public static void setDensityString(Context c){

		WindowManager window = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		window.getDefaultDisplay().getMetrics(metrics);
		switch(metrics.densityDpi){
		case DisplayMetrics.DENSITY_MEDIUM:
			densityString = "mdpi";
			break;
		case DisplayMetrics.DENSITY_HIGH:
			densityString = "hdpi";
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			densityString = "xhdpi";
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			densityString = "xxhdpi";
			break;
		}

	}





	/**
	 * Construtor por defeito da classe IGEOConfigsManager.
	 */
	public IGEOConfigsManager(){
		super();

		screenHashMapConfigs = new HashMap<String,IGEOScreenConfig>();
	}


	/**
	 * Devolve o título da App.
	 * @return String com o título da App atual
	 */
	public static String getAppName() {
		return myAppConfigs.appName;
	}


	/**
	 * Devolve uma String com o título do package da App.
	 * @return String com o título do package da App
	 */
	public static String getAppPackage() {
		return myAppConfigs.appPackageName;
	}






	/**
	 * Obtém as configurações da App relativas url's utilizados, ao packagename, ao nome da app, etc.
	 * @return Configurações da App
	 */
	public static GEOAppConfigs getAppConfigs() {
		return myAppConfigs;	
	}


	/**
	 * Obtém as configurações dos ecrãs
	 * @return HashMap com as configurações de ecrã indexadas pelo nome de cada ecrã.</br>
	 * Os nomes atualmente utilizados são:</br>
	 * . Home: HOME</br>
	 * . Lista de Sources: LIST_SOURCES</br>
	 * . Filtragem no explore: EXPLORE_SELECT</br>
	 * . Info: "info"</br>
	 * . Escolha das categorias: OPTIONS</br>
	 * . Mapa: MAP</br>
	 * . Lista: LIST</br>
	 * . Detalhes: DETAILS</br>
	 */
	public static HashMap<String,IGEOScreenConfig> getScreensConfigs() {
		return screenHashMapConfigs;
	}







	/**
	 * Obtém das definições da App o raio atual para pesquisa de DataItens.
	 * @param a Actividade atual.
	 * @return Raio atual para pesquisa de DataItens.
	 */
	public static int getAutoDetectionPOISRadius(Activity a){
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(a);
			String imgSett = prefs.getString(AUTO_DETECTION_POIS_RADIUS, "");
			int pos = Integer.parseInt(imgSett);

			switch(pos){

			case 1:
				return 1000;
			case 2:
				return 2000;
			case 3:
				return 5000;
			case 4:
				return 10000;

			}

			prefs = null;
			imgSett = null;

		} catch(Throwable e){
			//e.printStackTrace();

			//valor por defeito
			return 10000;
		}

		return -1;
	}





	/**
	 * Usado para ir obter as configurações atuais e construir os objetos  definidos nesta classe.
	 * A ideia será ir à directoria das configurações ler o ficheiro XML, fazer o parsing e colocá-las nas variáveis desta
	 * classe.
	 * @param c Contexto a utilizar.
	 */
	public static void applyCurrentConfigs(Context c){
		
		//o prefixo para o nome dos ficheiros a serem guardados é o nome da App. 
		FILE_PREFIX = c.getResources().getString(c.getApplicationInfo().labelRes);

		//Verifica se o ficheiro das configurações existe. O nome do ficheiro, é  precedido do nome da App para que exista um ficheiro por App
		//sem que estes entrem em conflito quando temos várias App's instaladas.
		if(IGEOFileUtils.fileExists(IGEOFileUtils.APP_FOLDER + File.separator + IGEOConfigsManager.CONFIGS_FILE_NAME.replace(".dat", "_"+c.getResources().getString(c.getApplicationInfo().labelRes)+".dat"))){
			IGEOGlobalConfigsItem saveItem = null;

			try {
				System.out.println("Loading configs");
				FileInputStream fin = new FileInputStream(IGEOFileUtils.APP_FOLDER + File.separator + IGEOConfigsManager.CONFIGS_FILE_NAME.replace(".dat", "_"+c.getResources().getString(c.getApplicationInfo().labelRes)+".dat"));
				ObjectInputStream ois = new ObjectInputStream(fin);   
				Object o = ois.readObject();
				if(o!=null) {
					if(o instanceof IGEOGlobalConfigsItem){
						saveItem = (IGEOGlobalConfigsItem) o;
					}
				}
				ois.close();
				System.out.println("Configs loaded");
				
				//se houve um erro na leitura do ficheiro
				if(saveItem==null){
					String xmlConfigsStr = null; 
					try{
						InputStream descriptor = c.getAssets().open(CONFIGS_FILE_NAME_XML);
						xmlConfigsStr = IGEOFileUtils.readFileToStringFromStream(descriptor);
					}
					catch(Exception e){
						e.printStackTrace();
					}

					myAppConfigs = IGEOXMLParser.parseAppConfigs(xmlConfigsStr);
					screenHashMapConfigs = IGEOXMLParser.parseScreensConfigs(xmlConfigsStr);
					
					xmlConfigsStr = null;
				}
				
				//se o conteúdo do ficheiro foi carregado sem problemas
				else {
					myAppConfigs = saveItem.appConfigs;
					screenHashMapConfigs = saveItem.screenHashMapConfigs;
					configsVersionDictionary = saveItem.configsVersionDictionary;
				}
				
				fin = null;
				ois = null;
				o = null;
				
				System.gc();
			}
			catch(Exception e){
				System.out.println("Fail loading configs");
			}
		}

		else {
			String xmlConfigsStr = null; 
			try{
				InputStream descriptor = c.getAssets().open(CONFIGS_FILE_NAME_XML);
				xmlConfigsStr = IGEOFileUtils.readFileToStringFromStream(descriptor);
				
				descriptor = null;
			}
			catch(Exception e){
				e.printStackTrace();
			}

			myAppConfigs = IGEOXMLParser.parseAppConfigs(xmlConfigsStr);
			screenHashMapConfigs = IGEOXMLParser.parseScreensConfigs(xmlConfigsStr);
			
			xmlConfigsStr = null;
		}

	}


	/**
	 * Obtém as configurações para um ecrã passando-lhes o name do mesmo. Para que funcione as
	 * configurações devem já ter sido lidas.
	 * Os nomes atualmente utilizados são:</br>
	 * . Home: HOME</br>
	 * . Lista de Sources: LIST_SOURCES</br>
	 * . Filtragem no explore: EXPLORE_SELECT</br>
	 * . Info: "info"</br>
	 * . Escolha das categorias: OPTIONS</br>
	 * . Mapa: MAP</br>
	 * . Lista: LIST</br>
	 * . Detalhes: DETAILS</br>
	 * @param screenName Nome do ecrã.
	 * @return Configurações do ecrã.
	 */
	public static IGEOScreenConfig getConfigsForScreen(String screenName) {
		if(screenName.equals(HOME)){
			return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME));
		}
		return null;
	}


	/**
	 * Obtém o URL para a imagem de fundo a usar na home dependendo da fonte selecionada.
	 * @param srcID Identificador da fonte selecionada.
	 * @return String com o URL para a imagem de fundo a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getBackgroundImageForSource(String srcID){
		if(((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).getBackgroundImageForCategoryName(srcID)!=null)
			return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).getBackgroundImageForCategoryName(srcID);
		else
			return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).getBackgroundImageForCategoryName("-1");
	}


	/**
	 * Obtém o URL para a imagem de fundo a usar na lista dependendo da fonte selecionada.</br>
	 * @param srcID Identificador da fonte selecionada.
	 * @return String com o URL para a imagem de fundo a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getBackgroundRightImageForSource(String srcID){
		if(((IGEOListScreenConfig) screenHashMapConfigs.get(LIST)).bgForSource.get(srcID) != null)
			return ((IGEOListScreenConfig) screenHashMapConfigs.get(LIST)).bgForSource.get(srcID);
		else
			return ((IGEOListScreenConfig) screenHashMapConfigs.get(LIST)).bgForSource.get("-1");
	}


	/**
	 * Obtém o URL do ícone a ser utilizado na seleção de categorias, para uma dada fonte e categoria.
	 * @param sourceID Identificador da fonte.
	 * @param categoryID Identificador da categoria.
	 * @return String com o URL para o ícone a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getIconForCategory(String sourceID, String categoryID){

		if(((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.containsKey(sourceID+"_"+categoryID))
			return ((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.get(sourceID+"_"+categoryID).imageNormalConfigs;
		else
			return ((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.get("-1").imageNormalConfigs;

	}


	/**
	 * Obtém o URL do ícone selecionado a ser utilizado na seleção de categorias, para uma dada fonte e categoria.
	 * @param sourceID Identificador da fonte.
	 * @param categoryID Identificador da categoria.
	 * @return String com o URL para o ícone a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getIconForCategorySelected(String sourceID, String categoryID){
		if(((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.containsKey(sourceID+"_"+categoryID))
			return ((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.get(sourceID+"_"+categoryID).imageClickConfigs;
		else
			return ((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).buttonsOptionConf.get("-1").imageClickConfigs;

	}


	/**
	 * Obtém a cor a ser utilizada na marcação de pontos num Google Map, para uma fonte e categoria.
	 * @param categoryID Identificador da categoria.
	 * @return Hue da cor a ser utilizada.
	 */
	public static float getColorForCategory(String categoryID){
		return ((IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP)).getPinColorForCategory(IGEODataManager.getActualSource().sourceID, categoryID).floatValue();
	}


	/**
	 * Obtém a cor a ser utilizada nos títulos da lista de DataItems, para uma fonte e categoria.
	 * @param categoryID Identificador da categoria.
	 * @return String com a cor em hexadecimal.
	 */
	public static String getColorForHTMLCategory(String categoryID){
		return ((IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP)).getColorHTMLForCategory(IGEODataManager.getActualSource().sourceID, categoryID);
	}


	/**
	 * Obtém o URL do pin usado na legenda do mapa, para uma dada fonte e categoria.
	 * @param sourceID Identificador da fonte.
	 * @param categoryID Identificador da categoria.
	 * @return String com o URL para o ícone a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getPinIconForCategory(String sourceID, String categoryID){
		return ((IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP)).getPinLegendForCategory(IGEODataManager.getActualSource().sourceID, categoryID);
	}


	/**
	 * Obtém a cor usada para o desenho dos poligonos, para uma dada fonte e categoria.
	 * @param categoryID Identificador da categoria.
	 * @return Inteiro com a representação em hexadecimal da cor a ser usada.
	 */
	public static int getPolygonLineColorForCategory(String categoryID){
		return ((IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP)).getPolygonColorForCategory(IGEODataManager.getActualSource().sourceID, categoryID);
	}


	/**
	 * Obtém a cor usada como cor de fundo dos polígonos, para uma dada fonte e categoria.
	 * @param categoryID Identificador da categoria.
	 * @return Inteiro com a representação em hexadecimal da cor a ser usada.
	 */
	public static int getPolygonBackgroundColorForCategory(String categoryID){
		return ((IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP)).getPolygonBackgroundColorForCategory(IGEODataManager.getActualSource().sourceID, categoryID);
	}


	/**
	 * Obtém a imagem usada como imagem de topo nos ecrãs da home, lista de fontes, filtragem do explore e seleção
	 * de categorias.
	 * @return String com o URL para o ícone a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getTopImage(){
		return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).urlTopImage;
	}


	/**
	 * Obtém a String a colocar no ecrã da Home como título da App.
	 * @return String a colocar no ecrã da Home como título da App.
	 */
	public static String getAppTitle(){
		return myAppConfigs.appName.toUpperCase();
	}


	/**
	 * Obtém o URL de cada um dos botões principais da home, isto é, "Lista de Fontes", "Perto de Mim" e "Explore", no
	 * @return Array com 3 posições, cuja ordem é "Lista de Fontes", "Perto de Mim" e "Explore"
	 * Caso as Strings obtidas não sejam precedidas de
	 * "file://" ou "http://" consideramos que se tratam de imagens contidas nos resources do projeto.
	 */
	public static String[] getHomeIcons(){
		return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).getBtnsNormal();
	}


	/**
	 * Obtém o URL de cada um dos botões principais da home, isto é, "Lista de Fontes", "Perto de Mim" e "Explore", no
	 * seu estado seleccionado.
	 * @return Array com 3 posições, cuja ordem é "Lista de Fontes", "Perto de Mim" e "Explore"
	 * Caso as Strings obtidas não sejam precedidas de
	 * "file://" ou "http://" consideramos que se tratam de imagens contidas nos resources do projeto.
	 */
	public static String[] getHomeIconsClicked(){
		return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).getBtnsClicked();
	}


	/**
	 * Devolve a cor a ser utilizada na seleção dos itens da lista de fontes.
	 * @return String com a cor em hexadecimal, a ser usada na seleção da lista de fontes.
	 */
	public static String getListsSelectionColor(){
		return ((IGEOListSourcesScreenConfig) screenHashMapConfigs.get(LIST_SOURCES)).colorSelection;
	}


	/**
	 * Obtém a cor base utilizada na App.
	 * @return String com a cor base da App em hexadecimal.
	 */
	public static String getAppColor(){
		return myAppConfigs.appColor;
	}


	/**
	 * Obtém a cor utilizada no fundo do subtítulo do ecrã da Home.
	 * @return String com a cor de fundo do subtitulo do ecrã da Home base da App em hexadecimal.
	 */
	public static String getSubtitleBackground(){
		return ((IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME)).subTitleBg;
	}


	/**
	 * Obtém o URL da imagem usada no clique dos itens de seleção da localização na filtragem por distrito,
	 * concelho e freguesia.
	 * @return String com o URL para a imagem a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getLocationSelectClickBackground(){
		return ((IGEOSelectLocationScreenConfigs) screenHashMapConfigs.get(EXPLORE_SELECT)).bSelection.imageClickConfigs;
	}


	/**
	 * Obtém o URL da imagem usada no botão de ida para o mapa.
	 * @return String com o URL para a imagem a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getLocationSelectBackground(){
		//return MockupGetConfigs.getLocationSelectBackground();
		return ((IGEOSelectLocationScreenConfigs) screenHashMapConfigs.get(EXPLORE_SELECT)).bSelection.imageNormalConfigs;
	}


	/**
	 * Obtém o URL da imagem usada no botão de ida para o mapa (estado seleccionado).
	 * @return String com o URL para a imagem a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getBtnMapClickBackground(){
		return ((IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS)).okButtonConfig.imageClickConfigs;
	}


	/**
	 * Obtém o URL da imagem usada por defeito nos detalhes dos itens.
	 * @return String com o URL para a imagem a utilizar. Caso a String obtida não seja precedida de
	 * "file://" ou "http://" consideramos que se trata de uma imagem contida nos resources do projeto.
	 */
	public static String getDefaultImage(){
		return ((IGEODetailsScreenConfig) screenHashMapConfigs.get(DETAILS)).defaultImageURL;
	}




	//ALTERAR CATEGORIES --------------------------------------------------------------------------------------------------
	/**
	 * Altera a cor utilizada nos títulos da lista para uma categoria de uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param catID Identificador dacategoria.
	 * @param color Cor em hexadecimal.
	 */
	public static void changeTitleListColorForSourceAndCategory(String srcID, String catID, String color){

		IGEONativeMapScreenConfig mapConfigs = (IGEONativeMapScreenConfig) IGEOConfigsManager.getScreensConfigs().get(MAP);

		if(catID.equals("-1"))
			mapConfigs.colorHTMLCategory.put(catID, color);
		else
			mapConfigs.colorHTMLCategory.put(srcID + "_" + catID, color);

		IGEOConfigsManager.screenHashMapConfigs.put(MAP, mapConfigs);
	}


	/**
	 * Altera a cor utilizada no desenho da linha dos poligonos do mapa para uma categoria de uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param catID Identificador da categoria.
	 * @param color Representação da cor em hexadecimal, seguindo o formato do exemplo: "0x00FFFF"
	 */
	public static void changePolygonLineColorForSourceAndCategory(String srcID, String catID, String color) {

		IGEONativeMapScreenConfig mapConfigs = (IGEONativeMapScreenConfig) IGEOConfigsManager.getScreensConfigs().get(MAP);

		if(color!=null) {
			Integer colorHex = Integer.decode(color);

			if(catID.equals("-1"))
				mapConfigs.polygonColorCategory.put(catID, colorHex);
			else
				mapConfigs.polygonColorCategory.put(srcID + "_" + catID, colorHex);

			IGEOConfigsManager.screenHashMapConfigs.put(MAP, mapConfigs);
		}
	}


	/**
	 * Altera a cor utilizada no preenchimento dos polígonos do mapa para uma categoria de uma determinada fonte.
	 * @param srcID Identificador da fonte.
	 * @param catID Identificador da categoria.
	 * @param color Representação da cor em hexadecimal, seguindo o formato do exemplo: "0x00FFFF"
	 */
	public static void changePolygonBackgroundColorForSourceAndCategory(String srcID, String catID, String color) {

		IGEONativeMapScreenConfig mapConfigs = (IGEONativeMapScreenConfig) IGEOConfigsManager.getScreensConfigs().get(MAP);

		if(color!=null) {
			Integer colorHex = Integer.decode(color);

			if(catID.equals("-1"))
				mapConfigs.polygonBackgroundColorCategory.put(catID, colorHex);
			else
				mapConfigs.polygonBackgroundColorCategory.put(srcID + "_" + catID, colorHex);

			IGEOConfigsManager.screenHashMapConfigs.put(MAP, mapConfigs);
		}
	}


	/**
	 * Altera a cor utilizada nos pins do mapa para uma categoria de uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param catID Identificador da categoria.
	 * @param color Hue da cor numa String. Ex.: "255.0".
	 */
	public static void changePinColorForSourceAndCategory(String srcID, String catID, String color) {

		IGEONativeMapScreenConfig mapConfigs = (IGEONativeMapScreenConfig) IGEOConfigsManager.getScreensConfigs().get(MAP);

		if(color!=null) {
			Float hue = Float.parseFloat(color);

			if(catID.equals("-1"))
				mapConfigs.colorPinsCategory.put(catID, hue);
			else
				mapConfigs.colorPinsCategory.put(srcID + "_" + catID, hue);

			IGEOConfigsManager.screenHashMapConfigs.put(MAP, mapConfigs);
		}
	}



	/**
	 * Altera o pin a ser utilizado na legenda do mapa para uma categoria de uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param catID Identificador da categoria.
	 * @param url URL da imagem.
	 * @param colorHex Cor em hexadecimal a adicionar à categoria.
	 * @param colorHue Hue da cor a adicionar à categoria que será usada na colocação dos pins.
	 */
	public static void changePinMapImageForSourceAndCategory(String srcID, String catID, String url, String colorHex, String colorHue) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName = FILE_PREFIX + "_" + CAT_PIN_ + srcID + "_" + catID + "." + extension;

		//Lançar a thread para obtenção das imagens
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, catID, IGEODonwloadItemType.CatPinImage, colorHex, colorHue)
				);
		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem do botão não selecionado, da categoria a ser utilizado no ecrã de escolha de categorias, para uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @param url URL da imagem.
	 */
	public static void changeCategoryIconNormalForSourceAndCategory(String srcID, String catID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName = FILE_PREFIX + "_" + CAT_IMG + srcID + "_" + catID + "." + extension;

		//Lançar a thread para obtenção das imagens
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, catID, IGEODonwloadItemType.CatIconNormal)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem por defeito, do botão não selecionado, da categoria a ser utilizado no ecrã de escolha de categorias.
	 * @param srcID ID da fonte.
	 * @param url URL da imagem.
	 */
	public static void changeCategoryIconNormalForSourceDefaultUrl(String srcID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName = FILE_PREFIX + "_" + CAT_DEFAULT_IMG + srcID + "." + extension;

		//Lançar a thread para obtenção das imagens
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, "-1", IGEODonwloadItemType.CatPinImage)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem do botão selecionado, da categoria a ser utilizado no ecrã de escolha de categorias, para uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @param url URL da imagem.
	 */
	public static void changeCategoryIconSelectedForSourceAndCategory(String srcID, String catID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName = FILE_PREFIX + "_" + CAT_SELECTED_IMG + srcID + "_" + catID + "." + extension;

		//Lançar a thread para obtenção das imagens
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, catID, IGEODonwloadItemType.CatIconSelected)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem por defeito, do botão selecionado, da categoria a ser utilizado no ecrã de escolha de categorias.
	 * @param srcID ID da fonte.
	 * @param url URL da imagem.
	 */
	public static void changeCategoryIconSelectedForSourceDefaultUrl(String srcID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName = FILE_PREFIX + "_" + CAT_SELECTED_DEFAULT_IMG + srcID + "." + extension;

		//Lançar a thread para obtenção das imagens
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, "-1", IGEODonwloadItemType.CatIconSelected)
				);

		extension = null;
		fileName = null;
	}
	//--


	
	
	
	
	
	
	
	//ALTERAR SOURCES --------------------------------------------------------------------------------------------------
	/**
	 * Altera a imagem de fundo do ecrã da home para uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param url URL da imagem.
	 */
	public static void changeBackgroundImageHomeForSource(String srcID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName =  FILE_PREFIX + "_" + HOME_BG + srcID + "." + extension;

		//Lançar a thread
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, "-1", IGEODonwloadItemType.HomeBgImage)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem por defeito, do fundo do ecrã da home.
	 * @param url URL da imagem.
	 */
	public static void changeBackgroundImageHomeDefaultUrl(String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName =  FILE_PREFIX + "_" + HOME_DEFAULT_BG + "." + extension;

		//Lançar a thread
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, "-1", "-1", IGEODonwloadItemType.HomeBgImage)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem de fundo do ecrã da lista de itens para uma fonte.
	 * @param srcID Identificador da fonte.
	 * @param url URL da imagem.
	 */
	public static void changeBackgroundImageListForSource(String srcID, String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName =  FILE_PREFIX + "_" + LIST_BG + srcID + "." + extension;

		//Lançar a thread
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, srcID, "-1", IGEODonwloadItemType.ListBGImage)
				);

		extension = null;
		fileName = null;
	}


	/**
	 * Altera a imagem por defeito, do fundo do ecrã da lista de itens.
	 * @param url URL da imagem.
	 */
	public static void changeBackgroundImageListDefaultUrl(String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1);

		String fileName =  FILE_PREFIX + "_" + LIST_DEFAULT_BG + "." + extension;

		//Lançar a thread
		IGEOApplication.downloadConfigsThread.addItemToDownload(
				new IGEOConfigDownloadItem(url, fileName, "-1", "-1", IGEODonwloadItemType.ListBGImage)
				);

		extension = null;
		fileName = null;
	}
	//--








	
	


	//métodos sicronos para alteração das imagens das configs
	//estes métodos são chamados quando termonamos a obtenção das configs
	/**
	 * Método para alteração de forma sícrona da imagem de fundo na home para uma fonte.
	 * @param srcID ID da fonte ou "-1" se se tratar da imagem por defeito.
	 * @param url URL da imagem.
	 */
	public static void setHomeBgImageForSource(String srcID, String url) {
		IGEOHomeScreenConfigs hsConfigs = (IGEOHomeScreenConfigs) screenHashMapConfigs.get(HOME);
		hsConfigs.backgroundConf.put(srcID, url);

		screenHashMapConfigs.put(HOME, hsConfigs);
	}


	/**
	 * Método para alteração de forma sícrona da imagem de fundo na lista para uma fonte.
	 * @param srcID ID da fonte ou "-1" se se tratar da imagem por defeito.
	 * @param url URL da imagem.
	 */
	public static void setListBgImageForSource(String srcID, String url) {
		IGEOListScreenConfig lConfigs = (IGEOListScreenConfig) screenHashMapConfigs.get(LIST);
		lConfigs.bgForSource.put(srcID, url);

		screenHashMapConfigs.put(LIST, lConfigs);
	}


	/**
	 * Método para alteração de forma sícrona da imagem do icone não selecionado do ecrã de seleção de categorias, para uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria ou "-1" caso se trate da imagem por defeito.
	 * @param url URL da imagem.
	 */
	public static void setCatIconNormalForSourceAndCategory(String srcID, String catID, String url) {
		IGEOOptionsScreenConfig optsConfigs = (IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS);

		if(catID.equals("-1")){
			IGEOButtonElementConfig btnTmp =  optsConfigs.buttonsOptionConf.get("-1");
			btnTmp.imageNormalConfigs = url;

			optsConfigs.buttonsOptionConf.put("-1", btnTmp);

			btnTmp = null;
		}
		else {
			if(optsConfigs.buttonsOptionConf.containsKey(srcID+"_"+catID)) {
				IGEOButtonElementConfig btnTmp = optsConfigs.buttonsOptionConf.get(srcID+"_"+catID);
				btnTmp.imageNormalConfigs = url;

				optsConfigs.buttonsOptionConf.put(srcID+"_"+catID, btnTmp);

				btnTmp = null;
			}
			else {
				IGEOButtonElementConfig btnTmp = new IGEOButtonElementConfig();
				btnTmp.imageNormalConfigs = url;

				optsConfigs.buttonsOptionConf.put(srcID+"_"+catID, btnTmp);

				btnTmp = null;
			}
		}

		screenHashMapConfigs.put(OPTIONS, optsConfigs);

		optsConfigs = null;
	}


	/**
	 * Método para alteração de forma sícrona da imagem do icone selecionado do ecrã de seleção de categorias, para uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria ou "-1" caso se trate da imagem por defeito.
	 * @param url URL da imagem.
	 */
	public static void setCatIconSelectedForSourceAndCategory(String srcID, String catID, String url) {
		IGEOOptionsScreenConfig optsConfigs = (IGEOOptionsScreenConfig) screenHashMapConfigs.get(OPTIONS);

		if(catID.equals("-1")){
			IGEOButtonElementConfig btnTmp =  optsConfigs.buttonsOptionConf.get("-1");
			btnTmp.imageClickConfigs = url;

			optsConfigs.buttonsOptionConf.put("-1", btnTmp);

			btnTmp = null;
		}
		else {
			if(optsConfigs.buttonsOptionConf.containsKey(srcID+"_"+catID)) {
				IGEOButtonElementConfig btnTmp = optsConfigs.buttonsOptionConf.get(srcID+"_"+catID);
				btnTmp.imageClickConfigs = url;

				optsConfigs.buttonsOptionConf.put(srcID+"_"+catID, btnTmp);

				btnTmp = null;
			}
			else {
				IGEOButtonElementConfig btnTmp = new IGEOButtonElementConfig();
				btnTmp.imageClickConfigs = url;

				optsConfigs.buttonsOptionConf.put(srcID+"_"+catID, btnTmp);

				btnTmp = null;
			}
		}

		screenHashMapConfigs.put(OPTIONS, optsConfigs);

		optsConfigs = null;
	}


	/**
	 * Método para alteração de forma sícrona da imagem do pin da legenda do mapa, para uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria ou "-1" caso se trate da imagem por defeito.
	 * @param url URL da imagem.
	 */
	public static void setCatPinImageForSourceAndCategory(String srcID, String catID, String url) {
		IGEONativeMapScreenConfig mapConfigs = (IGEONativeMapScreenConfig) screenHashMapConfigs.get(MAP);

		if(catID.equals("-1")){
			String pinTmp =  mapConfigs.pinLegendCategory.get("-1");
			pinTmp = url;

			mapConfigs.pinLegendCategory.put("-1", pinTmp);

			pinTmp = null;
		}
		else {
			mapConfigs.pinLegendCategory.put(srcID+"_"+catID, url);
		}

		screenHashMapConfigs.put(MAP, mapConfigs);

		mapConfigs = null;
	}
	//-------------------------------------------------------------------------------------------------------------------





	//GESTÃO DAS VERSÕES DAS CONFIGS ------------------------------------------------------------------------------------
	/**
	 * Obtém a versão das configurações por default da App
	 * @return Número da versão das configurações por default da App
	 */
	public static int getVersionNumberForAppDefaults() {
		if(configsVersionDictionary == null){
			configsVersionDictionary = new HashMap<String, Integer>();
		}

		if(!configsVersionDictionary.containsKey(APP_DEFAULTS_KEY)){
			configsVersionDictionary.put(APP_DEFAULTS_KEY, new Integer(-1));
		}

		return configsVersionDictionary.get(APP_DEFAULTS_KEY).intValue();
	}


	/**
	 * Altera a versão das configurações por default da App.
	 * @param version Número da versão das configurações por default da App.
	 */
	public static void setVersionNumberForAppDefaults(int version) {
		configsVersionDictionary.put(APP_DEFAULTS_KEY, new Integer(version));
	}


	/**
	 * Obtém a versão das configurações de uma fonte.
	 * @param srcID ID da fonte.
	 * @return Número da versão das configurações da fonte.
	 */
	public static int getVersionNumberForSource(String srcID) {
		if(configsVersionDictionary == null){
			configsVersionDictionary = new HashMap<String, Integer>();
		}

		if(!configsVersionDictionary.containsKey(SOURCE_KEY + srcID)){
			configsVersionDictionary.put(SOURCE_KEY + srcID, new Integer(-1));
		}

		return configsVersionDictionary.get(SOURCE_KEY + srcID).intValue();
	}


	/**
	 * Altera a versão das configurações de uma fonte.
	 * @param version Número da versão das configurações da fonte.
	 * @param srcID ID da fonte
	 */
	public static void setVersionNumberForSource(int version, String srcID) {
		configsVersionDictionary.put(SOURCE_KEY, new Integer(version));
	}


	/**
	 * Obtém a versão das configurações de uma categoria de uma fonte.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 * @return Número da versão das configurações da categoria.
	 */
	public static int getVersionNumberForSourceAndCategory(String srcID, String catID) {
		if(configsVersionDictionary == null){
			configsVersionDictionary = new HashMap<String, Integer>();
		}

		if(!configsVersionDictionary.containsKey(CATEGORY_KEY + catID + "_ " + srcID)){
			configsVersionDictionary.put(CATEGORY_KEY + catID + "_ " + srcID, new Integer(-1));
		}

		return configsVersionDictionary.get(CATEGORY_KEY + catID + "_ " + srcID).intValue();
	}


	/**
	 * Altera a versão das configurações de uma categoria de uma fonte.
	 * @param version Número da versão das configurações da categoria.
	 * @param srcID ID da fonte.
	 * @param catID ID da categoria.
	 */
	public static void setVersionNumberForSource(int version, String srcID, String catID) {
		configsVersionDictionary.put(CATEGORY_KEY + catID + "_ " + srcID, new Integer(version));
	}








	//DEFAULT CONFIGS --------------------------------------------------------------------------------------------------
	/**
	 * Obtém as configurações por defeito, ou seja, a imagem por defeito da home e a imagem por defeito da lista.
	 */
	public static void readDefaultConfigs(){
		IGEODataManager.readJSONAppDefaultsFromServer();
	}
	//--




	//GUARDAR AS CONFIGS
	/**
	 * Guarda localmente num ficheiro as configurações de ecrã da App.
	 */
	public static void writeDefaultConfigs(Context c){
		IGEOGlobalConfigsItem saveItem = new IGEOGlobalConfigsItem(myAppConfigs, screenHashMapConfigs, configsVersionDictionary);
		if(saveItem.appConfigs.appSourcesHashMap!=null)
			saveItem.appConfigs.appSourcesHashMap.clear();
		saveItem.appConfigs.appSourcesHashMap = null;

		try {
			System.out.println("Saving configs");
			FileOutputStream fout = new FileOutputStream(IGEOFileUtils.APP_FOLDER + File.separator + IGEOConfigsManager.CONFIGS_FILE_NAME.replace(".dat", "_"+c.getResources().getString(c.getApplicationInfo().labelRes)+".dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(saveItem);
			oos.close();
			System.out.println("Configs saved");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Fail saving configs");
		}
	}
	//----------------------------------------------------------------------------------------------------------------------


}

