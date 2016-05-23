# Setup

Robocode using JRuleEngine (http://jruleengine.sourceforge.net/) - only a very simple robot based on some basic one

Assuming the robocode installation in C:\robocode

Edit robocode.bat file in C:\robocode (please note that required jar files needs to be put into libs directory):

    java -Xmx512M -DNOSECURITY=true -cp libs/robocode.jar;libs/jsr94-1.1.jar;libs/jruleenginesrc.jar;libs/jrules.jar robocode.Robocode %*

