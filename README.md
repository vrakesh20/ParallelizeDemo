# ParallelizeDemo

@All, With this Demo, I want to walk you through Parallel Processing/Multi-Threading in the context of integration between Corporate Applications.

## Background

Over the past few years, I have worked extensively on integration of various corporate applications. Some of these applications are home grown, some others are third party applications which are hosted/managed by us and few others are third party apps that are entirely cloud based. Though parallel processing, multi threading is a very common concept which is used by many developers, I want to walk you through some sample/mock code which is designed keeping integration between corporate applications in mind.

While most of the Applications/Services can scale well, I found that certain cloud based Apps had issues scaling up. Especially, the response time of API calls in some of these Apps is quite slow.

Assume a scenario where in we have 2 systems - 1 which is owned/hosted by us, 2 which is cloud based but having a slower response time. Let's say, we have to design a daily sync program between 2 systems and it involves making 100K API calls. Even though our system is robust enough, hosted on high infra and is extremely fast, some times, if the 3rd party system which we are trying to integrate with is slow, then it can directly hamper the performance. In such cases, using parallel processing can be an option to improve the performance of the integration.

ParallelizeDemo.java contains sample code for the same. In real-time, we may integrate anything between 2 systems, ex: an employee record, a purchase order, an invoice, an asset, ledger information, etc.
So, in sample code, I created a class called "Item2Integrate" which can be replaced with a class/object that applies.
Similarly, the fetchDataFromSourceSystem function can be filled with logic that's applicable. In the makeAPICall method, I used Thread.Sleep() function so as to mock the response time taken by the API call.

FYI: As this is just demo/mocking code, I just passed 1 record per API call. There can be certain cases, where in you can do a bulk post/put in a single API call.

## Sample Output

Items in Source System		: 20000
Target System APIResponseTime	: 1 milli seconds
Number of CPUs			: 1

Test Description		: 1000 as step size & 1 Threads
Time Taken			    : 58736 milli seconds
pool-1-thread-1			: {0 1000},{1000 2000},{2000 3000},{3000 4000},{4000 5000},{5000 6000},{6000 7000},{7000 8000},{8000 9000},{9000 10000},{10000 11000},{11000 12000},{12000 13000},{13000 14000},{14000 15000},{15000 16000},{16000 17000},{17000 18000},{18000 19000},{19000 20000}

Test Description		: 1000 as step size & 2 Threads
Time Taken			    : 29330 milli seconds
pool-2-thread-1			: {0 1000},{2000 3000},{4000 5000},{6000 7000},{8000 9000},{10000 11000},{12000 13000},{14000 15000},{16000 17000},{18000 19000}
pool-2-thread-2			: {1000 2000},{3000 4000},{5000 6000},{7000 8000},{9000 10000},{11000 12000},{13000 14000},{15000 16000},{17000 18000},{19000 20000}

Test Description		: 1000 as step size & 4 Threads
Time Taken			    : 14693 milli seconds
pool-3-thread-1			: {0 1000},{7000 8000},{11000 12000},{14000 15000},{17000 18000}
pool-3-thread-2			: {1000 2000},{6000 7000},{10000 11000},{13000 14000},{16000 17000}
pool-3-thread-3			: {2000 3000},{5000 6000},{9000 10000},{12000 13000},{18000 19000}
pool-3-thread-4			: {3000 4000},{4000 5000},{8000 9000},{15000 16000},{19000 20000}

Test Description		: 1000 as step size & 8 Threads
Time Taken			    : 8821 milli seconds
pool-4-thread-1			: {0 1000},{15000 16000}
pool-4-thread-2			: {1000 2000},{14000 15000}
pool-4-thread-3			: {2000 3000},{13000 14000}
pool-4-thread-4			: {3000 4000},{12000 13000}
pool-4-thread-5			: {4000 5000},{11000 12000},{19000 20000}
pool-4-thread-6			: {5000 6000},{10000 11000},{18000 19000}
pool-4-thread-7			: {6000 7000},{9000 10000},{17000 18000}
pool-4-thread-8			: {7000 8000},{8000 9000},{16000 17000}

## Analysis

As seen in the above results/stats, by using parallel processing, the performance of the integration is enhanced greatly. But please be wary about using the number of threads. Because there can be a scenario where in source system can also be running a bunch of other critical processes around the same time when it is running this integration. In such a scenario, if majority of the threads are dedicated for this integration alone, it may not be the right thing. Alternatively, please check the network/bandwidth limitations of the 3rd party Application which you wish to integrate with. If the third party system can't scale up, no point spawning beyond a particular number of threads as it may not yield the desired results. So trying to analyze the case and striking the right balance between all these various parameters is truly important.
