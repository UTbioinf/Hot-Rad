#!/bin/bash
 
#$1 	# jar file
#$2	# config file
#$3	# first seq file
#$4	# second seq file
#$5	# output file

#export JAVA_HOME=/afs/nd.edu/user37/ccl/software/external/java-6.0/
#export PATH=${JAVA_HOME}/bin:$PATH

java -Xmx1024m -jar $1 $2 $3 $4 > $5
