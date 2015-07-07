package usbong.android.questionloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LoadingScreen extends Activity {

	String songname;
	String language;
	String difficulty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);
		Bundle bundle = getIntent().getExtras();
    	songname = bundle.getString("song_title");
    	language = bundle.getString("language");
    	difficulty = bundle.getString("difficulty");
	}
}
