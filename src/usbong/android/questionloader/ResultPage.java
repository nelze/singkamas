package usbong.android.questionloader;

import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
		numScore.setText(Double.toString(score)); //edited by Mike, 31 March 2015
    	
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
		else if (score >= 92)
		{
			letter.setText("A");
			letter.setTextColor(Color.parseColor("#5cf817"));
			msg.setText("Congratuations!");
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

    
    public void back(View view)
    {    	
    	Intent intent = new Intent(ResultPage.this, SongSelection.class);
    	intent.putExtra("language", language);
    	startActivity(intent);
    	ResultPage.this.finish();
    }
    
    @SuppressLint("NewApi")
	public void sendFeedback(View view)
    {
		//send to cloud-based service
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"usbong.ph@gmail.com"});
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		emailIntent.addFlags(RESULT_OK);
		startActivityForResult(Intent.createChooser(emailIntent, "Email:"),UsbongUtils.EMAIL_SENDING_SUCCESS);
    }
    
}
