package com.bitcliq.igeo.ui;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEOCountDownTimer;
import com.bitcliq.igeo.core.IGEODataManager;
import com.bitcliq.igeo.core.IGEOFileUtils;
import com.bitcliq.igeo.core.IGEOLocationManager;
import com.bitcliq.igeo.core.IGEOUtils;
import com.bitcliq.igeo.core.config.IGEOScreenConfig;
import com.bitcliq.igeo.core.datasource.IGEOCategory;
import com.bitcliq.igeo.core.datasource.IGEOGenericDataItem;
import com.bitcliq.igeo.core.datasource.IGEOJSONServerReader;
import com.bitcliq.igeo.ui_ordenamento.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;



/**
 * Esta actividade apresenta um mapa Google Maps, onde são colocados os pins, e polígonos quando aplicável,
 * que representam itens obtidos na consulta ao servidor.
 * @author Bitcliq, Lda.
 *
 */
public class IGEONativeMapActivity extends FragmentActivity implements IGEOConfigurableActivity {

	/**
	 * Fonte personalizada a ser utilizada.
	 */
	private static Typeface fontRegular;
	
	/**
	 * Fonte personalizada a utilizar em algumas TextView's.
	 */
	private static Typeface fontBold;

	private static Animation a = null;

	/**
	 * Zoom inicial do mapa.
	 */
	private static final int INITIAL_ZOOM = 9;

	/**
	 * Lista de itens a carregar no mapa.
	 */
	private ArrayList<IGEOGenericDataItem> listItems;

	/**
	 * HashMap que associa a cada marker um dataitem
	 */
	private HashMap<Marker,String> hashMapmarkersdataItems;

	WindowManager mWindowManager = null;
	LayoutInflater mInflater = null;


	//menu --
	/**
	 * View do menu / legendas dos pins.
	 */
	private RelativeLayout menuView = null;

	/**
	 * ListView onde são apresentados os itens da lista de legendas.
	 */
	private ListView menuList = null;

	/**
	 * Usada temporariamente para obter a lista de views de legendas.
	 */
	private ArrayList<View> viewConvertArrayListOptions = null;

	/**
	 * Lista com o conteúdo a colocar em cada uma das legendas.
	 */
	private ArrayList<IGEOItemListCategory> categoriesOptions;

	/**
	 * Adapter usado pelo menu.
	 */
	private IGEOCategoriesListAdapter listAdpArrayOptions = null;

	/**
	 * Utilizado no efeito de apresentar e esconder o menu.
	 */
	private IGEOCountDownTimer mcdt = null;

	RelativeLayout.LayoutParams params = null;

	/**
	 * Indica se o menu está ou não visível.
	 */
	private boolean menuOpen;
	//--


	/**
	 * Botão para a home
	 */
	public ImageView bHome;

	/**
	 * Botão para a lista.
	 */
	public ImageView bList;

	/**
	 * Botão para abrir o menu.
	 */
	public ImageView bMenu;

	public IGEOScreenConfig screenConfigs;

	/**
	 * View que é colocada por cima do mapa e é usada para impedir o toque no mesmo quando o menu se encontra visível.</br>
	 * Poderá de futuro vir a ser utilizada outra opção
	 */
	public RelativeLayout blockMapView;

	/**
	 * View que contém o menu.
	 */
	RelativeLayout viewMenuInt;

	/**
	 * Configurações da view do menu.
	 */
	RelativeLayout.LayoutParams paramsMenuInt;




	/**
	 * Mapa utilizado para a colocação dos pins e/ou desenho dos polígonos dos itens.
	 */
	private GoogleMap mMap;


	/**
	 * Operação que permite o carregamento dos dados de forma assíncrona.
	 */
	private LongOperationLoadDataItems loldi = null;

	/**
	 * Dialog que informa o utilizador que estamos a carregar os itens.
	 */
	private ProgressDialog progDailogLoad = null;

	/**
	 * Indica se o utilizador veio ou não da lista. Útil para saber o que fazer quando o utilizador faz back.
	 */
	private boolean fromList;
	
	/**
	 * Este timer é utilizado diversas vezes ao longo do ciclo de vida da atividade 
	 */
	private static Timer timer;
	
