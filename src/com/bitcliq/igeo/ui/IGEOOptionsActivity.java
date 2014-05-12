package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEODataManager;
import com.bitcliq.igeo.core.IGEOFileUtils;
import com.bitcliq.igeo.core.IGEOLocationManager;
import com.bitcliq.igeo.core.IGEOUtils;
import com.bitcliq.igeo.core.config.IGEOScreenConfig;
import com.bitcliq.igeo.core.datasource.IGEOCategory;
import com.bitcliq.igeo.ui_ordenamento.R;


/**
 * Atividade com uma lista de opções. Pode ser usada após o clique no "perto de mim" ou no "explore".
 * Na apresentação das categorias temos duas listas, cada uma delas correspondente a  uma das colunas onde são colocados ícones de categorias.
 * A opção tomada para a apresentação das categorias em duas colunas foi a da criação de duas listas. 
 * @author Bitcliq, Lda.
 *
 */

public class IGEOOptionsActivity extends Activity implements IGEOConfigurableActivity {

	/**
	 * Fonte personalizada utilizada em algumas TextView's ao longo da actividade.
	 */
	private static Typeface fontLight;
	private static Typeface fontBold;

	/**
	 * Altura de cada um dos itens.
	 */
	private static final int HEIGHT_PER_ITEM = 200;

	private ImageView bgImage;
	private ImageView bInfo;
	private ImageView btnsOptions;

	/**
	 * Botão de ida para o mapa
	 */
	public ImageView bMap;

	/**
	 * Botão de ida para a lista
	 */
	public ImageView bList;

	public TextView tBMap;
	public TextView tBList;
	public ImageView topImageView;
	public TextView tTextsBtnsOptions;

	//Estas variáveis correspondem à criação das listas e funcionam de modo
	//semelhante ao apresentado nas restantes atividades que utilizam listas.
	//O facto de existirem duas listas, deve-se ao facto de querermos apresentar uma tabela com duas colunas.
	public ListView listOptions1;
	private ArrayList<View> viewConvertArrayListOptions1 = null;
	private ArrayList<IGEOItemListCategory> dataItemsList1;
	private IGEOCategoriesListAdapter listAdpArrayOptions1 = null;

	public ListView listOptions2;
	private ArrayList<View> viewConvertArrayListOptions2 = null;
	private ArrayList<IGEOItemListCategory> dataItemsList2;
	private IGEOCategoriesListAdapter listAdpArrayOptions2 = null;
	//--

	/**
	 * View utilizada para colocar a imagem de topo.
	 */
	private RelativeLayout iHeader;

	/**
	 * Indica se o utilizador veio do ecrã "explore".
	 */
	private boolean fromExplore = false;


	//Carregamento das categorias --
	/**
	 * Operação utilizada para o carregamento das categorias de forma assíncrona.
	 */
	private LongOperationLoadCategories lolc = null;

	/**
	 * Dialog que avisa o utilizador que está a carregar as categorias.
	 */
	private ProgressDialog progDialogLoad = null;
	//--


	/**
	 * Botão de ida para a Home.
	 */
	private ImageView bHome;

