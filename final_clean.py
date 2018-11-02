#!/usr/local/bin/python
# Remove all groups with only one seq
# 15 April 2013 LAA

import string, sys

# If no arguments were given, print a helpful message
if len(sys.argv)!=4:
	print 'Usage: python final_clean.py <mincoverage> <inputfile> <outputfile>'
	sys.exit(0)

mincov = int(sys.argv[1])
inputfile = open(sys.argv[2])
outputfile = open(sys.argv[3], 'w')

seqbuffer = ""
counter = 0

first = 0

for line in inputfile:
	if line == "\n" or "Alignment" in line:
		if counter >= mincov:
			if first == 0:
				first = 1
			else:
				outputfile.write("\n")
			outputfile.write(seqbuffer)
		counter = 0
		seqbuffer = ""
	else:
		seqbuffer += line
		if line[0] == "@" or line[0] == ">":
			counter += 1

if counter >= mincov:
	outputfile.write("\n" + seqbuffer)

inputfile.close()
outputfile.close()
