#!/bin/bash

LOGFILE=test.log
NOT_SCENARIOS=ScenariosNotForJenkins
CUCUMBER_OPTIONS="--tags ~@$NOT_SCENARIOS"

if [ $# -eq 0 ]
then
    COUNT=1
else
    COUNT=$1
fi

if [ -e $LOGFILE ]
then
    rm $LOGFILE
fi

mvn clean install
pushd .
cd integration-tests

mvn clean install -PIT -Darquillian.cucumber.options="${CUCUMBER_OPTIONS}" >  ../$LOGFILE
for ((a=2; a <= $COUNT; a++))
do
    mvn verify -PIT -Darquillian.cucumber.options="${CUCUMBER_OPTIONS}" >>  ../$LOGFILE
done

popd
echo 
echo
echo -n "**** TEST RESULTS ****"
echo
echo -n "Test run: "
echo $COUNT
echo -n "SUCCESS run: "
grep "BUILD SUCCESS" $LOGFILE | wc -l
echo -n "FAILED run: "
grep "BUILD FAILURE" $LOGFILE | wc -l
#grep FAILURE! $LOGFILE | wc -l
grep -1 "Total time" $LOGFILE

