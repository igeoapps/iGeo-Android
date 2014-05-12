package com.bitcliq.igeo.ui.places;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Classe utilizada para guardar os dados dos locais (distrito / concelho / freguesia) pelos quais iremos fazer a filtragem no explore.
 * Aqui além do carregamento dos locais e da associação entre os mesmos, é
 * possível obter os concelhos de um distrito e as freguesias de um concelho de uma forma simples e rápida.
 * @author Bitcliq, Lda.
 *
 */
@SuppressWarnings("serial")
public class IGEOPlaces implements Serializable {

	/**
	 * HashMap com os distritos indexados pelo seu código.
	 */
	public HashMap<String,IGEODistrict> districts;

	/**
	 * Stream utilizado na leitura dos locais, isto é, distritos, concelhos e freguesias.
	 */
	private InputStream is;


	/**
	 * Construtor no qual passamos logo a stream usada na leitura do ficheiro.
	 * @param is Stream usada na leitura do ficheiro.
	 */
	public IGEOPlaces(InputStream is){
		this.districts=new HashMap<String,IGEODistrict>();

		this.is=is;

		String data[][] = readFile();
		addLocais(data[0],data[1],data[2],data[3],data[4],data[5]);
	}

	/**
	 * Construtor por defeito.
	 */
	public IGEOPlaces(){
		this.districts=new HashMap<String,IGEODistrict>();
	}


	/**
	 * Permite obter os códigos e nomes dos distritos em dois arrays.
	 * @return Array com duas posições, sendo que a primeira contém um array de Strings com os códigos dos
	 * distritos e o segundo contém os nomes dos distritos. A primeira posição de ambos os arrays contém a
	 * String vazia, sendo útil para na lista associarmos essa posição à posição com o texto "<Selecione>".
	 */
	public String[][] getDistrictsArrays(){
		String[] codes = districts.keySet().toArray(new String[0]);
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(Arrays.asList(codes));
		tmp.add(0,"");

		String[] nomes = new String[codes.length+1];
		nomes[0]="";
		IGEODistrict[] ds = districts.values().toArray(new IGEODistrict[0]);
		for(int i=0;i<ds.length;i++){
			nomes[i+1]=ds[i].name;
		}

		String[][] result = new String[2][];
		result[0]=tmp.toArray(new String[0]);
		result[1]=nomes;

		result = sortArrayCollection(result[0],result[1]);

		return result;
	}


	/**
	 * Permite obter os códigos e nomes dos concelhos de um distrito em dois arrays.
	 * @param districtCod Código do distrito no qual pretendemos obter os concelhos.
	 * @return Array com duas posições, sendo que a primeira contém um array de Strings com os códigos dos
	 * concelhos e o segundo contém os nomes dos concelhos. A primeira posição de ambos os arrays contém a
	 * String vazia, sendo útil para na lista associarmos essa posição à posição com o texto "<Selecione>".
	 */
	public String[][] getCouncilsArraysForDistrict(String districtCod){
		String[] codes = districts.get(districtCod).councils.keySet().toArray(new String[0]);
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(Arrays.asList(codes));
		tmp.add(0,"");

		String[] names = new String[codes.length+1];
		names[0]="";
		IGEOCouncil[] cs = districts.get(districtCod).councils.values().toArray(new IGEOCouncil[0]);
		for(int i=0;i<cs.length;i++)
			names[i+1]=cs[i].name;

		String[][] result = new String[2][];
		result[0]=tmp.toArray(new String[0]);
		result[1]=names;

		result = sortArrayCollection(result[0],result[1]);
		
		codes = null;
		names = null;

		return result;
	}


	/**
	 * Permite obter os códigos e nomes das freguesias de um concelho em dois arrays.
	 * @param districtCod Código do distrito no qual pretendemos obter as freguesias.
	 * @param councilCod Código do concelho no qual pretendemos obter as freguesias.
	 * @return Array com duas posições, sendo que a primeira contém um array de Strings com os códigos dos
	 * freguesias e o segundo contém os nomes dos freguesias. A primeira posição de ambos os arrays contém a
	 * String vazia, sendo útil para na lista associarmos essa posição à posição com o texto "<Selecione>".
	 */
	public String[][] getParishesArraysForCouncil(String districtCod,String councilCod){
		String[] codes = districts.get(districtCod).councils.get(councilCod).parishes.keySet().toArray(new String[0]);
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(Arrays.asList(codes));
		tmp.add(0,"");

		String[] names = new String[codes.length+1];
		names[0]="";
		IGEOParish[] fs = districts.get(districtCod).councils.get(councilCod).parishes.values().toArray(new IGEOParish[0]);
		for(int i=0;i<fs.length;i++)
			names[i+1]=fs[i].name;

		String[][] result = new String[2][];
		result[0]=tmp.toArray(new String[0]);
		result[1]=names;

		result = sortArrayCollection(result[0],result[1]);

		return result;
	}


