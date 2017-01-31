PAK				?= Sim/
BIN 			?= bin/
SRC 			?= src/
CLASSPATH		?= $(BIN)$(PAK)
SRCPATH 		?= $(SRC)$(PAK)
JC				:= javac
JFLAGS 			:= -d $(BIN) -classpath $(PAK)

filelist		:= $(SRCPATH)*.java
main 			:= Sim.Run

.PHONY: install run clean

run:
	java -cp $(BIN) $(main)

install: $(filelist)
	@mkdir -p $(CLASSPATH)
	$(JC) $(JFLAGS) $?

clean:
	rm -f $(CLASSPATH)*.class
