# BigData
记录大数据实训每日进度
# 5月21日
为了实现同步，首先要实现检测文件夹变动，然后再进行对应操作。
查了一些资料发现采用FileAlterationObserver，FileAlterationListener和FileAlterationMonitor可以实现上述目标，这三个类都是org.apache.commons.io.monitor库下的类，采用导入jar的方式可以直接使用。
于是先实现了程序的整体框架，在Main.java里面可以看到。
