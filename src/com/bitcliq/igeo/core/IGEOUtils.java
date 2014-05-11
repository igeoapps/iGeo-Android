package com.bitcliq.igeo.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.bitcliq.igeo.core.datasource.IGEOCategory;
import com.bitcliq.igeo.core.datasource.IGEOGenericDataItem;
import com.bitcliq.igeo.core.datasource.IGEOSource;
import com.bitcliq.igeo.ui_patrimonio.R;


/**
 * Esta classe é utilizada para a implementação de métodos  usados em várias classes da App
 * @author Bitcliq, Lda.
 *
 */

public class IGEOUtils
{

	//Strings usadas na leitura e parsing do JSON
	//Estas constantes foram definidas para previnir a ocupação de memória e dificuldades no seu
	//"desalocamento", provocado pela criação de Strings cada vez que vamos buscar um determinado campo no JSON
	private static final String SOURCES = "Sources";
	private static final String CATEGORIES = "Categories";
	private static final String OBJECTS = "Objects";


	//Variáveis utilizadas no parse do JSON, estáticas para evitar problemas com alocação 
	//de várias instancias destes objetos
	//Todas estas variáveis são para uso temporário.
	private static JSONObject jsonObj2 = null;
	private static JSONArray jsonArray1 = null;
	private static String gpsDataInfo = null;
	private static StringTokenizer st = null;
	private static String lat = null;
	private static String lng = null;
	private static Location l;
	private static StringTokenizer stMultiP;


	/**
	 * Lista temporária de itens lidos do JSON
	 */
	private static ArrayList<IGEOGenericDataItem> listResult;

	/**
	 * Item temporário lido no parser do JSON
	 */
	private static IGEOGenericDataItem di = null;

	/**
	 * Latitude do elemento.
	 */
	private static String sLat;

	/**
	 * Longitude do elemento.
	 */
	private static String sLng;

	/**
	 * Indica se o elemento lido é um polígono.
	 */
	private static String isPolygon;

	/**
	 * Indica se estamos a ler um item representado por vários polígonos.
	 */
	private static String isMultiPolygon;

	/**
	 * Guarda temporariamente o ponto central de um polígono
	 */
	private static String centerPoint;

	/**
	 * Guarda temporariamente os dados de uma categoria
	 */
	private static IGEOCategory c;

	/**
	 * Guarda temporariamente uma lista de categorias
	 */
	private static ArrayList<IGEOCategory> cats;
	
	/*
	 * Lista temporária de categorias devolvidas no JSON.
	 */
	private static ArrayList<IGEOCategory> listResultCats;
	//---------------------------------------------------------------------------------------------------------------------------


	/**
	 * Este HashMap é utilizado para a conversão de tamanhos e distâncias quando se criam views em tempo de execução.
	 * Para cada densidade de ecrã contém o valor pelo qual temos de multiplicar outro valor para obter a dimensão correta.
	 * A resolução base que está a ser utilizada é a HDPI.
	 */
	public static HashMap<Integer,Double> hashPercentResolutiuonSizes = new HashMap<Integer,Double>();




	/**
	 * Construtor por defeito da classe IGEOUtils.
	 */
	public IGEOUtils(){
		super();
	}



	/**
	 * Calcula e devolve a altura proporcional de uma view, através de uma largura recebida.
	 * @param width Largura da view em dp's
	 * @param height Altura da view em dp's
	 * @param newWidth Nova largura em dp's
	 * @return Nova altura em dps
	 */
	public static int getProportionalHeight(int width,int height,int newWidth){
		return ((newWidth*height)/width);
	}


