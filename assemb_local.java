/*
 * Core local assembly section of Hot RAD
 * 
 * Base code: Nicholas LaRosa
 * Modifications/Refactoring: Lauren Assour
 * 
 * Last Modified: 15 August 2014 - LAA
 * 
 */

import java.io.*;
import java.util.*;
//import java.util.regex.*;

class assemb_local {
	
    // Number of bases to count (STOP THIS FROM BEING HARDCODED LATER - DO PERCENTAGE OF SHORTEST READ?)  
    static int numtocount = 10;
    private static HashMap<String,int[]> seqCounts;

	public static void main(String[] args) throws Exception {
	
		// Set up default parameters
		HashMap <String, Integer> parameters = new HashMap <String, Integer>();
		parameters.put("matchScore", 1);
		parameters.put("mismatchScore", 0);
		parameters.put("gapScore", 0);
		parameters.put("percentIdent", 90);
				
		parameters.put("countLen", 50);
		parameters.put("filtMaxDiff", 5);
		parameters.put("minGroupSizeD", 3);
		parameters.put("maxAlign", 100);
		
		parameters.put("minInitial", 5);
		parameters.put("testArea", 8);
		parameters.put("overhang", 3);
	
		parameters.put("kband", 8);
			
		// Confirm arguments
		if (args.length != 3) {
			System.out.println("Usage: java assemb_local <configfile> <sequencefile1> <sequencefile2>");
			System.exit(1);
		}

		// Get filenames
		String configFile = args[0];
		String seqFile1 = args[1];
		String seqFile2 = args[2];
				
		// Parse config file
		parameters = configParse(configFile, parameters);

		/*String fileName = args[0];
		String fileName2 = args[1];
		String percIdent = args[2];       			// user can give percent identity

		String matchStr = "1";					// this will be the standard scoring scheme
		String mismatchStr = "0";
		String gapStr = "0";
	
		if( args.length == 7 )
		{
			if( args[3].equals("-scores") )			// get scoring scheme from user
			{	
				matchStr = args[4];
				mismatchStr = args[5];
				gapStr = args[6];
			}
			else
			{
				throw new IllegalArgumentException("Call 'java assemb <s1_output> <s1_output2>  <%_identity> <-scores <match> <mismatch> <gap>>.'\nFor additional assistance, please see the provided documentation.");
			}
		}
			
		double myArg = Double.valueOf(percIdent);		// getting percent identity
		int myArgInt = (int)myArg;				// make sure the number is between 0-100

		if( myArgInt > 100 || myArgInt <= 0 )			// invalid percent identity given
		{
			throw new IllegalArgumentException("Specify a percent identity between 0 and 100.");
		}
		*/
		// Variables
		
		double myPercentIdent = ((double)parameters.get("percentIdent")/100);		// convert to proportion for actual use
		int match_score = parameters.get("matchScore");
		int mismatch_score = parameters.get("mismatchScore");
		int gap_score = parameters.get("gapScore");
		int max_align = parameters.get("maxAlign");

		int min_initial = parameters.get("minInitial");
		int testarea = parameters.get("testArea");
		int overhang = parameters.get("overhang");
		int kband = parameters.get("kband");

		seqCounts = new HashMap<String, int[]>();

		//boolean newGroup = true;				// keep track of when to store new groups
		int groupNumber = 0;

		//String currCompare = "";				// compared to all strings after it
		String currLine = "";					// the current line being read
		//String finalOut = "";					// final output to screen
		//String currIdent = "";					// output the identifier of the matched sequence

		int i;							// string position
		int j;
		//int k;
		//int numObjects = 0;					// number of sequences in our ArrayList
		
		//int seqNum = 0;						// numbering sequences for when they are added to the object's ArrayList		

		//int n = 0;	
		
		//String outFile = "";					// name of the out#.txt file 
		//String tempStr = "";
		//String outNum = "";
		//int myIndex = 0;						
		//int currCount = 1;					// every other line will be a sequence, so keep track of which line we are on

		//int lines = 0;

		ArrayList<ArrayList<String>> identGroups = new ArrayList<ArrayList<String>>();  // an ArrayList of ArrayLists to hold the identifier groups

		aligner a1 = new aligner();					// allows us to use aligner.class
		a1.load(match_score, mismatch_score, gap_score, max_align, overhang, min_initial, testarea, kband);		// establish our scoring scheme

		// DONE WITH VARIABLES
	
		FileReader fr = new FileReader(seqFile1);			// this will read the sequence being compared to all others
		FileReader fr2 = new FileReader(seqFile2);
		BufferedReader br = new BufferedReader(fr);
		BufferedReader br2 = new BufferedReader(fr2);

		try {
			groupNumber = 0;								// our first group will be index 0
			identGroups.add(new ArrayList<String>());		// our first group
				
			currLine = br.readLine();

			while (currLine != null) {
			    ArrayList<String> myGroup = identGroups.get(groupNumber);
			    while (currLine != null && currLine.indexOf("Alignment") == -1 && !(currLine.trim().isEmpty())) {
				    myGroup.add(currLine);
				    String currSeq = br.readLine();
				    countChars(currLine, currSeq, parameters.get("countLen"));
				    myGroup.add(currSeq);
				    currLine = br.readLine();
				}
				if (currLine != null) {
				    //System.err.print("!!!!!" + currLine);
				    groupNumber++;
				    identGroups.add(new ArrayList<String>());
				    currLine = br.readLine();
				}
			}
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		br.close();
 

		try {
		    currLine = br2.readLine();
		    if (currLine.indexOf("Alignment") != -1) {
		    	currLine = br2.readLine();
		    }
		    String currSearchIdent = currLine;
		    String currSearchSeq = br2.readLine();
		    currLine = br2.readLine();
		    
		    while (currSearchIdent != null && currSearchSeq != null) {
		    	countChars(currSearchIdent, currSearchSeq, parameters.get("countLen"));
		    	int[] myCounts1 = seqCounts.get(currSearchIdent);
			
		    	boolean found = false;

		    	for (i = 0; i < identGroups.size(); i++) {
		    		if (identGroups.get(i).size() == 0) {
		    			continue;
		    		}
		    		int[] myCounts2 = seqCounts.get(identGroups.get(i).get(0));
				
		    		//System.out.println("******" + Arrays.toString(myCounts1));
		    		//System.out.println("++++++" + Arrays.toString(myCounts2));
		    		if (((Math.abs(myCounts1[0]-myCounts2[0]) + Math.abs(myCounts1[1]-myCounts2[1]) + Math.abs(myCounts1[2]-myCounts2[2]) + Math.abs(myCounts1[3]-myCounts2[3])/2) > parameters.get("filtMaxDiff"))) {
				    //System.out.println(myCounts1[1] + " " + myCounts2[1]);
		    			continue;
		    		}
				//System.out.println("Comparing " + currSearchIdent + " " + identGroups.get(i).get(0));
		    		if (a1.align(identGroups.get(i).get(1), currSearchSeq, myPercentIdent)) { //if two consensus match
		    			ArrayList<String> addGroup = identGroups.get(i);
		    			addGroup.add(currSearchIdent);
		    			addGroup.add(currSearchSeq);
		    			// merge them into i-th group
				    
		    			while (currLine != null && currLine.indexOf("Alignment") == -1 && !(currLine.trim().isEmpty())) {
		    					addGroup.add(currLine);
		    					currLine = br2.readLine();
		    			}

		    			found = true;
		    			a1.clear();
		    			break;
		    		}
		    		a1.clear();
			    
		    	}

		    	if (!found) {
			    System.out.println(currSearchIdent);
			    System.out.println(currSearchSeq);

		    		while (currLine != null && currLine.indexOf("Alignment") == -1 && !(currLine.trim().isEmpty())) {
		    			System.out.println(currLine);
		    			currLine = br2.readLine();
		    		}
			    
		    		//if (identGroups.size() == 0 currLine != null) {
				    System.out.println();
				
		    		//currLine = br2.readLine();
		    		//	}
		    	}

			    currSearchIdent = br2.readLine();
			    currSearchSeq = br2.readLine();
			    currLine = br2.readLine();
		    }
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		br2.close();
		//System.out.println("***" + identGroups.size());
		for(i = 0; i < identGroups.size(); i++) {
			for(j = 0; j < identGroups.get(i).size(); j++) {
				System.out.println(identGroups.get(i).get(j));
			}
			// Don't print a new line if last group
			if (i != identGroups.size()-1 && identGroups.get(i).size() != 0) {
				System.out.println();
				}
		}	

		/*for(j = 0; j < identGroups.get(identGroups.size()-1).size(); j++) {
			System.out.println(identGroups.get(identGroups.size()-1).get(j));
		}*/
	}
	
	// Gives base pair count for a provided sequence - numtocount limits the amount of bases we sum 
    public static void countChars(String name, String in_sequence, int numtocount) {
    	String sequence = in_sequence;
    	
    	// If we can actually truncate the read
    	if (in_sequence.length() > numtocount) {
    		sequence = in_sequence.substring(0, numtocount);
    	}
 
        int[] counts = {0,0,0,0};

	//System.out.println("!!!" + sequence);
        for (int i = 0; i < sequence.length(); i++) {
        	switch (sequence.charAt(i)) {
		    	case 'A':
		    		counts[0]++;
		    		break;
		        case 'C':
		    		counts[1]++;
		    		break;
		        case 'G':
			        counts[2]++;
			        break;
			case 'T':
			    	counts[3]++;
			    	break;
			default:
			    	break;
			}
	    }
	//System.out.println(name + ": " + counts[0] + " " + counts[1] + " " + counts[2] + " " + counts[3]);
        seqCounts.put(name, new int[] {counts[0], counts[1], counts[2], counts[3]});
    }
    
 // Read in parameters from config file
    public static HashMap<String, Integer> configParse(String filename, HashMap <String, Integer> parameters) {
    
    	BufferedReader configFile = null;
	    try {
	    	configFile = new BufferedReader(new FileReader(filename));
	    	String line = "";
	    	while ((line = configFile.readLine()) != null) {
	    		if (line.equals("")) {
	    			continue;
	    		}
	    		String[] values = line.split("=");
	    		// Is this a parameter we recognize?
	    		if (parameters.get(values[0]) == null) {
	    			System.out.println("Parameter "+ values[0] + " not recognized.");
				System.exit(1);
	    		}
	    		// Is the value an integer?
	    		else if (!(values[1].trim().matches("-?\\d+"))) {
	    			System.out.println("Parameter " + values[0] + " does not have an integer value (" + values[1] + " entered).");
				System.exit(1);
			}
	    		else {
	    			parameters.put(values[0], Integer.parseInt(values[1]));
	    		}
	    	}
	    	configFile.close();	
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	    
	    return parameters;    
    }
}

