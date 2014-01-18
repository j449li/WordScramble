package com.CAI.wordscramble;

import java.util.ArrayList;

/**
 * This is the BinarySearch class used to
 * search through the data base of words.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class BinarySearch{
	
	//calls the recursive binary search
    public boolean search(String key, ArrayList<String>wordList){
        if (wordList.size()==0){
            return false;
        }
        return search(0,wordList.size()-1,key,wordList);
    }
    
    //recursive binary search
    public boolean search(int floor, int ceil, String key, ArrayList<String>wordList){
        
        if (floor > ceil){   //word does not exist
            return false;
        }
        
        int middle = (floor+ceil)/2;
        
        if (wordList.get(middle).compareTo(key)==0){        //word found
            return true;
        }else if (wordList.get(middle).compareTo(key)<0){   //alphabetically too low
            return search(middle+1,ceil,key,wordList);
        }else{                                              //alphabetically too high
            return search(floor,middle-1,key,wordList);
        }
    }
}