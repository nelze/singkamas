package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

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

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	//http://youtu.be/<VIDEO_ID>
	ActionItem actionItem;
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
	ArrayList<String> chinDict = new ArrayList<String>();
	ArrayList<DictionaryEntry> dictEntries = new ArrayList<DictionaryEntry>();
	//this is the new definitions arraylist. uses SpannableStringBuilder instead of string to edit style in textview
	ArrayList<SpannableStringBuilder> definitionsSsb = new ArrayList<SpannableStringBuilder>();
	ArrayList<ArrayList<String>> overlaps = new ArrayList<ArrayList<String>>();
	ArrayList<String> partsList = new ArrayList<String>();
	QuickAction quickAction;
	String language;
	List<VideoItem> searchResults;
	double accuracy; //added by Mike, 27 March 2015
    YouTubePlayer player;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
	        //Display display = getWindowManager().getDefaultDisplay();
			//Point size = new Point();
			//display.getSize(size);
			//screenWidth = size.x;
        	actionItem = new ActionItem();
        	quickAction = new QuickAction(this);
        	Bundle bundle = getIntent().getExtras();
        	difficulty = bundle.getString("difficulty");
        	songname = bundle.getString("song_title");
        	language = bundle.getString("language");
        	videosFound = (ListView)findViewById(R.id.videos_found); 
    		question   = (TextView)findViewById(R.id.questionView);
    		//int qcoord[] = new int[2];
    	    //question.getLocationOnScreen(qcoord);
    		//float qy = question.getTop();
    		//float qx = question.getLeft();

    		
    		//added by Mike, 15 June 2015
    		//Reference: http://stackoverflow.com/questions/17945176/want-textview-to-change-color-with-click-just-like-on-a-button
    		//; last accessed: 15 June 2015; answer by Raghunandan
    		
    		//i put addChinDict here so that it does not have to run every time a user taps on next.
    		//I tested it, and it's slower to not put it in an arraylist. it's cleaner that way too.
    		//although if there's another solution without the arraylist that's more efficient, pls replace it in addChineseDictionary()
    		
    		//added by Lev, edited by Brent, 28 June 2015
    		clickable = false;
    		question.setOnTouchListener(new OnTouchListener() {
    			int start,end,overallLine,overallOffset;
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
		    		    	          overallLine = line;
		    		    	          overallOffset = offset;
		    		    	          //for (int i = partsList.size()-1; i >=0 ; i--)
		    		    	          Log.i(Integer.toString(offset),"OOOOOOFFSEETT");
		    		    	          for(int i = 0; i<partsList.size();i++)
		    		    	          {
		    		    	        	  start = questionDifficulty.indexOf(partsList.get(i));
		    		    	        	  end = start+partsList.get(i).length();
		    		    	        	  if (offset-start >= 0 && offset-end < 0)
		    		    	        	  {
		    		    	        		  SpannableStringBuilder def  = definitionsSsb.get(i);
		    		    	        		  actionItem.setTitleSpan(def);
		    		    	        		  quickAction.addActionItem(actionItem);
		    		    	        		 
		    		    	        		  quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
		    		    	        			  @Override
		    		    	        			  public void onItemClick(QuickAction source,int pos, int actionId) {
		    		    	        				     if (pos == 0) { //Add item selected
		    		    	        				          Toast.makeText(MainActivity.this, "Word copied to clipboard.", Toast.LENGTH_SHORT).show();
		    		    	        				     }
		    		    	        				 }
		    		    	        				});
		    		    	        		  quickAction.show(question,start,question.getLeft());
		    		    	        		  System.out.println("def here" + def);
		    		    	        		  spannable.setSpan(new ForegroundColorSpan(0xFFFFFFFF), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
<<<<<<< HEAD
		    		    	        		  spannable.setSpan(new BackgroundColorSpan(0xFFFF0000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    		    	        		  if(language.equalsIgnoreCase("japanese"))
		    		    	        			  break;
		    		    	        		  //Toast.makeText(getApplicationContext(), def,  Toast.LENGTH_SHORT).show();
=======
		    		    	        		  if (language.equalsIgnoreCase("Chinese"))
		    		    	        		  {
		    		    	        			  spannable.setSpan(new BackgroundColorSpan(0xFFFF0000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    		    	        			  //Toast.makeText(getApplicationContext(), def,  Toast.LENGTH_SHORT).show();
		    		    	        			  if(i==partsList.size()-1)
		    		    	        				  quickAction.show(question,(start+1));
		    		    	        		  }
		    		    	        		  else
		    		    	        			  quickAction.show(question,(i));
>>>>>>> master
		    		    	        	  }
		    		    	          }
		    		    	    }
		    		    	    return true;
	    			    }
	    			    if (event.getAction() == MotionEvent.ACTION_UP) {
				        //set to default color
	    			    	spannable.setSpan(new ForegroundColorSpan(0x93CCEA00),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			    	if (language.equalsIgnoreCase("Chinese"))
	    	        		  {
	    			    		spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			    		return true;
	    	        		  }
	    			    }
	    			    /*if (event.getAction() == MotionEvent.ACTION_MOVE) {
					        //set to default color
	    			    	
	    			    	Layout layout = ((TextView) v).getLayout();
	    			    		int x = (int)event.getX();
	    			    		int y = (int)event.getY();
	    			    		if (layout!=null){
		    		    	         int line = layout.getLineForVertical(y);
		    		    	         int offset = layout.getOffsetForHorizontal(line,x);
		    			    		if (offset>overallOffset)
		    			    		{
		    			    			Spannable[] spanArray = spannable.getSpans(start,overallOffset,Spannable.class);
		    			    			spannable.setSpan(new ForegroundColorSpan(0x93CCEA00),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    			    			spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    			    		}
	    			    		}
		    			    	return true;
			    			    	
		    			    }*/
    			    /*else if (event.getAction() == MotionEvent.ACTION_UP) {
    			        // set to default color
    		    		question.setTextColor(Color.parseColor("#acacab"));		

    		    		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    		    		ClipData clip = ClipData.newPlainText("text", question.getText().toString());
    		    		clipboard.setPrimaryClip(clip);
    		    		Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
    		    		
    			    }*/
    			}
    				}catch(Exception e){System.out.println("huehuehuehueahaue "+e);}    			
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
        	youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
    		youTubePlayerView.initialize(API_KEY, this);
        	if (difficulty.equalsIgnoreCase("easy"))
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	total = qm.getCount();//-1;//do a -1 because questionCounter starts at 0; added by Mike, 31 March 2015
        	System.out.println(">>>>TOTAL: "+total);
        	
//        	result.setText(""); //commented out by Mike, 27 March 2015
        	//answer.setText("Correct answer: "+ newQues.getCorrectAnswer());
        	
        	//added by Brent Anonas 28 June 2015
        	question.setText(questionDifficulty,BufferType.SPANNABLE);
        	spannable = (Spannable)question.getText();
        	answer.setText("");
        	translate1 = questionDifficulty;
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
        		if(addedDict)
        			chineseExecute();
        		else
        		{
        			addChineseDictionary();
        			chineseExecute();
        		}
        	}
        	//else if(language.equalsIgnoreCase("korean"))
        		
			return null;
		}
		
    	@Override
    	protected void onPostExecute(Void result)
    	{
    		try{
	    		for(int i = 0; i < partsList.size();i++)
	    		{
	    			int start = questionDifficulty.indexOf(partsList.get(i));
	    			int end = questionDifficulty.indexOf(partsList.get(i))+partsList.get(i).length();
	    			spannable.setSpan(new ForegroundColorSpan(0x93CCEA00),start ,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			//resizeQuestion();
	    			clickable=true;
	    			//overlapping search words. dont touch muna haha.
	    			//int overlap = overlap(partsList.get(i-1),partsList.get(i));
	    			//if(overlap>0)
	    			//	spannable.setSpan(new BackgroundColorSpan(0xFFFFFF00),start-overlap,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			//spannable.setSpan(new UnderlineSpan(), start, end-1, 0);
	    			//question.setText(spannable);
	    		}
    		}catch(Exception e){System.out.println("problem in post execute "+e);}
    	}
    }
    private void japExecute()
    {
    	try
    	{
	    	partsList.clear();
			definitionsSsb.clear();
			// TODO Auto-generated method stub
		    	String reply = "";
		    	String url = "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?9U";
		    	String data = "gloss_line="+ URLEncoder.encode(translate1,"UTF-8")+"&dicsel=9&glleng=60";
				reply = postFormDataToUrl(url, data);
		    	
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
					System.out.println("added fontList "+s);
					positionFont = reply.indexOf("<font color=\"blue\">", positionFont+19);
				}
				
				// print the <li> stuff
				int fontListIndex = 0;
				for (String s : liList)
				{	
					System.out.println("S here" + s);
					String[] parts = s.split(" ");
					s = s.replace(parts[1]+" ",parts[1]+"\n").replace("<br>", "\n").replace("�y","\n�y");
					SpannableStringBuilder ssb = new SpannableStringBuilder(s);
					ssb.setSpan(new RelativeSizeSpan(2f), s.indexOf(parts[1]),s.indexOf(parts[1])+parts[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					ssb.setSpan(new ForegroundColorSpan(0x93CCEA00), s.indexOf(parts[1]),s.indexOf(parts[1])+parts[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					//System.out.println("Text in li: "+tmp); 
		            //definitions.add(s);
		            definitionsSsb.add(ssb);
		            if(parts[1].equals("Possible"))
		            {
		            	partsList.add(fontList.get(fontListIndex));
		            	System.out.println("fontList "+fontList.get(fontListIndex));
		            	fontListIndex++;
		            }
		            else
		            	partsList.add(parts[1]);
		            System.out.println("parts"+parts[1]);
		            
				}
    	}catch(Exception e){System.out.println("THE PROBLEEEM "+e);}
    }
    
    private void chineseExecute()
	{
		partsList.clear();
		definitionsSsb.clear();
		//Log.i(translate1,"SADFSDFASDFASDFASFASDFASFASDFASDF");
		//searchPrefix(translate1,"");
		searchPrefix(translate1,"");
		for(int i = 0; i < partsList.size(); i++)
		{
			for (int j = i+1; j < partsList.size();)
			{
				ArrayList<String> innerOverlap = new ArrayList<String>();
				if (partsList.get(i).equals(partsList.get(j)))
					partsList.remove(i);
				else
				{
					if(partsList.get(i).length()<partsList.get(j).length())
					{
							String temp = partsList.get(j);
							partsList.set(j, partsList.get(i));
							partsList.set(i, temp);
					}
					if(partsList.get(i).contains(partsList.get(j)))
					{
						innerOverlap.add(partsList.get(j));
						partsList.remove(j);
					}
					j++;
				}
					
			}
		}
	}
    private void searchPrefix(String word,String result)
	{
    	//Log.i(translate1,"SADFSDFASDFASDFASFASDFASFASDFASDF");
		for(int i=1;i<=word.length();i++)
		{
			String prefix = word.substring(0,i);
			if (dictContains(prefix))
			{
				if(i==word.length())
				{
					result+=prefix;
					System.out.println("Result here" + result);
					//Log.i(result,"RESUUUUUUUUUUUULT");
					//definitionsSsb.add(result);
					
					break;
				}
				searchPrefix(word.substring(i,word.length()),result+prefix+"   ");
				}
		}
	}
	private boolean dictContains(String word)
	{
		

		//String[] dictionary = {"mobile","samsung","sam","sung","man","mango","icecream","and","go","i","love","ice","cream"};
		//chinDict is not that small huhu ambagal.
		for(String dict:chinDict)
		{
			if(!dict.contains("#"))
			{
			String splitDict[] = dict.split(" ");
			if(word.equals(splitDict[1]))
			{
				Log.i(dict,"DIIIIIIIIIIIICCCTT");
				partsList.add(splitDict[1]);
				SpannableStringBuilder ssb = new SpannableStringBuilder(dict);//.substring(dict.indexOf(splitDict[2]),dict.length()));
				definitionsSsb.add(ssb);
				return true;
			}
			}
		}
		return false;
	}
	@SuppressWarnings("resource")
	private void addChineseDictionary()
	{
		 URL url;
		 final String src = "https://raw.githubusercontent.com/SkycladObserver/CEdictSingkamas/master/cedict_ts.u8";			 
			try
				{
				    url = new URL(src);
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
					//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/cedict_ts.u8"),StandardCharsets.UTF_8.displayName()));
					String line;
					while((line = br.readLine())!=null)
					{
						 //System.out.println(line);
						chinDict.add(line);
						//line = br.readLine();
					}
					//System.out.println(dictionary.get(0));
				} catch(Exception e){System.out.println(e);}
			 Log.i("added mandarin dictionary","SADFSDFASDFASDFASFASDFASFASDFASDF");
			 addedDict = true;
	}
	/*private void koreanExecute()
	{
		try
    	{
	    	partsList.clear();
			definitionsSsb.clear();
			// TODO Auto-generated method stub
			//translate1 is the question
				String[] koreanline = translate1.split(" ");
				
				for (int i = 0; i < koreanline.length; i++)
				{
					String reply = "";
			    	String url = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=entry_idiom&query="+ koreanline[i];
				}
		    	String data = "gloss_line="+ URLEncoder.encode(translate1,"UTF-8")+"&dicsel=9&glleng=60";
				reply = postFormDataToUrl(url, data);
		    	
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
					System.out.println("added fontList "+s);
					positionFont = reply.indexOf("<font color=\"blue\">", positionFont+19);
				}
				
				// print the <li> stuff
				int fontListIndex = 0;
				for (String s : liList)
				{	
					System.out.println("S here" + s);
					String[] parts = s.split(" ");
					s = s.replace(parts[1]+" ",parts[1]+"\n").replace("<br>", "\n").replace("�y","\n�y");
					SpannableStringBuilder ssb = new SpannableStringBuilder(s);
					ssb.setSpan(new RelativeSizeSpan(2f), s.indexOf(parts[1]),s.indexOf(parts[1])+parts[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					ssb.setSpan(new ForegroundColorSpan(0x93CCEA00), s.indexOf(parts[1]),s.indexOf(parts[1])+parts[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					//System.out.println("Text in li: "+tmp); 
		            //definitions.add(s);
		            definitionsSsb.add(ssb);
		            if(parts[1].equals("Possible"))
		            {
		            	partsList.add(fontList.get(fontListIndex));
		            	System.out.println("fontList "+fontList.get(fontListIndex));
		            	fontListIndex++;
		            }
		            else
		            	partsList.add(parts[1]);
		            System.out.println("parts"+parts[1]);
		            
				}
    	}catch(Exception e){System.out.println("THE PROBLEEEM "+e);}
	}*/

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
        		questionDifficulty = parts[0];
        	else
        		questionDifficulty = parts[1];	
        	//question.setText("Hello");
        	//answer.setText("World");
        	
        	//added by Brent Anonas, 28 June 2015
        	question.setText(questionDifficulty);
        	spannable = (Spannable)question.getText();
        	translate1 = questionDifficulty;
        	if (language.equalsIgnoreCase("japanese"))
        		new DictionaryTask().execute();
        	else if (language.equalsIgnoreCase("mandarin"))
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