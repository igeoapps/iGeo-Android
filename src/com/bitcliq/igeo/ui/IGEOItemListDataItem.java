package com.bitcliq.igeo.ui;

/**
 * Classe utilizada para guardar os dados a apresentar num item da lista de DataItems.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOItemListDataItem {
	
	/**
	 * Nome do item.
	 */
    private String name;
    
    /**
     * Descrição do item.
     */
    private String description;
    
    /**
     * Utilizado para indicar se houve uma mudança de categoria. caso seja true, iremos
     * colocar um item com o título da nova categoria.
     */
    private boolean changeCategory;
    
    /**
     * ID da categoria.
     */
    public String categoryID;
 
    
    /**
     * Construtor.
     * @param name Nome do item.
     * @param description Descrição do item apresentada .
     * @param changeCategory Utilizado para indicar se houve uma mudança de categoria. caso seja true, iremos
     * antes deste item colocar um item com o título da nova categoria.
     * @param categoryID ID da categoria.
     */
    public IGEOItemListDataItem(String name, String description, boolean changeCategory, String categoryID) {
        this.name = name;
        this.description = description;
        this.changeCategory = changeCategory;
        this.categoryID = categoryID;
    }
    
    
    /**
     * Obtém o nome do item.
     * @return Nome do item.
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * Altera o nome do item.
     * @param name Nome do item.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Obtém a descrição do item.
     * @return Descrição do item.
     */
    public String getDescription() {
        return description;
    }
    
    
    /**
     * Altera a descrição do item.
     * @param description Descrição do item.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    /**
     * Obtém a indicação de se houve mudança de categoria em relação ao item
     * anterior.
     * @return true ou false conforme houve ou não mudança de categoria em relação ao item anterior.
     */
    public boolean getChangeCategory() {
        return changeCategory;
    }
    
    
    /**
     * Altera a informação de se houve ou não mudança de categoria em relação ao item anterior.
     * @param changeCategory true ou false conforme houve ou não mudança de categoria em relação ao item anterior.
     */
    public void setChangecategory(boolean changeCategory) {  //
        this.changeCategory = changeCategory;
    }
    
    
    /**
     * Obtém o ID da categoria do item.
     * @return ID da categoria do item.
     */
    public String getCategoryID() {
        return categoryID;
    }
    
    
    /**
     * Altera o ID da categoria do item.
     * @param categoryID ID da categoria do item.
     */
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
    @Override
    public String toString() {
        return name+", "+description;
    }
}