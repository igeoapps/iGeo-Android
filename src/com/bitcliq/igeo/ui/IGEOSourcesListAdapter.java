package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


/**
 * Adapter utilizado no ecrã da lista de fontes.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOSourcesListAdapter extends ArrayAdapter<IGEOItemListSource> {

	/**
	 * Contexto a ser utilizado.
	 */
	Context context;

	/**
	 * ArrayList de views.
	 */
	ArrayList<View> convertViewArray;

	/**
	 * Construtor.
	 * @param context Contexto a ser utilizado.
	 * @param resourceId identificador do recurso que contém o aspeto gráfico dos itens da lista.
	 * @param items Lista com os dados a apresentar.
	 */
	public IGEOSourcesListAdapter(Context context, int resourceId, List<IGEOItemListSource> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/**
     * Construtor.
     * @param context Contexto a ser utilizado.
     * @param resourceId ID do resource que contém o aspeto gráfico dos itens da lista.
     * @param items Lista com dados a apresentar em cada item da lista.
     * @param convertViewArray Lista de views que contém cada um dos itens da lista.
     */
	public IGEOSourcesListAdapter(Context context, int resourceId, List<IGEOItemListSource> items, ArrayList<View> convertViewArray) {
		super(context, resourceId, items);
		this.context = context;
		this.convertViewArray = convertViewArray;
	}

	
	/**
     * Adiciona a view de um item da lista ao adapter.
     * @param v View contendo um item da lista.
     */
	public void addView(View v){
		convertViewArray.add(v);
	}


	/**
     * Obtém a view de um item da lista.
     * @param position Posição do item na lista.
     * @param convertView View onde vai ser colocado o resultado.
     * @param parent ParentView da view onde vai ser colocado o resultado.
     */
	public View getView(int position, View convertView, ViewGroup parent) {
		return convertViewArray.get(position);
	}

}