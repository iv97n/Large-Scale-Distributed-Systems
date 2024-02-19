 # Large-Scale Distributed systems - LABS
**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of practice deliveries centered around the use of Java, AWS, and Docker technologies. 

## Lab 1
- ### 5.Benchmarking
	#### Runtime environment specifications: 
	- #### MSI CreatorPro M15  
		- 11th Gen Intel® Core™ i7-11800H × 16
		- 16.0 GiB RAM
	- #### Ubuntu 23.10
	- #### Ethernet 1000Mb/s
	#### Tweets in Spanish
	```bash
	java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter es <output file> <bucket name> <input file> [<input file>...]
	```  
     _Total number of lines read:_ 2,583,336  
     _Number of valid tweets read:_ 509,435  
     
     _Running time:_ 69892.232025 milliseconds
 

	#### Tweets in English 
	```bash
	java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter en <output file> <bucket name> <input file> [<input file>...]
	```
	_Total number of lines read:_ 2,583,336  
    	_Number of valid tweets read:_ 446,603  
    	
  	_Running time:_ 70193.48135 milliseconds 
	
	#### Tweets in Catalan
	```bash
	java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter ca <output file> <bucket name> <input file> [<input file>...]
	```
    _Total number of lines read:_ 2,583,336  
    	_Number of valid tweets read:_ 4,583  
    	
    _Running time:_ 64459.939122 milliseconds 
    	
    	