package com.CAI.wordscramble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.LinearLayout;

import android.graphics.Typeface;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.content.Context;

import android.view.View.OnClickListener;

import android.graphics.Typeface;

public class SummaryActivity extends Activity {
	
	private HashMap<String,String> dictionaryMap = new HashMap<String,String>();
	private ArrayList<String> validWordStrings = new ArrayList<String>(); //Stores valid permutations of letters that form words
	private ArrayList<String> usedWordStrings = new ArrayList<String>();
	
	private int score;
	private String grade;
	
	private LinearLayout scrollViewLinearLayout;
	private TextView definitionTextView;
	private TextView scoreSummaryTextView;
	private ImageView gradeImageView;
	private ImageButton restartImageButton;
	
	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		dictionaryMap = MainActivity.dictionaryMap;
		validWordStrings = (ArrayList<String>)getIntent().getSerializableExtra("VALID_WORDS");
		usedWordStrings = (ArrayList<String>)getIntent().getSerializableExtra("USED_WORDS");
		score = getIntent().getIntExtra("SCORE", 0);
		
		scrollViewLinearLayout = (LinearLayout)findViewById(R.id.scrollViewLinearLayout);
		definitionTextView = (TextView)findViewById(R.id.definitionTextView);
		scoreSummaryTextView = (TextView)findViewById(R.id.scoreSummaryTextView);
		restartImageButton = (ImageButton)findViewById(R.id.restartImageButton);
		
		loadingResources();
		
	}
	
	private void loadingResources(){
		
		//using the Typeface for otf loaded in MainActivity and set Typeface
		font = MainActivity.font;
		definitionTextView.setTypeface(font);
		scoreSummaryTextView.setTypeface(font);
		
		//load grade images
	}
	
	protected void onStart(){
		super.onStart();
		
		//scoreSummaryTextView & gradeImageView
		grade = findGrade(score);
		
		int used = usedWordStrings.size();
		int total = validWordStrings.size();
		scoreSummaryTextView.setText(String.format("Score:%d\nGrade:%s\nStats:%d/%d",score,grade,used,total));
		
		//Get a reference to the LayoutInflater service so we can "inflate" new Buttons
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    for (String word:validWordStrings){
	    	TextView wordTextView = (TextView)inflater.inflate(R.layout.word_view, null);
	    	//wordTextView.setTypeface(font);
	    	wordTextView.setText(word);
	    	
	    	if(usedWordStrings.contains(word))
	    		wordTextView.setTypeface(font);
	    		//wordTextView.setTypeface(null,Typeface.BOLD);
	    	
	    	wordTextView.setOnClickListener(new blockListener());
	    	scrollViewLinearLayout.addView(wordTextView);
	    }
	    restartImageButton.setOnClickListener(new blockListener());
	}
	
	private class blockListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	    	if (v.getId()==restartImageButton.getId()){
	    		finish();
	    	}else{
	    		TextView temp = (TextView)v;
	    		definitionTextView.setText(dictionaryMap.get(temp.getText()));
	    	}
	    }
	}
	
	private String findGrade(int points){
		if (points<5000){
			return "C";
		}else if (points>10000){
			return "A";
		}else{
			return "B";
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
