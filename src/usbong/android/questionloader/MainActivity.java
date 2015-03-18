package com.example.questionloader;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView question;
	TextView answer;
	TextView result;
	EditText input_ans;
	int questionCounter = 0;
	QuestionManager qm;
	Question newQues;
	Button button1;
	Button button2;
	String user_answer;
	String questionDifficulty;
	String difficulty;
	String songname;
	double score;
	double total;
	private ProgressBar mProgress;
	double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
    	
        	Bundle bundle = getIntent().getExtras();
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	System.out.println(difficulty);
    		question   = (TextView)findViewById(R.id.questionView);
    		result   = (TextView)findViewById(R.id.resultView);
    		answer   = (TextView)findViewById(R.id.answerView);
    		input_ans   = (EditText)findViewById(R.id.editText1);
    		button1= (Button)findViewById(R.id.enterButton);
    		button2= (Button)findViewById(R.id.nextButton);
    		mProgress = (ProgressBar) findViewById(R.id.progressBar1);
    		mProgress.setMax(100);
    		button2.setVisibility(View.INVISIBLE);
    		
        try
        {
        	
        	InputStream isE = getResources().getAssets().open("Japanese/" + songname);
        	//InputStream isM = getResources().getAssets().open("questions_M.txt");
        	//InputStream isH = getResources().getAssets().open("questions_H.txt");
        	qm = new QuestionManager();
        	qm.loadQuestions(isE, Question.DIFFICULTY_EASY);
        	//qm.loadQuestions(isM, Question.DIFFICULTY_MEDIUM);
        	//qm.loadQuestions(isH, Question.DIFFICULTY_HARD);
        	newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
        	
        	
        	String string = newQues.getQuestionText();
        	String[] parts = string.split("-");
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	total = qm.getCount();
        	//System.out.println("The question is " + questionDifficulty);
        	question.setText(questionDifficulty);
        	result.setText("");
        	//answer.setText("Correct answer: "+ newQues.getCorrectAnswer());
        	answer.setText("");
        	//progress counter
        	//System.out.println(questionCounter +" " + total);
        	progress = questionCounter/total;
            mProgress.setProgress((int) (progress*100));
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void nextQuestion(View view)
    {
    	button1.setVisibility(View.VISIBLE);
    	button2.setVisibility(View.INVISIBLE);
    	result.setText("");
    	answer.setText("");
    	input_ans.setText("");
    	
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    		String string = newQues.getQuestionText();
        	String[] parts = string.split("-");
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	//question.setText("Hello");
        	//answer.setText("World");
        	question.setText(questionDifficulty);
    		//question.setText(newQues.getQuestionText());
        	progress = questionCounter/total;
            mProgress.setProgress((int) (progress*100));
        	
        	
    	}
    	catch(Exception e)
    	{
    		double totalScore = Math.round(score/total);
    		Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
    		startActivity(i);
    		MainActivity.this.finish();
    		//Switch to scoreboard
    	}
    	
    }
    
    public void enterAnswer(View view)
    {
    	
    	user_answer = input_ans.getText().toString();
    	double accuracy = compareAnswer(user_answer.replaceAll("\\s+",""),newQues.getCorrectAnswer().replaceAll("\\s+",""));
    	result.setText("Accuracy: " + Math.round(accuracy) + "%");
    	score += Math.round(accuracy);
    	if (accuracy<100);
    	{
    		answer.setText("Correct answer: " + newQues.getCorrectAnswer());
    	}
    	questionCounter++;
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    	}
    	catch(Exception e)
    	{
    		button2.setText("End");
    		
    	}
    	button2.setVisibility(View.VISIBLE);
    	button1.setVisibility(View.INVISIBLE);
    	
    	
    }
    
    public double compareAnswer(String a, String b)
    {
    	
    	char[] first  = a.toLowerCase().toCharArray();
    	char[] second = b.toLowerCase().toCharArray();
    	double counter = 0;
    	double minLength = Math.min(first.length, second.length);

    	for(int i = 0; i < minLength; i++)
    	{
    	        if (first[i] != second[i])
    	        {
    	            counter++;    //this is the number of different characters
    	        }
    	}
    	System.out.println(counter);
    	
    	return 100*((minLength-counter)/minLength);
    }
    
}
