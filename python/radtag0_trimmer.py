#!/usr/bin/python
# Trim down sequences based on provided cut site/barcode length
# Trimmed seqs go to outfile, stats go to standard out
# Can have separate files for each individual
# Barcode correction included, also cut site removal
# Barcode file format: BARCODE	INFORMATION
# Can require all sequences to be the same length, for Stacks program
# Last Edit: 8 July 2014 - LAA - fixed qual issue with --keepall

import string, sys, optparse
from optparse import OptionParser

parser = OptionParser(usage="usage: %prog [options] BARCODE_FILE ILLUMINA_FILE")

parser.add_option("-c", "--cutsite", dest="cutsite", help="sequence of remaining end of cut site. Use 0 if no cut site \nDefault=AATTC (EcoR1)", default="AATTC", metavar="SEQUENCE")
parser.add_option("-a", "--searcharea", dest="searcharea", help="maximum offset to search for cutsite\n Default=4", type="int", default=4, metavar="INTEGER")
parser.add_option("-m", "--minmatch", dest="minmatch", help="minimum length of match to denote cut site found\n Default=length(cutsite)-1", type="int", metavar="INTEGER")
parser.add_option("-i", "--individuals", action="store_true", dest="sep", help="generate separate files for each individual", default=False)
parser.add_option("-s", "--samelen", action="store_true", dest="samelen", help="for Stacks program - cuts all seqs to same len", default=False)
parser.add_option("-l", "--length", dest="lenfors", help="if -s flag set, length to trim sequences to \nDefault=100", type="int", default=100, metavar="INTEGER")
parser.add_option("-n", "--numns", dest="numns", help="if sequence has greater than -n number of Ns, discard it \n Default=Null", type="int", metavar="INTEGER")
parser.add_option("-o", "--output", dest="outfile", help="if -i flag not set, output to this file\n Default=ILLUMINA_FILE.trimmed.fq", metavar="FILE")
parser.add_option("-p", "--pairedend", dest="pairedend", help="if paired-end reads, provide untagged (second set) of reads", metavar="FILE")
parser.add_option("-x", "--pairedendout", dest="pairedendout", help="if -p, set paired reads output\n Default=PAIRED_FILE.trimmed.fq", metavar="FILE")
parser.add_option("-r", "--minreadlen", dest="minreadlen", help="minimum length of read to retain\n Default=1", type="int", default=1, metavar="INTEGER")
parser.add_option("-v", "--lenavg", action="store_true", dest="lenavg", help="print out the average length of sequence per barcode", default=False)
parser.add_option("-b", "--barcorr", dest="barcorr", help="maximum number of bases to try to correct in barcode\t Default=1", type="int", default=1, metavar="INTEGER")
parser.add_option("-k", "--keepbar", action="store_true", dest="keepbar", help="retain barcodes in sequences; this DOES NOT search for cut sites, so is comparable to -c 0", default=False)
parser.add_option("-e", "--replen", dest="replacelen", help="replace paired reads with INTEGER number of Ns if they are at least in part a rev complement of the forward read. Set to 0 to not replace \n Default=20", default=20, metavar="INTEGER")
parser.add_option("-g", "--origheader", action="store_true", dest="origheader", help="keep original headers (no renaming)", default=False)
parser.add_option("-t", "--keepall", action="store_true", dest="keepall", help="keep all sequences regardless of finding cut site or barcode", default=False)
parser.add_option("-y", "--revcutsearch", dest="revcutsearch", help="number of bases to search for reverse cut site. 0 doesn't search \nDefault=0", default=0, metavar="INTEGER")
parser.add_option("-z", "--revcutsite", dest="revcutsite", help="the reverse read's cut site, to be removed if found \nDefault=AAT (Mse1)", default="AAT", metavar="SEQUENCE")
parser.add_option("-q", "--quiet", action="store_true", dest="quiet", help="do not print extra sequence information to ILLUMINA_FILE.info. Includes number of reads per barcode")
#parser.add_option("-g", "--config", dest="config", help="optional config file containing all options", metavar="FILE")
(options, args) = parser.parse_args()

