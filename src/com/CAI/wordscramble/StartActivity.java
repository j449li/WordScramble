package com.CAI.wordscramble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

/**
 * This is the starting activity of the CAI application.
 * It contains the instructions for the CAI application.
 * 
 * The application should be launched from this class.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class StartActivity extends Activity{
	private TextView instructionTextView;
	private Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu);
		
		// Get references to components that we will need to update
		instructionTextView=(TextView)findViewById(R.id.instructionTextView);
		startButton=(Button)findViewById(R.id.startButton);
		
		//instructions
		instructionTextView.setText("WELCOME TO WORD-SCRAMBLE!\n" +
				"	You will be given 6 letters, and the objective is to form as many words with them as possible." +
				"By default the difficulty is set to normal, and you will have a 90 seconds time limit." +
				"The difficulty settings can be changed in the menu once the CAI has started.\n\n" +
				"Interface:\n" +
				"	>Select letters to form words, which appears in the entryField above the space bar. Letters may also be deselected.\n" +
				"	>The space bar shuffles the letters and also clears any letters in the entryField.\n" +
				"	>To submit a word, simply tap anywhere else on the screen.\n\n"+
				"Scoring:\n" +
				"	>100*the scrabble value of the letter for each letter in every word formed will be added to the score.\n" +
				"	>Every 6-lettered word formed will be given a 500 point bonus.\n\n" +
				"Summary:\n" +
				"	>After the time runs out, a summary screen will be shown\n" +
				"	>A list of all the possible words will be given along with their definition\n\n" +
				"Good Luck!");
		
		//add listener for startButton
		startButton.setOnClickListener(new blockListener());
	}
	
	//Listener for the startButton
	private class blockListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	    	//start MainActivity
	    	Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(mainIntent);
	    }
	}
}
