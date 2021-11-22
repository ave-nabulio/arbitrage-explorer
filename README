# Aim

This project is a challenge provided by the SwissBorg company in the context of a recrutment process.

# Challenge

(from: <https://priceonomics.com/jobs/puzzle/>)

Daily trading volume in currency exchange markets often exceeds $1 trillion. With the advent of new crypto-currencies, your knowledge of algorithms, and a good pair of sound-canceling headphones, you're convinced that there could be some profitable arbitrage opportunities to exploit.

Sometimes, these currency pairs drift in a way that creates arbitrage loops where you can convert through a certain sequence of currencies to return a profit in your base currency. This is referred to as an arbitrage loop.

**Goal: write a program that efficiently finds the best arbitrage opportunities.**

## Main Assumptions

- In this market, prices are independent of supply and demand.
- The currency exchange broker provides all trades for free (no transaction fees).

*This asumptions are added by the contributor:*

- The exchange rates are changed only once at the beginning of the script.
- The number of currencies is small. This script has not been tested on larger data set.

# Data Source

To access real-time exchange rates, we use the API: <http://fx.priceonomics.com/v1/rates/>

# Solution

The solution of this problem is a very famous "school case" for graph theory. To solve the problem, we represent the exchange rate matrix with **fully connected directed graph**. The arbitrage opportunities are provided by the negative cycle in the graph. To detect the negative cycles, we use the Bellman-Ford algorithm for V-1 times where V is the number of currencies in the matrix. To discover the negative cycle we need to run the algorithm another time.

## Find the arbitrage

Arbitrage opportunities arise when a cycle is determined such that the edge weights satisfy the following expression

w1 *w2* w3 *...* wn > 1

where the weights represent the exchange rate between two currencies.

Since multiplication is not convenient in the context of the Bellman-Ford algorithm, we transform the arbitrage oportunity constraint, applying a neperian log as follows:

log(w1) + log(w2) + log(w3) + … + log(wn) > 0

and finally, for conveniance, we take the opposite:

(-log(w1)) + (-log(w2)) + (-log(w3)) + … + (-log(wn)) < 0

The Bellman-Ford algorithm is a well known problem so we won't detail here all the steps. We just mention that the main problem to code is the relaxation pass.

# How to run the script (on Ammonite)

To compile and run the script on Ammonite, simply use the following command:

```bash
amm ArbitrageExplorer.sc
```

# Complexity Analysis

In our implementation, the time complexity of the arbitrage discovery is upperbounded by the Bellman-Ford algorithm.

Bellman-Ford makes |E| relaxation for each iteration. Where |E| is the number of edges in the graph. In total, there is |V| iterations (|V - 1| to discover negative cycles and 1 to print it). In worst case then, the algorithm is **O(|V| x |E|)**. With small optimization, we can reach **O(|E|)** for the *best scenario* when only one relaxation is required.

# Remarques

Multiple aspects of the program is suboptimal however we consider here to be sufficient in the perspective of the challenge, regarding the complexity of the dataset and the time scale. Some aspects that could be improved:

- Parallelization of Bellman-Ford (https://ieeexplore.ieee.org/document/8212794), can reach an upperbound of O(|E|)
- Create OO form of the programm for reusability and scalability.

Finding multiple negative cycles in an optimal way is a quite complex task that was solved by (Yamada & Kinoshita, 2002) in the paper *Finding all the negative cycles in a directed graph*. But we consider this as beyond the scope of this challenge.

# Contributor

Raphaël Reis Nunes (<https://github.com/raphaelreis>)

# References

- <https://priceonomics.com/jobs/puzzle/>
- <https://anilpai.medium.com/currency-arbitrage-using-bellman-ford-algorithm-8938dcea56ea#:~:text=Bellman%20Ford%20algorithm%20can%20be,free%20transaction%20must%20act%20quick>.
- <https://github.com/vkostyukov/scalacaster/blob/master/src/graph/Graph.scala>
- <https://www.geeksforgeeks.org/print-negative-weight-cycle-in-a-directed-graph/>
- <https://www.jdoodle.com/embed/v0/27lR>
