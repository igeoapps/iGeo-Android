package com.bitcliq.igeo.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEODataManager;
import com.bitcliq.igeo.core.IGEOFileUtils;

/**
 * Esta classe devriva da classe Thread e corre em background sempre que a App esteja ativa (foreground), e que espera por pedidos para descarregar uma imagem.
 * Após a obtenção da imagem e consoante o seu tipo, a referência para a mesma é colocada no local correspondente das configurações.
 * @author Bitcliq, Lda.
 *
 */
public class IGEODownloadsConfigsThread extends Thread {

	/**
	 * Representa o estado em que a thread se encontra. Os valores possíveis são:</br>
	 * <li>Downloading: a descarregar uma imagem;</li>
	 * <li>OnPause: à espera de um item para descarregar</li>
	 * @author Bitcliq, Lda.
	 *
	 */
	enum IGEOThreadDownloadState {
		Downloading,
		OnPause
	}

	/**
	 * ArrayList que serve de fila de espera para download de imagens.
	 */
	private ArrayList<IGEOConfigDownloadItem> downloadItemsQueue;

	/**
	 * Item que está atualmente a ser descarregado.
	 */
	private IGEOConfigDownloadItem actualItem;

	/**
	 * Estado atual da thread. Indica se está em espera ou a fazer o download de uma imagem.
	 */
	private IGEOThreadDownloadState actualState;

	/**
	 * Handler utilizado para enviar pedidos para a thread
	 */
	public Handler mHandler;

	/**
	 * AsyncronTask que faz o download das imagens.
	 */
	public static imageDownloader currentImageDownloader;

	private URL url = null;
	private HttpURLConnection urlConnection = null;
	private static final File sdcard = Environment.getExternalStorageDirectory();
	private FileOutputStream fileOutput = null;
	private InputStream inputStream = null;
	private File file = null;
	private static final String DOWNLOAD_METHOD = "GET";
	
	private Context appContext;


	
	public IGEODownloadsConfigsThread(Context appContext) {
		this.appContext = appContext;
	}




	@Override
	public void run() {

		downloadItemsQueue = new ArrayList<IGEOConfigDownloadItem>();

		actualState = IGEOThreadDownloadState.OnPause;

		Looper.prepare();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {

				if(msg.obj == null)
					return;

				if(!(msg.obj instanceof IGEOConfigDownloadItem))
					return;

				addItemToDownload(((IGEOConfigDownloadItem) msg.obj));
			}
		};

