package com.bitcliq.igeo.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.bitcliq.igeo.core.IGEOConfigsManager;
import com.bitcliq.igeo.core.IGEOUtils;
import com.bitcliq.igeo.ui_ordenamento.R;


/**
 * Actividade que contém o "about" do projeto.
 * @author Bitclic, Lda.
 *
 */
public class IGEOInfoActivity extends Activity implements IGEOConfigurableActivity {

	/**
	 * WebView onde será apresentada a informação
	 */
	private WebView webViewInfo;

	/*
	 * Mensagem de erro apresentada quando não existe ligação à internet
	 */
	private TextView tErrorMsg1;

	/*
	 * Mensagem de erro apresentada quando não existe ligação à internet
	 */
	private TextView tErrorMsg2;

	//Fontes
	private static Typeface fontRegular;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_activity);

		webViewInfo = (WebView) findViewById(R.id.webViewInfo);
		webViewInfo.loadUrl(this.getResources().getString(R.string.info_url));
		webViewInfo.getSettings().setJavaScriptEnabled(true);

		if(!IGEOUtils.CheckInternet(this)){
			webViewInfo.setVisibility(View.GONE);
			
			//Fontes
			fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
			
			tErrorMsg1 = (TextView) findViewById(R.id.t_info_error1);
			tErrorMsg1.setText(this.getResources().getString(R.string.info_error1));
			tErrorMsg1.setTypeface(fontRegular);
			tErrorMsg1.setTextSize(18.0f);


			tErrorMsg2 = (TextView) findViewById(R.id.t_info_error2);
			tErrorMsg2.setText(this.getResources().getString(R.string.info_error2));
			tErrorMsg2.setTypeface(fontRegular);
			tErrorMsg2.setTextSize(18.0f);
		}
	}



	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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



	/**
	 * Obtém do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 */
	public void applyConfigs() {
		// TODO : to implement	
	}

}
