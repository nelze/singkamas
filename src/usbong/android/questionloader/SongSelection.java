package usbong.android.questionloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import usbong.android.utils.UsbongUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SongSelection extends ListActivity {
	String language;
	Button review;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
    	language= bundle.getString("language");
    	
    	
        try
        {
        	String[] fileList = getResources().getAssets().list(language);
        	System.out.println("filelist"+Arrays.toString(fileList));
        	//System.out.println(Arrays.toString(fileList));
        	
        	MyListAdapter adapter = new MyListAdapter(fileList);        	
        	setListAdapter(adapter);        	
        	
        	ListView list = getListView();        	
        	list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					arg1.setBackgroundColor(Color.parseColor("#4e4b3c"));
					
					// TODO Auto-generated method stub
					TextView textPlaceHolder = (TextView) arg1.findViewById(R.id.textViewPlaceHolder);
					//System.out.println(text.getText());
					if (language.equalsIgnoreCase("Japanese"))
					{
						Intent intent = new Intent(SongSelection.this, SettingSelection.class);
						intent.putExtra("song_title", textPlaceHolder.getText());
						intent.putExtra("language", language);
						//System.out.println("LAnguage"+language);
						startActivity(intent);
						SongSelection.this.finish();
					}
					else if (language.equalsIgnoreCase("Mandarin"))
					{
						Intent intent = new Intent(SongSelection.this, SettingSelection.class);
						intent.putExtra("song_title", textPlaceHolder.getText());
						intent.putExtra("language", language);
						//System.out.println("LAnguage"+language);
						startActivity(intent);
						SongSelection.this.finish();
					}
					else
					{
						Intent intent = new Intent(SongSelection.this, MainActivity.class);
				    	intent.putExtra("difficulty", "easy");
				    	intent.putExtra("song_title", textPlaceHolder.getText());
				    	intent.putExtra("language", language);
				    	startActivity(intent);
				    	SongSelection.this.finish();
				    	
					}
					
				}
			});
    		
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
				builder.setMessage(UsbongUtils.readTextFileInAssetsFolder(SongSelection.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
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
    
    
    private class MyListAdapter extends BaseAdapter
    {
    	private String[] fileList;
    	
    	public MyListAdapter(String[] fileList)
    	{
    		this.fileList = fileList;
    	}
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fileList.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return fileList[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			LayoutInflater inflater = getLayoutInflater();
			View view = null;
			
			if (convertView==null)
			{
				view = inflater.inflate(R.layout.song_selection, null);
			}
			else
			{
				view = convertView;
			}
			
			ImageView image = (ImageView) view.findViewById(R.id.imageView1);
			TextView text = (TextView) view.findViewById(R.id.textView1);			
			TextView textPlaceHolder = (TextView) view.findViewById(R.id.textViewPlaceHolder);			
			
			// set the image here
			// image.setImageBitmap(bm)
			//System.out.println(text);
			// set the text
//			text.setText(fileList[position]); //commented out by Mike, 30 March 2015
			textPlaceHolder.setText(fileList[position]); //added by Mike, 30 March 2015
			
			//added by Mike, 30 March 2015
	        try
	        {
	        	InputStream is = getResources().getAssets().open(language+"/" + fileList[position]);        	
	        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        	String currentLine=br.readLine();
	        	text.setText(currentLine+"\n"+br.readLine());	        	
/*
	        	String currentLine;
	        	while((currentLine=br.readLine())!=null){
	        		text.setText(currentLine+"\n");
	        	}
*/	        	
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
			
			return view;
		}
    	
    }
    
    
    
    
}
