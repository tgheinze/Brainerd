name := "Brainerd"
 
organization := "heinze.org"
 
version := "0.1"
 
scalaVersion := "2.9.2"


 
libraryDependencies ++= {
       Seq(
"junit" % "junit" % "4.8.1",
"log4j" % "log4j" % "1.2.14",
"commons-io" % "commons-io" % "2.1",
"org.apache.commons" % "commons-compress" % "1.3",
"org.scalatest" % "scalatest_2.9.1" % "1.7.RC2",
"org.neo4j" % "neo4j" % "1.8.M02",
"me.prettyprint" % "hector-core" % "0.8.0-2",
"org.apache.thrift" % "thrift" % "0.2.0",
"net.sf.opencsv" % "opencsv" % "2.1",
"trgr.rd.dexter" % "dexterTokenizer" % "2.2",
"org.mongodb" % "casbah-commons_2.9.1" % "3.0.0-M2",
"org.mongodb" % "casbah-core_2.9.1" % "3.0.0-M2",
"org.mongodb" % "casbah-gridfs_2.9.1" % "3.0.0-M2",
"org.mongodb" % "casbah-query_2.9.1" % "3.0.0-M2",
"org.mongodb" % "casbah-util_2.9.1" % "3.0.0-M2",
"org.mongodb" % "mongo-java-driver" % "2.8.0",
"net.sf.opencsv" % "opencsv" % "2.1",
"com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7",
"org.slf4j" % "slf4j-log4j12" % "1.6.4",
"net.liftweb" % "lift-json_2.9.0-1" % "2.4-M3",
"org.codehaus.jackson" % "jackson-xc" % "1.9.2",
"org.codehaus.jackson" % "jackson-core-asl" % "1.9.2",
"org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.2",
"net.vz.mongodb.jackson" % "mongo-jackson-mapper" % "1.4.2"
       )
}


 

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
