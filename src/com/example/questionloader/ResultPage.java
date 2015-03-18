package com.example.questionloader;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
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
		System.out.println("SCORE:"+score);
		
		numScore.setText(Double.toString(Math.round(score)));
    	
		if (score < 60 )
		{
			letter.setText("F");
		}
		else if (score >= 60 && score < 68)
		{
			letter.setText("D");
		}
		else if (score >= 68 && score < 75)
		{
			letter.setText("C");
		}
		else if (score >= 75 && score < 81)
		{
			letter.setText("C+");
		}
		else if (score >= 81 && score < 87)
		{
			letter.setText("B");
		}
		else if (score >= 87 && score < 92)
		{
			letter.setText("B+");
		}
		else if (score >= 92)
		{
			letter.setText("A");
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
