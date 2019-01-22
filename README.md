# spark-uniqness

## Task


The purpose of this task is connected with Spark code optimization. Originally written code is used for counting unique values stored in parquet datafile.


Scala code is stored in Driver object. A testing file was added to the project (src/main/resources/file.parquet) - it has around 100 MB. AWS testing file will be much bigger and will have around 800MB.


```
   /*
     * BEGIN OF
     * CODE FOR TUNING
     */
    val finalDF = sc.sql(s"select $checkCols from $tableName")
    finalDF.show(5)
    val uniqueVals = checkCols.split(",").map(col => {
      // get row counts by value
      val uniqueValCnt = finalDF
        .select(col)
        .where(s"$col is not null")
        // set up a list of tuples with string representation of column value as the "key" and 1 as the "value"
        .map(_.get(0).toString -> 1)
        .rdd
        // sum up all values for the same key
        .reduceByKey((a, b) => a + b)
        // filter to only values with counts at or below the threshold and take only MAXVALUES_TO_SHOW values if there are more than that
        .filter(_._2 <= uniquenessThreshold)
        // get count of values that violate uniqueness
        .count()
      (col, uniqueValCnt)
    }).filter(_._2 >= 0) // only keep columns with more than zero values that violate uniqueness

    /*
     * END OF
     * CODE FOR TUNING
     */

```

## Run

```
spark-submit --class Driver SparkApp.JAR
```

## Testing

- a solution should be tested on AWS cluster
- execution time is measured
- acceptance criteria: execution time need to be very similar during 3 independent runs 

NOTE: Before testing make sure there is nothing currently running on the cluster

```
[cloudera@quickstart ~]$ yarn application -list
```

## Prizes
Award for the best solution (the fastest one) will be a t-shirt "DataMass Best Spark Coder" + set of stickers + Jack Daniels. 
:)










