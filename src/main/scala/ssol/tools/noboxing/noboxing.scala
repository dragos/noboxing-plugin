package ssol.tools.noboxing

/** Signal that the annotated class or method should contain no
 *  boxing operations. If the "noxboxing" compiler plugin is
 *  enabled, the scala compiler will issue a warning for each
 *  boxing operation.
 *
 *  For example
 *  {{{
 *    @noboxing def fastMult(x: Int, y: Int) = x * y
 *  }}}
 */
class noboxing extends scala.StaticAnnotation

