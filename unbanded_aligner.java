/*
 * Alignment object section of Hot RAD
 * 
 * Base code: Nicholas LaRosa
 * Modifications/Refactoring: Lauren Assour
 * 
 * Keeping unbanded version for posterity's sake
 * Last Modified: 15 August 2014 - LAA
 * 
 */

public class unbanded_aligner {
    int gap_score = 0;    		// local alignment scores
    int mismatch_score = 0;		// default scoring scheme
    int match_score = 1;

    int min_initial;
    int testarea;

    int maxsize;
    int overhang;
    
    String centerSeq;
    String otherSeq;
    
    final int diagInt = 0;
    final int upInt = 1;
    final int leftInt = 2;
    
    int maxDimension = 5000;
    
    int[][] values = new int[maxDimension][maxDimension];                 	// this will be our matrix of quality scores
    int[][] trace = new int[maxDimension][maxDimension];                  	// this will be our matrix of tracebacks

    int[] percentValues1 = new int[maxDimension];			// these two arrays keep track of our percent identity row sums
    int[] percentValues2 = new int[maxDimension];
    int[] finalPercentValues = new int[maxDimension];		// this array will store the final values where match = +1, others are +0

    public void load(int scoreM, int scoreMM, int scoreG, int maxSize, int over, int init, int area) {	// prepare the matrices
		int i;
		maxsize = maxSize;
		testarea = area;
		min_initial = init;
		overhang = over;

		if (scoreM < 0) {
			match_score = -1*scoreM;	// match must be positive
		}
		else {
			match_score = scoreM;	
		}
	
		if (scoreMM < 0) {
			mismatch_score = scoreMM;	// mismatch must be negative
		}
		else {
			mismatch_score = -1*scoreMM;
		}
	
		if (scoreG < 0) {
			gap_score = scoreG;		// gap must be negative
		}
		else {
			gap_score = -1*scoreG;
		}

		values[0][0] = 0;
		trace[0][0] = -1;
		percentValues1[0] = 0;
		finalPercentValues[0] = 0;

		for (i = 1; i < maxDimension; i++) {			// row of gap scores (row1)
			values[i][0] = values[i-1][0] + gap_score;
			trace[i][0] = upInt;
			percentValues1[i] = percentValues1[i-1];	// gap score is +0 for this array
		}
			
		for (i = 1; i < maxDimension; i++) {			// column of gap scores (column1)
			values[0][i] = values[0][i-1] + gap_score;
			trace[0][i] = leftInt;
		}
	}

	public void clear()	{					// this function resets our percent identity trackers
		int i;
		
		for (i = 0; i < maxDimension; i++) {                               // row of gap scores (row1)
			percentValues1[i] = 0;        			// gap score is +0 for this array
            percentValues2[i] = 0;
			finalPercentValues[i] = 0;
		}
	}

