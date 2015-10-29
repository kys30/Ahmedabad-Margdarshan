package sen.application.gui;

import java.io.IOException;
import java.util.ArrayList;

import sen.database.sqlite.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class BusRoutes extends Activity implements OnItemSelectedListener 
{
	
	Spinner spinner;
	String selectedRoute;
	private String DATABASE_PATH="/data/data/sen.application.gui/databases/";
	private String DATABASE_NAME="ROUTE";
	
	private DatabaseHandler db=null;
	
	public ArrayList<String> routes=new ArrayList<String>();
	private ArrayList<String> stations=new ArrayList<String>();
	
	private ArrayAdapter<String> stationAdapter;
	private Context myContext;
	private ListView dynamicList;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.bus_routes);
		
		myContext=getApplicationContext();
	
		spinner=(Spinner)findViewById(R.id.spinner_routes);
		TextView title=(TextView)findViewById(R.id.tvTitle);
		dynamicList = (ListView)findViewById(R.id.lvBusRoute);
	
		//Font styling for title
	    Typeface fontOPP = Typeface.createFromAsset(getAssets(),"Old printing presS.ttf");
	    title.setTypeface(fontOPP);
    
	   try 
	   {
			  
			db=new DatabaseHandler(myContext,DATABASE_PATH,DATABASE_NAME);
			db.createDatabase();
		} 
	   catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	db.openDatabase();
	Cursor result=db.execute("select distinct RouteNo from ROUTE");
	
	result.moveToFirst();
	while(result.isAfterLast()==false)
	{
		routes.add(result.getString(0));
		result.moveToNext(); 
	}
    
	result.close();
	db.close();
    
    //initializing routes
  
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,routes);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener((OnItemSelectedListener) this);
	
	}
	
	public void onItemSelected(AdapterView<?> adapter, View arg1, int pos,
            long id) 
	{
		 selectedRoute = spinner.getSelectedItem().toString();
	        //displaying route information dynamically
		 try 
		 	{
			  
				db=new DatabaseHandler(myContext,DATABASE_PATH,DATABASE_NAME);
				db.createDatabase();
		 	} 
			
		 catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 	db.openDatabase();	
			Cursor result=db.execute("select StationName from CONTAINS WHERE RouteNo='"+selectedRoute+"'");
			result.moveToFirst();
			
			stations.clear();
			
			while(result.isAfterLast()==false)
			{
				stations.add(result.getString(0));
				Log.v("STATION NAME",stations.get(0));
				result.moveToNext(); 
			}
		    
			result.close();
            stationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,stations);
            dynamicList.setAdapter(stationAdapter);
        
    }

    public void onNothingSelected(AdapterView<?> adapter) {}

}
