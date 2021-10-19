# Jenkins module

## Task 1 - [here](Task_1/)
* connect static slave node
* create declarative job
* add parameter environment
* trigger on push and pr
* skip build if commit message is `SKIP_CI`
* create zip file with suffix `$BRANCH_NAME` and store it like artifact and build_number
* create shared library to send slack notification with build status
* in parallel ping 3 different servers and if ping failed - stop the job
* move all logic to shared library

## Task 2 - [here](Task_2/)
* Setup system messgae
* setup global admin email address
* setup smtp server
* setup slack
* setup github
* Create three folders `/folder1`, `/folder1/folder2` and `folder3`
* for `folder1` configure your shared library
* create credentials `USERNAME` and `PASSWORD`
* create group and role `poweruser` and assing it to `folder1`
* inside folder3 create test-job with build permissions for `poweruser` 