	public boolean align(String refStr, String readStr, double myPercentIdent) {		// the function will return false if there is no match 
										// or true if there is a match
		String seq1 = "";           	// LONGER
        String seq2 = "";          	// SHORTER

      	int m;                   	// number of rows       (length of first sequence in file)
      	int n;                  	// number of columns    (length of second sequence in file)
       	int j;
       	int i;

      	int q_up;
       	int q_left;

		int Sij;

		int percentMax = 0;		// this is the maximum alignment number when match = +1		
    	double percentIdent;            // if our percentIdent > 10%, we have a successful match

		int percent_Sij;		// values for finding percent identity
		int percent_up;
		int percent_left;

        if (refStr.length() >= readStr.length()) {
        	seq1 = refStr;                 		// reference is longer
            seq2 = readStr;
		}
        else {
        	seq1 = readStr;				// read is longer
        	seq2 = refStr;
        }
		
	if (seq1.length() > maxsize) {
	    seq1 = seq1.substring(0, maxsize);
	}
	if (seq2.length() > maxsize) {
	    seq2 = seq2.substring(0, maxsize);
	}

		seq1 = " " + seq1;				// firstSeq is longer and will be the rows of our matrix
        seq2 = " " + seq2;

        m = seq1.length(); 			     	// m = rows, n = columns
        n = seq2.length();

	int max_match = 0;
	if (testarea+1 < m && testarea+1 < n) {
	    for (int t = 0; t < overhang; t++) {
		int tempmatcha = 0;
		int tempmatchb = 0;
		for (int k = 1; k < testarea+1; k++) {
		    //System.out.println("M: " + m + " N: " + n + " K: " + k + " T: " + t + " Testarea: " + testarea);
		    if (k+t >= n) {
			    break;
			}
		    try {
		    if (Character.toLowerCase(seq1.charAt(k)) == Character.toLowerCase(seq2.charAt(k+t))) {
			tempmatcha++;
			// System.out.println(t + " - " + seq1.charAt(k) + " " + Character.toLowerCase(seq2.charAt(k+t)));
		    }
		    }
		    catch (Exception e) {
			System.out.println("M: " + m + " N: " + n + " K: " + k + " T: " + t + " Testarea: " + testarea);
			System.exit(1);
		    }
		    
		}
		if (tempmatcha > max_match) {
		    max_match = tempmatcha;
		}
		if (tempmatcha >= min_initial) {
		    break;
		}
		for (int k = 1; k <testarea+1; k++) {
		    if (Character.toLowerCase(seq1.charAt(k+t)) == Character.toLowerCase(seq2.charAt(k))) {
			tempmatchb++;
			//System.out.println(t + " - " + seq1.charAt(k+t) + " " + Character.toLowerCase(seq2.charAt(k)));
		    }
		}
		if (tempmatchb > max_match) {
		    max_match = tempmatchb;
		}
		if (tempmatchb >= min_initial) {
		    break;
		}
	    }
		
	    if (max_match < min_initial) {
		//System.out.println("FAILURE");
		return false;
	    }
	}
        for (i = 1; i < m; i++)  {        		// each row             // we are calculating left to right             
        	for( j = 1; j < n; j++) {  		// each column
        		if (Character.toLowerCase(seq1.charAt(i)) == Character.toLowerCase(seq2.charAt(j))) {
        			Sij = match_score;
        			percent_Sij = 1;
				}
        		else {
        			Sij = mismatch_score;
        			percent_Sij = 0;
				}
	
				values[i][j] = values[i-1][j-1] + Sij;
				trace[i][j] = diagInt;

				percentValues2[j] = percentValues1[j-1] + percent_Sij;		// our match value (+1) will be added only when there is
				
				//System.out.println("#" + percentValues2[j]);
				// a match according to our current scoring scheme
                q_up = values[i-1][j] + gap_score;
                q_left = values[i][j-1] + gap_score;
	
				percent_up = percentValues1[j];			// percent identity up
				percent_left = percentValues2[j-1];		// percent identity down
                 
				if ((q_left > values[i][j]) && (q_left > q_up)) {                	// max is left
					values[i][j] = q_left;
					trace[i][j] = leftInt;
					percentValues2[j] = percent_left;
				}
				else if (q_up > values[i][j]) {                                   	// max is up
					values[i][j] = q_up;
					trace[i][j] = upInt;
					percentValues2[j] = percent_up;
                }
        	}

			for (j = 1; j < n; j++) {
				percentValues1[j] = percentValues2[j];		// moving the second array to the first because we only use 2 arrays
			}				

			finalPercentValues[i] = percentValues2[n-1];		// this is the final number in the columns when match = +1
        }

		/*
		for(i = 0; i < m; i++)
		{
			//for(j = 0; j < n; j++)
			{
				System.out.print(Integer.toString(finalPercentValues[i]) + "\t");
			}
			System.out.println("");
		}
		*/

        percentMax = finalPercentValues[0];					

		for (i = 1; i < m; i++)   {       			// each row             // time to get coordinates of each maximum
			if (finalPercentValues[i] >= percentMax) {
				percentMax = finalPercentValues[i];
			}
		}

		percentIdent = percentMax/(double)(n);		// n is the length of secondSeq (smaller sequence)
	
		//System.out.println(Integer.toString(percentMax));
		//System.out.println(Double.toString(percentIdent));

        if (percentIdent >= myPercentIdent) {		// user specified percent identity
			return true;				// we have a successful alignment
		}
			
		return false;
	}

