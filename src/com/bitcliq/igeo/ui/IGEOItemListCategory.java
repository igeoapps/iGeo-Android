package com.bitcliq.igeo.ui;

/**
 * Classe utilizada para guardar os dados de um item a apresentar na lista de categorias.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOItemListCategory {
	
	/**
	 * ID da categoria.
	 */
    private String categoryID;
    
    /**
     * URL do ícone da categoria no estado normal.
     */
    private String urlImg;
    
    /**
     * URL do ícone da categoria no estado selecionado.
     */
    private String urlImgSelected;
 
    
    /**
     * Construtor.
     * @param categoryID ID da categoria.
     * @param urlImg URL do ícone da categoria no estado normal.
     * @param urlImgSelected URL do icone da categoria no estado selecionado.
     */
    public IGEOItemListCategory(String categoryID, String urlImg, String urlImgSelected) {
    	this.categoryID = categoryID;
        this.urlImg = urlImg;
        this.urlImgSelected = urlImgSelected;
    }
    
    
    /**
     * Obtém o ID da categoria.
     * @return ID da categoria.
     */
    public String getCategoryID() {
        return categoryID;
    }
    
    /**
     * Altera o ID da categoria.
     * @param categoryID ID da categoria.
     */
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
    
    /**
     * Obtém o URL do icone da categoria no estado normal.
     * @return URL do icone da categoria no estado normal.
     */
    public String getUrlImg() {
        return urlImg;
    }
    
    /**
     * Altera o URL do icone da categoria no estado normal.
     * @param urlImg URL do icone da categoria no estado normal.
     */
    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
    
    /**
     * Obtém o URL do icone da categoria no estado selecionado.
     * @return URL do icone da categoria no estado selecionado.
     */
    public String getUrlImgSelected() {
        return urlImgSelected;
    }
    
    /**
     * Altera o URL do icone da categoria no estado selecionado.
     * @param urlImgSelected URL do icone da categoria no estado selecionado.
     */
    public void setUrlImgSelected(String urlImgSelected) {
        this.urlImgSelected = urlImgSelected;
    }
    
}