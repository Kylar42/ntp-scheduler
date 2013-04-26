ntp-scheduler
=============

NTP Project, cs460
Scheduling System for Non-Traditional Programs

The root directory of this project will be referred to as $PROJECT_ROOT.

All documentation is in $PROJECT_ROOT/project-docs:

project-docs/deliverables
 - contains All deliverables and hand-ins, including the final paper, in Pages, Word and PDF format. 

project-docs/handouts
 - contains files handed out by Professor Dawkins

project-docs/plans
 - contains OmniGraffle and PDF versions of the project timeline and plan.


All source code is in $PROJECT_ROOT/src:
NTP Server:
src/main - root of NTP Server code. 
src/main/java - all java code for the project.
src/main/resources - all resources for this project, including jar files.
src/main/resources/properties - properties files
src/main/resources/static_content - HTML, CSS, Javascript files for the HTTP server.
src/main/resources/scripts - scripts for execution.
src/main/resource-src - source code for library jars

Monitor Application:
src/monitor - root of Monitor code
src/monitor/java - Monitor app source code
src/monitor/resources - jar files for libraries
src/monitor/resources/properties - properties for app
src/monitor/resources/scripts - scripts for execution

$PROJECT_ROOT/build.xml is the ant script - running it without arguments will give you a list of targets.
