package com.sudoku.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class Database {
	
	private ArrayList<NumberFeatures>[] database;
	private int[] vote;
	
	public Database(){
		database = new ArrayList[9];
		for(int i=0; i<9; i++){
			database[i]= new ArrayList<NumberFeatures>();
		}
	}
	
	public Database(InputStream is){
		database = new ArrayList[9];
		for(int i=0; i<9; i++){
			database[i]= new ArrayList<NumberFeatures>();
		}
		try {
			loadXmlDb(is);
			Log.i("db", "db loaded");
			Log.i("db", String.valueOf(database[0].size()));
		} catch (Exception e) {
			Log.e("db","error in create db");
			e.printStackTrace();
		}
	}
	
	// Build Database
	
	void addFeature(NumberFeatures ft){
		database[ft.getValue()-1].add(ft);
	}
	
	ArrayList<Integer> stringToVec(String s){
		ArrayList<Integer> ftList = new ArrayList<Integer>();
		String[] values = s.split(";");
		for(int i=0; i<values.length; i++){
			int val = Integer.parseInt(values[i]);
			ftList.add(val);
		}
		return ftList;
	}
	
	public void loadXmlDb(InputStream is) throws XmlPullParserException, IOException {
		// Load data in a new parser
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setInput(is,null);
		
		// Parse data
		int eventType = parser.getEventType();
		int currentNumber=0;
		ArrayList<Integer> density= new ArrayList<Integer>();
		ArrayList<Integer> align= new ArrayList<Integer>();
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch(eventType){
			case XmlPullParser.START_TAG:
				if(parser.getName().equals("number")){
					currentNumber+=1;
				}
				else if(parser.getName().equals("density")){
					parser.next();
					String value = parser.getText();
					density = stringToVec(value);
				}
				else if(parser.getName().equals("alignement")){
					parser.next();
					String value = parser.getText();
					align = stringToVec(value);
				}
				break;
			case XmlPullParser.END_TAG:
				if(parser.getName().equals("features")){
					addFeature(new NumberFeatures(currentNumber, density, align));
				}
				break;				
					
			}
			eventType=parser.next();
		}
	}
	
	// Estimate number value from its features
	
	double distance(ArrayList<Integer> v1, ArrayList<Integer> v2){
		double k=0;
		for(int i=0;i<v2.size();i++){
			k+= Math.pow(v2.get(i)-v1.get(i), 2);
		}
		return Math.sqrt(k);
	}
	
	void densityPpv(int k, NumberFeatures ft){
		HashMap<Double, Integer> distanceMap = new HashMap<Double, Integer>();
		for(int n=0; n<9; n++){
			ArrayList<NumberFeatures> listOfFt = database[n];
			for(int i=0; i<listOfFt.size();i++){
				distanceMap.put(distance(ft.getDensity(), listOfFt.get(i).getDensity()), listOfFt.get(i).getValue());
			}
		}
		Set<Double> distances = distanceMap.keySet();
		ArrayList<Double> distList = new ArrayList<Double>(distances);
		java.util.Collections.sort(distList);
		for(int i=0; i<k; i++){
			double d = distList.get(i);
			int value = distanceMap.get(d);
			vote[value-1]+=1;
			distanceMap.remove(d);
		}
	}
	
	void alignPpv(int k, NumberFeatures ft){
		HashMap<Double, Integer> distanceMap = new HashMap<Double, Integer>();
		for(int n=0; n<9; n++){
			ArrayList<NumberFeatures> listOfFt = database[n];
			for(int i=0; i<listOfFt.size();i++){
				distanceMap.put(distance(ft.getAlignement(), listOfFt.get(i).getAlignement()), listOfFt.get(i).getValue());
			}
		}
		Set<Double> distances = distanceMap.keySet();
		ArrayList<Double> distList = new ArrayList<Double>(distances);
		java.util.Collections.sort(distList);
		for(int i=0; i<k; i++){
			double d = distList.get(i);
			int value = distanceMap.get(d);
			vote[value-1]+=1;
			distanceMap.remove(d);
		}
	}
	
	public int findValue(NumberFeatures ft, int k){
		vote = new int[9];
		densityPpv(k, ft);
		alignPpv(k, ft);
		int max=0;
		for(int i=1; i<9; i++){
			if(vote[i]>vote[max]){
				max=i;
			}
		}
		return max+1;
	}
}
