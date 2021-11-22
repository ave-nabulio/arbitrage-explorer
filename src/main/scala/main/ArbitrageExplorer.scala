package main

object ArbitrageExplorer extends App {

  // Initialize the parameters
  val (edgesInit, edgesLog, vertices, s) = setup()

  // Run the main function
  runMain()

  def runMain() {
    // Discover the arbitrage opportunities 
    val gains = discoverArbitrages(edgesLog, vertices, s)

    // Print the optimal opportunity
    val maxGains = gains.maxBy(_._2)
    val maxGainsRound = Utils.round(maxGains._2, 2)
    println()
    print("Optimal arbitrage: ")
    maxGains._1.map(x => print(x))
    println()
    println("Optimal Gain: " + maxGainsRound + "%")
  }

  def setup(startCurrency: String = "USD") = {
    var rates = get("https://fx.priceonomics.com/v1/rates/")
    // Transform the raw rates into usable format (we don't use external libraries to fit in 
    // very basic ammonite env.)
    rates = rates.replaceAll("\"", "").replace(" ", "")
    val edgesInit = rates.substring(1, rates.length-1)
                         .split(",")
                         .map(f => f.split(":"))
                         .map(v => (v(0).split("_"),v(1)))
                         .map{case (a, b) => ((a(0), a(1)) -> b.toDouble)}.toMap
    // Print the exchange rates for information  
    println("** Exchange Rates **")                  
    println(edgesInit)
    println()
    // We need to take the neperian from the weights to fulfill the algorithms requirements
    // Check the README for further details.
    val edgesLog = edgesInit.map{ case ((a, b), c) => ((a, b) -> -math.log(c))}.toMap
                            .filter{ case ((k1, k2), _) => k1 != k2}
    // List of currencies                     
    val vertices = edgesInit.keys.flatMap{ case (a,b) => List(a,b) }.toList
    // Idx of the start vertex (can be any currency)
    val s = startCurrency

    (edgesInit, edgesLog, vertices, s)
  }


  /**
   * Returns the text (content) from a URL as a String.
   * Warning: This method does not time out when the service is non-responsive.
   */
  def get(url: String): String = scala.io.Source.fromURL(url).mkString

  /**
    * Discover the arbitrage opporutnities for a certain list of exchange rates. This method leverages
    * the Bellman-Ford algorithm to find out negative cycles in the graph. A negative cycle models an 
    * arbitrage opportunity.
    *
    * @param edges: mapping of currency pair and the exchange rate
    * @param vertices: list of currencies
    * @param start: the currency from which the distance is calculated
    * @return the mapping of the gains for each negative cycle
    */
  def discoverArbitrages(edges: Map[(String, String), Double], vertices: List[String], start: String) = {

    // println(edges)
    // Map of all distances
    var D = scala.collection.mutable.Map[String, Double]()
    // All possible gains by arbitrage
    var gains = Map[List[(String, String)], Double]()
    // Map predecessors
    var predecessors = Map[String, String]()

    /**
      * Initialize all the node distances to +infinity except for S which set to 0
      */
    def distanceInit() {
      D = D + (s -> 0)
      vertices.foreach(v => D = D + (v -> Double.PositiveInfinity))
      D(s) = 0.0
    }
    
    /**
      * Computes Bellman-Ford algorithm to find the shortest path of every node paired to S.
      * Bellman-Ford requires V-1 rounds to relax the network completely. To detect and print
      * negative cycle, it is required to relax the network one more time.
      *
      * @param hops: number of round of the relaxation process
      */
    @annotation.tailrec
    def relax(hops: Int) {
      if (hops != 0) {
        edges.map{ case ((k1, k2), value) => 
          if ((D(k1) + value) < D(k2) && k2 != s) {
            D(k2) = D(k1) + value
            predecessors = predecessors + (k2 -> k1)
          }
        }
        relax(hops - 1)
      }
    }
    
    def printNegativeCylces() {
      edges.foreach{ case ((source, dest), value) => 
        if ((D(source) + value) < D(dest)) {
          // Compute the negative cycle
          var negativeCycle = List[String]()
          negativeCycle = negativeCycle :+ source :+ dest
          var tmpSource = source
          while (!negativeCycle.contains(predecessors(tmpSource))) {
            negativeCycle = predecessors(tmpSource) :: negativeCycle
            tmpSource = predecessors(tmpSource)
          }
          // Compute relative gain and print it
          val (currencyPairs, pureGain) = Utils.computeGain(negativeCycle, edgesInit)
          val relativeGain = (pureGain - 1.0) * 100.0
          Utils.printTradList(currencyPairs, relativeGain)
          // Save relative gain
          gains += (currencyPairs -> relativeGain)
        }
      }
    } 
    
    distanceInit()
    val start = System.nanoTime()
    relax(vertices.length - 1)
    printNegativeCylces()
    val stop = System.nanoTime()
    
    println()
    println("Computation time: " + (stop - start) / 1e9 + " seconds")
    
    gains
  }
}