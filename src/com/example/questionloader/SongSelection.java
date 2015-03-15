package com.example.questionloader;

import java.io.InputStream;
import java.util.Arrays;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SongSelection extends ListActivity {
	
	
	String language;
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
					// TODO Auto-generated method stub
					TextView text = (TextView) arg1.findViewById(R.id.textView1);
					//System.out.println(text.getText());
					if (language.equalsIgnoreCase("Japanese"))
					{
						Intent intent = new Intent(SongSelection.this, SettingSelection.class);
						intent.putExtra("song_title", text.getText());
						intent.putExtra("language", language);
						//System.out.println("LAnguage"+language);
						startActivity(intent);
					}
					else
					{
						Intent intent = new Intent(SongSelection.this, MainActivity.class);
				    	intent.putExtra("difficulty", "easy");
				    	intent.putExtra("song_title", text.getText());
				    	intent.putExtra("language", language);
				    	startActivity(intent);
				    	
					}
					
				}
			});
    		
        }
        catch(Exception e)
        {
        	e.printStackTrace();
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
			
			
			// set the image here
			// image.setImageBitmap(bm)
			//System.out.println(text);
			// set the text
			text.setText(fileList[position]);
			
			
			
			
			return view;
		}
    	
    }
    
    
    
}
