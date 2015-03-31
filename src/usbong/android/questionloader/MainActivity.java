package usbong.android.questionloader;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
	ArrayList<Integer> indices = new ArrayList<Integer>();
	String language;

	double accuracy; //added by Mike, 27 March 2015
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        	Bundle bundle = getIntent().getExtras();
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	language = bundle.getString("language");
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
        	
        	InputStream isE = getResources().getAssets().open(language+"/" + songname);        	
//        	Log.d(">>>language : songname", language+" : "+songname);
        	qm = new QuestionManager();
        	qm.loadQuestions(isE, Question.DIFFICULTY_EASY);
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
//        	result.setText(""); //commented out by Mike, 27 March 2015
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
    	
    	button2.setVisibility(View.INVISIBLE);
//    	result.setText(""); //commented out by Mike, 27 March 2015
    	answer.setText("");
    	input_ans.setText("");
    	button1.setVisibility(View.INVISIBLE);
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
            indices.clear();
            button1.setVisibility(View.VISIBLE);
        	
        	
    	}
    	catch(Exception e)
    	{
    		double totalScore = Math.round(score/total);
    		Intent i = new Intent(getApplicationContext(), ResultPage.class);
    		i.putExtra("score", totalScore);
    		i.putExtra("language", language);
    		startActivity(i);
    		MainActivity.this.finish();
    		//Switch to scoreboard
    	}
    	
    }

    @SuppressLint("NewApi")
	public void exitMainActivity(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exiting...");
		builder.setMessage("Are you sure you want to return to song selection?")

		   .setCancelable(false)
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
			       	Intent intent = new Intent(MainActivity.this, SongSelection.class);
			    	intent.putExtra("language", language);
			    	startActivity(intent);
			    	MainActivity.this.finish();
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
    
    @SuppressLint("NewApi")
	public void enterAnswer(View view)
    {
    	
    	user_answer = input_ans.getText().toString();
    	//edited by Mike, 27 March 2015
    	double temp_accuracy = compareAnswer(user_answer.replaceAll("\\s+",""),newQues.getCorrectAnswer().replaceAll("\\s+",""));
    	score += Math.round(temp_accuracy);

    	//aggregate accuracy scores
//    	accuracy = (accuracy+temp_accuracy)/(questionCounter+1);
//    	result.setText("Accuracy: " + Math.round(accuracy) + "%");
    	result.setText("Accuracy: " + score + "%");

    	System.out.println("accuracy: "+accuracy);
    	System.out.println("questionCounter: "+questionCounter);
    	
    	if (accuracy<100);
    	{
    		/*try 
    		{
    			String sb = makeBold(indices, newQues.getCorrectAnswer());
    			answer.setText(Html.fromHtml("Correct answer: " + sb));
    		}
    		
    		catch (Exception e)
    		{
    			answer.setText(Html.fromHtml("Correct answer: <b>" + newQues.getCorrectAnswer() + "</b>"));
    		}*/
    		//
    		answer.setText("Correct answer: " + newQues.getCorrectAnswer());
    		
    	}
    	questionCounter++;
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    	}
    	catch(Exception e)
    	{
    		//button2.setText("End");
    		Drawable drawableX = this.getResources().getDrawable(R.drawable.end_selector);
    		button2.setBackground(drawableX);
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
    	        	indices.add(1);
    	        	System.out.println("Here");
    	            counter++;    //this is the number of different characters
    	        }
    	        else
    	        {
    	        	indices.add(0);
    	        	System.out.println("Here");
    	        	
    	        }
    	}
    	//System.out.println(counter);
    	System.out.println("Here" + indices.size());
    	return 100*((minLength-counter)/minLength);
    }
    
    public String makeBold(ArrayList<Integer> index, String s)
    {
    	StringBuilder boldMe = new StringBuilder(s.replaceAll("\\s+", ""));
    	System.out.println(boldMe);
    	System.out.println(index.size());
    	for (int i = indices.size()-1; i>0; i++)
    	{
    		if (indices.get(i)==1)
    		{
    			boldMe.insert(i+1, "</b");
    			boldMe.insert(i, "<b>");
    		}
    	}
    	
		return boldMe.toString();
    	
    }
    
}
