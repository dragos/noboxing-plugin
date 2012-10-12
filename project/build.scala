import sbt._
import Keys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

object Build extends sbt.Build {

  lazy val root =
    project(id = "noboxing",
            base = file("."),
            settings = Seq(
              initialCommands := "",
              scalaVersion := "2.9.2",
              scalacOptions := Seq("-deprecation", "-unchecked"),
              shellPrompt := { (state: State) => "[%s]$ " format(Project.extract(state).currentProject.id) },
              resolvers ++= Seq(
                "Sonatype" at "https://oss.sonatype.org/content/repositories/releases",
                "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots",
                "Novus Nexus Public" at "https://nexus.novus.com:65443/nexus/content/groups/public/",
                "Novus Snpashots" at "https://nexus.novus.com:65443/nexus/content/repositories/snapshots"
              ),
              libraryDependencies <++= scalaVersion (v => Seq(
                "org.scala-lang" % "scala-compiler" % "2.9.2",
                "org.scala-lang" % "scala-library" % "2.9.2",
                "junit" % "junit" % "4.7" % "test"
              ))
            ))

  def project(id: String, base: File, settings: Seq[Project.Setting[_]] = Nil) =
    Project(id = id,
            base = base,
            settings = Project.defaultSettings ++ settings)
}
