package sen.application.gui;

import java.io.IOException;
import java.util.ArrayList;

import sen.database.sqlite.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BusStops extends Activity implements OnItemClickListener {
	
	AutoCompleteTextView spinner;
	TableLayout table;
	String selectedStation, route, routeFrequency;
	LinearLayout layoutResult;
	
	private Context myContext;
	private String DATABASE_PATH="/data/data/sen.application.gui/databases/";
	private String DATABASE_NAME="ROUTE";
	private DatabaseHandler db=null;
	
	public ArrayList<String> routes=new ArrayList<String>();
	public ArrayList<Integer> frequency=new ArrayList<Integer>();
	ArrayList<String> stations = new ArrayList<String>();
	

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.bus_stops);
		
		myContext=getApplicationContext();
	
		spinner=(AutoCompleteTextView)findViewById(R.id.spinner_stations);
		TextView title=(TextView)findViewById(R.id.tvTitle);
		layoutResult = (LinearLayout)findViewById(R.id.llBusStops);
	    layoutResult.setOrientation(1);
	
	
		//Font styling for title
	    Typeface fontOPP = Typeface.createFromAsset(getAssets(),"Old printing presS.ttf");
	    title.setTypeface(fontOPP);
    
	    //initializing stations
	    Bundle extras2 = getIntent().getExtras();
	    stations = extras2.getStringArrayList("stationList");
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,stations);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemClickListener((OnItemClickListener) this);
     
	}
	
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
            long id) 
	{
		selectedStation = spinner.getText().toString();
		if(!(stations.contains(selectedStation)))
		{
			Toast.makeText(getApplicationContext(), "Invalid entries...", Toast.LENGTH_LONG).show();
		}
		else{fetchRouteInformation();}
    }

    private void fetchRouteInformation()
    {
    	try {
    		  
    		db=new DatabaseHandler(myContext,DATABASE_PATH,DATABASE_NAME);
    		db.createDatabase();
    		} 
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}
    	
    	db.openDatabase();
    	
    	Cursor result=db.execute("select distinct RouteNo from CONTAINS where StationName='"+selectedStation+"'");
    	result.moveToFirst();
    	
    	routes.clear();
    	while(result.isAfterLast()==false)
    	{
    		routes.add(result.getString(0));
    		result.moveToNext(); 
    	}
       
    	result.close();
    	
    	for (int i=0;i<routes.size();i++)
        {
	    	result=db.execute("select Frequency from ROUTE where RouteNo='"+routes.get(i)+"'");
	    	result.moveToFirst();	
	    	frequency.add(result.getInt(0));
	    	
        }
    	result.close();
      
    	db.close();
        
    	layoutResult.removeAllViews();
        
        for(int j=0; j<routes.size(); j++)
        {
        	final TextView txt =  new TextView(sen.application.gui.BusStops.this);
            
        	txt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            txt.setTextSize(20);
            txt.setBackgroundDrawable(getResources().getDrawable(R.drawable.textlines));
    		txt.setPadding(5, 5, 5, 5); 
        	txt.setText("Bus No.: ");
            txt.append(routes.get(j));
            txt.append("\nFrequency of bus/hr : ");
            txt.append(""+frequency.get(j)+"");
             
            layoutResult.addView(txt);
             
        }
      
	}

	public void onNothingSelected(AdapterView<?> adapter) {}
}



