import java.io.{File, FileWriter}
import java.util.{Timer, TimerTask}

import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.io.OutputFormat
import org.apache.flink.configuration.Configuration

class WriteAndUpdater(accessKey: String, secretKey: String, endpoint: String, bucket: String,localPrefix:String, keyPrefix: String, period: Int) extends OutputFormat[(String,String)] {
  var timer: Timer = _
 /* var file: File = _
  var fileWriter: FileWriter = _*/
  var length = 0L
  var amazonS3: AmazonS3Client = _
  var index=0


  override def configure(configuration: Configuration): Unit = {
    timer = new Timer("S3Writer")
    timer.schedule(new TimerTask() {
      def run(): Unit = {

      }
    }, 1000, period)
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConfig = new ClientConfiguration()
    clientConfig.setProtocol(Protocol.HTTP)
    amazonS3 = new AmazonS3Client(credentials, clientConfig)
    amazonS3.setEndpoint(endpoint)

  }

  override def open(taskNumber: Int, numTasks: Int): Unit = {

  }

  override def writeRecord(tuple: (String,String)): Unit = {
    this.synchronized {
      var it=tuple._1
      var destination=tuple._2
      if (StringUtils.isNoneBlank(it)) {

        var file = new File(localPrefix+destination + ".txt")
        var fileWriter = new FileWriter(file, true)

        fileWriter.append(it + "\n")
        length += it.length

        val targetKey = keyPrefix+destination
        amazonS3.putObject(bucket, targetKey, file)
        println("开始上传文件：%s 至 %s 桶的 %s 目录下".format(file.getAbsoluteFile, bucket, targetKey))

        fileWriter.flush()
        fileWriter.close()
      }
    }
  }


  override def close(): Unit = {
    timer.cancel()
  }
}

