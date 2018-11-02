/* 
 * Stores the current minimum SP alignment sequence (gaps included) and its alignment score (from the center star tree alignment)
 * 
 * Base code: Nicholas LaRosa
 * Modifications/Refactoring: Lauren Assour
 * 
 * Last Modified: 14 Jan. 2014 - LAA
 * 
 */

public class sequence {
	private String seq = "";
	private String ident = "";

	private int alignScore = 0;			// number of gaps or mismatches	
	private int sumScore = 0;			// alignment score based on scoring scheme

	public void newSeqDetails(String seq, int alignScore, int sumScore) {
		this.seq = seq;			// This is the sequence with matches
		this.alignScore = alignScore;
		this.sumScore = sumScore;
	}

	public void addIdent(String ident) {
		this.ident = ident;
	}

	public String getSeq() {
		return(seq);
	}

	public int getScore() {
		return(alignScore);
	}

	public int getSumScore() {
		return(sumScore);
	}

	public String print() {
		return("Seq " + ident + " gives the optimal multiple alignment with an alignment score of " + alignScore + ".\n\n\t" + seq + "\n");
	}  
}

