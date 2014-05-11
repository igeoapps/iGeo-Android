package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
 
/**
 * Adapter utilizado na listagem de categorias, no ecrã da escolha de categorias.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOCategoriesListAdapter extends ArrayAdapter<IGEOItemListCategory> {
 
	/**
	 * Contexto a ser utilizado.
	 */
    Context context;
    
    /**
     * Array a view de cada item da lista.
     */
    ArrayList<View> convertViewArray;
 
    /**
     * Construtor.
     * @param context Contexto a ser utilizado.
     * @param resourceId ID do resource que contém o aspeto gráfico dos items da lista.
     * @param items Lista com os dados a apresentar em cada iten da lista.
     */
    public IGEOCategoriesListAdapter(Context context, int resourceId, List<IGEOItemListCategory> items) {
        super(context, resourceId, items);
        this.context = context;
    }
    
    /**
     * Construtor.
     * @param context Contexto a ser utilizado.
     * @param resourceId ID do resource que contém o aspeto gráfico dos items da lista.
     * @param items Lista com osdados a apresentar em cada item da lista.
     * @param convertViewArray Lista de views contedo cada um dos items da lista.
     */
    public IGEOCategoriesListAdapter(Context context, int resourceId, List<IGEOItemListCategory> items, ArrayList<View> convertViewArray) {
        super(context, resourceId, items);
        this.context = context;
        this.convertViewArray = convertViewArray;
    }
    
    
    /**
     * Adiciona a view de um item da lista ao adapter.
     * @param v View contyendo um item da lista.
     */
    public void addView(View v){
    	convertViewArray.add(v);
    }
 
    
    /**
     * Obtém a view de um item da lista.
     * @param position Posição do item na lista.
     * @param convertView View onde vai ser colocado o resultado.
     * @param parent ParentView da view onde vai ser colocado resultado.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
    	return convertViewArray.get(position);
    }
    
}