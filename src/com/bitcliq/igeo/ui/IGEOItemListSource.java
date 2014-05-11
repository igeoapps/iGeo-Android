package com.bitcliq.igeo.ui;

/**
 * Classe utilizada para guardar os dados a apresentar num item da lista de fontes.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOItemListSource {
	
	/**
	 * Nome da fonte.
	 */
    private String name;
    
    /**
     * Subtítulo ou legenda da fonte que é apresentada na segunda linha da lista de fontes
     * por baixo do name da fonte.
     */
    private String subTitle;
 
    
    /**
     * Construtor.
     * @param name Nome da fonte
     * @param subTitle Subtítulo ou legenda da fonte que é apresentada na segunda linha da lista de fontes
     * por baixo do name da fonte.
     */
    public IGEOItemListSource(String name, String subTitle) {
        this.name = name;
        this.subTitle = subTitle;
    }
    
    
    /**
     * Devolve o Nome da fonte.
     * @return Nome da fonte.
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * Altera o nome da fonte.
     * @param name Nome da fonte.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Obtém o Subtítulo ou legenda da fonte.
     * @return Subtítulo ou legenda da fonte.
     */
    public String getSubTitle() {
        return subTitle;
    }
    
    
    /**
     * Altera o Subtítulo ou legenda da fonte.
     * @param subTitle Subtítulo ou legenda da fonte.
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    
    
    /**
     * Obtém uma representação em texto do objeto.
     */
    @Override
    public String toString() {
        return name+", "+subTitle;
    }
}