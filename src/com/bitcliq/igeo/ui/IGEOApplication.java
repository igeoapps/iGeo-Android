package com.bitcliq.igeo.ui;

import java.lang.reflect.Field;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.DisplayMetrics;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEOFileUtils;
import com.bitcliq.igeo.core.IGEOLocationManager;
import com.bitcliq.igeo.core.IGEOUtils;
import com.bitcliq.igeo.core.datasource.IGEOXMLParser;
import com.bitcliq.igeo.threads.IGEODownloadsConfigsThread;


/**
 * Esta é a classe pricipal da App, representa a instância atual da App.</br>
 * Nesta classe são tratadas questões como a inicialização do listener de localização e a instanciação
 * de variáveis estáticas de outras classes utilizadas ao longo da App.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOApplication extends Application {

	/**
	 * Contexto da App.
	 */
	private static Context context;

	/**
	 * Esta variável é utilizada n obtenção de bitmaps. Caso seja possível, no dispositivo
	 * em que a app está a coorer, criar bitmaps na heap nativa, esta variável irá conter
	 * o campo a alterar através de reflection para que esse alocamento nativo seja possível. No caso
	 * do dispositivo não permitir esse alocamento, esta variável será null e não iremos tentar esse
	 * tipo de alocamento de cada vez que criamos um bitmap.
	 */
	public static Field fieldNativeHeap = null;




	//Métodos para a localização ###############################################################
	/**
	 * Listener de localização da App.
	 */
	public static IGEOLocationManager igeoLocationManager = null;

	/**
	 * Inicializa e coloca á escuta o listner de localização.
	 */
	public void startLocationListener(){
		if(igeoLocationManager==null){
			igeoLocationManager = new IGEOLocationManager(getApplicationContext());
		}
		igeoLocationManager.startLocationListener();
		igeoLocationManager.initLocation();
	}

	/**
	 * Para o listener de localização.
	 */
	public void stopLocationListener(){
		igeoLocationManager.stopLocationListener();
	}

	/**
	 * Obtém a localização atual.
	 * @return Objeto do tipo Location contendo as coordenadas GPS da localização atual.
	 */
	@SuppressWarnings("static-access")
	public Location getActualLocation(){
		return igeoLocationManager.getActualLocation();
	}
	//##########################################################################################



	
	/**
	 * Instância da thread que se encontra a correr na app e que é utilizada como uma fila de espera
	 * para receber pedidos de download de itens, correspondentes a imagens de configurações da App, e que
	 * faz os downloads dessas mesmas imagens, sendo que no final desse download coloca a referência no local
	 * correto (para a imagem que foi transferida).
	 */
	public static IGEODownloadsConfigsThread downloadConfigsThread;









	@Override
	public void onCreate(){
		super.onCreate();

		IGEOConfigsManager.setDensityString(this);

		//Verifica se é possível a alocação de bitmaps na cache nativa. Se sim, vamos tirar partido desse recurso futuramente
		//ao longo da App
		final BitmapFactory.Options options = new BitmapFactory.Options();
		try{
			Class<?> clazz =  options.getClass();
			fieldNativeHeap = clazz.getDeclaredField("inNativeAlloc");
			fieldNativeHeap.setAccessible(true);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		//Criaa a pasta da app
		IGEOFileUtils.createBaseFolder();


		
		IGEOFileUtils.createBaseFolder();
		context = getApplicationContext();


		//Inicialização das escalas de tamanhos entre densidades
		//Isto é util em casos em que queremos redimensionar ou criar dinamicamente uma view, com valores
		//fixos, e nas quais temos de ter o cuidado de ter em conta a densidade de ecrã.
		IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_LOW,0.5);
		IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_MEDIUM,0.65);
		IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_HIGH,1.0);
		IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_XHIGH,1.5);
		IGEOUtils.hashPercentResolutiuonSizes.put(DisplayMetrics.DENSITY_XXHIGH,2.0);


		//Construir o hashMap das cores usado no parse do XML
		IGEOXMLParser.constructHashMapColors();

		//Aplicar configurações
		IGEOConfigsManager.applyCurrentConfigs(this);

		igeoLocationManager = new IGEOLocationManager(getApplicationContext());


		//Lançar a thread para downloads
		downloadConfigsThread = new IGEODownloadsConfigsThread(this);
		downloadConfigsThread.start();

	}



	@Override
	public void onTerminate() {
		//Quando a App termina, as configuraçõessão escritas num ficheiro.
		IGEOConfigsManager.writeDefaultConfigs(this);
		downloadConfigsThread.interrupt();
		downloadConfigsThread = null;

		super.onTerminate();
	}




	/**
	 * Obtém o contexto da App.
	 * @return Contexto da App.
	 */
	public static Context getContext() {
		return context;
	}

}