	/**
	 * Trata-se de um método específico para a leitura do ficheiro com os locais.
	 * @return Devolve um array de arrays de Strings com 6 posições, em que temos respetivamente: o array de
	 * códigos de distritos, o array de nomes de distritos, o array de códigos de concelhos, o array de nomes de
	 * concelhos, o array de códigos de freguesia e o array de nomes de freguesias.
	 */
	public String[][] readFile(){
		try {
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"ISO-8859-1"));

			ArrayList<String> districtNames=new ArrayList<String>();
			ArrayList<String> districtCodes=new ArrayList<String>();

			ArrayList<String> councilNames=new ArrayList<String>();
			ArrayList<String> councilCodes=new ArrayList<String>();

			ArrayList<String> parishNames=new ArrayList<String>();
			ArrayList<String> parishCodes=new ArrayList<String>();

			String line="";
			while((line = br.readLine()) !=null){
				StringTokenizer stLine = new StringTokenizer(line," \t");
				String cod=stLine.nextToken();
				String name="";
				while(stLine.hasMoreTokens()){
					name+=stLine.nextToken()+" ";
				}

				if(cod.length()==2){
					districtNames.add(name);
					districtCodes.add(cod);
				} else if(cod.length()==4){
					councilNames.add(name);
					councilCodes.add(cod);
				} else if(cod.length()==6){
					parishNames.add(name);
					parishCodes.add(cod);
				} 
			}

			br.close();

			String[][] data = new String[6][];
			data[0]=(String[]) districtCodes.toArray(new String[0]);
			data[1]=(String[]) districtNames.toArray(new String[0]);
			data[2]=(String[]) councilCodes.toArray(new String[0]);
			data[3]=(String[]) councilNames.toArray(new String[0]);
			data[4]=(String[]) parishCodes.toArray(new String[0]);
			data[5]=(String[]) parishNames.toArray(new String[0]);

			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}









	/**
	 * Ordena por ordem crescente, utilizando o algoritmo insertion sort, um conjunto de dois arrays,
	 * tendo como base comparações existentes no segundo array. Isto será utilizado para ordenar arrays
	 * contendo os dados dos distritos, concelhos e freguesias, mantendo a ordem dos seus códigos. Assim,
	 * sempre que alteramos uma posição no array dos nomes, alteramos a mesma posição no array de códigos.
	 * @param a Array com os códigos.
	 * @param b Array com os nomes.
	 * @return Array de códigos e de nomes ordenado por ordem crescente de name.
	 */
	private String[][] sortArrayCollection(String[] a,String[] b){

		for (int i = 1; i < a.length; i++){
			String tmp = new String(a[i].getBytes());
			String tmp2 = new String(b[i].getBytes());
			for (int j = i - 1; j >= 0 && b[j].compareTo(tmp2)>0; j--){
				a[j + 1] = a[j];
				b[j + 1] = b[j];
				a[j] = tmp;
				b[j] = tmp2;
			}
		}

		String[][] data = new String[2][];
		data[0]=a;
		data[1]=b;

		return data;
	}


	/**
	 * Costroi a estrutura a ser utilizada, contendo os distritos, os concelhos de cada um, e para cada conselho
	 * as respetivas freguesias, tudo isto indexados pelos seus códigos.
	 * @param districtCodes Array com os códigos dos distritos.
	 * @param districtNames Array com os nomes dos distritos.
	 * @param councilCodes Array com os códigos dos concelhos.
	 * @param councilNames Array com os nomes dos concelhos.
	 * @param parishCodes Array com os códigos das freguesias.
	 * @param parishNames Array com os nomes das freguesias.
	 */
	public void addLocais(String[] districtCodes,String[] districtNames,String[] councilCodes,String[] councilNames,String[] parishCodes,String[] parishNames){
		for(int i=0;i<districtCodes.length;i++){
			districts.put(districtCodes[i], new IGEODistrict(districtNames[i]));
		}

		for(int i=0;i<councilCodes.length;i++){
			String codDistrito=(new String(""+councilCodes[i])).substring(0,2);
			districts.get(codDistrito).councils.put(councilCodes[i],new IGEOCouncil(councilNames[i]));
		}

		for(int i=0;i<parishCodes.length;i++){
			String codDistrito=(new String(""+parishCodes[i])).substring(0,2);
			String codFreguesia=(new String(""+parishCodes[i])).substring(0,4);
			districts.get(codDistrito).councils.get(codFreguesia).parishes.put(parishCodes[i],new IGEOParish(parishNames[i]));
		}
	}


	/**
	 * Obtém um HashMap com os distritos, indexados pelo seu código.
	 * @return HashMap com os distritos, indexados pelo seu código.
	 */
	public HashMap<String,IGEODistrict> getDistritos(){
		return districts;
	}
}
