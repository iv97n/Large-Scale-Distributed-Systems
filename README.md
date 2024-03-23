 # Large-Scale Distributed systems - LABS
**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of laboratory projects centered around the use of Java, AWS, and Spark technologies. 

## Lab 1
- ### 1. Tweet Parser
	The _SimplifiedTweet_ class implements the functionalities related to the parsing of the tweets.  
	
	The static method _SimplifiedTweet.fromJson()_ takes as input a oneline representation of a json encoded tweet and returns an _Optional_ object containing a _SimplifiedTweet_ instance. The method does not propagate exceptions, but handles them by omitting the invalid inputs. The _try_ block assumes a correct formatting of the tweet, so any missing field or invalid type will raise an exception that will be catched and ignored.

- ### 2. Tweet Filter
	The _FileLanguageFilter_ class implements the functionalities related to the 	parsing of the tweets. The class itself is an extension of the 				_LanguageFilter_ interface, which defines the _filterLanguage()_ method signature. The _FileLanguageFilter_ has two attributes related to the input and output file paths.
	
	The method _filterLanguage()_ takes as input a language specification string which follows the ISO 639-1 standard, and writes to the output file in append mode all the tweets from the input file that satisfy the language condition. The method does propagate IOExceptions in case of error opening and managing input resources. Try-with-resource functionality is used to auomatically release resources in case of exception.

- ### 3. Uploader
	The _S3Uploader_ class implements the functionalities related to the uploading of the output files to the AWS cloud. The class itself is an implementation of the interface _Uploader_, which defines the _upload_ method signature. If an exception occurs, a message is prompted into the standard output warning the user that the specified bucket path is unreachable.
	
	
	
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


**Final Note**: Please note that the warning showing during execution that reads: "WARNING: JAXB is unavailable. Will fallback to SDK implementation which may be less performant.If you are using Java 9+, you will need to include javax.xml.bind:jaxb-api as a dependency." is related to the usage of the Java SDK S3 dependency. JAXB is a Java Standard but in newer versions of Java it must be included explicitly as a dependency in order to achieve the maximum performance in XML processing. Nonetheless, we opted for leaving the POM as asked and do not include extra dependencies, considering the existence of major reasons for the teachers to not mention this situation explicitly.

## Lab 2
- ### 1. Spark Twitter Filter
	The _edu.upf.TwitterLanguageFilterApp_ main method leverages the Spark framework to perform a set of filtering distributed transformations on an input file. The results are eventually saved back to the disk. During this process, the file information is loaded into a Resilient Distributed Dataset (RDD), dividing the contents into multiple partitions, which are allocated along multiple nodes of the cluster. The concatenation of the used transformations only has narrow dependencies, which enhances parallelism and reduces data transmission overheads. The transformations applied are the following:
	- _Json-to-SimplifiedTweet map:_ Maps each entry from the online .json format to a Optional\<SimplifiedTweet\> instance.  
	- _Valid tweet filtering:_ Filters the RDD so the new one only contains non-empty Optional\<SimplifiedTweet\> instances.
	- _Language filtering:_ Filters the RDD so the new one only contains tweets with the specified language.
	- _SimplifiedTweet-to-Json_ map:  Maps each entry from an Optional\<SimplifiedTweet\> instance to a online .json object.

	
