# Hot RAD
####Last Modified: 24 September 2014 LAA

It's recommended to add both the main directory and the python directory to your path
Example fastq files, barcode file, and config file in example folder

#####Dependencies:
Python v.2.x or below

Java v. --

#####Optional dependencies:
Makeflow: http://www3.nd.edu/~ccl/software/makeflow/

Weaver: https://bitbucket.org/pbui/weaver


=======

#####TABLE OF CONTENTS: 
*Build Info

*Run with example data locally

*Usage with Makeflow/Weaver

*GUI

*Usage without Makeflow/Weaver (local, single core)

*Config File Info

*Unbanded Alignment


=======

#####BUILD INFO:

Rebuild jars using:
 
			./buildjars

=======

#####RUN WITH EXAMPLE DATA LOCALLY:

Step 1 (Trim off barcodes and cut sites):
	
			python python/radtag0_trimmer.py example/test_barcodes.txt example/shuffled_long_test_data.fq

Step 2 (Align reads):
	
			assemble.sh assembDist.jar shuffled_long_test_data.trimmed.fq example/test_config.txt q shuffled_long_results.txt

Step 3 (Clean results):
 
			python python/radtag1_makefakegenome.py -r -c 8 -m 90 shuffled_long_results.txt shuffled_long_seqs.fa

=======

#####USAGE WITH MAKEFLOW/WEAVER:
Step 1 (demultiplex reads):
     
			python python/radtag0_trimmer.py <barcodefile> <fasta/fastq file> 

To see options call python python/radtag0_trimmer.py -h

Step 2 (build Makeflow):	

			construct.sh <trimmed_sequence_file> <number_of_splits> <configfile> <-q/a> <destination_directory>

file formats:   for FASTA data          -a
                for FASTQ data          -q
                
See below for config file options

			cd <destination_directory>

Step 3 (run Makeflow):         
case 1: running locally

			makeflow
        
case 2: running on WorkQueue

			screen -dmS myPool 'work_queue_pool -Tcondor -M <project_name> <number_of_workers>'
			screen -dmS myMaster sh -c 'time makeflow -Twq -p <port_number> -a -N <project_name>'

Note: for AFS users at Notre Dame, specify a port number between 9000 and 10000

Optional:
(filter results):

   			python python/radtag1_makefakegenome.py -r <final.txt> <output>

use -h to view all options
-r specifies Hot RAD assembler used

(rename results):
	
			mv final.txt <new_file1>
			mv Makeflow.makeflowlog <new_file2>
   			mv Makeflow.wqlog <new_file3>

(clean up):             

			makeflow -c

=======
#####GUI

A shell script to do the aforementioned steps can be created using the GUI:

			java -jar HRGUI.jar

GUI manual: hotradmanual_30Jan2014.pdf

=======

#####USAGE WITHOUT MAKEFLOW/WEAVER (local, single core):
Step 1:
If needed (using non-demultiplexed reads):
      
			python python/radtag0_trimmer.py <barcodefile> <fasta/fastq file>
     
To see options call python python/radtag0_trimmer.py -h

Step 2:

			assemble.sh assembDist.jar <trimmed_sequence_file> <configfile> <fast(q)/fast(a)> <outputfile>
Step 3:
If FASTA file of representative sequences desired:

			python python/radtag1_makefakegenome.py -r <final.txt> <output>

=======

#####CONFIG FILE INFO:

Listed values are defaults

*"Cost" of a match

	matchScore=1 (zero/positive integer)

*Cost of a mismatch (zero/negative int)

	mismatchScore=0

*Cost of a gap (zero/negative int)

	gapScore=0

*Percent identity of sequences to count as sufficiently aligned (0-100)

	percentIdent=90

*Number of bases to compare base counts 

	countLen=50

*Number of allowed differences in base counts

	filtMaxDiff=5

*Minimum group size to continue from distributed assembly

	minGroupSizeD=3

*Maximum number of bases to align 

	maxAlign=100

*Number of bases to test for base counts (see Section 3.1 of pdf manual)

	testArea=8

*Minimum number of bases to match in test area

	minInitial=5

*Maximum overhang length (Section 3.1 of pdf manual)

	overhang=3

*Size of k band for banded alignment

	kband=8

#####UNBANDED ALIGNMENT: 
Hot RAD was originally written with basic Smith-Waterman alignment. While creating a large enough band
is essentially the same thing, the original unbanded alignment code is contained in the unbanded_*.java files.
