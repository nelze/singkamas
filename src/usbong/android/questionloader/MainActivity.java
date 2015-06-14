package usbong.android.questionloader;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	//http://youtu.be/<VIDEO_ID>
	public static final String API_KEY = UsbongUtils.API_KEY;
	
	String VIDEO_ID ;
	MediaPlayer mpSplash1;
	MediaPlayer mpSplash2;
	TextView question;
	TextView answer;
	TextView result;
	ListView videosFound;
    Handler handler;
	EditText input_ans;
	int questionCounter = 0;
	QuestionManager qm;
	YouTubePlayerView youTubePlayerView;
	Question newQues;
	Button button1;
	Button button2;
	String user_answer;
	String questionDifficulty;
	YouTubePlayerFragment youTubePlayerFragment;
	String difficulty;
	boolean correct = true;
	String songname;
	double score;
	double total;
	String link;
	private ProgressBar mProgress;
	double progress;
	ArrayList<Integer> indices = new ArrayList<Integer>();
	String language;
	List<VideoItem> searchResults;
	double accuracy; //added by Mike, 27 March 2015
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        	Bundle bundle = getIntent().getExtras();
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	language = bundle.getString("language");
        	videosFound = (ListView)findViewById(R.id.videos_found); 
    		question   = (TextView)findViewById(R.id.questionView);
    		result   = (TextView)findViewById(R.id.resultView);
    		answer   = (TextView)findViewById(R.id.answerView);
    		input_ans   = (EditText)findViewById(R.id.editText1);
    		button1= (Button)findViewById(R.id.enterButton);
    		button2= (Button)findViewById(R.id.nextButton);
    		mProgress = (ProgressBar) findViewById(R.id.progressBar1);
    		mProgress.setMax(100);
    		handler = new Handler();
    		button2.setVisibility(View.INVISIBLE);
        try
        {
        	
        	InputStream isE = getResources().getAssets().open(language+"/" + songname);        	
//        	Log.d(">>>language : songname", language+" : "+songname);
        	qm = new QuestionManager();
        	qm.loadQuestions(isE, Question.DIFFICULTY_EASY);
        	newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
        	VIDEO_ID = newQues.getVideo();
        	link = newQues.getLink();
        	String string = newQues.getQuestionText();
        	String[] parts = string.split("~"); //changed "-" to "~" by Mike, 2 June 2015
        	youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
    		youTubePlayerView.initialize(API_KEY, this);
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	total = qm.getCount();//-1;//do a -1 because questionCounter starts at 0; added by Mike, 31 March 2015
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
            searchOnYoutube(songname);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
       
        
    }
    
    private void searchOnYoutube(final String keywords){
    		new Thread(){
                public void run(){
                    YoutubeConnector yc = new YoutubeConnector(MainActivity.this);
                    searchResults = yc.search(keywords);  
                    if ((searchResults!=null)&&(searchResults.size()>2)) {
                    	searchResults = searchResults.subList(0,2); //added by Mike, 22 May 2015
                    }
                    System.out.println("SR:" + searchResults);
                    System.out.println("KW:" + keywords);
                    if (searchResults!= null)
                    {
	                    handler.post(new Runnable(){
	                        public void run(){
	                        		updateVideosFound();
	                        }
	                    });
                }
                }
            }.start();
        }
    
    private void updateVideosFound(){
        	ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults){
	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) {
	                if(convertView == null){
	                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
	                }
	                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
	                TextView title = (TextView)convertView.findViewById(R.id.video_title);
	                TextView description = (TextView)convertView.findViewById(R.id.video_description);
	                 
	                VideoItem searchResult = searchResults.get(position);
	                 
	                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
	                title.setText(searchResult.getTitle());
	                description.setText(searchResult.getDescription());
	                return convertView;
	            }
	        };          
	         
	        videosFound.setAdapter(adapter);          
	        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	     
	            @Override
	            public void onItemClick(AdapterView<?> av, View v, int pos,
	                    long id) {              
	                //Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
	                //intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
	                //startActivity(intent);
	            	
	            	VIDEO_ID = searchResults.get(pos).getId();
	            	System.out.println("Here" + VIDEO_ID);
	            	player.cueVideo(VIDEO_ID);
	            }             
	        });			
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
	
	public void copyToClipboard(View view)
	{
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("text", question.getText().toString());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
	}
    
    public void nextQuestion(View view)
    {
    	if (!correct)
    		mpSplash1.stop();
    	else
    		mpSplash2.stop();
    	button2.setVisibility(View.INVISIBLE);
//    	result.setText(""); //commented out by Mike, 27 March 2015
    	answer.setText("");
    	input_ans.setText("");
    	button1.setVisibility(View.INVISIBLE);
    	try
    	{
    		newQues = qm.getQuestion(Question.DIFFICULTY_EASY,questionCounter);
    		String string = newQues.getQuestionText();
        	String[] parts = string.split("~"); //changed "-" to "~" by Mike, 2 June 2015
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
    		double totalScore = score/total; //added by Mike, 18 April 2015    	
    		Intent i = new Intent(getApplicationContext(), ResultPage.class);
    		i.putExtra("score", totalScore);
    		i.putExtra("language", language);
    		startActivity(i);
    		MainActivity.this.finish();
    		//Switch to scoreboard
    	}
    	
    }
    // Old Xiami Code
    
   /* public void openLink(View view)
    {
    	if(link.equalsIgnoreCase(""))
    		Toast.makeText(this, "Sorry, no link available!", Toast.LENGTH_LONG).show();
    	else
    	{
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
    		startActivity(browserIntent);
    		}
    }*/

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
    	System.out.println(">>>temp_accuracy: "+temp_accuracy);