	/**
	 * TextView onde é colocado o nome da fonte em que o utlizador se encontra
	 */
	private TextView tSubtitle;






	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_activity);

		try{
			//indica se o utilizador ou não do "explore"
			//isto poderá ser útil quando fizermos o back
			Bundle b = this.getIntent().getExtras();
			int x = b.getInt("fromExplore");
			if(x==1)
				fromExplore = true;
		}
		catch(Exception e){

		}


		tSubtitle = (TextView) findViewById(R.id.t_subtitle);
		tSubtitle.setText(IGEODataManager.getActualSource().sourceName);
		tSubtitle.setTextColor(Color.WHITE);
		tSubtitle.setTypeface(fontBold);
		tSubtitle.setTextSize(12.0f);


		applyConfigs();


		bMap = (ImageView) findViewById(R.id.bMap);
		bMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(IGEODataManager.getActualCategoriesListActualFilterIDS()==null){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
					adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.text_select_categories_options));
					adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}
				else if(IGEODataManager.getActualCategoriesListActualFilterIDS().size()==0){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
					adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.text_select_categories_options));
					adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}


				if(fromExplore){  //se o utilizador veio do "explore" a localização atual não é importante
					Intent i = new Intent(IGEOOptionsActivity.this, IGEONativeMapActivity.class);
					Bundle b = new Bundle();
					b.putInt("fromExplore", 1);
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}

				else {  //senão, temos de ir verificar se a localização atual está disponível

					//verifica sinal de localização
					if(IGEOLocationManager.hadLocation(IGEOOptionsActivity.this)){
						Intent i = new Intent(IGEOOptionsActivity.this, IGEONativeMapActivity.class);
						startActivityForResult(i, 1);

						i = null;
					}
					else {
						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
						adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.no_location_home));
						adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
								new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						});

						Dialog d = adb.create();
						d.show();
					}
				}
				//
			}
		});
		bMap.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bMap.setBackgroundResource(R.drawable.ord_roundbackground_btn_map_click);
					bMap.setBackgroundResource(IGEOOptionsActivity.this.getResources().getIdentifier(IGEOConfigsManager.getBtnMapClickBackground(), "drawable", IGEOOptionsActivity.this.getPackageName()));
				}
				else {
					bMap.setBackgroundResource(R.drawable.pat_roundbackground_btn_map);
				}

				return false;
			}
		});



		bList = (ImageView) findViewById(R.id.bList);
		bList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(IGEODataManager.getActualCategoriesListActualFilterIDS()==null){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
					adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.text_select_categories_options));
					adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}
				else if(IGEODataManager.getActualCategoriesListActualFilterIDS().size()==0){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
					adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.text_select_categories_options));
					adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}


				if(fromExplore){  //se o utilizador veio do "explore" a localização atual não é importante
					Intent i = new Intent(IGEOOptionsActivity.this, IGEOListActivity.class);
					Bundle b = new Bundle();
					b.putInt("fromExplore", 1);
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}

				else {  //senão, temos de ir verificar se a localização atual está disponível

					//verifica sinal de localização
					if(IGEOLocationManager.hadLocation(IGEOOptionsActivity.this)){
						Intent i = new Intent(IGEOOptionsActivity.this, IGEOListActivity.class);
						startActivityForResult(i, 1);

						i = null;
					}
					else {
						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
						adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.no_location_home));
						adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
								new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						});

						Dialog d = adb.create();
						d.show();
					}
				}
				//
			}
		});
		bList.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bList.setBackgroundResource(R.drawable.ord_roundbackground_btn_map_click);
					bList.setBackgroundResource(IGEOOptionsActivity.this.getResources().getIdentifier(IGEOConfigsManager.getBtnMapClickBackground(), "drawable", IGEOOptionsActivity.this.getPackageName()));
				}
				else {
					bList.setBackgroundResource(R.drawable.pat_roundbackground_btn_map);
				}

				return false;
			}
		});




		tBMap = (TextView) findViewById(R.id.tbMap);
		tBList = (TextView) findViewById(R.id.tbList);

		//Atribuição das fontes aos botões de ida para o mapa e para a lista
		fontLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		tBMap.setTypeface(fontLight);
		tBMap.setTextSize(15.0f);
		tBList.setTypeface(fontLight);
		tBList.setTextSize(15.0f);
		fontLight = null;
		//--
		


		bInfo = (ImageView) findViewById(R.id.bInfo);
		bInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(IGEOOptionsActivity.this, IGEOInfoActivity.class);
				startActivityForResult(i,1);
				overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

				i=null;
			}
		});


		bHome = (ImageView) findViewById(R.id.bHome);
		bHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!fromExplore){
					onBackPressed();
				}
				else {
					//Terminar as atividades até chegar à home
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


		
		if(!fromExplore){
			//inicializa a localização
			((IGEOApplication) this.getApplication()).startLocationListener();
		}


		//chamar o método que constrói a lista de categorias
		progDialogLoad = ProgressDialog.show(IGEOOptionsActivity.this,
				IGEOOptionsActivity.this.getResources().getString(R.string.title_load_categories),
				IGEOOptionsActivity.this.getResources().getString(R.string.text_load_categories));
		progDialogLoad.setCancelable(true);
		progDialogLoad.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				onBackPressed();  //o onBack já faz o cancelamento da asyncron task
			}
		});

		//Este tempo de espera permite que quando ainda não temos localização atual
		//haja tempo para que a localização seja obtida por esse listener.
		if(!IGEOLocationManager.hadLocation(this)){
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				public void run() {

					IGEOOptionsActivity.this.runOnUiThread(new Runnable() {

						public void run() {
							lolc = new LongOperationLoadCategories();
							lolc.execute("");
						}
					});

				}

			}, 3000);

			timer = null;
		}
		else {
			lolc = new LongOperationLoadCategories();
			lolc.execute("");
		}
	

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {     

		//utilizamos este método para que quando a app é colocada em background sejam gravadas as suas configurações
	    if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	    	IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());
	    }
	    
		return super.onKeyDown(keyCode, event);
	}



	//Métodos para a construção das listas ##################################################################################################

	/**
	 * Constroi os botões com base na lista de categorias e nas configurações.</div>
	 */
	public void createOptionsButtons() {
		constructCategoriesListView1();
		constructCategoriesListView2();

		int density = this.getResources().getDisplayMetrics().densityDpi;
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) listOptions1.getLayoutParams();
		params1.rightMargin = getWindowManager().getDefaultDisplay().getWidth() / 2;
		params1.height = IGEOUtils.getSizeToResolution(density, listOptions1.getCount() * HEIGHT_PER_ITEM
				);
		listOptions1.setLayoutParams(params1);

		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) listOptions2.getLayoutParams();
		params2.leftMargin = getWindowManager().getDefaultDisplay().getWidth() / 2;
		params2.height = IGEOUtils.getSizeToResolution(density, listOptions2.getCount() * HEIGHT_PER_ITEM
				);
		listOptions2.setLayoutParams(params2);

	}


	/**
	 * Classe utilizada para auxiliar na construção das listas de categorias.
	 * @author Bitcliq, Lda.
	 *
	 */
	class ViewHolder {
		TextView name;
		ImageView img;
		ImageView imgSelected;
	}


	/**
	 * Método que constrói a lista de categorias na coluna da esquerda.
	 */
	public void constructCategoriesListView1(){

		int cont = 0;
		dataItemsList1 = new ArrayList<IGEOItemListCategory>();

		ArrayList<IGEOCategory> sortedArrayCategories = sortCategoriesByID(IGEODataManager.getCategoriesListActualSource());

		for(IGEOCategory c : sortedArrayCategories){
			if(cont%2!=0){
				cont++;
				continue;
			}

			dataItemsList1.add(new IGEOItemListCategory(c.categoryID,
					IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, c.categoryID),
					IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, c.categoryID)));

			cont++;
		}

		this.listOptions1 = (ListView) findViewById(R.id.listViewCategories1);
		viewConvertArrayListOptions1 = getConvertListArrayCategories(dataItemsList1);
		listAdpArrayOptions1 = new IGEOCategoriesListAdapter(this,R.layout.items_list_categories,dataItemsList1,viewConvertArrayListOptions1);
		listOptions1.setAdapter(listAdpArrayOptions1);
		final ArrayList<IGEOCategory> sortedArrayCategories2 = (ArrayList<IGEOCategory>) sortedArrayCategories.clone();
		listOptions1.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){

				//Como o comportamento é diferente se estamos numa ou noutra lista, esta afetação permite-nos saber
				//qual a posição do item a que nos estamos a referir clicando na posição "pos" da lista. Isto é,
				//se clicarmos na posição 1, estamos a referir-nos à posição 2 da lista total de itens,
				//dado que a posição 0 se encontra na coluna da esquerda, a posição 1 na coluna da direita, e por aí adiante.
				pos*=2;

				IGEOCategory selectedCategory = sortedArrayCategories2.get(pos);

				if(IGEODataManager.getActualCategoriesListActualFilter()==null){
					try{
						String urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						//((ImageView) ((RelativeLayout) arg1).getChildAt(1)).setImageResource(R.drawable.ord_cat_default_selected);  //ALTERAR
					}

					IGEODataManager.addActualCategory(selectedCategory);
				}
				else if(IGEODataManager.isFilteringByCategory(selectedCategory)){

					try{
						String urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						((ImageView) ((RelativeLayout) arg1).getChildAt(1)).setImageResource(R.drawable.ord_cat_default);  //ALTERAR
					}

					IGEODataManager.removeActualCategory(selectedCategory.categoryID);
				}
				else {

					try{
						String urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						//((ImageView) ((RelativeLayout) arg1).getChildAt(1)).setImageResource(R.drawable.ord_cat_default_selected);  //ALTERAR
					}

					IGEODataManager.addActualCategory(selectedCategory);
				}


			}
		});

		listOptions1.setDivider(null);

	}



	/**
	 * Lista de categorias ordenada pelo seu ID.
	 */
	private ArrayList<IGEOCategory> sortedArrayCategories;
	//Semelhante ao que foi feito para a lista 1
	public void constructCategoriesListView2(){

		int cont = 0;
		dataItemsList2 = new ArrayList<IGEOItemListCategory>();

		sortedArrayCategories = sortCategoriesByID(IGEODataManager.getCategoriesListActualSource());

		for(IGEOCategory c : sortedArrayCategories){
			if(cont==0){
				cont++;
				continue;
			}
			if(cont%2==0){
				cont++;
				continue;
			}

			dataItemsList2.add(new IGEOItemListCategory(c.categoryID,
					IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, c.categoryID),
					IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, c.categoryID)));

			cont++;
		}

		this.listOptions2 = (ListView) findViewById(R.id.listViewCategories2);
		viewConvertArrayListOptions2 = getConvertListArrayCategories(dataItemsList2);
		listAdpArrayOptions2 = new IGEOCategoriesListAdapter(this,R.layout.items_list_categories,dataItemsList2,viewConvertArrayListOptions2);
		listOptions2.setAdapter(listAdpArrayOptions2);
		final ArrayList<IGEOCategory> sortedArrayCategories2 = (ArrayList<IGEOCategory>) sortedArrayCategories.clone();
		listOptions2.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){

				//semelhante ao que foi feito para a lista 1
				pos=(pos*2)+1;

				IGEOCategory selectedCategory = sortedArrayCategories2.get(pos);

				if(IGEODataManager.getActualCategoriesListActualFilter()==null){
					try{
						String urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}

					IGEODataManager.addActualCategory(selectedCategory);
				}
				else if(IGEODataManager.isFilteringByCategory(selectedCategory)){

					try{
						String urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						//e.printStackTrace();
						((ImageView) ((RelativeLayout) arg1).getChildAt(1)).setImageResource(R.drawable.ord_cat_default);
					}

					IGEODataManager.removeActualCategory(selectedCategory.categoryID);
				}
				else {

					try{
						String urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, selectedCategory.categoryID);

						if(urlIcon!=null){  //se existe imagem para a categoria

							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);

						}

						else {  //se não existe imagem para a categoria
							urlIcon = IGEOConfigsManager.getIconForCategorySelected(IGEODataManager.getActualSource().sourceID, "-1");
							setImageForIcon(((ImageView) ((RelativeLayout) arg1).getChildAt(1)), urlIcon);
						}
					}
					catch(Exception e){
						//e.printStackTrace();
					}

					IGEODataManager.addActualCategory(selectedCategory);
				}

			}
		});

		listOptions2.setDivider(null);

	}


	private ArrayList<View> getConvertListArrayCategories(ArrayList<IGEOItemListCategory> db){
		ArrayList<View> viewList = new ArrayList<View>();

		for(int i=0;i<db.size();i++){
			viewList.add(getConvertViewCategory(db.get(i),i));
		}

		return viewList;
	}

	ViewHolder holder = null;
	public View getConvertViewCategory(IGEOItemListCategory itemListCategory,int index) {
		View convertView=null;

		LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.items_list_categories, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.t_categoryName);
			holder.img = (ImageView) convertView.findViewById(R.id.i_category);

			String urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, itemListCategory.getCategoryID());

			if(urlIcon!=null){  //se existe imagem para a categoria
				setImageForIcon(holder.img, urlIcon);

			}

			else {  //se não existe imagem para a categoria
				urlIcon = IGEOConfigsManager.getIconForCategory(IGEODataManager.getActualSource().sourceID, "-1");
				setImageForIcon(holder.img, urlIcon);
			}

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.name.setText(IGEODataManager.getActualSource().categoryHashMap.get(itemListCategory.getCategoryID()).categoryName);

		int density = this.getResources().getDisplayMetrics().densityDpi;
		ListView.LayoutParams paramsView = (ListView.LayoutParams) new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, 180);
		paramsView.height = IGEOUtils.getSizeToResolution(density, 185);
		convertView.setLayoutParams(paramsView);

		return convertView;
	}

	
	

	/**
	 * Ordena as categorias pelo seu identificador. 
	 * @param categories Lista de categorias a ordenar.
	 * @return Categorias ordenadas pelo seu ID.
	 */
	public ArrayList<IGEOCategory> sortCategoriesByID(ArrayList<IGEOCategory> categories){

		for(int i=1; i <= categories.size(); i++){
			int x = i - 1;
			int y = i;

			while(y!=0 && y!=categories.size() && categories.get(x).compareID(categories.get(y).categoryID)==1){

				IGEOCategory tmp = categories.get(x);
				categories.remove(x);
				categories.add(y, tmp);
				x--;
				y--;

				tmp = null;
			}

		}

		return categories;
	}
	



	/**
	 * Altera a imagem de um pin da legenda do ecrã do mapa.
	 * @param icon View correspondente ao pin.
	 * @param url URL da imagem a colocar no pin.
	 */
	private void setImageForIcon(ImageView icon, String url){
		
		//Verifica se o nome do item a colocar se inicia por "/", isto porque, os itens iniciados por "/" são aqueles que
		//foram definidos como sendo itens contidos nos resources.
		if(!url.startsWith("/")){

			int res = IGEOOptionsActivity.this.getResources().getIdentifier(url, "drawable", IGEOOptionsActivity.this.getPackageName());
			icon.setImageResource(res);

		}
		else {
			icon.setImageDrawable(new BitmapDrawable(IGEOOptionsActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1))));

			System.gc();
		}
	}

	
	














	//Obtém as sources e constroi a lista com as mesmas #####################################
	/**
	 * Esta operação é utilizada para carregar a lista de categorias assicronamente.
	 * @author Bitcliq, Lda.
	 *
	 */
	private class LongOperationLoadCategories extends AsyncTask<String, Void, String> {

		boolean canceledLoad = false;
		boolean noLocation = false;

		public void cancelLoadCategories(){
			canceledLoad = true;
			this.cancel(true);
		}

		@Override
		protected String doInBackground(String... params) {

			if(canceledLoad){
				return null;
			}

			try{
				//verifica sinal de localização
				if(IGEOLocationManager.hadLocation(IGEOOptionsActivity.this)){

				}
				else {
					if(!fromExplore){
						IGEOOptionsActivity.this.runOnUiThread(new Runnable() {

							public void run() {
								noLocation = true;

								AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
								adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.no_location_home));
								adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
										new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										onBackPressed();
									}
								});

								Dialog d = adb.create();
								try {
									d.show();
								}
								catch(Exception e){

								}
							}
						});

						return null;
					}
				}

				//atualiza a lista de categorias na source atual
				if(!fromExplore)
					IGEODataManager.getCategoriesListActualSourceInLocation(IGEOLocationManager.getActualLocation());
				else
					IGEODataManager.getCategoriesListActualSourceInSearchLocation();

				//################################################################################################################################################################

			} catch(Throwable t){
				System.out.println("error here!!!");
				t.printStackTrace();
			}

			return null;
		}      

		@Override
		protected void onPostExecute(String result) {

			try {

				IGEOOptionsActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						progDialogLoad.dismiss();
						progDialogLoad = null;

					}
				});

				if(canceledLoad){

					IGEOOptionsActivity.this.runOnUiThread(new Runnable() {

						public void run() {

							progDialogLoad.dismiss();
							progDialogLoad = null;

						}
					});

					return;
				}

				IGEOOptionsActivity.this.runOnUiThread(new Runnable() {
					public void run() {

						try{
							//Log.i("DGACategories",""+IGEODataManager.getCategoriesListActualSource());

							if(IGEODataManager.getCategoriesListActualSource()!=null){
								if(IGEODataManager.getCategoriesListActualSource().size()!=0){
									createOptionsButtons();
								}
								else {
									if(!noLocation){
										AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
										adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.no_categories));
										adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
												new DialogInterface.OnClickListener() {

											public void onClick(DialogInterface dialog, int which) {
												onBackPressed();
											}
										});

										Dialog d = adb.create();
										d.show();
									}
								}
							}
							else {
								if(!noLocation){
									AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
									adb.setMessage(
											(!IGEOLocationManager.hadLocation(IGEOOptionsActivity.this) && noLocation)?
													IGEOOptionsActivity.this.getResources().getString(R.string.no_location_home) :
														IGEOOptionsActivity.this.getResources().getString(R.string.error_load_categories));
									adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
											new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									});

									Dialog d = adb.create();
									d.show();
								}
							}
						}
						catch(Exception e){
							e.printStackTrace();

							if(!noLocation){
								AlertDialog.Builder adb = new AlertDialog.Builder(IGEOOptionsActivity.this);
								adb.setMessage(IGEOOptionsActivity.this.getResources().getString(R.string.error_load_categories));
								adb.setNeutralButton(IGEOOptionsActivity.this.getResources().getString(R.string.text_back),
										new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										onBackPressed();
									}
								});

								Dialog d = adb.create();
								d.show();
							}
						}

					}
				});

			} catch(Exception generalException){

			}

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}


	}























	//Métodos da atividade ###################################################################################################################################

	@Override
	public void onStop(){
		super.onStop();
	}


	@Override
	public void onPause(){
		super.onPause();
	}


	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//Log.i("DGAItems", "onactivityresult");

		super.onActivityResult(requestCode, resultCode, data);

		try{
			Bundle b = data.getExtras();
			int x = b.getInt("backHome");
			if(x==1){

				if(fromExplore){
					//limpar as sources atuais
					if(IGEODataManager.localHashMapCategories!=null){
						IGEODataManager.localHashMapCategories.clear();
					}
					if(IGEODataManager.getCategoriesListActualSource()!=null){
						IGEODataManager.clearCategoriesListActualSource();
					}
					
					//aqui vamos recursivamente terminar as atividades até chegar á home
					Intent intent = new Intent();
					Bundle b2 = new Bundle();
					b2.putInt("backHome",1);
					intent.putExtras(b2);
					setResult(Activity.RESULT_OK,intent);

					intent = null;
					b2 = null;
				}

				View vRoot = findViewById(R.id.RootView);
				finish();
				unbindDrawables(vRoot);
				System.gc();
			}
		}
		catch(Exception e){

		}

	}




	@Override
	public void onBackPressed(){

		if(lolc!=null)
			lolc.cancelLoadCategories();

		//limpar as sources atuais
		if(IGEODataManager.localHashMapCategories!=null){
			IGEODataManager.localHashMapCategories.clear();
		}
		if(IGEODataManager.getCategoriesListActualSource()!=null){
			IGEODataManager.clearCategoriesListActualSource();
		}

		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
	}
	//################################################################################################################
	
	
	
	
	
	
	
	
	
	
	//CONFIGS ##################################################################################################################
	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 */
	public void applyConfigs() {

		//header
		iHeader = (RelativeLayout) findViewById(R.id.topView);
		int res = IGEOOptionsActivity.this.getResources().getIdentifier(IGEOConfigsManager.getTopImage(), "drawable", IGEOOptionsActivity.this.getPackageName());
		iHeader.setBackgroundResource(res);

		//background do subtitulo
		tSubtitle.setBackgroundResource(IGEOOptionsActivity.this.getResources().getIdentifier(IGEOConfigsManager.getSubtitleBackground(), "drawable", IGEOOptionsActivity.this.getPackageName()));
		tSubtitle.setPadding(10, 6, 10, 6);

	}
	//#########################################################################################################################
	
	
	
	
	
	




	//A abordagem utilizada para desalocar as view's, subview, imagens e outros.
	//Esta abordagem foi baseada no seguinte post:
	//http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android
	@Override
	protected void onDestroy() {

		System.gc();

		super.onDestroy();

		View v = findViewById(R.id.RootView);

		unbindDrawables(v);
		System.gc();
	}


	/**
	 * Utilizado para desalocar todas as views icluidas na view principal.
	 * @param view View principal.
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
	//--

}
