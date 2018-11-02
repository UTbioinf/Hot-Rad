#/usr/bin/python
# Takes as input makefile, modifies to add 'reduce' steps
# Saves a modified makefile called makefile.temp
#
# Usage: python mergewriter.py makefile configfile
# 
# Last Modified: 20 Jan 2014 - LAA

import sys, math

file = open(sys.argv[1])
outs = []

config = sys.argv[2].split("/")[-1]
outfile = open(sys.argv[1]+".temp", 'w')

# First 3 lines we don't care about as they are the split command
line = file.readline()
outfile.write(line)
outfile.write(file.readline())
outfile.write(file.readline())
line = file.readline()

# Get filenames
while line != "":
	outfile.write(line)
	parts = line.split(":")
	outs.append(parts[0])
	# Skip every other line as it's the command
	outfile.write(file.readline())
	line = file.readline()

file.close()
#outfile = open(sys.argv[1]+".temp", 'w')

numlevels = math.ceil(math.log(len(outs), 2))

levels = 0

# Number of times we want a character to appear for temp name
filemultiplier = 3

while levels <= numlevels:
	i = len(outs)-1
	newouts = []
	
	# Names by char (65 = A)
	filenames = 65

	# Not even number of outfiles, so we want to treat one original output as a new one
	if len(outs)%2 != 0:
		newouts.append(outs[i])
		i -= 1

	while i > 0:
		# Don't need to write a full merge because only two files
		if len(outs) == 2:
			outfile.write("final1.temp: final_clean.py " + outs[i-1] + "\n")
			outfile.write("\tpython final_clean.py 2 " + outs[i-1] + " final1.temp\n")
			outfile.write("final2.temp: final_clean.py " + outs[i] + "\n") 
                        outfile.write("\tpython	final_clean.py 2 " + outs[i] + " final2.temp\n")
			outfile.write("final.txt: localassemble.sh assembLocal.jar final1.temp final2.temp " + config + "\n")
                	outfile.write("\tlocalassemble.sh assembLocal.jar " + config + " final1.temp final2.temp final.txt\n")
			break
		#Make sure only letters in names
		if filenames == 91:
			filenames = 97
		if filenames == 123:
			filemultiplier += 1
			filenames = 65		
		outfile.write(chr(filenames)*(filemultiplier) + ".temp: localassemble.sh assembLocal.jar " + outs[i-1] +  " " + outs[i] + " " + config + "\n")
		outfile.write("\tlocalassemble.sh assembLocal.jar " + config + " " + outs[i-1] + " " + outs[i] + " "  + chr(filenames)*(filemultiplier) + ".temp\n")
		
		newouts.append(chr(filenames)*(filemultiplier) +".temp")
		i -= 2
		filenames += 1	
	outs = newouts
	levels += 1
	filemultiplier += 1

outfile.close()
