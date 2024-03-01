# Large-Scale Distributed systems - LABS

**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of practice deliveries centered around the use of Java, AWS, Docker, and Spark technologies. 

## Lab 2
- ### 1. Spark Twitter Filter
	The _com.edu.TwitterLanguageFilterApp_ main method leverages the Spark framework to perform a set of filtering distributed transformations on an input file. The results are eventually saved back to the disk. During this process, the file information is loaded into a Resilient Distributed Dataset (RDD), dividing the contents into multiple partitions, which are allocated along multiple nodes of the cluster. The concatenation of the used transformations only has narrow dependencies, which enhances parallelism and reduces data transmission overheads. The transformations applied are the following:
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
	The _com.edu.TwitterLanguageFilterApp_ main method leverages the Spark framework to perform a set of filtering, counting, and sorting distributed transformations on an input file. The results are eventually saved back to the disk. Simmilarly to the _TwitterLanguageFilterApp_, we use RDDs to distribute the computations among multiple nodes in a cluster. The transformations implied are the following:
	- _Json-to-SimplifiedTweet map:_ Maps each entry from the online .json format to a Optional\<SimplifiedTweet\> instance.  
	- _Validity, language, and originality filtering:_ Filters the RDD so the new one only contains non-empty Optional\<SimplifiedTweet\> instances. Additionally, only original tweets of the specified language are selected.
	- _SimplifiedTweet-to-text_ map:  Maps each entry from an Optional\<SimplifiedTweet\> instance to a string containing the content of the tweet.
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
			1.	([[], #thevoice],46)
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
	Blablabla