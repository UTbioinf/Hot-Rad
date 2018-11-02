#!/bin/bash

# Usage: construct.sh <database_file> <number_of_splits> <configfile> <-q/a> <destination_directory>

if [ $# -ne 5 ]; then		# incorrect arguments
	echo "Usage: construct.sh <database_file> <number_of_splits> <configfile> <-q/a> <destination_directory>"
	exit 1;
fi

curr=`pwd -P`/			# generating full path names of both current and destination
dest=$curr$5/			# these are the paths we will need to eliminate from the Makeflow
stash='_Stash/0/0/0/'
make=$dest'/Makeflow'		# path of our Makeflow file
data=$1
dataD=`readlink -e $1`

weaver -O $dest ./weaveAssemb.py $data $2 $3 $4

echo "GOT HERE"

cp ./assemble.sh $dest		# copy executables into destination
cp ./split.pl $dest
cp ./*.jar $dest
cp ./localassemble.sh $dest
cp ./final_clean.py $dest
cp $3 $dest

echo $curr

sed -i.bak s#$dest##g $make	# remove current and destination absolute paths
sed -i.bak s#$curr##g $make 
sed -i.bak s#$stash##g $make	# and remove the _Stash hierarchy

#sed -i.bak s#$data##g $make
sed -i.bak s#$dataD$data#$dataD#g $make 	# the database file is the only one with a full path name
					# and this substitution is only necessary when database file 
rm $make.bak				# has same parent directory as the current directory or destination
rm -rf $dest'_Stash'
python mergewriter.py $dest/Makeflow $3

#/bin/cat $dest/Makeflow $dest/Makeflow.temp > $dest/Makeflow.temp.temp
mv $dest/Makeflow.temp $dest/Makeflow

echo "Project successfully created in $dest directory."
