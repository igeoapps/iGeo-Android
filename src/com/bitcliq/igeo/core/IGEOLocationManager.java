package com.bitcliq.igeo.core;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Esta classe é responsável pela gestão de alterações de localização. É aqui que se encontra o LocationListner.
 * @author Bitcliq, Lda.
 *
 */

public class IGEOLocationManager
{

	/**
	 * Tempo mínimo entre cada atualização da localização
	 */
	private static final long MIN_TIME_UPDATE = 5000;

	/**
	 * Distância mínima em metros para atualização da localização
	 */
	private static final long MIN_DISTANCE_UPDATE = 0;

	/**
	 * Gestor de localização
	 */
	private static LocationManager igeoLocManager;

	/**
	 * Listener que irá receber as novas localizações
	 */
	private static LocationListener locationListener;


	/**
	 * Contém a última localização obtida
	 */
	private static Location igeoLastLoc = new Location("");

	/**
	 * Contexto atual. É usado para iniciar a localização.
	 * Será usado aqui o ApplicationCOntext dado que o LocationManager é geral.
	 */
	private Context context;

	/**
	 * Variável temporária para guardar uma localização recebida
	 */
	private static Location loc = null;





	/**
	 * Construtor por defeito da classe IGEOLocationManager
	 */
	public IGEOLocationManager(){
		super();
	}


	/**
	 * Construtor em que se passa o contexto e inicia a localização
	 * @param context Contexto atual
	 */
	public IGEOLocationManager(Context context){
		this.context = context;

		initLocation();
	}


	//Operações de inicialização e paragem do listener #############################
	/**
	 * Coloca o listener a receber alterações de localização.
	 */
	public void startLocationListener(){
		try{
			igeoLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE,locationListener);
		}
		catch(Throwable t){
			t.printStackTrace();
		}

		igeoLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, locationListener);
		Log.i("DGALocation", "location started");
	}


	/**
	 * Faz com que listener deixe de receber alterações de localização.
	 */
	public void stopLocationListener(){
		igeoLocManager.removeUpdates(locationListener);
		Log.i("DGALocation", "location stoped");
	}



	/**
	 * Inicializa o listener, definindo os seus métodos e indo obter a última localização conhecida e colocando-a
	 * como localização atual.
	 */
	public void initLocation(){

		igeoLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//Start a location listener
		locationListener=new LocationListener() {

			public void onLocationChanged(Location loc) {

				//Log.i("DGALocation", "new location"+loc.getLatitude()+", "+loc.getLongitude());

				igeoLastLoc=loc;

			}

			public void onProviderDisabled(String provider) {

				try {
					igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
					if(!igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )){
						igeoLastLoc = null;
						//Log.i("DGALocation", "had lost location");
					}
				}
				catch(Throwable t){
					if(!igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )){
						igeoLastLoc = null;
						//Log.i("DGALocation", "had lost location");
					}
				}

			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,Bundle extras) {
			}



		};



		try{
			loc = igeoLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			igeoLastLoc=loc;
			//Log.i("DGALocation", "getting initial location = "+loc.getLatitude()+", "+loc.getLongitude());

		} catch(Exception e){
			try{
				loc = igeoLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				igeoLastLoc=loc;
				//Log.i("DGALocation", "getting initial location = "+loc.getLatitude()+", "+loc.getLongitude());

			} catch(Exception ex){
				ex.printStackTrace();
				//Log.i("DGALocation", "error getting initial location");
			}
		}

	}





	/**
	 * Verifica se o GPS está ativo.
	 * @return true ou false consoante o GPS está ou não ativo.
	 */
	public static boolean isGPSActivated() {
		try {
			if(igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
				return true;
			else
				return igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		}
		catch(Throwable t){
			return igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		}
	}




	/**
	 * Verifica se existe acesso à localização atual por GPS ou pela rede. Embora
	 * o GPS possa não estar ativo, podemos em alguns casos ter acesso à localização atual através da internet ou do
	 * cruzamento do sinal de redes wifi.
	 * @return true ou false consoante existe ou não acesso à localização atual.
	 */
	public static boolean hadLocation(Activity a) {

		try {
			igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER );

			if(igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )){

				if(!igeoLocManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
					if(IGEOUtils.CheckInternet(a)){
						if(igeoLastLoc==null)
							return false;
						
						if(igeoLastLoc.getLatitude()>0 && igeoLastLoc.getLatitude()>0)
							return true;
						else
							return false;
					}
					else
						return false;
				}

				if(igeoLastLoc!=null){
					if(igeoLastLoc==null)
						return false;
					
					if(igeoLastLoc.getLatitude()>0 && igeoLastLoc.getLatitude()>0)
						return true;
					else
						return false;
				}

			}
			return false;
		}
		catch(Throwable t){
			if(igeoLocManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )){

				if(IGEOUtils.CheckInternet(a)){
					if(igeoLastLoc==null)
						return false;
					
					if(igeoLastLoc.getLatitude()>0 && igeoLastLoc.getLatitude()>0)
						return true;
					else
						return false;
				} else
					return false;

			}
			return false;
		}

	}


	
	
	
	
	
	
	//Outros métodos úteis #########################################################################
	/**
	 * Devolve a última localização obtida.
	 * @return Location localização obtida
	 */
	public static Location getActualLocation() {
		return igeoLastLoc;
	}

}

