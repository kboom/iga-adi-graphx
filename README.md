# Running locally

To run this project locally make sure that you have "include dependencies from provided scope" checked. This project depends on spark which is shipped with the container so it does not need to be included twice.

# Running in a real cluster

docker pull mesosphere/spark:2.5.0-2.2.1-hadoop-2.7


https://stackoverflow.com/questions/37132559/add-jars-to-a-spark-job-spark-submit


# RUNNING
* include a fat jar into spark image
* run from spark binaries at kubernetes master 

#### Run

Forward SparkUI from kubernetes driver pod
```bash
kubectl port-forward $(kubectl get  pods --selector=spark-role=driver --output=jsonpath="{.items..metadata.name}") 4040
```

### Useful commands

```bash
kubectl delete po $(kubectl get po -o=jsonpath='{.items[*].metadata.name}')
```

#### Performance tuning

https://blog.cloudera.com/blog/2015/03/how-to-tune-your-apache-spark-jobs-part-2/