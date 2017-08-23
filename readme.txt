FIT VUT @2014
projekt IJA 2014 - client-server labyrinth game 
version for HW3(0.3)
team members:
    -logins: xfajci00,xpalac02
    -names: Martin Fajčík, Jiří Palacký

############################# QUICK START GUIDE ###############################
project usage:
./run.sh (dont forget to set up chmod first) builds project int /build, creates jar in /dest-server,
           runs project           
           AND generates documentation into /doc directory
           
in case of 
    -own build
please open run.sh script and uncomment certain section

./ant [test]
(compiles if already not) and runs junit test cases

./ant clean
cleans test build from /build/testdir

./ant compile
builds testbuild within /build/testdir

##NETWORK INTERFACE DESIGN##
https://docs.google.com/spreadsheets/d/11Doaj5u5vv5qGdZyaqaQxV7Z5Stf3LzvN0XKXrVyBTw/edit?usp=sharing
