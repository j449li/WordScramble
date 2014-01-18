package com.CAI.wordscramble;

import java.util.HashMap;

/**
 * This is the ScoreKeeper object for
 * the CAI application.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class ScoreKeeper {

	HashMap<String,Integer> SCORETABLE = new HashMap<String,Integer>();
	
	int score;
	
	//constructor creates a table of values associated with each letter
	public ScoreKeeper(){
		//pairs a multiplier value associated with each letter (from Scrabble letter values)
		SCORETABLE.put("a",1);
		SCORETABLE.put("b",3);
		SCORETABLE.put("c",3);
		SCORETABLE.put("d",2);
		SCORETABLE.put("e",1);
		SCORETABLE.put("f",4);
		SCORETABLE.put("g",2);
		SCORETABLE.put("h",4);
		SCORETABLE.put("i",1);
		SCORETABLE.put("j",8);
		SCORETABLE.put("k",5);
		SCORETABLE.put("l",1);
		SCORETABLE.put("m",3);
		SCORETABLE.put("n",1);
		SCORETABLE.put("o",1);
		SCORETABLE.put("p",3);
		SCORETABLE.put("q",10);
		SCORETABLE.put("r",1);
		SCORETABLE.put("s",1);
		SCORETABLE.put("t",1);
		SCORETABLE.put("u",1);
		SCORETABLE.put("v",4);
		SCORETABLE.put("w",4);
		SCORETABLE.put("x",8);
		SCORETABLE.put("y",4);
		SCORETABLE.put("z",10);
		
	}
	
	//update the user's score
	protected void add(String word){
		//iterate through each letter adding the associated value for the letter * 100
		for (int i=0;i<word.length();i++){
			score+=SCORETABLE.get(word.charAt(i)+"")*100;
		}
		
		if (word.length()==6){   //bonus of 500 for 6-lettered words formed
			score+=500;
		}
	}
	
	//returns the user's current score
	protected int getScore(){
		return score;
		
	}
	
}
