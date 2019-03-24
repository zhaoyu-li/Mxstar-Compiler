#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.6-complete.jar:./bin cn.edu.sjtu.acm.compiler2017.demo.Semantic"
cat > program.txt   # save everything in stdin to program.txt
$CCHK