package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEODataManager;
import com.bitcliq.igeo.core.IGEOFileUtils;
import com.bitcliq.igeo.core.IGEOUtils;
import com.bitcliq.igeo.core.config.IGEOScreenConfig;
import com.bitcliq.igeo.core.datasource.IGEOCategory;
import com.bitcliq.igeo.core.datasource.IGEOGenericDataItem;
import com.bitcliq.igeo.core.datasource.IGEOJSONServerReader;
import com.bitcliq.igeo.ui_ordenamento.R;


/**
 * Esta atividade apresenta uma lista de itens  Contém ainda a possibilidade de pesquisar por
 * palavras chave, ir para o mapa e voltar para a Home.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOListActivity extends Activity implements IGEOConfigurableActivity {

	/**
	 * Tamanho máximo que uma linha da TextView (título do item) pode ocupar
	 */
	private static final int MAX_TEXT_SIZE = 333;

	/**
	 * Número máximo de itens que são carregados na lista de cada vez.
	 */
	private final static int LIM_ITEMS = 10;

	/**
	 * Fonte personalizada a utilizar em algumas TextView's.
	 */
	private static Typeface fontRegular;

	/**
	 * Fonte personalizada a utilizar em algumas TextView's.
	 */
	private static Typeface fontMedium;

	/**
	 * Fonte personalizada a utilizar em algumas TextView's.
	 */
	private static Typeface fontBold;

	/**
	 * Último item selecionado.
	 */
	private int currentLastItem = 0;

	/**
	 * Item atualmente selecionado.
	 */
	private int actualSelection = 0;

	/**
	 * Lista de itens.
	 * Neste momento todos os itens são carregados, mesmo os que não estão visiveis na lista.
	 * De futuro o ideal será pedir ao servidor apenas o número de itens que pode ser visualizado na lista
	 * Neste momento são obtidos todos os itens e a contrói-se a ListView à medida
	 * que o utilizador clica em "Ver mais".
	 */
	public ArrayList<IGEOGenericDataItem> listItems;

	/**
	 * Imagem de fundo.
	 */
	public ImageView bgImage;

	/**
	 * Botão de ida para o mapa.
	 */
	public ImageView bMap;

	/**
	 * TextEdit utilizada para escrever o texto da pesquisa.
	 */
	public EditText tSearch;

	/**
	 * Ícone da pesquisa inserido na EditText.
	 */
	public ImageView bSearch;

	/**
	 * View principal da actividade.
	 */
	private RelativeLayout rootView;

	/**
	 * HashMap que contém para cada posição da lista o item correspondente. Isto é necessário para sabermos
	 * em que item clicados.
	 */
	private HashMap<Integer, String> hashMapPositionDataItems;

	/**
	 * Indica se o utilizador veio do mapa.
	 */
	private boolean fromMap;

	/**
	 * Indica se foi efetuada alguma pesquisa por palavras-chave, ou alguma alteração nas palavras chave. Isto permite que caso o utilizador tenha vindo
	 * mapa, quando volta à lista sejam carregados os itens de acordo com a pesquisa efetuada.
	 */
	private boolean changedSearch;

	/**
	 * View de topo que contém a pesquisa e os restantes botões.
	 */
	private RelativeLayout topView;


	public IGEOScreenConfig screenConfigs;

	/**
	 * Timer utilizado para remover a selecção ao fim de alguns milissegundos um item clicado.</br>
	 * Poderão haver outras soluções.
	 */
	private Timer timerDesselect;

	/**
	 * Botão para voltar à Home.
	 */
	private ImageView bHome;



	//Variáveis da lista
	/**
	 * Lista onde vão ser apresentados os itens.
	 */
	public ListView listView;

	/**
	 * ArrayList que será utilizado para albergar as views de cada item da lista.
	 */
	private ArrayList<View> viewConvertArrayListDataItems = null;

	/**
	 * Lista com o conteúdo a colocar em cada um dos itens da lista.
	 */
	private ArrayList<IGEOItemListDataItem> dataContentDataItems;

	/**
	 * Adapter usado na lista.
	 */
	private IGEODataItemListAdapter listAdpArrayDataItems = null;

	/**
	 * TextView onde é apresentado o número de resultados obtidos.
	 */
	private TextView tResults;

	/**
	 * Número de categorias adicionadas na ListView até ao momento.
	 */
	int numAddedCategories = 0;
	//--


	//Carregamento dos itens --
	/**
	 * LongOperation usada no carregamento assíncrono dos itens.
	 */
	private LongOperationLoadDataItems loldi = null;

	/**
	 * Dialog que apresenta ao utilizador a mensagem de loading.
	 */
	private ProgressDialog progDailogLoad = null;
	//--


	/**
	 * TextView onde é colocado o nome da fonte em que nos encontramos
	 */
	private TextView tSubtitle;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);


		//Fontes
		fontRegular = Typeface.createFromAsset(getAssets(), this.getResources().getString(R.string.regular_font));
		fontMedium = Typeface.createFromAsset(getAssets(),  this.getResources().getString(R.string.medium_font));



		hashMapPositionDataItems = new HashMap<Integer, String>();

		//é importante saber se o utilizador veio do mapa para que, quando clica no botão do mapa o comportamento seja dependente
		//desse facto. Ou seja, se o utilizador veio do ecrã do mapa, e clicamos no botão do mapa, é feito apenas um back.
		//Mas, caso o utilizador tenha feito uma pesquisa e obtido resultados, é necessário
		//recarregar o mapa com os novos items.
		Bundle b = this.getIntent().getExtras();
		try{
			fromMap = b.getBoolean("fromMap"); 
		}
		catch(Exception e){
			fromMap = false;
		}


		rootView = (RelativeLayout) findViewById(R.id.RootView);


		//colocar a imagem da fonte atual
		final String urlBGImage = IGEOConfigsManager.getBackgroundRightImageForSource(IGEODataManager.getActualSource().sourceID);

		if(urlBGImage!=null){  //Se existe imagem para essa fonte
			changeBG(urlBGImage);
		}
		else {  //Se não existe imagem
			if(IGEODataManager.getActualSource()!=null){  //Se existe fonte selecionada

				changeBG(IGEOConfigsManager.getBackgroundRightImageForSource("-1"));
				
			}
		}
		//


		changedSearch = false;


		bHome = (ImageView) findViewById(R.id.bHome);
		bHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//limpar dados
				IGEODataManager.clearKeywords();
				IGEODataManager.clearLocationSearch();
				IGEODataManager.clearTemporaryDataItems();
				IGEODataManager.localHashMapCategories.clear();

				// terminar as atividades até chegar á home
				Intent intent = new Intent();
				Bundle b2 = new Bundle();
				b2.putInt("backHome",1);
				intent.putExtras(b2);
				setResult(Activity.RESULT_OK,intent);

				intent = null;
				b2 = null;

				View vRoot = findViewById(R.id.RootView);
				finish();
				unbindDrawables(vRoot);
				System.gc();
			}
		});
		bHome.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bHome.setImageResource(R.drawable.home2_click);
				}
				else {
					bHome.setImageResource(R.drawable.home2);
				}

				return false;
			}
		});



		bMap = (ImageView) findViewById(R.id.bMap);
		bMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(fromMap){
					if(!changedSearch)
						onBackPressed();
					else {
						IGEODataManager.clearTemporaryDataItems();
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putBoolean("changedSearch",true);
						intent.putExtras(b);
						setResult(Activity.RESULT_OK,intent);

						View vRoot = findViewById(R.id.RootView);
						finish();
						unbindDrawables(vRoot);
						System.gc();
					}
				}
				else {
					// TODO Auto-generated method stub
					Intent i = new Intent(IGEOListActivity.this, IGEONativeMapActivity.class);
					Bundle b = new Bundle();
					b.putBoolean("fromList", true);
					i.putExtras(b);
					startActivityForResult(i, 1);
				}
			}
		});
		bMap.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bMap.setImageResource(R.drawable.mapa_click);
				}
				else {
					bMap.setImageResource(R.drawable.mapa);
				}

				return false;
			}
		});


		tSearch = (EditText) findViewById(R.id.editTextSearch);
		tSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					changedSearch = true;

					IGEODataManager.clearTemporaryDataItems();

					if(tSearch.getText()!=null){
						//substitui o espaço pelo caracter +, de forma a pesquisar por várias palavras separadas.
						search(tSearch.getText().toString().replace(" ", "+"));
					}

					//fechar o teclado
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(tSearch.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

					in = null;
				}
				return false;
			}
		});
		tSearch.clearFocus();

		bSearch = (ImageView) findViewById(R.id.bSearch);
		bSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changedSearch = true;

				IGEODataManager.clearTemporaryDataItems();
				if(tSearch.getText()!=null){
					//substitui o espaço pelo caracter +, de forma a pesquisar por várias palavras separadas.
					search(tSearch.getText().toString().replace(" ", "+"));
				}

				//fechar o teclado
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(tSearch.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

				in = null;
			}
		});
		bSearch.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bSearch.setImageResource(R.drawable.pesquisa_click);
				}
				else {
					bSearch.setImageResource(R.drawable.pesquisa);
				}

				return false;
			}
		});

		tResults = (TextView) findViewById(R.id.tResults);

		//Obtem fontes e constrói a lista
		constructDataItemsList();


		tSubtitle = (TextView) findViewById(R.id.t_subtitle);
		tSubtitle.setText(IGEODataManager.getActualSource().sourceName);
		tSubtitle.setTextColor(Color.WHITE);
		tSubtitle.setTypeface(fontBold);
		tSubtitle.setTextSize(12.0f);


		//aplicar as configurações iniciais
		applyConfigs();

	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {     

		//utilizamos este método para que quando colocamos a app em background sejam gravadas as configurações
		//da mesma.
	    if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	    	IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}

















	//Obtém as sources e constroi a lista com as mesmas #####################################
	/**
	 * Esta operação permite o carregamento de forma assicrona dos itens a colocar na lista.
	 * @author Bitcliq, Lda.
	 *
	 */
	private class LongOperationLoadDataItems extends AsyncTask<String, Void, String> {

		boolean canceledLoad = false;

		public void cancelLoaddataItems(){
			canceledLoad = true;
			this.cancel(true);
		}


		@Override
		protected String doInBackground(String... params) {

			if(canceledLoad){
				return null;
			}

			//Obter os pontos
			ArrayList<String> categories = IGEODataManager.getActualCategoriesListActualFilterIDS();

			if(IGEODataManager.getLocalHashMapDataItems()!=null){  //se já existem dados guardados e a filtragem é a mesma vamos busca-los
				listItems = IGEODataManager.getLocalListDataItems();
			}
			else {  //se ainda não existem vamos ao servidor

				if(IGEODataManager.getLocalHashMapDataItems()!=null){  //limpa os dados atualmente guardados
					IGEODataManager.clearTemporaryDataItems();
				}

				if(IGEODataManager.getLocalHashMapDataItems()!=null){  //se os dados já existem vamos busca-los
					listItems = IGEODataManager.getLocalListDataItems();
				}
				else {  //se ainda não existem vamos ao servidor
					if(IGEODataManager.getLocationSearch()==null){  //se estamos a fazer uma pesquisa no perto de mim
						if(IGEODataManager.getKeywords()!=null){  //se estamos a fazer uma pesquisa com keywords
							listItems = IGEODataManager.getListForSourceAndCategoriesNearFiltered(
									IGEODataManager.getActualSource().sourceID,
									categories,
									IGEODataManager.getKeywords(),
									((IGEOApplication) IGEOListActivity.this.getApplication()).getActualLocation()
									);
						}
						else {
							listItems = IGEODataManager.getListForSourceAndCategoriesNear(
									IGEODataManager.getActualSource().sourceID,
									categories,
									((IGEOApplication) IGEOListActivity.this.getApplication()).getActualLocation()
									);
						}
					}
					else {  //se estamos a fazer uma pesquisa no explore
						if(IGEODataManager.getKeywords()!=null){  //se estamos a fazer uma pesquisa com keywords
							listItems = IGEODataManager.getListForSourceAndCategoriesInSearchLocationFiltered(
									IGEODataManager.getActualSource().sourceID,
									categories,
									IGEODataManager.getKeywords()
									);
						}
						else {
							listItems = IGEODataManager.getListForSourceAndCategoriesInSearchLocation(
									IGEODataManager.getActualSource().sourceID,
									categories
									);
						}
					}
				}
			}


			//limpa a lista atual de categorias
			if(categories!=null)
				categories.clear();
			categories = null;


			return null;
		}      



		@Override
		protected void onPostExecute(String result) {

			IGEOListActivity.this.runOnUiThread(new Runnable() {

				public void run() {

					progDailogLoad.dismiss();
					progDailogLoad = null;

				}
			});

			if(canceledLoad){

				IGEOListActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						progDailogLoad.dismiss();
						progDailogLoad = null;

					}
				});

				return;
			}

			if(listItems==null){

				IGEOListActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						if(listView!=null){
							numTitlesInList = 0;
							currentLastItem = 0;
							listView = (ListView) findViewById(R.id.listViewDataItems);
							viewConvertArrayListDataItems = new ArrayList<View>();
							listAdpArrayDataItems = new IGEODataItemListAdapter(IGEOListActivity.this,R.layout.items_list_data_items, new ArrayList<IGEOItemListDataItem>(),new ArrayList<View>());
							listView.setAdapter(listAdpArrayDataItems);

							System.gc();
						}

						tResults.setText(IGEODataManager.getActualNumResults()+" "+IGEOListActivity.this.getResources().getString(R.string.text_regists));

						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOListActivity.this);
						adb.setMessage((IGEOJSONServerReader.errorJSON ? 
								IGEOListActivity.this.getResources().getString(R.string.text_msg_error_list):
									IGEOListActivity.this.getResources().getString(R.string.text_msg_no_data_list)));
						adb.setNeutralButton((IGEODataManager.getKeywords()==null?getResources().getString(R.string.text_back):getResources().getString(R.string.text_ok)), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								if(IGEODataManager.getKeywords()==null){
									IGEODataManager.clearKeywords();
									onBackPressed();
								}
								
							}
						});

						Dialog d = adb.create();
						d.show();

						d = null;
					}
				});

				return;
			}
			if(listItems.size()==0){
				IGEOListActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						if(listView!=null){
							numTitlesInList = 0;
							currentLastItem = 0;
							listView = (ListView) findViewById(R.id.listViewDataItems);
							viewConvertArrayListDataItems = new ArrayList<View>();
							listAdpArrayDataItems = new IGEODataItemListAdapter(IGEOListActivity.this,R.layout.items_list_data_items, new ArrayList<IGEOItemListDataItem>(),new ArrayList<View>());
							listView.setAdapter(listAdpArrayDataItems);

							System.gc();
						}

						tResults.setText(IGEODataManager.getActualNumResults()+" "+IGEOListActivity.this.getResources().getString(R.string.text_regists));

						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOListActivity.this);
						adb.setMessage((IGEOJSONServerReader.errorJSON ? 
								IGEOListActivity.this.getResources().getString(R.string.text_msg_error_list):
									IGEOListActivity.this.getResources().getString(R.string.text_msg_no_data_list)));
						adb.setNeutralButton((IGEODataManager.getKeywords()==null?getResources().getString(R.string.text_back):getResources().getString(R.string.text_ok)), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								if(IGEODataManager.getKeywords()==null){
									IGEODataManager.clearKeywords();
									onBackPressed();
								}
								
							}
						});

						Dialog d = adb.create();
						d.show();

						d = null;
					}
				});

				return;
			}


			if(IGEODataManager.getKeywords()!=null){
				currentLastItem = 0;
				numTitlesInList = 0;
			}

			IGEODataManager.addTemporaryDataItems(listItems);


			IGEOListActivity.this.runOnUiThread(new Runnable() {

				public void run() {

					constructListViewDataItems();

				}
			});

			loldi = null;

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}


	}






	//Pesquisa por palavras chave ################################################################################
	/**
	 * Efetua uma pesquisa com base nas palavras chave introduzidas e faz o reload da lista.
	 * @param keywords palavras pelas quais pretendemos efetuar a pesquisa
	 */
	private void search(String keywords){
		IGEODataManager.setKeywords(keywords);

		//faz com que seja possível colocar o title da categoria sempre que fazemos uma pesquisa
		currentCategoryID = null;

		progDailogLoad = ProgressDialog.show(IGEOListActivity.this,
				IGEOListActivity.this.getResources().getString(R.string.title_load_list),
				IGEOListActivity.this.getResources().getString(R.string.text_load_list));
		loldi = new LongOperationLoadDataItems();
		loldi.execute();
	}

	//############################################################################################################




















	//Criação da listView ########################################################################################
	/**
	 * Obtém a lista inicial de itens e constroi a ListView com os mesmos.
	 */
	private void constructDataItemsList(){
		progDailogLoad = ProgressDialog.show(IGEOListActivity.this, this.getResources().getString(R.string.title_load_list),
				this.getResources().getString(R.string.text_load_list));
		progDailogLoad.setCancelable(true);
		progDailogLoad.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				onBackPressed();  //o onBack já faz o cancelamento da asyncron task
			}
		});
		loldi = new LongOperationLoadDataItems();
		loldi.execute("");
	}

	//--

	String currentCategoryID = null;
	int numTitlesInList = 0;  //numero de titulos na lista
	private void constructListViewDataItems(){

		IGEOCategory cTmp = null;
		dataContentDataItems = new ArrayList<IGEOItemListDataItem>();
		int contItems = 0;
		int cont = 0;

		//hashmap que faz a correspondencia entre a posição na lista e o ID do item
		hashMapPositionDataItems = new HashMap<Integer, String>();

		//fazer o array com a informação
		for(IGEOGenericDataItem di : listItems){

			//apareceu a primeira categoria
			if(currentCategoryID==null){
				cTmp = IGEODataManager.getActualSource().categoryHashMap.get( di.categoryID );
				currentCategoryID = di.categoryID;
				dataContentDataItems.add(new IGEOItemListDataItem(cTmp.categoryName, null, true, di.categoryID));
				contItems++;
				numTitlesInList++;
				cTmp = null;
			}

			//apareceu a segunda ou posteriores categorias
			else if(!currentCategoryID.equals(di.categoryID)){
				cTmp = IGEODataManager.getActualSource().categoryHashMap.get( di.categoryID );
				currentCategoryID = di.categoryID;
				dataContentDataItems.add(new IGEOItemListDataItem(cTmp.categoryName, null, true, di.categoryID));
				contItems++;
				numTitlesInList++;
				cTmp = null;
			}


			hashMapPositionDataItems.put(contItems++, di.itemID);
			dataContentDataItems.add(new IGEOItemListDataItem(di.title, di.textOrHTML, false, null));

			cont++;

			//Isto permite que carreguemos na lista apenas o número de itens correspondente ao máximo definido.
			//Após o carregamento destes items podemos clicar no botão "Ver mais" que aparece no final da lista para carregar
			//mais itens na lista.
			if(cont>LIM_ITEMS){
				if((currentLastItem+cont+1)<listItems.size()){
					dataContentDataItems.add(new IGEOItemListDataItem(IGEOListActivity.this.getResources().getString(R.string.text_load_more), null, false, null));
					currentLastItem += cont;
					break;
				}
			}
		}





		this.listView = (ListView) findViewById(R.id.listViewDataItems);
		viewConvertArrayListDataItems = getConvertListArrayDataItems(dataContentDataItems);
		listAdpArrayDataItems = new IGEODataItemListAdapter(this,R.layout.items_list_data_items,dataContentDataItems,viewConvertArrayListDataItems);
		listView.setAdapter(listAdpArrayDataItems);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, final View arg1,int pos, long arg3){

				//se não é um ponto
				if(!hashMapPositionDataItems.containsKey(pos)){
					if(pos==currentLastItem+numTitlesInList && currentLastItem!=0){  //se é o ver mais

						//define a seleção atual, para que após carregar mais itens sejamos colocados nessa mesma posição
						actualSelection = listView.getFirstVisiblePosition();

						//carrega mais itens na lista
						loadMoreDataItems();
					}
				}

				//se é um ponto
				else {
					//seleciona o item que foi clicado
					((RelativeLayout) arg1).getChildAt(1).setBackgroundColor(Color.parseColor(IGEOConfigsManager.getListsSelectionColor()));

					//Este timmer é utilizado para que passado 200ms o iten clicado seja descelecionado, evitando que o mesmo
					//esteja selecionado quando voltamos á actividade.
					timerDesselect = new Timer();
					timerDesselect.schedule(new TimerTask() {

						public void run() {

							IGEOListActivity.this.runOnUiThread(new Runnable() {

								public void run() {
									IGEOListActivity.this.runOnUiThread(
											new Thread(new Runnable() { 
												public void run() {
													((RelativeLayout) arg1).getChildAt(1).setBackgroundColor(Color.parseColor("#99000000"));
												}
											}));
								}
							});

						}

					}, 200);

					//Lança a actividade que apresenta os detalhes do item selecionado
					Intent i = new Intent(IGEOListActivity.this, IGEODetailsActivity.class);
					Bundle b = new Bundle();
					b.putString("itemID", hashMapPositionDataItems.get(pos));
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}



			}
		});

		listView.setDivider(null);

		//altera a textview que apresenta o número de resultados obtidos.
		tResults.setText(""+IGEODataManager.getActualNumResults()+" "+this.getResources().getString(R.string.text_regists));

	}


	//--







	/**
	 * Carrega mais itens na ListView. O número de itens a adicioar é igual ao limite de itens a carregar na lista
	 * inicialmente.
	 */
	private void loadMoreDataItems(){

		int cont = 0;
		int contItems = currentLastItem+1;
		int i=currentLastItem;
		dataContentDataItems = new ArrayList<IGEOItemListDataItem>();

		boolean hadLoadMore = false; //indica se foi necessáio criar o "load more" no final da lista
		IGEOCategory cTmp = null;
		IGEOGenericDataItem di = null;


		//percorre a lista d itens e adiciona mais itens à lista
		for(i=currentLastItem;i<listItems.size();i++){
			di= listItems.get(i);

			if(!currentCategoryID.equals(di.categoryID)){  //se encontramos outra categoria
				cTmp = IGEODataManager.getActualSource().categoryHashMap.get( di.categoryID );
				currentCategoryID = di.categoryID;
				dataContentDataItems.add(new IGEOItemListDataItem(cTmp.categoryName, null, true, di.categoryID));
				contItems++;
				currentLastItem++;
				cTmp = null;
			}


			hashMapPositionDataItems.put(contItems+numTitlesInList-1, di.itemID);
			contItems++;
			dataContentDataItems.add(new IGEOItemListDataItem(di.title, di.textOrHTML, false, null));

			cont++;

			if(cont>LIM_ITEMS){
				hadLoadMore = true;

				if((currentLastItem+cont+1)<listItems.size()){
					dataContentDataItems.add(new IGEOItemListDataItem(IGEOListActivity.this.getResources().getString(R.string.text_load_more), null, false, null));
					currentLastItem += cont;
					break;
				}
			}


		}

		if(!hadLoadMore){
			currentLastItem = 0;
		}

		viewConvertArrayListDataItems = getConvertListArrayDataItems(dataContentDataItems);

		//juntar os itens anteriormente criados
		viewConvertArrayListDataItems.addAll(0,
				(((IGEODataItemListAdapter) listView.getAdapter()).convertViewArray
						.subList(0, (((IGEODataItemListAdapter) listView.getAdapter()).convertViewArray).size()-1)
						)
				);

		dataContentDataItems.addAll(0,
				(((IGEODataItemListAdapter) listView.getAdapter()).items
						.subList(0, (((IGEODataItemListAdapter) listView.getAdapter()).items).size()-1)
						)
				);

		listAdpArrayDataItems = new IGEODataItemListAdapter(this,R.layout.items_list_data_items,dataContentDataItems,viewConvertArrayListDataItems);
		listView.setAdapter(listAdpArrayDataItems);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, final View arg1,int pos, long arg3){
				//se nao é um ponto
				if(!hashMapPositionDataItems.containsKey(pos)){
					if(pos==currentLastItem+numTitlesInList && currentLastItem!=0){  //se é o ver mais

						//define a seleção atual, para que após carregar mais itens sejamos colocados nessa mesma posição
						actualSelection = listView.getFirstVisiblePosition();

						loadMoreDataItems();
					}
				}

				//se é um ponto
				else {
					((RelativeLayout) arg1).getChildAt(1).setBackgroundColor(Color.parseColor(IGEOConfigsManager.getListsSelectionColor()));

					timerDesselect = new Timer();
					timerDesselect.schedule(new TimerTask() {

						public void run() {

							IGEOListActivity.this.runOnUiThread(new Runnable() {

								public void run() {
									IGEOListActivity.this.runOnUiThread(
											new Thread(new Runnable() { 
												public void run() {
													((RelativeLayout) arg1).getChildAt(1).setBackgroundColor(Color.parseColor("#99000000"));
												}
											}));
								}
							});

						}

					}, 200);

					Intent i = new Intent(IGEOListActivity.this, IGEODetailsActivity.class);
					Bundle b = new Bundle();
					b.putString("itemID", hashMapPositionDataItems.get(pos));
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}

			}
		});

		listView.setDivider(null);

		listView.setSelection(actualSelection+1);

	}







	/**
	 * Classe utilizada para auxiliar a construção da lista.
	 * @author Bitcliq, Lda.
	 *
	 */
	class ViewHolder {
		TextView name;
		TextView description;
	}


	/**
	 * Dado uma lista com os conteúdos acolocar em cada um dos itens da lista, retorna uma lista com as view's
	 * dos items da lista.
	 * @param db Lista com os conteúdos acolocar em cada um dos itens da lista.
	 * @return Array com as view's dos itens da lista.
	 */
	private ArrayList<View> getConvertListArrayDataItems(ArrayList<IGEOItemListDataItem> db){
		ArrayList<View> viewList = new ArrayList<View>();

		for(int i=0;i<db.size();i++){
			viewList.add(getConvertViewDataItem(db.get(i),i));
		}

		return viewList;
	}




	/**
	 * Cria a view para um item da lista dado a informação que temos de colocar na mesma.
	 * @param itemListDataItem Dados a colocar no item da lista.
	 * @param index Index do item da lista que estamos a criar.
	 * @return View para um item da lista dado a informação que temos de colocar na mesma.
	 */
	public View getConvertViewDataItem(IGEOItemListDataItem itemListDataItem,int index) {
		ViewHolder holder = null;
		View convertView=null;

		LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.items_list_data_items, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.t_ItemTitle);
			holder.description = (TextView) convertView.findViewById(R.id.t_ItemDescription);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.name.setText(itemListDataItem.getName());
		holder.description.setText(itemListDataItem.getDescription());

		//se estamos na view em que mudamos de categoria, vamos adicionar um iten extra onde iremos colocar o titulo
		if(itemListDataItem.getChangeCategory()){
			holder.name.setText(itemListDataItem.getName().toUpperCase());
			((RelativeLayout) convertView).getChildAt(1).setBackgroundColor(Color.parseColor(IGEOConfigsManager.getColorForHTMLCategory(itemListDataItem.getCategoryID())));
			ListView.LayoutParams paramsView = (ListView.LayoutParams) new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, 180);
			int density = this.getResources().getDisplayMetrics().densityDpi;
			paramsView.height = IGEOUtils.getSizeToResolution(density,
					80
					);

			convertView.setLayoutParams(paramsView);

			holder.name.setTypeface(fontRegular);
			holder.name.setTextSize(20.0f);

			//adaptar o iten da lista e a textvie wdo título ao tamanho do texto que nela vamos colocar
			if(numAddedCategories==0){
				((RelativeLayout) convertView).setPadding(0, IGEOUtils.getSizeToResolution(density,-14), 0, 0);
				numAddedCategories++;
				((RelativeLayout) convertView).getChildAt(1).setPadding(0, IGEOUtils.getSizeToResolution(density,14), 0, 0);

				ListView.LayoutParams paramsView3 = (ListView.LayoutParams) new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, 180);

				int tamText = IGEOUtils.getSizeForString(holder.name.getText().toString(), holder.name);
				paramsView3.height = IGEOUtils.getSizeToResolution(density,
						(tamText<=IGEOUtils.getSizeToResolution(density,MAX_TEXT_SIZE) ? 88 : 120)
						);

				convertView.setLayoutParams(paramsView3);
			}
			else {
				((RelativeLayout) convertView).setPadding(0, 0, 0, IGEOUtils.getSizeToResolution(density,-28));
				numAddedCategories++;
				((RelativeLayout) convertView).getChildAt(1).setPadding(0, IGEOUtils.getSizeToResolution(density,28), 0, 0);
				((RelativeLayout) (((RelativeLayout) convertView).getChildAt(1))).setPadding(0, IGEOUtils.getSizeToResolution(density,14), 0, 0);

				int tamText = IGEOUtils.getSizeForString(holder.name.getText().toString(), holder.name);
				ListView.LayoutParams paramsView3 = (ListView.LayoutParams) new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, 180);
				paramsView3.height = IGEOUtils.getSizeToResolution(density,
						(tamText<=IGEOUtils.getSizeToResolution(density,MAX_TEXT_SIZE) ? 88 : 120)
						);

				convertView.setLayoutParams(paramsView3);
			}

		}
		
		//se estamos a carregar o botão "Ver mais"
		else if(itemListDataItem.getName().equals(IGEOListActivity.this.getResources().getString(R.string.text_load_more))){
			ListView.LayoutParams paramsView = (ListView.LayoutParams) new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, 180);
			int density = this.getResources().getDisplayMetrics().densityDpi;
			paramsView.height = IGEOUtils.getSizeToResolution(density,
					65
					);
			holder.name.setGravity(Gravity.CENTER);
			convertView.setLayoutParams(paramsView);

			//center horizontal
			RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) ((RelativeLayout) convertView).getChildAt(1).getLayoutParams();
			params1.topMargin = 0;
			params1.bottomMargin = 0;
			params1.topMargin = IGEOUtils.getSizeToResolution(density,
					20
					);
			((RelativeLayout) convertView).getChildAt(1).setLayoutParams(params1);

			//alterar o fundo do botão para carregaento de mais itens
			((RelativeLayout) convertView).getChildAt(1).setBackgroundResource(R.drawable.pat_roundbackground_btn_map);
		}
		
		//se estamos a colocar um item normal da lista de itens
		else {
			int density = this.getResources().getDisplayMetrics().densityDpi;

			((RelativeLayout) convertView).getChildAt(1).setPadding(0, IGEOUtils.getSizeToResolution(density,
					10
					), 0, 0);

			((RelativeLayout) convertView).getChildAt(1).setBackgroundColor(Color.parseColor("#99000000"));

			holder.name.setTypeface(fontMedium);
			holder.name.setTextSize(18.0f);
			holder.name.setTextColor(Color.WHITE);

			holder.description.setTypeface(fontRegular);
			holder.description.setTextSize(15.0f);
			holder.description.setTextColor(Color.parseColor("#CACBCF"));

		}

		return convertView;
	}

	//#######################################################################################################################
	
	
	
	
	
	
	/**
	 * Altera a imagem de fundo do ecrã desta actividade.
	 * @param url URL da imagem.
	 */
	private void changeBG(final String url) {
		System.out.println("url = "+url);
		if(!url.startsWith("/")){

			IGEOListActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					int res = IGEOListActivity.this.getResources().getIdentifier(url, "drawable", IGEOListActivity.this.getPackageName());
					rootView.setBackgroundResource(res);
				}
			});

		}
		else {
			IGEOListActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						rootView.setBackgroundDrawable(new BitmapDrawable(IGEOListActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1))));
					} else {
						rootView.setBackground(new BitmapDrawable(IGEOListActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1))));
					}

					System.gc();
				}
			});
		}
	}











	@Override
	public void onBackPressed(){
		if(loldi!=null)
			loldi.cancelLoaddataItems();

		if(!fromMap){  //se veio dalista
			IGEODataManager.clearTemporaryDataItems();
			IGEODataManager.clearKeywords();
		} else {  //se veio do mapa
			if(IGEODataManager.getActualNumResults()==0)
				IGEODataManager.clearKeywords();
		}

		super.onBackPressed();
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		try{
			Bundle b = data.getExtras();
			int x = b.getInt("backHome");
			//No caso de estarmos a fazer um back recursivo até á home
			if(x==1){
				Intent intent = new Intent();
				Bundle b2 = new Bundle();
				b2.putInt("backHome",1);
				intent.putExtras(b2);
				setResult(Activity.RESULT_OK,intent);

				intent = null;
				b = null;
				b2 = null;

				View vRoot = findViewById(R.id.RootView);
				finish();
				unbindDrawables(vRoot);
				System.gc();
			}
		}
		catch(Exception e){

		}

	}
	
	
	
	
	
	
	//CONFIGS ###############################################################################################################
	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 */
	public void applyConfigs() {

		//fundo da view de topo
		topView = (RelativeLayout) findViewById(R.id.topView);
		topView.setBackgroundColor(Color.parseColor(IGEOConfigsManager.getAppColor()));

		//fudo da textview com o número de resultados encontrado
		tResults = (TextView) findViewById(R.id.tResults);
		tResults.setBackgroundResource(IGEOListActivity.this.getResources().getIdentifier(IGEOConfigsManager.getSubtitleBackground(), "drawable", IGEOListActivity.this.getPackageName()));
		tResults.setPadding(5, 2, 5, 2);

		//background do subtitulo
		tSubtitle.setBackgroundResource(IGEOListActivity.this.getResources().getIdentifier(IGEOConfigsManager.getSubtitleBackground(), "drawable", IGEOListActivity.this.getPackageName()));
		tSubtitle.setPadding(10, 6, 10, 6);

	}
	//#######################################################################################################################







	//A abordagem utilizada para desalocar as view's, subview, imagens e outros.
	//Esta abordagem foi baseada no seguinte post:
	//http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android
	@Override
	protected void onDestroy() {

		System.gc();

		super.onDestroy();

		unbindDrawables(findViewById(R.id.RootView));
		System.gc();
	}


	/**
	 * Este método é útil no desalocamento dos bitmaps e outras views alocadas na actividade atual.
	 * @param view View principal onde queremos recursivamente desalocar as views.
	 */
	private void unbindDrawables(View view) {
		try{
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}

				if(!(view instanceof AdapterView))
					((ViewGroup) view).removeAllViews();
			}

		} catch(Throwable e){
			finish();
			super.onDestroy();
		}
	}

}
