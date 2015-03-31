package usbong.android.questionloader;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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