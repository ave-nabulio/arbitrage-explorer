package main

object Utils {

  def round(x: Double, p: Int): Double = {
    val s = math.pow(10, p)
    (x * s).toInt / s
  }

  def computeGain(negativeCycle: List[String], exchangeRates: Map[(String, String), Double]) = {

    val flattenNC = negativeCycle.map(i => List(i,i)).flatten
    val preprocessedNC = flattenNC.tail :+ flattenNC.head
    val currencyPairs = preprocessedNC.grouped(2).toList.collect{ case List(x,y) => (x,y) }
    val exchangeRatesList = currencyPairs.map{ case (k1, k2) => exchangeRates((k1, k2))}
    val pureGain = exchangeRatesList.fold(1.0)(_*_)

    (currencyPairs, pureGain)
  }

  def printTradList(currencyPairs: List[(String, String)], gain: Double) {
    print("Arbitrage: ")
    currencyPairs.foreach(print(_))
    println()
    println("Gain: " + round(gain, 2) + "%")
  }

}