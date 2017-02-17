PAK				?= Sim/
BIN 			?= bin/
SRC 			?= src/
CLASSPATH		?= $(BIN)$(PAK)
SRCPATH 		?= $(SRC)$(PAK)
JC				:= javac
JFLAGS 			:= -d $(BIN) -classpath $(PAK)

PACKAGER 		:= jar
PACFLAGS 		:= cfm
PACMETA 		:= META-INF/
MANIFEST 		:= $(PACMETA)MANIFEST.MF
VERSION 		:= 1.0
PACNAME 		:= Lab$(VERSSION).jar

filelist		:= $(SRCPATH)*.java
main 			:= Sim.Run

.PHONY: install run clean

run:
	java -cp $(BIN) $(main)

run-jar:
	java -jar $(PACNAME)

install: $(filelist)
	@mkdir -p $(CLASSPATH)
	$(JC) $(JFLAGS) $?

clean:
	rm -f $(CLASSPATH)*.class

package:
	jar $(PACFLAGS) $(PACNAME) $(MANIFEST) -C $(BIN) $(PAK)

build: install package clean
