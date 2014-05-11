package com.bitcliq.igeo.ui.places;

/**
 * Classe utilizada para guardar os dados a apresentar num item da lista dos
 * locais de pesquisa a selecionar, isto é, dos distritos, concelhos e freguesias.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOGenericSearchListItem {
	
	/**
	 * Nome do distrito, concelho ou freguesia.
	 */
    private String name;
 
    /**
     * Construtor.
     * @param name Nome do distrito concelho ou freguesia.
     */
    public IGEOGenericSearchListItem(String name) {
        this.name = name;
    }
    
    /**
     * Obtém o nome do distrito, concelho ou freguesia a apresentar na lista.
     * @return Nome do distrito, concelho ou freguesia.
     */
    public String getTitle() {
        return name;
    }
    
    /**
     * Altera o nome do distrito, concelho ou freguesia a apresentar na lista.
     * @param name Nome do distrito concelho ou freguesia.
     */
    public void setTitle(String name) {
        this.name = name;
    }
    
    /**
     * Representação em texto do item.
     */
    @Override
    public String toString() {
        return name;
    }
}