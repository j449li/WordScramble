package com.CAI.wordscramble;

import java.io.IOException;
import java.io.InputStream;
import android.content.res.AssetManager;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Typeface;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.view.LayoutInflater;
import android.content.Context;

import android.view.View.OnClickListener;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * This is the SummaryActivity for the CAI application.
 * 
 * This is where the user's performance is summarised. 
 * A list of all the possible words is also given along 
 * with their definition to help students improve their
 * vocabulary and spelling.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class SummaryActivity extends Activity {
	
	private HashMap<String,String> dictionaryMap = new HashMap<String,String>();
	private ArrayList<String> validWordStrings = new ArrayList<String>(); //Stores valid permutations of letters that form words
	private ArrayList<String> usedWordStrings = new ArrayList<String>();
	
	private int score;
	
	//layout widgets
	private LinearLayout scrollViewLinearLayout;
	private TextView definitionTextView;
	private TextView scoreSummaryTextView;
	private ImageView gradeImageView;
	private ImageButton restartImageButton;
	
	private Typeface font;
	
	private TextView pre; //keeps track of the previous TextView selected so that it can be unselected

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		// Get references to Ecomponents that we will need to update
		scrollViewLinearLayout = (LinearLayout)findViewById(R.id.scrollViewLinearLayout);
		definitionTextView = (TextView)findViewById(R.id.definitionTextView);
		scoreSummaryTextView = (TextView)findViewById(R.id.scoreSummaryTextView);
		gradeImageView = (ImageView)findViewById(R.id.gradeImageView);
		restartImageButton = (ImageButton)findViewById(R.id.restartImageButton);
		
		loadingResources();
		
	}
	
	//load data passed from MainActivity
	private void loadingResources(){
		
		dictionaryMap = MainActivity.dictionaryMap;  //getting dictionary through static referencing
		validWordStrings = (ArrayList<String>)getIntent().getSerializableExtra("VALID_WORDS");
		usedWordStrings = (ArrayList<String>)getIntent().getSerializableExtra("USED_WORDS");
		score = getIntent().getIntExtra("SCORE", 0);
		
		//using the Typeface for otf loaded in MainActivity and set Typeface
		font = MainActivity.font; //getting font through static reference
		scoreSummaryTextView.setTypeface(font);

	}
	
	//Setups the components of SummaryActivity
	@Override
	protected void onStart(){
		super.onStart();
		
		//scoreSummaryTextView & gradeImageView
		String grade = findGrade(score);
		loadGradeImageView(grade);
		
		int used = usedWordStrings.size();
		int total = validWordStrings.size();
		scoreSummaryTextView.setText(String.format("Score:%d\nGrade:%s\nStats:%d/%d",score,grade,used,total));
		
		//Get a reference to the LayoutInflater service so we can "inflate" new Buttons
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    int preword_length = 2; //keeps track of the length of the previous word
	    
	    for (String word:validWordStrings){
	    	//add a TextView that separates words based on their size
	    	if (word.length()!=preword_length){
	    		TextView wordLengthTextView = (TextView)inflater.inflate(R.layout.word_view, null);
	    		wordLengthTextView.setTypeface(null,Typeface.BOLD_ITALIC);
	    		wordLengthTextView.setClickable(false);
	    		wordLengthTextView.setBackgroundColor(Color.GRAY);
	    		wordLengthTextView.setText(String.format("%d-Lettered Word(s)",word.length()));
	    		
	    		scrollViewLinearLayout.addView(wordLengthTextView);
	    		
	    		preword_length = word.length();
	    	}
	    	
	    	//add the word to the linearLayout of the scrollview
	    	TextView wordTextView = (TextView)inflater.inflate(R.layout.word_view, null);
	    	wordTextView.setText(word);
	    	
	    	//If the user was able to form this word, use the loaded font
	    	if(usedWordStrings.contains(word)){
	    		wordTextView.setTypeface(font);
	    	}
	    	
	    	//set listener for the TextView, and add it to the linear-layout of the scrollview
	    	wordTextView.setOnClickListener(new blockListener());
	    	scrollViewLinearLayout.addView(wordTextView);
	    }
	    restartImageButton.setOnClickListener(new blockListener());
	}
	
	//Private listener inner-class for clickable views
	private class blockListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	    	if (v.getId()==restartImageButton.getId()){
	    		finish();
	    	}else{
	    		String word = (String)((TextView)v).getText();
	    		definitionTextView.setText(
	    				String.format("%s:\n\n%s",word,dictionaryMap.get(word)));
	    		
	    		//unhighlight the previously selected TextView
	    		if (pre!=null){
	    			pre.setBackgroundColor(Color.TRANSPARENT);
	    		}
	    		
	    		pre = (TextView)v; //set the newly highlighted TextView to the previous
	    		
	    		//highlight the selected TextView
	    		v.setBackgroundColor(Color.WHITE);

	    	}
	    }
	}
	
	//Determines the grade level achieved by the user
	private String findGrade(int points){
		if (points<4000){
			return "C";
		}else if (points>8000){
			return "A";
		}else{
			return "B";
		}
	}
	
	//Load the image associated with their grade
	private void loadGradeImageView(String grade){
		//Loading images and setting up database
		AssetManager assets = getAssets();
		
		InputStream stream;
	    	
		try {
		    //Get an InputStream to the asset representing the next grade image
		    stream = assets.open("grades/"+grade+".gif");
		    //Load the asset as a Drawable
		    Drawable image = Drawable.createFromStream(stream, grade);
		    gradeImageView.setImageDrawable(image);
		}catch (IOException e) {
		    Log.e("Summary Intent", "Error Loading " + grade+".gif", e);

	    }
	}

}
