import java.util
import java.util.{Properties, UUID}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

object Consumer {
  //s3参数
  val accessKey = "B295DE50D353418FD1F6"
  val secretKey = "Wzg1NkVEQjRGMEIwQTRGRTIxM0NDQzgxQjAwQjFGNDg4M0I0NjU5MkVd"
  val endpoint = "scuts3.depts.bingosoft.net:29999"
  val bucket = "chenchaoyu"
  //要读取的文件
  val key = "daas.txt"

  //kafka参数
  val topic = "dataflow_CCY"
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  //本地路径前缀
  val localPrefix="result/"
  //上传文件的路径前缀
  val keyPrefix = "upload/"
  //上传数据间隔 单位毫秒
  val period = 5000

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    //从Kafka topic中引入数据
    val kafkaConsumer = new FlinkKafkaConsumer010[String](topic, new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)

    //得到数据流
    val inputKafkaStream = env.addSource(kafkaConsumer)

    //按城市分类dataStream,得到一个以(String，String)为元素的流
    var citySumStream=inputKafkaStream.map(x=>(x,getValue(x,4))).keyBy(1).timeWindow(Time.seconds(20))
      .reduce((a,b)=>(new String(a._1+"\n"+b._1),a._2))
      //调用自定义S3Writer,将数据写入文件(每个元素一个文件),并上传到s3
      .writeUsingOutputFormat(new WriteAndUpdater(accessKey, secretKey, endpoint, bucket,localPrefix,keyPrefix, period))


      env.execute()
  }

  /**
   * 输入一条数据，返回一个字段值的列表
   * 例如输入{"username":"李雅晗","buy_time":"2019-11-30 11:27:53"}
   * 返回[李雅晗,2019-11-30 11:27:53]
   * @param str 输入的字符串
   * @return 字段值组成的数组
   */
  def getRows(str: String):util.ArrayList[String]={
    var strLen=str.size
    //去掉两边的{和}
    var newStr=str.substring(1,strLen-1)
    //分割成key:"value"形式
    var list=newStr.split(",")

    var result=new util.ArrayList[String]
    for(i<-0 until list.length){
      var subStr=list(i)
      var subList=list(i).split(":")

      var valueStr=subList(1)
      if(valueStr.charAt(0)==' '){
         valueStr=valueStr.substring(1)
       }
      var len=valueStr.size
      var value=valueStr.substring(1,len-1)
      //println("value:"+value)
      result.add(value)
    }
    return result
  }

  /**
   * 返回x的第i个字段的值
   * 例如输入({"username":"李雅晗","buy_time":"2019-11-30 11:27:53"},1)
   * 返回字符串 2019-11-30 11:27:53
   * @param x 一条数据
   * @param i 字段的索引
   * @return 第i个字段的值
   */
  def getValue(x:String,i:Int):String={
      var list=getRows(x)
      return list.get(i)
  }
}