	public sequence align(String centerSeq, String otherSeq)	// output sequence object (string and int)	
	{ 	
		this.centerSeq = centerSeq;
		this.otherSeq = otherSeq;           

		int m;                   	// number of rows       (length of first sequence in file)
      	int n;                  	// number of columns    (length of second sequence in file)
      	int j;
       	int i;

      	int q_up;
       	int q_left;

		int Sij;
      	int score;

		sequence seqInfo = new sequence();	// hold our information (sequence with gaps and alignment score)

		int alignScore = 0;					// the two values we will put in the sequence object
		String gappedCenter = "";     		
	
		centerSeq = " " + centerSeq;				// firstSeq is longer and will be the rows of our matrix
        otherSeq = " " + otherSeq;

        m = otherSeq.length(); 					// m = rows, n = columns, centerSeq will be on top of matrix
        n = centerSeq.length();

        for (i = 1; i < m; i++)  {        			// each row             // we are calculating left to right
        	for (j = 1; j < n; j++)  {			// each column
        		if (Character.toLowerCase(otherSeq.charAt(i)) == Character.toLowerCase(centerSeq.charAt(j))) {
        			Sij = match_score;
                }
				else if ((otherSeq.charAt(i) == '-') || (centerSeq.charAt(j) == '-')) {	// make sure we account for matching to gaps
					Sij = gap_score;
				}	
                else {
                	Sij = mismatch_score;
                }
	
				values[i][j] = values[i-1][j-1] + Sij;
				trace[i][j] = diagInt;

                q_up = values[i-1][j] + gap_score;
                q_left = values[i][j-1] + gap_score;

                if (n >= m) {							// rather put a gap in left (centerSeq, n, is up)
					if ((q_up > values[i][j]) && (q_up > q_left)) {        	// max is up
						values[i][j] = q_up;
						trace[i][j] = upInt;
					}
					else if (q_left > values[i][j]) {                       	// max is left
						values[i][j] = q_left;
						trace[i][j] = leftInt;
                    }
				}
				else {								// rather put a gap in 
					if ((q_left > values[i][j]) && (q_left > q_up)) {       	// max is up
						values[i][j] = q_left;
						trace[i][j] = leftInt;
					}
					else if (q_up > values[i][j]) {  	               	// max is left
						values[i][j] = q_up;
						trace[i][j] = upInt;
					}
				}
				
        	}
        }

		// Now it is time for the traceback. We only care about adding gaps to the centerSeq
		i = m - 1;
		j = n - 1;

		score = values[i][j];

		while ((i > 0) && (j > 0)) { 			// we will go through the traceback until we reach C(0,0)
			if (trace[i][j] == diagInt) {		// no gaps added
				gappedCenter = centerSeq.substring(j,j+1) + gappedCenter;		// appending string in reverse
				if (Character.toLowerCase(otherSeq.charAt(i)) != Character.toLowerCase(centerSeq.charAt(j))) {	// add to alignment score if mismatch	
					alignScore++;				
                }
				
				j--;
				i--;
			}
			else if (trace[i][j] == leftInt) {		// adds a gap in otherSeq (left)
				gappedCenter = centerSeq.substring(j,j+1) + gappedCenter;
				alignScore++;
				j--;
			}
			else {					// adds a gap in centerSeq (up)
				gappedCenter = "-" + gappedCenter;
				alignScore++;								// add to alignment score if gap added
				i--;

			}
		}

		if ((i > 0) || (j > 0)) {		// there are still letters to be printed
			if (i > 0) {		// there are leftover bases in otherSeq (gaps in centerSeq)
				while (i > 0) {
					gappedCenter = "-" + gappedCenter;
					alignScore++;
					i--;
				}
			}
			else {		// there are leftover bases in centerSeq (gaps in otherSeq)
				while (j > 0) {
                    gappedCenter = centerSeq.substring(j,j+1) + gappedCenter;
				 	alignScore++;	
					j--;
				}
			}
		}
		
		seqInfo.newSeqDetails(gappedCenter, alignScore, score);
		return(seqInfo);					// return our sequence object 	
	}

