## Overview
The service receives push notifications via HTTP and sends them to client devices via [Swrve push API](https://docs.swrve.com/swrves-apis/api-guides/swrve-push-api-guide/).

It's got a database. 

Pushes are stored to the database as soon as they are received and are updated as they go through their life cycle.

Pushes may be for sending as soon as possible or they may be scheduled to be sent in some time of the day. 

Attempts to send pushes are stored to the DB as well. 

There may be an error while trying to send a push. After some errors there are going to be retries after some time, after others the push is marked as failed and there would be no more tries. 

Pushes are sent to Swrve push API in concurrent threads, the service allows it, but there is a minimum delay between starting sending threads that must be held or Swrve would respond with 429 code (Rate Limit Exceeded). 

There are metrics accessible via JMX, like sending time, number of active sending threads, error counters and others. 
## Controller
There is a controller receiving **POST /push** requests. 

The body of a request contains a JSON like this:

```JSON
{
	"platform": "IOS", 
	"messagePrototypeKey": "Hello", 
	"pushKey": "463B3209-6E33-4E88-AF52-CDA87C0550EC", 
	"message": "Hello client!", 
	"cronExpression": null
}
```
Where:

* **platform** - IOS or ANDROID
* **messagePrototypeKey** - the key of the message prototype in loans project
* **pushKey** - the client's device id in Swrve, that is earlier provided by the mobile app to web-api
* **cronExpression** - used to describe the time interval when the push can be sent

The response body is empty. 

Received pushes are added to **PUSH_NOTIFICATIONS** database table. 

Sometimes received pushes are also added to the sending queue (see Queue), and sometimes they are just left in the DB to be put into the queue later by Quartz jobs. 

After the request is processed, the situation may be one of the following:

* PUSH_NOTIFICATIONS.STATUS =  IN_MEMORY and the push is in the queue (cronExpression == null and there was room in the queue)
* PUSH_NOTIFICATIONS.STATUS =  PENDING (cronExpression == null and there was no room in the queue)
* PUSH_NOTIFICATIONS.STATUS =  SCHEDULED (cronExpression != null)

The average time to receive a push is 50-200ms. 
## Queue
The queue is an **ArrayBlockingQueue** of **push.queue.capacity** size. 
### Reading the queue
The reading happens by a schedule. The delay between two readings is **swrve.delayMillis**.

**swrve.delayMillis** must be 4ms, so the rate of requests to Swrve wouldn't exceed **300 per second**, or else Swrve would respond with **429 (Rate Limit Exceeded)** HTTP response code. 

After a push is read from the queue, a sending thread is started in a special thread pool for sending to Swrve. 

**Note**. If the micro service is stopped while some pushes are IN_MEMORY (in the queue or in the thread pool), then after the restart they all will be made PENDING and thus will make it to the queue again. That may cause the ones from the pool be sent twice. 
## Thread pool for sending attempts
For the pool not to overflow and always have free threads the maximum size is calculated 
as **swrve.averageProcessingMillis / swrve.delayMillis**, where **swrve.averageProcessingMillis** is the expected average Swrve response time. 

If the average Swrve response time doesn't exceed **swrve.averageProcessingMillis**, the pool won't overflow.

If Swrve starts to respond slower, it may. 

This is desirable, as the number of concurrent threads in an application shouldn't be unlimited. 

For this matter the thread pool executor is configured to have 
**rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy()**

It means that when the pool reaches the max, the queue reading thread won't pass the sending to the pool, but will do it itself. 

**Note**. There may be only one queue reading thread. This is accomplished by configuring it's method as 
@Scheduled(**fixedDelayString** = "${swrve.delayMillis}")
It's **fixedDelayString** there, and not **fixedRateString**. 
**fixedDelayString** is the time between the **completion** of the method execution, and the **beginning** of the next execution. 

As there may be just one thread for reading the queue, then when the pool overflows, the number of sending threads won't grow without control. 

The only sending threads may be the pool ones and the one reading the queue. 

**Note**. In prod **swrve.averageProcessingMillis** is used only for limiting the sending thread pool size. In tests it is used to emulate the Swrve response time. 
## The Process of sending a push
For each attempt to send a push a row in SEND_ATTEMPTS is created. 

The columns are:

* ID
* PUSH_NOTIFICATION_ID
* STATUS - IN_PROCESS|OK|ERROR
* MILLIS
* ERROR_TYPE - SWRVE|MESSAGE_PROTOTYPE_KEY|OTHER
* SWRVE_ERROR_CODE
* SWRVE_ERROR_MESSAGE
* OTHER_ERROR_STACK_TRACE

### Errors that may occur while sending
#### HTTP status code  429, 500, 502, 503 or 504
The push goes to RETRY state. 

The next try is going to be after **push.retry.delayInSeconds**. 

The max number of attempts a push may have is **push.retry.maxAttempts**. 

After they are exceeded the push goes to GIVEN_UP state, and there won't be any more attempts. 

SEND_ATTEMPTS.ERROR_TYPE = SWRVE
#### Other 4xx or 5xx HTTP status code
The push goes to FAILED state. 

SEND_ATTEMPTS.ERROR_TYPE = SWRVE
#### MessagePrototypeException (see Swrve push keys)
The push goes to FAILED state.

SEND_ATTEMPTS.ERROR_TYPE =  MESSAGE_PROTOTYPE_KEY
#### Some RuntimeException while working with the DB
This is unlikely to happen, but if it does, the push stays in IN_MEMORY state while not being in the queue. 

It won't be sent until the micro service is restarted, then it would be loaded to the queue again. 

SEND_ATTEMPTS and PUSH_NOTIFICATIONS remain unchanged. 
#### Some RuntimeException while sending
The push goes to FAILED state.

SEND_ATTEMPTS.ERROR_TYPE =  OTHER

OTHER_ERROR_STACK_TRACE = < the stacktrace >
### Swrve push keys (or transactional campaigns)
In loan project we've got a list of message prototypes. 

There is a similar list in our Swrve account. 

For every push notification message prototype we have in loans project we want to have an associated item in the Swrve list (in fact it's two items: for Android and for iOS).

It will help us analyze pushes of each message prototype separately. 

The mappings are stored in properties project. 

Here is an example:
```YAML
swrve:
  messagePrototypePushKeys:
    'Hello':
      IOS: d36ae023-010c-4f3a-9bd7-9924a754b4b4
      ANDROID: f31690cb-a763-4259-af18-6aed41afd9ed
    'Good bye':
      IOS: d36ae023-010c-4f3a-9bd7-9924a754b4b4
      ANDROID: f31690cb-a763-4259-af18-6aed41afd9ed
```

If while sending a push it fails to find the corresponding Swrve push key, an exception will be thrown. 

### If a new push notification message prototype is added in loans

Let's assume we're adding a new message prototype for a brand (let the brand be **vivus.se**). 

We have two mobile apps used by vivus.se clients: **vivus.se for Android** and **vivus.se for iOS**. 

For each one of them, before the new prototype is in the vivus.se prod environment, we'd want to do the following. 

For each of the apps we add a new **transactional campaign** in the Swrve account we use for vivus.se.

The transactional campaigns must have the same name as the message prototype will be seen in backoffice lists. 

In **properties** project add mappings to Swrve transactional campaigns from the message prototype. 
## Push states
* **SCHEDULED** - waiting for a job for putting scheduled pushes into the queue
* **PENDING** - the last time there was an attempt to put the push in the queue, the latter was full. There's a job that tries to put pending pushes to the queue every **push.job.sendPending.repeatInterval** ms
* **IN_MEMORY** - the push is either in the queue or in the sending thread pool, waiting for Swrve to respond
* **RETRY** - there've been from **1** to **push.retry.maxAttempts - 1** unsuccessful attempts to send the push, and the errors were the ones that allow retries. **push.retry.delayInSeconds** must pass after a try before the next one. 
* **GIVEN_UP** -  there've been **push.retry.maxAttempts** unsuccessful attempts to send the push and there will be no more. 
* **SENT** - the push is successfully sent, no other actions required. 
* **FAILED** - there's been an error while sending, that doesn't allow retries. There will be no more tries. 
## SendScheduledJob
The job queries SCHEDULED and RETRY pushes from the DB and puts them to the queue. 

There may be not enough room in the queue, so not every push retrieved may pass to it. 

As there is room, the pushes go to the queue.

As soon as the queue is full, the job execution stops, and the next execution is going to be by the schedule. 

The job has the following properties:

* **push.job.sendScheduled.startDelay** - after how many millis since the startup the first execution should start
* **push.job.sendScheduled.repeatInterval** - after how many millis since an execution the next execution should start. 
* **push.job.sendScheduled.portionSize** - how many pushes can be handled during an execution.

The following rules must be satisfied:

* push.job.sendScheduled.portionSize < push.job.queue.capacity
* push.job.sendScheduled.portionSize * swrve.delayMillis < push.job.sendScheduled.repeatInterval
## SendPendingJob
The job queries PENDING pushes from the DB and puts them to the queue. 

There may be not enough room in the queue, so not every push retrieved may pass to it. 

As there is room, the pushes go to the queue, as soon as it's full the job execution stops, and the next execution is going to be by the schedule. 

The job has the following properties:

* **push.job.sendPending.startDelay** - after how many millis since the startup the first execution should start
* **push.job.sendPending.repeatInterval** - after how many millis since an execution the next execution should start. 
* **push.job.sendPending.portionSize** - how many pushes can be handled during an execution.

The following rules must be satisfied:

* push.job.sendPending.portionSize < push.job.queue.capacity
* push.job.sendPending.portionSize * swrve.delayMillis < push.job.sendPending.repeatInterval
## Metrics
There are codehale metrics accessible via JMX:

* **receivingTime** - the statistics of the time it takes the micro service to receive a push
* **scheduledPushReceived** - how many pushes with cronExpression != null have been received by the micro service
* **immediatePushReceived** - how many pushes with cronExpression == null have been received by the micro service
* **immediatePushWentPending** - how many pushes received by the micro service didn't make it to the queue and become PENDING
* **sendingSuccess** - how many pushes have been successfully processed by Swrve Push API
* **sendingTime** - the statistics of Swrve response time
* **sendingFail** - how many pushes have failed to be sent by Swrve due to an error at Swrve side, our side or somewhere else around
* **sendingAttemptFailed** - how many times there's been an error while sending that allows retries
* **sendingGivenUp** - how many pushes have exceeded max attemts
* **swrveSendingError** - how many pushes failed to be sent due to an error at Swrve side
* **messagePrototypeKeySendingError** - how many pushes have failed to be sent due to the absence of Swrve push_key for the mobile platform and the message prototype key
* **otherSendingError** - runtime exceptions on our side while sending
* **notStoredExceptionWhileAttempt** - runtime exceptions on our side related to the db access
* **queueSize** - the size of the queue of pushes to send
* **sendingThreadPoolSize** - the number of sending threads in the pool waiting for Swrve API to respond
* **notStoredExceptionWhileReceiving** - exceptions while receiving pushes by the micro service
* **notStoredExceptionReadingQueue** - exceptions while reading the queue related to the db access
## A test results
The test had 20 threads sending pushes to the micro service. Each thread sent 10000 pushes one after another without a delay. 

All pushes were addressed to one real mobile app installation on a real device. 

This is the statistics of Swrve response time. 

We can see that the average response time is near 1000, but sometimes it may be up to 4000 and even more. 

If we have **swrve.averageProcessingMillis = 2000** it would mean the thread pool size **500**. 

If we have **swrve.averageProcessingMillis = 4000** the thread pool size would be **1000**.
 
| Stat            | Millis |
|-----------------|-------:|
| 50thPercentile  | 983    |
| 75thPercentile  | 2513   |
| 95thPercentile  | 3883   |
| 98thPercentile  | 4510   |
| 99thPercentile  | 5956   |
| 999thPercentile | 7688   |
| Min             | 233    |
| Mean            | 1541   |
| StdDev          | 1320   |
| Max             | 7701   |
