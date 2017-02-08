# Parallel-Artificial-Bee-Colony
The Vehicle Routing Problem (VRP) is a combinatorial optimization problem where the goal is to find the most optimal set of routes for a fleet of vehicles to service a given set of customers. This can be seen as a general case of the Travelling Salesman Problem where there are <i>m</i> vehicles that start out of a depot, service each customer once, and then return back to the starting depot. Also, several further constraints can be applied to the problem statement such as the capacity of each vehicle, time windows for each customer, etc. 

<p align="center">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/abc.png" width="300">
</p>


We try to solve the Vehicle Routing Problem by using the Artificial Bee Colony (ABC) algorithm, an optimization algorithm that mimics the swarm intelligence of bees in nature. We implement this algorithm in parallel over several cores and present a comparative study of the results.

<p align="center">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/WS-CR.png" width="500">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/WS-SC.png" width="500">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/WS-EC.png" width="500">
</p>

<p align="center">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/SS-RT.png" width="500">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/SS-S.png" width="500">
  <img src="https://github.com/ad8454/Parallel-Artificial-Bee-Colony/blob/master/images/SS-EC.png" width="500">
</p>
