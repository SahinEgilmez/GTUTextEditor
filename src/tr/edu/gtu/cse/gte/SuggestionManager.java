/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.edu.gtu.cse.gte;

import com.sun.java_cup.internal.runtime.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;


/**
 * Sınıfın generic olmasının sebebi verilen parametrenin sadece
 * string türünden olmama durumunu karsılamak ıcın
 * @author burak
 * @param <T> 
 */
public class SuggestionManager<T extends Comparable<T>> {
    
    private static class itemOfQueue{
        private String value;
        private Integer priority;
        private String TYPE;//k:keyword , i:identifier
        
        itemOfQueue(String val,Integer prio,String type){
            this.value=val;
            this.priority=prio;
            this.TYPE=type;
        }
        
        void setValue(String val){this.value=val;}
        void setPrio(Integer val){this.priority=val;}
        void setTYPE(String val){this.TYPE=val;}
        Integer getPriority(){return this.priority;}
        String getValue(){return this.value;}
        String getTYPE(){return this.value;}
    }
    
    private static class itemComparator implements Comparator<itemOfQueue>{
        @Override
        public int compare(itemOfQueue s1,itemOfQueue s2){
            if(s1.getPriority().intValue()<s2.getPriority().intValue()){
                return 1;
            }
            if(s1.getPriority().intValue()>s2.getPriority().intValue()){
                return -1;
            }
            else{
                return 0;
            }
        }
    }
    
    private PriorityQueue<itemOfQueue> currentSuggestions=new PriorityQueue<itemOfQueue>();
    //private BinarySearchTree<T> SuggestionSearchTree=new BinarySearchTree<T>();
   
    
    public static void main(String []args) throws IOException{
        ArrayList<String> arr=new ArrayList<String>();
        String line;
        try {

        BufferedReader bufferreader = new BufferedReader(new FileReader("C:\\Users\\burak\\Desktop\\proje\\GTUTextEditor\\src\\tr\\edu\\gtu\\cse\\gte\\deneme.txt"));
        while ((line = bufferreader.readLine()) != null) {     
            arr.add(line);
            
        }

    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    }
        
        SuggestionManager<String> manager=new SuggestionManager(arr);
        ArrayList<String> res=manager.search("r");
        Iterator<String> iter=res.iterator();
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
        
    }
    
    public SuggestionManager(ArrayList<String> linesOfText){
            this.currentSuggestions=new PriorityQueue<itemOfQueue>(1,new itemComparator());
        parser(linesOfText);

    }
    
    /**
     * Search metodunun yerine yazılmıs metod   
     */
    public void update(ArrayList<String> linesOfText){
        this.currentSuggestions=new PriorityQueue<itemOfQueue>(1,new itemComparator());
        parser(linesOfText);
    }
    
   
    
    private void parser(ArrayList<String> linesOfText){
        Parser myparser=new Parser();
        for(String iter:linesOfText){
            myparser.parse(iter);
        }
        ArrayList<String> keywords=myparser.getKeywords();
        ArrayList<String> identifier=myparser.getVariables();
        Iterator<String> iterKW=keywords.iterator();
        Iterator<String> iterV=identifier.iterator();
        while(iterKW.hasNext()){
           String temp=iterKW.next();
           toQueue(temp);
        }
        
        int i=0;
        
        while(iterV.hasNext()){
           String temp=iterV.next();
           toQueue(temp);
                 
        }
        
        
    }
    
    private itemOfQueue isInQueue(String val){
        Iterator<itemOfQueue> iter=this.currentSuggestions.iterator();
        while(iter.hasNext()){
            itemOfQueue temp=iter.next();
            if(temp.getValue().equals(val)){
                return temp;
              
            }
        }
        return null;
    }
    
    private void toQueue(String value){
        itemOfQueue temp=isInQueue(value);
        
        if(temp!=null){
            temp.setPrio(temp.getPriority()+1);
            this.currentSuggestions.remove(temp);
            this.currentSuggestions.add(temp);
        }
        else{
            this.currentSuggestions.add(new itemOfQueue(value,0,"i"));
        }
    }
    
    public ArrayList<String> search(String key){
        char[] c_arr = key.toCharArray();
        ArrayList<String> result=new ArrayList<String>();
        PriorityQueue<itemOfQueue> resTemp = this.currentSuggestions;
        PriorityQueue<itemOfQueue> res= new PriorityQueue<itemOfQueue>(1,new itemComparator());
        
        for (int i = 0; i <c_arr.length ; i++) {
            res= new PriorityQueue<itemOfQueue>(1,new itemComparator());
            for(itemOfQueue temp: resTemp){
                if(i<temp.getValue().length()){
                    if(temp.getValue().charAt(i)==key.charAt(i)){
                        res.add(temp);
                    }
                }
            }
            resTemp=new PriorityQueue<itemOfQueue>(res);
       }
        
        for(itemOfQueue temp3:res){
            result.add(temp3.getValue());
        }
        
        return result;
    }
}
