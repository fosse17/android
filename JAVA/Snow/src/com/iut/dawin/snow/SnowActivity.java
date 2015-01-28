package com.iut.dawin.snow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iut.dawin.snow.utils.HttpUtils;


public class SnowActivity extends Activity {



	//Widgets
	private TextView mState, mTempMatin, mTempMidi, mVitesse, mNiveau;
	private ImageView mImage;
	private EditText mEditText;
	private Button mButton;
	private ProgressDialog mDialog;

	//On garde en m�moire l'ID de l'image affich�e
	private int mCurrentImageId = R.drawable.sunny;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snow);

		//R�cupere les r�f�rences aux widgets                                                                                                                                                            
		mState = (TextView)findViewById(R.id.state);
		mTempMatin = (TextView)findViewById(R.id.temperature_matin);
		mTempMidi = (TextView)findViewById(R.id.temperature_midi);
		mVitesse = (TextView)findViewById(R.id.vitesse_vent);
		mNiveau = (TextView)findViewById(R.id.niveau_neige);
		mImage = (ImageView)findViewById(R.id.image);
		mEditText = (EditText)findViewById(R.id.searchbox);
		mButton = (Button)findViewById(R.id.searchbutton);

		//On restore notre �tat si l'on peut
		if(savedInstanceState != null){
			mState.setText(savedInstanceState.getString("state"));
			mTempMatin.setText(savedInstanceState.getString("tMatin"));
			mTempMidi.setText(savedInstanceState.getString("tMidi"));
			mVitesse.setText(savedInstanceState.getString("vVent"));
			mNiveau.setText(savedInstanceState.getString("level"));
			mCurrentImageId = savedInstanceState.getInt("imageId");
			mImage.setImageResource(mCurrentImageId);
		}


		//Set listener for button
		mButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				//Dialog d'attente
				mDialog = ProgressDialog.show(SnowActivity.this, "", getString(R.string.txt_wait), true);

				//Lancement de la tache
				GetWheatherTask task = new GetWheatherTask();
				task.execute("http://snowlabri.appspot.com/snow?station=" + mEditText.getText().toString());



			}

		});
		
		final IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(new BatteryMonitor(), ifilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_snow, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivityForResult(new Intent(SnowActivity.this, StationListActivity.class), 0);
		return true;
	}




	@Override
	protected void onSaveInstanceState(Bundle outState) {

		//On sauvegarde notre �tat en cas de changement destruction de l'activit�
		outState.putString("state", mState.getText().toString());
		outState.putString("tMatin", mTempMatin.getText().toString());
		outState.putString("tMidi", mTempMidi.getText().toString());
		outState.putString("vVent", mVitesse.getText().toString());
		outState.putString("level", mNiveau.getText().toString());
		outState.putInt("imageId", mCurrentImageId);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//resultCode nous assure que l'activit� a termin� correctement
		if(resultCode == RESULT_OK){

			//On r�cup�re le nom de la station fourni par StationListActivity
			//et on le mets dans l'EditText
			final String station = data.getStringExtra("station");
			mEditText.setText(station);

			//Dialog d'attente
			mDialog = ProgressDialog.show(SnowActivity.this, "", getString(R.string.txt_wait), true);

			//Lancement de la tache
			GetWheatherTask task = new GetWheatherTask();
			task.execute("http://snowlabri.appspot.com/snow?station=" + station);
		}

	}





	private class GetWheatherTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String ...url) {

			//Creation de la requ�te
			final HttpClient httpClient = HttpUtils.getHttpClient(SnowActivity.this);
			final HttpGet httpGet = new HttpGet(url[0]);

			try {

				//Envoi de la requ�te
				HttpResponse httpResponse = httpClient.execute(httpGet);

				//V�rification et r�cup�ration de la r�ponse
				if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					final ByteArrayOutputStream out = new ByteArrayOutputStream();
					httpResponse.getEntity().writeTo(out);
					out.close();
					Log.d("SnowActivity", "Result: " + out.toString());
					return out.toString();
				}
				else {
					Log.e("SnowActivity", "La requ�te HTTP n'a pas aboutie correctement");
					return null;
				}
			} catch (ClientProtocolException e) {
				Log.e("SnowActivity", "Une erreur de protocole est survenue");
				return null;
			} catch (IOException e) {
				Log.e("SnowActivity", "Une erreur IO est survenue");
				return null;
			}
		}

		protected void onProgressUpdate(Integer... progress) {
			//Nothing
		}

		protected void onPostExecute(String result) {

			//On n'attends plus
			mDialog.dismiss();

			//Ne rien faire si le r�sultat est null
			if(result == null)
				return;

			try {

				//Parsing du r�sultat
				JSONObject jsonObject = new JSONObject(result);

				//Mise � jour de l'UI
				mState.setText("Ouverte: " + jsonObject.getString("ouverte" ));
				mTempMatin.setText("Temp�rature matin: " + jsonObject.getString("temperatureMatin"));
				mTempMidi.setText("Temp�rature midi: " + jsonObject.getString("temperatureMidi"));
				mVitesse.setText("Vitesse du vent: " + jsonObject.getString("vent"));
				mNiveau.setText("Niveau de la neige: " + jsonObject.getString("neige"));

				final String temps = jsonObject.getString("temps");
				if(temps.equals("beau")){
					mImage.setImageResource(R.drawable.sunny);
					mCurrentImageId = R.drawable.sunny;
				}
				else if (temps.equals("couvert")){
					mImage.setImageResource(R.drawable.cloudy);
					mCurrentImageId = R.drawable.cloudy;
				}
				else {
					mImage.setImageResource(R.drawable.snowy);
					mCurrentImageId = R.drawable.snowy;
				}

			} catch (JSONException e) {
				Log.e("SnowActivity", "Une erreur de JSON est survenue");
			}
		}
	}

	public class BatteryMonitor extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) { 

			final int level = intent.getIntExtra("level", 0);

			Log.i("SnowActivity", "EVENT:" + intent.getAction());
			Log.i("SnowActivity", "Level = " + level);

			//Notification de l'utilisateur si la batterie est faible
			if(intent.getAction() == Intent.ACTION_BATTERY_CHANGED){
				if(level < 5)
					Toast.makeText(getApplicationContext(), R.string.low_battery, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
