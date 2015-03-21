package usbong.android.questionloader;

import java.io.InputStream;

import android.app.Activity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
        
    	
        setContentView(R.layout.review_page);
        
        Bundle bundle = getIntent().getExtras();
    	language = bundle.getString("language");
    	image = (ImageView) findViewById(R.id.imageView1);
    	tx = (TextView) findViewById(R.id.textView1);
    	if (language.equalsIgnoreCase("Japanese"))
    	{
    		image.setImageResource(R.drawable.japanese_chart);
    	}
    	else if (language.equalsIgnoreCase("Korean"))
    	{
    		image.setImageResource(R.drawable.korean_chart);
    	}
    	else
    	{
    		tx.setText("Sorry, no review feature is available yet for your language");
    	}
    		
        
    }

    
    
    
    
}