# If incorrect number of arguments were given, print a helpful message
if len(args) != 2:
    	print ""
	parser.print_usage()
	sys.exit(0)

# If no	minmatch provided, calculate it
if options.minmatch == None:
        options.minmatch = len(options.cutsite)-1

# Get filename
fname = args[1].split("/")
fname = fname[len(fname)-1].split(".")
# If fname isn't already a single thing, join
if len(fname) != 1:
	fname = ".".join(fname[0:len(fname)-1])
else:
	fname = fname[0]

# Get paired-end stuff
if options.pairedend != None:
	pairedf = open(options.pairedend)
	pfname = options.pairedend.split("/")
	pfname = pfname[len(pfname)-1].split(".")
	pfname = ".".join(pfname[0:len(pfname)-1])
else:
	pairedf = None
	pairedout = None


options.revcutsearch = int(options.revcutsearch)

#If no output file specified and -i not set, generate name
if options.outfile == None and options.sep == False:
	filename_parts = args[0].split(".")
	options.outfile = fname + ".trimmed.fq" 

if options.pairedend != None and options.pairedendout == None and options.sep == False:
	options.pairedendout = pfname + ".trimmed.fq"

# Number of Ns
numns = options.numns
toomanyns = 0

# Individual lengths distribution
indivlength = {}

#If config file provided, get out all the args
barcodefile = open(args[0])
samelen = options.samelen
separate = options.sep
illufile = open(args[1])
minreadlen = options.minreadlen

if options.sep == False:
	fd = open(options.outfile, 'w')
	if options.pairedend != None: 
		pairedout = open(options.pairedendout, 'w')

# Keep track of length of seqs
length = {}
barcodes = {}
revbar = {}

# For multiple separate files
files = {}
pfiles = {}

# Keep track of number of each barcode
bcodecount = {}

# Count number of reverse cut sites trimmed
revtrimmed = 0

# Count number of bad barcodes
badbcode = 0

# Counter number of too short reads
tooshort = 0

# Barcode length
barlens = []

# End of cut site
cutsite = options.cutsite
#revcut = rev_compliment(options.cutsite)
# How much of the cut site needs to match to 
# 	count it as the cut site
minmatch = options.minmatch

# If we have no cut site
if cutsite == "0":
	cutsite = "";
	minmatch = 0;
	revcut = "";

# How far to search for cut site
searcharea = options.searcharea

# Number of Ns to replace bad paired end reads
seqreplace = int(options.replacelen)

# Number of seqs with barcodes corrected
corrected = 0

# Sequences skipped due to bad barcode, etc.
skipped = 0

# If all same length
mylength = options.lenfors

# Statistics
totreads = 0
totretreads = 0
avglen = 0

# Method to correct barcodes
def barcode_correction(seq, barlen):
	mybcode = seq[0:barlen]
	loc = 0
	seq4 = ""
	while loc < len(mybcode):
		if mybcode[loc] == 'N':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
                        seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]
                        seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
			seq4 = mybcode[0:loc]+'A'+mybcode[loc+1:]
		if mybcode[loc] == 'A':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if mybcode[loc] == 'T':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'A'+mybcode[loc+1:]
		if mybcode[loc] == 'C':
			seq1 = mybcode[0:loc]+'A'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if mybcode[loc] == 'G':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'A'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if seq1 in barcodes:
			return seq1                                                  

		if seq2 in barcodes:
			return seq2

		if seq3 in barcodes:
			return seq3                    
		
		if seq4 in barcodes:
			return seq4
                                   
		loc += 1
	return ""

