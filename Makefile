

default: dev

package:
	mvn package

run:
	java -jar target/mvn-1-1.0-SNAPSHOT-jar-with-dependencies.jar com.play.app.App

# recursive development
dev:
	mvn compile exec:java -Dexec.mainClass="com.play.app.App" \
		-Dlog4j2.configurationFile="target/log4j.properties" \
		-DLOG4J_SKIP_JANSI=false
	make

clean:
	mvn clean
# run2:
# 	java -Dorg.lwjgl.util.Debug=true -cp target/my-app-2-1.0-SNAPSHOT-jar-with-dependencies.jar com.leo.app.App
