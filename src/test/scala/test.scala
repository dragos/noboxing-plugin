import ssol.tools.noboxing.noboxing

class Test {
  def hello(x: String) {
    println("Hello, " + x)
  }

  @noboxing
  def arith(x: List[Int]) = x(0) + x(1)

  @noboxing
  def arithArray(x: Array[Int]) = x(0) + x(1)

  @noboxing
  def squareArray(xs: Array[Int]) = xs map (x => x * x)

  @noboxing
  def compose(f: Int => Int, g: Int => Int): Int = f(g(42))

  @noboxing
  def composeGen[A](x: A, f: Int => Int, g: A => Int): Int = f(g(x))
}


@noboxing class MatrixMult {

  def mult(m: Matrix[Int], n: Matrix[Int]) = {
    val p = new Matrix[Int](m.rows, n.cols)

    for (i <- 0 until m.rows) 
      for (j <- 0 until n.cols) {
        var sum = 0
        for (k <- 0 until n.rows)
          sum += m(i, k) * n(k, j)
        p(i, j) = sum
      }
    p
  }

}

class Matrix[@specialized(Int) A: ClassManifest](val rows: Int, val cols: Int) {
  private val arr: Array[Array[A]] = new Array[Array[A]](rows, cols)
  
  def apply(i: Int, j: Int): A = {
    if (i < 0 || i >= rows || j < 0 || j >= cols)
      throw new NoSuchElementException("Indexes out of bounds: " + (i, j))

    arr(i)(j)
  }

  def update(i: Int, j: Int, e: A) {
    arr(i)(j) = e
  }

  def rowsIterator: Iterator[Array[A]] = new Iterator[Array[A]] {
    var idx = 0;
    def hasNext = idx < rows
    def next = {
      idx += 1
      arr(idx - 1)
    }
  }
}
