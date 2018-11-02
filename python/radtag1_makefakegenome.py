#!/usr/bin/python
# Via contig fasta file from SeqMan or Hot RAD
# Which contigs are good enough to 
# use as a "reference"
#
# Last Edit: 30 Jan 2014 - LAA - added Hot RAD assembler support

import string, sys, optparse
from optparse import OptionParser

parser = OptionParser(usage="usage: %prog [options] INPUT_FASTA OUTPUT_FASTA")

parser.add_option("-r", "--hotrad", dest="hotrad", action="store_true", help="Hot RAD input file as opposed to SeqMan");
parser.add_option("-c", "--coverage", dest="coverage", help="minimum coverage of contig to be considered valid \nDefault=7", default=7, metavar="INTEGER")
parser.add_option("-m", "--minlen", dest="minlen", help="minimum length of contig to be considered valid \nDefault=105",type="int", default=105, metavar="INTEGER")
parser.add_option("-x", "--maxlen", dest="maxlen", help="maximum length of contig to be considered valid \nDefault=113", type="int", default=113, metavar="INTEGER")
parser.add_option("-s", "--singlecontig", action="store_true", dest="single", help="create one single contig separated by Ns")
parser.add_option("-n", "--numns", dest="numns", help="if -s flag set, number of Ns to pad contigs \nDefault=30", type="int", default=30, metavar="INTEGER")
parser.add_option("-q", "--quiet", action="store_true", dest="quiet", help="do not print extra information to STDOUT")
#parser.add_option("-g", "--config", dest="config", help="optional config file containing all options", metavar="FILE")
(options, args) = parser.parse_args()

# If incorrect number of arguments were given, print a helpful message
if len(args) != 2:
        print ""
        parser.print_usage()
        sys.exit(0)

infile = open(args[0])
outfile = open(args[1], 'w')

singlecon = options.single
hotrad = options.hotrad
minlen = int(options.minlen)
maxlen = int(options.maxlen)
mincov = int(options.coverage)
good = 0
all = 0
covproblem = 0
lenproblem = 0
string = ""
nstring = "N"*options.numns
currloc = 0

line = infile.readline()
if singlecon:
	outfile.write(">FakeGenome\n")

seqcounter = 0
firstid = ""
firstseq = ""

# Read another line to get rid of first info line for locus
if hotrad:
	if "Alignment" in line:
		line = infile.readline()

while line != "":
	if hotrad:
		while line != "" and (line[0] == "@" or line[0] == ">"):
			if firstid == "":
				firstid = line
				firstseq = infile.readline().strip()
			else:
				line = infile.readline()
			seqcounter += 1
			line = infile.readline()

		all += 1
		if seqcounter < mincov:
			covproblem += 1
		elif len(firstseq) < minlen or len(firstseq) > maxlen:
			lenproblem += 1
		if seqcounter >= mincov and len(firstseq) >= minlen and len(firstseq) <= maxlen:
			if singlecon:
				string += firstseq + nstring
			else:
				outfile.write(">" + firstid[1:] + firstseq + "\n")
			good += 1
		firstid = ""
		firstseq = ""
		seqcounter = 0
		line = infile.readline()

	else:
		if line[0] == ">":
			temp = line.split(" ")
			if int(temp[1]) >= mincov:
				if int(temp[3]) >= minlen and int(temp[3]) <= maxlen:
					tline = infile.readline().strip()
					thisseq = ""
					while tline[0] != ">":
						thisseq += tline
						tline = infile.readline().strip()
						if singlecon:
							string += thisseq + nstring
						else:
					                #Print out start and end positions for this seq
					                #This can be used in analysis (seqmanstats)
							outfile.write(line.strip() + " " + str(currloc) + " " + str(currloc+len(thisseq)+numns-1) + "\n")
							currloc += len(thisseq) + numns
							outfile.write(thisseq + "\n")
							good += 1
							line = tline
					else:
						line = infile.readline()
						lenproblem += 1
				else:
					line = infile.readline()
					covproblem += 1
					all += 1
			else:
				line = infile.readline()

if singlecon:
 	outfile.write(string + "\n")
infile.close()
outfile.close()

if options.quiet != True:
	print "Total contigs: " + str(all)
	print "Kept contigs: " + str(good)
	print "Lost due to coverage: " + str(covproblem)
	print "Lost due to length: " + str(lenproblem)
