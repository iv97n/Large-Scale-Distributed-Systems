 # Large-Scale Distributed systems - LABS
**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of practice deliveries centered around the use of Java, AWS, and Docker technologies. 

## Lab 1
- ### 1. Tweet Parser
	The _SimplifiedTweet_ class implements the functionalities related to the parsing of the tweets.  
	
	The static method _SimplifiedTweet.fromJson()_ takes as input a oneline representation of a json encoded tweet and returns an _Optional_ object containing a _SimplifiedTweet_ instance. The method does not propagate exceptions, but handles them by omitting the invalid inputs. The _try_ block assumes a correct formatting of the tweet, so any missing field or invalid type will raise an exception that will be catched and ignored.

- ### 2. Tweet Filter
	The _FileLanguageFilter_ class implements the functionalities related to the 	parsing of the tweets. The class itself is an extension of the 				_LanguageFilter_ interface, which defines the _filterLanguage()_ method signature. The _FileLanguageFilter_ has two attributes related to the input and output file paths.
	
	The method _filterLanguage()_ takes as input a language specification string which follows the ISO 639-1 standard, and writes to the output file in append mode all the tweets from the input file that satisfy the language condition. The method does propagate IOExceptions in case of error opening and managing input resources. Try-with-resource functionality is used to auomatically release resources in case of exception.

- ### 3. Uploader
	The _S3Uploader_ class implements the functionalities related to the uploading of the output files to the AWS cloud. The class itself is an implementation of the interface _Uploader_, which defines the _upload_ method signature.
	
	
	
	The method _upload_ takes as input an array of file paths, and uploads the files to the specified S3 bucket. The method assumes the user has already set valid credentials to the AWS services.

- ### 4. Main
	The _main_ method is enclosed inside the _TwitterFilter_ class. It is the one responsible for the handling of the input parameters and the overall workflow of the application. In case of invalid number of parameters, the exception is handled and a print message is displayed on the standard output with the usage of the application. The program itself does not handle invalid input and output file paths, but propoagates the IOException until the execution is halted.

- ### 5.Benchmarking
	The benchmark has been conducted locally, including as input files all the Eurovision tweets. The method used for time recording has been _System.nanoTime()_.  No further issues have been found.

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
     
     _Running time:_ 69,892.232025 milliseconds
 

	#### Tweets in English 
	```bash
	java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter en <output file> <bucket name> <input file> [<input file>...]
	```
	_Total number of lines read:_ 2,583,336  
    	_Number of valid tweets read:_ 446,603  
    	
  	_Running time:_ 70,193.48135 milliseconds 
	
	#### Tweets in Catalan
	```bash
	java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter ca <output file> <bucket name> <input file> [<input file>...]
	```
    _Total number of lines read:_ 2,583,336  
    	_Number of valid tweets read:_ 4,583  
    	
    _Running time:_ 64,459.939122 milliseconds 
    	
- ### 6. Extensions
	- **Unit tests**  
	Three different unit tests have been included in the application. These tests are automatically executed when running the command _mvn package_, and each of them asserts the correct parsing for a different input tweet.
		- Parsing of a valid tweet: The test asserts that each of the attributes of the generated _SimplifiedTweet_ instance match the expected output.
		- Parsing of an invalid tweet: The test checks that the Optional object returned by the _SimplifiedTweet.fromJson()_ method is indeed empty.
		- Parsing of a tweet with a missing field: The test checks that the Optional object returned by the _SimplifiedTweet.fromJson()_ method is indeed empty.