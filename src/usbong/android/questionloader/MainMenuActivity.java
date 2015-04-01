package usbong.android.questionloader;



import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainMenuActivity extends Activity {
	
	Button start;
	Button exit;
	String language;
	Spinner spinner;
	Button review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	review = (Button)findViewById(R.id.button4);
        setContentView(R.layout.main_game);
        
        spinner = (Spinner) findViewById(R.id.spinner1);
     // Create an ArrayAdapter using the string array and a default spinner layout
     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
             R.array.language_array, android.R.layout.simple_spinner_item);
     // Specify the layout to use when the list of choices appears
     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     // Apply the adapter to the spinner
     spinner.setAdapter(adapter);
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
				//send to cloud-based service
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"usbong.ph@gmail.com"});
				emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				emailIntent.addFlags(RESULT_OK);
				startActivityForResult(Intent.createChooser(emailIntent, "Email:"),UsbongUtils.EMAIL_SENDING_SUCCESS);
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
