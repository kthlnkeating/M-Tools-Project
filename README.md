# Introduction
This package lets you edit M Routines and Look at globals using Eclipse. It also includes a non-functional terminal and Debugging capabilities which are close to being done but are not quite there yet.

# Installation
## Requirements
 - Recent Eclipse. Tested on Luna, Kepler, and Juno.
 - Java 1.7 or Java 1.8. YOU MUST CHECK FOR THAT USING YOUR OS'S COMMAND LINE.
 ECLISPE DOES NOT WARN YOU IF IT CAN'T LOAD IT. Use `java -version` to find out
 what version you have.
 - DOES NOT WORK ON MACS. Joel Ivey's version works, but I have to make
   instructions for it.

## How to install
### Eclipse
 - Download the latest Eclipse IDE from http://eclipse.org. Any version-type is
   fine. Download the smallest one.
 - To install Eclipse, all you need to do is unzip the downloaded folder in a
   place you can remember and open the Eclipse file (Eclipse.exe on Windows,
   eclipse on Linux)
 - On Eclipse, Menu Help > Install New Software...
 - Click Add
 - Enter `M-tools` in first field and `https://raw.githubusercontent.com/shabiel/M-Tools-Project/master/MToolsUpdateSiteProject/` on the next.
 - Click on Next, Next, Accept the license, Finish.
 - Override the warning about unsigned content.

### VISTA
 - Log into VISTA as a programmer in Programming Mode
 - Create a user that has XUPROG and XUPROGMODE. If you use the OSEHRA Test
 runner or the OSEHRA virtual machine, you will already have the System Manager
 user (AC;VC: SM1234;SM1234!!!) that has that permission. I won't cover how to
 create a user here.
 - Get and Install this KID build: https://raw.githubusercontent.com/shabiel/M-Tools/T12/M-Editor%20For%20Eclipse%20XT_7.3_101%20not%20yet%20released/XT73P101T12.kid
 - Get the file first: On Linux, type:
 ```
 cd /tmp/
 wget https://raw.githubusercontent.com/shabiel/M-Tools/T12/M-Editor%20For%20Eclipse%20XT_7.3_101%20not%20yet%20released/XT73P101T12.kid
 $gtm_dist/mumps -dir

 GTM>D ^XPDIL,^XPDI

 Enter a Host File: /tmp/XT73P101T12.kid

 KIDS Distribution saved on Apr 17, 2015@16:20:41
 Comment: T12: Added the correct version of XUSRB1

 This Distribution contains Transport Globals for the following Package(s):
 XT*7.3*101
 Distribution OK!

 Want to Continue with Load? YES//
 GTM>D ^XPDIL,^XPDI

 Enter a Host File: /tmp/XT73P101T12.kid

 KIDS Distribution saved on Apr 17, 2015@16:20:41
 Comment: T12: Added the correct version of XUSRB1

 This Distribution contains Transport Globals for the following Package(s):
 XT*7.3*101
 Distribution OK!

 Want to Continue with Load? YES//

 Select INSTALL NAME:    XT*7.3*101     Loaded from Distribution    4/17/15@16:54
 :11
      => T12: Added the correct version of XUSRB1  ;Created on Apr 17, 2015@16:
 
 This Distribution was loaded on Apr 17, 2015@16:54:11 with header of 
    T12: Added the correct version of XUSRB1  ;Created on Apr 17, 2015@16:20:41
    It consisted of the following Install(s):
      XT*7.3*101
 Checking Install for Package XT*7.3*101
 
 Install Questions for XT*7.3*101
 
 
 
 Want KIDS to INHIBIT LOGONs during the install? NO// 
 Want to DISABLE Scheduled Options, Menu Options, and Protocols? NO// 
```

 
# Brief Usage Tutorial
 1. Make sure that you have a VistALink port open on your VISTA environment. Cache users can use the XOBU SITE SETUP MENU to start a port. GT.M users need to use a xinetd listener that invokes `GTMLNX^XOBVTCP`.
 2. Menu Window > Preferences > VistA. Give the server a name and add its ip address and port number for your VistALink port.
 3. Hit OK, OK.
 4. Open M Prespective by Window > Open Prespective > Other > VistA. You may need to resize your prespective by "restoring" the Windows.
 5. Create a new Project, or import a git repository. You can google the latter. For the first, File > New > Project > General > Project. Choose a name and hit okay. 
 6. On the Project Exlporer on your upper left corner, right click on the Project, choose Properties, then choose VistA. Type in the name *exactly* of your VistA server that you set up in Step 2. Hit OK.
 7. Click on Menu VistA > Connect to VistA. You should see a screen asking you for the access and verify code. If you don't see this screen, you need to check all your settings, down to whether you can even connect to the VistALink port from netcat or telnet. Also, YOU MUST USE A USER WHO HAS THE KEY XUPROGMODE.
 8. Once you are signed in, you can use the VistA menu or the ugly green M button to load a routine. Saving is done using the regular Eclipse Save. To create a new routine Menu File > New > VistA Routine.
 
 That's it.
 
 There is detailed usage documentation in the repository.

# Linux Notes
Linux is always finicky. So here's my set-up that I am sure works.

 - I have Ubuntu 14.04 x64.
 - Install Oracle Java 7 or 8. Make sure it's set to be the default java
   implementation in the system. My memory tells me that OpenJDK didn't work; but
   it should be tried again.
 - Eclipse Luna x64.

# Plug-in Development guidance

MTools imported as an Eclipse project.

Can be ran via opening the plugin.xml of a given projecting and clicking run or debug in the upper right corner.

Or alternatively just to copy the files from /MtoolsUpdateSiteProject/plugins (the 3 jar plugin files) into $ECLIPSE_HOME/dropins.

Can also use the project 'MToolsUpdateSite' to create and deploy p2 update site later (as is done in RASR).


Notes:
Refer to /MiscDependencies and /docs for complete installation details.
