
import org.apache.spark.sql.{SQLContext, SparkSession}


object uniqnessCheck {

  private val master = "local[2]"
  private val appName = "uniqness-check"


  def main(args: Array[String]): Unit = {

    val sc = SparkSession.builder().master(master).appName(appName).enableHiveSupport().getOrCreate()
    //val checkCols = "customer_id,customer_fname,customer_lname"

    val checkCols = "id,name,location,rand1,rand2,rand3,rand4,rand5,rand6,rand7,rand8,rand9,rand10,rand11,"+
                    "rand12,rand13,rand14,rand15,rand16,rand17,rand18,rand19,rand20"

    val tableName = "sampleTable"
    // For implicit conversions like converting RDDs to DataFrames
    import sc.implicits._
    val sample = sc.read.parquet("./src/main/resources/file.parquet")
    val uniquenessThreshold = 1

    //sample.show(5)
    sample.createOrReplaceTempView("sampleTable")


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


    uniqueVals.take(5).map(x => {
      println(x._1)
      println(x._2)})
  }
}