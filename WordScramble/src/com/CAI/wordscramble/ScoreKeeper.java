package com.CAI.wordscramble;
import java.util.HashMap;

public class ScoreKeeper {

	HashMap<String,Integer> SCORETABLE = new HashMap<String,Integer>();
	
	int score;
	
	public ScoreKeeper(){
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
	

	protected void add(String word){
		for (int i=0;i<word.length();i++){
			score+=SCORETABLE.get(word.charAt(i)+"")*100;
		}
		
		if (word.length()==6){
			score+=500;
		}
	}
	
	protected int getScore(){
		return score;
		
	}
	
}
