package sen.application.gui;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sen.application.algorithm.Algorithm;
import sen.application.algorithm.ResultNode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ProgressDialog;

public class ResultActivity extends Activity implements OnItemClickListener,Serializable
{
	  
	AutoCompleteTextView autoSource1, autoDestination1;
	TextView[] tvResult;
	LinearLayout layoutResult;
	Context myContext;
	String sourceSelect, destinationSelect;
	
	int id=0;
	ResultNode node = new ResultNode();
	ArrayList<String> stations=new ArrayList<String>();
	private String startStation, endStation;
	private List<LinkedList<ResultNode>> resultShow=new LinkedList <LinkedList<ResultNode>>();
	private ProgressDialog loading;
	ArrayList<String> routeList;
	ArrayAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) 
	  {
		   super.onCreate(savedInstanceState);
		   setContentView(R.layout.resultfirst);
		   
	   
	   
		   		//variable declarations
		   myContext=getApplicationContext();
		   int N=20;
		   tvResult = new TextView[N];
	       layoutResult = (LinearLayout)findViewById(R.id.llResult);
	       routeList = new ArrayList<String>();
	       autoSource1 = (AutoCompleteTextView)findViewById(R.id.acSelection3);
	   	   autoDestination1 = (AutoCompleteTextView)findViewById(R.id.acSelection4);
	   	   TextView title = (TextView)findViewById(R.id.tvAMD);
	   	   
   	   
	   	   		//Font styling for title        
	   	   Typeface fontOPP = Typeface.createFromAsset(getAssets(),"Old printing presS.ttf");
	   	   title.setTypeface(fontOPP);
       
	   	   	//retrieving selection information from main activity
	        Bundle extras = getIntent().getExtras();
	        sourceSelect = extras.getString("source");
		    destinationSelect = extras.getString("destination");
			stations = extras.getStringArrayList("stationList");
			adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,stations);
			autoSource1.setAdapter(adapter);
		    autoDestination1.setAdapter(adapter);
		    autoSource1.setOnItemClickListener(this);
		    autoDestination1.setOnItemClickListener(this);
		    autoSource1.setThreshold(1);
		    autoDestination1.setThreshold(1);
		    autoSource1.setText(sourceSelect);
		    autoDestination1.setText(destinationSelect);
		
	    
		    //generating information from the algorithm
		    //initialize the resultShow with algorithm result
	 	
		    new algorithmExecute().execute();
	  
	  }
	   
	OnClickListener listener = new OnClickListener()
	{
			public void onClick(View v)
			{
				id=v.getId();
				//passing the list of resultNodes to the next activity
				Intent result_expansion=new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.ResultExpansion.class);
				result_expansion.putExtra("source1",sourceSelect);
				result_expansion.putExtra("destination1",destinationSelect);
				result_expansion.putExtra("viewID",id);
				result_expansion.putExtra("SIZE", resultShow.get(id).size());
				
				for(int i=0;i<resultShow.get(id).size();i++)
				{
					String name=Integer.toString(i);
					Log.v("NAME,",name);
					result_expansion.putExtra(name,resultShow.get(id).get(i) );
				}
				
				startActivity(result_expansion);
			}
	};
	
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		sourceSelect = autoSource1.getText().toString();
		destinationSelect = autoDestination1.getText().toString();
		
	}
	//generating Menu	
	public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu_one, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
    {
 
        switch (item.getItemId())
        {
	        case R.id.menu_routes: Intent showRoutes = new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.BusRoutes.class);
													   startActivity(showRoutes);
													   return true;
	                     
	 
	        case R.id.menu_stops: Intent showStops = new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.BusStops.class);
	                              showStops.putStringArrayListExtra("stationList",stations );
	                              startActivity(showStops);
								  return true;
	            
	 
	        case R.id.menu_brts:Intent showBrts = new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.brts.class);
					            startActivity(showBrts);
								return true;
										            
	 
	        case R.id.menu_map: Intent showMap = new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.showMap.class);
							    startActivity(showMap);
							    return true;
	 
	        case R.id.menu_brts_map: Intent showPreferences = new Intent(sen.application.gui.ResultActivity.this, sen.application.gui.brtsMap.class);
	        							startActivity(showPreferences);
	        							return true;
	            
	         default:
	            return super.onOptionsItemSelected(item);
        }
    }
	  
	public class algorithmExecute extends AsyncTask<String, Integer, String> 
	{
		ProgressDialog dialog;
		protected void onPreExecute(){

		dialog = new ProgressDialog(ResultActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Please be patient!");
		dialog.setTitle("Fetching Results...");
		
		dialog.show();
	}
		
		@Override
		protected String doInBackground(String... arg0) 
		{
			Algorithm alg = new Algorithm(myContext, sourceSelect, destinationSelect);
		    alg.execute(sourceSelect, destinationSelect);
		    resultShow=alg.Results;
		    dialog.dismiss();
			return null;
		}
		
		protected void onProgressUpdate(Integer...progress ){}
		
		protected void onPostExecute(String result)
		{
			for(int i=0; i<resultShow.size(); i++)
			{
		    	TextView txt =  new TextView(sen.application.gui.ResultActivity.this);
		    	txt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		     	txt.setTextSize(20);
	    		txt.setText("\nRoute Plan "+""+(i+1)+"\n\n");
		    	txt.append("Start from ");
	    		txt.append(sourceSelect+"\n");
	    		for(int j=0; j<resultShow.get(i).size(); j++)
	    		{
		    		endStation=resultShow.get(i).get(j).End;
		    		routeList=resultShow.get(i).get(j).RouteNo;
		    	    txt.append("Board bus no. "); 
		    		for(int k=0; k<routeList.size(); k++)
		    		{
		    			txt.append(" "+routeList.get(k)+",");
		    		}
		    		
		    		txt.append("\nGet down at "+endStation+"\n");
		    	}
	    		txt.setBackgroundDrawable(getResources().getDrawable(R.drawable.textlines));
	    		txt.setFocusable(true);
	    		txt.setClickable(true);
	    		layoutResult.addView(txt);
	    		tvResult[i]=txt;
	    		tvResult[i].setId(i);
			  	tvResult[i].setOnClickListener(listener);
		  }
			//on click action of GO button
		    Button goButton = (Button)findViewById(R.id.bGo1);
			goButton.setOnClickListener(new View.OnClickListener() 
			{	
				public void onClick(View v) 
				{
					Intent intent = getIntent();
					intent.putExtra("source",sourceSelect);
					intent.putExtra("destination",destinationSelect);
					finish();
					startActivity(intent);
				
				}
				}						);

	 
}

}}