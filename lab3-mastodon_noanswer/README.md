## Section 4: Spark Stateful transformations with Windows
```
spark-submit --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" --class edu.upf.MastodonWindows target/lab3-mastodon-1.0-SNAPSHOT.jar src/main/resources/map.tsv
```
In this section we introduce the use of windows to keep track of values between consecutive micro batches. Each micro-batch displays the 15 most frequent languages of the last 20 seconds, while each windows does the same for the last minute.

The windowed values are computed using the .reduceByKeyAndWindow() method, which computes the reduced value of over a new window using the old window’s reduce value. We define then two functions that will serve as parameters for this method:
- func ((a,b) -> a+b): reduce the new values that entered the window i.e., adding new counts
- invFunc ((a,b) -> a-b): “inverse reduce” the old values that left the window i.e., subtracting old counts   

Additionally, we define window duration to be 60 seconds, and the slide duration to be 20 seconds.

Behavioral information of this function has been obtained from [reduceByKeyAndWindow Spark documentation](https://spark.apache.org/docs/latest/api/python/reference/api/pyspark.streaming.DStream.reduceByKeyAndWindow.html)

