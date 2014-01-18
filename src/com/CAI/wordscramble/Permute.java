package com.CAI.wordscramble;

import java.util.ArrayList;

public class Permute {
	private ArrayList<String>permutations=new ArrayList<String>();
	
	protected  ArrayList<String> getPerms(char[]letterStrings){
		permutations.clear();
		String letters="";
		
		for (char ch:letterStrings){
			letters+=ch+"";
		}
		
		for (int size=3;size<=6;size++){
			perm("",letters,size);
		}
		
		return permutations;
    }
    
    private void perm(String prefix, String str, int size){
        int n=str.length();
        
        if (n==6-size){
        	permutations.add(prefix);
        }else{
            for (int i=0;i<n;i++){
                perm(prefix+str.charAt(i),str.substring(0,i)+str.substring(i+1,n),size);
            }
        }
    }
    

}
