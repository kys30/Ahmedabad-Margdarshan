package sen.application.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class brtsMap extends Activity 
{
	private WebView image;
	FrameLayout mContentView;
	
	
	private static final FrameLayout.LayoutParams ZOOM_PARAMS =  
	new FrameLayout.LayoutParams(  
					ViewGroup.LayoutParams.FILL_PARENT,  
					ViewGroup.LayoutParams.WRAP_CONTENT,  
					Gravity.BOTTOM);  
	
	public void onCreate(Bundle savedInstanceState) 
	{
	super.onCreate(savedInstanceState);      
	setContentView(R.layout.brtsmap);
	image = (WebView) this.findViewById(R.id.webView1);
	image.getSettings().setBuiltInZoomControls(true);
   
	mContentView = (FrameLayout) getWindow().  
    getDecorView().findViewById(android.R.id.content);  
    
	final View zoom = this.image.getZoomControls();  
    mContentView.addView(zoom, ZOOM_PARAMS);  
    zoom.setVisibility(View.GONE);  
  
	StringBuilder html = new StringBuilder();
	html.append("<html>");
	html.append("<head>");
	html.append("</head>");
	html.append("<body>");
	html.append("<h1 style='background-color:#606;color:#ff2;font-family:'Lucida Sans Unicode', 'Lucida Grande', sans-serif';font-size=14px'>Ahmedabad BRTS Map</h1>");
	html.append("<p style='background-color:#606'><img style='width: 100%;' height='100%' src='brtsmap.jpg' /></p>");
	html.append("</body></html>");
	image.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "UTF-8", "");
	
	 

	}
}
