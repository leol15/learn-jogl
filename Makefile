

default:
	mvn clean && mvn package

run:
	java -jar target/mvn-1-1.0-SNAPSHOT-jar-with-dependencies.jar com.play.app.App

# run2:
# 	java -Dorg.lwjgl.util.Debug=true -cp target/my-app-2-1.0-SNAPSHOT-jar-with-dependencies.jar com.leo.app.App
