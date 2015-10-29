package sen.application.gui;

import java.io.IOException;
import java.util.ArrayList;

import sen.application.gps.GPSLocation;
import sen.database.sqlite.DatabaseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class HomeScreen extends Activity implements OnItemClickListener
{
  
						//declarations of variables
	private static  Context context;
	private String DATABASE_PATH="/data/data/sen.application.gui/databases/";
	private String DATABASE_NAME="ROUTE";
	String selectSource, selectDestination;
	
	Button goButton,preferences,getMap,gps;
	TextView title,text;
	AutoCompleteTextView autoSource, autoDestination;
	ImageView image;
	Toast toast;
	
	ArrayList<String> store=new ArrayList<String>();
	DatabaseHandler db=null;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		
	super.onCreate(savedInstanceState);      
	setContentView(R.layout.main);
	
	context=getApplicationContext();
				//initializing the buttons,spinners and textviews.
	title = (TextView)findViewById(R.id.tvAMD);
	
	autoSource = (AutoCompleteTextView)findViewById(R.id.acSelection1);
	autoDestination = (AutoCompleteTextView)findViewById(R.id.acSelection2);
    
	goButton = (Button)findViewById(R.id.bGo);
    

				//Font styling for title
    Typeface fontOPP = Typeface.createFromAsset(getAssets(),"Old printing presS.ttf");
    title.setTypeface(fontOPP);
 
    			//database handling    
    try
	{
		db=new DatabaseHandler(context,DATABASE_PATH,DATABASE_NAME);
		db.createDatabase();
	}
	catch (IOException e) 
	{
		throw new Error("Unable to create Database");
	}
	
	db.openDatabase();
	
	Cursor result=db.execute("select * from STATION order by StationName");
	result.moveToFirst();
				//Fetching the result in an arraylist
	while(result.isAfterLast()==false)
	{
		store.add(result.getString(result.getColumnIndex("StationName")));
		result.moveToNext(); 
	}
	
	db.close();  // Closing the database to avoid leakage.

	
				//Auto complete text view functioning
	
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,store);
    
	autoSource.setAdapter(adapter);
    autoDestination.setAdapter(adapter);
    
    autoSource.setOnItemClickListener((OnItemClickListener) this);
    autoDestination.setOnItemClickListener((OnItemClickListener) this);
    
    autoSource.setThreshold(1);
    autoDestination.setThreshold(1);
    

    
	    	  		
    				//on click action of GO button
	goButton.setOnClickListener(new View.OnClickListener() 
	{	
		public void onClick(View v) 
		{
	     
			if(!((store.contains(selectSource)) && (store.contains(selectDestination))))
			{
				Toast.makeText(getApplicationContext(), "Invalid entries...", Toast.LENGTH_LONG).show();
			}
			
			else if(selectSource.equals(selectDestination))
			{
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.error,(ViewGroup) findViewById(R.id.Toast));
					
					// display the error image
				image = (ImageView) layout.findViewById(R.id.ivError);
				image.setImageResource(R.drawable.eror);
				
				// set a message
				text = (TextView) layout.findViewById(R.id.tvErrorMsg);
				text.setText("You are already at your destination!!");
					// Toast...
				
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(layout);
				toast.show();
			
			}	
			else 
			{
				Intent result=new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.ResultActivity.class);
				result.putExtra("source",selectSource);
				result.putExtra("destination",selectDestination);
				result.putStringArrayListExtra("stationList",store );
				startActivity(result);}
			}
		}					);
	}
	
			//generating Menu	
	public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
    {
 
        switch (item.getItemId())
        {
	        case R.id.menu_routes: Intent showRoutes = new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.BusRoutes.class);
													   startActivity(showRoutes);
													   return true;
	                     
	 
	        case R.id.menu_stops: Intent showStops = new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.BusStops.class);
	                              showStops.putStringArrayListExtra("stationList",store );
	                              startActivity(showStops);
								  return true;
	            
	 
	        case R.id.menu_brts:Intent showBrts = new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.brts.class);
					            startActivity(showBrts);
								return true;
										            
	 
	        case R.id.menu_map: Intent showMap = new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.showMap.class);
							    startActivity(showMap);
							    return true;
	 
	        case R.id.menu_gps: 
								GPSLocation loc=new GPSLocation(context);
								final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
								loc.execute();
								String gpsSource=loc.spots.get(0).stop;
								if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
								buildAlertMessageNoGps(gpsSource);
								else
								{
								try {
									Thread.sleep(5000);
								} 
								catch (InterruptedException e) 
								{
									e.printStackTrace();
								}	
								
									fetchGPS(gpsSource);
								}
								selectSource=gpsSource;
								
								return true;
	 
	        case R.id.menu_brts_map: Intent showPreferences = new Intent(sen.application.gui.HomeScreen.this, sen.application.gui.brtsMap.class);
	        						startActivity(showPreferences);
	        						return true;
	            
	         default:		return super.onOptionsItemSelected(item);
        }
    }    
	
				//Method to Turn on GPS
	private void buildAlertMessageNoGps(final String s) 
	{
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	           {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) 
	               {
	                   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	                  
	                   try 
	                   {
							Thread.sleep(5000);
	                   } 
	                   catch (InterruptedException e) 
	                   {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                 
	                 autoSource.setText(s);
	                   
	               }
	           }				)
	           .setNegativeButton("No", new DialogInterface.OnClickListener()
	           {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) 
	               {
	                    dialog.cancel();
	               }
	           }				);
	    
	    final AlertDialog alert = builder.create();
	    alert.show();
	   
	    
	}
					//Method to Fetch GPS Data
	private void fetchGPS(String s)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		   builder.setMessage("Fetching Data from GPS")
		           .setCancelable(false);
		   final AlertDialog alert = builder.create();
		  
		   alert.show();
		   autoSource.setText(s); 
		   alert.cancel();
	}

	public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
			long id) {
		selectSource = autoSource.getText().toString();
		selectDestination = autoDestination.getText().toString();
						}



}   
 