package com.bitcliq.igeo.core.datasource;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.bitcliq.igeo.core.config.IGEOButtonElementConfig;
import com.bitcliq.igeo.core.config.IGEODetailsScreenConfig;
import com.bitcliq.igeo.core.config.IGEOHomeScreenConfigs;
import com.bitcliq.igeo.core.config.GEOAppConfigs;
import com.bitcliq.igeo.core.config.IGEOInfoScreenConfig;
import com.bitcliq.igeo.core.config.IGEOListScreenConfig;
import com.bitcliq.igeo.core.config.IGEOListSourcesScreenConfig;
import com.bitcliq.igeo.core.config.IGEONativeMapScreenConfig;
import com.bitcliq.igeo.core.config.IGEOOptionsScreenConfig;
import com.bitcliq.igeo.core.config.IGEOScreenConfig;
import com.bitcliq.igeo.core.config.IGEOSelectLocationScreenConfigs;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


/**
 * Parser XML usado para ler as configurações.
 * Este parser é usado para que as configurações
 * dos ecrãs possam ser guardadas em ficheiros XML, facilitando a criação de novas App's alterando apenas esses ficheiros
 * e fazendo alterações no aspeto gráfico das App's existentes.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOXMLParser implements Serializable
{



	//Keys dos elementos principais
	/**
	 * Lista de keys a ler nos nós do tipo "GEOAppConfigs"
	 */
	private static final String[] appConfigsNodeKeyList = new String[] { "Name",
		"URLUpdate", "PackageName", "URLRequests", "URLRequestsSources", "URLRequestsCategories", "URLRequestAppDefaults", "AppColor"};

	/**
	 * Lista de keys a ler nos nós do tipo "IGEOHomeScreenConfigs"
	 */
	private static final String[] homeScreenConfigsNodeKeyList = new String[] { "TopImage",
		"BackgroundForSource", "HomeButtons", "SubTitleBg"};

	/**
	 * Lista de keys a ler nos nós do tipo "ListSourcesScreenConfigs"
	 */
	private static final String[] listSourcesScreenConfigsNodeKeyList = new String[] { "TopImage", "SelectionColor"};

	/**
	 * Lista de keys a ler nos nós do tipo "IGEOSelectLocationScreenConfigs"
	 */
	private static final String[] selectLocationScreenConfigsNodeKeyList = new String[] { "TopImage",
	"Buttons"};

	/**
	 * Lista de keys a ler nos nós do tipo "OptionsScreenConfigs"
	 */
	private static final String[] optionsScreenConfigsNodeKeyList = new String[] { "TopImage",
		"ButtonElementConfig", "ButtonsForCategory"/*, ""*/};

	/**
	 * Lista de keys a ler nos nós do tipo "NativeMapScreenConfigs"
	 */
	private static final String[] mapScreenConfigsNodeKeyList = new String[] { "PinColorsForCategory", "DefaultPinColor"};

	/**
	 * Lista de keys a ler nos nós do tipo "ListScreenConfigs"
	 */
	private static final String[] listScreenConfigsNodeKeyList = new String[] { "BackgroundForSource"};

	/**
	 * Lista de keys a ler nos nós do tipo "DetailsScreenConfigs"
	 */
	private static final String[] detailsScreenConfigsNodeKeyList = new String[] { "URLDefaultImage"};
	//--



	//Keys de outros elementos
	/**
	 * Lista de keys a ler nos nós do tipo "IGEOButtonElementConfig"
	 */
	private static final String[] buttonElementConfigKeyList = new String[] { "ButtonName", "ImageNormal", "ImageClick"};

	/**
	 * Lista de keys a ler nos nós do tipo "PinColorsForCategoryEntry"
	 */
	private static final String[] pinColorsForCategoryEntryKeyList = new String[] { "SourceID", "CategoryID", "Color"};
	//--



	//Associação das cores
	//Eses arrays permitem que, através de uma lista de nomes pré-definidos, se obtenha a cor correspondente, por defeito, nas suas diferentes vertentes:
	//cor do pin do Google Maps, cor html a usar nos títulos das listas, imagem a usar na legenda do mapa, cor da linha dos poligonos,
	//e fundo dos poligonos.
	//As cores disponíveis por defeito são: 
	//Amarelo: YELLOW, azul: BLUE, magenta: MAGENTA, laranja: ORANGE, cor-de-rosa: ROSE, violeta: VIOLET, verde: GREEN, azul cian: CYAN,
	//azul claro: AZURE, vermelho: RED

	/**
	 * Valores das cores do pin do Google Maps para cada posição
	 */
	private static final float[] pinColors = new float[] {
		BitmapDescriptorFactory.HUE_YELLOW,
		BitmapDescriptorFactory.HUE_BLUE,
		BitmapDescriptorFactory.HUE_MAGENTA,
		BitmapDescriptorFactory.HUE_ORANGE,
		BitmapDescriptorFactory.HUE_ROSE,
		BitmapDescriptorFactory.HUE_VIOLET,
		BitmapDescriptorFactory.HUE_GREEN,
		BitmapDescriptorFactory.HUE_CYAN,
		BitmapDescriptorFactory.HUE_AZURE,
		BitmapDescriptorFactory.HUE_RED,

		BitmapDescriptorFactory.HUE_CYAN
	};

	/**
	 * Valores das cores HTML, por defeito, para os títulos da lista para cada posição
	 */
	private static final String[] htmlColors = new String[] {
		"#FFCC66",
		"#0000CC",
		"#380000",
		"#CC3300",
		"#CC0099",
		"#CC00FF",
		"#336600",
		"#00FFFF",
		"#00CCCC",
		"#AA0000",

		"#00FFFF"
	};

	/**
	 * Nome da imagem do pin de legenda por defeito, que se encontra nos resources, associada a cada posição.
	 */
	private static final String[] legendPin = new String[] {
		"yellow",
		"blue",
		"magenta",
		"orange",
		"rose",
		"violet",
		"green",
		"cyan",
		"azure",
		"red",

		"cyan"
	};

	/**
	 * Valores das cores da linha, por defeito, do polígono para cada posição
	 */
	private static final int[] polygonLineColor = new int[] {
		0xFFFFCC66,
		0xFF0000CC,
		0xFF380000,
		0xFFCC3300,
		0xFFCC0099,
		0xFFCC00FF,
		0xFF33CC00,
		0xFF00FFFF,
		0xFF00CCCC,
		0xFFAA0000,

		0xFF00FFFF
	};

	/**
	 * Valores das cores de fundo, por defeito, do polígono para cada posição
	 */
	private static final int[] polygonBackgroundColor = new int[] {
		0x66FFCC66,
		0x660000CC,
		0x66380000,
		0x66CC3300,
		0x66CC0099,
		0x66CC00FF,
		0x6633CC00,
		0x6600FFFF,
		0x6600CCCC,
		0x66AA0000,

		0x6600FFFF
	};

	/**
	 * A cada cor é associada uma posição. Isto permite definir que uma dada categoria tem uma determinada cor,
	 * e que possamos associar automaticamente a sua cor em hexadecimal, a sua cor relativa ao pin Google Maps, a
	 * cor dos seus poligonos e do preenchimento dos mesmos, bem como a sua imagem a usar na legenda.
	 */
	public static HashMap<String,Integer> posForColor;

	/**
	 * Inicializa o HashMap com as cores ás posições respetivas.
	 */
	public static void constructHashMapColors(){
		posForColor = new HashMap<String,Integer>();
		posForColor.put("YELLOW", 0);
		posForColor.put("BLUE", 1);
		posForColor.put("MAGENTA", 2);
		posForColor.put("ORANGE", 3);
		posForColor.put("ROSE", 4);
		posForColor.put("VIOLET", 5);
		posForColor.put("GREEN", 6);
		posForColor.put("CYAN", 7);
		posForColor.put("AZURE", 8);
		posForColor.put("RED", 9);
		posForColor.put("CYAN", 10);
	}
	//--




	protected static Document xmlFromString(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return doc;
	}




	/**
	 * Construtor por defeito da classe IGEOXMLParser
	 */
	public IGEOXMLParser(){
		super();
	}



	/**
	 * Faz o parsing de uma string contendo um XML com as configurações internas da aplicação, como por exemplo,
	 * o url inicial onde se vai buscar informação, o url de alterações, o títilo da aplicação, entre outros.
	 * @param xmlStr XML com as configurações
	 * @return Objeto do tipo GEOAppConfigs com as configurações.
	 */
	public static GEOAppConfigs parseAppConfigs(String xmlStr){
		Document xmlDoc;
		try {
			xmlDoc = xmlFromString(xmlStr);
			NodeList appConfigsNode = (NodeList) xmlDoc.getElementsByTagName("AppConfigs");
			return parseAppConfigsXML(appConfigsNode);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	public static GEOAppConfigs parseAppConfigsXML(NodeList appConfigsNode) {
		GEOAppConfigs result = null;
		Element element = (Element) appConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		//le cada uma das subtags desta tag
		for (String key : appConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				try{
					Object value = idElement.getTextContent();
					keyValueMap.put(key, value);
				} catch(Exception e){
					e.printStackTrace();
					return null;
				}

			}

		}

		result = new GEOAppConfigs();
		result.appName = (String) keyValueMap.get(appConfigsNodeKeyList[0]);
		result.updateURL = (String) keyValueMap.get(appConfigsNodeKeyList[1]);
		result.appPackageName = (String) keyValueMap.get(appConfigsNodeKeyList[2]);
		result.URLRequests = (String) keyValueMap.get(appConfigsNodeKeyList[3]);
		result.URLRequestsSources = (String) keyValueMap.get(appConfigsNodeKeyList[4]);
		result.URLRequestsCategories = (String) keyValueMap.get(appConfigsNodeKeyList[5]);
		result.URLRequestAppDefaults = (String) keyValueMap.get(appConfigsNodeKeyList[6]);
		result.appColor = (String) keyValueMap.get(appConfigsNodeKeyList[7]);

		return result;
	}













	/**
	 * Lê e devolve numa lista a configuração de todos os ecrãs da App.
	 * @param xmlStr XML com as configurações dos ecrãs
	 * @return ArrayList de configurações dos ecrãs da App
	 */
	public static HashMap<String, IGEOScreenConfig> parseScreensConfigs(String xmlStr) {

		HashMap<String, IGEOScreenConfig> result = null;

		Document xmlDoc;
		try {
			xmlDoc = xmlFromString(xmlStr);
			NodeList screenConfigsNode = (NodeList) xmlDoc.getElementsByTagName("ScreensConfigs");
			return parseScreenConfigsXML(screenConfigsNode);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;	
	}


	/**
	 * Faz o parsing das configurações dos ecrãs da App.
	 * @param screenConfigsNode Nó contendo a tag <ScreenConfigs>.
	 * @return HashMap com as configurações de ecrã para cada um dos ecrãs.
	 */
	public static HashMap<String, IGEOScreenConfig> parseScreenConfigsXML(NodeList screenConfigsNode) {
		HashMap<String, IGEOScreenConfig> result = null;

		//Obter a tag screenConfigs
		Element element = (Element) screenConfigsNode.item(0);

		result = new HashMap<String, IGEOScreenConfig>();

		NodeList screenConfigsHome = element.getElementsByTagName("HomeScreenConfigs");
		result.put("home", parseHomeConfigsXML(screenConfigsHome));

		//NodeList screenConfigsInfo = element.getElementsByTagName("InfoScreenConfigs");
		result.put("info", new IGEOInfoScreenConfig());

		NodeList screenConfigsListSources = element.getElementsByTagName("ListSourcesScreenConfig");
		result.put("list_sources", parseListSourcesConfigsXML(screenConfigsListSources));

		NodeList screenConfigsSelectLocation = element.getElementsByTagName("SelectLocationScreenConfigs");
		result.put("explore_select", parseSelectLocationConfigsXML(screenConfigsSelectLocation));

		NodeList screenConfigsOptions = element.getElementsByTagName("OptionsScreenConfig");
		result.put("options", parseOptionsConfigsXML(screenConfigsOptions));

		NodeList screenConfigsNativeMap = element.getElementsByTagName("NativeMapScreenConfig");
		result.put("map", parseMapConfigsXML(screenConfigsNativeMap));

		NodeList screenConfigsList = element.getElementsByTagName("ListScreenConfig");
		result.put("list", parseListConfigsXML(screenConfigsList));

		NodeList screenConfigsDetails = element.getElementsByTagName("DetailsScreenConfig");
		result.put("details", parseDetailsConfigsXML(screenConfigsDetails));

		return result;
	}







	/**
	 * Faz o parsing das configurações do ecrã Home
	 * @param homeConfigsNode Nó contendo a tag IGEOHomeScreenConfigs
	 * @return Configurações do ecrã Home.
	 */
	public static IGEOHomeScreenConfigs parseHomeConfigsXML(NodeList homeConfigsNode) {
		IGEOHomeScreenConfigs result = null;
		Element element = (Element) homeConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		result = new IGEOHomeScreenConfigs();
		//le cada uma das subtags desta tag
		for (String key : homeScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				//parse das configurações do background
				if(key.equals(homeScreenConfigsNodeKeyList[1])){
					//Lista
					NodeList listBg = idElement.getElementsByTagName("BackgroundForSourceEntry");
					result.backgroundConf = parseBackgroundForSourceConfigsXML(listBg);

					//Imagem por default
					NodeList defaultBgList = idElement.getElementsByTagName("DefaultBackground");
					Element defaultBg = (Element) defaultBgList.item(0);
					try{
						result.backgroundConf.put("-1", defaultBg.getTextContent());
					} catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}

				//parse das configurações dos botões
				else if(key.equals(homeScreenConfigsNodeKeyList[2])){

					//Lista de botões
					NodeList buttonsConfigs = idElement.getElementsByTagName("ButtonElementConfig");
					for(int i=0;i<buttonsConfigs.getLength();i++){
						Element btnElement = (Element) buttonsConfigs.item(i);
						result.btns[i] = parseButtonElementConfigsXML(btnElement);
					}

				}

				else {
					try{
						Object value = idElement.getTextContent();
						keyValueMap.put(key, value);
					} catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}

			}

		}

		result.urlTopImage = (String) keyValueMap.get(homeScreenConfigsNodeKeyList[0]);
		result.subTitleBg = (String) keyValueMap.get(homeScreenConfigsNodeKeyList[3]);

		return result;
	}



	/**
	 * Faz o parsing das configurações de um botão
	 * @param element Nó contendo a tag <IGEOButtonElementConfig>.
	 * @return Configurações do botão.
	 */
	public static IGEOButtonElementConfig parseButtonElementConfigsXML(Element element) {
		IGEOButtonElementConfig result = null;
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		//le cada uma das subtags desta tag
		for (String key : buttonElementConfigKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				try{
					Object value = idElement.getTextContent();
					keyValueMap.put(key, value);
				} catch(Exception e){
					e.printStackTrace();
					return null;
				}

			}

		}

		result = new IGEOButtonElementConfig();
		result.name = (String) keyValueMap.get(buttonElementConfigKeyList[0]);
		result.imageNormalConfigs = (String) keyValueMap.get(buttonElementConfigKeyList[1]);
		result.imageClickConfigs = (String) keyValueMap.get(buttonElementConfigKeyList[2]);

		return result;
	}





	/**
	 * Faz o parsing das configurações do ecrã da lista de fontes
	 * @param listSourcesConfigsNode Nó contendo a tag <IGEOListSourcesScreenConfig>
	 * @return Configurações do ecrã da lista de fontes.
	 */
	public static IGEOListSourcesScreenConfig parseListSourcesConfigsXML(NodeList listSourcesConfigsNode) {
		IGEOListSourcesScreenConfig result = null;
		Element element = (Element) listSourcesConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		//le cada uma das subtags desta tag
		for (String key : listSourcesScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				try{
					Object value = idElement.getTextContent();
					keyValueMap.put(key, value);
				} catch(Exception e){
					e.printStackTrace();
					return null;
				}

			}

		}

		result = new IGEOListSourcesScreenConfig();
		result.urlTopImage = (String) keyValueMap.get(listSourcesScreenConfigsNodeKeyList[0]);
		result.colorSelection = (String) keyValueMap.get(listSourcesScreenConfigsNodeKeyList[1]);

		return result;
	}






	/**
	 * Faz o parsing das configurações  do ecrã da seleção da localização ("Explore").
	 * @param selectLocationConfigsNode Nó contendo a tag <IGEOSelectLocationScreenConfigs>.
	 * @return Configurações do ecrã da seleção da localização ("Explore").
	 */
	public static IGEOSelectLocationScreenConfigs parseSelectLocationConfigsXML(NodeList selectLocationConfigsNode) {
		IGEOSelectLocationScreenConfigs result = null;
		Element element = (Element) selectLocationConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		result = new IGEOSelectLocationScreenConfigs();
		//le cada uma das subtags desta tag
		for (String key : selectLocationScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				//parse das configurações dos botões
				if(key.equals(selectLocationScreenConfigsNodeKeyList[1])){

					//Lista de botões
					NodeList buttonsConfigs = idElement.getElementsByTagName("ButtonElementConfig");
					for(int i=0;i<buttonsConfigs.getLength();i++){
						Element btnElement = (Element) buttonsConfigs.item(i);
						IGEOButtonElementConfig b = parseButtonElementConfigsXML(btnElement);
						if(b.name.equals("selection")){
							result.bSelection = b;
						}
						else if(b.name.equals("search")){
							result.bSearch = b;
						}
					}

				}

				else {
					try{
						Object value = idElement.getTextContent();
						keyValueMap.put(key, value);
					} catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}

			}

		}

		result.urlTopImage = (String) keyValueMap.get(selectLocationScreenConfigsNodeKeyList[0]);

		return result;
	}






	/**
	 * Faz o parsing das configurações do ecrã da seleção de categorias.
	 * @param optionsConfigsNode Nó contendo a tag <OptionsScreenConfigs>.
	 * @return Configurações do ecrã da seleção de categorias.
	 */
	public static IGEOOptionsScreenConfig parseOptionsConfigsXML(NodeList optionsConfigsNode) {
		IGEOOptionsScreenConfig result = null;
		Element element = (Element) optionsConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		result = new IGEOOptionsScreenConfig();
		//le cada uma das subtags desta tag
		for (String key : optionsScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				//parse das configurações dos botões
				if(key.equals(optionsScreenConfigsNodeKeyList[1])){

					//b = parseButtonElementConfigsXML(btnElement);
					result.okButtonConfig = parseButtonElementConfigsXML(idElement);
					System.out.println("result.okButtonConfig = "+result.okButtonConfig);
					
//					//Lista de botões
//					NodeList buttonsConfigs = idElement.getElementsByTagName("ButtonElementConfig");
//					System.out.println("buttonsConfigs text = "+buttonsConfigs.item(0).getTextContent());
//					for(int i=0;i<buttonsConfigs.getLength();i++){
//						Element btnElement = (Element) buttonsConfigs.item(i);
//						result.okButtonConfig = parseButtonElementConfigsXML(btnElement);
//					}

				}

				//parse das configurações dos botões
				else if(key.equals(optionsScreenConfigsNodeKeyList[2])){

					//Lista de botões
					NodeList buttonsConfigs = idElement.getElementsByTagName("ButtonsForCategoryEntry");
					IGEOButtonElementConfig b = null;
					Element btnElement = null;
					for(int i=0;i<buttonsConfigs.getLength();i++){
						btnElement = (Element) buttonsConfigs.item(i);
						if(btnElement == null)
							continue;
						
						b = parseButtonElementConfigsXML(btnElement);

						if(b==null)
							break;

						result.buttonsOptionConf.put(b.name, b);
					}

					NodeList defaultButtonConfigs = idElement.getElementsByTagName("DefaultButtonCategory");
					btnElement = (Element) defaultButtonConfigs.item(0);
					IGEOButtonElementConfig bDefault = parseButtonElementConfigsXML(btnElement);
					
					result.buttonsOptionConf.put("-1", bDefault);

				}

				else if(key.equals(optionsScreenConfigsNodeKeyList[0])){
					try{
						Object value = idElement.getTextContent();
						keyValueMap.put(key, value);
					} catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}

			}

		}

		result.urlTopImage = (String) keyValueMap.get(selectLocationScreenConfigsNodeKeyList[0]);

		return result;
	}







	/**
	 * Faz o parsing das configurações do ecrã do mapa.
	 * @param mapConfigsNode Nó contendo a tag <IGEONativeMapScreenConfig>.
	 * @return Configurações do ecrã do mapa.
	 */
	public static IGEONativeMapScreenConfig parseMapConfigsXML(NodeList mapConfigsNode) {
		IGEONativeMapScreenConfig result = null;
		Element element = (Element) mapConfigsNode.item(0);

		result = new IGEONativeMapScreenConfig();
		//le cada uma das subtags desta tag
		for (String key : mapScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);


			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				//parse das cores para cada categoria
				if(key.equals(mapScreenConfigsNodeKeyList[0])){

					//Lista de cores para os pins
					NodeList pinConfigs = idElement.getElementsByTagName("PinColorsForCategoryEntry");
					Element pinElement = null;
					PinColorsForCategoryEntry pinEntry = null;
					for(int i=0;i<pinConfigs.getLength();i++){
						pinElement = (Element) pinConfigs.item(i);
						pinEntry = parsePinColorForCategoryConfigsXML(pinElement);

						int pos = posForColor.get(pinEntry.color);

						//Associar em cada uma das hashmaps a categoria e a cor associada
						result.colorPinsCategory.put(pinEntry.srcID+"_"+pinEntry.catID, pinColors[pos]);
						result.colorHTMLCategory.put(pinEntry.srcID+"_"+pinEntry.catID, htmlColors[pos]);
						result.pinLegendCategory.put(pinEntry.srcID+"_"+pinEntry.catID, legendPin[pos]);
						result.polygonColorCategory.put(pinEntry.srcID+"_"+pinEntry.catID, polygonLineColor[pos]);
						result.polygonBackgroundColorCategory.put(pinEntry.srcID+"_"+pinEntry.catID, polygonBackgroundColor[pos]);
					}

				}

				//parse da cor por defeito
				else if(key.equals(mapScreenConfigsNodeKeyList[1])){

					//Lista de cores para os pins
					NodeList pinConfigs = idElement.getElementsByTagName("Color");
					Element pinElement = (Element) pinConfigs.item(0);

					int pos = posForColor.get(pinElement.getTextContent());

					result.colorPinsCategory.put("-1", pinColors[pos]);
					result.colorHTMLCategory.put("-1", htmlColors[pos]);
					result.pinLegendCategory.put("-1", legendPin[pos]);
					result.polygonColorCategory.put("-1", polygonLineColor[pos]);
					result.polygonBackgroundColorCategory.put("-1", polygonBackgroundColor[pos]);
				}

			}

		}

		return result;
	}



	/**
	 * Esta classe é utilizada para guardar temporariamente o conteúdo das tags <PinColorsForCategoryEntry>.
	 * Esta classe contém a fonte e categoria de cada item bem como a cor associada.
	 * @author Bitcliq, Lda.
	 *
	 */
	static class PinColorsForCategoryEntry {
		String srcID;
		String catID;
		String color;

		PinColorsForCategoryEntry(){

		}

		public String toString(){
			return new String("PinColorsForCategoryEntry (srcID: "+srcID+", catID: "+catID+", color: "+color);
		}
	}

	/**
	 * Faz o parsing de um elemento do tipo <PinColorsForCategoryEntry> do XML com as configurações de ecrã.
	 * @param element Nó contendo a tag <PinColorsForCategoryEntry>.
	 * @return PinColorsForCategoryEntry Configurações  de cores / imagens (SourceID, categoryID, Color).
	 */
	public static PinColorsForCategoryEntry parsePinColorForCategoryConfigsXML(Element element) {
		PinColorsForCategoryEntry result = null;
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		//le cada uma das subtags desta tag
		for (String key : pinColorsForCategoryEntryKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				try{
					Object value = idElement.getTextContent();
					keyValueMap.put(key, value);
				} catch(Exception e){
					e.printStackTrace();
					return null;
				}

			}

		}

		result = new PinColorsForCategoryEntry();
		result.srcID = (String) keyValueMap.get(pinColorsForCategoryEntryKeyList[0]);
		result.catID = (String) keyValueMap.get(pinColorsForCategoryEntryKeyList[1]);
		result.color = (String) keyValueMap.get(pinColorsForCategoryEntryKeyList[2]);

		return result;
	}







	/**
	 * Faz o parsing das configurações de ecrã da lista de itens.
	 * @param listConfigsNode Nó contendo a tag <IGEOListScreenConfig>.
	 * @return Configurações do ecrã da lista de itens.
	 */
	public static IGEOListScreenConfig parseListConfigsXML(NodeList listConfigsNode) {
		IGEOListScreenConfig result = null;
		Element element = (Element) listConfigsNode.item(0);

		//le cada uma das subtags desta tag
		for (String key : listScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				result = new IGEOListScreenConfig();
				Element idElement = (Element) idElementList.item(0);

				//parse das configurações do background
				if(key.equals(listScreenConfigsNodeKeyList[0])){
					//Lista
					NodeList listBg = idElement.getElementsByTagName("BackgroundForSourceEntry");
					result.bgForSource = parseBackgroundForSourceConfigsXML(listBg);

					//Imagem por default
					NodeList defaultBgList = idElement.getElementsByTagName("DefaultBackground");
					Element defaultBg = (Element) defaultBgList.item(0);
					try{
						result.bgForSource.put("-1", defaultBg.getTextContent());
					} catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}

			}

		}

		return result;
	}




	/**
	 * Faz o parsing da tag <BackgroundForSourceEntry> do XML de configuração dos ecrãs.
	 * @param listBgNode Nó contendo a tag <BackgroundForSourceEntry>.
	 * @return HashMap que associa a cada string do tipo "srcID_catID" o url da imagem a utilizar como fundo.
	 * Caso não venha precedido de "file://" ou "http://" assume-se que se trata do nome de um ficheiro existente nos
	 * resources do projeto.
	 */
	public static HashMap<String, String> parseBackgroundForSourceConfigsXML(NodeList listBgNode) {
		HashMap<String, String> result = new HashMap<String, String>();

		Element element = null;
		String srcID = null;
		String url = null;

		try{
			for(int i=0;i<listBgNode.getLength();i++){
				element = (Element) listBgNode.item(i);
				srcID = element.getElementsByTagName("SourceID").item(0).getTextContent();
				url = element.getElementsByTagName("URLBG").item(0).getTextContent();
				result.put(srcID, url);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}





	/**
	 * Faz o parsing das configurações de ecrã dos detalhes de um item.
	 * @param detailsConfigsNode Nó contendo a tag <IGEODetailsScreenConfig>.
	 * @return Configurações do ecrã dos detalhes dos itens.
	 */
	public static IGEODetailsScreenConfig parseDetailsConfigsXML(NodeList detailsConfigsNode) {
		IGEODetailsScreenConfig result = null;
		Element element = (Element) detailsConfigsNode.item(0);
		Map<String, Object> keyValueMap = new TreeMap<String, Object>();

		//le cada uma das subtags desta tag
		for (String key : detailsScreenConfigsNodeKeyList) {
			NodeList idElementList = element.getElementsByTagName(key);

			//se não é null obtemos o primeiro elemento
			if(idElementList != null){
				Element idElement = (Element) idElementList.item(0);

				try{
					Object value = idElement.getTextContent();
					keyValueMap.put(key, value);
				} catch(Exception e){
					e.printStackTrace();
					return null;
				}

			}

		}

		result = new IGEODetailsScreenConfig();
		result.defaultImageURL = (String) keyValueMap.get(detailsScreenConfigsNodeKeyList[0]);

		return result;
	}

}

