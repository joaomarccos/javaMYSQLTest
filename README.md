# Log Parser


## Requirements

- Maven >= 3
- JDK 1.8
- MySQL (port 3306 - to change this view application.properties file)


## Run from command line 
	
- clone the repository
- run "mvn package" for generate the executable jar
	
 - java -jar "parser.jar" --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 

## Notes

Two versions are avaible. See tags bellow

v1.0.0 - Simple implementation

v2.0.0 - Sofisticated implementation using spring batch
