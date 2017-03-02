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
VERSION 		:= 3.1
PACNAME 		:= Lab$(VERSION).jar

filelist		:= $(SRCPATH)*.java
main 			:= Sim.Run

.PHONY: install run clean

run:
	java -cp $(BIN) $(main)

run-jar:
	java -jar $(BIN)$(PACNAME)

install: $(filelist)
	@mkdir -p $(CLASSPATH)
	$(JC) $(JFLAGS) $?

clean:
	rm -f $(CLASSPATH)*.class

package:
	jar $(PACFLAGS) $(BIN)$(PACNAME) $(MANIFEST) -C $(BIN) $(PAK)

build: install package clean
