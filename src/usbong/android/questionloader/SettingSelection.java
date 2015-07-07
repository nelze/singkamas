package usbong.android.questionloader;

import java.io.InputStream;

import usbong.android.utils.UsbongUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingSelection extends Activity {
	
	
	String songname;
	String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
    	songname = bundle.getString("song_title");
    	language = bundle.getString("language");
        
        if (language.equalsIgnoreCase("Japanese")) {
        	setContentView(R.layout.setting_selection_japanese);        	
        }
        else if (language.equalsIgnoreCase("Mandarin")) {
            setContentView(R.layout.setting_selection_mandarin);                   	
        }
        else { //default
            setContentView(R.layout.setting_selection_japanese);        	        	
        }    	        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_and_feedback_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		switch(item.getItemId())
		{
			case(R.id.about):
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("About Singakamas");
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(SettingSelection.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int id) {
				            dialog.cancel();
				       }
				   });
				AlertDialog alert = builder.create();
				alert.show();
				return true;
				
			case(R.id.feedback):
				//http://stackoverflow.com/questions/8701634/send-email-intent;
				//last accessed: 1 April 2015, answer by Doraemon
				//send to cloud-based service
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto","usbong.ph@gmail.com",null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Singkamas: Feedback (Android)");
				emailIntent.putExtra(Intent.EXTRA_TEXT  , UsbongUtils.defaultFeedbackMessage);
				startActivity(Intent.createChooser(emailIntent, "Sending feedback..."));
				return true;
			default:
				return super.onOptionsItemSelected(item);

		}
	}
    
    public void easy(View view)
    {
    	
    	//Intent intent = new Intent(SettingSelection.this, MainActivity.class);
    	Intent intent = new Intent(SettingSelection.this, LoadingScreen.class);
    	intent.putExtra("difficulty", "easy");
    	intent.putExtra("song_title", songname);
    	intent.putExtra("language", language);
    	startActivity(intent);
    	SettingSelection.this.finish();
    }
    
    public void medium(View view)
    {
    	//Intent intent = new Intent(SettingSelection.this, MainActivity.class);
    	Intent intent = new Intent(SettingSelection.this, LoadingScreen.class);
    	intent.putExtra("difficulty", "medium");
    	intent.putExtra("song_title", songname);
    	intent.putExtra("language", language);
    	startActivity(intent);
    	SettingSelection.this.finish();
    	
    }
    
    
    @SuppressLint("NewApi")
	public void exitSettingSelection(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to return to Main Menu?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
			       	Intent intent = new Intent(SettingSelection.this, MainMenuActivity.class);
			    	startActivity(intent);
			    	SettingSelection.this.finish();
		       }
		   })
		   .setNegativeButton("No", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		            dialog.cancel();
		       }
		   });
		AlertDialog alert = builder.create();
		alert.show();
    }

}