# Stupidly redundant, but wanting to keep the original function clean and working
def multi_barcode_correction(mybcode, currloc, depth):
	if depth >= options.barcorr: 
		return ""

	barlen = len(mybcode)
	loc = currloc + 1
	seq4 = ""
	seq8 = ""

	while loc < len(mybcode):
		if mybcode[loc] == 'N':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
                        seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]
                        seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
			seq4 = mybcode[0:loc]+'A'+mybcode[loc+1:]
		if mybcode[loc] == 'A':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if mybcode[loc] == 'T':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'A'+mybcode[loc+1:]
		if mybcode[loc] == 'C':
			seq1 = mybcode[0:loc]+'A'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'G'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if mybcode[loc] == 'G':
			seq1 = mybcode[0:loc]+'C'+mybcode[loc+1:]
			seq2 = mybcode[0:loc]+'A'+mybcode[loc+1:]        
			seq3 = mybcode[0:loc]+'T'+mybcode[loc+1:]
		if seq1 in barcodes:
			return seq1                                                  

		elif seq2 in barcodes:
			return seq2

		elif seq3 in barcodes:
			return seq3                    
		
		elif seq4 in barcodes:
			return seq4
                    
		else:
			seq5 = multi_barcode_correction(seq1, loc, depth+1)
			seq6 = multi_barcode_correction(seq2, loc, depth+1)
			seq7 = multi_barcode_correction(seq3, loc, depth+1)
			if seq4 != "":
				seq8 = multi_barcode_correction(seq4, loc, depth+1)
			
			if seq5 in barcodes:
				return seq5
			if seq6 in barcodes:
				return seq6
			if seq7 in barcodes:
				return seq7
			if seq8 in barcodes:
				return seq8

		loc += 1

	return ""


def rev_compliment(seq):
	revstring = ""
	seq = seq.upper()
	x = len(seq)-1	
	while x >= 0:
		if seq[x] == "A":
			revstring += "T"
		elif seq[x] == "T":
			revstring += "A"
		elif seq[x] == "C":
			revstring += "G"
		elif seq[x] == "G":
			revstring += "C"
		else:
			revstring += seq[x]
		x -= 1

	return revstring


def find_cutsite(seq, barcode):
	if seq.rfind(revcut) == -1:
		return False
	else:
		if seq[seq.rfind(revcut):].rfind(barcode) == -1:
			return False
		else:
			return True

def find_revcutsite(seq, qual, revcut, area):
    counter = 0
    found = False
    while counter < int(area) and not found:
        if seq[counter:counter+len(revcut)] == revcut:
            seq = seq[counter+len(revcut):]
            qual = qual[counter+len(revcut):]
            found = True
	    break
        else:
            counter += 1
    return (seq, qual)

# Longest barcode
maxbarlen = 0

#Cutsite reverse
revcut = rev_compliment(options.cutsite)
overlapped = 0

# Separate out barcodes and barcode information
for line in barcodefile:
	parts = line.strip().split()
	barcodes[parts[0].upper()] = parts[1]
	revbar[parts[0].upper()] = rev_compliment(parts[0].upper())
	bcodecount[parts[0].upper()] = 0
	if len(parts[0]) not in barlens:
		if len(parts[0]) > maxbarlen:
			maxbarlen = len(parts[0])
		barlens.append(len(parts[0]))
	if separate:
		files[parts[1]] = open(parts[1] + "_" + parts[0] + "_" + cutsite + "_" + fname + ".fq", 'w')
		if options.pairedend != None:
			pfiles[parts[1]] = open(parts[1] + "_" + parts[0] + "_" + cutsite + "_" + pfname + ".fq", 'w')
barlens = sorted(barlens, reverse=True)

line = illufile.readline()
if pairedf != None:
	pline = pairedf.readline()
prevline = " "

