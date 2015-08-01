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
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewPage extends Activity {
	
	
	String songname;
	String language;
	ImageView image;
	TextView tx;
	Button button;
	
	private Boolean isShowingHiraganaChart=true;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.review_page);
        
        Bundle bundle = getIntent().getExtras();
    	language = bundle.getString("language");
    	image = (ImageView) findViewById(R.id.imageView1);
    	tx = (TextView) findViewById(R.id.textView1);
    	button = (Button) findViewById(R.id.button);    	
    	
    	if (language.equalsIgnoreCase("Japanese"))
    	{
    		image.setImageResource(R.drawable.hiragana_chart);
    		button.setVisibility(View.VISIBLE);
    	}
    	else if (language.equalsIgnoreCase("Korean"))
    	{
    		image.setImageResource(R.drawable.hangeul_chart);
    	}
    	else
    	{
    		tx.setText("Sorry, no review feature is available yet for your language.");
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
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(ReviewPage.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
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

    public void buttonPressed(View view) {
    	if (!isShowingHiraganaChart) {
//    		button.setText("Review Katakana");
    		isShowingHiraganaChart=true;
    		button.setBackgroundResource(R.drawable.review_katakana);
    		image.setImageResource(R.drawable.hiragana_chart);
    	}
    	else {
    		isShowingHiraganaChart=false;
//    		button.setText("Review Hiragana");
    		button.setBackgroundResource(R.drawable.review_hiragana);
    		image.setImageResource(R.drawable.katakana_chart);    		
    	}
	 }   
    
    @SuppressLint("NewApi")
	public void exitReviewPage(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to return to Main Menu?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
			       	//Intent intent = new Intent(ReviewPage.this, MainMenuActivity.class);
			    	//startActivity(intent);
			    	ReviewPage.this.finish();
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