## Solace benchmarks using Java and Stomp WebSockets

###Summary

We tested the time it takes for a message to travel from the Solace publisher to a java proxy server and then from the server to a UI. 

A trial consists of 1,000 messages of the same twelve character string "Hello World!" being sent at fixed interval. We changed the value of the interval from 10ms to 25ms and run each trial twenty times per interval.

The Java application sends the messages over WebSockets using a Stomp client, with each message connection using its own thread. Since the Solace cloud configuration allows at most 50 simultaneous connections to its VPN, a thread pool managed the execution of the threads, with the MAX_THREADS parameter set to 50.


###Results

###Message Integrity
Each trial of the 10ms interval group lost an average of 25 messages. With each lost message, one of two issues occurred: 1) The `StompClient.send()` function took longer to execute than WebSocket functions using other protocols (i.e. NodeJS). As a result, fewer messages were sent before the timer cleared the function executed on each interval. 2) A JCSMPTransportException ocurred where the TCPClient cannot connect or read from the router. At greater intervals both issues still occured, though with less frequency.

#####[Table 1] Messages Missing per Trial (n = 1000 messages)

| Trial # | 10 ms   | 15 ms   | 20 ms   | 25 ms   |  
| ------: | ------: | ------: | ------: | ------: |
| **1**   | 20      | 0       | 0       | 0       |
| **2**   | 30      | 0       | 0       | 0       |
| **3**   | 43      | 0       | 1       | 0       |
| **4**   | 20      | 0       | 3       | 0       |
| **5**   | 13      | 0       | 5       | 0       |
| **6**   | 18      | 0       | 0       | 0       |
| **7**   | 18      | 0       | 2       | 0       |
| **8**   | 15      | 0       | 0       | 0       |
| **9**   | 42      | 0       | 0       | 0       |
| **10**  | 12      | 0       | 2       | 0       |
| **11**  | 26      | 0       | 2       | 0       |
| **12**  | 19      | 0       | 3       | 0       |
| **13**  | 17      | 0       | 0       | 2       |
| **14**  | 15      | 0       | 0       | 0       |
| **15**  | 51      | 0       | 0       | 0       |
| **16**  | 25      | 0       | 0       | 0       |
| **17**  | 42      | 1       | 2       | 0       | 
| **18**  | 22      | 0       | 3       | 0       |
| **19**  | 19      | 0       | 1       | 0       |
| **20**  | 30      | 0       | 0       | 0       |


###Speed
The time between both from Solace to java server and from server to UI are fairly consistent between intervals. In both cases the highest and with the highest variation is at 10ms, and they decrease slighly as the intervals increase. Further, the deviation for the time from server to UI is greater than the mean for 10ms, meaning the distribution of those trials may have multiple peaks. Please look at Table 2 and Table 3 below for more details.  


#####[Table 2] Statistics for time between Solace and java proxy server (n = 1000 messages, averaged over 20 trials each)
| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | 9        | 98       | 12        | 14          | 18.269    | 10.7871    |
| **15**        | 9        | 79       | 11        | 13          | 17.227    | 10.1057    |
| **20**        | 9        | 92       | 11        | 13          | 17.012    | 10.3652    |
| **25**        | 9        | 80       | 11        | 12          | 16.221    | 9.9017     |


#####[Table 3] Statistics for time between java proxy server and UI (n = 1000 messages, averaged over 20 trials each)

| Interval (ms) | min (ms) | max (ms) | mode (ms) | median (ms) | mean (ms) | stdev (ms) |
| ------------: | -------: | -------: | --------: | ----------: | --------: | ---------: |
| **10**        | < 1      | 47       | 2         | 3           | 3.710     | 4.0670     |
| **15**        | < 1      | 24       | 2         | 2           | 2.120     | 1.5007     |
| **20**        | < 1      | 29       | 2         | 2           | 2.281     | 1.9184     |
| **25**        | < 1      | 19       | 2         | 2           | 1.973     | 1.2905     |

