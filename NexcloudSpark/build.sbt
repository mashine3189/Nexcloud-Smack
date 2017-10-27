import Dependencies._
import sbtassembly.MergeStrategy

name := """NexCloudSparkAPI"""


spName := "nexcloud/NexCloudSparkAPI"

sparkVersion := "2.0.0"

sparkComponents ++= Seq("core","streaming", "sql")

licenses += "Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0")

spIncludeMaven := true

credentials += Credentials("Spark Packages Realm",
  "spark-packages.org",
  sys.props.getOrElse("GITHUB_USERNAME", default = ""),
  sys.props.getOrElse("GITHUB_PERSONAL_ACCESS_TOKEN", default = ""))



lazy val commonSettings = Seq(
  organization := "com.nexcloud",
  version := "0.1.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "NexCloudSparkAPI" ,
    unmanagedBase in Compile := baseDirectory.value / "lib" / "main",
    unmanagedJars in Compile ++= {
      val base = baseDirectory.value
      val customJars = (base ** "*.jar")
      customJars.classpath
    },
    //libraryDependencies ++= Seq(kafka, akkaHttp, lift,  sparkCassandraConnect ) // Spark
    libraryDependencies ++= Seq(kafka, akkaHttp, akka, akkaActor, akkaStream, akkaStreamKafka, akkaConsumer, lift, sparkCassandraConnect)
  )


//javaOptions += "-Xmx4096m"
javaOptions in run ++= Seq("-Xms4096M", "-Xmx4G", "-XX:MaxPermSize=1024M", "-XX:+UseConcMarkSweepGC")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.last
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}