package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.youtube.player.YouTubePlayerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView.BufferType;
import android.widget.Toast;

public class LoadingScreen extends Activity {

	ArrayList<DictionaryEntry> dictEntries = new ArrayList<DictionaryEntry>();
	String songname;
	String language;
	String difficulty;
	String questionDifficulty;
	String questionDifficultyFinal;
	String translate1;
	File file;
	PrintWriter pw;
	QuestionManager qm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);
		Bundle bundle = getIntent().getExtras();
    	songname = bundle.getString("song_title");
    	language = bundle.getString("language");
    	difficulty = bundle.getString("difficulty");
    	try
    	{
    		file = new File(Environment.getExternalStorageDirectory()+File.separator+songname+".skm");//+"/Singkamas Files/"
			if (!file.exists()) 
			{
	    		file.createNewFile();
	    		System.out.println(file.getAbsolutePath()+"hueheuheueuehuehhuheuehufhuewhefsfse");
    			pw = new PrintWriter(file);
	         	InputStream isE = getResources().getAssets().open(language+"/" + songname);
	         	qm = new QuestionManager();
	         	qm.loadQuestions(isE, Question.DIFFICULTY_EASY);
	         	System.out.println(qm.getCount()+"qmCOUUUUUUUUUUNT");
	         	for(int i = 0; qm.getQuestion(Question.DIFFICULTY_EASY,i)!=null ; i++)
	         	{
	         		System.out.println("qm huehue"+i);
	         		Question newQues = qm.getQuestion(Question.DIFFICULTY_EASY,i);
	         		String string = newQues.getQuestionText();
	         		String[] parts = string.split("~");
	         		if (difficulty.equalsIgnoreCase("easy"))
	             	{
	             		questionDifficultyFinal = parts[0].replace("　","").replace(" ", "");
	             		questionDifficulty = "　"+parts[0].replace(" ", "　");
	             		translate1 = language.equalsIgnoreCase("japanese") ? "　"+parts[1].replace(" ", "　"):"　"+parts[0].replace(" ", "　");
		             	}
	             	else
	             	{
	             		questionDifficultyFinal = parts[1].replace("　", "").replace(" ", "");
	             		questionDifficulty = "　"+parts[1].replace(" ", "　");
	             		translate1 = questionDifficulty;
	             	}
			    	colorJapParticles();
		    		DictionaryTask dt = new DictionaryTask();
		    		dt.execute();
		    		while(dt.getStatus() != AsyncTask.Status.FINISHED)
		    		{
		    			try
		    			{
		    				System.out.println("pause");
		    				Thread.sleep(100);
		    			}catch(Exception e)
		    			{
		    				e.printStackTrace();
		    			}
		    		}
	         	}
	        }
			else
			{
	    		Toast.makeText(getApplicationContext(), "go to next class!",Toast.LENGTH_LONG).show();
			}
    	}
	    catch(Exception e)
	    {
	      	System.out.println(e+"lol error");
	       	e.printStackTrace();
	    }
	}
	private class DictionaryTask extends AsyncTask<String,String,String>
    {
		//ProgressDialog progressDialog;
		//@Override
	    //protected void onPreExecute() {
	    //    progressDialog = new ProgressDialog(LoadingScreen.this);
	    //    progressDialog.setMessage("Downloading...");
	    //    progressDialog.setCancelable(false);
	     //   progressDialog.show();
	   //}
		@Override
		protected String doInBackground(String... params) {
			//EXECUTE THEM ALL!
        	if (language.equalsIgnoreCase("japanese"))
        		japExecute();
        	/*else if (language.equalsIgnoreCase("mandarin"))
        	{
        		if(addedDict)
        			chineseExecute();
        		else
        		{
        			addChineseDictionary();
        			chineseExecute();
        		}
        	}*/
        	//else if(language.equalsIgnoreCase("korean"))
        		
			return null;
		}
    	//@Override
    	protected void onPostExecute(Void result)
    	{
	    	System.out.println(dictEntries.size()+"SIZE OF DICT");
			for(int i = 0; i < dictEntries.size();)
			{
				int start = dictEntries.get(i).start();
				int end = dictEntries.get(i).end();
				if(start != -1)
				{
					if(i%2==0&&dictEntries.get(i).getColor()==-1)
					{
						pw.write(dictEntries.get(i).getWord()+"|"+dictEntries.get(i).getDefinition()+"|"+"0xFFD7FF77"+"|"+start+"|"+end+"\n");
						System.out.println("added in here"+i);
					}
					else if(dictEntries.get(i).getColor()==-1)
					{
						pw.write(dictEntries.get(i).getWord()+"|"+dictEntries.get(i).getDefinition()+"|"+"0x93CCEA00"+"|"+start+"|"+end+"\n");
						System.out.println("added in here"+i);
					}
					else
					{
						pw.write(dictEntries.get(i).getWord()+"|"+dictEntries.get(i).getDefinition()+"|"+dictEntries.get(i).getColor()+"|"+start+"|"+end+"\n");
						System.out.println("added in here"+i);
					}
					i++;
					pw.flush();
				}
				else
				{
					dictEntries.remove(i);
				}
				Toast.makeText(getApplicationContext(), "IT IZZ DOONE",Toast.LENGTH_LONG).show();
			}
    	}
    }
	private boolean inQuestion(String query,String def,int color)
    {
    	if(questionDifficulty.contains(query))
		{
    		Log.i(query,"added huehuehuheuue");
			Log.i(def,"thisistoSsbhuehuehuheuue");
    		int position = questionDifficulty.indexOf(query);
    		while(position!=-1)
    		{
    			String s = questionDifficulty.substring(0,position);
    			int spaces = s.length() - s.replace("　", "").length();
    			dictEntries.add(new DictionaryEntry(query.replace("　",""),def,color,position-spaces,position+query.replace("　","").length()-spaces));
    			position = questionDifficulty.indexOf(query, position+query.length());
    		}
			return true;
		}
    	return false;
    }
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
    			else
    				wholeWord = questionDifficulty.substring(questionDifficulty.indexOf(query),questionDifficulty.indexOf(query)+query.length());
    			dictEntries.add(new DictionaryEntry(wholeWord,def,-1,position,position+wholeWord.length()));
    			position = questionDifficultyFinal.indexOf(query, position+wholeWord.length());
    		}			
			return true;
		}
    	return false;
    }
    private void colorJapParticles()
    {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("particles.txt")));
			String line;
			while((line = br.readLine())!=null)
			{
				String[] parts = line.split("~");
				int color = line.length()-line.replace("~","").length()==2 ? -1 : 0xFFAEEEEE;
				if(inQuestion("　"+parts[0]+"　",parts[1],color))
					System.out.println("added"+parts[0]);
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
    	}catch(Exception e){System.out.println("THE PROBLEEEM ");
    	e.printStackTrace();}
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
   	    }   		finally
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
}