package sen.application.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import sen.database.sqlite.DatabaseHandler;

import android.content.Context;
import android.database.Cursor;



public class Algorithm 
{
	private String Source,Destination;
	private Context myContext;
	private DatabaseHandler db=null;
	public ArrayList<String> directPath=new ArrayList<String>();
	public ArrayList<String> peerStations=new ArrayList<String>();
	public List< LinkedList<ResultNode>> Results=new LinkedList <LinkedList<ResultNode>>();
	private String DATABASE_NAME="ROUTE";
	private String DATABASE_PATH="/data/data/sen.application.gui/databases/";
	private int counter=0;
	
	public Algorithm(Context c,String s,String d)
	{
		myContext=c;
		Source=s;
		Destination=d;
	}
	
	private void openDatabase()
	{
		try 
		{
			 db=new DatabaseHandler(myContext,DATABASE_PATH,DATABASE_NAME);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();	
		}
		
		db.openDatabase();
	}
	
	private ArrayList<String> directPath(String s,String d)
	{
		
		ArrayList<String> r=new ArrayList<String>();
		
			//SQL query to get the direct routes from s to d.
		String sql="select C.RouteNo from (select * from CONTAINS  where StationName='"
			+s+
		"' ) as C,( select * from CONTAINS  where StationName='"
			+d+
		"' ) as B WHERE C.RouteNo=B.RouteNo";
		
			//Fetching the Result
		Cursor result=db.execute(sql);
		result.moveToFirst();
		
		while(result.isAfterLast()==false)
		{
			
			r.add(result.getString(0));
			result.moveToNext(); 
		}
		
		result.close();
		
		return r;
		 
		
	}
	
	public int getLength(ResultNode r) 			// Function to find the length of a given route
	{
				// SQL Query to find out the length of the route
		String sql="Select D - C  from (select StationSeqNo as D from contains where StationName='"
		+r.End+"' and  RouteNo='"+r.RouteNo.get(0)+"') ,"
		+"(select StationSeqNo as C from  contains where StationName='"+
		r.Origin+"' and  RouteNo='"+r.RouteNo.get(0)+"')";

		Cursor result=db.execute(sql);
		result.moveToFirst();

		return Math.abs(result.getInt(0));
	
	}
	
	
	public void setSequence()			//Function to set the priority of the Result routes
	{
		Comparator c=new Comparator<LinkedList<ResultNode>>() 
		{
	        public int compare(LinkedList<ResultNode> a1, LinkedList<ResultNode> a2)
	        {
	        	int c1=0;
	        	int c2=0;
	        	for(int i=0;i<a1.size();i++)
	        	{
	        		c1=c1+a1.get(i).length;
	        		
	        	}
	        	for(int i=0;i<a2.size();i++)
	        	{
	        		c2=c2+a2.get(i).length;
	        	}
	    	
	        	if(c1>c2)
	        		return 1;
		          else return -1;      
	        }     
	     };
		
		 Collections.sort(Results,c); 
	
	}
	
	public void doubleHop(String s,String d)
	{
		if(counter==2)				// Algorithm termination condition
			 return;
		
		      //SQL Queries to find the no. of routes passing thru s and d.
		Cursor resultone=db.execute("select count( distinct RouteNo) from CONTAINS where StationName='"+s+"'");
		Cursor resulttwo=db.execute("select count( distinct RouteNo) from CONTAINS where StationName='"+d+"'");
		
		resultone.moveToFirst();
		resulttwo.moveToFirst();


								//Case 1
		if(resultone.getInt(0)<resulttwo.getInt(0))
		{
		
			
			Cursor result=db.execute("select distinct RouteNo from CONTAINS where StationName='"+s+"'");
			result.moveToFirst();
			String route=result.getString(0);
			result.close();
			result=db.execute("select StationName from CONTAINS where RouteNo='"+route+"'");
			result.moveToFirst();
			String newSource=result.getString(0);
			execute(newSource,d);
			
			while(Results.size()>1)
				Results.remove(1);
			
			ResultNode r=new ResultNode();
			r.End=newSource;
			r.Origin=s;
			r.RouteNo.add(route);
			r.length=0;
			Results.get(0).add(0,r);
		}
	    else
	    {
			Cursor result=db.execute("select distinct RouteNo from CONTAINS where StationName='"+d+"'");
			result.moveToFirst();
			String route=result.getString(0);
			result.close();
			result=db.execute("select StationName from CONTAINS where RouteNo='"+route+"'");
			result.moveToFirst();
			String newSource=result.getString(0);
			execute(s,newSource);
			
			while(Results.size()>1)
			Results.remove(1);
			ResultNode r=new ResultNode();
			r.Origin=newSource;
			r.End=d;
			r.RouteNo.add(route);
			r.length=0;
			Results.get(0).add(r);
		
	    }
		
		if(Results.size()==0)
		{
			execute(s,"Lal Darwaja");
		}
	
	
	}
	
	
	public void execute(String s,String d)		// The main execution function of the Algorithm
	{
		
		counter++;						//Termination Condition
		this.openDatabase();
		
		ArrayList<String> temporaryResult=new ArrayList<String>(); 
		temporaryResult = directPath(s,d);  // TemporaryResult is an arraylist of Direct Paths from s to d
	
		
		
		if(!temporaryResult.isEmpty())
		{
			
			for(int i=0;i<temporaryResult.size();i++) //Adding all the direct paths to result
			{
				LinkedList<ResultNode> store=new LinkedList<ResultNode>();	
				
				ResultNode r=new ResultNode();
				r.Origin=s;
				r.End=d;
				r.RouteNo.add(temporaryResult.get(i));
				store.add(r);
				
				Results.add(store);
			    
			
			}
		
		}
		
		Cursor peer=db.execute("select StationName from STATION where StationType='H'");
		peer.moveToFirst();
		
		while(peer.isAfterLast()==false)
		{
			peerStations.add(peer.getString(0));
			peer.moveToNext();
		}
		
		
		
		for(int i=0;i<peerStations.size()-1;i++)
		{
			LinkedList<ResultNode> finalLink=new LinkedList<ResultNode>();
	
			ResultNode temporaryFirst=new ResultNode();
			ResultNode temporarySecond=new ResultNode();
			
			ArrayList<String> peerResultFirst=directPath(s,peerStations.get(i));
			ArrayList<String> peerResultSecond=directPath(peerStations.get(i),d);
			
			if((!peerResultFirst.isEmpty())&&(!peerResultSecond.isEmpty()))
			
			{	
				temporaryFirst.Origin=s;
				temporaryFirst.End=peerStations.get(i);
				temporaryFirst.RouteNo=(ArrayList<String>) peerResultFirst.clone();
				temporaryFirst.length=getLength(temporaryFirst);
				finalLink.add(temporaryFirst);
			
		
				temporarySecond.Origin=peerStations.get(i);
				temporarySecond.End=d;
				temporarySecond.RouteNo=(ArrayList<String>) peerResultSecond.clone();
				temporarySecond.length=getLength(temporarySecond);
				
				finalLink.add(temporarySecond);
				peerResultFirst.clear();
				peerResultSecond.clear();
			}
	
			Results.add(finalLink);
			
		}	
	
		
		for(int i=0;i<Results.size();i++)
		{
			if(Results.get(i).isEmpty())
				{
				Results.remove(i);
				i--;
				}
			
		}
		
		if(Results.isEmpty())
			doubleHop(s,d);
	
		
		db.close();
		setSequence();			// Setting the Priority sequence of the obtained result.
		
		
		
	}
}
	

