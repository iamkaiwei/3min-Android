package com.threemin.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.FoursquareVenuesResquestListener;
import br.com.condesales.models.Venue;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

public class LocationActivity extends ThreeMinsBaseActivity implements ConnectionCallbacks,OnConnectionFailedListener {
	GoogleMap googleMap;
	private LocationClient mLocationClient;
	ListView list;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		list=(ListView) findViewById(R.id.location_list);
		mContext=this;
		googleMap = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		
		mLocationClient=new LocationClient(this, this, this);
		mLocationClient.connect();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
				Venue venue=(Venue) list.getItemAtPosition(postion);
				String data=new Gson().toJson(venue);
				Intent intent=new Intent();
				intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationRequest request = LocationRequest.create();
		request.setNumUpdates(1);
		mLocationClient.requestLocationUpdates(request, new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
				requestTipsNearby(latLng);
			}
		});
	}

	@Override
	public void onDisconnected() {
		
	}
	
	private void requestTipsNearby(LatLng latLng) {
		Location loc = new Location("");
		loc.setLatitude(latLng.latitude);
		loc.setLongitude(latLng.longitude);

		VenuesCriteria criteria2 = new VenuesCriteria();
		criteria2.setLocation(loc);
		EasyFoursquareAsync asyn=new EasyFoursquareAsync(this);
		asyn.getVenuesNearby(new FoursquareVenuesResquestListener() {

			@Override
			public void onError(String errorMsg) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onVenuesFetched(ArrayList<Venue> venues) {
				
				ArrayAdapter<Venue> adapter = new ArrayAdapter<Venue>(mContext,
				        android.R.layout.simple_list_item_1, venues);
				list.setAdapter(adapter);
				addMakertoMap(venues);
			}
		}, criteria2);
	}
	
	private void addMakertoMap(List<Venue> venues){
		for(Venue venue:venues){
		googleMap.addMarker(new MarkerOptions()
        .title(venue.getName())
        .position(new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng())));
		}
	}
}
