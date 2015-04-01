package usbong.android.questionloader;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import usbong.android.utils.UsbongUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        	total = qm.getCount()-1;//do a -1 because questionCounter starts at 0; added by Mike, 31 March 2015
        	System.out.println(">>>>TOTAL: "+total);

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
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(MainActivity.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
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
    	double temp_score = score/(questionCounter+1); //added by Mike, 31 March 2015    	
    	//Reference: http://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java
    	//; answer by Bozho, last accessed: 31 March 2015
    	String a = new DecimalFormat("#.##").format(temp_score); //added by Mike, 31 March 2015
    	result.setText("Accuracy: " + a + "%");

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
    	double maxLength = Math.max(first.length, second.length); //added by Mike, 31 March 2015    	

    	for(int i = 0; i < minLength; i++)
    	{
    	        if (first[i] != second[i])
    	        {
    	        	indices.add(1);
//    	        	System.out.println("Here");
    	            counter++;    //this is the number of different characters
    	        }
    	        else
    	        {
    	        	indices.add(0);
//    	        	System.out.println("Here");  	        	
    	        }
    	}
    	//System.out.println(counter);
    	System.out.println("Here" + indices.size());

//    	return 100*((minLength-counter)/minLength);
    	return 100*((minLength-counter)/maxLength); //edited by Mike, 31 March 2015
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
