# FORMAT: % perl split.pl (database file) (number of segments)
#
# This program will split the input data into a number of files to be analyzed
# 
# will create all part.txt files (weaver creates parts.txt and takes care of output files)
# Modified 5 April 2013 LAA - Fixed splitting for Fastq 
# This relies on the assumption that Fasta/Fastq files are SINGLE LINE, so that each sequence is on one line


use POSIX;

my $dbFilename = '';
my $numSegments = 2;		# user specified numSegments, calculated maxSegments
my $maxSegments = 2;

if(scalar(@ARGV) < 2)		# let's intervene
{
	$dbFilename = 'test.fasta';
	$numSegments = 20;
}
else
{
	$dbFilename = $ARGV[0];
	$numSegments = $ARGV[1];
}

my $numIdent = 0;		# number of identifiers
my $totalLines = 1;             # add one for first line read to confirm fasta/fastq

my $begin = 0;			# first line number added to a "part" file
my $cutoff = 0;			# last line number added to a "part" file

my $segLength = 0;		# segment length based on total lines and segments
my $n = 1;			# number of segments already created

open(DBFILE,"$dbFilename");

my $type = "";

my $firstLine = <DBFILE>;
if (substr($firstLine, 0, 1) eq ">")	{
    $type = "fasta";
}
else {
    $type = "fastq"; 
}


while(<DBFILE>)
{
	$totalLines++;
}


# get numIdent based on filetype
if ($type eq "fasta") {
	$numIdent = $totalLines/2;		
}
else {
	$numIdent = $totalLines/4;
}

$maxSegments = floor($numIdent/2);	# we need a minimum of 2 identifiers for each segments

if($numSegments > $maxSegments)		
{
	$numSegments = $maxSegments;	# intervene if use specifies too many segments	
}

close(DBFILE);

if($numSegments == 0)
{
	$numSegments = 2;
} 

$segLength = ceil($numIdent/$numSegments);		# segment length depends on the total line number and the number of segments

# Counter for num seqs written to current seg
$tempcounter = 0;
$n = 1;

open(DBFILE,"$dbFilename");

# Open up the first part
open(PART, ">part1.txt");

while (my $line = <DBFILE>) {
    # Are we at the end of the segment?
    if ($tempcounter == $segLength) {
        close(PART);
	$tempcounter = 0;
        $n++;
        open(PART,">part$n.txt");
    }
    # Write 4 lines if fastq
    if ($type eq "fastq") {
        print PART $line . uc(<DBFILE>) . <DBFILE> . <DBFILE>;
    }
    # Write 2 lines if fasta
    if ($type eq "fasta") {
        print PART $line . uc(<DBFILE>);
    }

    $tempcounter++;
}

close(PART);
close(DBFILE);
close(WRITE);

