set -e
cd "$(dirname "$0")"
export Mxstar="java -classpath ./lib/antlr-runtime-4.7.2.jar:./bin Main"
cat > program.cpp   # save everything in stdin to astProgram.txt
$Mxstar