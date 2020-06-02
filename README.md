# BigData
陈潮宇 记录大数据实训每日进度
# 5月21日
为了实现同步，首先要实现检测文件夹变动，然后再进行对应操作。  

查了一些资料发现采用FileAlterationObserver，FileAlterationListener和FileAlterationMonitor可以实现上述目标，这三个类都是org.apache.commons.io.monitor库下的类，采用导入jar的方式可以直接使用。
于是先实现了程序的整体框架，在Main.java里面可以看到。  

紧接着实现了文件的上传和删除功能，即Filesyncer类的uploadFile和deleteFile函数  
# 5月22日
今天主要在实现程序中断后，继续从原来进度上传的功能。首先我把它分成两个部分 

一、程序中断后，原来已经上传的文件不用再次上传，只上传未上传的文件。

二、程序中断后，原来已经上传的分片不用再次上传，只上传未上传的分片。  

两个部分是类似的，但实现起来有一点区别。目前已经实现第一个部分，正在实现第二部分。 采用将未上传的文件路径写入一个log文本文件来实现，同时log文件维持一个状态码。每次启动程序时，检查状态码，若为0，则代表有文件未上传，将列表里的文件依次上传，若为1则表示已上传全部文件，不需要进行任何操作。

对于第二个部分，由于需要从上次中断的分片继续上传，因此需要记录上传的uploadID,已经已完成的分片数。  
# 5月23日
今天完整实现了昨天的第二个部分，即分片中断后可以从上次的进度继续重传。除此之外加入了从键盘获取用户输入的功能，同时加入一些文字提示让程序可用性提高。
另外记录一下一些设计时比较纠结或困扰的点：  

首先就是对于FileSyncer类，由于这个类是用来进行上传或删除文件的，因此经常要调用到，为此采用了单例模式，只维持一个实例，并且开放初始化函数。 

另外就是文件读写，采用的分割符要不常见，最后选了一个"$$$$"符号。感觉最优雅的做法应该是写成类似json数据的格式，但对于java解析这方面的数据没什么经验，抱着能用就好的心态就这么处理了（之后会了解一下这方面）。  

还有一个问题就是感觉可能是一个潜在的bug，如果代码运行到写入log文件时，只写入了一半程序就被终止了，那么这时的log文件是不完整的，下次运行时可能无法正确同步所有文件。解决方法可能是采用类似数据库的事务操作，但是由于时间原因也没有实现。
# 5月25日
完成了程序设计说明书和编译说明的编写，同时组织小组其他成员完成PPT和程序使用说明书。
另外课上学习了大数据的一些架构，例如kafka，Spark，Flink等。记录在课程二文件夹内。
同时准备好了spark开发环境
# 5月26日
课上学习了实操手册中的内容，包括运用spark计算如来在西游记出现了多少次，了解了scala语言；大数据计算和存储分离，登录spark服务器，运行sql语句创建外部表，然后进行查询；大数据打擂台，对比了几个不同架构处理（Hive，Spark，GreenPlum）大数据的能力；将大数据整合到软件，包括使用spark jdbc操作数据库。
完成了扩展题的1，2，3
# 5月27日
完成了扩展题的4
# 5月28日
尝试使用spark jdbc和greenplum jdbc运行一些查询，建表，删表等操作。
# 5月29日
学习了一些scala语言的基础语法
# 6月1日
课上完成了实操5-8，课下完成了实操5-7的扩展题，扩展题8还有点问题。
# 6月2日
课上完成了分册3实操1-3。课下完成了实操1,3扩展题。为了完成扩展题二，查看了flink的api文档，发现需要将数据按窗口进行处理，然后运用datastream的聚合函数进行统计。但是目前不知道如何排序，正在查找资料解决问题。另外组织小组开始进行大作业开发，先完成了后端的demo开发，用postman进行接口测试。已完成的部分扩展题放在课程2和3中。


