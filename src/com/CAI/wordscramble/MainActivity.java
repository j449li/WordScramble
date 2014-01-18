package com.CAI.wordscramble;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import android.view.LayoutInflater;
import android.content.Context;

import android.view.View;
import android.view.View.OnClickListener;

import android.os.CountDownTimer;

import java.io.IOException;
import android.util.Log;

import android.content.Intent;
import 	android.graphics.Typeface;

public class MainActivity extends Activity {
	private final int LEVEL=20;
	private final int BLOCKS=6;
	private final String VOWEL="aeiou";
	private final String CONSONANT="bcdfghjklmnpqrstvwxyz";
	private final String ALPHA="abcdefghijklmnopqrstuvwxyz";
	
	//Keeps track of the user's score
	private ScoreKeeper keeper;
	
	//Timer instance variables
	private int time; //time in seconds left
	private CountDownTimer timer;
	
	private Random randomGen=new Random();
	
	private char[] letterStrings = new char[BLOCKS];
	private ArrayList<String> validWordStrings = new ArrayList<String>(); //Stores valid permutations of letters that form words
	private ArrayList<String> usedWordStrings = new ArrayList<String>();
	private HashMap<Character,Drawable> imageBlocksMap = new HashMap<Character,Drawable>();
	
	//otf
	protected static Typeface font;
	
	//database
	protected static HashMap<String,String> dictionaryMap = new HashMap<String,String>();
	private ArrayList<String> wordList = new ArrayList<String>();
	private BinarySearch searcher = new BinarySearch();
	
	//screen widgets
	private ImageButton shuffleImageButton;
	private TextView timerTextView;
	private TextView scoreTextView;
	private TextView entryStatusTextView;
	private LinearLayout entryFieldLinearLayout;
	private LinearLayout letterFieldLinearLayout;
	private TableLayout mainTableLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		shuffleImageButton=(ImageButton)findViewById(R.id.shuffleImageButton);
		timerTextView=(TextView)findViewById(R.id.timerTextView);
		scoreTextView=(TextView)findViewById(R.id.scoreTextView);
		entryStatusTextView = (TextView)findViewById(R.id.entryStatusTextView);
		
		entryFieldLinearLayout=(LinearLayout)findViewById(R.id.entryFieldLinearLayout);
		letterFieldLinearLayout=(LinearLayout)findViewById(R.id.letterFieldLinearLayout);
		
		mainTableLayout=(TableLayout)findViewById(R.id.mainTableLayout);
	    mainTableLayout.setClickable(true);
	    
	    //Adding listeners
	    mainTableLayout.setOnClickListener(new blockListener());
	    shuffleImageButton.setOnClickListener(new blockListener());
		