	/**
	 * Dada uma resolução e um valor na resolução hdpi, devolve o valor na resolução correspondente
	 * @param resolution Resolução para a qual queremos fazer a conversão
	 * @param size Valor a converter
	 * @return Valor convertido para a nova resolução
	 */
	public static int getSizeToResolution(int resolution,int size){
		if(hashPercentResolutiuonSizes == null){
			hashPercentResolutiuonSizes = new HashMap<Integer,Double>();
			IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_LOW,0.5);
			IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_MEDIUM,0.0);
			IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_HIGH,1.0);
			IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_XHIGH,1.65);
			IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_XXHIGH,2.0);
		}

		int res = 0;
		try {
			res = ((int) (size*IGEOUtils.hashPercentResolutiuonSizes.get(resolution)));
			return res;
		}
		catch(Exception e){  //Em caso de excepção retornamos a resolução utilizada na maioria dos dispositivos (XHDPI)
			e.printStackTrace();
			IGEOUtils.hashPercentResolutiuonSizes.put(resolution,2.0);
			res = ((int) (size*IGEOUtils.hashPercentResolutiuonSizes.get(resolution)));
		}

		return ((int) (size*IGEOUtils.hashPercentResolutiuonSizes.get(resolution)));
	}






	//JSON ##################################################################################
	/**
	 * Recebe um JSON com uma lista de itens e transforma-o numa coleção de itens.
	 * @param jsonObj JSONObject com o JSON do qual queremos fazer o parsing.
	 * @return ArrayList com os itens contidos no JSON.
	 */

	public static ArrayList<IGEOGenericDataItem> getDataListFromJSONObject(JSONObject jsonObj) {

		try {
			jsonArray1 = jsonObj.getJSONArray(OBJECTS);

			listResult = new ArrayList<IGEOGenericDataItem>(); 

			for(int i=0;i<jsonArray1.length();i++){

				jsonObj = jsonArray1.getJSONObject(i);

				di = new IGEOGenericDataItem();
				di.itemID = jsonObj.getString("ID");
				di.categoryID = jsonObj.getString("CategoryID");
				di.textOrHTML = jsonObj.getString("Resumo");
				di.title = jsonObj.getString("Titulo");
				di.imageURL = jsonObj.getString("URL_Imagem");

				isMultiPolygon = jsonObj.getString("MultiPolygon");
				int numPointsAdd = 0;
				if(isMultiPolygon.equals("True")){

					//ponto central --
					centerPoint = jsonObj.getString("CenterPoint");
					sLat = centerPoint.substring(0, centerPoint.indexOf(" ")-1);
					sLng = centerPoint.substring(centerPoint.indexOf(" ")+1);
					sLat = sLat.replace(",",".");
					sLng = sLng.replace(",",".");

					double cLat = Double.parseDouble(sLat);
					double cLng = Double.parseDouble(sLng);
					if(cLat<0){
						double tmp = cLat;
						cLat = cLng;
						cLng = tmp;
					}
					di.centerPoint = new Location("");
					di.centerPoint.setLatitude(cLat);
					di.centerPoint.setLongitude(cLng);
					//--


					//se nesta fonte existem polígonos
					if(!IGEOConfigsManager.nonDrawPolygonSources.contains(IGEODataManager.getActualSource().sourceID)){

						di.multiPolygon = true;
						di.lastPolygonCoordenatesList = new ArrayList<Integer>();

						//obtenção da lista de coordenadas gps do ponto e a fazer a sua separação através da vírgula
						//vamos ler os valores dois a dois, sendo o primeiro lido a latitude e o segundo a longitude
						//caso recebamos de servidor a longitude primeiro que a latitude é feita uma troca entre os valores
						gpsDataInfo = jsonObj.getString("Gps");
						try{
							if(gpsDataInfo!=null){
								if(!gpsDataInfo.equals("")){


									di.locationCoordenates = new ArrayList<Location>();

									//para cada polígono
									stMultiP = new StringTokenizer(gpsDataInfo,"|");
									while(stMultiP.hasMoreTokens()){

										//para cada ponto do polígono
										st = new StringTokenizer(stMultiP.nextToken()," ");
										while(st.hasMoreTokens()){

											lng = st.nextToken();
											lat = st.nextToken();

											if(lng.endsWith(",")){
												lng = lng.substring(0, lng.indexOf(","));
											}
											if(lat.endsWith(",")){
												lat = lat.substring(0, lat.indexOf(","));
											}

											//prevenir os casos em que as coordenadas vêm com vírgulas em vez de pontos
											lat = lat.replace(",", ".");
											lng = lng.replace(",", ".");

											double latitude = Double.parseDouble(lat);
											double longitude = Double.parseDouble(lng);
											l = new Location("");
											l.setLatitude(latitude);
											l.setLongitude(longitude);
											di.locationCoordenates.add(l);

											numPointsAdd++;

										}  //pontos

										di.lastPolygonCoordenatesList.add(numPointsAdd-1);

									}  //polígonos

								}
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}

					}  //if

					//se nesta fonte apenas existem pontos, adiciona-se o ponto que foi obtido ao item
					else {
						di.locationCoordenates = new ArrayList<Location>();
						di.locationCoordenates.add(di.centerPoint);
					}

				}

				//se é um polígono
				else {

					isPolygon = jsonObj.getString("Polygon");
					if(isPolygon.equals("True")){
						//ponto central --
						centerPoint = jsonObj.getString("CenterPoint");
						sLat = centerPoint.substring(0, centerPoint.indexOf(" ")-1);
						sLng = centerPoint.substring(centerPoint.indexOf(" ")+1);
						sLat = sLat.replace(",",".");
						sLng = sLng.replace(",",".");
						double cLat = Double.parseDouble(sLat);
						double cLng = Double.parseDouble(sLng);
						if(cLat<0){
							double tmp = cLat;
							cLat = cLng;
							cLng = tmp;
						}
						di.centerPoint = new Location("");
						di.centerPoint.setLatitude(cLat);
						di.centerPoint.setLongitude(cLng);
						//--
					}



					//semelhante ao explicado acima mas apenas para um polígono
					gpsDataInfo = jsonObj.getString("Gps");
					try{
						if(gpsDataInfo!=null){
							if(!gpsDataInfo.equals("")){

								di.locationCoordenates = new ArrayList<Location>();

								st = new StringTokenizer(gpsDataInfo," ");

								while(st.hasMoreTokens()){

									lng = st.nextToken();
									lat = st.nextToken();

									if(lng.endsWith(",")){
										lng = lng.substring(0, lng.indexOf(","));
									}
									if(lat.endsWith(",")){
										lat = lat.substring(0, lat.indexOf(","));
									}

									//prevenir os casos em que as coordenadas vêm com virgulas em vez de pontos
									lat = lat.replace(",", ".");
									lng = lng.replace(",", ".");

									double latitude = Double.parseDouble(lat);
									double longitude = Double.parseDouble(lng);
									
									if(latitude<0){
										double tmp = latitude;
										latitude = longitude;
										longitude = tmp;
									}
									
									l = new Location("");
									l.setLatitude(latitude);
									l.setLongitude(longitude);
									di.locationCoordenates.add(l);

								}


							}
						}

					}

					catch(Exception e){
						e.printStackTrace();
					}

				}

				listResult.add(di);
			}

			//libertar variáveis
			di = null;
			gpsDataInfo = null;
			st = null;
			lat = null;
			lng = null;
			l = null;
			gpsDataInfo = null;
			st = null;
			lat = null;
			lng = null;
			l = null;
			isMultiPolygon = null;
			centerPoint = null;
			sLat = null;
			sLng = null;
			stMultiP = null;
			isPolygon = null;

			System.gc();

			return listResult;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			//libertar variáveis
			di = null;
			gpsDataInfo = null;
			st = null;
			lat = null;
			lng = null;
			l = null;
			gpsDataInfo = null;
			st = null;
			lat = null;
			lng = null;
			l = null;

			if(listResult!=null)
				listResult.clear();
			listResult = null; 

			isMultiPolygon = null;
			centerPoint = null;
			sLat = null;
			sLng = null;
			stMultiP = null;
			isPolygon = null;

			System.gc();
		}

		return null;	
	}



	/**
	 * Dado um JSONObject obtém um item.
	 * @param jsonObj JSONObject contém a informação sobre o item.
	 * @return IGEOGenericDataItem com a informação contida no JSONObject.
	 */
	public static IGEOGenericDataItem getDataItemFromJSONObject(JSONObject jsonObj) {

		di = null;

		di = new IGEOGenericDataItem();
		try {
			di.itemID = jsonObj.getString("ID");

			di.categoryID = jsonObj.getString("CategoryID");
			di.textOrHTML = jsonObj.getString("Detalhes");
			di.title = jsonObj.getString("Titulo");
			di.imageURL = jsonObj.getString("URL_Imagem");

			jsonObj = null;

			System.gc();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			di = null;
			jsonObj = null;

			System.gc();
		}

		return di;
	}


	/**
	 * Recebe um JSONArray com uma lista de categorias e transforma-a numa lista de objetos do tipo IGEOCategory.
	 * @param jsonArray Array com o JSON do qual queremos fazer o parsing.
	 * @return ArrayList com as categorias contidas no JSONArray.
	 * @throws JSONException 
	 */
	public static ArrayList<IGEOCategory> getCategoriesFromJSONArray(JSONArray jsonArray) throws JSONException {

		c = null;
		cats = new ArrayList<IGEOCategory>();

		for(int j=0;j<jsonArray.length();j++){

			jsonObj2 = jsonArray.getJSONObject(j);

			c = new IGEOCategory();
			c.categoryID = jsonObj2.getString("ID");
			c.categoryName = jsonObj2.getString("Titulo");

			String strIcon = jsonObj2.getString("Icon");
			if(strIcon!=null){
				if(!strIcon.equals("")){
					c.icon = strIcon;
				}
			}
			strIcon = null;

			String strIconSelected = jsonObj2.getString("IconSelected");
			if(strIconSelected!=null){
				if(!strIconSelected.equals("")){
					c.iconSelected = strIconSelected;
				}
			}
			strIconSelected = null;




			//obtenção da versão, das cores e do pin do mapa
			//a cor obtida é a mesma, apenas em diferentes formatos consoante a sua utilização final na app
			String version = jsonObj2.getString("Version");
			String colorHex = jsonObj2.getString("colorhex");
			String colorHue = jsonObj2.getString("colorhue");
			String pinMap = jsonObj2.getString("pinimg");

			int versionInt = Integer.parseInt(version);

			//ALTERAÇÕES PARA OBTER AS CONFIGS DO SERVIDOR --
			if(c.icon!=null){
				if(!c.icon.equals("")){
					//aqui compara-se a versão atual das configurações da categoria com a que foi recebida
					//se for menor, significa que a que vem do servidor tem alterações pelo que a configuração vai ser alterada
					if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
						IGEOConfigsManager.changeCategoryIconNormalForSourceAndCategory(
								IGEODataManager.getActualSource().sourceID,
								c.categoryID,
								IGEOFileUtils.BASE_URL + c.icon);
					}
				}
			}

			if(c.iconSelected!=null){
				if(!c.iconSelected.equals("")){
					//semelhante ao já explicado acima
					if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
						IGEOConfigsManager.changeCategoryIconSelectedForSourceAndCategory(
								IGEODataManager.getActualSource().sourceID,
								c.categoryID,
								IGEOFileUtils.BASE_URL + c.iconSelected);
					}
				}
			}

			if(pinMap!=null){
				if(!pinMap.equals("")){
					//semelhante ao já explicado acima
					if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
						IGEOConfigsManager.changePinMapImageForSourceAndCategory(
								IGEODataManager.getActualSource().sourceID,
								c.categoryID,
								IGEOFileUtils.BASE_URL + pinMap,
								colorHex,
								colorHue);
					}
				}
			}

			/*if(colorHex!=null){
				if(!colorHex.equals("")){
					//semelhante ao já explicado acima
					if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
						IGEOConfigsManager.changeTitleListColorForSourceAndCategory(
								IGEODataManager.getActualSource().sourceID,
								c.categoryID,
								colorHex);
					}
				}
			}

			if(colorHue!=null){
				if(!colorHue.equals("")){
					//semelhante ao já explicado acima
					if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
						IGEOConfigsManager.changePinColorForSourceAndCategory(
								IGEODataManager.getActualSource().sourceID,
								c.categoryID,
								colorHue);
					}
				}
			}*/

			//após a alteração do conteúdo das configurações, atualiza-se a versão
			if(IGEOConfigsManager.getVersionNumberForSourceAndCategory(IGEODataManager.getActualSource().sourceID, c.categoryID) < versionInt){
				IGEOConfigsManager.setVersionNumberForSource(versionInt, IGEODataManager.getActualSource().sourceID, c.categoryID);
			}
			//--

			version = null;
			colorHex = null;
			colorHue = null;
			pinMap = null;





			cats.add(c);
		}

		return cats;
	}



	/**
	 * Recebe um JSONArray com uma lista de categorias e transforma-a numa lista de objetos do tipo IGEOCategory.
	 * Este método obtém o JSONArray e chama um método auxiliar para fazer o seu parsing (getCategoriesFromJSONArray).
	 * @param jsonObj JSONObject a partir do qual irá ser feito o parsing.
	 * @return Lista com as categorias contidas no JSON.
	 */
	public static ArrayList<IGEOCategory> getCategoriesFromJSONObject(JSONObject jsonObj){
		try {
			jsonArray1 = jsonObj.getJSONArray(CATEGORIES);

			listResultCats = new ArrayList<IGEOCategory>(); 

			listResultCats = getCategoriesFromJSONArray(jsonArray1);

			jsonObj = null;
			System.gc();

			return listResultCats;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			jsonObj = null;

			if(listResultCats!=null)
				listResultCats.clear();

			listResultCats = null;

			System.gc();
		}

		return null;
	}


	/**
	 * Variavel temporária usada na leitura de fontes de um JSONObject.
	 */
	private static IGEOSource s;
	/**
	 * Lista temporária de fontes usada na leitura de um JSONArray
	 */
	private static ArrayList<IGEOSource> listResultSources;

	/**
	 * Obtém uma lista de fontes dado um JSON object.
	 * @param jsonObj JSONObject a partir do qual será feito o parsing.
	 * @return ArrayList com as fontes correspondentes.
	 */
	public static ArrayList<IGEOSource> getSourcesFromJSONObject(JSONObject jsonObj){

		s = null;

		try {
			jsonArray1 = jsonObj.getJSONArray(SOURCES);

			listResultSources = new ArrayList<IGEOSource>(); 

			for(int i=0;i<jsonArray1.length();i++){

				jsonObj = jsonArray1.getJSONObject(i);

				s = new IGEOSource();
				s.sourceID = jsonObj.getString("ID");
				s.sourceName = jsonObj.getString("Titulo");
				s.color = jsonObj.getString("Cor");
				s.imageURL = jsonObj.getString("URL_Imagem");
				s.srcSubTitle = jsonObj.getString("Legenda");

				//se não está definida como sendo uma fonte na qual é possivel o desenho de polígonos, vamos adicionar
				//á lista que contém o ID das categorias desse tipo, para que essa verificação possa ser feita ao obter
				//os itens para a lista e para o mapa
				if(jsonObj.getString("DrawPolygon").equals("False")){
					IGEOConfigsManager.nonDrawPolygonSources.add(s.sourceID);
				}

				try {
					String version = jsonObj.getString("Version");
					String urlBgHome = jsonObj.getString("homeimg");
					String urlBgList = jsonObj.getString("listimg");

					int versionInt = Integer.parseInt(version);

					//ALTERAÇÕES PARA OBTER AS CONFIGS DO SERVIDOR --
					//Aqui a atualização é feita de forma semelhante á que fazemos para as categorias
					if(urlBgHome!=null){
						if(!urlBgHome.equals("")){
							if(IGEOConfigsManager.getVersionNumberForSource(s.sourceID) < versionInt){
								IGEOConfigsManager.changeBackgroundImageHomeForSource(s.sourceID, IGEOFileUtils.BASE_URL + urlBgHome);
							}
						}
					}

					if(urlBgList!=null){
						if(!urlBgList.equals("")){
							if(IGEOConfigsManager.getVersionNumberForSource(s.sourceID) < versionInt){
								IGEOConfigsManager.changeBackgroundImageListForSource(s.sourceID, IGEOFileUtils.BASE_URL + urlBgList);
							}
						}
					}

				
					if(IGEOConfigsManager.getVersionNumberForSource(s.sourceID) < versionInt){
						IGEOConfigsManager.setVersionNumberForSource(versionInt, s.sourceID);
					}
					//--

					version = null;
					urlBgHome = null;
					urlBgList = null;
				} catch(Exception e){
					e.printStackTrace();
				}



				listResultSources.add(s);
			}

			s = null;
			System.gc();

			return listResultSources;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			if(listResultSources!=null)
				listResultSources.clear();
			listResultSources = null;

			System.gc();
		}

		return null;
	}








	/**
	 * Obtém as configurações por defeito, ou seja, a imagem por defeito da home, a imagem por defeito da lista, entre outras.
	 * @param jsonObj Array com o JSON do qual queremos fazer o parse.
	 * @throws JSONException 
	 */
	public static void readAppDefaultsFromJSONObject(JSONObject jsonObj) throws JSONException {

		jsonArray1 = jsonObj.getJSONArray("Theme");

		for(int j=0;j<jsonArray1.length();j++){
			jsonObj2 = jsonArray1.getJSONObject(j);

			String version = null;
			String urlBgHome = null;
			String urlBgList = null;

			try {
				version = jsonObj2.getString("Version");
			} catch(Exception e1){

			}

			try {
				urlBgHome = jsonObj2.getString("homeimg");
			} catch(Exception e){

			}

			try {
				urlBgList = jsonObj2.getString("listimg");
			} catch(Exception e){

			}
			
			int versionInt = Integer.parseInt(version);

			//ALTERAÇÕES PARA OBTER AS CONFIGS DO SERVIDOR --
			if(urlBgHome!=null){
				if(!urlBgHome.equals("")){
					if(IGEOConfigsManager.getVersionNumberForAppDefaults() < versionInt){
						IGEOConfigsManager.changeBackgroundImageHomeDefaultUrl(IGEOFileUtils.BASE_URL + urlBgHome);
					}
				}
			}

			if(urlBgList!=null){
				if(!urlBgList.equals("")){
					if(IGEOConfigsManager.getVersionNumberForAppDefaults() < versionInt){
						IGEOConfigsManager.changeBackgroundImageListDefaultUrl(IGEOFileUtils.BASE_URL + urlBgList);
					}
				}
			}

			if(IGEOConfigsManager.getVersionNumberForAppDefaults() < versionInt){
				IGEOConfigsManager.setVersionNumberForAppDefaults(versionInt);
			}
			//--

			version = null;
			urlBgHome = null;
			urlBgList = null;
		}

	}

	//############################################################################################


	//INTERNET ##################################################################################
	/**
	 * Verifica se uma App com uma determinada configuração se encontra instalada.
	 * @param relatedAppInfo Configurações da App que queremos verificar
	 * @return true ou false consoante a App esteja ou não instalada no dispositivo
	 */
	public static boolean checkAppInstalled(IGEORelatedAppInfo relatedAppInfo) {
		return false;	
	}



	//ATIVAÇÃO DE GPS E SERVIÇOS -----------------------------------------------------------------------------------------------------------------------

	/**
	 * Activa o wifi após confirmação do utilizador.
	 * @param a Atividade atual.
	 */
	private static void turnOnWifi(final Activity a){
		WifiManager wifi = (WifiManager) a.getSystemService(Context.WIFI_SERVICE);
		if(!wifi.isWifiEnabled()){
			AlertDialog.Builder builder = new AlertDialog.Builder(a);

			builder.setTitle(a.getResources().getString(R.string.text_access_permition_title));
			builder.setMessage(a.getResources().getString(R.string.text_access_permition_text));
			builder.setPositiveButton(a.getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try{
						WifiManager wifi = (WifiManager) a.getSystemService(Context.WIFI_SERVICE);
						wifi.setWifiEnabled(true);
					} catch(Exception e){
						AlertDialog.Builder builder2 = new AlertDialog.Builder(a);

						builder2.setTitle(a.getResources().getString(R.string.text_error_wifi_activation_title));
						builder2.setMessage(a.getResources().getString(R.string.text_error_wifi_activation_text));
						builder2.setNeutralButton(a.getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});

						Dialog alert2 = builder2.create();
						alert2.show();
					}
				}
			});

			builder.setNegativeButton(a.getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog.Builder builder3 = new AlertDialog.Builder(a);

					builder3.setTitle(a.getResources().getString(R.string.text_error));
					builder3.setMessage(a.getResources().getString(R.string.text_limited_access));
					builder3.setNeutralButton(a.getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});                                     

					Dialog alert3 = builder3.create();
					alert3.show();
				}
			});

			Dialog alert = builder.create();
			alert.show();
		}
	}


	/**
	 * Método utilizado para pedido de ligação à internet através de transferência de dados.
	 * @param a Actividade atual.
	 * @param context Contexto atual.
	 * @param enabled true ou false consoante pretendemos ativar ou desativar a ligação.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setMobileDataEnabled(Activity a, Context context, boolean enabled) {
		final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class conmanClass = null;
		try {
			conmanClass = Class.forName(conman.getClass().getName());
		} catch (ClassNotFoundException e) {
		}
		Field iConnectivityManagerField = null;
		try {
			iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
		iConnectivityManagerField.setAccessible(true);
		Object iConnectivityManager = null;
		try {
			iConnectivityManager = iConnectivityManagerField.get(conman);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		Class iConnectivityManagerClass = null;
		try {
			iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		} catch (ClassNotFoundException e) {
		}
		Method setMobileDataEnabledMethod = null;
		try {
			setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		setMobileDataEnabledMethod.setAccessible(true);

		try {
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

	}


	/**
	 * Verifica se existe uma ligação à internet ativa.
	 * @param a Actividade atual.
	 * @return true ou false consoante existe ou não uma ligação à internet ativa.
	 */
	public static boolean CheckInternet(Activity a) 
	{
		ConnectivityManager connec = (ConnectivityManager) a.getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isConnected()) {
			return true;
		} else if (mobile.isConnected()) {
			return true;
		}
		return false;
	}


	/**
	 * Verifica se existe uma ligação à internet, e caso não exista, pergunta ao utilizador como se pretende
	 * ligar à internet.
	 * @param a Actividade atual.
	 * @return true ou false consoante existe ou não uma ligação ativa à internet.
	 */
	public static boolean servicesActivation(final Activity a){

		if(CheckInternet(a)){
			return true;
		}

		final CharSequence[] items = {"Wifi", "Ligação de Dados"};

		AlertDialog.Builder builder4 = new AlertDialog.Builder(a);
		builder4.setTitle(a.getResources().getString(R.string.text_how_to_connect));
		builder4.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(item==0){
					turnOnWifi(a);

				} else if(item==1){
					setMobileDataEnabled(a,a.getBaseContext(),true);
				}
			}
		}).show();

		return true;
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------








	/**
	 * Este método é utilizado para obter a largura em pixeís do texto colocado numa TextView. É
	 * útil na construção da lista de itens para determinar quando terá que se de aumentar a sua altura e pode
	 * ser útil noutras situações em que se tenha de forçar o aumento de altura de uma view em função do número
	 * de linhas de uma TextView.
	 * @param text String com o texto.
	 * @param textView TextView onde vai ser colocado o texto.
	 * @return Tamanho em pixeis que o texto irá ocupar na TextView.
	 */
	public static int getSizeForString(String text,TextView textView){
		Rect bounds = new Rect();
		Paint textPaint = textView.getPaint();
		textPaint.getTextBounds(text,0,text.length(),bounds);
		int width = bounds.width();

		return width;
	}



	/**
	 * Dado o url de um ficheiro deviolve o seu nome.
	 * @param url URL do ficheiro
	 * @return Nome do ficheiro
	 */
	public static String getFileNameFromURL(String url){
		if(url == null){
			return null;
		}
		else if(url.equals("")){
			return url;
		}
		else {
			return(url.substring(url.lastIndexOf("/") + 1));
		}
	}

}