while line != "":
	#Check to make sure this is actually a label
	if line[0] == "@" and prevline[0] != "+":
		rawseqid = line.strip()
		seqidparts = line.strip().split("/")
		seqid = seqidparts[0]
		seqid = seqid[1:len(seqid)]
		totreads += 1
		seqline = illufile.readline().strip().upper()
		line = illufile.readline()
		if pairedf != None:
			rawpseqid = pline.strip()
			pseqline = pairedf.readline().strip().upper()
			pline = pairedf.readline()
		while line != "" and line[0] != "+":
			seqline += line.strip()
			line = illufile.readline()
			if pairedf != None:
				pseqline += pline.strip()
				pline = pairedf.readline()

		nogoodbarcode = 0
		barlen = 0

		rawseqline = seqline
                qualid = line
                qual = illufile.readline().strip()
                line = illufile.readline().strip()
                rawqualline = qual
		
		if numns != None and seqline.count("N") >= numns:
			toomanyns += 1
			prevline = line
                        line = illufile.readline()
                        if pairedf != None:
                                pline = pairedf.readline()
                        continue

		if len(seqline) < (maxbarlen + searcharea + len(cutsite)):
			skipped += 1
			prevline = line
			line = illufile.readline()
                        if pairedf != None:
                                pline = pairedf.readline()
			continue

		for barlen in barlens:
			abcode = seqline[0:barlen]

			# Try to get the barcode info - if failed, try correcting
			try:
				bcode = barcodes[seqline[0:barlen]]
				break
			except KeyError:
				if options.barcorr == 1:
					abcode = barcode_correction(seqline, barlen)
				else:
					abcode = multi_barcode_correction(seqline[0:barlen], -1, 0)
				#If it can't be corrected and it's the last, this is broken
				if abcode == "" and barlen == barlens[len(barlens)-1]:
					badbcode += 1
					nogoodbarcode = 1
					break
				elif abcode != "":
					bcode = barcodes[abcode]
					corrected += 1
					break
		if nogoodbarcode == 1:
			if options.keepall:
				totretreads += 1
				fd.write(rawseqid + "\n" + seqline + "\n" + qualid + qual + "\n")
				if pairedf != None:
					pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
			prevline = line
			line = illufile.readline()
			if pairedf != None:
				pline = pairedf.readline()
			continue
		if separate:
			fd = files[bcode]
			if pairedf != None:
				pairedout = pfiles[bcode]
		#rawseqline = seqline
		#qualid = line
		#qual = illufile.readline().strip()
		#line = illufile.readline().strip()
		#rawqualline = qual
		if pairedf != None:
			pqualid = pline
			pqual = pairedf.readline().strip()
			pline = pairedf.readline().strip()
			if options.revcutsearch != 0:
				temp_ret = ("a", "b")
				temp_ret = find_revcutsite(pseqline, pqual, options.revcutsite, options.revcutsearch)
				if pseqline != temp_ret[0]:
					revtrimmed += 1
				pseqline = temp_ret[0]
				pqual = temp_ret[1]
		while line != "" and line[0] != "@":
			qual += line.strip()
			line = illufile.readline()
			if pairedf != None:
				pqual += pline.strip()
				pline = pairedf.readline()
		seqline = seqline[barlen:]
		qual = qual[barlen:]		

		# Keep track of sliding window
		counter = 0
		# Has this seq been written to file yet?
		written = 0

		# If we're keeping the barcode we don't really need to find the cutsite
		if options.keepbar:
			seqline = rawseqline
			qual = rawqualline
			if len(seqline) < minreadlen:
				tooshort += 1
				prevline = line
				line = illufile.readline()
				if pairedf != None:
					pline = pairedf.readline()
			if samelen:
				cutlen = len(seqline) - mylength
				seqline = seqline[0:len(seqline)-cutlen]
				qual = qual[0:len(qual)-cutlen]
			try:
				length[len(seqline)] += 1
				avglen += len(seqline)
				totretreads += 1
			except KeyError:
				length[len(seqline)] = 1
				avglen += len(seqline)
				totretreads += 1
			if options.lenavg:
				try:
					indivlength[abcode] += len(seqline)
				except KeyError:
					indivlength[abcode] = len(seqline)

			# If our qualid is empty don't add stuff to it
			if options.origheader == True:
				fd.write(rawseqid + "\n" + seqline + "\n" + qualid + qual + "\n")
			elif qualid.strip() == "+":
				fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/1" + "\n" + seqline + "\n" + qualid.strip() + "\n" + qual + "\n")
			else:
				fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/1" + "\n" + seqline + "\n+" + str(bcodecount[abcode]) + "_" + bcode + "_" + qualid.strip()[1:] + "\n" + qual + "\n")
			if pairedf != None:
				if seqreplace == 0 or find_cutsite(pseqline, revbar[abcode]) == False:
					if options.origheader == True:
						pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
					elif pqualid.strip() == "+":
						pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + pseqline + "\n+\n" + pqual + "\n")
					else:
						pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + pseqline + "\n" + pqualid.strip() + "\n" + pqual + "\n")
				else:
					overlapped += 1
					if options.origheader == True:
						pairedout.write(rawpseqid + "\n" + ("N"*seqreplace) + "\n" + pqualid + ("B"*seqreplace) + "\n")
					elif pqualid.strip() == "+":
						pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n+\n" + ("B"*seqreplace) +"\n")
					else:
						pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n" + pqualid.strip() + "\n" + ("B"*seqreplace) + "\n")
			written = 1
			bcodecount[abcode] += 1

		else:
                # Sliding window to find actual cut site
			while counter < searcharea and not written:
				if seqline[counter:counter+len(cutsite)] == cutsite:
					seqline = seqline[counter+len(cutsite):]
					qual = qual[counter+len(cutsite):]
					if len(seqline) < minreadlen:
						tooshort += 1
						prevline = line
						line = illufile.readline()
						if pairedf != None:
							pline = pairedf.readline()	
						continue
					if samelen:
						cutlen = len(seqline) - mylength
						seqline = seqline[0:len(seqline)-cutlen]
						qual = qual[0:len(qual)-cutlen]
					try:
						length[len(seqline)] += 1
						avglen += len(seqline)
						totretreads += 1
					except KeyError:
						length[len(seqline)] = 1
						avglen += len(seqline)
						totretreads += 1
					if options.lenavg:
						try:
							indivlength[abcode] += len(seqline)
						except KeyError:
							indivlength[abcode] = len(seqline)

				# If our qualid is empty don't add stuff to it
					if options.origheader == True:
						fd.write(rawseqid + "\n" + seqline + "\n" + qualid + qual + "\n")
					elif qualid.strip() == "+":
						fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/1" + "\n" + seqline + "\n" + qualid.strip() + "\n" + qual + "\n")
					else:
						fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/1" + "\n" + seqline + "\n+" + str(bcodecount[abcode]) + "_" + bcode + "_" + qualid.strip()[1:] + "\n" + qual + "\n")
					if pairedf != None:
						if seqreplace == 0 or find_cutsite(pseqline, revbar[abcode]) == False:
							if options.origheader == True:
								pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
							elif pqualid.strip() == "+":
								pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + pseqline + "\n+\n" + pqual + "\n")
							else:
								pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + pseqline + "\n" + pqualid.strip() + "\n" + pqual + "\n")
						else:
							overlapped += 1
							if options.origheader == True:
								pairedout.write(rawpseqid + "\n" + ("N"*seqreplace) + "\n" + pqualid + ("B"*seqreplace) + "\n")
							elif pqualid.strip() == "+":
								pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n+\n" + ("B"*seqreplace) + "\n")
							else:
								pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n" + pqualid.strip() + "\n" + ("B"*seqreplace) + "\n")
								
					written = 1
					bcodecount[abcode] += 1
					break
				if not written:
					summation = 0
					y = 0
					# Start from identified cut site start
					#	count up number of matching characters
					while y < len(cutsite):
						if seqline[y+counter] == cutsite[y]:
							summation += 1
						y += 1
					# If we view this as a good cut site, trim it off and print
					if summation >= minmatch:
						seqline = seqline[counter+len(cutsite):]
						qual = qual[counter+len(cutsite):]
						if samelen:
							cutlen = len(seqline) - mylength
							seqline = seqline[0:len(seqline)-cutlen]
							qual = qual[0:len(qual)-cutlen]
						try:
							length[len(seqline)] += 1
							avglen += len(seqline)
							totretreads += 1
						except KeyError:
							length[len(seqline)] = 1
							avglen += len(seqline)
							totretreads += 1
						if options.lenavg:
							try:
								indivlength[abcode] += len(seqline)
							except KeyError:
								indivlength[abcode] = len(seqline)
						
								
						if options.origheader == True:
							fd.write(rawseqid + "\n" +  seqline + "\n" + qualid + qual + "\n")
						elif qualid.strip() == "+":
							fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/1" + "\n" + seqline + "\n" + qualid.strip() + "\n" + qual + "\n")
						else:
							fd.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/1" + "\n" + seqline + "\n+" + str(bcodecount[abcode]) + "_" + bcode + "_" + qualid.strip()[1:] + "\n" + qual + "\n")
						if pairedf != None:
							if seqreplace == 0 or find_cutsite(pseqline, revbar[abcode]) == False:
								if options.origheader == True:
									pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
								elif pqualid.strip() == "+":
									pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/2" + "\n" + pseqline + "\n+\n" + pqual + "\n")
								else:
									pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/2" + "\n" + pseqline + "\n" + pqualid.strip() + "\n" + pqual + "\n")
							else:
								overlapped += 1
								if options.origheader == True:
									pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
								elif pqualid.strip() == "+":
									pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_"  + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n+\n" + ("B"*seqreplace) + "\n")
								else:
									pairedout.write("@" + str(bcodecount[abcode]) + "_" +  bcode + "_" + seqid + "/2" + "\n" + ("N"*seqreplace) + "\n" + pqualid.strip() + "\n" + ("B"*seqreplace) + "\n")

								
						written = 1
						bcodecount[abcode] += 1
						break
					else:
						counter += 1
			if not written:
				if options.keepall:
					totretreads += 1
					fd.write(rawseqid + "\n" + seqline + "\n" + qualid + qual + "\n")
					if pairedf != None:
						pairedout.write(rawpseqid + "\n" + pseqline + "\n" + pqualid + pqual + "\n")
				skipped += 1
	else:
		prevline = line
		line = illufile.readline()
		if pairedf != None:
			pline = pairedf.readline()

