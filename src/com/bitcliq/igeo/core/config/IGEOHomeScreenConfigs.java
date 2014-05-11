package com.bitcliq.igeo.core.config;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Configurações de um ecrã home.
 * @author Bitcliq, Lda.
 *
 */

@SuppressWarnings("serial")
public class IGEOHomeScreenConfigs implements IGEOScreenConfig, Serializable 
{
	/**
	 * Configuração da imagem de fundo para cada uma das fontes.
	 * Esta variável é um HashMap que associa a cada fonte o URL de uma imagem de fundo.
	 */
	public HashMap<String,String> backgroundConf;
	
	/**
	 * URL da imagem de topo.
	 */
	public String urlTopImage;
	
	/**
	 * Configuraçõesdos botões da home.
	 */
	public IGEOButtonElementConfig[] btns;
	
	/**
	 * URL para o background do subtitulo.
	 */
	public String subTitleBg;
	
	
	
	
	/**
	 * Construtor por defeito.
	 */
	public IGEOHomeScreenConfigs(){
		super();
		
		btns = new IGEOButtonElementConfig[3];
	}

	
	/**
	 * Obtém o título da imagem de fundo usada na home dado o ID de uma fonte.
	 * @param srcID da fonte
	 * @return URL para a imagem de fundo
	 */
	public String getBackgroundImageForCategoryName(String srcID) {
		if(backgroundConf!=null){
			if(backgroundConf.containsKey(srcID)){
				return backgroundConf.get(srcID);
			}
		}
		
		return null;
	}
	
	
	
	
	/**
	 * Obtém o url de cada um dos botões da home no seu modo normal.
	 * @return Array de Strings com o URL de cada um dos ecrãs, sendo que à posição 0 corresponde o botão "Lista de Fontes", à posição 1 o botão "Perto de Mim"
	 * e à posição 2 o botão do "Explore".
	 */
	public String[] getBtnsNormal(){
		return new String[] { btns[0].imageNormalConfigs, btns[1].imageNormalConfigs, btns[2].imageNormalConfigs };
	}
	
	
	/**
	 * Obtém o url de cada um dos botões da home - modo seleccionado.
	 * @return Array de Strings com o URL de cada um dos ecrãs, sendo que à posição 0 corresponde o botão "Lista de Fontes", à posição 1 o botão "Perto de Mim"
	 * e à posição 2 o botão do "Explore".
	 */
	public String[] getBtnsClicked(){
		return new String[] { btns[0].imageClickConfigs, btns[1].imageClickConfigs, btns[2].imageClickConfigs };
	}
	
}

