package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Adapter utilizado na listagem de DataItems, no ecrã da escolha de categorias.
 * @author Bitcliq, Lda.
 *
 */
public class IGEODataItemListAdapter extends ArrayAdapter<IGEOItemListDataItem> {

	/**
	 * Contexto a ser utilizado.
	 */
	Context context;

	/**
	 * Array a view de cada item da lista.
	 */
	ArrayList<View> convertViewArray;

	//para obter a lista atual de itens
	/**
	 * Lista com os itens associados à lista atual.
	 */
	List<IGEOItemListDataItem> items;

	
	/**
	 * Construtor.
	 * @param context Contexto a utilizar.
	 * @param resourceId Ientificador do recurso que contém o aspeto gráfico dos itens da lista.
	 * @param items Lista com os itens associados à lista atual.
	 */
	public IGEODataItemListAdapter(Context context, int resourceId, List<IGEOItemListDataItem> items) {
		super(context, resourceId, items);
		this.context = context;
		this.items = items;
	}
	
	/**
	 * Construtor.
	 * @param context Contexto a utilizar.
	 * @param resourceId Ientificador do recurso que contém o aspeto gráfico dos itens da lista.
	 * @param items Lista com os itens associados à lista atual.
	 * @param convertViewArray Lista de views que contêm cada um dos itens da lista.
	 */
	public IGEODataItemListAdapter(Context context, int resourceId, List<IGEOItemListDataItem> items, ArrayList<View> convertViewArray) {
		super(context, resourceId, items);
		this.context = context;
		this.convertViewArray = convertViewArray;
		this.items = items;
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
     * @param convertView View onde vai ser colocado o resultado.
     * @param parent Parent da view onde vai ser colocado o resultado.
     */
	public View getView(int position, View convertView, ViewGroup parent) {
		return convertViewArray.get(position);
	}


	/**
	 * Remove todas as views da lista. Esta operação é util quando lidamos com listas muito grandes
	 * ou quando pretendemos, usando a mesma lista, remover os itens atuais e adicionar itens novos,
	 * como é o caso da pesquisa por palavras-chave na lista de DataItems.
	 */
	public void removeAllViews(){
		if(convertViewArray!=null){
			convertViewArray.clear();
		}
			
		if(items!=null)
			items.clear();

		System.gc();
	}

}