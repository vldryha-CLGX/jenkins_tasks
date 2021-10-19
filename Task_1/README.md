# Jenkins module

## Task 1 
 * connect static slave node
 * create declarative job
 * add parameter environment
 * trigger on push and pr
 * skip build if commit message is "SKIP_CI"
 * create zip file with suffix $BRANCH_NAME and store it like artifact and build_number
 * create shared library to send slack notification with build status
 * in parallel ping 3 different servers and if ping failed - stop the job
 * move all logic to shared library


 > [Link](https://github.com/vlddryga2233/jenkins-library/tree/main/vars) for shared library

 ## Structure of the library

  * archiveBuild - archive .jar file in zip format
  * buildSpring  - build adn test spring app
  * gitTag  - create and push git tag
  * pingServer - ping 3 servers
  * slackNotifier - send notification to slack server

## Structure
 * .mvn  - maven packages
 * src   - spring application
 * Jenkinsfile - pipeline file for Jenkins
 * mvnw  - executable file
 * mvnw.cmd - executable file for powershall
 * pom.xml - application packages


 ## Steps
  * Create master and slave nodes 
  * Provide github web-hook
  * Provide github access token
  * Configure jenkins github plugin
  * Install plugin for slack and skip_ci
  * create pipeline
  *  move logic to lib