	public int[] getArrayValues(String centerSeq, String otherSeq) {	// output the number of gaps added to centerSeq and the number of mismatches
		this.centerSeq = centerSeq;
		this.otherSeq = otherSeq;           

		int m;                   	// number of rows       (length of first sequence in file)
      	int n;                  	// number of columns    (length of second sequence in file)
      	int j;
       	int i;

      	int q_up;
       	int q_left;

		int Sij;
	
		int[] arrayValues = new int[2];		// holds the amount of gaps [0] and mismatches [1] with the getArrayValues method

		int numberGaps = 0;					// the two values we will put in the output array
		int numberMismatches = 0;     		
	
		centerSeq = " " + centerSeq;				// firstSeq is longer and will be the rows of our matrix
        otherSeq = " " + otherSeq;

        m = otherSeq.length(); 					// m = rows, n = columns, centerSeq will be on top of matrix
        n = centerSeq.length();

        for (i = 1; i < m; i++) {         			// each row             // we are calculating left to right
        	for (j = 1; j < n; j++) {		// each column
        		if (Character.toLowerCase(otherSeq.charAt(i)) == Character.toLowerCase(centerSeq.charAt(j))) {
        			Sij = match_score;
        		}
				else if ((otherSeq.charAt(i) == '-') || (centerSeq.charAt(j) == '-')) {	// make sure we account for matching to gaps
					Sij = gap_score;
				}	
                else {
                	Sij = mismatch_score;
                }
	
				values[i][j] = values[i-1][j-1] + Sij;
				trace[i][j] = diagInt;

                q_up = values[i-1][j] + gap_score;
                q_left = values[i][j-1] + gap_score;

                if (n >= m) {							// rather put a gap in left (centerSeq, n, is up)
					if ((q_up > values[i][j]) && (q_up > q_left)) {        	// max is up
						values[i][j] = q_up;
						trace[i][j] = upInt;
                    }
					else if (q_left > values[i][j]) {                       	// max is left
						values[i][j] = q_left;
						trace[i][j] = leftInt;
					}
				}
				else {								// rather put a gap in up
					if ((q_left > values[i][j]) && (q_left > q_up))  {    	// max is up
						values[i][j] = q_left;
						trace[i][j] = leftInt;
					}
					else if (q_up > values[i][j]) {  	               	// max is left
						values[i][j] = q_up;
						trace[i][j] = upInt;
					}
				}
				
        	}
        }

		// Now it is time for the traceback. We only care about adding gaps to the centerSeq

		i = m - 1;
		j = n - 1;

		while ((i > 0) && (j > 0)) { 			// we will go through the traceback until we reach C(0,0)
			if (trace[i][j] == diagInt) {		// no gaps added
				if (Character.toLowerCase(otherSeq.charAt(i)) != Character.toLowerCase(centerSeq.charAt(j))) {	// add to mismatch count if mismatch	
					numberMismatches++;				
				}
				j--;
				i--;
			}
			else if (trace[i][j] == leftInt)	{	// adds a gap in otherSeq (left)
				numberMismatches++;
				j--;			
			}
			else {					// adds a gap in centerSeq (up)
				numberGaps++;			// add to gap count if gap added
				numberMismatches++;
				i--;
			}
		}

		if ((i > 0) || (j > 0)) {		// there are still letters to be printed
			if (i > 0) {			// there are leftover bases in otherSeq (gaps in centerSeq)
				while (i > 0) {
					numberGaps++;
					numberMismatches++;
					i--;
				}
			}
			else {				// there are leftover bases in centerSeq (gaps in otherSeq)
				while (j > 0) {
					numberMismatches++;
					j--;
				}
			}
		}
		
		arrayValues[0] = numberGaps;				// prepare output
		arrayValues[1] = numberMismatches;

		return(arrayValues);					// return our array of alignment values
	}		

}

