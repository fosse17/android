package com.iut.dawin.snow;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StationListActivity extends ListActivity {




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_list);

		//On récupére les deux tableaux de données définis dans strings.xml
		final String[] stations = getResources().getStringArray(R.array.stations);
		final String[] stationAdresses = getResources().getStringArray(R.array.stations_adresses);

		//On donne un nouvel Adapter a la liste
		setListAdapter(new StationAdapter(stations, stationAdresses));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_station_list, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		//On défini un résultat pour cette activité grâce à un Intent. Cet Intent contient le nom de la station
		//et pourra être récupéré par SnowActivity
		final Intent returnIntent = new Intent();
		returnIntent.putExtra("station", (String)getListView().getItemAtPosition(position));
		setResult(RESULT_OK, returnIntent);     
		finish();
	}



	public class StationAdapter extends BaseAdapter {

		private String[] mStations;
		private String[] mStationAdresses;

		public StationAdapter(String[] stations, String[] stationAdresses){
			mStations = stations;
			mStationAdresses = stationAdresses;
		}

		public int getCount() {
			return mStations.length;
		}

		public Object getItem(int position) {
			return mStations[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			//Le layout inflater permet de générer un View a partir d'un fichier XML
			final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			//On génére la view pour un item de la liste
			final View rowView = inflater.inflate(R.layout.row_layout, parent, false);

			//On récupére une référence vers les widgets que l'on doit modifier
			final TextView station = (TextView) rowView.findViewById(R.id.txt_station);
			final TextView stationAdresse = (TextView) rowView.findViewById(R.id.txt_station_adress);

			//On met le texte approprié a l'item 
			station.setText(mStations[position]);
			stationAdresse.setText(mStationAdresses[position]);

			//On renvoie la vue qui pourra être utilisée par la liste
			return rowView;
		}

	}
}
