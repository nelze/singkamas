package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
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
/**
 * QuickAction dialog in Android taken from http://www.londatiga.net/it/how-to-create-quickaction-dialog-in-android/
 * by Lorensius Londa. Some modifications were made as well.
 *
 */
public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	//http://youtu.be/<VIDEO_ID>
	//ActionItem actionItem;
	public static final String API_KEY = UsbongUtils.API_KEY;
	//ActionItem ai = new ActionItem();
	int screenWidth;
	String VIDEO_ID ;
	boolean addedDict = false;
	boolean clickable;
	Spannable spannable;
	MediaPlayer mpSplash1;
	MediaPlayer mpSplash2;
	TextView question;
	TextView answer;
	TextView result;
	ListView videosFound;
	String translate1;
	String questionDifficultyFinal;
    Handler handler;
    String koreanwithspace;
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
	String selection;
	String[] selectionArgs;
	double score;
	double total;
	String link;
	int color;
	private ProgressBar mProgress;
	double progress;
	ArrayList<Integer> indices = new ArrayList<Integer>();
	ArrayList<String> chinDict = new ArrayList<String>();
	ArrayList<DictionaryEntry> dictEntries = new ArrayList<DictionaryEntry>();
	String language;
	List<VideoItem> searchResults;
	double accuracy; //added by Mike, 27 March 2015
    YouTubePlayer player;
    
    private ChineseDBAdapter dbHelper;
	private SimpleCursorAdapter dataAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        	Bundle bundle = getIntent().getExtras();
        	try{
        	dbHelper = new ChineseDBAdapter(this);
    		dbHelper.open();}
        	catch (Exception e)
        	{
        		Log.i("0","Opening error " + e);
        	}
    		
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	language = bundle.getString("language");
        	videosFound = (ListView)findViewById(R.id.videos_found); 
    		question   = (TextView)findViewById(R.id.questionView);
    		
    		//added by Mike, 15 June 2015
    		//Reference: http://stackoverflow.com/questions/17945176/want-textview-to-change-color-with-click-just-like-on-a-button
    		//; last accessed: 15 June 2015; answer by Raghunandan
    		
    		//i put addChinDict here so that it does not have to run every time a user taps on next.
    		//I tested it, and it's slower to not put it in an arraylist. it's cleaner that way too.
    		//although if there's another solution without the arraylist that's more efficient, pls replace it in addChineseDictionary()
    		
    		//added by Lev, edited by Brent, 28 June 2015
    		clickable = false;
    		question.setOnTouchListener(new OnTouchListener() {
    			int start,end;
    			@SuppressLint("ClickableViewAccessibility")
				@Override
    			public boolean onTouch(View v, MotionEvent event) {
    			try
    			{
    			//clickable is set by default to false when the app has not connected to the internet.
    			//bugs tend to happen when a user clicks on the textview without it loading yet.
    			if(clickable){
	    			    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    			    		QuickAction quickAction = new QuickAction(MainActivity.this);
		    			    	Layout layout = ((TextView) v).getLayout();
		    		    	      int x = (int)event.getX();
		    		    	      int y = (int)event.getY();
		    		    	      if (layout!=null){
		    		    	          int line = layout.getLineForVertical(y);
		    		    	          int offset = layout.getOffsetForHorizontal(line,x);
		    		    	          Log.i(Integer.toString(offset),"OOOOOOFFSEETT");
		    		    	          for(int i = 0; i<dictEntries.size();i++)
		    		    	          {  
		    		    	        	  int tempstart = dictEntries.get(i).start();
		    		    	        	  int tempend = dictEntries.get(i).end();
		    		    	        	  if (offset-tempstart >= 0 && offset-tempend < 0)
		    		    	        	  {
		    		    	        		  start = tempstart;
		    		    	        		  end = tempend;
		    		    	        		  SpannableStringBuilder def  = dictEntries.get(i).getSpannableString();
		    		    	        		  if(quickAction.size()>0)
		    		    	        		  {
			    		    	        		  for(int j = 0; j<quickAction.size();j++)
			    		    	        		  {
			    		    	        			  if(!dictEntries.get(i).getDefinition().equals(quickAction.getActionItem(j).getTitle()))
			    		    	        			  {
			    		    	        				  ActionItem actionItem = new ActionItem(dictEntries.get(i).getDefinition());
					    		    	        		  actionItem.setTitleSpan(def);
					    		    	        		  quickAction.addActionItem(actionItem);
					    		    	        		  color = dictEntries.get(i).getColor();
			    		    	        			  }
			    		    	        		  }
		    		    	        		  }
		    		    	        		  else
		    		    	        		  {
		    		    	        			  ActionItem actionItem = new ActionItem(dictEntries.get(i).getDefinition());
			    		    	        		  actionItem.setTitleSpan(def);
			    		    	        		  quickAction.addActionItem(actionItem);
			    		    	        		  color = dictEntries.get(i).getColor();
		    		    	        		  }
		    		    	        		  quickAction.show(question,start,question.getLeft());
		    		    	        		  quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
		    		    	        			  @Override
		    		    	        			  public void onItemClick(QuickAction source,int pos, int actionId) {
		    		    	        				  for(DictionaryEntry entry: dictEntries)
		    		    	        					  spannable.setSpan(new ForegroundColorSpan(entry.getColor()),entry.start(),entry.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								    			      spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, questionDifficultyFinal.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	    		    	        				   
		    		    	        				  Toast.makeText(MainActivity.this, "Word copied to clipboard.", Toast.LENGTH_SHORT).show();
		    		    	        				 }
		    		    	        				});
		    		    	        		  quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
													@Override
													public void onDismiss() {
														for(DictionaryEntry entry: dictEntries)
			    		    	        					  spannable.setSpan(new ForegroundColorSpan(entry.getColor()),entry.start(),entry.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									    			      spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, questionDifficultyFinal.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	
														//spannable.setSpan(new ForegroundColorSpan(color),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								    			    	//spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
													} 
			    		    	        		  });
		    		    	        		  System.out.println("def here" + def);
		    		    	        		 spannable.setSpan(new ForegroundColorSpan(0xFFFFFFFF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    		    	        		 spannable.setSpan(new BackgroundColorSpan(0xFF845723), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    		    	        	  }
		    		    	          }
		    		    	    }  
		    		    	    return true;
	    			    }
    			    /*else if (event.getAction() == MotionEvent.ACTION_UP) {
    			        // set to default color
    		    		question.setTextColor(Color.parseColor("#acacab"));		

    		    		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    		    		ClipData clip = ClipData.newPlainText("text", question.getText().toString());
    		    		clipboard.setPrimaryClip(clip);
    		    		Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
    		    		
    			    }*/
    				}
    				}catch(Exception e){e.printStackTrace();}    			
    				return true;
    				}
    			});
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
        	koreanwithspace = parts[0];
        	youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
    		youTubePlayerView.initialize(API_KEY, this);
        	if (difficulty.equalsIgnoreCase("easy"))
        	{
        		questionDifficultyFinal = language.equalsIgnoreCase("japanese")||language.equalsIgnoreCase("mandarin") ? parts[0].replace("　","").replace(" ", "").replace("\\", " "):parts[0];
        		questionDifficulty = language.equalsIgnoreCase("japanese")? "　"+parts[0].replace(" ", "　")+"　": parts[0];
        		translate1 = language.equalsIgnoreCase("japanese")? "　"+parts[1].replace(" ", "　")+"　":questionDifficulty;
        	}
        	else
        	{
        		questionDifficultyFinal =language.equalsIgnoreCase("japanese")||language.equalsIgnoreCase("mandarin")  ? parts[1].replace("　","").replace(" ", "").replace("\\", " "):parts[1];
        		questionDifficulty = language.equalsIgnoreCase("japanese")?"　"+parts[1].replace(" ", "　")+"　":parts[1];
        		translate1 = questionDifficulty;
        	}
        	total = qm.getCount();//-1;//do a -1 because questionCounter starts at 0; added by Mike, 31 March 2015
        	System.out.println(">>>>TOTAL: "+total);