		Looper.loop();

	}


















	@Override
	public void interrupt() {
		mHandler.removeCallbacks(this);

		if(currentImageDownloader!=null){
			if(!currentImageDownloader.finished)
				currentImageDownloader.cancelarDownload();
		}

		super.interrupt();
	}


	/**
	 * Adiciona um item á lista de itens para download.
	 * @param item Item que pretendemos descarregar.
	 */
	public void addItemToDownload(IGEOConfigDownloadItem item) {
		Log.i("-- IGEODownloads", "" + item.url + " added");
		if(actualState == IGEOThreadDownloadState.OnPause){
			//fazer o download
			actualItem = item;
			actualState = IGEOThreadDownloadState.Downloading;
			currentImageDownloader = new imageDownloader();
			currentImageDownloader.execute("");
		}
		else {
			downloadItemsQueue.add(item);
		}
	}


	/**
	 * Método chamado quando o download é terminado. este método é utilizado para descarregar os novos itens na
	 * fila de espera, e colocar as referências nos locais apropriados para o item que acabou de ser descarregado.
	 */
	private void downloadFinished() {
		Log.i("-- IGEODownloads", "" + actualItem.url + " downloaded");
		
		actualItem.finalName = "/" + actualItem.finalName;

		actualState = IGEOThreadDownloadState.OnPause;

		makeAlterationsWithDownloadedItem();

		if(downloadItemsQueue.size()>0){
			//fazer o download
			actualItem = downloadItemsQueue.get(0);
			actualState = IGEOThreadDownloadState.Downloading;
			downloadItemsQueue.remove(0);
			currentImageDownloader = new imageDownloader();
			currentImageDownloader.execute("");
		}
		else {

		}
	}


	/**
	 * Método que após o download de uma imagem efetua as respetivas alterações de refreências nas configs.
	 */
	public void makeAlterationsWithDownloadedItem() {

		switch (actualItem.type) {
		case HomeBgImage:
			IGEOConfigsManager.setHomeBgImageForSource(actualItem.srcID, actualItem.finalName);
			break;
		case ListBGImage:
			IGEOConfigsManager.setListBgImageForSource(actualItem.srcID, actualItem.finalName);
			break;
		case CatIconNormal:
			IGEOConfigsManager.setCatIconNormalForSourceAndCategory(actualItem.srcID, actualItem.catID, actualItem.finalName);
			break;
		case CatIconSelected:
			IGEOConfigsManager.setCatIconSelectedForSourceAndCategory(actualItem.srcID, actualItem.catID, actualItem.finalName);
			break;
		case CatPinImage:
			
			//No caso de ser um pin do mapa, além do url do pin atualiza também a cor do pin do mapa a apresentar no mapa e a cor
			//do titulo a apresentar na lista. Fizemos desta forma para que não ocorram situações em que a cor já está disponível mas a imagem
			//da legenda do mapa ainda está a ser descarregada dando assim lugar a inconsistências.
			IGEOConfigsManager.setCatPinImageForSourceAndCategory(actualItem.srcID, actualItem.catID, actualItem.finalName);
			
			if(actualItem.colorHex!=null){
				if(!actualItem.colorHex.equals("")){
					IGEOConfigsManager.changeTitleListColorForSourceAndCategory(
							IGEODataManager.getActualSource().sourceID,
							actualItem.catID,
							actualItem.colorHex);
				}
			}
			
			if(actualItem.colorHue!=null){
				if(!actualItem.colorHue.equals("")){
					IGEOConfigsManager.changePinColorForSourceAndCategory(
							IGEODataManager.getActualSource().sourceID,
							actualItem.catID,
							actualItem.colorHue);
				}
			}
			
			break;

		default:
			break;
		}

	}








	/**
	 * Esta AsyncTask é utilizada para fazer em background o download de uma imagem, chamando no final do mesmo o
	 * método que coloca a referência para a mesma no local correto das configs.
	 * @author Bitcliq, Lda.
	 *
	 */
	class imageDownloader extends AsyncTask<String , Integer, Void>
	{
		boolean finished = false;
		boolean canceled = false;
		boolean noFreeSpace = false;
		boolean noDownloadCompleted = false;

		public void cancelarDownload(){
			canceled = true;

			finished = true;
			
			noDownloadCompleted = true;

			this.cancel(true);
		}

		@Override
		protected void onPreExecute()
		{

		} 
		@SuppressLint("SdCardPath")
		@Override
		protected Void doInBackground(String... params) 
		{
			Log.i("-- IGEODownloads", "" + actualItem.url + " init download");

			finished = false;

			if(canceled){
				System.gc();

				finished = true;
				
				noDownloadCompleted = true;

				return null;
			}

			String url_str = actualItem.url;
			String fileName = actualItem.finalName;
			Log.i("-- IGEODownloads", "download from: " + url_str);
			Log.i("-- IGEODownloads", "download to: " + fileName);

			byte[] buffer = null;

			try {
				url = new URL(url_str);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod(DOWNLOAD_METHOD);
				//urlConnection.setDoOutput(true);
				urlConnection.connect();
				
				File file = new File(IGEOFileUtils.APP_FOLDER, fileName);

				fileOutput = new FileOutputStream(file);
				inputStream = urlConnection.getInputStream();

//				fileOutput = appContext.openFileOutput(fileName,Context.MODE_PRIVATE);
//				inputStream = urlConnection.getInputStream();

				System.gc();
				buffer = new byte[10000];
				int bufferLength = 0;

				if(canceled){
					inputStream = null;
					fileOutput = null;
					file = null;
					buffer=null;
					url_str = null;
					System.gc();

					finished = true;
					
					noDownloadCompleted = true;

					return null;
				}

				boolean emptyFile = true;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					if(canceled){
						inputStream.close();
						urlConnection.disconnect();
						
						noDownloadCompleted = true;
						
						break;
					}
					fileOutput.write(buffer, 0, bufferLength);
					emptyFile = false;
				}
				//System.out.println("emptyFile = "+emptyFile);
				if(emptyFile){
					noDownloadCompleted = true;
				}
				fileOutput.close();
				
				

				if(canceled){
					inputStream = null;
					fileOutput = null;
					file = null;
					buffer=null;
					url_str = null;
					System.gc();

					finished = true;
					
					noDownloadCompleted = true;

					return null;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				
				inputStream = null;
				fileOutput = null;
				file = null;
				buffer=null;
				url_str = null;
				System.gc();

				finished = true;
				
				noDownloadCompleted = true;

				if(canceled){
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				
				inputStream = null;
				fileOutput = null;
				file = null;
				buffer=null;
				url_str = null;
				System.gc();

				finished = true;
				
				noDownloadCompleted = true;

				if(canceled){
					return null;
				}
			}
			//-------------------------



			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values)
		{

		}
		@Override
		protected void onPostExecute(Void result) 
		{
			if(!noDownloadCompleted)
				downloadFinished();
			else
				noDownloadCompleted = false;

			finished = true;
		}

	}

}
