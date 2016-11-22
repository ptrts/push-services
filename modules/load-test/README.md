# Load test for push-service
An app that sends pushes to push-service in several concurrent threads (like 20 threads, each of those sends 10 000 pushes) 

Within one thread the pushes are sent one after another, without a delay. 

The results are to be analyzed manually through the JMX metrics and the database of push-service.