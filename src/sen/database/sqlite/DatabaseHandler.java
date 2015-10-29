package sen.database.sqlite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper
{


	private String DATABASE_PATH;
	private String DATABASE_NAME;
	private SQLiteDatabase Database;
	private Context myContext;
	
	
	public DatabaseHandler(Context context,String path,String name) throws IOException // Constructor
	{
		super(context,name,null,1);
		myContext = context;
		DATABASE_PATH=path;
		DATABASE_NAME=name;
		
	}

	
	public void onCreate(SQLiteDatabase db) 
	{									//Overridden onCreate method (does nothing)
	}


	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{	
	}									// Overridden onUpgrade method (does nothing)


	public void createDatabase() throws IOException
	{
		
		boolean dbexist = checkdatabase();
		if(dbexist);					// Do nothing, since the database already exists.  

		else
		{							// Get a writable database and Copy the 
			this.getWritableDatabase(); //database from Assets folder to the Data folder
			copyDatabase();
		}
	}


	private boolean checkdatabase() 
	{

		SQLiteDatabase checkdb = null;
		try
		{
			String myPath = DATABASE_PATH+ DATABASE_NAME;
			checkdb=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch(SQLiteException e){
			Log.d("DEBUGGER", "Database doesn't exist");
		}

		if(checkdb!=null)
		{	checkdb.close();
		return true;
		}
		return false;
	}							//End of checkdatabase

	
	private void copyDatabase() 
	{
		try {
			// Open your local database as the input stream
			InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

			// Path to the just created empty database
			String outFileName = DATABASE_PATH+DATABASE_NAME;

			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) 
				myOutput.write(buffer, 0, length);
			

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
			} 
		catch (Exception e) 
		{
			Log.e("error", e.toString());
			Log.v("ERROR IN COPYING","TMKC");
		}
	}										//End of copyDatabase()

	
	public void openDatabase() throws SQLException
	{
		String path=DATABASE_PATH+DATABASE_NAME;
		Database=SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READWRITE);
	}


	public void close()
	{
		Database.close();
	}

	
	public Cursor execute(String sql)
	{
		Cursor c=Database.rawQuery(sql, null);
		return c;
	}

	public void delete()
	{
		myContext.deleteDatabase(DATABASE_PATH+DATABASE_NAME);
	}

}			//End of Class
