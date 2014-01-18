package com.CAI.wordscramble;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import java.io.InputStream;

//used to read data files
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

//Used to provide Settings menu at top-right of app
import android.view.MenuItem;

//Used to build an alert dialog (when the game is over)
import android.app.AlertDialog;
import android.content.DialogInterface;

// Used to load and play sounds effects, and control volume
import android.media.SoundPool;
import android.media.AudioManager;

/**
 * This is the MainActivity of the WordScramble
 * CAI application.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class MainActivity extends Activity {
	//Constants
	private final int LEVEL=30; //Specifies the minimum number of words that can be formed
	protected static final int BLOCKS=6;
	private final String VOWEL="aeiou";
	private final String CONSONANT="bcdfghjklmnpqrstvwxyz";
	private final String ALPHA="abcdefghijklmnopqrstuvwxyz";
	
	//Keeps track of the user's score
	private ScoreKeeper keeper;
	
	//Timer instance variables
	private int time = 91*1000; //default time (medium 90secs)
	private CountDownTimer timer;
	
	//Random generation
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
	private final String[] PRE_GENERATED_LETTERS = {"ehnfta","dnmaiu","drygba","byagdr","sreodz","rpvaeh",
			"aetwyh","yeorlu","awelyu","osriev","nouesd","erftas","aitruc","ewiytr","geuraw",
			"aentwb","atseno","irebne","rsieit","foyrea","aenrul","raeocm","lesaog","oesdaw",
			"laocve","ogiref","aftuor","doreaw","yeagul","ruedfs","teache","arionk","eyirap",
			"dleanv","gnskie","vedain","klegia","teuros","feuosl","karocb","ppasul","radmeo",
			"elsodo","ildask","cesoit","diecan","apgeel","roaedn","einkas","sendra","acalte",
			"oubtdr","nfacde","eisegr","wcoena","bsurae","erwied","demlur","radoyh","lloaes"};
	
	//screen widgets
	private ImageButton shuffleImageButton;
	private TextView timerTextView;
	private TextView scoreTextView;
	private TextView entryStatusTextView;
	private LinearLayout entryFieldLinearLayout;
	private LinearLayout letterFieldLinearLayout;
	private TableLayout mainTableLayout;
	
	// Used to play sound effects
	private SoundPool soundPool;
	private int right_sound_id;
	private int wrong_sound_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get references to components that we will need to update
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
	
	//load sound, images and database and other resources
	protected void loadingResources(){
	    
	    // Allow volume buttons to set the game volume
	    setVolumeControlStream(AudioManager.STREAM_MUSIC);
	     
	    // Create a SoundPool object, and use it to load the two sound effects
	    soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	    right_sound_id = soundPool.load(this, R.raw.correct, 1);
	    wrong_sound_id = soundPool.load(this, R.raw.wrong, 1);
	    
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
		    int count=24815; //number of words in the file
		    
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
	
	//Generating letters for the problem set, initialising timer, and texts
	@Override
	protected void onStart(){
		super.onStart();
		
		validWordStrings.clear();
		validWordStrings = new ArrayList<String>();
	    usedWordStrings.clear();
	    
	    //the program will try a maximum of 10 times to generate letters that can form at least 30 words
	    //after the 10 tries the program will randomly select a set of letters from PRE_GENERATED_LETTERS
	    //this is to speed up program efficiency
	    int counter = 0;
	    do{
	    	validWordStrings.clear();
	    	
		    int numOfVows = randomGen.nextInt(3)+1; //number of vowels (1-3)
		    
		    //randomly select vowels from the VOWEL string
		    for (int i=0;i<numOfVows;i++){
		    	letterStrings[i]=VOWEL.charAt(randomGen.nextInt(5));
		    }
		    
		    //randomly select vowels from the CONSONANT string until 6 letters are selected
		    for (int i=numOfVows;i<BLOCKS;i++){
		    	letterStrings[i]=CONSONANT.charAt(randomGen.nextInt(21));
		    }
		    
		    getValidWords();
		    
		    counter++;
		    
	    }while(validWordStrings.size()<LEVEL && counter<10);
	    
	    //after 10 times choose letters from PRE_GENERATED_LETTERS
	    if (counter==10){
	    	validWordStrings.clear();
	    	
	    	String word = PRE_GENERATED_LETTERS[randomGen.nextInt(PRE_GENERATED_LETTERS.length)];
	    	for (int i=0;i<BLOCKS;i++){
	    		letterStrings[i]=word.charAt(i);
	    	}
	    	
	    	getValidWords();
	    	
	    }
	    
	    //Shuffle and Inflate the letters
	    shuffle();
	    
	    //Creates a Timer
	    timer = new CountDownTimer(time,1000){
	    	public void onTick(long msLeft){
	    		int minutes = (int)msLeft/1000/60;
	    		int seconds = (int)msLeft/1000%60;
	    		timerTextView.setText(String.format("%d:%02d", minutes, seconds));
	    	}
	    	
	    	public void onFinish(){
	    		timerTextView.setText(String.format("%d:%02d", 0, 0));
	    		
	    		timer.cancel();
	    		startSummaryIntent();
	    	}
	    };
	    
	    timer.start();
	    
	    //instantiating a ScoreKeeper object
	    keeper = new ScoreKeeper();
	    
	    //Setting initial texts
	    entryStatusTextView.setText("");
	    scoreTextView.setText("000");
	    
	}
	
	//inflates an ImageButton for each letter in letterStrings randomly
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
	    	letterBlock.setTag(letterStrings[i]); //associates the button to the letter it represents
	    	letterBlock.setImageDrawable(imageBlocksMap.get(letterStrings[i])); //set image
	    	letterBlock.setOnClickListener(new blockListener()); //set listener
	    	letterFieldLinearLayout.addView(letterBlock);
	    }
	}
	
	//'submits' the letters in the entryFieldLinearLayout to check if its valid
	public void submitEntry(){
		String entryWord = "";
		int entrySize = entryFieldLinearLayout.getChildCount();
		
		if (entrySize<3){
			entryStatusTextView.setText("At least 3 letters");
			return;
		}
		
		//form a string from the letterblocks in the entryFieldLinearLayout
		for (int i=0;i<entrySize;i++){
			View tempBlock = entryFieldLinearLayout.getChildAt(i);
			entryWord+=tempBlock.getTag();
		}
		
		if (searcher.search(entryWord,wordList) && 
				!usedWordStrings.contains(entryWord)){               //valid word and not used
			
			entryStatusTextView.setText("VALID: "+entryWord);
			keeper.add(entryWord);
			scoreTextView.setText(keeper.getScore()+"");
			
			usedWordStrings.add(entryWord);
			
			// Play the sound effect for the right answer
			soundPool.play(right_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
			
			shuffle(); //clear the entryFieldLL for the user to enter the next word
		}else if(usedWordStrings.contains(entryWord)){               //used word
			entryStatusTextView.setText("USED: "+entryWord);
			
			// Play the sound effect for the wrong answer
			soundPool.play(wrong_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
		}else{                                                       //not valid
			entryStatusTextView.setText("INVALID: "+entryWord);
			
			// Play the sound effect for the wrong answer
			soundPool.play(wrong_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
		}
		
		
	}
	
	//Private listener inner-class for clickable views
	private class blockListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	    	
	        int entryLayoutIndex=entryFieldLinearLayout.indexOfChild(v);
	        int letterLayoutIndex=letterFieldLinearLayout.indexOfChild(v);
	        
	        //determines where the click comes from
	        if(entryLayoutIndex!=-1){                           //from entryFieldLL
	        	entryFieldLinearLayout.removeView(v);
	        	letterFieldLinearLayout.addView(v);
	        }else if(letterLayoutIndex!=-1){                    //from leterFieldLL
	        	letterFieldLinearLayout.removeView(v);
	        	entryFieldLinearLayout.addView(v);
	        }else if(v.getId()==shuffleImageButton.getId()){    //from shuffle bar
	        	shuffle();
	        }else if(v.getId()==mainTableLayout.getId()){       //from mainTableLayout
	        	submitEntry(); //submit what ever is in entryFieldLL
	        }
	    }
	}
	
	//Get all the valid words that can be formed from the letters in letterStrings
	private void getValidWords(){
	    Permute permuter = new Permute();
	    
		Arrays.sort(letterStrings);
	    ArrayList<String>permList=new ArrayList<String>();
	    
	    //get all the permutations of the letters in letterStrings
	    permList = permuter.getPerms(letterStrings);
	    
	    //check if each permutation is valid and add it to validWordStrings
	    for (String perm:permList){
	    	if (searcher.search(perm,wordList)&&!validWordStrings.contains(perm)){
	    		validWordStrings.add(perm);
	    	}
	    }
	}
	
	//starts the SummaryActivity
	private void startSummaryIntent(){

		//start summary intent
		Intent summaryIntent = new Intent(this, SummaryActivity.class);
		
		//Pass the following data to the summary intent
		summaryIntent.putExtra("USED_WORDS",usedWordStrings);
		summaryIntent.putExtra("VALID_WORDS", validWordStrings);
		summaryIntent.putExtra("SCORE", keeper.getScore());
		
	    startActivity(summaryIntent);
	    
	}
	
	//add items to the menu when it is created
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Add our "Difficulty Menu" menu option as the FIRST item
	    menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.difficulty_menu);
	    
		// Add our "Restart" menu option as the second (FIRST+1) item
	    menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, R.string.restart);
	    
	    // Add our "Home" menu option as the third (FIRST+2) item
	    menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, R.string.home_menu);
	    
		return true;
	}
	
	// Called automatically when an option in the Settings menu at top-right is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // If the user has selected our "Difficulty Menu" menu option
	    if (item.getItemId() == Menu.FIRST) {
	        // Create an AlertDialog Builder and set its title to "Select Difficulty" string resource
	        AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(this);
	        choicesBuilder.setTitle(R.string.select_difficulty);
	         
	        // Add array of possible choices to the Dialog and set the behaviour when one is tapped.
	        choicesBuilder.setItems(R.array.timeLimits, 
	                new DialogInterface.OnClickListener() 
	                {
	                    public void onClick(DialogInterface dialog, int item)
	                    {
	                        if (item == 0){           //EASY
	                        	time = 121*1000;
	                        }else if (item == 1){     //MEDIUM
	                        	time = 91*1000;
	                        }else{                    //HARD
	                        	time = 61*1000;
	                        }
	                        //restart activity
	                        onDestroy();
	                        onStart();
	                    }
	                }
	        );
	        // Create and display the dialog
	        AlertDialog choicesDialog = choicesBuilder.create();
	        choicesDialog.show();
	        return true;
	    }
	    // if they selected "Restart", then restart CAI
	    else if (item.getItemId() == Menu.FIRST+1) {
	    	onDestroy();
	        onStart();
	        return true;            
	    }
	    // if they selected "Home", then the app will go back to the StartActivity
	    else if (item.getItemId() == Menu.FIRST+2) {
	    	timer.cancel();
	    	finish();
	    	
	    	return true; 
	    }
	     
	    // If the user did not pick any of our menu items
	    return false;
	}
	
	//Stops the timer and pause activity
	@Override
	protected void onPause(){
		super.onPause();
		timer.cancel();
	}
	
	//Stops the timer and stop activity
	@Override
	protected void onStop(){
		super.onStop();
		timer.cancel();
	}
	//Stops the timer and end activity
	@Override
	protected void onDestroy(){
		super.onDestroy();
		timer.cancel();
	}

}