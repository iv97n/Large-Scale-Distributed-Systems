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

The windowed values are computed using the .reduceByKeyAndWindow() method, which computes the reduced value of over a new window using the old window’s reduce value. We define then two functions that will serve as parameters for this method:
- func ((a,b) -> a+b): reduce the new values that entered the window i.e., adding new counts
- invFunc ((a,b) -> a-b): “inverse reduce” the old values that left the window i.e., subtracting old counts   

Additionally, we set the window's duration to be 60 seconds, and the slide's duration to be 20 seconds.

Behavioral information of this function has been obtained from [reduceByKeyAndWindow Spark documentation](https://spark.apache.org/docs/latest/api/python/reference/api/pyspark.streaming.DStream.reduceByKeyAndWindow.html)

