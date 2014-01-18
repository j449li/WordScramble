package com.CAI.wordscramble;

import java.util.ArrayList;

/**
 * This is the Permutations class for the CAI application. 
 * It helps generate a String Array of all the possible
 * permutations of a given six letters.
 * 
 * @author George Li
 * @version May 27, 2013
 */

public class Permute {
	private ArrayList<String>permutations=new ArrayList<String>();
	
	//returns an array of all the permutations for a string
	protected  ArrayList<String> getPerms(char[]letterStrings){
		permutations.clear();
		String letters="";
		
		//converting the character array to a string
		for (char ch:letterStrings){
			letters+=ch+"";
		}
		
		//Generates the permutations of letters based on size. From 3-letters to 6-letters.
		for (int size=3;size<=MainActivity.BLOCKS;size++){
			perm("",letters,size);
		}
		
		return permutations;
    }
    
	//recursive algorithm
    private void perm(String prefix, String str, int size){
        int n=str.length();
        
        if (n==MainActivity.BLOCKS-size){     //if the prefix is the desired length, add it to permutations
        	permutations.add(prefix);
        }else{
            for (int i=0;i<n;i++){            //else further break down the str until desired size
                perm(prefix+str.charAt(i),str.substring(0,i)+str.substring(i+1,n),size);
            }
        }
    }
    

}
