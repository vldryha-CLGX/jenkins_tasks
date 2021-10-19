# Jenkins module
## Task 2
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

## Structure 
* [jenkins_config.groovy](jenkins_config.groovy) - configuring by using groovy script
* [jenkins_as_code.yaml](jenkins_as_code.yaml)  - configuring by using JCasC plugin

## Steps
        When using groovy script to configure jenkins i meet a lot of problem with documentation.
    I needed to use examples of code from different resources. Also after creation script we get a hard to read code.

    What about Configuration as Code. It is much simpler than groovy script for one reason - we have a documentation. Also one advantage is yaml format  easy to read and undertanding.