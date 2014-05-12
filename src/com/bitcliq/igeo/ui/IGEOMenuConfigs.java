package com.bitcliq.igeo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.bitcliq.igeo.ui_ordenamento.R;

/**
 * Esta é uma atividade utilizada para alterar as possíveis configurações da App.</br>
 * Neste momento a única configuração disponível é o raio utilizado na obtenção dos itens
 * no perto de mim.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOMenuConfigs extends PreferenceActivity {

	//constantes utilizadas
	private static final String TITLE = "Raio de pesquisa";
	private static final String KEY = "search_radius";
	private static final String DEFAULT_TITLE = TITLE + " - 10km";
	private static final String DEFAULT_PREFERENCES_RADIUS_VALUE = "4";
	//--

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences_igeo);
		
		//coloca o título na janela apresentada quando vamos escolher o valor a utilizar
		((ListPreference) IGEOMenuConfigs.this.getPreferenceManager().findPreference(KEY)).setDialogTitle(TITLE);

		//utilizando este listener podemos atribuir o valor que pretendemos ao titulo da label do campo "Raio de pesquisa"
		//sempre que alteramos o valor do raio.
		SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

				if(IGEOMenuConfigs.this.getPreferenceManager().findPreference(key) instanceof ListPreference){

					ListPreference listPref = (ListPreference) IGEOMenuConfigs.this.getPreferenceManager().findPreference(key);

					if(listPref.getTitle().toString().contains("-")){
						String newTitle = listPref.getTitle().toString().substring(0, listPref.getTitle().toString().indexOf("-") - 1);
						listPref.setTitle(newTitle);
						newTitle = null;
					}

					listPref.setTitle(listPref.getTitle().toString()+" - "+listPref.getEntries()[Integer.parseInt(listPref.getValue())-1]);
					listPref = null;
				}

			}
		};

		this.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

		//Permite definir o raio inicial na primeira vez que vamos às definições (após instalação da App)
		ListPreference listPref = (ListPreference) IGEOMenuConfigs.this.getPreferenceManager().findPreference(IGEOMenuConfigs.this.getResources().getString(R.string.search_radius));
		if(listPref.getValue() == null){
			listPref.setValue(DEFAULT_PREFERENCES_RADIUS_VALUE);
			listPref.setTitle(DEFAULT_TITLE);
		}
		else {
			listPref.setTitle(TITLE + " - " + listPref.getEntries()[Integer.parseInt(listPref.getValue())-1]);
		}

	}


	@Override
	public void onBackPressed(){
		super.onBackPressed();

		overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
	}

}

