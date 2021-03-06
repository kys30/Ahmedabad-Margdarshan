package sen.application.gui;

import java.io.IOException;
import java.util.ArrayList;

import sen.database.sqlite.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultExpansion1 extends Activity 
{
	
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
	   setContentView(R.layout.result_expansion_one);
	   
	   myContext=getApplicationContext();
	   Bundle extras2 = getIntent().getExtras();
	   String selectedRoute = extras2.getString("SelectedRoute");
	   dynamicList = (ListView)findViewById(R.id.lvHops);
	   
	   TextView txtRoute = (TextView)findViewById(R.id.tvRouteInfo);
	   txtRoute.setText("Route No. "+selectedRoute+" Information");
	   
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
 }


