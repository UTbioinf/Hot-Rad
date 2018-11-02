/*
 * Core assembly section of Hot RAD
 * 
 * Base code: Nicholas LaRosa
 * Modifications/Refactoring: Lauren Assour
 * 
 * Keeping unbanded for posterity's sake
 * Last Modified: 15 August 2014 - LAA
 * 
 */

import java.io.*;
import java.util.*;

class unbanded_assemb {

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
		
	    //THIS NEEDS TO BE FIXED AND STOPPED FROM BEING HARDCODED
	    BufferedWriter singletonfile = null;
	    try {
	    	singletonfile = new BufferedWriter(new FileWriter("mysingletons.txt"));
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }

	    // Confirm arguments
		if (args.length != 3) {
			System.out.println("Usage: java assemb <sequencefile> <configfile> <fast(a)/fast(q)");
			System.exit(1);
		}

		// Get filenames
		String configFile = args[1];
		String seqFile = args[0];

		// Get file type
		int typeCount = 0;
		if (args[2].equals("a")) {
			typeCount = 3;
		}
		else if (args[2].equals("q")) {
			typeCount = 5;
		}
		else {
			System.out.println("Usage: java assemb <sequencefile> <configfile> <fast(a)/fast(q); a or q not provided");
			System.exit(1);
		}
		
		// Parse config file
		parameters = configParse(configFile, parameters);
		//System.out.println(parameters.get("mismatchScore"));
		//System.exit(0);
		
		/*// Default scoring scheme
		//String matchStr = "1";			
		//String mismatchStr = "0";
		//String gapStr = "0";

		// This count depends on file type: FASTA has 1 sequence every 2 lines, FASTQ has 1 sequence every 4 lines
		// So we reset the grouping on the third or fifth line - 3 = FASTA, 5 = FASTQ
		//int typeCount = 3;					

		// Default number of bases to count
		//int countlen = 50;
		// Default max difference between base counts of two sequences
		//int filtmaxdiff = 5;
		
		// Argument parsing
		if(Double.valueOf(args[1]) > 100 || Double.valueOf(args[1]) <= 0 ) {                   // invalid percent identity given
			throw new IllegalArgumentException("Specify a percent identity between 0 and 100 (java assemb <db_file> <%_identity> <-q/-a> <-scores <match> <mismatch> <gap>> <-countlen <int>>)");
		}

		if (args[2].equals("-q")) {
			//typeCount = 5;
		}
		else if(!(args[2].equals("-a"))) {
		    throw new IllegalArgumentException("Please provide -q or -a (java assemb <db_file> <%_identity> <-q/-a> <-scores <match> <mismatch> <gap>> <-countlen <int>>)");
		}
		
		if (args.length > 3) {
		    if (!(args[3].equals("-scores"))) {
		    	if (args[3].equals("-countlen")) {
		    		//countlen = Integer.parseInt(args[4]);
		    	}
		    	else if(args[3].equals("-countdiff")) {
		    		//filtmaxdiff = Integer.parseInt(args[4]);
		    	}
		    	else {
		    		throw new IllegalArgumentException("Check for -scores/-countlen ordering (java assemb <db_file> <%_identity> <-q/-a> <-scores <match> <mismatch> <gap>> <-countlen <int>>)");
		    	}
		    }
		    else {
		    	//matchStr = args[4];
		    	//mismatchStr = args[5];
		    	//gapStr = args[6];
		    }

		    if (args.length > 7) {
		    	if (!args[7].equals("-countlen")) {
		    		throw new IllegalArgumentException("-countlen not in correct location (java assemb <db_file> <%_identity> <-q/-a> <-scores <match> <mismatch> <gap>> <-countlen <int>>)");
		    	}
		    	//countlen = Integer.parseInt(args[8]);
		    }
		}*/
	
		// Get scoring set up
		double myPercentIdent = ((double)parameters.get("percentIdent")/100);		// convert to proportion for actual use
		int match_score = parameters.get("matchScore");
		int mismatch_score = parameters.get("mismatchScore");
		int gap_score = parameters.get("gapScore");
		int max_align = parameters.get("maxAlign");

