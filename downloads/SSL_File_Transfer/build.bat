set PATH=%PATH%;C:\j2sdk1.4.2\bin
set CLASSPATH=%CLASSPATH;.;SSL_File_Transfer.jar

deltree /y build

mkdir build

javac *.java -d ./build
