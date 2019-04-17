# async-task
An async task executor in producer-consumer mod which could automatically shutdown when all tasks are executed and block producer threads when too many tasks undone to prevent OOM and release them when space is free.

## Usage
This project is used for producer-consumer/producer-consumer situation when the consumer/producer spends much more time than thee producer and the consumer.

eg: There is a affair which reads large quantity of data from file and then do some time consuming task such like networks and finally write results to disk.

## How to use

New an instance of AsyncTaskService and then invoke the submitTask method.

Notice: The implementation of of Producer should do time consuming tasks while the implementation of Consumer does the opposite.
