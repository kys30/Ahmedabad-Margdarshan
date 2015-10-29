package sen.application.gui;

import java.util.ArrayList;

import sen.application.algorithm.ResultNode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultExpansion extends Activity implements OnItemClickListener 
{

	TextView title, Source, Destination;
	ListView list;
	ArrayList<String> busList;
	ArrayAdapter busAdapter;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_expansion);
	   
			//variable initializations
	    title = (TextView)findViewById(R.id.tvAMD);
	    Source = (TextView)findViewById(R.id.tvSourceValue);
	    Destination = (TextView)findViewById(R.id.tvDestinationValue);
	    list = (ListView)findViewById(R.id.lvBusNo);
	    busList = new ArrayList<String>();
	   
	    
            //Font styling for title        
       Typeface fontOPP = Typeface.createFromAsset(getAssets(),"Old printing presS.ttf");
       title.setTypeface(fontOPP);	   

  			//retrieving selection information from main activity
        Bundle extras1 = getIntent().getExtras();
      
        String sourceSelect = (String)extras1.getString("source1");
	    String destinationSelect = (String)extras1.getString("destination1");
	    int viewID=getIntent().getIntExtra("viewID",0);
	    int size=getIntent().getIntExtra("SIZE",0);
	  
	    ResultNode[] result=new ResultNode[size];
	    
	    for(int i=0;i<size;i++)
	    {
		     result[i]=(ResultNode)extras1.getSerializable(Integer.toString(i));
		     busList.addAll(result[i].RouteNo);
	     
	    }
  
	    		//Setting the value of source and destination dynamically     
	    Source.setText(sourceSelect);
	    Destination.setText(destinationSelect);
	    busAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,busList);
	    list.setAdapter(busAdapter);
	    list.setOnItemClickListener(this);

	  }

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		String route = (String) arg0.getItemAtPosition(arg2);
		Log.v("selectedRoute",route);
		Intent routeSend = new Intent(sen.application.gui.ResultExpansion.this, sen.application.gui.ResultExpansion1.class);
		routeSend.putExtra("SelectedRoute", route);
		startActivity(routeSend);
	}
}
