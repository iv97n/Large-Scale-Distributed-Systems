# Large-Scale Distributed systems - LABS

**Collaborators:** [iv97n](https://github.com/iv97n), [joseperezclar](https://github.com/joseperezclar), [martioms01](https://github.com/martioms01)  

**Course reference**: [24953] - Large-Scale Distributed Systems. [Universitat Pompeu Fabra](https://www.upf.edu/)

**Introduction**: This repository consists of a collection of practice deliveries centered around the use of Java, AWS, Docker and Spark technologies. 

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

