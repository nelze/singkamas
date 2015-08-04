package usbong.android.questionloader;

import java.text.DecimalFormat;

import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ResultPage extends Activity {	
	double score;
	TextView letter;
	TextView numScore;
	TextView msg;
	String language;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);
        
        Bundle bundle = getIntent().getExtras();
    	score = bundle.getDouble("score");
    	language = bundle.getString("language");
    	letter   = (TextView)findViewById(R.id.score);
		numScore   = (TextView)findViewById(R.id.numScore);
		msg = (TextView)findViewById(R.id.textView1);
		System.out.println("SCORE:"+score);
		
//		numScore.setText(Double.toString(Math.round(score)));
//		numScore.setText(Double.toString(score)); //edited by Mike, 31 March 2015
    	
    	String a = new DecimalFormat("#.##").format(score); //added by Mike, 18 April 2015
		numScore.setText(a); //edited by Mike, 18 April 2015
		
		if (score < 60 )
		{
			letter.setText("F");
			letter.setTextColor(Color.parseColor("#f92429"));
			msg.setText("Do better next time!");
		}
		else if (score >= 60 && score < 68)
		{
			letter.setText("D");
			letter.setTextColor(Color.parseColor("#f88617"));
			msg.setText("Do better next time!");
		}
		else if (score >= 68 && score < 75)
		{
			letter.setText("C");
			letter.setTextColor(Color.parseColor("#f88617"));
			msg.setText("Do better next time!");
		}
		else if (score >= 75 && score < 81)
		{
			letter.setText("C+");
			letter.setTextColor(Color.parseColor("#f8cb17"));
			msg.setText("Getting there!");
		}
		else if (score >= 81 && score < 87)
		{
			letter.setText("B");
			letter.setTextColor(Color.parseColor("#d6f817"));
			msg.setText("Aim higher!");
		}
		else if (score >= 87 && score < 92)
		{
			letter.setText("B+");
			letter.setTextColor(Color.parseColor("#b6f817"));
			msg.setText("Keep it up!");
		}
		else if (score >= 92 && score < 100)
		{
			letter.setText("A");
			letter.setTextColor(Color.parseColor("#5cf817"));
			msg.setText("Congratuations!");
		}
		else if (score >= 100)
		{
			letter.setText("S");
			letter.setTextColor(Color.parseColor("#4eff00"));
			msg.setText("Super Congrats!");
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
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(ResultPage.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
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

    
    public void back(View view)
    {    	
    	//Intent intent = new Intent(ResultPage.this, SongSelection.class);
    	//intent.putExtra("language", language);
    	//startActivity(intent);
    	ResultPage.this.finish();
    }
    
    @SuppressLint("NewApi")
	public void sendFeedback(View view)
    {
		//http://stackoverflow.com/questions/8701634/send-email-intent;
		//last accessed: 1 April 2015, answer by Doraemon
		//send to cloud-based service
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto","usbong.ph@gmail.com",null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Singkamas: Feedback (Android)");
		emailIntent.putExtra(Intent.EXTRA_TEXT  , UsbongUtils.defaultFeedbackMessage);
		startActivity(Intent.createChooser(emailIntent, "Sending feedback..."));
    }
    
    @SuppressLint("NewApi")
	public void exitResultPage(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to return to Main Menu?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
			       	Intent intent = new Intent(ResultPage.this, MainMenuActivity.class);
			    	startActivity(intent);
			    	ResultPage.this.finish();
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
