package com.bitcliq.igeo.threads;

/**
 * Esta classe é utilizada para guardar a informação correspondente a uma imagem que pretendemos ir buscar ao servidor, a colocar na UI da App.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOConfigDownloadItem {
	
	/**
	 * Representa o tipo do item que pretendemos descarregar. Os valores possíveis são:</br>
	 *<li>HomeBgImage: Imagem de fundo do ecrã da Home.</li>
	 *<li>ListBGImage: Imagem de fundo do ecrã da lista de itens.</li>
	 *<li>CatIconNormal: Imagem para o estado normal do ícone de uma categoria utilizado no ecrã de seleção de categorias.</li>
	 *<li>CatIconSelected: Imagem para o estado selecionado do ícone de uma categoria utilizado no ecrã de seleção de categorias.</li>
	 *<li>CatPinImage: Imagem do pin usado na legenda do mapa para uma categoria.</li>
	 * @author Bitcliq, Lda.
	 *
	 */
	public enum IGEODonwloadItemType {
		HomeBgImage,
	    ListBGImage,
	    CatIconNormal,
	    CatIconSelected,
	    CatPinImage
	};
	
	/**
	 * URL da imagem a descarregar.
	 */
	String url;
	
	/**
	 * Nome do ficheiro que vamos guardar no dispositivo
	 */
	String destinationFolder;
	
	/**
	 * Identificador da fonte caso aplicável, "-1" caso contrário.
	 */
	String srcID;
	
	/**
	 * Identificador da categoria caso aplicável, "-1" caso contrário.
	 */
	String catID;
	
	/**
	 * Tipo de imagem que pretendemos descarregar.
	 */
	IGEODonwloadItemType type;
	
	String colorHex;
	
	String colorHue;
	
	/**
	 * Construtor da classe.
	 * @param URL URL da imagem a descarregar.
	 * @param destination Nome do ficheiro que vamos guardar no dispositivo.
	 * @param sID ID da fonte caso aplicável, ou "-1" caso contrário.
	 * @param cID ID da categoria caso aplicável, ou "-1" caso contrário.
	 * @param t Tipo de item que pretendemos descarregar.
	 */
	public IGEOConfigDownloadItem(String URL, String destination, String sID, String cID, IGEODonwloadItemType t) {
		url = URL;
        destinationFolder = destination;
        srcID = sID;
        catID = cID;
        type = t;
	}
	
	
	/**
	 * Construtor da classe.
	 * @param URL URL da imagem a descarregar.
	 * @param destination Nome do ficheiro que vamos guardar no dispositivo.
	 * @param sID ID da fonte caso aplicável, ou "-1" caso contrário.
	 * @param cID ID da categoria caso aplicável, ou "-1" caso contrário.
	 * @param t Tipo de item que pretendemos descarregar.
	 * @param hex Cor em hexadecimal caso se trate de um pin do mapa.
	 * @param hue Hue da cor caso se trate de um pin do mapa.
	 */
	public IGEOConfigDownloadItem(String URL, String destination, String sID, String cID, IGEODonwloadItemType t, String hex, String hue) {
		url = URL;
        destinationFolder = destination;
        srcID = sID;
        catID = cID;
        type = t;
        this.colorHex = hex;
        this.colorHue = hue;
	}
	
	
	/**
	 * Devolve uma representação legível do item.
	 * @return String Representação do item numa String.
	 */
	public String toString() {
		String result = "";
		
		result += "IGEOConfigDownloadItem (url = url, destinationFolder = destinationFolder, srcID = srcID, catID = catID, type = type)";
		
		return result;
	}

}