	/**
	 * TextView onde é colocado o nome da fonte que o utilizador está a visualizar
	 */
	private TextView tSubtitle;






	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//permite verificar se o utilizador vem da lista.
		Bundle b = this.getIntent().getExtras();
		try{
			fromList = b.getBoolean("fromList"); 
		}
		catch(Exception e){
			fromList = false;
		}

		mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		setContentView(R.layout.native_map_activity);
		setUpMapIfNeeded();


		viewMenuInt = (RelativeLayout) findViewById(R.id.viewMenuInt);
		paramsMenuInt = (RelativeLayout.LayoutParams) viewMenuInt.getLayoutParams();


		blockMapView = (RelativeLayout) findViewById(R.id.blockMapView);
		blockMapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		blockMapView.setVisibility(View.GONE);


		//Fontes
		fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");


		//obter os DataItems
		progDailogLoad = ProgressDialog.show(IGEONativeMapActivity.this, getResources().getString(R.string.title_load_map),
				getResources().getString(R.string.text_load_map));
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
		//--



		//Listners do mapa
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

			public boolean onMarkerClick(Marker marker) {

				return false;
			}

		}
				);
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			public void onInfoWindowClick(Marker marker) {

				String selectedItemID = hashMapmarkersdataItems.get(marker);
				if(selectedItemID!=null){
					Intent i = new Intent(IGEONativeMapActivity.this, IGEODetailsActivity.class);
					Bundle b = new Bundle();
					b.putString("itemID", selectedItemID);
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}

			}
		});
		mMap.setOnMapClickListener(new OnMapClickListener(){

			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		// Enabling MyLocation Layer of Google Map
		mMap.setMyLocationEnabled(true);
		//--

		constructMenu();

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

				//aqui vamos recursivamente terminar as atividades até chegar á home
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
					bHome.setImageResource(R.drawable.home_click);
				}
				else {
					bHome.setImageResource(R.drawable.home);
				}

				return false;
			}
		});

		bList = (ImageView) findViewById(R.id.bList);
		bList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(fromList){
					onBackPressed();
				}
				else {
					Intent i = new Intent(IGEONativeMapActivity.this, IGEOListActivity.class);
					Bundle b = new Bundle();
					b.putBoolean("fromMap", true);
					i.putExtras(b);
					startActivityForResult(i, 1);
				}

			}
		});
		bList.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					bList.setImageResource(R.drawable.lista_click);
				}
				else {
					bList.setImageResource(R.drawable.lista);
				}

				return false;
			}
		});





		bMenu = (ImageView) findViewById(R.id.bMenu);
		bMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(menuOpen){
					closeMenu();
					bMenu.setImageResource(R.drawable.menu_mapa);
				}
				else {
					openMenu();
					bMenu.setImageResource(R.drawable.fechar_menu_mapa);
				}

				menuOpen = !menuOpen;




			}
		});


		tSubtitle = (TextView) findViewById(R.id.t_subtitle);
		tSubtitle.setText(IGEODataManager.getActualSource().sourceName);
		tSubtitle.setTextColor(Color.WHITE);
		tSubtitle.setTypeface(fontBold);
		tSubtitle.setTextSize(12.0f);
		
		
		applyConfigs();


		menuOpen= false;


		System.gc();
		//--

	}


	/**
	 * Abre o menu com a legenda do mapa.
	 */
	private void openMenu(){

		blockMapView.setVisibility(View.VISIBLE);

		bHome.setVisibility(View.GONE);
		bList.setVisibility(View.GONE);
		tSubtitle.setVisibility(View.GONE);

		final int finalScrollX = 0;


		//Aqui estamos a criar um efeito através de um count down.
		//A cada passo, a view vai percorrer 25% da distancia que falta até estar na posição final
		//Isto permite o deslocamento da view da legenda para a posição pretendida.
		int numSteps = 22;//18;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 200);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				params = (RelativeLayout.LayoutParams) menuView.getLayoutParams();

				int step = 0;
				if (Math.abs((finalScrollX - params.rightMargin) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - params.rightMargin) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - params.rightMargin) > minStep)
				{
					if (finalScrollX - params.rightMargin > 0)
						step = minStep;
					else
						step = -minStep;
				}

				params.rightMargin = params.rightMargin+step;
				menuView.setLayoutParams(params);
			} 

			public void onFinish() {

				params.rightMargin = finalScrollX;
				menuView.setLayoutParams(params);
				menuOpen = true;

			}
		}.start();
	}



	/**
	 * Fecha o menu da legenda do mapa.
	 */
	private void closeMenu(){

		blockMapView.setVisibility(View.GONE);

		bHome.setVisibility(View.VISIBLE);
		bList.setVisibility(View.VISIBLE);
		tSubtitle.setVisibility(View.VISIBLE);

		final int finalScrollX = -paramsMenuInt.width;

		//Aqui estamos a criar um efeito através de um count down.
		//A cada passo, a view vai percorrer 25% da distancia que falta até estar na posição final
		//Isto permite o deslocamento da view da legenda para a posição pretendida.
		int numSteps = 22;//18;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 200);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				params = (RelativeLayout.LayoutParams) menuView.getLayoutParams();

				int step = 0;
				if (Math.abs((finalScrollX - params.rightMargin) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - params.rightMargin) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - params.rightMargin) > minStep)
				{
					if (finalScrollX - params.rightMargin > 0)
						step = minStep;
					else
						step = -minStep;
				}

				params.rightMargin = params.rightMargin+step;
				menuView.setLayoutParams(params);
			} 

			public void onFinish() {

				params.rightMargin = finalScrollX;
				menuView.setLayoutParams(params);

				menuOpen = false;

			}
		}.start();
	}



	
	
	
	
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);

		menuView.setVisibility(View.VISIBLE);
		menuView.bringToFront();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {

				IGEONativeMapActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						IGEONativeMapActivity.this.runOnUiThread(
								new Thread(new Runnable() { 
									public void run() {

										DisplayMetrics displaymetrics = new DisplayMetrics();
										getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
										int width = displaymetrics.widthPixels;

										RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuView.getLayoutParams();
										params.width = width;

										params.rightMargin = -paramsMenuInt.width;

										menuView.setLayoutParams(params);

										bMenu.bringToFront();
									}
								}));
					}
				});

			}

		}, 50);

		//descer o botão da localização atual
		mMap.setPadding(
				0,
				bMenu.getLayoutParams().height + IGEOUtils.getSizeToResolution(getResources().getDisplayMetrics().densityDpi, 75),
				0,
				0
				);
		//--
	}


	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}


	@Override
	public void onPause(){

		super.onPause();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {     

	    if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	    	IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}





	//Este método foi retirado de um exemplo que vem com o SDK
	//Aqui estamos a construir o mapa onde serão criados os pontos e os poligonos.
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
		}
	}













	//Obtém as sources e constroi a lista com as mesmas #####################################
	/**
	 * Nesta classe estamos a obter de forma assicrona os itens do servidor, ou do DataManager caso tenhamos vindo da lista.
	 * @author Bitcliq, Lda.
	 *
	 */
	private class LongOperationLoadDataItems extends AsyncTask<String, Void, String> {

		/**
		 * Indica se a operação foi cancelada.
		 */
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



			//Obter os pontos e colocar os pins no mapa
			ArrayList<String> categories = IGEODataManager.getActualCategoriesListActualFilterIDS();

			if(IGEODataManager.getLocalHashMapDataItems()!=null){  //se os dados já existem vamos busca-los
				listItems = new ArrayList<IGEOGenericDataItem>(IGEODataManager.getLocalHashMapDataItems().values());
			}
			else {  //se ainda não existem vamos ao servidor
				if(IGEODataManager.getLocationSearch()==null){  //se estamos a fazer uma pesquisa no perto de mim
					if(IGEODataManager.getKeywords()!=null){  //se estamos a fazer uma pesquisa com keywords
						listItems = IGEODataManager.getListForSourceAndCategoriesNearFiltered(
								IGEODataManager.getActualSource().sourceID,
								categories,
								IGEODataManager.getKeywords(),
								((IGEOApplication) IGEONativeMapActivity.this.getApplication()).getActualLocation()
								);
					}
					else {
						listItems = IGEODataManager.getListForSourceAndCategoriesNear(
								IGEODataManager.getActualSource().sourceID,
								categories,
								((IGEOApplication) IGEONativeMapActivity.this.getApplication()).getActualLocation()
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

			categories = null;


			return null;
		}      

		@Override
		protected void onPostExecute(String result) {

			IGEONativeMapActivity.this.runOnUiThread(new Runnable() {

				public void run() {

					progDailogLoad.dismiss();
					progDailogLoad = null;

				}
			});

			if(canceledLoad){

				IGEONativeMapActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						progDailogLoad.dismiss();
						progDailogLoad = null;

					}
				});

				return;
			}

			if(listItems==null){

				IGEONativeMapActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						AlertDialog.Builder adb = new AlertDialog.Builder(IGEONativeMapActivity.this);
						adb.setMessage((IGEOJSONServerReader.errorJSON ? 
								getResources().getString(R.string.text_msg_error_list):
									getResources().getString(R.string.text_msg_no_data_list)));
						adb.setNeutralButton(getResources().getString(R.string.text_back), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								onBackPressed();
							}
						});

						Dialog d = adb.create();
						d.show();

					}
				});

				return;
			}
			if(listItems.size()==0){
				IGEONativeMapActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						AlertDialog.Builder adb = new AlertDialog.Builder(IGEONativeMapActivity.this);
						adb.setMessage((IGEOJSONServerReader.errorJSON ? 
								getResources().getString(R.string.text_msg_error_list):
									getResources().getString(R.string.text_msg_no_data_list)));
						adb.setNeutralButton(getResources().getString(R.string.text_back), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								onBackPressed();
							}
						});

						Dialog d = adb.create();
						d.show();

					}
				});

				return;
			}

			hashMapmarkersdataItems = new HashMap<Marker,String>();
			Marker mTmp = null;
			if(listItems!=null){
				if(listItems.size()>0){

					Location loc = null;
					for(IGEOGenericDataItem di : listItems){

						if(di.locationCoordenates==null)
							continue;

						if(di.locationCoordenates.size()==0)
							continue;

						if(di.locationCoordenates.size()<2){  //se é um ponto

							loc = di.locationCoordenates.get(0);

							mTmp = mMap.addMarker(
									new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(di.title)
									.icon(BitmapDescriptorFactory.defaultMarker(IGEOConfigsManager.getColorForCategory(di.categoryID)))
									);

							hashMapmarkersdataItems.put(mTmp, di.itemID);

						}
						else if(!di.multiPolygon){  //se é um poligono


							loc = di.centerPoint;

							mTmp = mMap.addMarker(
									new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(di.title)
									.icon(BitmapDescriptorFactory.defaultMarker(IGEOConfigsManager.getColorForCategory(di.categoryID)))
									);

							hashMapmarkersdataItems.put(mTmp, di.itemID);


							if(!IGEOConfigsManager.nonDrawPolygonSources.contains(IGEODataManager.getActualSource().sourceID)){


								PolygonOptions rectOptions = new PolygonOptions();
								for(Location lPoint : di.locationCoordenates){
									if(lPoint.getLatitude()<0){
										double tmp = lPoint.getLatitude();
										lPoint.setLatitude(lPoint.getLongitude());
										lPoint.setLongitude(tmp);
									}

									// Instantiates a new Polygon object and adds points to define a rectangle
									rectOptions.add(new LatLng(lPoint.getLatitude(), lPoint.getLongitude()));

								}
								
								rectOptions.strokeColor(Color.parseColor(IGEOConfigsManager.getColorForHTMLCategory(di.categoryID)));
								rectOptions.fillColor(Color.parseColor(IGEOConfigsManager.getColorForHTMLCategory(di.categoryID).replace("#", "#99")));
								rectOptions.strokeWidth(3);
								
								Polygon polygon = mMap.addPolygon(rectOptions);
								polygon = null;

								//polygon.
							}


						}

						else {  //se é um multipoligono


							loc = di.centerPoint;


							if(!IGEOConfigsManager.nonDrawPolygonSources.contains(IGEODataManager.getActualSource().sourceID)){
								
								PolygonOptions rectOptions = new PolygonOptions();
								int count = 0;
								for(Location lPoint : di.locationCoordenates){

									if(lPoint.getLatitude()<0){
										double tmp = lPoint.getLatitude();
										lPoint.setLatitude(lPoint.getLongitude());
										lPoint.setLongitude(tmp);
									}

									// Instantiates a new Polygon object and adds points to define a rectangle
									rectOptions.add(new LatLng(lPoint.getLatitude(), lPoint.getLongitude()));

									if(di.lastPolygonCoordenatesList.contains(count)){

										rectOptions.strokeColor(Color.parseColor(IGEOConfigsManager.getColorForHTMLCategory(di.categoryID)));
										rectOptions.fillColor(Color.parseColor(IGEOConfigsManager.getColorForHTMLCategory(di.categoryID).replace("#", "#99")));
										rectOptions.strokeWidth(3);

										// Get back the mutable Polygon
										Polygon polygon = mMap.addPolygon(rectOptions);
										polygon = null;

										mTmp = mMap.addMarker(
												new MarkerOptions().position(new LatLng(lPoint.getLatitude(), lPoint.getLongitude())).title(di.title)
												.icon(BitmapDescriptorFactory.defaultMarker(IGEOConfigsManager.getColorForCategory(di.categoryID)))
												);

										hashMapmarkersdataItems.put(mTmp, di.itemID);

										rectOptions = new PolygonOptions();
									}


									count++;
								}
								

							}


						}






					}

					if(IGEODataManager.getLocationSearch()==null){
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(IGEOLocationManager.getActualLocation().getLatitude(),IGEOLocationManager.getActualLocation().getLongitude()),INITIAL_ZOOM));
					}
					else {
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
								((Location) listItems.get(0).locationCoordenates.get(0)).getLatitude(),
								((Location) listItems.get(0).locationCoordenates.get(0)).getLongitude()),
								INITIAL_ZOOM));
					}


				}

				//adicionar aos items temporários
				IGEODataManager.addTemporaryDataItems(listItems);


			}

			mTmp = null;
			loldi = null;

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}


	}


















	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//Log.i("DGAItems", "onactivityresult");

		try{
			Bundle b = data.getExtras();
			int x = b.getInt("backHome");
			if(x==1){

				//aqui vamos recursivamente terminar as atividades até chegar á home
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
				return;
			}
		}
		catch(Exception e){

		}

		try{
			Bundle b = data.getExtras();

			boolean changeSearch = false;

			changeSearch = b.getBoolean("changeSearch");

			mMap.clear();

			//obter os DataItems
			progDailogLoad = ProgressDialog.show(IGEONativeMapActivity.this,
					IGEONativeMapActivity.this.getResources().getString(R.string.title_load_map),
					IGEONativeMapActivity.this.getResources().getString(R.string.text_load_map));
			loldi = new LongOperationLoadDataItems();
			loldi.execute("");
			//--

		}
		catch(Exception e1){
			e1.printStackTrace();
		}

	}







	

	@Override
	public void onBackPressed(){
		if(menuOpen){
			bMenu.setImageResource(R.drawable.menu_mapa);
			closeMenu();
			return;
		}

		if(loldi!=null)
			loldi.cancelLoaddataItems();

		if(!fromList){
			IGEODataManager.clearTemporaryDataItems();
			IGEODataManager.clearKeywords();
		}

		if(hashMapmarkersdataItems!=null){
			hashMapmarkersdataItems.clear();
			hashMapmarkersdataItems = null;
		}

		if(mMap!=null)
			mMap.clear();

		super.onBackPressed();

	}









	//http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android
	@Override
	protected void onDestroy() {

		if(mMap!=null)
			mMap.clear();

		if(loldi!=null)
			loldi.cancelLoaddataItems();

		if(listItems!=null)
			listItems.clear();
		listItems = null;




		//--

		fontRegular = null;

		a = null;

		if(listItems!=null)
			listItems.clear();
		listItems = null;

		if(hashMapmarkersdataItems!=null)
			hashMapmarkersdataItems.clear();
		hashMapmarkersdataItems = null;

		mWindowManager = null;
		mInflater = null;

		//menu
		menuView = null;
		menuList = null;

		if(viewConvertArrayListOptions!=null){
			viewConvertArrayListOptions.clear();
		}
		viewConvertArrayListOptions = null;

		if(categoriesOptions!=null){
			categoriesOptions.clear();
		}
		categoriesOptions = null;

		System.gc();

		super.onDestroy();

		View v = findViewById(R.id.RootView);

		unbindDrawables(v);
		System.gc();
	}







	/**
	 * Semelhante ao método existente em outras classes, este método desaloca as view's recursivamente através da view
	 * principal da atividade.
	 * @param view View principal da atividade.
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










	//menu --
	/**
	 * Esta classe é utilizada para auxiliar a criação da lista de itens do menu.
	 * @author Bitcliq, Lda.
	 *
	 */
	class ViewHolder {
		TextView name;
		ImageView img;
		ImageView imgSelected;
	}

	
	/**
	 * Constroi a view do menu.
	 */
	private void constructMenu(){
		menuView = (RelativeLayout) findViewById(R.id.viewMenu);
		menuList = (ListView) findViewById(R.id.listViewMenu);

		categoriesOptions = new ArrayList<IGEOItemListCategory>();
		ArrayList<IGEOCategory> sortedArrayCategories = sortCategoriesByID(IGEODataManager.getActualCategoriesListActualFilter());

		for(IGEOCategory c : sortedArrayCategories){
			
			categoriesOptions.add(new IGEOItemListCategory(c.categoryID,
					IGEOConfigsManager.getPinIconForCategory(IGEODataManager.getActualSource().sourceID, c.categoryID),
					IGEOConfigsManager.getPinIconForCategory(IGEODataManager.getActualSource().sourceID, c.categoryID)));

		}

		this.menuList = (ListView) findViewById(R.id.listViewMenu);
		viewConvertArrayListOptions = getConvertListArrayCategories(categoriesOptions);
		listAdpArrayOptions = new IGEOCategoriesListAdapter(this,R.layout.items_list_categories_menu,categoriesOptions,viewConvertArrayListOptions);
		menuList.setAdapter(listAdpArrayOptions);
		menuList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){

			}
		});

		menuList.setDivider(null);

		menuView.setVisibility(View.GONE);
	}



	
	/**
	 * Utilizada para gerar a lista de itens da ListView.
	 * @param db ArrayList com o conteúdo dos itens da lista.
	 * @return Lista de View's correspondente aos itens da lista.
	 */
	private ArrayList<View> getConvertListArrayCategories(ArrayList<IGEOItemListCategory> db){
		ArrayList<View> viewList = new ArrayList<View>();

		for(int i=0;i<db.size();i++){
			viewList.add(getConvertViewCategory(db.get(i),i));
		}

		return viewList;
	}

	ViewHolder holder = null;
	
	/**
	 * Obtém a view correspondente para um item da lista.
	 * @param itemListCategory Informação de um item da lista.
	 * @param index Index do item que estamos a criar.
	 * @return View correspondente a um item da lista.
	 */
	public View getConvertViewCategory(IGEOItemListCategory itemListCategory,int index) {
		View convertView=null;

		LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.items_list_categories_menu, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.t_categoryName);
			
			String urlPin = IGEOConfigsManager.getPinIconForCategory(IGEODataManager.getActualSource().sourceID, itemListCategory.getCategoryID());

			if(itemListCategory.getUrlImg()!=null){  //se existe a imagem do pin
				holder.img = (ImageView) convertView.findViewById(R.id.i_category);
				setImageForIcon(holder.img, urlPin);
			}
			else {  //se não existe a imagem do pin colocamos uma por defeito
				urlPin = IGEOConfigsManager.getPinIconForCategory(IGEODataManager.getActualSource().sourceID, "-1");
				holder.img = (ImageView) convertView.findViewById(R.id.i_category);
				setImageForIcon(holder.img, urlPin);
			}

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.name.setText(IGEODataManager.getActualSource().categoryHashMap.get(itemListCategory.getCategoryID()).categoryName);

		holder.name.setTypeface(fontRegular);
		holder.name.setTextSize(15.0f);

		return convertView;
	}



	/**
	 * Este método permite ordenar as categorias pelo seu ID.
	 * @param categories Lista de categorias a ordenar.
	 * @return Categorias ordenadas pelo ID.
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
	//--





	/**
	 * Altera a imagem de um pin da legenda.
	 * @param icon View correspondente ao pin.
	 * @param url URL da imagem a colocar no pin.
	 */
	private void setImageForIcon(ImageView icon, String url){
		if(!url.startsWith("/")){

			int res = IGEONativeMapActivity.this.getResources().getIdentifier(url, "drawable", IGEONativeMapActivity.this.getPackageName());
			icon.setImageResource(res);

		}
		else {
			
			icon.setImageDrawable(new BitmapDrawable(IGEONativeMapActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1))));
			
			System.gc();
		}
	}





	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 */
	public void applyConfigs() {
		
		//background do subtitulo
		tSubtitle.setBackgroundResource(IGEONativeMapActivity.this.getResources().getIdentifier(IGEOConfigsManager.getSubtitleBackground(), "drawable", IGEONativeMapActivity.this.getPackageName()));
		tSubtitle.setPadding(10, 6, 10, 6);
		
	}


}

