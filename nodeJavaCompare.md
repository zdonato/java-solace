# Comparison of NodeJS and Java benchmarks for Solace

## Objective
We decided to compare the speeds of a Java driven and a NodeJS driven application using the Solace messaging system, currently the fastest, most efficient messaging system on the market. 

## Test Summary
We ran two different types of tests, an asynchronous messaging test where the messages were sent with an interval between them, and synchronous test where all the messages were sent in series.

Both applications had only two components: a frontend and a proxy server. One sever was written in Java while the other was written in NodeJS. WebSockets were used to connect to the Solace cloud. While NodeJS has its own native WebSocket implementation, Java needed an extra layer to facilitate the data exchange across the TCP connection, for which we chose the STOMP protocol and STOMP web client.

Futhermore, the Java application sent each message in its own thread to the Solace cloud. Since the Solace cloud configuration allows at most 50 simultaneous connections to its VPN, a thread pool managed the execution of the threads, with the MAX_THREADS parameter set to 50.

## Tests
### Asynchronous Interval Test
In this test set we used a Web Worker to schedule repeatedly sending a message asynchronously at a given interval through JavaScript's setInterval method. 

A trial consists of 1,000 messages of the twelve character string "Hello World!" being sent during the fixed interval. We changed the value of the interval from 10ms to 25ms and ran each trial sixty times per interval.

#### Results

##### Solace to Proxy Server

For the 10ms interval, both the mean and standard deviation have higher values with the Java application than with the NodeJS application. The Java application has a mean of 20.567 and a standard deviation of 12.582, while the NodeJS application has a mean of 16.092 and a standard deviation of 8.112. The difference between them is 4.48ms and 4.47ms respectively.  

While in subsequent intervals the Java application means decrease to 16.247ms for 25ms, the means for the NodeJS application fluctuate between 15.982ms and 16.680ms throughout the intervals. At 25ms, the Java application has a mean 16.247ms while the NodeJS application has a mean of 16.680ms, a difference of a mere 433μs lower for the Java application. 

The standard deviation for subsequent intervals for the Java application also decreases, but never reaches the NodeJS application values. For example at 25ms, the Java application has a standard deviation of 10.105ms, while the NodeJS application has 9.224ms, a 881μs difference.  

##### [Table 1] Time between Solace and Java proxy server (over 60 trials each)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | 9        | 103      | 12        | 16          | 20.567    | 12.582     |
| **15**        | 8        | 90       | 11        | 13          | 16.894    | 10.837     |
| **20**        | 9        | 86       | 11        | 12          | 16.681    | 10.575     |
| **25**        | 9        | 77       | 11        | 12          | 16.247    | 10.105     |


##### [Table 2] Time between Solace and NodeJS proxy server (over 60 trials)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | 9        | 61       | 11        | 13          | 16.092    | 8.112      |
| **15**        | 9        | 65       | 10        | 12          | 15.982    | 8.460      |
| **20**        | 9        | 75       | 11        | 13          | 16.404    | 9.472      |
| **25**        | 9        | 76       | 11        | 13          | 16.680    | 9.224      |

##### Proxy Server to UI
Both the Java application and NodeJS application mean and stanard deviation values decrease as the intervals increase. The values for the Java application decrease by the order of 100μs, while the NodeJS application values decrease by the order of 10μs. 

At the 10ms interval, the Java application has a mean of 2.511 and a standard deviation of 1.852, while the NodeJS application has a mean of 1.095 and a standard deviation of 0.746. The difference between them is 1.42ms and 1.11ms respectively. For subsequent intervals, both the means and standard deviations for the Java application get closer to those of the NodeJS application without reaching them. For example, at the 25ms interval, the mean for the Java application was 1.559ms and the standard deviation was 1.014ms, while the NodeJS application had a mean of 1.040ms and a standard deviation of 0.6823ms, a difference of 519μs and 332μs respectively. 

##### [Table 3] Time between Java proxy server and UI (over 60 trials each)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | < 1      | 21       | 1         | 2           | 2.511     | 1.852      |
| **15**        | < 1      | 16       | 2         | 2           | 1.838     | 1.207      |
| **20**        | < 1      | 14       | 1         | 2           | 1.681     | 1.075      |
| **25**        | < 1      | 15       | 1         | 1           | 1.559     | 1.014      |


##### [Table 4] Time between NodeJS proxy server and UI (over 60 trials)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | < 1      | 6        | 1         | 1           | 1.095     | 0.746      |
| **15**        | < 1      | 6        | 1         | 1           | 1.070     | 0.722      |
| **20**        | < 1      | 6        | 1         | 1           | 1.088     | 0.709      |
| **25**        | < 1      | 7        | 1         | 1           | 1.040     | 0.6823     |

### Synchronous Test
In this test set we used a for-loop to send all messages in a series.

A trial consists of 1,000 messages of the twelve character string "Hello World!" with no interval between the messages. Each trial was run sixty times.

#### Results

##### Solace to Proxy Server
##### [Table 5] Time between Solace and Java proxy server (over 60 trials)
The mean and standard deviation for the NodeJS application, 231.962ms and 64.791ms was markedly higher than those for the Java application, which was 44.392ms and 27.688ms. This makes the difference between the means and standard deviations of the two types of applications 188ms and 37ms respectively.

The values for both types of applications was also higher for the sync test than the async test, with around 116% higher for the Java application mean and 13415% higher for the NodeJS application mean.

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ---------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | 18       | 345      | 34        | 39          | 44.392    | 27.688     |
| **Async (10ms)** | 9        | 103      | 12        | 16          | 20.567    | 12.582     |


##### [Table 6] Time between Solace and NodeJS proxy server (over 60 trials)

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| -------------:   | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | 100      | 348      | 241       | 236         | 231.962   | 64.791     |
| **Async (10ms)** | 9        | 61       | 11        | 13          | 16.092    | 8.112      |


##### Proxy Server to UI
The mean and standard deviation for the NodeJS application, 8.939ms and 20.507ms was also higher than those for the Java application, which was 3.090ms and 7.731ms. This makes the difference between the means and standard deviations of the two types of applications 5.8ms and 12.8ms respectively.

The values for both types of applications were also higher for the sync test than the async test, with around 23% higher for the Java application mean and 716% higher for the NodeJS application mean.

Additionally, for both types of applications the standard deviation is higher than the mean, pointing to the possibility of multiple peaks in the distributions.

##### [Table 7] Time between Java proxy server and UI (over 60 trials)

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ---------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | < 1      | 107      | 2         | 2           | 3.090     | 7.731      |
| **Async (10ms)** | < 1      | 21       | 1         | 2           | 2.511     | 1.852      |


##### [Table 8] Time between NodeJS proxy server and UI (over 60 trials)

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| -------------:   | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | < 1      | 100      | 1         | 1           | 8.939     | 20.507     |
| **Async (10ms)** | < 1      | 6        | 1         | 1           | 1.095     | 0.746      |

## Conclusions
For the Async Interval test, the NodeJS application was more consistent in its values between the intervals for both from the Solace to proxy server and from the proxy server to the UI than the Java application. The NodeJS application was also between a few milliseconds to a few hundred microseconds faster than the Java application.

For the Synchronous test, on the other hand, the Java application was faster than the NodeJS application by a significant margin. Since this could be due to the Java applications use of multithreading, while the JavaScript language and NodeJS lend themselves to be more efficient in asynchronous design patterns and scenarios (such as UI interaction). With the asynchronous tests, the setInterval function put each message in its own worker thread, utilizing client-side parallelization. The for-loop, on the other hand, used a single thread on the client side for sending all messages, possibly creating bottlenecks in the response times.   