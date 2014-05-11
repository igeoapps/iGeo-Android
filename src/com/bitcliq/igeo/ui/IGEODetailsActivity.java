package com.bitcliq.igeo.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEODataManager;
import com.bitcliq.igeo.core.IGEOFileUtils;
import com.bitcliq.igeo.core.datasource.IGEOGenericDataItem;
import com.bitcliq.igeo.ui_patrimonio.R;

/**
 * Atividade que apresenta um item que contém uma imagem, um titulo e outras
 * informações e um texto de descrição ou pares atributo-valor.
 * A imagem é obtida através de um link e colocada numa ImageView, As strings relativas à fonte, categoria
 * e título do item são carregadas em TextView's, e a restante informação é carregada utilizando HTML, e CSS's
 * que se encontram na pasta assets, o qual contém uma lista de pares atributo-valor.
 * 
 * @author Bitcliq, Lda.
 * 
 */

public class IGEODetailsActivity extends Activity implements
		IGEOConfigurableActivity {

	/**
	 * Diretoria base para carregamento da WebView. Isto permite ir à pasta
	 * dos assets obter as css's.
	 */
	private static final String BASE_FOLDER = "file:///android_asset/";

	/**
	 * Delay utilizado para o carregamento das views na actividade.
	 */
	private static final int TIME_TO_LOAD = 100;

	// DADOS --
	/**
	 * Identificador da categoria do item.
	 */
	public String categoryID;

	/**
	 * Identificador do item.
	 */
	private String itemID;

	/**
	 * Guarda o Item atual.
	 */
	public IGEOGenericDataItem dataItem;
	// --

	// VIEWS --
	/**
	 * TextView onde será colocado o título do item.
	 */
	public TextView tTitle;

	/**
	 * TextView onde será colocado o nome da fonte do item.
	 */
	public TextView tSource;

	/**
	 * TextView onde será colocado o name da categoria do item.
	 */
	public TextView tCategory;

	/**
	 * ImageView onde será colocada a imagem do item.
	 */
	public ImageView itemImageView;

	/**
	 * WebView onde é apresentado a descrição do item.
	 */
	public WebView itemTextWebView;

	/**
	 * Botão para voltar à Home.
	 */
	private ImageView bHome;
	// --

	// OUTROS --
	/**
	 * Fonte personalizada em bold a aplicar em algumas TextView na atividade.
	 */
	private Typeface fontBold;

	/**
	 * Fonte personalizada normal a aplicar em algumas TextView na atividade.
	 */
	private Typeface fontRegular;

	/**
	 * Operação usada para a obtenção de dados do servidor e carregamento do item de
	 * forma assíncrona.
	 */
	private LongOperationLoadDataItem loldi = null;

	/**
	 * ProgressDialig que mostra ao utilizador a informação de que se está a
	 * carregar o item.
	 */
	private ProgressDialog progDialogLoad = null;
	
	private static Drawable tmpImage = null;
	
	private static double screenWidth = -1;

	// --

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Ao verificarmos que esta atividade demorava algum tempo a carregar
		 * devido ao carregamento de várias views, decidimos fazer
		 * esse carregamento após o início da atividade no método onPostCreate e utilizar um timer.
		 */

	}

	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Carrega as views ao fim de 100ms
		Timer timerHideSplash = new Timer();
		timerHideSplash.schedule(new TimerTask() {

			public void run() {

				IGEODetailsActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						setContentView(R.layout.details_activity);

						Bundle b = IGEODetailsActivity.this.getIntent()
								.getExtras();
						try {
							itemID = b.getString("itemID");
						} catch (Exception e) {
							e.printStackTrace();
						}

						bHome = (ImageView) findViewById(R.id.bHome);
						bHome.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								// limpar dados
								IGEODataManager.clearKeywords();
								IGEODataManager.clearLocationSearch();
								IGEODataManager.clearTemporaryDataItems();
								IGEODataManager.localHashMapCategories.clear();

								// aqui vamos terminar as
								// atividades até chegar à home
								// Foi utilizada esta abordagem para que a limpeza dos dados
								// das atividades anteriores àquela em que o utilizador se encontra seja simples, evitando problemas de gestão de
								// memória, problemas na gestão da stack de navegação, entre outros.
								Intent intent = new Intent();
								Bundle b2 = new Bundle();
								b2.putInt("backHome", 1);
								intent.putExtras(b2);
								setResult(Activity.RESULT_OK, intent);

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

								if (event.getAction() != MotionEvent.ACTION_UP
										&& event.getAction() != 3) {
									bHome.setImageResource(R.drawable.home_click);
								} else {
									bHome.setImageResource(R.drawable.home);
								}

								return false;
							}
						});

						progDialogLoad = ProgressDialog
								.show(IGEODetailsActivity.this,
										IGEODetailsActivity.this
												.getResources()
												.getString(
														R.string.details_title_load),
										IGEODetailsActivity.this
												.getResources()
												.getString(
														R.string.details_text_load));
						progDialogLoad.setCancelable(true);
						progDialogLoad
								.setOnCancelListener(new DialogInterface.OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										// TODO Auto-generated method stub
										onBackPressed(); // chamamos oo
															// onBackPressed
															// porque este já
															// faz o
															// cancelamento da
															// asyncron task
									}
								});
						loldi = new LongOperationLoadDataItem();
						loldi.execute("");

						tTitle = (TextView) findViewById(R.id.tTitle);

						tSource = (TextView) findViewById(R.id.tSource);

						tCategory = (TextView) findViewById(R.id.tCategory);

						itemImageView = (ImageView) findViewById(R.id.imgItem);

						itemTextWebView = (WebView) findViewById(R.id.webViewDescription);

						applyConfigs();

					}
				});

			}

		}, TIME_TO_LOAD);
	}

	@Override
	public void onBackPressed() {

		//cancela a tarefa de carregamento dos dados do item
		if (loldi != null)
			loldi.cancelLoaddataItem();

		itemTextWebView = null;

		super.onBackPressed();

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {     

		//utilizamos este método para que quando a app vai para background sejam gravadas as suas configurações
	    if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	    	IGEOConfigsManager.writeDefaultConfigs(this.getApplicationContext());
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	

	// Obtém os detalhes do item
	// #####################################
	private class LongOperationLoadDataItem extends
			AsyncTask<String, Void, String> {

		boolean canceledLoad = false;

		public void cancelLoaddataItem() {
			canceledLoad = true;
			this.cancel(true);
		}

		@Override
		protected String doInBackground(String... params) {

			if (canceledLoad) {
				return null;
			}

			dataItem = IGEODataManager.getDataItemForID(itemID);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			IGEODetailsActivity.this.runOnUiThread(new Runnable() {

				public void run() {

					progDialogLoad.dismiss();
					progDialogLoad = null;

				}
			});

			if (canceledLoad) {

				IGEODetailsActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						progDialogLoad.dismiss();
						progDialogLoad = null;

					}
				});

				return;
			}

			if (dataItem == null) {

				IGEODetailsActivity.this.runOnUiThread(new Runnable() {

					public void run() {

						AlertDialog.Builder adb = new AlertDialog.Builder(
								IGEODetailsActivity.this);
						adb.setMessage(getResources().getString(R.string.details_text_error));
						adb.setNeutralButton(getResources().getString(R.string.text_back),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										if (IGEODataManager.getKeywords() == null) {
											onBackPressed();
										}
									}
								});

						Dialog d = adb.create();
						d.show();

					}
				});

				return;
			}

			//Após o item ter sido obtido, vamos carregar nas respetivas view's o seu conteúdo
			setupUIDataItem();

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}

	
	/**
	 * Coloca nas views respetivas a informação sobre o item.
	 */
	private void setupUIDataItem() {

		if (dataItem != null) {

			tTitle.setText(dataItem.title);
			tSource.setText("Fonte: "
					+ IGEODataManager.getActualSource().sourceName);
			tCategory.setText("Categoria: "
					+ IGEODataManager.localHashMapCategories
							.get(dataItem.categoryID).categoryName);
			fontBold = Typeface.createFromAsset(getAssets(),
					"fonts/Roboto-Bold.ttf");
			fontRegular = Typeface.createFromAsset(getAssets(),
					"fonts/Roboto-Regular.ttf");
			tTitle.setTypeface(fontBold);
			tSource.setTypeface(fontRegular);
			tCategory.setTypeface(fontRegular);

			if (dataItem.textOrHTML != null) {

				String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
						+ "<link href=\"styles/style.css\" rel=\"stylesheet\" type=\"text/css\"> "
						+ "</head>"
						+ "<body>"
						+ dataItem.textOrHTML
						+ "</body>" + "</html>";

				itemTextWebView.loadDataWithBaseURL(BASE_FOLDER, html,
						"text/html", "UTF-8", null);
				itemTextWebView
						.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

				html = null;
			}

			
			if (dataItem.imageURL != null) {
				if (!dataItem.imageURL.equals("")) {
					tmpImage = IGEOFileUtils
							.getDrawableFromInternet(dataItem.imageURL);
					
					//este método embora deprecated, garante o funcionamento da App nos dispositivos com versões anteriores
					//ao Android 4.0
					itemImageView.setBackgroundDrawable(tmpImage);
					
					//redimensionar a imagem
					//este redimensionamento é necessário dado que as imagens obtidas para os itens têm uma dimensão
					//variável. Devido a este facto, optou-se por redimensionar a mesma à largura do ecrã mantendo a relação largura-altura.
					//Assim, usando uma regra de três simples, vamos fazer o seguinte:
					//
					//   largura original ---------- altura original
					//   largura do ecrã  ---------- x
					//
					//   x = (largura do ecrã  x  altura original) / largura original
					//
					//Esta operação pode ser observada nas linhas seguintes:
					double w = tmpImage.getIntrinsicWidth();
					double h = tmpImage.getIntrinsicHeight();
					if(screenWidth == -1){
						DisplayMetrics displaymetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
						screenWidth = displaymetrics.widthPixels;
						
						displaymetrics = null;
					}
					
					double newH = (screenWidth * h) / w;
					
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemImageView.getLayoutParams();
					params.height = (int) newH;
					itemImageView.setLayoutParams(params);
					
					//--
					
				} else {
					//aqui será colocada a imagem por defeito
					itemImageView
							.setBackgroundResource(IGEODetailsActivity.this
									.getResources().getIdentifier(
											IGEOConfigsManager
													.getDefaultImage(),
											"drawable",
											IGEODetailsActivity.this
													.getPackageName()));
				}
			} else {
				//aqui será colocada a imagem por defeito
				itemImageView.setBackgroundResource(IGEODetailsActivity.this
						.getResources().getIdentifier(
								IGEOConfigsManager.getDefaultImage(),
								"drawable",
								IGEODetailsActivity.this.getPackageName()));
			}

		}
	}

	
	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e
	 * guarda-as na variável screenConfigs.
	 */
	public void applyConfigs() {

	}

	
	//Abordagem utilizada para desalocar as view's, subview, imagens e outros.
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

	private void unbindDrawables(View view) {
		try {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}

				if (!(view instanceof AdapterView))
					((ViewGroup) view).removeAllViews();
			}
		} catch (Throwable e) {
			finish();
			super.onDestroy();
		}
	}

}
