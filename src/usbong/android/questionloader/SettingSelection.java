package usbong.android.questionloader;

import java.io.InputStream;

import usbong.android.questionloader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingSelection extends Activity {
	
	
	String songname;
	String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
        
    	
        setContentView(R.layout.setting_selection);
        
        Bundle bundle = getIntent().getExtras();
    	songname = bundle.getString("song_title");
    	language = bundle.getString("language");
    	
    		
        
    }

    
    
    public void easy(View view)
    {
    	
    	Intent intent = new Intent(SettingSelection.this, MainActivity.class);
    	intent.putExtra("difficulty", "easy");
    	intent.putExtra("song_title", songname);
    	intent.putExtra("language", language);
    	startActivity(intent);
    }
    
    public void medium(View view)
    {
    	Intent intent = new Intent(SettingSelection.this, MainActivity.class);
    	intent.putExtra("difficulty", "medium");
    	intent.putExtra("song_title", songname);
    	intent.putExtra("language", language);
    	startActivity(intent);
    	SettingSelection.this.finish();
    	
    }
    
    
    
}
