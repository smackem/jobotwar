# package.sh builds a standalone jobotwar macosx or linux package
# remember to use jdk 14

JAR=jobotwar-gui-1.0-SNAPSHOT.jar
VERSION='1.2'

# note:
# macosx (.dmg) works fine
# ubuntu (.deb) installs as "add-on" to opt/jobotwar. it executes fine from there, anyway...

# set java release to 14
#sed -i "" "s/\<maven\.compiler\.release>13\</<maven.compiler.release>14</" pom.xml
# rebuild jars
mvn clean package
# package jars in dmg
jpackage -i jobotwar-gui/target/ -n jobotwar --main-class net.smackem.jobotwar.gui.JarMain --main-jar $JAR --java-options '--enable-preview' --app-version $VERSION
# reset java release
#sed -i "" "s/\<maven\.compiler\.release>14\</<maven.compiler.release>13</" pom.xml 