		int min_initial = parameters.get("minInitial");
		int testarea = parameters.get("testArea");
		int overhang = parameters.get("overhang");

		seqCounts = new HashMap<String, int[]>();
		ArrayList<ArrayList<String>> identGroups = new ArrayList<ArrayList<String>>(); 	// an ArrayList of ArrayLists to hold the identifier groups

		boolean alignBool = false;				// this will be false if no match, but true if there was a match
		boolean alreadyAdded = false;			// true if a sequence has already been grouped

		String currCompare = "";				// compared to all strings after it
		String currLine = "";					// the current line being read
		String currIdent = "";					// output the identifier of the matched sequence

		int i;									// string position
		int j;
		int k;

		int currCount = 0;
		
		String tempIdent = "";
		String tempSeq = "";

		unbanded_aligner a1 = new unbanded_aligner();				// allows us to use aligner.class
		a1.load(match_score, mismatch_score, gap_score, max_align, overhang, min_initial, testarea);			// establish our scoring scheme
	
		sequence tempSeqObj = new sequence();
		int[] tempArray = new int[2];								// method getArrayValues

		// DONE WITH VARIABLES

		FileReader freader = new FileReader(seqFile);          		// this will read the sequence being compared to all others
        BufferedReader br = new BufferedReader(freader);			
        
