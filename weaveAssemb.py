# Nicholas LaRosa

import sys
import re
import os
import mmap
import math

from weaver.dataset import FileList
from weaver.function import ParseFunction, ShellFunction, Function 
from weaver.nest import Nest
from array import array

########################
# Establish our inputs #
########################

match_score = 1;			# this will be our standard scoring scheme
mismatch_score = 0;
gap_score = 0;

if len(sys.argv) <= 5:						# if we don't recieve the proper arguments
	raise Exception("'weaver.py <arguments> weaveAssemb.py <database file> <number of segments> <configfile> <-q/a>'")

configfile = sys.argv[len(sys.argv)-2]
templist = configfile.split("/")
configfile = templist[len(templist)-1]

print configfile

if str(sys.argv[len(sys.argv)-1]) == '-a':			# FASTA
	fileFormat = 'a'
	myFile = sys.argv[len(sys.argv)-4]
	segments = int(sys.argv[len(sys.argv)-3])
#	percentIdent = float(sys.argv[len(sys.argv)-2])	

elif str(sys.argv[len(sys.argv)-1]) == '-q':			# FASTQ
	fileFormat = 'q'
	myFile = sys.argv[len(sys.argv)-4]
	segments = int(sys.argv[len(sys.argv)-3])
#	percentIdent = float(sys.argv[len(sys.argv)-2])	

#elif str(sys.argv[len(sys.argv)-4]) == '-scores' and str(sys.argv[len(sys.argv)-5]) == '-a':	# unique scoring scheme, FASTA
#	fileFormat = '-a'
#	myFile = str(sys.argv[len(sys.argv)-8])	
#	segments = int(sys.argv[len(sys.argv)-7])
#	percentIdent = float(sys.argv[len(sys.argv)-6])	
#	match_score = int(sys.argv[len(sys.argv)-3])
#	mismatch_score = int(sys.argv[len(sys.argv)-2])
#	gap_score = int(sys.argv[len(sys.argv)-1])

#elif str(sys.argv[len(sys.argv)-4]) == '-scores' and str(sys.argv[len(sys.argv)-5]) == '-q':	# unique scoring scheme, FASTQ
#	fileFormat = '-q'
#	myFile = str(sys.argv[len(sys.argv)-8])	
#	segments = int(sys.argv[len(sys.argv)-7])
#	percentIdent = float(sys.argv[len(sys.argv)-6])	
#	match_score = int(sys.argv[len(sys.argv)-3])
#	mismatch_score = int(sys.argv[len(sys.argv)-2])
#	gap_score = int(sys.argv[len(sys.argv)-1])

else:								# standard scoring scheme, FASTA	
	fileFormat = 'a'
	myFile = sys.argv[len(sys.argv)-4]       
        segments = int(sys.argv[len(sys.argv)-3])             
        #percentIdent = float(sys.argv[len(sys.argv)-1])      

if int(segments) == 0:						# user specified 0 segments, stop weaver
	raise Exception("Please specify more than 0 segments: 'weaver.py <arguments> weaveAssemb.py <database file> <number of segments> <configfile> <-q/a>")	

#if int(percentIdent) > 100 or int(percentIdent) <= 0:
#	raise Exception("Please specify a percent identity between 0 and 100.")

#if match_score == 0 and mismatch_score == 0 and gap_score == 0:
#	raise Exception("Please specify an appropriate scoring scheme.")

##########################################################################
# Establish actual amount of segments (make sure they are adequate size) #
##########################################################################

numIdent = 0
totalLines = 0
maxSegments = 0
myFile = os.path.abspath(myFile)

dbFile = open(myFile, "r")			# opens file for reading

for line in dbFile:
        if re.match("^>", line) or re.match("^@", line):		# this is regex in Python
                numIdent += 1						# get number of identifiers
	totalLines += 1							# get number of lines

dbFile.close()					# close file

maxSegments = math.floor(numIdent/2)      	# we need a minimum of 2 identifiers for each segments

if segments > maxSegments:
        segments = maxSegments;    		# intervene if use specifies too many segments

if segments == 0:				# our database file must be empty, stop weaver
	raise Exception('It seems your database file contains less than two identifiers. Please check ' + myFile + '.');

##########################################
# Check arguments for new directory (-o) #
##########################################

found = 0
newDir = ''

for arg in sys.argv:
	if found == 0:	
		if arg == "-o":					# string comparison
			found = 1
	else:							# new directory found
		newDir = arg
		break

#######################
# Create the datasets #
#######################

i = 0
partStr = ''							# string to hold path name for parts.txt
shellScript = './assemble.sh'

if newDir == '':	
	newDir = '.'
	partStr = 'parts.txt'
else:
	partStr = newDir + 'parts.txt'
	#myFile = '../' + myFile					# change database path also
	shellScript = '.' + shellScript

partsFile = open(partStr,'w')					# opens a new file for writing

for i in range(1,(int(segments)+1)):				# write a file for every part (range is exclusive)
	partsFile.write(newDir + "part%d.txt\n" % i)		# this writes to a file, str() converts int to string
								# %d is a placeholder for a digit, %s for a string
partsFile.close()						# close file

######################
# Construct Makeflow #
######################

partSet = FileList(partStr)
inputSet = [myFile]									# our database file
scriptExe = [shellScript, configfile]

print inputSet

splitIt = Function('split.pl', cmd_format = 'perl {executable} {inputs} {arguments}')	# split the database file into desired segments
splitIt(inputs = myFile, outputs = partSet, arguments = int(segments), local = True)

assembler = Function('assembDist.jar', cmd_format = ('%s {executable} {inputs} ' % os.path.abspath('./assemble.sh'))+configfile+' '+fileFormat+' {outputs}') # this is our aligning function

outs = Map(assembler, partSet, includes = scriptExe)					# run alignments, get outputs 

#Merge(outs, 'result.txt', local = True)							# join outputs with Merge


