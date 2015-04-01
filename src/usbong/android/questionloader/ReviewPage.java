package usbong.android.questionloader;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
    		image.setImageResource(R.drawable.korean_chart);
    	}
    	else
    	{
    		tx.setText("Sorry, no review feature is available yet for your language.");
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
			       	Intent intent = new Intent(ReviewPage.this, MainMenuActivity.class);
			    	startActivity(intent);
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