- ### 2.Benchmarking the Spark-based TwitterFilter application on EMR
	The benchmark has been conducted on two different settings of the AWS EMR (Elastic Map Reduce) service, and both configurations have yielded successful executions. The input to the application was the _.json_ Eurovision tweet files, and the roles used were EMR_DefaultRole and EMR_EC2_DefaultRole, as suggested in the Labs Forum. The execution times noted are the ones produced by the EMR console.

	### Initial setting

	#### Runtime environment specifications (EMR cluster settings): 
	-  #### Capacity
		- 1 m4.large master node
		- 1 m4.large core node
		- 1 m4.large task node
	- #### Amazon EMR version
	    - emr-7.0.0
	- #### Installed applications
        - Hadoop 3.3.6, Hive 3.1.3, JupyterEnterpriseGateway 2.6.0, Livy 0.7.1, Spark 3.5.0
    - #### Amazon Linux Release
        - 2023.3.20240219.0
    
	#### Tweets in Spanish
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar es s3://lsds2024.lab2.output.u198727/output/benchmark/es s3://lsds2024.lab2.output.u198727/input
	```  
    _Running time:_ 5 minutes, 30 seconds

	#### Tweets in English 
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar en s3://lsds2024.lab2.output.u198727/output/benchmark/en s3://lsds2024.lab2.output.u198727/input
	```
  	_Running time:_  5 minutes, 02 seconds
	
	#### Tweets in Catalan
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar ca s3://lsds2024.lab2.output.u198727/output/benchmark/ca s3://lsds2024.lab2.output.u198727/input
	```
    _Running time:_ 4 minutes, 48 seconds

	### Performance-enhanced setting
	-  #### Capacity
		- 1 m5.xlarge master node
		- 1 m5.xlarge core node
		- 2 m5.xlarge task nodes
	- #### Amazon EMR version
	    - emr-7.0.0
	- #### Installed applications
        - Hadoop 3.3.6, Hive 3.1.3, JupyterEnterpriseGateway 2.6.0, Livy 0.7.1, Spark 3.5.0
    - #### Amazon Linux Release
        - 2023.3.20240219.0
    #### Tweets in Spanish
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar es s3://lsds2024.lab2.output.u198727/output/benchmark/es s3://lsds2024.lab2.output.u198727/input
	```  
    _Running time:_ 1 minute, 18 seconds

	#### Tweets in English 
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar en s3://lsds2024.lab2.output.u198727/output/benchmark/en s3://lsds2024.lab2.output.u198727/input
	```
  	_Running time:_  1 minute, 02 seconds
	
	#### Tweets in Catalan
	```bash
	spark-submit --deploy-mode cluster --class edu.upf.TwitterLanguageFilterApp s3://lsds2024.lab2.output.u198727/jars/spark-test-1.0-SNAPSHOT.jar ca s3://lsds2024.lab2.output.u198727/output/benchmark/ca s3://lsds2024.lab2.output.u198727/input
	```
    _Running time:_ 1 minute, 02 seconds
- ### 3. Most popular bi-grams in a given language
	The _edu.upf.BiGramsApp_ main method leverages the Spark framework to perform a set of filtering, counting, and sorting distributed transformations on an input file. The results are eventually saved back to the disk. Simmilarly to the _TwitterLanguageFilterApp_, we use RDDs to distribute the computations among multiple nodes in a cluster. The transformations implied are the following:
	- _Json-to-ExtendedSimplifiedTweet map:_ Maps each entry from the online .json format to a Optional\<ExtendedSimplifiedTweet\> instance.  
	- _Validity, language, and originality filtering:_ Filters the RDD so the new one only contains non-empty Optional\<ExtendedSimplifiedTweet\> instances. Additionally, only original tweets of the specified language are selected.
	- _ExtendedSimplifiedTweet-to-text_ map:  Maps each entry from an Optional\<ExtendedSimplifiedTweet\> instance to a string containing the content of the tweet.
	- _Bigrams counting_: Transform the RDD into a (key, value) RDD of the form (bigram, 1). Eventually perform a reduce by key transformation to count the number of appearances of each bigram.
	- _Key-value swapping:_ Swap the key and the value in the RDD for sorting purposes.
	- _Key sorting_: Sort the RDD by key value (number of appearances).

	#### Results
	- #### Spanish [es]
			1. ([de, la],3421)  
			2. ([#eurovision, #finaleurovision],3341)  
			3. ([que, no],2425)  
			4. ([la, canción],2345)  
			5. ([de, #eurovision],2312)  
			6. ([en, el],2186)  
			7. ([lo, que],2035)  
			8. ([en, #eurovision],1856)  
			9. ([a, la],1843)  
			10. ([en, la],1828)  
	- #### English [en]
			1. ([this, is],5871)
			2. ([of, the],5791)
			3. ([in, the],5227)
			4. ([for, the],4374)
			5. ([the, eurovision],4265)
			6. ([eurovision, is],3317)
			7. ([eurovision, song],3182)
			8. ([i, love],3089)
			9. ([is, the],2925)
			10. ([to, be],2674)
	- #### Catalan [ca]
			1. ([de, la],70)
			2. ([#eurovision, #finaleurovision],53)
			3. ([#eurovision, []],46)
			4. ([#thevoice, []],46)
			5. ([[], #thevoice],46)
			6. ([up, –],45)
			7. ([al-barakah, 🎥🎥],45)
			8. ([#france, blew],45)
			9. ([–, wilāyat],45)
			10. ([[], #eurovision],45)
	
	Notice that the catalan execution output has some words which do not seem to be part of the catalan language. Upon quite a bit of investigation, we found out that the file Eurovision10.json contains many repeated tweets, most of them written in arabic but assigned the catalan language tag [ca]. This statement is clearly backed up by the results of the execution when we run our algorithm without Eurovision10.json and on Eurovision10.json alone:

	- #### Catalan [ca] without Eurovision10.json
			1. ([de, la],61)
			2. ([#eurovision, #finaleurovision],53)
			3. ([la, de],35)
			4. ([a, la],31)
			5. ([que, no],30)
			6. ([a, #eurovision],28)
			7. ([#eurovision, #eurovision2018],28)
			8. ([#eurovision, #esc2018],24)
			9. ([israel, #eurovision],22)
			10. ([de, israel],22)

	- #### Catalan [ca] only on Eurovision10.json
			1. ([[], #thevoice],46)
			2. ([#thevoice, []],46)
			3. ([#eurovision, []],46)
			4. ([[], #eurovision],45)
			5. ([–, wilāyat],45)
			6. ([[], ⤵️⤵️],45)
			7. ([blew, up],45)
			8. ([al-barakah, 🎥🎥],45)
			9. ([wilāyat, al-barakah],45)
			10. ([up, –],45)
	
	The two executions above prove our point.
- ### 4. Most Retweeted Tweets for Most Retweeted Users
	The _edu.upf.MostRetweetedApp_ main method leverages the Spark framework to perform a set of filtering, counting, and sorting distributed transformations on an input file. The results are eventually saved back to the disk. Simmilarly both exercises above, we use RDDs to distribute the computations among multiple nodes in a cluster. The transformations implied are the following:
	- _Json-to-ExtendedSimplifiedTweet map:_ Maps each entry from the online .json format to a Optional\<ExtendedSimplifiedTweet\> instance.
 	- _Validity and retweeted:_ Filters the RDD so the new one only contains non-empty Optional\<ExtendedSimplifiedTweet\> instances. Additionally, only retweeted tweets are selected.
	- _Retweeted-user count map:_ Counts the number of times each user in the dataset has been retweeted.
   	- _Sorted retweeted-user count map:_ Sort the users from most retweeted to less. To do so, we swap key and value and sort by key.
   	- _Take first 10 users:_ We take the first ten users and take only their user id.
   	- _Create empty JavaPairRDD top_tweetid_userid:_ We create an empty JavaPairRDD to fill it later on.
   	- _For each user id do:_
   	  	- _Get all user retweeted tweets:_ Check all retweeted tweets where the RetweetedUserId == userId
   	  	- _Count each user retweeted tweets:_ Create a RDD with the retweeteduserid in the key and 1 as a value. Additionally, add the text of the tweet for further usage.
   	  	- _Sort user retweeted tweets from most retweeted to less:_ Reduce by key the _user-retweeted-tweets_, swap the key and value and sort by key to order them.
   	  	- _Take most retweeted user tweet:_ use take(1) to get the first in the RDD, and parallelize the output of take to get a _most-retweeted-id_ RDD (as the take() returns a list)
   	  	- _Link the user with most retweeted tweet:_ use mapToPair to create an RDD with _userId_ as key and (_retweeted_id_, _text_) as value
   	  	- _Append result in the top_tweetid_userid RDD:_ Use union to add the result found at the end of the iteration to our final RDD.
	- _Use coalesce:_ Use coalsce(1) to specify the number of partitions. This way we ensure a single output.
   	- _Save as text file:_ Finally, save the result in the output and stop the sparkcontext. 

	Note that we use .persist() in two different RDDs. This is due to the fact that those RDDs are used in each iteration, hence significantly improving the performance of the iterative algorithms.

	Note also that there is a part in the pdf stating: _Be aware that certain users might be very retweeted and still not have tweets in this dataset._ We have decided to solve this problem by iterating through the retweetedtweetid, and printing the text of the retweeted text rather than the original, which is exactly the same. This way we ensure that the text of the tweet is seen in the output in case the original tweet was not in our dataset. 

	Last but not least, we attach the results of running our MostRetweetedApp through the entire Eurovision dataset. Note that we also include the command for local execution:

	```bash
	spark-submit --master local --class edu.upf.MostRetweetedApp target/spark-test-1.0-SNAPSHOT.jar output input
	```

	1. (3143260474,(995356756770467840,RT @NetflixES: Ella está al mando. Con @PaquitaSalas nada malo puede pasar, ¿no? #Eurovision https://t.co/5HeUDCqxX6))
	2. (24679473,(995397243426541568,RT @bbceurovision: See what he did there? #Eurovision #CzechRepublic  #CZE https://t.co/DwdfXmTqXg))
	3. (15584187,(995433967351283712,RT @Eurovision: The Winner of the 2018 #Eurovision Song Contest is ISRAEL! #ESC2018 #AllAboard https://t.co/Myre7yh3YV))
	4. (437025093,(995435123351973890,RT @ManelNMusic: Así que el año pasado quedo último con un gallo y este año gana una gallina... #Eurovision https://t.co/EfvXQbb8jp))
	5. (39538010,(995434971765538817,RT @pewdiepie: My chicken is not your goddamn prom dress #Eurovision))
	6. (38381308,(995388604045316097,RT @elmundotoday: Puigdemont ha logrado aparecerse durante unos segundos en el vestido de la participante de Estonia y ha proclamado la ind…))
	7. (739812492310896640,(995405811261300740,RT @PaquitaSalas: Qué guasa tiene la niña, ¿eh? #Eurovision https://t.co/Iv1yottkvQ))
	8. (1501434991,(995394150978727936,RT @auronplay: Muy bien Alemania secuestrando a Ed Sheeran y poniéndole una peluca. #Eurovision))
	9. (29056256,(995381560277979136,RT @Uznare: eurovision rules https://t.co/I8cG3D5tCh))
	10. (2754746065,(995439842107576321,RT @LVPibai: Rodolfo Chikilicuatre, un actor disfrazado con una guitarra de plástico quedó siete puestos por encima que la ganadora de un c…))
	
## Lab 3

## Section 3: Stateless - joining a static RDD with a real time steam
```
spark-submit --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" --class edu.upf.MastodonStateless target/lab3-mastodon-1.0-SNAPSHOT.jar src/main/resources/map.tsv
```
In this section we aim to compute the the number tweets for each language within an interval of 20 seconds. We will do it in a stateless fashion, it is to say, we will only work on the current micro-batch.

Transformations applied:
- Extract the tweet language
- Standarize the tweet language name by joining the DStream with an RDD obtained from an input file
- Count the number of tweets of each language by mapping to a (language, 1) DSteram and reducing by key
- Sorting of the languages by tweet count (value)


## Section 4: Spark Stateful transformations with Windows
```
spark-submit --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" --class edu.upf.MastodonWindows target/lab3-mastodon-1.0-SNAPSHOT.jar src/main/resources/map.tsv
```
In this section we introduce the use of windows to keep track of values between consecutive micro batches. Each micro-batch displays the 15 most frequent languages of the last 20 seconds, while each windows does the same for the last minute.

The windowed values are computed using the .reduceByKeyAndWindow() method, which computes the reduced value of over a new window using the old window's reduce value. We define then two functions that will serve as parameters for this method:
- func ((a,b) -> a+b): reduce the new values that entered the window i.e., adding new counts
- invFunc ((a,b) -> a-b): "inverse reduce" the old values that left the window i.e., subtracting old counts   

Additionally, we set the window's duration to be 60 seconds, and the slide's duration to be 20 seconds.

Behavioral information of this function has been obtained from [reduceByKeyAndWindow Spark documentation](https://spark.apache.org/docs/latest/api/python/reference/api/pyspark.streaming.DStream.reduceByKeyAndWindow.html)

## Section 5: Spark Stateful transformations with state variables
```
spark-submit --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" --class edu.upf.MastodonWithState target/lab3-mastodon-1.0-SNAPSHOT.jar <language: es, ca, en, ...>
```
In this section we forget about batches and windows and want to know the number of occurrences (cumulative sum of values) throughout the whole stream of data. To do so, "updateStateByKey" function is used, allowing to maintain the state across the streaming data and to keep track of the cumulative count of tweets for each user-language pair over time. To use updateStateByKey, we define a function to update the state for each user based on incoming tweets.

In plain english, the program maintains a running count of tweets for each user-language pair and filters out tweets not in the specified language. It then sorts the users based on tweet counts, and prints the top 20 users with the highest tweet counts in the desired language. 

## Section 6: DynamoDB
In this section we create an application to write data to a dynamo DB. Note that we have created the table in AWS as stated in the lab introduction. We have considered to use as primary key the hashtag and the language, as we might encounter one hashtag in more than one language. In order to avoid possible errors we only consider those tweets that have certain conditions, that is that have at least one hashtag and that the language of the tweet has been succesfully read. It is necessary to have all the AWS required infrastructure correctly configured (cli session and table) for the program to run.

### Write
We first check if the tweet is in the table, if it is not, we create a new item and put it on the table, if the tweet exists, we update its count adding 1, and we add the tweet id to the list of tweets containing that hashtag. In the main we need to create a repository for each partition and for each tweet call the method write

```
spark-submit --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties --class edu.upf.MastodonHashtags target/lab3-mastodon-1.0-SNAPSHOT.jar
```
### Read
We simply need to scan the table, get all tweets with the specified language and use the class HashTagCount to help us order it. Note that now we shall specify the language we want to use.

```
spark-submit --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties --class edu.upf.MastodonHashtagsReader target/lab3-mastodon-1.0-SNAPSHOT.jar <language: es, ca, en, ...>
```