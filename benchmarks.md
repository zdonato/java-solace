# Solace benchmarks using Java and Stomp WebSockets

### Summary
We tested the time it takes for a message to travel from the Solace publisher to a java proxy server and then from the server to a UI. 

The Java application sends the messages over WebSockets using a Stomp client, with each message connection using its own thread. Since the Solace cloud configuration allows at most 50 simultaneous connections to its VPN, a thread pool managed the execution of the threads, with the MAX_THREADS parameter set to 50.

## Asynchronous Interval Test (10ms-25ms)
In this test set we used a Web Worker to schedule repeatedly sending a message asynchronously at a given interval through JavaScript's setInterval method. 

A trial consists of 1,000 messages of the twelve character string "Hello World!" being sent that fixed interval. We changed the value of the interval from 10ms to 25ms and ran each trial sixty times per interval.

### Results

### Message Integrity
Throughout all of the trials, no messages were lost.

### Speed
For the times from Solace to the Java server, both mean and deviation decreased with the interval increase. For example, between 10ms and 15ms, the mean decreased by 18%, from 20.567ms to 16.894ms, and standard deviation decreased by 14%, from 12.582ms to 10.837ms. For subsequent intervals, the decrease is not as steep with a mean decrease of 1% and a standard deviation decrease of 2% between 15ms and 20ms.

The time between both from the Java server also decreases with interval increase. Between 10ms and 15ms, the mean decreased by 27%, from 2.511ms to 1.838ms, and standard deviation decreased by 34%, from 1.852ms to 1.207ms. For subsequent intervals, the decrease is not as steep with a mean decrease of 9% and a standard deviation decrease of 11% between 15ms and 20ms.    

##### [Table 1] Time between Solace and java proxy server (over 60 trials each)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | 9        | 103      | 12        | 16          | 20.567    | 12.582     |
| **15**        | 8        | 90       | 11        | 13          | 16.894    | 10.837     |
| **20**        | 9        | 86       | 11        | 12          | 16.681    | 10.575     |
| **25**        | 9        | 77       | 11        | 12          | 16.247    | 10.105     |

##### [Table 2] Time between java proxy server and UI (over 60 trials each)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | < 1      | 21       | 1         | 2           | 2.511     | 1.852      |
| **15**        | < 1      | 16       | 2         | 2           | 1.838     | 1.207      |
| **20**        | < 1      | 14       | 1         | 2           | 1.681     | 1.075      |
| **25**        | < 1      | 15       | 1         | 1           | 1.559     | 1.014      |


## Synchronous for-loop Test (0ms)

### Summary
In this test set we used a for-loop to send all messages in a series.

A trial consists of 1,000 messages of the twelve character string "Hello World!" with no interval between the messages. Each trial was run sixty times.

### Results

### Message Integrity
Throughout all of the trials, no messages were lost.

### Speed
For the times from Solace to the Java server, both mean and deviation were higher for the synchronous test then asynchronous tests. For example, when comparing to the asynchronous 10ms test, the synchronous test's mean was 116% higher, from 20.567ms to 44.392ms. The standard deviation was 120% higher, from 12.582ms to 27.688ms. 

The times from the Java server to the UI were also higher with the synchronous test than the asynchronous tests. Comparing again to the asynchronous 10ms test, the synchronous test's mean was 23% higher, from 2.511 to 3.090. The standard deviation was 314% higher, from 1.852ms to 7.731ms. This standard deviation is 4.2 times greater than the mean, showing the possibility of multiple peaks in the distribution of times from the Java server to the UI.

##### [Table 3] Time between Solace and java proxy server (over 60 trials)

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| -------------:   | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | 18       | 345      | 34        | 39          | 44.392    | 27.688     |
| **Async (10ms)** | 9        | 103      | 12        | 16          | 20.567    | 12.582     |

##### [Table 4] Time between java proxy server and UI (over 60 trials)

|  Test Type (ms)  | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| -------------:   | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **Sync (0ms)**   | < 1      | 107      | 2         | 2           | 3.090     | 7.731      |
| **Async (10ms)** | < 1      | 21       | 1         | 2           | 2.511     | 1.852      |