//        	result.setText(""); //commented out by Mike, 27 March 2015
        	//answer.setText("Correct answer: "+ newQues.getCorrectAnswer());
        	
        	//added by Brent Anonas 28 June 2015
        	question.setText(questionDifficultyFinal,BufferType.SPANNABLE);
        	spannable = (Spannable)question.getText();
        	answer.setText("");
        	
        	//runs async task
    		new DictionaryTask().execute();
    		
    		//question.setY(qy);
    		//progress counter
        	//System.out.println(questionCounter +" " + total);
        	progress = questionCounter/total;
            mProgress.setProgress((int) (progress*100));
            searchOnYoutube(songname);
          
        }
        catch(Exception e)
        {
        	System.out.println(e+"lol error");
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
    
    //added by Brent and Lev, 28 June 2015
    private class DictionaryTask extends AsyncTask<Void,Void,Void>
    {
		@Override
		protected Void doInBackground(Void... params) {
			//EXECUTE THEM ALL!
        	if (language.equalsIgnoreCase("japanese"))
        		japExecute();
        	else if (language.equalsIgnoreCase("mandarin"))
        	{
        		//if(!addedDict)
        			//addChineseDictionary();
        		chineseExecute();
        	}
        	else if(language.equalsIgnoreCase("korean"))
				try {
					koreanExecute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Error " + e);
				}
        	//else if(language.equalsIgnoreCase("korean"))
        		
			return null;
		}
		
    	@Override
    	protected void onPostExecute(Void result)
    	{
	    	for(int i = 0; i < dictEntries.size();)
	    	{
	    		int start = dictEntries.get(i).start();
	    		int end = dictEntries.get(i).end();
	    		if(start != -1)
	    		{
	    			if(i%2==0&&dictEntries.get(i).getColor()==-1)
	    			{
	    				spannable.setSpan(new ForegroundColorSpan(0xFF668d3c),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    				dictEntries.get(i).setColor(0xFF668d3c);
	    				//spannable.setSpan(new ForegroundColorSpan(0xFFD7FF77),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    				//dictEntries.get(i).setColor(0xFFD7FF77);
	    			}
	    			else if(dictEntries.get(i).getColor()==-1)
	    			{
	    				//dictEntries.get(i).setColor(0x93CCEA00);
	    				//spannable.setSpan(new ForegroundColorSpan(0x93CCEA00),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    				dictEntries.get(i).setColor(0xFF99ca3d);
	    				spannable.setSpan(new ForegroundColorSpan(0xFF99ca3d),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			else
	    			{
	    				spannable.setSpan(new ForegroundColorSpan(dictEntries.get(i).getColor()),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			i++;
	    		}
	    		else
	    		{
	    			dictEntries.remove(i);
	    		}
	    	}
    		clickable=true;
    	}
    }
    /**
     * Used mainly for the japanese particles.
     * @param 
     * @param 
     * @param color 
     * @return if it is in question or not
     */
    private boolean inQuestion(String query,String def,int color)
    {
    	Log.i("query"+query+"huehue",def);
    	if(questionDifficulty.contains(query))
		{
    		int position = questionDifficulty.indexOf(query);
    		while(position!=-1)
    		{
    			Log.i(query,"added huehuehuheuue");
    			Log.i(def,"thisistoSsbhuehuehuheuue");
    			String s = questionDifficulty.substring(0,position);
    			int spaces = s.length() - s.replace("　", "").length();
    			dictEntries.add(new DictionaryEntry(query.replace("　",""),def,color,position-spaces,position+query.replace("　","").length()-spaces));
    			position = questionDifficulty.indexOf(query, position+query.length());
    		}
			return true;
		}
    	return false;
    }
    /**
     * Automatically searches the word in the sentence and adds it to dictEntries; Used for all languages.
     * @param query
     * @param def
     * @return if it is in question or not
     */
    private boolean inQuestion(String query,String def)
    {
    	if(questionDifficulty.contains(query))
		{
    		int position = questionDifficultyFinal.indexOf(query);
    		System.out.println(position+"positioooooooooooooon");
    		while(position!=-1)
    		{
    			Log.i(query,"added huehuehuheuue");
    			Log.i(def,"thisistoSsbhuehuehuheuue");
    			String wholeWord = "";
    			if(questionDifficulty.indexOf("　",questionDifficulty.indexOf(query))!=-1)
    				wholeWord = questionDifficulty.substring(questionDifficulty.indexOf(query),questionDifficulty.indexOf("　",questionDifficulty.indexOf(query)));
    			else if(questionDifficulty.indexOf(" ",questionDifficulty.indexOf(query))!=-1&&(language.equalsIgnoreCase("mandarin")||language.equalsIgnoreCase("korean")))
    				wholeWord = questionDifficulty.substring(questionDifficulty.indexOf(query),questionDifficulty.indexOf(" ",questionDifficulty.indexOf(query)));
    			else
    				wholeWord = questionDifficulty.substring(questionDifficulty.indexOf(query),questionDifficulty.indexOf(query)+query.length());
    			dictEntries.add(new DictionaryEntry(wholeWord,def,position,position+wholeWord.length()));
    			position = questionDifficultyFinal.indexOf(query, position+wholeWord.length());
    		}			
			return true;
		}
    	return false;
    }
    private void colorParticles()
    {
		try {
			BufferedReader br = null;
			if(language.equalsIgnoreCase("japanese"))
				br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("localJapaneseDictionary.txt")));
			else
				br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("localKoreanDictionary.txt")));
			String line;
			while((line = br.readLine())!=null)
			{
				String[] parts = line.split("~");
				int color = line.length()-line.replace("~","").length()==2 ? -1 : 0xFFd37627;
				if (language.equalsIgnoreCase("japanese"))
					inQuestion("　"+parts[0]+"　",parts[1],color);
				else
					inQuestion(parts[0],parts[1]);
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("CANT FIND FILE","asdfasdf");
				e.printStackTrace();
			}
    }
    private void japExecute()
    {
    	try
    	{
    		dictEntries.clear();
			// TODO Auto-generated method stub
		    	String reply = "";
		    	String url = "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?9U";
		    	String data = "gloss_line="+ URLEncoder.encode(translate1,"UTF-8")+"&dicsel=9&glleng=60";
				reply = postFormDataToUrl(url, data);
		    	colorParticles();
				// collect <li> stuff
				ArrayList<String> liList = new ArrayList<String>();
				int position = reply.indexOf("<li>", 0);
				while(position>-1)
				{
					String s = reply.substring(position+4, reply.indexOf("</li>", position));
					if (s.toLowerCase().trim().startsWith("possible"))
					{
						s = s.substring(s.indexOf("<br>")+4);
						System.out.println("FIXED: "+s);
					}
					
					liList.add(s);
					position = reply.indexOf("<li>", position+4);
				}
				
				//collect all inflected verbs
				ArrayList<String> fontList = new ArrayList<String>();
				int positionFont = reply.indexOf("<font color=\"blue\">");
				while(positionFont>-1)
				{
					String s = reply.substring(positionFont+19, reply.indexOf("</font>", positionFont));
					System.out.println(s+"hahahahahahahahahahahah");
					fontList.add(s);
					System.out.println("added fontList"+s+"huehue");
					positionFont = reply.indexOf("<font color=\"blue\">", positionFont+19);
				}
				
				// print the <li> stuff
				int fontListIndex = 0;
				for (String s : liList)
				{	
					boolean done = false;
					System.out.println("S here" + s);
					String[] parts = s.split(" ");
					System.out.println("parts[] asjfdkdaskfjh"+parts[0]+parts[1]);
					if(difficulty.equalsIgnoreCase("easy"))
					{
						int kanaIndex = s.indexOf("【");
						outerloop:
						while(kanaIndex!=-1)
						{
							int endKana = s.indexOf("】", kanaIndex+1);
							String kana = s.substring(kanaIndex+1, endKana);
							System.out.println(kana+"kanaahahahahahahahahahahahah");
							if(inQuestion(kana,s))
								break outerloop;
							String dictCode = s.substring(s.indexOf("(",endKana)+1,s.indexOf(")",endKana));
							System.out.println(dictCode+"hahahahahahahahahahahah");
							if(kana.contains("("))
									kana = kana.replace(kana.substring(kana.indexOf("("),kana.indexOf(")")+1),"");
							String kanaParts[] = kana.split(";");
							for(String subpart : kanaParts)
							{
								subpart = subpart.replace(" ", "");
								System.out.println(subpart+"huehuehuesubpparthaskjdfhaskd");
								if(inQuestion(subpart,s))
									break outerloop;
								else if(dictCode.contains("v"))
								{
									if (dictCode.contains("v5aru")||dictCode.contains("v5uru")||dictCode.contains("vz"))
									{
										if(inQuestion(subpart.substring(0,subpart.length()-2),s))
											break outerloop;
									}
									else if(inQuestion(subpart.substring(0,subpart.length()-1),s))
										break outerloop;
								}
							}
							kanaIndex = s.indexOf("【",endKana);
						}
					}
					else if(difficulty.equalsIgnoreCase("medium")&&parts[0].equals(""))
					{
						if(s.contains("v5aru")||s.contains("v5uru")||s.contains("vz")||s.contains("v-unspec"))
						{
							if(inQuestion(parts[1].substring(0,parts[1].length()-2),s))
							{
								System.out.println("succeeeeeeeeess-2");
								done = true;
							}
						}
						else if (s.contains("v5k-s")||s.contains("v1")||s.contains("v2")||s.contains("v4")||s.contains("v5")||s.contains("vn")||s.contains("vr")||s.contains("vs")||s.contains("vs-c"))
						{
							if(inQuestion(parts[1].substring(0,parts[1].length()-1),s))
							{
								System.out.println("succeeeeeeeeess-1");
								done = true;
							}
						}
					}
		            if(!parts[0].equals("")&&!done)
		            {
		            	if(inQuestion(fontList.get(fontListIndex),s))
		            		fontListIndex++;
		            }
		            else if(s.indexOf(parts[1])!=-1&&!done)
		            {
		            	if(inQuestion(parts[1],s))
		            		System.out.println("added in last case");
		            }
		            System.out.println("parts"+parts[1]);
				}
    	}catch(Exception e){System.out.println("THE PROBLEEEM "+e);}
    }
    /**
     * chineseExecute uses dynamic programming. If it were to be put in the server side of the code, 
     * searchPrefix and dictContains need to be added there.
     */
    private void chineseExecute()
	{
    	dictEntries.clear();
		//searchPrefix(translate1,"");
    	String[] parts = questionDifficulty.split(" ");
    	for(String part:parts)
    	{
    		dictContains(part);
    		/*for(String dict:chinDict)
    		{
    			if(!dict.contains("#"))
    			{
    			String splitDict[] = dict.split(" ");
    			//To show traditional and simplified versions of the character
    			int index = difficulty.equalsIgnoreCase("easy") ? 1 : 0;
    			if(part.equals(splitDict[index]))
    			{
    				int opposite = difficulty.equalsIgnoreCase("easy") ? 0 : 1;
    				String diff = difficulty.equalsIgnoreCase("easy") ? "\nTraditional: " : "\nSimplified: ";
    				String s = splitDict[index]+diff+splitDict[opposite]+"\n"+dict.substring(dict.indexOf(splitDict[2]),dict.length());
    				if(inQuestion(splitDict[index],s))
    					Log.i(dict,"DIIIIIIIIIIIICCCTT ADDED");
    			}
    			}
    		}*/
    	}
	}
    private void searchPrefix(String word,String result)
	{
		for(int i=1;i<=word.length();i++)
		{
			String prefix = word.substring(0,i);
			if (dictContains(prefix))
			{
				if(i==word.length())
				{
					result+=prefix;
					System.out.println("Result here" + result);
					break;
				}
				searchPrefix(word.substring(i,word.length()),result+prefix+"   ");
				}
		}
	}
    private boolean dictContains(String word)
	{
		System.out.println("Entering dictcontains");
		String url = "http://moscpas.dyndns.biz/getDefinitionMandarin.php?word='" + word+"'";
		String readUrlContentAsString;
		try {
			
			Cursor cursor = dbHelper.fetchDefByWord(word);
			System.out.println("Step 1");
	        if(cursor!=null && cursor.getCount()==0){//this means not yet in cache so get it then save it to local db
	            System.out.println("cursor is null");
	            readUrlContentAsString = NetUtil.readUrlContentAsString(url);
				ObjectMapper mapper = new ObjectMapper();
				List<LinkedHashMap> map = mapper.readValue(readUrlContentAsString, List.class);
				//System.out.println("Korean here " + map.get(0).get("DEF").toString());
				String full = map.get(0).get("READING").toString()+"\n"+map.get(0).get("DEF").toString();
				if (inQuestion(word, full)) //save it to local
				{
					dbHelper.createEntry( word, map.get(0).get("DEF").toString(), map.get(0).get("READING").toString());
					System.out.println("Entry created");
					cursor.close();
					return true;
				}
	        }
	        else //this means cursor has a value
	        {
	        	System.out.println("Cursor is not empty");
	        	if  (cursor.moveToFirst()) {
	                do {
	                	String full =cursor.getString(cursor.getColumnIndex("READING"))  + "\n" + cursor.getString(cursor.getColumnIndex("DEF"));
	                //String dir = cursor.getString(cursor.getColumnIndex("def"));
	                	if (inQuestion(word, full))
	                	{
	                		cursor.close();
	                		return true;
	                	}
	                }while (cursor.moveToNext());
	          }
	        }
	    cursor.close();
	    return true;
			
		} 
		//this means no definition
		catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error: " + e);
		}
		
		/*for(String dict:chinDict)
		{
			if(!dict.contains("#"))
			{
			String splitDict[] = dict.split(" ");
			//To show traditional and simplified versions of the character
			int index = difficulty.equalsIgnoreCase("easy") ? 1 : 0;
			if(word.equals(splitDict[index]))
			{
				int opposite = difficulty.equalsIgnoreCase("easy") ? 0 : 1;
				String diff = difficulty.equalsIgnoreCase("easy") ? "\nTraditional: " : "\nSimplified: ";
				String s = splitDict[index]+diff+splitDict[opposite]+"\n"+dict.substring(dict.indexOf(splitDict[2]),dict.length());
				if(inQuestion(splitDict[index],s))
					Log.i(dict,"DIIIIIIIIIIIICCCTT ADDED");
				return true;
			}
			}
		}*/
		return false;
	}
	private void koreanExecute() 
	{
		
		dictEntries.clear();
		colorParticles();
		String parts[] = questionDifficultyFinal.split(" ");
		System.out.println("Korean " + parts.length);
		for (int i = 0; i < parts.length; i++)
		{
			System.out.println("Hello" + parts[i]);
		}
		//search array parts(the sentence) in server side; send parts[] or questionDifficultyFinal to server side.
		//server returns String array definition.
		//If definition found, add to array. if not found, return "no def in dict"
		for (int i = 0; i < parts.length; i++)
		{
			String url = "http://moscpas.dyndns.biz/getDefinitionKorean.php?word='" + parts[i]+"'";
			String readUrlContentAsString;
			try {
				
				
				readUrlContentAsString = NetUtil.readUrlContentAsString(url);
				ObjectMapper mapper = new ObjectMapper();
				List<LinkedHashMap> map = mapper.readValue(readUrlContentAsString, List.class);
				System.out.println("Korean here " + map.get(0).get("DEF").toString());
				inQuestion(parts[i], map.get(0).get("DEF").toString());
			} 
			//this means no definition
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("error: " + e);
			}
		}
	}
	@SuppressLint("NewApi")
	@SuppressWarnings("resource")
	private void addChineseDictionary()
	{
		 URL url;
		 final String src = "https://raw.githubusercontent.com/SkycladObserver/CEdictSingkamas/master/cedict_ts.u8";			 
			try
				{
				    url = new URL(src);
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
					//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("cedict_ts.u8"),StandardCharsets.UTF_8.displayName()));
					//BufferedReader br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("cedict_ts.u8")));
					
					String line;
					while((line = br.readLine())!=null)
						chinDict.add(line);
				} catch(Exception e){System.out.println(e);}
			 Log.i("added mandarin dictionary","SADFSDFASDFASDFASFASDFASFASDFASDF");
			 addedDict = true;
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


	/*//commented out by Mike, June 15, 2015
	public void copyToClipboard(View view)
	{		
>>>>>>> 180c57f28043a0cdadff69b0bbcbcb1fafba0f46
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("text", question.getText().toString());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
	}
    
=======
	}*/
    public void nextQuestion(View view)
    {
    	clickable=false;
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
        	{
        		questionDifficultyFinal = language.equalsIgnoreCase("japanese") ||language.equalsIgnoreCase("mandarin") ? parts[0].replace("　","").replace(" ", "").replace("\\", " "):parts[0];
        		questionDifficulty = language.equalsIgnoreCase("japanese")? "　"+parts[0].replace(" ", "　")+"　": parts[0];
        		translate1 = language.equalsIgnoreCase("japanese") ? "　"+parts[1].replace(" ", "　")+"　":questionDifficulty;
        	}
        	else
        	{
        		questionDifficultyFinal =language.equalsIgnoreCase("japanese")||language.equalsIgnoreCase("mandarin") ? parts[1].replace("　","").replace(" ", "").replace("\\", " "):parts[1];
        		questionDifficulty = language.equalsIgnoreCase("japanese")?"　"+parts[1].replace(" ", "　")+"　":parts[1];
        		translate1 = questionDifficulty;
        	}
        	//question.setText("Hello");
        	//answer.setText("World");
        	
        	//added by Brent Anonas, 28 June 2015
        	question.setText(questionDifficultyFinal);
        	spannable = (Spannable)question.getText();
        	if (language.equalsIgnoreCase("japanese"))
        		new DictionaryTask().execute();
        	else if (language.equalsIgnoreCase("mandarin"))
    			new DictionaryTask().execute();
        	else if (language.equalsIgnoreCase("korean"))
        		new DictionaryTask().execute();
        	//if (language.equalsIgnoreCase("japanese"))
        		//japaneseDictionary(questionDifficulty);
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
    		dbHelper.close();
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
			       	//Intent intent = new Intent(MainActivity.this, xSongSelection.class);
			    	//intent.putExtra("language", language);
			    	//startActivity(intent);
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
/*    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
*/	
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
/*//commented out by Mike, 15 June 2015
  answer.setText(Html.fromHtml("Correct answer: " + newBold));
*/
    		answer.setText(Html.fromHtml("Correct answer: <b>" + newQues.getCorrectAnswer()+"</b>"));
    		
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
//    		@SuppressWarnings("deprecation")
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
    public void finish()
	{
		dbHelper.close();
		super.finish();
	}
    public static String postFormDataToUrl(String url, String data) throws Exception
	{
		InputStream is = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(data));
	        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
	        
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();           
	        InputStreamReader isr = new InputStreamReader(is);
	            
	        BufferedReader reader = new BufferedReader(isr);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        
	        return sb.toString();
	    }
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
			}
		}
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		Toast.makeText(this, arg1.toString(), Toast.LENGTH_LONG).show();
		
	}     
      
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