		loadingResources();
	    
	}
	
	protected void loadingResources(){
		//Loading images and setting up database
		AssetManager assets = getAssets();
	    
		//load otf and set Typeface to the new font
		font = Typeface.createFromAsset(assets,"font/20db.otf");
		timerTextView.setTypeface(font);
		entryStatusTextView.setTypeface(font);
		scoreTextView.setTypeface(font);
	    
	    //reading from /letters
	    Character letter;
	    InputStream stream;
	    for (int i=0;i<ALPHA.length();i++){
	    	letter=ALPHA.charAt(i);
	    	
		    try {
		        //Get an InputStream to the asset representing the next letter image
		        stream = assets.open("letters/"+letter+".jpg");
		        //Load the asset as a Drawable
		        Drawable image = Drawable.createFromStream(stream, letter+"");
		        imageBlocksMap.put(letter,image);
		    }
		    catch (IOException e) {
		        Log.e("Logo Quiz App", "Error Loading " + letter+".jpg", e);
		    }
	    }
	    
	    //reading from /dictionary
	    try {
		    InputStream wordFile = assets.open("dictionary/word.txt");
		    InputStream defnFile = assets.open("dictionary/defn.txt");
		    BufferedReader bufWord = new BufferedReader(new InputStreamReader(wordFile)); 
		    BufferedReader bufDefn = new BufferedReader(new InputStreamReader(defnFile));
		    String nextWord = bufWord.readLine().trim().toLowerCase();
		    String nextDefn = bufDefn.readLine().trim();
		    int count=24815;
		    
		    do{
		    	dictionaryMap.put(nextWord,nextDefn);
		    	wordList.add(nextWord);
		    	nextWord=bufWord.readLine().trim().toLowerCase();
		    	nextDefn=bufDefn.readLine().trim();
		    	count--;
		    }while (count!=0);
		    
		    bufWord.close();
		    bufDefn.close();
		    wordFile.close();
		    defnFile.close();
		    
	    } catch (IOException e){
	    	Log.e("Logo Quiz App", "Error Loading " + "word.txt & defn.txt", e);
	    }
	    
	}
	
	protected void onStart(){
		super.onStart();
		//Generating letters for the problem set
	    //Change this into a more efficient algorithm (takes too long to load)
		validWordStrings.clear();
		validWordStrings = new ArrayList<String>();
	    usedWordStrings.clear();
	    
	    Permute permuter = new Permute();
	    do{
	    	validWordStrings.clear();
	    	
		    int numOfVows = randomGen.nextInt(3)+1;
		    
		    for (int i=0;i<numOfVows;i++){
		    	letterStrings[i]=VOWEL.charAt(randomGen.nextInt(5));
		    }
		    
		    for (int i=numOfVows;i<BLOCKS;i++){
		    	letterStrings[i]=CONSONANT.charAt(randomGen.nextInt(21));
		    }
		    
		    Arrays.sort(letterStrings);
		    ArrayList<String>permList=new ArrayList<String>();
		    permList = permuter.getPerms(letterStrings);
		    for (String perm:permList){
		    	if (searcher.search(perm,wordList)&&!validWordStrings.contains(perm)){
		    		validWordStrings.add(perm);
		    	}
		    }
		    
	    }while(validWordStrings.size()<LEVEL);
	    
	    //Shuffle and Inflate the letters
	    shuffle();
	    
	    //Timer
	    time = 50*1000; //in millisecond
	    timer = new CountDownTimer(time,1000){
	    	public void onTick(long msLeft){
	    		int minutes = (int)msLeft/1000/60;
	    		int seconds = (int)msLeft/1000%60;
	    		timerTextView.setText(String.format("%d:%02d", minutes, seconds));
	    	}
	    	
	    	public void onFinish(){
	    		timer.cancel();
	    		startSummaryIntent();
	    	}
	    };
	    
	    timer.start();
	    
	    keeper = new ScoreKeeper();
	    entryStatusTextView.setText("");
	    
	}
	
	private void shuffle(){
		entryFieldLinearLayout.removeAllViews();
		letterFieldLinearLayout.removeAllViews();
		
		//Shuffling the letterStrings Array
		for (int i=0;i<BLOCKS;i++){
			int newIndex = randomGen.nextInt(BLOCKS);
			char letter = letterStrings[i];
			letterStrings[i] = letterStrings[newIndex];
			letterStrings[newIndex] = letter;
		}
		
		//Get a reference to the LayoutInflater service so we can "inflate" new Buttons
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    for (int i=0;i<BLOCKS;i++){
	    	ImageButton letterBlock = (ImageButton)inflater.inflate(R.layout.blocks_layout, null);
	    	letterBlock.setTag(letterStrings[i]);
	    	letterBlock.setImageDrawable(imageBlocksMap.get(letterStrings[i]));
	    	letterBlock.setOnClickListener(new blockListener());
	    	letterFieldLinearLayout.addView(letterBlock);
	    }
	}
	
	public void submitEntry(){
		String entryWord = "";
		int entrySize = entryFieldLinearLayout.getChildCount();
		
		if (entrySize<3){
			entryStatusTextView.setText("At least 3 letters");
			return;
		}
		
		for (int i=0;i<entrySize;i++){
			View tempBlock = entryFieldLinearLayout.getChildAt(i);
			entryWord+=tempBlock.getTag();
		}
		
		if (searcher.search(entryWord,wordList) && !usedWordStrings.contains(entryWord)){
			entryStatusTextView.setText("VALID: "+entryWord);
			keeper.add(entryWord);
			scoreTextView.setText(keeper.getScore()+"");
			
			usedWordStrings.add(entryWord);
			shuffle();
		}else if(usedWordStrings.contains(entryWord)){
			entryStatusTextView.setText("USED: "+entryWord);
		}else{
			entryStatusTextView.setText("INVALID: "+entryWord);
		}
		
		
	}
	
	//Private listener inner-class for clickable views
	private class blockListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	    	
	        int entryLayoutIndex=entryFieldLinearLayout.indexOfChild(v);
	        int letterLayoutIndex=letterFieldLinearLayout.indexOfChild(v);
	        
	        if(entryLayoutIndex!=-1){
	        	entryFieldLinearLayout.removeView(v);
	        	letterFieldLinearLayout.addView(v);
	        }else if(letterLayoutIndex!=-1){
	        	letterFieldLinearLayout.removeView(v);
	        	entryFieldLinearLayout.addView(v);
	        }else if(v.getId()==shuffleImageButton.getId()){
	        	shuffle();
	        }else if(v.getId()==mainTableLayout.getId()){
	        	submitEntry();
	        }
	    }
	}
	
	private void startSummaryIntent(){

		//start new intent
		Intent summaryIntent = new Intent(this, SummaryActivity.class);

		summaryIntent.putExtra("USED_WORDS",usedWordStrings);
		summaryIntent.putExtra("VALID_WORDS", validWordStrings);
		summaryIntent.putExtra("SCORE", keeper.getScore());
		
	    startActivity(summaryIntent);
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
		
	protected void onDestroy(){
		super.onDestroy();
		timer.cancel();
	}

}
