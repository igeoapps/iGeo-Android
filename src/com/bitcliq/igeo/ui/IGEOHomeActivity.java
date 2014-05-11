package com.bitcliq.igeo.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bitcliq.igeo.core.datasource.IGEOSource;
import com.bitcliq.igeo.ui.places.IGEOGenericSearchListAdapter;
import com.bitcliq.igeo.ui.places.IGEOGenericSearchListItem;
import com.bitcliq.igeo.ui.places.IGEOPlaces;
import com.bitcliq.igeo.ui_patrimonio.R;
import com.bitcliq.igeo.views.IGEOHorizontalScrollView;


/**
 * Actividade inicial que contém as principais opções da App.</br>
 * Esta atividade agrupa três ecrãs devido ao efeito que se pretende. Teremos o ecrã com os botões da home,
 * à esquerda na scrollView teremos a lista de categorias e à direita teremos a lista de itens. A transição entre os ecrãs
 * será feita pelo deslocamento de uma scrollview programaticamente no clique de cada um dos três botões do ecrã inicial.
 * Futuramente o modo como o efeito é feito poderá ser alterado.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOHomeActivity extends Activity implements IGEOConfigurableActivity {

	/**
	 * URL dos ícones da home.
	 */
	private int homeIcons[];

	/**
	 * URL dos ícones da home selecionados.
	 */
	private int homeIconsClicked[];

	//Fontes
	private static Typeface fontRegular;
	private static Typeface fontLight;
	private static Typeface fontBlack;
	private static Typeface fontBold;





	//VIEWS DO PRIMEIRO ECRÃ - HOME ###########################################################################
	/**
	 * View principal da home.
	 */
	public RelativeLayout homeView;

	/**
	 * Título da home.
	 */
	public TextView homeTTitle;

	/**
	 * Botão de ida para a página "About".
	 */
	public ImageView homeBInfo;

	/**
	 * Subtítulo onde é apresentada a fonte quando selecionada.
	 */
	public TextView homeTSubtitle;

	/**
	 * Botão para a funcionalidade "perto de mim"
	 */
	public ImageView homeBNearMe;

	/**
	 * Texto do botão "perto de mim"
	 */
	public TextView homeTTitleNearMe;

	/**
	 * Botão para a funcionalidade "explore"
	 */
	public ImageView homeBExplore;

	/**
	 * Texto do botão do explore.
	 */
	public TextView homeTTitleExplore;

	/**
	 * Botão para a lista de fontes.
	 */
	public ImageView homeBListSources;

	/**
	 * texto do botão para a lista de fontes.
	 */
	public TextView homeTTitleListSources;

	/**
	 * View de topo que irá ter como fundo a imagem de topo.
	 */
	public ImageView homeTopImageView;

	/**
	 * TextView com um segundo Subtítulo da App.
	 */
	public TextView homeTOpenGeographicData;

	/**
	 * Esta variável é utilizada para prevenir um duplo clique na lista.
	 */
	private boolean touchBlocked;

	//###############################################################################################












	//VIEWS PARA O ECRÃ DA DIREITA - Lista de Fontes ############################################################

	/**
	 * View principal da lista de fontes.
	 */
	public RelativeLayout sourcesView;

	/**
	 * ListView utilizado para a lista de fontes.
	 */
	public ListView listListView;

	/**
	 * Indica qual o item que se encontra selecionado na lista de fontes.
	 */
	private int posSelectedItem;

	/**
	 * Imagem de topo onde vamos colocar a imagem de topo.
	 */
	private RelativeLayout iHeader;

	/**
	 * Botão para voltar á home.
	 */
	private ImageView bHome;

	/**
	 * LongOperation para obter a lista de sources
	 */
	private LongOperationLoadSources lols = null;

	/**
	 * Dialog que avisa o utilizador de que estamos a carergar as fontes quando este clica no botão para ir para
	 * a lista de sources e as fontes ainda não foram obtidas.
	 */
	private ProgressDialog progDailogLoad = null;

	/**
	 * Indica se houve erro na obtensão das sources
	 */
	private boolean errorInSources = false;


	//Variáveis para a construção da lista de sources - igual ao que temos em outras actividades
	private ArrayList<View> viewConvertArrayListSources = null;
	private ArrayList<IGEOItemListSource> descricoesBrevesSources;
	private IGEOSourcesListAdapter listAdpArraySources = null;
	private boolean clickedBSources = false;
	//--

	/**
	 * Indica se as fontes já existem ou não.
	 */
	private boolean sourcesExists = false;

	//###############################################################################################









	//VIEWS PARA O ECRÃ DA DIREITA - Seleção de localização no epxlore ##############################

	/**
	 * Fonte principal da view
	 */
	private RelativeLayout searchView;

	/**
	 * Botão de pesquisa.
	 */
	private ImageView homeBSearch;

	/**
	 * texto do botão de pesquisa.
	 */
	private TextView tButtonSearch;



	//INFORMAÇÃO RELATIVA Á FILTRAGEM --------------------------

	//Listas para o distrito concelho e freguesia,
	//Semelhante ás listas criadas em outras atividades.
	ListView l_district, l_council, l_parish;

	//Botões para aceder a cada uma das listas
	RelativeLayout b_district, b_council, b_parish;

	/**
	 * Variável onde serão colocados os districts, conselhos e parishes.
	 * É a esta variável que vamos obter os concelhos de um distrito, as freguesias de um concelho, entre outros.
	 */
	private IGEOPlaces l;

	//Arrays com  os códigos dos distritos, concelhos e freguesias, usados temporáriamente.
	private String[] codDistricts, districtNames;
	private String[] codCouncils, councilNames;
	private String[] codParishes, parishNames;
	private String[][] resultDist;

	//variáveis onde vamos guardar
	/**
	 * Código do distrito selecionado
	 */
	private String codDistrict="";

	/**
	 * Código do concelho selecionado.
	 */
	private String codCouncil="";

	/**
	 * Código da freguesia selecionada.
	 */
	private String codParish="";


	//Adapter das listas - semelhantes ao de outras atividades
	ArrayAdapter adapterDist, adapterConc, adapterPar;


	//Usado para colocar os items das listas
	ArrayList<View> viewConvertArrayListDistricts = null;
	ArrayList<View> viewConvertArrayListCouncils = null;
	ArrayList<View> viewConvertArrayListParishes = null;

	//Texto com os valores atuais de cada lista
	TextView t_district;
	TextView t_council;
	TextView t_parish;

	//View que preenche o ecrã e apresenta a lista
	RelativeLayout rl_DistrictsList;
	RelativeLayout rl_CouncilsList;
	RelativeLayout rl_ParishesList;

	//ArrayList que contém a informação a colocar em cada item da lista
	ArrayList<IGEOGenericSearchListItem> dataItemsListDistricts;
	ArrayList<IGEOGenericSearchListItem> dataItemsListCouncils;
	ArrayList<IGEOGenericSearchListItem> dataItemsListParishes;

	//Adapter das listas
	IGEOGenericSearchListAdapter listAdpArrayDistricts = null;
	IGEOGenericSearchListAdapter listAdpArrayCouncils = null;
	IGEOGenericSearchListAdapter listAdpArrayParishes = null;
	
	private static final String SELECT = "<Selecionar>";


	//Handler usado para quando obtivemos os distritos concelhos e freguesias
	/**
	 * Define a mensagem a ser recebida em caso de sucesso quando a thread que lê os locais
	 * termina a sua tarefa.
	 */
	public static final int MSG_OK = 0;

	/**
	 * Handler utilizado para receber a mensagem de que a obtenção de locais para filtragem terminou.</ br>
	 * A obtenção dos locais é feita através de uma thread que é lançada quando iniciamos a actividade. Assim,
	 * este handler é chamado quando essa thread termina a sua execução, enviando para este handler uma mensagem indicando
	 * se correu bem a obtenção dos locais.
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_OK:
				getPlaces();
				break;
			}
		}
	};

	//-------------------------------------------------------------------------------


	//--






	//Splash
	/**
	 * View que contém o splashscreen apresentado no inicio da App.
	 */
	private RelativeLayout splashScreen;

	/**
	 * View que permite o scroll horizontal fazendo o efeito de deslize quando clicamos nos botões
	 * "Lista de Fontes" e "Explore".</br>
	 * Este efeito poderá posteriormente ser alterado para funcionar de uma outra forma.
	 */
	private IGEOHorizontalScrollView scrollScreen;

	/**
	 * View que se encontra dentro da ScrollView e que contém as views dos 3 ecrãs.
	 */
	private LinearLayout scrollViewContainer;

	/**
	 * View principal. A instanciação desta variável permite posteriormente desalocar todas as view's contidas nesta atividade.
	 */
	public RelativeLayout RootView;

	/**
	 * Cont-down utilizado para o efeito de deslize do scroll.
	 */
	private IGEOCountDownTimer mcdt = null;


	/**
	 * Indica qual o ecrã em que estamos atualmente.
	 */
	private int actualScreen = HOME;

	/**
	 * Ecrã principal.
	 */
	private static final int HOME = 0;

	/**
	 * Ecrã com a lista de fontes.
	 */
	private static final int SOURCE_LIST = 1;

	/**
	 * Ecrã da filtragem do local no explore.
	 */
	private static final int EXPLORE = 2;

	/**
	 * Lista de distritos.</br>
	 * Embora não possamos considerar esta view propriamente um ecrã, quando ela se encontra na frente
	 * e fazemos back pretendemos que esta seja escondida, assim, tem um comportamento semelhante a um ecrã.
	 */
	private static final int DISTRICTS_LIST = 3;

	/**
	 * Lista de concelhos.</br>
	 * Embora não possamos considerar esta view propriamente um ecrã, quando ela se encontra na frente
	 * e fazemos back pretendemos que esta seja escondida, assim, tem um comportamento semelhante a um ecrã.
	 */
	private static final int COUNCILS_LIST = 4;

	/**
	 * Lista de freguesias.</br>
	 * Embora não possamos considerar esta view propriamente um ecrã, quando ela se encontra na frente
	 * e fazemos back pretendemos que esta seja escondida, assim, tem um comportamento semelhante a um ecrã.
	 */
	private static final int PARISHES_LIST = 5;

	/**
	 * Indica se no momento atual estamos a correr alguma animação. Isto é importante para que
	 * não iniciemos uma outra animação antes da atual ter sido terminada.
	 */
	private boolean isInAnimation = false;











	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);

		//Load dos locais para a pesquisa no explore
		//Vamos lançar uma thread e após isso enviar para o handler uma mensagem indicando que o carregamento
		//foi bem sucedido.
		//Para o carregamento dos locais vamos ler o ficheiro palaces e através da classe IGEOPlaces construir uma estrutura
		//que é utilizada para fazer consultas aos mesmos.
		new Thread(new Runnable() {
			public void run() {
				InputStream is=getResources().openRawResource(R.raw.places4);
				l = new IGEOPlaces(is);

				handler.sendEmptyMessage(MSG_OK);
			}
		}).start();
		//

		splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
		splashScreen.setVisibility(View.VISIBLE);
		splashScreen.bringToFront();
		splashScreen.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		//deixamos as views serem criadas e após 4 segundos, desativamos o splashcreen e movemos o scroll
		//para a view do meio, ou seja, a Home.
		Timer timerHideSplash = new Timer();
		timerHideSplash.schedule(new TimerTask() {

			public void run() {

				IGEOHomeActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						splashScreen.setVisibility(View.GONE);
						//colocar o scroll no ecrã da home
						scrollScreen.scrollBy(getWindowManager().getDefaultDisplay().getWidth(), 0);

					}
				});

			}

		}, 4000);





		//Atribuição das fontes ás textview's. Aqui instanciamos ainda as fontes.
		//Os ficheiros das fontes encontram-se na subpasta fonts da pasta assets.
		//Estamos ainda aqui a atribuir uma sombra a algumas das textview's para aumentar a sua visibilidade
		//independentemente da imagem de fundo.
		fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		fontLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		fontBlack = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
		homeTTitle = (TextView) findViewById(R.id.t_titulo);
		homeTTitle.setTypeface(fontBlack);
		homeTTitle.setTextSize(38.0f);

		homeTTitle.setShadowLayer(5.0f, 2.0f, 2.0f, Color.BLACK);

		homeTSubtitle = (TextView) findViewById(R.id.t_dados);
		homeTSubtitle.setTypeface(fontRegular);
		homeTSubtitle.setTextSize(12.0f);

		homeTSubtitle.setShadowLayer(5.0f, 2.0f, 2.0f, Color.BLACK);

		homeTTitleNearMe = (TextView) findViewById(R.id.tNearMe);
		homeTTitleNearMe.setTypeface(fontLight);
		homeTTitleNearMe.setTextSize(15.0f);

		homeTTitleExplore = (TextView) findViewById(R.id.tExplore);
		homeTTitleExplore.setTypeface(fontLight);
		homeTTitleExplore.setTextSize(15.0f);

		homeTTitleListSources = (TextView) findViewById(R.id.t_list_sources);
		homeTTitleListSources.setTypeface(fontLight);
		homeTTitleListSources.setTextSize(15.0f);

		homeTSubtitle = (TextView) findViewById(R.id.t_subtitle);
		homeTSubtitle.setText("");
		homeTSubtitle.setTextColor(Color.WHITE);
		homeTSubtitle.setTypeface(fontBold);
		homeTSubtitle.setTextSize(12.0f);

		tButtonSearch = (TextView) findViewById(R.id.tButtonSearch);
		tButtonSearch.setTypeface(fontLight);
		tButtonSearch.setTextSize(15.0f);


		fontRegular = null;
		fontLight = null;
		fontBlack = null;

		System.gc();

		//--




		//Inicialização do scrollview
		scrollScreen = (IGEOHorizontalScrollView) findViewById(R.id.DGAHorizontalScrollView1);
		//listner de paragem do scroll
		//não foi usado mas pode vir a ser usado de futuro
		scrollScreen.setOnScrollStopListner(new IGEOHorizontalScrollView.onScrollStopedListner() {
			public void onScrollStoped() {

			}
		});

		//listner de toque na galeria
		scrollScreen.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				return true;

			}
		});
		scrollScreen.setHorizontalScrollBarEnabled(true);

		//faz com que não seja possível mover o scroll sem ser através do click nos botões
		//de futuro poderemos pensar numa solução em que dada a direção do movimento no caso de
		//termos movido o dedo no ecrã, iremos para a lista de fontes ou para a filtragem do explore.
		scrollScreen.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		scrollViewContainer = (LinearLayout) findViewById(R.id.scrollViewContainer);

		//vamos buscar as views dos ecrãs a adicionar
		homeView = (RelativeLayout) findViewById(R.id.view_home);
		sourcesView = (RelativeLayout) findViewById(R.id.view_sources_list);
		searchView = (RelativeLayout) findViewById(R.id.view_explore_search);

		
		//Esta foi a abordagem utilizada dado que a colocação inicial das view's lado a lado no scroll
		//Não estava a permitir que estas fossem desenhadas inicialmente sendo utilizadas sem problemas, e estavamos também a
		//ter problemas ao adicionas as view's á scrollview.
		//Desta forma as view's são desenhadas, e são adicionadas dinâmicamente
		//vamos buscar a mainView para remover de la os ecras
		RootView = (RelativeLayout) findViewById(R.id.RootView);
		RootView.removeView(homeView);
		RootView.removeView(sourcesView);
		RootView.removeView(searchView);

		//vamos adicionar os ecrãs á scrollView
		scrollViewContainer.addView(sourcesView);
		LinearLayout.LayoutParams paramsSources = (LinearLayout.LayoutParams) sourcesView.getLayoutParams();
		paramsSources.width = getWindowManager().getDefaultDisplay().getWidth();
		sourcesView.setLayoutParams(paramsSources);

		scrollViewContainer.addView(homeView);
		LinearLayout.LayoutParams paramsHome = (LinearLayout.LayoutParams) homeView.getLayoutParams();
		paramsHome.width = getWindowManager().getDefaultDisplay().getWidth();
		homeView.setLayoutParams(paramsHome);

		scrollViewContainer.addView(searchView);
		LinearLayout.LayoutParams paramsSearch = (LinearLayout.LayoutParams) searchView.getLayoutParams();
		paramsSearch.width = getWindowManager().getDefaultDisplay().getWidth();
		searchView.setLayoutParams(paramsHome);

		//desabilitar o scroll
		scrollScreen.setHorizontalScrollBarEnabled(false);



		homeBInfo = (ImageView) findViewById(R.id.bInfo);
		homeBInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(IGEOHomeActivity.this, IGEOInfoActivity.class);
				startActivityForResult(i,1);
				//as animações aqui utilizadas estão definidas em res/anim
				overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
				
				i = null;
			}
		});
		homeBInfo.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					homeBInfo.setImageResource(R.drawable.info_click);
				}
				else {
					homeBInfo.setImageResource(R.drawable.info);
				}

				return false;
			}
		});


		homeBListSources = (ImageView) findViewById(R.id.b_list_sources);
		homeBListSources.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickedBSources = true;
				gotoSourcesList();
				if(errorInSources || !sourcesExists){
					progDailogLoad = ProgressDialog.show(IGEOHomeActivity.this,
							IGEOHomeActivity.this.getResources().getString(R.string.title_load_home),
							IGEOHomeActivity.this.getResources().getString(R.string.text_load_home));
					progDailogLoad.setCancelable(true);
					progDailogLoad.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							onBackPressed();  //o onBack já faz o cancelamento da asyncron task
						}
					});
					constructSourcesList();
				}
			}
		});
		homeBListSources.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					homeBListSources.setImageResource(homeIconsClicked[0]);
				}
				else {
					homeBListSources.setImageResource(homeIcons[0]);
				}

				return false;
			}
		});





		homeBExplore = (ImageView) findViewById(R.id.b_explore);
		homeBExplore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(IGEODataManager.getActualSource()==null){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_source_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}

				gotoExplore();
			}
		});
		homeBExplore.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					homeBExplore.setImageResource(homeIconsClicked[2]);
				}
				else {
					homeBExplore.setImageResource(homeIcons[2]);
				}

				return false;
			}
		});

		homeBNearMe = (ImageView) findViewById(R.id.b_near_me);
		homeBNearMe.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(IGEODataManager.getActualSource()==null){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_source_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();

					return;
				}


				if(!IGEOLocationManager.isGPSActivated()){
					//verifica sinal de localização
					if(IGEOLocationManager.hadLocation(IGEOHomeActivity.this)){
						Intent i = new Intent(IGEOHomeActivity.this, IGEOOptionsActivity.class);
						startActivityForResult(i, 1);
						overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

						i = null;
					}
					else {
						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
						adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.no_location_home));
						adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
								new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						});

						Dialog d = adb.create();
						d.show();
					}
				}
				else {
					Intent i = new Intent(IGEOHomeActivity.this, IGEOOptionsActivity.class);
					startActivityForResult(i, 1);
					overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

					i = null;
				}


			}
		});
		homeBNearMe.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					homeBNearMe.setImageResource(homeIconsClicked[1]);
				}
				else {
					homeBNearMe.setImageResource(homeIcons[1]);
				}

				return false;
			}
		});


		bHome = (ImageView) findViewById(R.id.bHome);
		bHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backSourcesList();
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




		//inicializa as listas de distritos, concelhos e freguesias
		constructExploreSearch();

		//vai obter as sources e constroi a lista com as mesmas
		constructSourcesList();

		//verifica se a internet estão ligados e se não pede a sua ativaçao ao utilizador
		IGEOUtils.servicesActivation(this);

		//aplicar as configurações
		try {
			applyConfigs();
		}
		catch(Exception e){

		}

	}


























	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 * Atenção, estamos a supor que as imagens se encontram nos resources da App. De futuro iremos implementar
	 * este método por forma a que seja possível obter as imagens através de ficheiros locais ou remotos.
	 */
	public void applyConfigs() {

		//icones dos botões da home não clicados
		int res;
		homeBListSources = (ImageView) findViewById(R.id.b_list_sources);
		String[] homeIconsStr = IGEOConfigsManager.getHomeIcons();
		res = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsStr[0], "drawable", IGEOHomeActivity.this.getPackageName());
		homeBListSources.setImageResource(res);
		homeIcons = new int[3];
		homeIcons[0] = res;

		homeBNearMe = (ImageView) findViewById(R.id.b_near_me);
		res = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsStr[1], "drawable", IGEOHomeActivity.this.getPackageName());
		homeBNearMe.setImageResource(res);
		homeIcons[1] = res;

		homeBExplore = (ImageView) findViewById(R.id.b_explore);
		res = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsStr[2], "drawable", IGEOHomeActivity.this.getPackageName());
		homeBExplore.setImageResource(res);
		homeIcons[2] = res;

		//header
		iHeader = (RelativeLayout) findViewById(R.id.topView);
		res = IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getTopImage(), "drawable", IGEOHomeActivity.this.getPackageName());
		iHeader.setBackgroundResource(res);

		//icones dos botões da home clicados
		String[] homeIconsClickedStr = IGEOConfigsManager.getHomeIconsClicked();
		homeIconsClicked = new int[3];
		homeIconsClicked[0] = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsClickedStr[0], "drawable", IGEOHomeActivity.this.getPackageName());
		homeIconsClicked[1] = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsClickedStr[1], "drawable", IGEOHomeActivity.this.getPackageName());
		homeIconsClicked[2] = IGEOHomeActivity.this.getResources().getIdentifier(homeIconsClickedStr[2], "drawable", IGEOHomeActivity.this.getPackageName());


		//IMAGENS SERVIDOR - ALTERADO
		final String urlBGImage = IGEOConfigsManager.getBackgroundImageForSource("-1");
		changeBG(urlBGImage);
		//--


		//background do subtitulo
		homeTSubtitle.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getSubtitleBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
		homeTSubtitle.setPadding(10, 6, 10, 6);

		//background dos botões de seleção do local no explore
		b_district.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
		b_council.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
		b_parish.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));

		homeTTitle.setText(IGEOConfigsManager.getAppTitle());
	}











	//Animações de passagem entre ecrãs ###############################################
	//Este tipo de animações utilizado consiste em deslizar a scrollview que contém as views
	//dos três ecrãs, para um dos lados, percorrendo a distância em x igual á largura do ecrã.
	//Esse deslize, é feito, percorrendo a cada passo da animação 25% do total que falta percorrer,
	//dando assim um efeito de animação em que inicialmente a velocidade é maior e depois vai diminuindo
	//progressivamente.
	/**
	 * Desliza o ecrã de forma a ficarmos no ecrã da lista de fontes.
	 */
	private void gotoSourcesList(){

		//se estamos a correr uma animação, não vamos correr uma outra
		if(isInAnimation)
			return;

		isInAnimation = true;

		final int finalScrollX = 0;

		int numSteps = 20;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 150);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				int step = 0;
				if (Math.abs((finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - scrollScreen.getScrollX()) > minStep)
				{
					if (finalScrollX - scrollScreen.getScrollX() > 0)
						step = minStep;
					else
						step = -minStep;
				}

				scrollScreen.scrollBy(step, 0);
			} 

			public void onFinish() {

				//if(!this.canceled){
				int lastScrollX = finalScrollX - scrollScreen.getScrollX();
				scrollScreen.scrollBy(lastScrollX, 0);	

				actualScreen = SOURCE_LIST;
				isInAnimation = false;
				clickedBSources = false;
				bHome.setVisibility(View.VISIBLE);
				//}
			}
		}.start();

	}


	/**
	 * Volta da lista de fontes para a Home.
	 */
	private void backSourcesList(){

		if(isInAnimation)
			return;

		bHome.setVisibility(View.GONE);

		isInAnimation = true;

		final int finalScrollX = getWindowManager().getDefaultDisplay().getWidth();

		int numSteps = 20;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 150);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				int step = 0;
				if (Math.abs((finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - scrollScreen.getScrollX()) > minStep)
				{
					if (finalScrollX - scrollScreen.getScrollX() > 0)
						step = minStep;
					else
						step = -minStep;
				}

				scrollScreen.scrollBy(step, 0);
			} 

			public void onFinish() {

				int lastScrollX = finalScrollX - scrollScreen.getScrollX();
				scrollScreen.scrollBy(lastScrollX, 0);	
				actualScreen = HOME;
				isInAnimation = false;

			}
		}.start();

	}




	/**
	 * Move o ecrã até ao ecrã da filtragem da funcionalidade "explore".
	 */
	private void gotoExplore(){

		if(isInAnimation)
			return;

		isInAnimation = true;

		final int finalScrollX = getWindowManager().getDefaultDisplay().getWidth() * 2;

		int numSteps = 20;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 150);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				int step = 0;
				if (Math.abs((finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - scrollScreen.getScrollX()) > minStep)
				{
					if (finalScrollX - scrollScreen.getScrollX() > 0)
						step = minStep;
					else
						step = -minStep;
				}

				scrollScreen.scrollBy(step, 0);
			} 

			public void onFinish() {

				int lastScrollX = finalScrollX - scrollScreen.getScrollX();
				scrollScreen.scrollBy(lastScrollX, 0);	

				actualScreen = EXPLORE;
				isInAnimation = false;
				bHome.setVisibility(View.VISIBLE);

			}
		}.start();

	}


	/**
	 * Volta do Explore para a Home.
	 */
	private void backExplore(){

		if(isInAnimation)
			return;

		bHome.setVisibility(View.GONE);

		isInAnimation = true;

		final int finalScrollX = getWindowManager().getDefaultDisplay().getWidth();

		int numSteps = 20;
		int timePerStep = 40;
		final int stepPercent = 25;
		final int minStep = Math.max(1, getWindowManager().getDefaultDisplay().getWidth() / 150);

		mcdt = (IGEOCountDownTimer) new IGEOCountDownTimer(numSteps * timePerStep, timePerStep) { 

			public void onTick(long millisUntilFinished) {
				int step = 0;
				if (Math.abs((finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100) > minStep)
				{
					step = (finalScrollX - scrollScreen.getScrollX()) * stepPercent / 100;
				}
				else if (Math.abs(finalScrollX - scrollScreen.getScrollX()) > minStep)
				{
					if (finalScrollX - scrollScreen.getScrollX() > 0)
						step = minStep;
					else
						step = -minStep;
				}

				scrollScreen.scrollBy(step, 0);
			} 

			public void onFinish() {

				int lastScrollX = finalScrollX - scrollScreen.getScrollX();
				scrollScreen.scrollBy(lastScrollX, 0);	
				actualScreen = HOME;
				isInAnimation = false;

			}
		}.start();

	}
	
	//--
















	//Construção das listas da pesquisa do Explore ############################################
	/**
	 * Constroi as listas e outros elementos necessários á filtragem do explore.
	 */
	private void constructExploreSearch(){

		t_district = (TextView) findViewById(R.id.t_distrito);
		t_district.setText(SELECT);
		t_council = (TextView) findViewById(R.id.t_conselho);
		t_council.setText(SELECT);
		t_parish = (TextView) findViewById(R.id.t_freguesia);
		t_parish.setText(SELECT);

		//layout das listas
		//impede que ao clicarmos numa posição sem itens estejamos a passar o touch para a view que temos abaixo
		rl_DistrictsList = (RelativeLayout) findViewById(R.id.rl_listaDistritos);
		rl_DistrictsList.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		rl_CouncilsList = (RelativeLayout) findViewById(R.id.rl_listaConselhos);
		rl_CouncilsList.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		rl_ParishesList = (RelativeLayout) findViewById(R.id.rl_listaFreguesias);
		rl_ParishesList.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		//listas
		l_district = (ListView) findViewById(R.id.listaDistritos);
		l_council = (ListView) findViewById(R.id.listaConselhos);
		l_parish = (ListView) findViewById(R.id.listaFreguesias);

		//conteúdo a colocar nos itens das listas
		dataItemsListDistricts = new ArrayList<IGEOGenericSearchListItem>();
		dataItemsListCouncils = new ArrayList<IGEOGenericSearchListItem>();
		dataItemsListParishes = new ArrayList<IGEOGenericSearchListItem>();

		//lista de districts
		viewConvertArrayListDistricts = getConvertListArray(dataItemsListDistricts);
		listAdpArrayDistricts = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListDistricts,viewConvertArrayListDistricts);
		l_district.setAdapter(listAdpArrayDistricts);
		l_district.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){

				rl_DistrictsList.setVisibility(View.GONE);

			}
		});

		//lista de conselhos
		viewConvertArrayListCouncils = getConvertListArray(dataItemsListCouncils);
		listAdpArrayCouncils = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListCouncils,viewConvertArrayListCouncils);
		l_council.setAdapter(listAdpArrayCouncils);
		l_council.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){
				if(pos!=0){
					TextView t_name = (TextView) ((RelativeLayout) arg1).getChildAt(0);
					String name = t_name.getText().toString();
					t_council.setText(name);
				} else {
					t_council.setText("<Selecionar>");
					codCouncil = null;
					codParish = null;
					IGEODataManager.clearConc();
					IGEODataManager.clearPar();
				}
				changeParishes(pos);

				rl_CouncilsList.setVisibility(View.GONE);

			}
		});

		//lista de parishes
		viewConvertArrayListParishes = getConvertListArray(dataItemsListParishes);
		listAdpArrayParishes = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListParishes,viewConvertArrayListParishes);
		l_parish.setAdapter(listAdpArrayParishes);
		l_parish.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){
				if(pos!=0){
					TextView t_nome = (TextView) ((RelativeLayout) arg1).getChildAt(0);
					String nome = t_nome.getText().toString();
					t_parish.setText(nome);
				} else {
					codParish = null;
					IGEODataManager.clearPar();
					t_parish.setText("<Selecionar>");
				}

				rl_ParishesList.setVisibility(View.GONE);

			}
		});

		//botão que abre a lista de distritos
		b_district = (RelativeLayout) findViewById(R.id.b_distrito);
		b_district.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				rl_DistrictsList.setVisibility(View.VISIBLE);
				rl_DistrictsList.bringToFront();

				actualScreen = DISTRICTS_LIST;
			}
		});
		b_district.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					b_district.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectClickBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}
				else {
					b_district.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}

				return false;
			}
		});

		//botão que abre a lista de concelhos
		b_council = (RelativeLayout) findViewById(R.id.b_conselho);
		b_council.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(t_district.getText().toString().equals(SELECT)){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_district_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();
				} else {
					rl_CouncilsList.setVisibility(View.VISIBLE);
					rl_CouncilsList.bringToFront();
					actualScreen = COUNCILS_LIST;
				}

			}
		});
		b_council.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					b_council.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectClickBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}
				else {
					b_council.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}

				return false;
			}
		});

		//botão que abre a lista de freguesias
		b_parish = (RelativeLayout) findViewById(R.id.b_freguesia);
		b_parish.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(t_district.getText().toString().equals(SELECT)){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_district_and_council_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();
				} else if(t_council.getText().toString().equals("<Selecionar>")){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_council_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();
				} else {
					rl_ParishesList.setVisibility(View.VISIBLE);
					rl_ParishesList.bringToFront();
					actualScreen = PARISHES_LIST;
				}

			}
		});
		b_parish.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					b_parish.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectClickBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}
				else {
					b_parish.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getLocationSelectBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}

				return false;
			}
		});



		homeBSearch = (ImageView) findViewById(R.id.buttonSearch);
		homeBSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//Verifica se foram pelo menos selecionados um distrito e um conselho
				if(t_council.getText().toString().equals("<Selecionar>")){
					AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
					adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.select_district_and_council_home));
					adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});

					Dialog d = adb.create();
					d.show();
				}
				else {
					//Ir para o ecrã com os resultados
					//Guardar o local
					IGEODataManager.setLocationSearch(codDistrict, codCouncil, codParish);
					Intent i = new Intent(IGEOHomeActivity.this, IGEOOptionsActivity.class);
					Bundle b = new Bundle();
					b.putInt("fromExplore", 1);
					i.putExtras(b);
					startActivityForResult(i, 1);

					i = null;
					b = null;
				}
			}
		});
		homeBSearch.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if(event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=3){
					homeBSearch.setBackgroundResource(IGEOHomeActivity.this.getResources().getIdentifier(IGEOConfigsManager.getBtnMapClickBackground(), "drawable", IGEOHomeActivity.this.getPackageName()));
				}
				else {
					homeBSearch.setBackgroundResource(R.drawable.pat_roundbackground_btn_map);
				}

				return false;
			}
		});

	}





	/**
	 * Obtém os distritos concelhos e freguesias, e constroi as listas e outras view's necessárias.
	 */
	private void getPlaces(){
		resultDist = l.getDistrictsArrays();
		codDistricts=resultDist[0];
		districtNames=resultDist[1];
		for(String s : districtNames){
			dataItemsListDistricts.add(new IGEOGenericSearchListItem(s));
		}
		viewConvertArrayListDistricts = getConvertListArray(dataItemsListDistricts);
		listAdpArrayDistricts = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListDistricts,viewConvertArrayListDistricts);
		l_district.setAdapter(listAdpArrayDistricts);
		l_district.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){
				if(pos!=0){
					TextView t_nome = (TextView) ((RelativeLayout) arg1).getChildAt(0);
					String nome = t_nome.getText().toString();
					t_district.setText(nome);
				} else {
					t_district.setText(SELECT);
					codDistrict = null;
					codCouncil = null;
					codParish = null;
					IGEODataManager.clearLocationSearch();
				}

				t_council.setText(SELECT);
				t_parish.setText(SELECT);
				clearCouncilsList();
				clearparishesList();
				changeCouncils(pos);

				rl_DistrictsList.setVisibility(View.GONE);

			}
		});

	}



	/**
	 * Altera o código do distrito atualmente selecionado e gera a lista de concelhos para esse distrito.
	 * @param pos Posição da lista de concelhos em que o utilizador clicou.
	 */
	private void changeCouncils(int pos){
		codDistrict=codDistricts[pos];

		String[][] result = new String[2][];

		if(pos==0){
			t_parish.setText(SELECT);
			clearparishesList();
			codCouncils=new String[] {new String("")};
			councilNames=new String[] {new String("")};
		} else {
			result=l.getCouncilsArraysForDistrict(codDistrict);
			codCouncils=result[0];
			councilNames=result[1];
		}

		//clear da lista de parishes
		t_parish.setText(SELECT);
		clearparishesList();

		//adicionar os conselhos á lista
		for(String s : councilNames){
			dataItemsListCouncils.add(new IGEOGenericSearchListItem(s));
		}
		viewConvertArrayListCouncils = getConvertListArray(dataItemsListCouncils);
		listAdpArrayCouncils = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListCouncils,viewConvertArrayListCouncils);
		l_council.setAdapter(listAdpArrayCouncils);
		l_council.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){
				if(pos!=0){
					TextView t_nome = (TextView) ((RelativeLayout) arg1).getChildAt(0);
					String nome = t_nome.getText().toString();
					t_council.setText(nome);
				} else {
					t_council.setText(SELECT);
					codCouncil = null;
					codParish = null;
					IGEODataManager.clearConc();
					IGEODataManager.clearPar();
				}

				t_parish.setText(SELECT);
				changeParishes(pos);
				
				rl_CouncilsList.setVisibility(View.GONE);

			}
		});

	}


	/**
	 * Altera o código do concelho atualmente selecionado e gera a lista de freguesias para esse concelho.
	 * @param pos Posição da lista de concelhos em que o utilizador clicou.
	 */
	private void changeParishes(int pos){
		codCouncil=codCouncils[pos];

		String[][] result = new String[2][];

		if(pos==0){
			codParishes=new String[] {new String("")};
			parishNames=new String[] {new String("")};
		} else {
			result=l.getParishesArraysForCouncil(codDistrict,codCouncil);
			codParishes=result[0];
			parishNames=result[1];
		}

		//clear da lista de freguesias
		clearparishesList();

		//adicionar as freguesias á lista
		for(String s : parishNames){
			dataItemsListParishes.add(new IGEOGenericSearchListItem(s));
		}
		viewConvertArrayListParishes = getConvertListArray(dataItemsListParishes);
		listAdpArrayParishes = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListParishes,viewConvertArrayListParishes);
		l_parish.setAdapter(listAdpArrayParishes);
		l_parish.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos2, long arg3){
				if(pos2!=0){
					TextView t_nome = (TextView) ((RelativeLayout) arg1).getChildAt(0);
					String nome = t_nome.getText().toString();
					t_parish.setText(nome);
					codParish=codParishes[pos2];
				} else {
					IGEODataManager.clearPar();
					codParish = null;
					t_parish.setText("<Selecionar>");
				}

				rl_ParishesList.setVisibility(View.GONE);

			}
		});

	}




	/**
	 * Limpa a lista de concelhos. Isto é utilizado quando selecionamos um distrito diferente do atual.
	 */
	private void clearCouncilsList(){
		dataItemsListCouncils.clear();
		viewConvertArrayListCouncils = getConvertListArray(dataItemsListCouncils);
		listAdpArrayCouncils = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListCouncils,viewConvertArrayListCouncils);
		l_council.setAdapter(listAdpArrayCouncils);
		l_council.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){


			}
		});
	}


	/**
	 * Limpa a lista de freguesias. Isto é utilizado quando selecionamos uma freguiesia diferente da atual.
	 */
	private void clearparishesList(){
		dataItemsListParishes.clear();
		viewConvertArrayListParishes = getConvertListArray(dataItemsListParishes);
		listAdpArrayParishes = new IGEOGenericSearchListAdapter(this,R.layout.items_list_search,dataItemsListParishes,viewConvertArrayListParishes);
		l_parish.setAdapter(listAdpArrayParishes);
		l_parish.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){


			}
		});
	}



	
	
	
	

	/**
	 * Classe utilizada para auxiliar a construção das listas utilizadas nas pesquisas.
	 * @author Bitcliq, Lda.
	 *
	 */
	class ViewHolder {
		TextView title;
		TextView subTitle;
	}

	//Método semelhante a outros usados na construção de listas nas várias actividades
	private ArrayList<View> getConvertListArray(ArrayList<IGEOGenericSearchListItem> db){
		ArrayList<View> viewList = new ArrayList<View>();

		for(int i=0;i<db.size();i++){
			viewList.add(getConvertView(db.get(i),i));
		}

		return viewList;
	}

	//Método semelhante a outros usados na construção de listas nas várias actividades
	public View getConvertView(IGEOGenericSearchListItem itemListaGenericPesquisa,int index) {
		ViewHolder holder = null;
		View convertView=null;

		LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.items_list_search, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.t_nome);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if(index==0)
			holder.title.setText("<Selecionar>");
		else
			holder.title.setText(itemListaGenericPesquisa.getTitle());

		return convertView;
	}
	//#######################################################################################



























	//Obtém as sources e constroi a lista com as mesmas #####################################
	/**
	 * Operação que permite o carregamento e contrução da lista de fontes de forma assicrona.
	 * @author Bitcliq, Lda.
	 *
	 */
	private class LongOperationLoadSources extends AsyncTask<String, Void, String> {

		boolean canceledLoad = false;

		public void cancelLoadSources(){
			canceledLoad = true;
			this.cancel(true);
		}

		@Override
		protected String doInBackground(String... params) {

			if(canceledLoad){
				return null;
			}

			try{
				Collection<IGEOSource> sourceList = IGEODataManager.getSourcesList();
				if(sourceList!=null){
					IGEOConfigsManager.getAppConfigs().setSourcesList(sourceList);

					//construir a lista
					if(IGEOConfigsManager.getAppConfigs().getSourcesHashMap()!=null){

						IGEOHomeActivity.this.runOnUiThread(new Runnable() {

							public void run() {

								constructSourcesListView();
								sourcesExists = true;

							}
						});

						errorInSources = false;
					}
					else {
						errorInSources = true;
						if(actualScreen==SOURCE_LIST){

							IGEOHomeActivity.this.runOnUiThread(new Runnable() {

								public void run() {
									AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
									adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.text_error_sources_home));
									adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
											new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int which) {
											backSourcesList();
										}
									});

									Dialog d = adb.create();
									d.show();
								}
							});

						}
					}




				}
				else {
					//show error
					errorInSources = true;
					if(clickedBSources){
						clickedBSources = false;
						IGEOHomeActivity.this.runOnUiThread(new Runnable() {

							public void run() {
								AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
								adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.text_error_sources_home));
								adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
										new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										backSourcesList();
									}
								});

								Dialog d = adb.create();
								d.show();
							}
						});
					}
				}
			} catch(Throwable t){
				//show error
				t.printStackTrace();
				errorInSources = true;
			}

			return null;
		}      

		@Override
		protected void onPostExecute(String result) {

			if(progDailogLoad!=null){
				IGEOHomeActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						progDailogLoad.dismiss();
						progDailogLoad = null;

					}
				});
			}

			if(canceledLoad){

				IGEOHomeActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						if(progDailogLoad!=null){
							progDailogLoad.dismiss();
							progDailogLoad = null;
						}

					}
				});

				return;
			}


			if(IGEOConfigsManager.getAppConfigs().getSourcesHashMap()==null && actualScreen==SOURCE_LIST){

				IGEOHomeActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
						adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.text_error_sources_home));
						adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
								new DialogInterface.OnClickListener() {

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

			try {
				if(IGEOConfigsManager.getAppConfigs().getSourcesHashMap().size()==0 && actualScreen==SOURCE_LIST){
					IGEOHomeActivity.this.runOnUiThread(new Runnable() {

						public void run() {

							AlertDialog.Builder adb = new AlertDialog.Builder(IGEOHomeActivity.this);
							adb.setMessage(IGEOHomeActivity.this.getResources().getString(R.string.text_error_sources_home));
							adb.setNeutralButton(IGEOHomeActivity.this.getResources().getString(R.string.text_ok),
									new DialogInterface.OnClickListener() {

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

			} catch(Exception e){

			}

			lols = null;

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}


	}



	/**
	 * Chama a operação para obtenção da lista de fontes e construção de ListView correspondente.
	 */
	private void constructSourcesList(){
		lols = new LongOperationLoadSources();
		lols.execute("");
	}



	/**
	 * Constroi a ListView com as fontes.
	 */
	public void constructSourcesListView(){

		try {
			posSelectedItem = -1;

			descricoesBrevesSources = new ArrayList<IGEOItemListSource>();
			final ArrayList<IGEOSource> sortedArraySources = sortSourcesByID(new ArrayList<IGEOSource>(IGEOConfigsManager.getAppConfigs().getSourcesHashMap().values()));

			for(IGEOSource s : sortedArraySources){
				descricoesBrevesSources.add(new IGEOItemListSource(s.sourceName, s.srcSubTitle));
			}

			this.listListView = (ListView) findViewById(R.id.listViewSources);
			viewConvertArrayListSources = getConvertListArraySources(descricoesBrevesSources);
			listAdpArraySources = new IGEOSourcesListAdapter(this,R.layout.items_list_sources,descricoesBrevesSources,viewConvertArrayListSources);
			listListView.setAdapter(listAdpArraySources);
			touchBlocked = false;
			listListView.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> a, View arg1,int pos, long arg3){

					if(touchBlocked)
						return;

					try {
						//previne que estejamos a clicar numa fonte de dados enquanto decorre a anmação de passagem entre ecrãs
						if(isInAnimation) {
							touchBlocked = false;
							return;
						}

						//limpar dados
						IGEODataManager.clearLocationSearch();
						IGEODataManager.clearTemporaryDataItems();
						IGEODataManager.clearCurrentFilterCategories();

						IGEOSource clickedSource = sortedArraySources.get(pos);
						if(IGEODataManager.getActualSource()!=null){

							//se estamos a desselecionar a  fonte atual
							if(clickedSource.sourceID.equals(IGEODataManager.getActualSource().sourceID)){
								IGEODataManager.setActualSource(null);
								final String urlBGImage = IGEOConfigsManager.getBackgroundImageForSource("-1");

								//IMAGENS SERVIDOR
								changeBG(urlBGImage);
								//--

								homeTSubtitle.setVisibility(View.GONE);
								((RelativeLayout) arg1).getChildAt(0).setBackgroundColor(Color.parseColor("#77000000"));

								((RelativeLayout) arg1).getChildAt(0).requestLayout();

								posSelectedItem = -1;
							}


							//se estamos a selecionar uma fonte de dados diferente da atual
							else {
								IGEODataManager.clearActualSource();

								clickedSource = sortedArraySources.get(pos);

								final String urlBGImage = IGEOConfigsManager.getBackgroundImageForSource(clickedSource.sourceID);

								if(urlBGImage!=null){  //Se existe imagem para essa source

									//IMAGENS SERVIDOR
									changeBG(urlBGImage);
									//--
								}
								else {  //Se não existe imagem para essa source
									if(IGEODataManager.getActualSource()!=null){  //Se estamos com uma source selecionada

										//IMAGENS SERVIDOR
										changeBG(IGEOConfigsManager.getBackgroundImageForSource("-1"));
										//--

									}
								}

								//colocar no datamanager a fonte atual
								IGEODataManager.setActualSource(clickedSource);
								//alterar o subtitulo da home
								homeTSubtitle.setText(clickedSource.sourceName);
								homeTSubtitle.setVisibility(View.VISIBLE);

								((RelativeLayout) arg1).getChildAt(0).setBackgroundColor(Color.parseColor(IGEOConfigsManager.getListsSelectionColor()));
								((RelativeLayout) arg1).getChildAt(0).requestLayout();

								(((RelativeLayout) listListView.getChildAt(posSelectedItem)).getChildAt(0)).setBackgroundColor(Color.parseColor("#77000000"));
								(((RelativeLayout) listListView.getChildAt(posSelectedItem)).getChildAt(0)).requestLayout();

								posSelectedItem = pos;
							}

						}



						//se ainda não tinha sido selecionada nenhuma fonte de dados
						else {
							clickedSource = sortedArraySources.get(pos);

							final String urlBGImage = IGEOConfigsManager.getBackgroundImageForSource(clickedSource.sourceID);

							if(urlBGImage!=null){  //Se existe imagem para essa source

								//IMAGENS SERVIDOR
								changeBG(urlBGImage);
								//--

							}
							else {  //Se não existe imagem para essa source
								if(IGEODataManager.getActualSource()!=null){  //Se estamos com uma source selecionada

									//IMAGENS SERVIDOR
									changeBG(urlBGImage);
									//--

								}
							}

							//colocar no datamanager a fonte atual
							IGEODataManager.setActualSource(clickedSource);
							//alterar o subtitulo da home
							homeTSubtitle.setText(clickedSource.sourceName);
							homeTSubtitle.setVisibility(View.VISIBLE);
							((RelativeLayout) arg1).getChildAt(0).setBackgroundColor(Color.parseColor(IGEOConfigsManager.getListsSelectionColor()));

							((RelativeLayout) arg1).getChildAt(0).requestLayout();

							posSelectedItem = pos;

						}

						backSourcesList();

					} catch(Throwable e){  //apanha a excepção do click na lista
						IGEODataManager.setActualSource(null);
						final String urlBGImage = IGEOConfigsManager.getBackgroundImageForSource("-1");

						//IMAGENS SERVIDOR - ALTERADO
						changeBG(urlBGImage);
						//--

						homeTSubtitle.setVisibility(View.GONE);
						((RelativeLayout) arg1).getChildAt(0).setBackgroundColor(Color.parseColor("#77000000"));

						((RelativeLayout) arg1).getChildAt(0).requestLayout();

						posSelectedItem = -1;

						e.printStackTrace();
					}

					touchBlocked = false;
				}
			});

		}
		catch(Exception generalException){
			generalException.printStackTrace();
		}
		finally {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				public void run() {

					IGEOConfigsManager.readDefaultConfigs();

				}

			}, 1000);

			timer = null;
		}
	}



	//semelhante ao método utilizado na construção de outras ListView's.
	private ArrayList<View> getConvertListArraySources(ArrayList<IGEOItemListSource> db){
		ArrayList<View> viewList = new ArrayList<View>();

		for(int i=0;i<db.size();i++){
			viewList.add(getConvertViewSource(db.get(i),i));
		}

		return viewList;
	}

	//semelhante ao método utilizado na construção de outras ListView's.
	public View getConvertViewSource(IGEOItemListSource itemListSource,int index) {
		ViewHolder holder = null;
		View convertView=null;

		LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.items_list_sources, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.t_sourceName);
			holder.subTitle = (TextView) convertView.findViewById(R.id.t_sourceSubtitle);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.title.setText(itemListSource.getName().toUpperCase());
		holder.title.setTypeface(fontLight);
		holder.title.setTextSize(18.0f);

		if(itemListSource.getSubTitle()==null){
			holder.subTitle.setVisibility(View.GONE);
		}
		else if(itemListSource.getSubTitle().equals("")){
			holder.subTitle.setVisibility(View.GONE);
		}
		else {
			holder.subTitle.setText(itemListSource.getSubTitle());
			holder.subTitle.setTypeface(fontLight);
			holder.subTitle.setTextSize(15.0f);
		}

		holder = null;

		return convertView;
	}




	/**
	 * Ordena as fonte pelo seu ID.</ br>
	 * Isto é util dado que pretendemos apresentar a lista de fontes ordenada pelo sei ID.
	 * @param sources Lista de fontes a ordenar.
	 * @return Lista de fontes ordenadas pelo ID.
	 */
	public ArrayList<IGEOSource> sortSourcesByID(ArrayList<IGEOSource> sources){

		for(int i=1; i <= sources.size(); i++){
			int x = i - 1;
			int y = i;

			while(y!=0 && y!=sources.size() && sources.get(x).compareID(sources.get(y).sourceID)==1){

				IGEOSource tmp = sources.get(x);
				sources.remove(x);
				sources.add(y, tmp);
				x--;
				y--;

				tmp = null;
			}

		}

		return sources;
	}
	//#######################################################################################











	/**
	 * Altera a imagem de fundo do ecrã desta actividade.
	 * @param url URL da imagem.
	 */
	private void changeBG(final String url) {

		System.out.println("url imagem = "+url);
		if(!url.startsWith("/")){

			IGEOHomeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					int res = IGEOHomeActivity.this.getResources().getIdentifier(url, "drawable", IGEOHomeActivity.this.getPackageName());
					scrollViewContainer.setBackgroundResource(res);
				}
			});

		}
		else {
			IGEOHomeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					
					//Vamos utilizar um ou outro método dependendo de se estamos num dispositivo com Android 4.1 ou num
					//dispositivo com uma versão do Android anterior a essa versão.
					if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						BitmapDrawable bd = new BitmapDrawable(IGEOHomeActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1)));
						scrollViewContainer.setBackgroundDrawable(bd);
					} else {
						scrollViewContainer.setBackground(new BitmapDrawable(IGEOHomeActivity.this.getResources(), IGEOFileUtils.getBitmapFromAppFolder(url.substring(1))));
					}

					System.gc();
				}
			});
		}
	}






















	//Métodos próprios da Activity #########################################################

	@Override
	public void onBackPressed(){

		if(isInAnimation)
			return;

		if(actualScreen==HOME){
			if(lols!=null)
				lols.cancelLoadSources();

			IGEODataManager.clearActualSource();
			IGEODataManager.clearTemporaryDataItems();

			IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());

			super.onBackPressed();
		}
		else if(actualScreen==SOURCE_LIST){
			backSourcesList();
		}
		else if(actualScreen==EXPLORE){
			backExplore();
			IGEODataManager.clearLocationSearch();
		}
		else if(actualScreen==DISTRICTS_LIST){
			rl_DistrictsList.setVisibility(View.GONE);
			actualScreen = EXPLORE;
		}
		else if(actualScreen==COUNCILS_LIST){
			rl_CouncilsList.setVisibility(View.GONE);
			actualScreen = EXPLORE;
		}
		else if(actualScreen==PARISHES_LIST){
			rl_ParishesList.setVisibility(View.GONE);
			actualScreen = EXPLORE;
		}
		else {
			IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());

			super.onBackPressed();
		}
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


	@Override
	public void onResume(){
		super.onResume();

		//definir o raio nas settings da app
		IGEOConfigsManager.getAppConfigs().setProximityRadius(IGEOConfigsManager.getAutoDetectionPOISRadius(this));
	}


	//Cria o menu com a opção de ida para as definições onde podemos alterar o raio de pesquisa.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.igeo_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		//vamos aqui lançar a atividade das definições onde podemos alterar o raio de pesquisa.
		case R.id.settings:
			Intent i = new Intent(IGEOHomeActivity.this, IGEOMenuConfigs.class);
			startActivityForResult(i,1);
			overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

			i = null;
			break;
		}
		return true;
	}







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







	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		try{
			//indica se
			Bundle b = data.getExtras();
			int x = b.getInt("backHome");
			//se viemos recursivamente de outra actividade até á home coloca o scroll posicionado de forma
			//a que fiquemos no ecrã da home. Isto previne o caso em que viemos da escolha de categorias, para a qual
			//fomos através do explore, e clicamos no botão da home.
			if(x==1){
				actualScreen = HOME;
				int lastScrollX = - getWindowManager().getDefaultDisplay().getWidth();
				scrollScreen.scrollBy(lastScrollX, 0);	
				actualScreen = HOME;
				isInAnimation = false;
				bHome.setVisibility(View.GONE);
			}

			b = null;
		}
		catch(Exception e){
			//e.printStackTrace();
		}

	}

}
