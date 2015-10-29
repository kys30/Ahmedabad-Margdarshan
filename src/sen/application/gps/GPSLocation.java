package sen.application.gps;

import java.io.IOException;
import java.util.ArrayList;


import sen.database.sqlite.DatabaseHandler;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class GPSLocation 
{
	
	
	Context myContext;
	public String myLatitude;
	public String myLongitude;
	private DatabaseHandler db=null;
	private String DATABASE_NAME="GPS";
	private String DATABASE_PATH="/data/data/sen.application.gui/databases/";
	public ArrayList<Loc> spots=new ArrayList<Loc>();
	
	public GPSLocation(Context c)
	{
		myContext=c;
	}
	
	public void openDatabase() 
	{
		 
		 
	    try
		{
			db=new DatabaseHandler(myContext,DATABASE_PATH,DATABASE_NAME);
			db.createDatabase();
		}
		catch (IOException e) 
		{
			throw new Error("Unable to create Database");
		}
		
		
		db.openDatabase();
		
	}

	public void execute()
	{
		String myLocation;//variables used to collect data from database
		double dataLongitude;
		double dataLatitude;
		double myLongitude;//gps longitude
		double myLatitude;
		
		
		openDatabase();
		
		String sql="select * from Coordinates where Latitude>=(23.024981-0.01)"
		+" INTERSECT "
		+"select * from Coordinates where Latitude<=(23.024981+0.01)"
		+" INTERSECT "
		+"select * from Coordinates where Longitude >=(72.577847-0.01)"
		+" INTERSECT "
		+"select * from Coordinates where Longitude<=(72.577847+0.01)";
		
		Cursor result=db.execute(sql);
		result.moveToFirst();
		
		
		while(result.isAfterLast()==false)
		{
			myLocation =result.getString(0);
			dataLatitude=result.getDouble(1);
			dataLongitude=result.getDouble(2);
			result.moveToNext();
			Loc myLoc=new Loc(myLocation,dataLongitude,dataLatitude);
        
			spots.add(myLoc);
		
		}
		result.close();
		db.close();

		Log.v("IT CAME ","HERE");
		Log.v("THE CLOSEST STATION IS:",spots.get(0).stop);
		
	}

	public void getCoordinates()
	{
	
		LocationManager locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
     
	 
		LocationListener locationListener = new LocationListener() 
		{
			//Overridden method
	         public void onLocationChanged(Location location) 
	         {
	           // Called when a new location is found by the network location provider.
	        	 makeUseOfNewLocation(location);
	         }
	         protected void makeUseOfNewLocation(Location location) 
	     	 {
	        	 Log.v("Your Latitude is:",Double.toString(location.getLatitude()));
	        	 Log.v(" Your Longitude is :",Double.toString(location.getLongitude()));
	     	}
	         	//Un-overridden methods.
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	        public void onProviderEnabled(String provider) {}
	        public void onProviderDisabled(String provider) {}
	       };
	      
	     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	    
	    
	}

	public String getLocation()
	{
		String location;
		this.openDatabase();
		String sql="MY SQL QUERY";
		Cursor result=db.execute(sql);
		result.moveToFirst();
		location="XYZ";
	
		return location;
	}


}


