package edu.berkeley.cs169.christinahang.logincounter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LoggedInActivity extends Activity {
	
	private TextView welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged_in);
		
		// get username and count from calling intent (main screen)
		Bundle extras = this.getIntent().getExtras();
		String user = extras.getString("user");
		int count = extras.getInt("count");
		
		// display welcome message for this user and log in count
		String msg = "Welcome " + user + "\n" + "You have logged in " + count + " times.";
		welcome = (TextView)findViewById(R.id.welcome);
		welcome.setText(msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_logged_in, menu);
		return true;
	}
	
	public void logout(View v) {
		// when logout button clicked, go back to main screen
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

}
