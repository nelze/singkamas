package usbong.android.questionloader;



import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainMenuActivity extends Activity {
	
	Button start;
	Button exit;
	String language;
	Spinner spinner;
	Button review;
	Button spinOpen;
	@Override
	protected void onStart()
	{
		try
		{
			super.onStart();
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	    	if(position==0)
	    	    	{
	    	    		review.setVisibility(View.VISIBLE);
	    	    		spinOpen.setBackgroundResource(R.drawable.japanbutton_idle);
	    	    	}
	    	    	else if(position==1)
	    	    	{
	    	    		review.setVisibility(View.INVISIBLE);
	    	    		spinOpen.setBackgroundResource(R.drawable.mandarinbutton_idle);
	    	    	}
	    	    	else if(position==2)
	    	    	{
	    	    		review.setVisibility(View.VISIBLE);
	    	    		spinOpen.setBackgroundResource(R.drawable.koreanbutton_idle);
	    	    	}
	    	    }
	
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	    }
	    	});
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game);
        review = (Button)findViewById(R.id.button4);
        spinner = (Spinner) findViewById(R.id.spinner1);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	             R.array.language_array, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     spinner.setAdapter(adapter);
	     spinner.setVisibility(View.INVISIBLE);
	     
	     spinOpen = (Button) findViewById(R.id.button5);
	     spinOpen.setOnClickListener(new View.OnClickListener() {
	    	    @Override
	    	    public void onClick(View v) {
	    	        spinner.performClick();
	    	    }
	    	});
/*		
	     Display display = getWindowManager().getDefaultDisplay();
	     Point size = new Point();
	     display.getSize(size);
	     UsbongUtils.width = size.x;
	     UsbongUtils.height = size.y;
*/	     

	     //Reference: http://stackoverflow.com/questions/11338602/how-to-get-screen-resolution-of-your-android-device;
	     //last accessed: 1 April 2015, answer by K_Anas
	     DisplayMetrics metrics = getResources().getDisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(metrics);
	     UsbongUtils.width = metrics.widthPixels;
	     UsbongUtils.height = metrics.heightPixels;
	     UsbongUtils.dpi = metrics.densityDpi;
	     
	     UsbongUtils.defaultFeedbackMessage="\n\n\n\n\n-----\nInformation\nApp version: "+UsbongUtils.APP_VERSION+"\nAPI Level: "+android.os.Build.VERSION.SDK+"\nOS Version: "+System.getProperty("os.version")+"\nHost (Device): "+android.os.Build.DEVICE+"\nModel: "+android.os.Build.MODEL+"\nScreen: "+UsbongUtils.width+"x"+UsbongUtils.height+", "+UsbongUtils.dpi+"dpi"+"\n-----";
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
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(MainMenuActivity.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
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
    
    public void startRecord(View view) {
    	language = spinner.getSelectedItem().toString();
		Intent i = new Intent(getApplicationContext(), SongSelection.class);
		i.putExtra("language", language);
		
		startActivity(i);
	 }
    public void exit(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to exit?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		            MainMenuActivity.this.finish();
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
    
    public void review(View view)
    {
    	language = spinner.getSelectedItem().toString();
    	Intent intent = new Intent(MainMenuActivity.this, ReviewPage.class);
    	intent.putExtra("language", language);
    	startActivity(intent);
    }
}
