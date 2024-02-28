# Large-Scale Distributed systems - LABS
**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of practice deliveries centered around the use of Java, AWS, Docker, and Spark technologies. 

## Lab 2
- ### 1. Spark Twitter Filter
	Here we still need to write a brief explanation  
	
- ### 2.Benchmarking the Spark-based TwitterFilter application on EMR
	The benchmark has been conducted on AWS EMR (Elastic Map Reduce), including as input files all the Eurovision tweets. Since the EMR version specified on the class slides was not available, we used the newest one, Amazon EMR emr-7.0.0 Spark package. With this package we were able to succesfully execute our Spark program. The rest of parameters, such as instance types, are the specified ones in the handout: "m4.large". Moreover, the roles used are EMR_DefaultRole and EMR_EC2_DefaultRole respectively, as it was suggested in the Labs Forum. The execution times noted are the ones produced by the EMR console, as it is requested. The benchmarks are the following:

	#### Runtime environment specifications (EMR cluster settings): 
	- #### Amazon EMR
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
    	
- ### 3. Most popular bi-grams in a given language
	Blablabla

- ### 4. Most Retweeted Tweets for Most Retweeted Users
	Blablabla