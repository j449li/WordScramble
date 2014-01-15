package com.CAI.wordscramble;

import java.util.ArrayList;

public class BinarySearch{

    public boolean search(String key, ArrayList<String>wordList){
        if (wordList.size()==0){
            return false;
        }
        return search(0,wordList.size()-1,key,wordList);
    }
    
    public boolean search(int floor, int ceil, String key, ArrayList<String>wordList){
        
        if (floor > ceil){
            return false;
        }
        
        int middle = (floor+ceil)/2;
        
        if (wordList.get(middle).compareTo(key)==0){
            return true;
        }else if (wordList.get(middle).compareTo(key)<0){
            return search(middle+1,ceil,key,wordList);
        }else{
            return search(floor,middle-1,key,wordList);
        }
    }
}