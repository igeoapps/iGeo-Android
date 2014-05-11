package com.bitcliq.igeo.ui.places;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Adapter a ser utilizado na ListView com os distritos, na ListView com os concelhos,
 * e na ListView com as fregueisas.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOGenericSearchListAdapter extends ArrayAdapter<IGEOGenericSearchListItem> {
 
	/**
	 * Contexto a utilizar.
	 */
    Context context;
    
    /**
     * ArrayList com as views dos items da lista
     */
    ArrayList<View> convertViewArray;
 
    
    /**
     * Construtor do adapter.
     * @param context Contexto a ser utilizado.
     * @param resourceId Identificador do recurso que contém o aspeto gráfico dos items da lista.
     * @param items Lista que contém os dados a apresentar.
     */
    public IGEOGenericSearchListAdapter(Context context, int resourceId, List<IGEOGenericSearchListItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }
    
    /**
     * Construtor do adapter.
     * @param context Contexto a ser utilizado.
     * @param resourceId Identificador do recurso que contém o aspeto gráfico dos items da lista.
     * @param items Lista que contém os dados a apresentar.
     * @param convertViewArray Lista que contém as views já criadas com os items da lista.
     */
    public IGEOGenericSearchListAdapter(Context context, int resourceId, List<IGEOGenericSearchListItem> items, ArrayList<View> convertViewArray) {
        super(context, resourceId, items);
        this.context = context;
        this.convertViewArray = convertViewArray;
    }
    
    
    /**
     * Adiciona a view de um item da lista ao adapter.
     * @param v View que contém um item da lista.
     */
    public void addView(View v){
    	convertViewArray.add(v);
    }
 
    
    /**
     * Obtém a view de um item da lista.
     * @param position Posição do item na lista.
     * @param convertView View onde vamos colocar o resultado.
     * @param parent ParentView da view onde pretendemos colocaro resultado.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
    	return convertViewArray.get(position);
    }
    
}