//    	score += Math.round(temp_accuracy);
    	score += temp_accuracy;

    	//make bold
    	String newBold = makeBold(indices, newQues.getCorrectAnswer());
    	//aggregate accuracy scores
//    	accuracy = (accuracy+temp_accuracy)/(questionCounter+1);
//    	result.setText("Accuracy: " + Math.round(accuracy) + "%");

    	int temp = questionCounter+1;
    	System.out.println(">>>score: "+score);
    	System.out.println(">>>questionCounter: "+temp);
    	
    	double temp_score = score/(questionCounter+1); //added by Mike, 31 March 2015    	
    	//Reference: http://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java
    	//; answer by Bozho, last accessed: 31 March 2015
    	String a = new DecimalFormat("#.##").format(temp_score); //added by Mike, 31 March 2015
    	result.setText("Accuracy: " + a + "%");

//    	score = temp_score;    	//removed by Mike, 18 April 2015
     	
    	System.out.println("accuracy: "+accuracy);
    	System.out.println("questionCounter: "+questionCounter);
    	
    	if (temp_accuracy<100) 
    	{
    		answer.setText(Html.fromHtml("Correct answer: " + newBold));
    		
    		//Play wrong sound
    		mpSplash1 = MediaPlayer.create(this, R.raw.wrong);
    		mpSplash1.start();
    		correct = false;
    	}
    	else {
    		answer.setText("Correct!");
    		
    		//Play correct sound
    		mpSplash2 = MediaPlayer.create(this, R.raw.correct);
    		mpSplash2.start();
    		correct = true;
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
    		button2.setBackgroundDrawable(drawableX);
    		
    		//added by Mike, 11 April 2015
    		progress=total;
            mProgress.setProgress((int) (progress*100));
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
    	        	indices.add(i);
//    	        	System.out.println("Here");
    	            counter++;    //this is the number of different characters
    	        }
    	        
    	}
    	//add missing characters to mistakes
    	if (first.length < second.length)
    	{
    		for (int i = first.length; i < second.length; i++)
    		{
    			indices.add(i);
    			
    		}
    	}
    	
    	return 100*((minLength-counter)/maxLength); //edited by Mike, 31 March 2015
    }
    
    public String makeBold(ArrayList<Integer> index, String s)
    {
    	
		String withspace = s;
		
		
		ArrayList<Integer> spaces = new ArrayList<Integer>();
		for (int i = 0; i < withspace.length(); i++)
		{
			if (withspace.charAt(i) == ' ')
			{
				spaces.add(i);
			}
		}
		
		for (int i = 0; i < spaces.size(); i++)
		{
			for (int j = 0; j < index.size(); j++)
			{
				if (index.get(j) >= spaces.get(i))
				{
					int temp = index.get(j)+1;
					index.set(j, temp);
				}
				
			}
		}
		
		StringBuilder sb = new StringBuilder(withspace);
		int last = index.size()-1;
		for (int i = withspace.length(); i >=0; i--)
		{
			if (last >=0 && i == index.get(last))
			{
			
				sb.insert(i+1, "</b></font>");
				sb.insert(i, "<font color='red'><b>");
				last--;
			}
			
		}
		
		
		return sb.toString();
    	
    }

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		Toast.makeText(this, arg1.toString(), Toast.LENGTH_LONG).show();
		
	}
	
	 
     
      
     
      
     YouTubePlayer player;

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

		/** add listeners to YouTubePlayer instance **/
		this.player = player;
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.setPlaybackEventListener(playbackEventListener);

		/** Start buffering **/
		if (!wasRestored) {
			player.cueVideo(VIDEO_ID);
		}
	}    
	
	private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

		@Override
		public void onBuffering(boolean arg0) {
		}

		@Override
		public void onPaused() {
		}

		@Override
		public void onPlaying() {
		}

		@Override
		public void onSeekTo(int arg0) {
		}

		@Override
		public void onStopped() {
		}

	};

	private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

		@Override
		public void onAdStarted() {
		}
		
		@Override
		public void onLoaded(String arg0) {
		}

		@Override
		public void onLoading() {
		}

		@Override
		public void onVideoEnded() {
		}

		@Override
		public void onVideoStarted() {
		}



		@Override
		public void onError(ErrorReason arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}