		try {
			currCount = 1;
				
			while ((currLine = br.readLine()) != null)	{
			    alreadyAdded = false;
				
			    if (currCount == typeCount)	{					// once we have reached the end of a sequence's info, reset the count
					currCount = 1;
				}

				if (currCount == 1) {							// first line of a group is the identifier	
					currIdent = currLine;
					currCount++;
				}
				else if (currCount == 2) {						// we are on the second line of a set of info, where sequence is
					currCompare = currLine;

					// Get number of each base
					countChars(currIdent, currCompare, parameters.get("countLen"));
					int[] myCounts = seqCounts.get(currIdent);

					// Look through each existing group
					for (i = 0; i < identGroups.size(); i++) {
						// See if sequences are similar enough, if not, go on to next group
					    int[] currCounts = seqCounts.get(identGroups.get(i).get(0));
					    if (((Math.abs(currCounts[0]-myCounts[0]) + Math.abs(currCounts[1]-myCounts[1]) + Math.abs(currCounts[2]-myCounts[2]) + Math.abs(currCounts[3]-myCounts[3]))/2) > parameters.get("filtMaxDiff")) {
						//System.out.println(currCounts[1] + " " + myCounts[1]);
					    	continue;
					    }
					
					    // Try to align - get 1 because 0 is the identifier line
					    alignBool = a1.align(identGroups.get(i).get(1), currCompare, myPercentIdent);
					    a1.clear();

					    // If alignment is good
					    if (alignBool) {				
						    identGroups.get(i).add(currIdent);		// add to correct inner ArrayList	
						    identGroups.get(i).add(currCompare);			
						    
						    // Good to go, break
						    alreadyAdded = true;
						    break;
					    }
					}
					
					// If it didn't and it wasn't already added to a group, make a new group
					if(!alignBool && !alreadyAdded) {
						//i will be set to identGroups.size() after loop iteration above
						identGroups.add(new ArrayList<String>());	
						identGroups.get(i).add(currIdent);
						identGroups.get(i).add(currCompare);					
					}
					currCount++;
				}	
				else {
					currCount++;
				}
			}
  		}
		finally	{						
			// STEP 2: Center Star Tree alignment, receives consensus sequences
			// Places them as the first sequence of their respective groups
			br.close();	

			String centerSeq = "";
			String otherSeq = "";

			int optimalIndex = 0;
			int numSeqChecked = 0;
			int currScore = 0;
			int currSumScore = 0;
			int currMin = 0;
			int minGroupSize = parameters.get("minGroupSizeD");
			int finalScore = 0;

			ArrayList<String> ourGroup = new ArrayList<String>();			// holds the current group
			ArrayList<String> ourIdents = new ArrayList<String>();			// holds the identifiers of the group sequences
			
			ArrayList<Integer> gapArray = new ArrayList<Integer>();			// keep track of the optimal sequence's alignments with other sequences
			ArrayList<Integer> mismatchArray = new ArrayList<Integer>();	// (gaps and mismatches)
	
			ArrayList<Integer> centerOrder_I = new ArrayList<Integer>();	// basically a two dimensional ArrayList holding the order of indexes
			ArrayList<Integer> centerOrder_G = new ArrayList<Integer>();	// order of gap counts
			ArrayList<Integer> best_centerOrder_I = new ArrayList<Integer>(); // hold onto 'best' ordering
			
			
			// Look through current groups to remove unwanted ones
			/*for (i = 0; i < identGroups.size(); i++) {
				// If there are less than the minGroupSize sequences in a group, delete it
				if (identGroups.get(i).size() < minGroupSize) {			
				    for (int x = 0; x < identGroups.get(i).size(); x++) {
				    	singletonfile.write(identGroups.get(i).get(x) + "\n");
				    }
					identGroups.remove(i);
					i--;						// subtract from i in order to compensate for shifting of indices
				}
			}*/

			for (k = 0; k < identGroups.size(); k++) {				// outer ArrayList (run center star for each group of sequences)
				if (identGroups.get(k).size() < minGroupSize) {			
				    for (int x = 0; x < identGroups.get(k).size(); x++) {
				    	singletonfile.write(identGroups.get(k).get(x) + "\n");
				    }
					identGroups.remove(k);
					k--;						// subtract from i in order to compensate for shifting of indices
					continue;
				}
				
				for (j = 0; j < identGroups.get(k).size()-1; j+=2) {
					ourIdents.add(identGroups.get(k).get(j));		// sequence identifiers are even number indices
					ourGroup.add(identGroups.get(k).get(j+1));		// nucleotide sequences are odd number indices
				}

				numSeqChecked = 0;
				currScore = 0;
				currSumScore = 0;
				currMin = 0;
				finalScore = 0;

				while (numSeqChecked < ourGroup.size()) {
					currScore = 0;
					currSumScore = 0;
					centerSeq = ourGroup.get(numSeqChecked);		// center of star tree

					// Set up gap and mismatch arrays for each seq
					getArrays:
						for (i = 0; i < ourGroup.size(); i++) {
							if (i == numSeqChecked) {
								gapArray.add(0);					// have values at each index
								mismatchArray.add(0);
								continue getArrays;
							}

							otherSeq = ourGroup.get(i);		
							tempArray = a1.getArrayValues(centerSeq,otherSeq);		// find out how many gaps/mismatches	

							gapArray.add(tempArray[0]);
							mismatchArray.add(tempArray[1]);
						}	

					getOrder:
						for (i = 0; i < gapArray.size(); i++) {
							if (i == numSeqChecked) {
								continue getOrder;
							}

							if (centerOrder_I.isEmpty()) {				// if this is the first index encountered
								centerOrder_I.add(i);
								centerOrder_G.add(gapArray.get(i));
								continue getOrder;
							}

						// Get ordering figured out by comparing gaps/mismatches
						innerCheck:
							for (j = 0; j < centerOrder_I.size(); j++) {
								if (gapArray.get(i) == centerOrder_G.get(j)) {	// same number of gaps, need to compare matches
									if (mismatchArray.get(i) <= mismatchArray.get(centerOrder_I.get(j))) { // same #mismatches
										centerOrder_I.add(j,i);					// add before
										centerOrder_G.add(j,gapArray.get(i));

										continue getOrder;
									}
									else {								// more mismatchess
										if (j == (centerOrder_I.size()-1))	{		// we are at the end
											centerOrder_I.add(j+1,i);
											centerOrder_G.add(j+1,gapArray.get(i));

											continue getOrder;
										}
										else if (gapArray.get(i) == centerOrder_G.get(j+1)) {	// next has same gaps too
											continue innerCheck;
										}
										else {							// add after (end of similar)
											centerOrder_I.add(j+1,i);
											centerOrder_G.add(j+1,gapArray.get(i));

											continue getOrder;
										}
									}
								}
								else if (gapArray.get(i) < centerOrder_G.get(j)) {	// less gaps, add in front		
									centerOrder_I.add(j,i);
									centerOrder_G.add(j,gapArray.get(i));

									continue getOrder;
								}
								else {							// more gaps, check
									if (j == (centerOrder_I.size()-1))   {      	// we are at the end
										centerOrder_I.add(j+1,i);
										centerOrder_G.add(j+1,gapArray.get(i));

										continue getOrder;
									}
									else {						// we just need to skip this one
										continue innerCheck;
									}
								}
							}
						}

					// Aligning in the order we established
					for (i = 0; i < centerOrder_I.size(); i++) {
						otherSeq = ourGroup.get(centerOrder_I.get(i));
						tempSeqObj = a1.align(centerSeq,otherSeq); 		// align center with all others

						centerSeq = tempSeqObj.getSeq();
						currScore = currScore + tempSeqObj.getScore();		// keep track of alignment score
	
						currSumScore = currSumScore + tempSeqObj.getSumScore();	// keep track of alignment score (based on scoring scheme)	
					}

					if (numSeqChecked == 0) {
						currMin = currScore;
						finalScore = currSumScore; 
						optimalIndex = numSeqChecked;
						best_centerOrder_I = centerOrder_I;
						centerOrder_I = new ArrayList<Integer>();
					}
					else {
						if (currScore < currMin) {
							currMin = currScore;
							finalScore = currSumScore;
							optimalIndex = numSeqChecked;
							best_centerOrder_I = centerOrder_I;
							centerOrder_I = new ArrayList<Integer>();
						}
					}

					gapArray.clear();
					mismatchArray.clear();
					centerOrder_I.clear();
					centerOrder_G.clear();

					numSeqChecked++;
				}

				// Where k is the current group
				tempIdent = identGroups.get(k).get(0);				// we are moving the optimal to the first positions
				tempSeq = identGroups.get(k).get(1);				// in each grouping of sequences

				identGroups.get(k).set(0, ourIdents.get(optimalIndex));
				identGroups.get(k).set(1, ourGroup.get(optimalIndex));

				identGroups.get(k).set((2*optimalIndex), tempIdent);		// move to position of the consensus sequence
				identGroups.get(k).set((2*optimalIndex)+1, tempSeq);

				ourGroup.clear();
				ourIdents.clear();
				ourIdents.clear();

				finalScore /= (identGroups.get(k).size()/2); 
				System.out.println( "Alignment score per sequence: " + Integer.toString(finalScore));
	
				for (j = 0; j < identGroups.get(k).size(); j++) {
					System.out.println(identGroups.get(k).get(j));
				}
				/*for (j = 0; j < best_centerOrder_I.size(); j++) {
					System.out.println(identGroups.get(k).get(best_centerOrder_I.get(j)*2));
					System.out.println(identGroups.get(k).get(best_centerOrder_I.get(j)*2+1));
					}*/
			}
		}
	}
	
	// Gives base pair count for a provided sequence - numtocount limits the amount of bases we sum 
    public static void countChars(String name, String in_sequence, int numtocount) {

    	String sequence = in_sequence;
    	
    	// If we can actually truncate the read
    	if (in_sequence.length() > numtocount) {
    		sequence = in_sequence.substring(0, numtocount);
    	}
 
        int[] counts = {0,0,0,0};

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
    
   /* public static ArrayList<Integer> deepCopy(ArrayList<Integer> in) {
    	ArrayList<Integer> out = new ArrayList<Integer>();
    	for (int x = 0; x < in.size(); x++) {
    		out.add(in.get(x));
    	}
    	return out;
    }*/
}