if options.quiet != True:
	tempfile = open(fname + ".info", 'w')
	#tempfile = open(args[0] + "_" + fname + "_" + cutsite + "_" + ".info", 'w')
	tempfile.write("Total number of reads: " + str(totreads) +"\n")
	tempfile.write("Total number of retained reads: " + str(totretreads) + "\n")
	tempfile.write("Number of sequences skipped due to too many Ns (" + str(numns) + "): "  + str(toomanyns) + "\n")
	tempfile.write("Number of sequences skipped due to being too short (<" + str(minreadlen) + "bp): " + str(tooshort) + "\n")
	tempfile.write("Number of sequences skipped due to inability to find cut site: " + str(skipped) + "\n")
	tempfile.write("Number of sequences skipped due to bad barcodes: " + str(badbcode) + "\n")
	tempfile.write("Number of sequences with corrected barcodes: " + str(corrected) + "\n")
	tempfile.write("Number of paired reads repaced by Ns: " + str(overlapped) + "\n")
	if options.revcutsearch != 0:
		tempfile.write("Number of cut sites trimmed from reverse reads: " + str(revtrimmed) + "\n")
	tempfile.write("Average number of reads per barcode: " + str(totretreads/len(bcodecount)) + "\n")
	if totretreads != 0:
		tempfile.write("Average read length: " + str(avglen/totretreads) + "\n")
	else:
		tempfile.write("Average read length: 0\n")

	keys = sorted(length.keys())

	tempfile.write("--- Length Distribution of Sequences ---" + "\n")
	# Print out the number of sequences per length
	for key in keys:
		tempfile.write(str(key) + ": " + str(length[key]) + "\n")

	if options.lenavg:
		tempfile.write("--- Length Distribution of Sequences per Barcode ---\n")
		keys = sorted(indivlength.keys())
		
		for key in keys:
			tempfile.write(str(key) + "/" + barcodes[key] + ": " + str(indivlength[key]/bcodecount[key]) + "\n");

	tempfile.write("--- Counts per Barcode ---" + "\n")
	# Print out the number of sequences per barcode
	for barcode in bcodecount:
		tempfile.write(barcode + "/" + barcodes[barcode] + ": " + str(bcodecount[barcode]) + "\n")
	tempfile.close()

if separate:
	for thing in files:
		files[thing].close()
	for pthing in pfiles:
		pfiles[pthing].close()
else:
	fd.close()
illufile.close()
if pairedf != None:
	pairedf.close()
	pairedout.close()
