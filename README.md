# spark-uniqness

## Zadanie
Konkrus polega na optymalizacji kodu spark-scala służacego do określania ilości unikalnych rokordów zapisanych w poszczególnych kolumnach. 

Kod został umieszczony w obiekcie Driver. W projecie dodany został przykładowy plik testowy (około 100 MB).


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


## Nagrody
Narodą za najlepsze rozwiązanie jest super koszulka DataMass best codder + zestaw naklejek + Jack Daniels.










