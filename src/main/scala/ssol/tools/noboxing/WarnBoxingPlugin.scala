package ssol.tools.noboxing

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class WarnBoxingPlugin(val global: Global) extends Plugin {
  import global._

  val name = "noboxing"
  val description = "checks for boxing operations"
  val components = List[PluginComponent](Component)
  
  private object Component extends PluginComponent {
    val global: WarnBoxingPlugin.this.global.type = WarnBoxingPlugin.this.global
    val runsAfter = List("erasure")
    
    val phaseName = WarnBoxingPlugin.this.name
    def newPhase(_prev: Phase) = new NoBoxingPhase(_prev)    
    
    lazy val NoBoxingAnnotation = definitions.getClass("ssol.tools.noboxing.noboxing")

    class NoBoxingPhase(prev: Phase) extends StdPhase(prev) {
      override def name = WarnBoxingPlugin.this.name

      def apply(unit: CompilationUnit) {
        log("checking " + unit)
        treeTraverser(unit.body)
      }
    }

    import definitions.{getMember, PredefModule}
    lazy val arrayOps = Set(
      getMember(PredefModule, "refArrayOps"),
      getMember(PredefModule, "intArrayOps"),
      getMember(PredefModule, "longArrayOps"),
      getMember(PredefModule, "doubleArrayOps"),
      getMember(PredefModule, "floatArrayOps"),
      getMember(PredefModule, "charArrayOps"),
      getMember(PredefModule, "byteArrayOps"),
      getMember(PredefModule, "shortArrayOps"),
      getMember(PredefModule, "booleanArrayOps"),
      getMember(PredefModule, "unitArrayOps")
    )

    object warnTraverser extends Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        case Apply(meth, args) =>
          if (definitions.isBox(tree.symbol))
            reporter.warning(tree.pos, "Boxing operation (%s)".format(tree))
          else if (definitions.isUnbox(tree.symbol))
            reporter.warning(tree.pos, "Unboxing operation (%s)".format(tree))
          else if (arrayOps(tree.symbol))
            reporter.warning(tree.pos, "Array boxing operation (%s)".format(tree))

          super.traverse(tree)
        case _ =>
          super.traverse(tree)
      }
    }

    object treeTraverser extends Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        case DefDef(mods, name, tparams, vparams, tpt, rhs) 
                    if tree.symbol.hasAnnotation(NoBoxingAnnotation) =>
          log("checking %s".format(tree.symbol))
          warnTraverser(rhs)

        case ClassDef(_, _, _, impl) if tree.symbol.hasAnnotation(NoBoxingAnnotation) =>
          log("checking %s".format(tree.symbol))
          warnTraverser(impl)

        case _ =>
          super.traverse(tree)
      }
    }
  }
}
