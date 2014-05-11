package com.bitcliq.igeo.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bitcliq.igeo.ui.IGEOApplication;


/**
 * Esta classe contém os métodos necessários à manipulação de ficheiros, isto é, leitura e escrita em ficheiros,
 * criação e listagem de diretorias, operações sobre bitmaps, entre outros.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOFileUtils
{
	/**
	 * URL para a diretoria onde se encontram os ficheiros de configurações e imagens.
	 * Não utilizada na versão 1.0.
	 */
	private final static String main_file_path = "DGADataFiles";

	/**
	 * Tamanho máximo do buffer a ser criado para a leitura do ficheiro de configurações.
	 */
	private static final int MAX_BUFFER = 1024 * 1024;

	/**
	 * Domínio do URL usado nas pesquisas e na obtenção de imagens.
	 */
	public static String BASE_URL = "http://dadosabertos.bitcliq.com";

	/**
	 * Pasta principal onde são colocadas as imagens e o ficheiro de configs a utilizar na App.
	 */
	public static String APP_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "IGEOFiles";

	/**
	 * Construtor por defeito da classe DGADataFiles
	 */
	public IGEOFileUtils(){
		super();
	}


	//Diretorias e caminhos ######################################################################################
	/**
	 * Usado para criar no dispositivo a diretoria base de acesso às configurações.
	 * Não implementado na versão 1.0.
	 */
	public static void createBaseFolder(){

		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

		} 
		else {
			File directory = new File(APP_FOLDER);
			if(!directory.exists())
				directory.mkdirs();
			else
				Log.d("DGTApp", "Directory already created");

		}

	}



	/**
	 * Obtém o caminho para a diretoria principal da aplicação onde são lidos os ficheiros de configurações e onde
	 * se encontram as imagens.
	 * @return Caminho para a diretoria base da App.
	 */
	public static String rootPath(){
		return "/sdcard"+File.separator+main_file_path;
	}








	//Leitura e escrita em ficheiros ######################################################################################
	/**
	 * Lê um ficheiro e devolve o seu conteúdo numa string.
	 * Não utilizada na versão 1.0.
	 * @param pathToFile caminho para o ficheiro.
	 * @return String com o conteúdo do ficheiro.
	 */
	public static String readFileToStringDefaultFolder(String pathToFile) {
		byte[] buffer = readFileToByteArrayDefaultFolder(pathToFile);

		if(buffer!=null){
			return new String(buffer);
		}

		return null;
	}


	/**
	 * Lê um ficheiro e devolve o seu conteúdo numa string.</br>
	 * Não utilizada na versão 1.0.
	 * @param pathToFile caminho para o ficheiro.
	 * @return String com o conteúdo do ficheiro.
	 */
	public static String readFileToString(String pathToFile) {
		byte[] buffer = readFileToByteArray(pathToFile);

		if(buffer!=null){
			return new String(buffer);
		}

		return null;
	}


	/**
	 * Lê um ficheiro na diretoria base e devolve o seu conteúdo num array de bytes.
	 * Não utilizada na versão 1.0.
	 * @param pathToFile caminho para o ficheiro
	 * @return Array de bytes com o conteúdo do ficheiro
	 */
	@SuppressWarnings("resource")
	public static byte[] readFileToByteArrayDefaultFolder(String pathToFile) {
		byte[] result = null;

		try {			
			File file = new File("/sdcard"+File.separator+main_file_path+File.separator+pathToFile);

			if ( file.length() <= Integer.MAX_VALUE) {
				result = new byte[(int) file.length()];
				RandomAccessFile raf = new RandomAccessFile(file, "r");

				raf.readFully(result);				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}



	/**
	 * Lê um ficheiro na diretoria base e devolve o seu conteúdo num array de bytes.
	 * @param pathToFile caminho para o ficheiro
	 * @return Array de bytes com o conteúdo do ficheiro
	 */
	public static byte[] readFileToByteArray(String pathToFile) {
		byte[] result = null;

		try {			
			File file = new File(pathToFile);

			if ( file.length() <= Integer.MAX_VALUE) {
				result = new byte[(int) file.length()];
				@SuppressWarnings("resource")
				RandomAccessFile raf = new RandomAccessFile(file, "r");

				raf.readFully(result);				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}



	/**
	 * Lê um ficheiro na diretoria base e devolve o seu conteúdo num array de bytes.
	 * @param istr Stream usada para ler o ficheiro.
	 * @return Array de bytes con o conteúdo do ficheiro.
	 */
	public static String readFileToStringFromStream(InputStream istr) {
		String s = null;
		byte[] buffer = new byte[MAX_BUFFER];

		try {

			int c=0;
			int offset = 0;

			c = istr.read(buffer, offset, MAX_BUFFER);

			offset+=c;

			return new String(buffer, 0, c);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}



	/**
	 * Lê um ficheiro numa qualquer localização e devolve o seu conteúdo numa String.</br>
	 * @param filepath caminho para o ficheiro.
	 * @return Conteúdo do ficheiro numa String.
	 */
	@SuppressWarnings("resource")
	public static byte[] readFile(String filepath) {
		byte[] result = null;

		try {			
			File file = new File(filepath);

			if ( file.length() <= Integer.MAX_VALUE) {
				result = new byte[(int) file.length()];
				RandomAccessFile raf = new RandomAccessFile(file, "r");

				raf.readFully(result);				
			}

		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return result;
	}


	/**
	 * Elimina um ficheiro ou diretoria recursivamente.
	 * @param fileOrDirectory Caminho para o ficheiro.
	 */
	public static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		try{
			fileOrDirectory.delete();
		} catch(Exception e){
		}
	}


	/**
	 * Elimina um ficheiro.
	 * @param fileName Path para o ficheiro
	 * @return true ou false conforme a eliminação do ficheiro tenha sido bem ou mal sucedida.
	 */
	public static boolean deleteFile(String fileName){

		try{
			File directory = new File(fileName);

			return directory.delete();
		} catch(Throwable e){
			return false;
		}

	}


	/**
	 * Obtêm uma lista com os ficheiros de uma diretoria contida na diretoria principal da aplicação.</br>
	 * @param fileName Caminho para a diretoria
	 * @return Array de strings com os nomes dos ficheiros.
	 */
	public static String[] listDir(String fileName){
		File directory = new File("/sdcard"+File.separator+main_file_path+File.separator+fileName);

		ArrayList<String> fileNames = new ArrayList<String>();
		File[] files = directory.listFiles();
		for (File file : files){
			fileNames.add(file.getName());
		}

		return fileNames.toArray(new String [0]);
	}


	/**
	 * Obtém a lista de diretorias dentro de uma diretoria
	 * @param folder Caminho para a diretoria
	 * @return Lista de diretorias num array de strings
	 */
	public static String[] arrayOfFoldersInFolder(String folder){
		File directory = new File("/sdcard"+File.separator+main_file_path+File.separator+folder);

		ArrayList<String> fileNames = new ArrayList<String>();
		File[] files = directory.listFiles();
		for (File file : files){
			if(file.isDirectory())
				fileNames.add(file.getName());
		}

		return fileNames.toArray(new String [0]);
	}


	/**
	 * Escreve o conteúdo de um array de Bytes num ficheiro na diretoria principal da App.
	 * @param data Buffer com os dados
	 * @param filePath Caminho para o ficheiro
	 */
	public static void writeFileContentToPath(Byte[] data,String filePath){
		File file = new File("/sdcard"+File.separator+main_file_path+File.separator+filePath);

		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for(Byte b : data){
			byte tmp = b.byteValue();
			try {
				fis.write(tmp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	}


	/**
	 * Escreve o conteúdo de um array de bytes num ficheiro.</br>
	 * @param data Buffer com os dados
	 * @param filePath Caminho para o ficheiro
	 */
	public static void writeFileContentToPathbyte(byte[] data,String filePath){
		File file = new File("/sdcard"+File.separator+main_file_path+File.separator+filePath);
		
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for(byte b : data){
			byte tmp = b;
			try {
				out.write(tmp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}













	//Criação de bitmaps ######################################################################################
	/**
	 * Obtém um bitmap de um servidor
	 * @param strName Caminho para o ficheiro remoto.
	 * @return Bitmap correspondente.
	 */
	public static Bitmap getBitmapFromInternet(String strName) {

		try {
			URL url = new URL(strName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}



	//variáveis temporárias utilizadas no método getDrawableFromInternet
	static Bitmap bInternet = null;
	static String strName2;
	//--
	/**
	 * Método usado para obter um Drawable que se encontra num servidor remoto.
	 * Este método chama o método getBitmapFromInternet para obter o bitmap a utilizar no Drawable.
	 * @param strURL URL para o ficheiro.
	 * @return Drawable correspondente
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getDrawableFromInternet(String strURL){

		strName2 = strURL;

		Thread t = new Thread(
				new Runnable() {

					public void run(){
						bInternet = getBitmapFromInternet(strName2);

						DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
						int newHeight = IGEOUtils.getProportionalHeight(bInternet.getWidth(), bInternet.getHeight(), metrics.widthPixels);
						Bitmap resizedBitmap = Bitmap.createScaledBitmap(bInternet, metrics.widthPixels, newHeight, false);

						bInternet = resizedBitmap;

						metrics = null;
						resizedBitmap = null;

						System.gc();
					}

				}
				);
		t.start();

		try {
			t.join();
		} catch(Exception e){

		}

		Drawable d = null;
		if(bInternet!=null){
			d = new BitmapDrawable(bInternet);
		}

		return d;

	}



	/**
	 * Retorna um Bitmap dando o caminho para o ficheiro da imagem correspondente.
	 * @param filePath Caminho opara o ficheiro.
	 * @return Bitmap com o conteúdo do ficheiro.
	 */
	public static Bitmap getBitmapFromAppFolder(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		options.inDither=false;
		options.inPurgeable=true;
		options.inInputShareable=true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		if(IGEOApplication.fieldNativeHeap!=null){
			try{
				IGEOApplication.fieldNativeHeap.set(options,true);
			} catch(Exception e){
				//e.printStackTrace();
			}
		}
		
		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(APP_FOLDER + File.separator + filePath, options);
		return bitmap;
	}



	//Outras operações sobre ficheiros ######################################################################################
	/**
	 * Indica se um ficheiro existe no dispositivo.
	 * @param filePath Caminho para o ficheiro.
	 * @return True ou false consoante o ficheiro existe ou não no dispositivo.
	 */
	public static boolean fileExists(String filePath) {
		File file = new File(filePath);
		
		return file.exists();
	}

}

