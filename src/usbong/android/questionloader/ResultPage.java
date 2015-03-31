package usbong.android.questionloader;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    
    
    public void back(View view)
    {
    	
    	Intent intent = new Intent(ResultPage.this, SongSelection.class);
    	intent.putExtra("language", language);
    	startActivity(intent);
    	ResultPage.this.finish();
    }
    
    
    
}
