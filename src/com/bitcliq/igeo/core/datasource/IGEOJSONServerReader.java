package com.bitcliq.igeo.core.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

/**
 * Esta classe é utilizada para ler do servidor um JSON passando como argumento o URL. Nesta classe é feita ainda a gestão de 
 * ocorrência de erros no acesso ao servidor, o número de tentativas a efetuar, entre outros.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOJSONServerReader {
	
	/**
	 * Indica se ocorreu ou não um erro na leitura do JSON. 
	 */
	public static boolean errorJSON = false;
	
	/**
	 * É utilizado para construir o texto lido através do servidor a qual contém a informação em formato JSON.
	 */
	public static StringBuilder builder = new StringBuilder();
	
//	/**
//	 * Número de tentativas feitas para obter o resultado do servidor.
//	 */
//	private static int times = 0;
	
	/**
	 * String onde é guardado temporariamente o resultado json. Trata-se de uma string static e declarada fora
	 * do método onde é afetada por forma a poupar recursos e gerir de forma mais eficiente a memória.
	 */
	private static String result;
	
	/**
	 * JSONObject onde será guardado o JSON construído através da String lida do servidor.
	 */
	private static JSONObject json;
	
	//VARIÁVEIS PARA ACESSO AO SERVIDOR --
	private static DefaultHttpClient client;
	private static HttpGet httpGet;
	private static HttpEntity entity;
	private static HttpResponse response;
	//--
	
	/**
	 * Stream usada para ler os dados do servidor.
	 */
	private static InputStream content;
	
	/**
	 * Buffer usado para a leitura de dados do servidor
	 */
	private static BufferedReader reader;
	
	/**
	 * Variável temporária que irá conter uma linha de texto lida do servidor.
	 */
	private static String line;
	
	/**
	 * Variável onde será colocado o estado do pedido ao servidor, possibilitando verificar se o
	 * mesmo teve ou não sucesso.
	 */
	private static StatusLine statusLine;
	
	/**
	 * Timeout utilizado na conecção ao servidor para obtenção de dados.
	 */
	private static final int TIMEOUT = 40000;
	
	
	
	
	
	/**
	 * Construtor por defeito
	 */
	public IGEOJSONServerReader(){
		
	}


	
	/**
	 * Lê um JSON do servidor. O URL passado contém com os parâmetros necessários à pesquisa, e o resultado do pedido é 
	 * colocado JSONObject.
	 * @param url URL contendo o link para o servidor e os respetivos parâmetros necessários para uma pesquisa.
	 * @return JSONObject construído a partir da String em formato JSON obtida do servidor, ou null em caso de erro.
	 */
	public static JSONObject getJSONObject(String url){
		
		errorJSON = false;
		
		System.out.println("json url = "+url);

		//times++;

		result = null;
		try{
			result =readJSONFromServer(url);
		} catch(Exception e){
			e.printStackTrace();
			result =readJSONFromServer(url);
			errorJSON = true;
		}

		try {
			json = new JSONObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			errorJSON = true;
			return null;
		}
		
		result = null;
		System.gc();
		
		return json;
	}

	
	/**
	 * Método auxiliar à obtenção do JSON que lida com questões como o número de tentativas, o status do
	 * pedido, entre outros.
	 * @param url URL contendo o link para o servidor e os respetivos parâmetros de pesquisa.
	 * @return JSONObject construído a partir da String em formato JSON obtida do servidor, ou null em caso de erro.
	 */
	private static String readJSONFromServer(String url) {
		
		errorJSON = false;

		client = new DefaultHttpClient();
		httpGet = new HttpGet(url);
		
		//adiciona ao servidor um timeout de 40 segundos
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);
		httpGet.setParams(httpParameters);
		
		System.out.println("url = "+url);

		try {
			
			builder = new StringBuilder();
			
			response = client.execute(httpGet);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				entity = response.getEntity();
				content = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(content));
				while ((line = reader.readLine()) != null) {
					
					try{
						builder.append(line);
					} catch(OutOfMemoryError e){
						e.printStackTrace();
						
						client = null;
						httpGet = null;
						entity = null;
						
						if(content!=null)
							content.close();
						content = null;
						
						if(reader!=null){
							reader.reset();
							reader.close();
						}
						
						line = null;
						response = null;
						statusLine = null;
						
						builder = null;
						
						System.gc();
						return null;
					}
				}

			} else {
				//System.out.println("statusCode = "+statusCode);
				client = null;
				httpGet = null;
				entity = null;
				
				if(content!=null)
					content.close();
				content = null;
				
				if(reader!=null){
					reader.reset();
					reader.close();
				}
				reader = null;
				
				line = null;
				response = null;
				statusLine = null;
				
				builder = null;
				
				System.gc();
				
				errorJSON = true;
				
				return null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
			client = null;
			httpGet = null;
			entity = null;

			content = null;
			
			reader = null;
			
			line = null;
			response = null;
			statusLine = null;
			
			builder = null;
			
			System.gc();
			
			errorJSON = true;
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			errorJSON = true;
			return null;
		}

		String s = "";

		try{
			s = new String(builder.toString());
			builder.delete(0,builder.length()-1);
			builder.setLength(0);
			builder = null;

			client = null;
			httpGet.abort();

			entity.consumeContent();
			entity = null;
			
			content.close();
			content = null;
			
			reader.close();
			reader = null;

			System.gc();
			
			errorJSON = true;
			
			return s;
		} catch(OutOfMemoryError e){
			e.printStackTrace();
			builder.delete(0,builder.length()-1);
			builder.setLength(0);

			System.gc();
			
			System.gc();
			
			errorJSON = true;
			
			return null;
			
		} catch(Exception e){
			e.printStackTrace();

			builder.delete(0,builder.length()-1);
			builder.setLength(0);
			
			client = null;
			httpGet = null;
			entity = null;

			content = null;
			
			reader = null;
			
			line = null;
			response = null;
			statusLine = null;
			
			builder = null;

			System.gc();
			
			errorJSON = true;

			return null;
		}

	}
	
}
