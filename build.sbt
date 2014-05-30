name := "elgWebside"

version := "1.0-SNAPSHOT"

play.Project.playJavaSettings

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  cache,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.26",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.1.Final",
  "be.objectify" %% "deadbolt-java" % "2.2-RC4",
  "org.apache.commons" % "commons-lang3" % "3.3",
  "org.apache.commons" % "commons-email" % "1.3.2"
)

resolvers ++= Seq(
  Resolver.url("Objectify Play Repository", new URL("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Snapshot Repository", new URL("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns)
)



