import sbt.Keys.{libraryDependencies, organizationHomepage, scalaVersion}

val kafkaVersion = "2.2.1"
val cpVerison = "5.2.2"
val avro4sVersion = "2.0.4"
val scoptVersion = "3.7.1"
val pureconfVersion = "0.11.1"
val logbackVersion = "1.2.3"
val alpakaVersion = "1.0.4"
val scalatestVersion = "3.0.8"
val mockedstreamsVersion = "3.3.0"

lazy val common = Seq(

  isSnapshot := true,

  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.12.8",

  organization := "fr.xebia",

  organizationHomepage := Some(url("http://blog.xebia.fr")),

  developers += Developer("ldivad", "Loïc DIVAD", "ldivad@xebia.fr", url("https://github.com/DivLoic")),

  coverageEnabled := true,
    
  scalacOptions ++= Seq(
    "-encoding", "utf8",
    "-Xfatal-warnings",
    "-Ypartial-unification",
    "-deprecation", "-unchecked", "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-language:postfixOps"
  )
)

lazy val `xke-kafka-streams-tests` = (project in file("."))
  .aggregate(`crocodile-commons`, `crocodile-datagen`, `crocodile-streams`)
  .settings(common: _*)
  .settings {
    description := ""
  }

lazy val `crocodile-commons` = project
  .settings(common: _*)
  .settings(coreDependencies: _*)
  .settings(avroDependencies: _*)
  .settings {
    description := "reusable components used by the webapp team and streaming team of crocodile"
  }

lazy val `crocodile-datagen` = project
  .settings(common: _*)
  .dependsOn(`crocodile-commons`)
  .settings(testDependencies: _*)
  .settings {
    description := "data generator module"

    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % alpakaVersion
  }

lazy val `crocodile-streams` = project
  .settings(common: _*)
  .dependsOn(`crocodile-commons`)
  .settings(testDependencies: _*)
  .settings {
    description := "streaming application"

    libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion
  }
  .settings(
    coverageExcludedPackages :=
      """
        |fr.xebia.ldi.crocodile.stream.Main;
        |fr.xebia.ldi.crocodile.stream.CrocoConversion;
      """.stripMargin
  )

lazy val coreDependencies = Seq(
  libraryDependencies ++= Seq(
    "com.github.scopt" %% "scopt" % scoptVersion,
    "com.github.pureconfig" %% "pureconfig" % pureconfVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion force(),

    "org.apache.kafka" % "kafka-clients" % kafkaVersion,

    "io.circe" %% "circe-core" % "0.11.1",
    "io.circe" %% "circe-parser" % "0.11.1"
  )
)

lazy val testDependencies = Seq(
  libraryDependencies ++= Seq(
    "com.madewithtea" %% "mockedstreams" % mockedstreamsVersion % Test,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test
  )
)

lazy val avroDependencies = Seq(

  resolvers ++= Seq("confluent" at "http://packages.confluent.io/maven/"),

  libraryDependencies ++= Seq(
    "io.confluent" % "kafka-avro-serializer" % cpVerison,
    "io.confluent" % "kafka-streams-avro-serde" % cpVerison,

    "com.sksamuel.avro4s" %% "avro4s-core" % avro4sVersion,
  )
)
