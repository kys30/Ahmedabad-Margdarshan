package sen.application.gps;

public class Loc 

{
	public String stop;
	public double lang;
	public double lat;
	public double distance;


	Loc(String a,double b,double c)
	{ 
		stop=a;
		lang=b;
		lat=c;
				
	}


	public void cal(double point_lang,double point_lat)
	{
		double a= 111.2*(point_lang-lang);
		double b=111.2*(point_lat-lat);
		distance= a*a + b*b;
		distance=Math.sqrt(distance); 
	}
}