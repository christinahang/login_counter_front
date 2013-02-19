package edu.berkeley.cs169.christinahang.logincounter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	// Get the message box.
	private TextView msg;
	
	// Set uri for login and add user with heroku server
	private static final String loginUri = "http://gentle-river-6547.herokuapp.com/users/login";
	private static final String addUri = "http://gentle-river-6547.herokuapp.com/users/add";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		msg = (TextView)findViewById(R.id.message);
		// This message should appear in the message box when activity starts.
		msg.setText("Please enter your credentials below.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private class LoginCounter extends AsyncTask<String, Void, JSONObject> {
		
		// Error code constants
		public static final int SUCCESS = 1;
		public static final int ERR_BAD_CREDENTIALS = -1;
		public static final int ERR_USER_EXISTS = -2;
		public static final int ERR_BAD_USERNAME = -3;
		public static final int ERR_BAD_PASSWORD = -4;
		
		// user is the input username and pass is the input password
		private String user;
		private String pass;
		
		@Override
		protected void onPreExecute() {
			EditText userInput = (EditText)findViewById(R.id.user);
			EditText passInput = (EditText)findViewById(R.id.pass);
			// Get the user's inputs
			user = userInput.getText().toString();
			pass = passInput.getText().toString();
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// After getting the user's inputs, send inputs as json object to correct uri
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);
			JSONObject dataSent = new JSONObject();
			// set up json object for sending
			try {
				dataSent.put("user", user);
				dataSent.put("password", pass);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				// set json object as data
				StringEntity input = new StringEntity(dataSent.toString());
				post.setEntity(input);
				
				// set header for json data
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
				
				// send post request and get response
				HttpResponse resp = client.execute(post);
				InputStream in = resp.getEntity().getContent();
				
				// convert response from HttpResponse to string
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sbuilder = new StringBuilder();
				String ln;
				while ((ln = reader.readLine()) != null) {
					sbuilder.append(ln + "\n");
				}
				String response = sbuilder.toString();
				
				// convert string response into json object to go to onPostExecute
				JSONObject dataRecv = new JSONObject(response);
				return dataRecv;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				// extract error code from json response
				int errCode = result.getInt("errCode");
				
				// display message according to error code
				switch (errCode) {
				case SUCCESS:
					// on SUCCESS go to welcome screen with username and count
					goToWelcomeScreen(user, result.getInt("count"));
					break;
				case ERR_USER_EXISTS:
					msg.setText("This user name already exists. Please try again.");
					break;
				case ERR_BAD_CREDENTIALS:
					msg.setText("Invalid username and password combination. Please try again.");
					break;
				case ERR_BAD_USERNAME:
					msg.setText("The user name should be non-empty and at most 128 characters long. Please try again.");
					break;
				case ERR_BAD_PASSWORD:
					msg.setText("The password should be at most 128 characters long. Please try again.");
					break;
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void goToWelcomeScreen(String user, int count) {
		// start a new activity with the username and count
		Intent i = new Intent(this, LoggedInActivity.class);
		i.putExtra("user", user);
		i.putExtra("count", count);
		startActivity(i);
	}
	
	public void login(View v) {
		// when login button clicked, post request directed to login uri
		new LoginCounter().execute(loginUri);
	}
	
	public void add(View v) {
		// when add button clicked, post request directed to add uri
		new LoginCounter().execute(addUri);
	}

}
