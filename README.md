# NexCloud SMACK Stack Sample

* 데이터 소 스 : 트위터
* 브로커 : Kafka ( Kafka topic : tweet_spark )
* 데이터 저장소 : cassandra
* 실시간 스트리밍 변환 : Spark
* 프로세실행 : Akka {Actor, AkkaHttpStreaming}

----------------------------------------------------------------------------------------------------
### Twitter -> Akka HTTP with Streaming -> Kafka ( Raw Data 수집 )
----------------------------------------------------------------------------------------------------
1) Twitter에서 제공한 API로 Event Listner등록
2) Event발생시 마다 Akka Http Streaming API를 사용하여 Kafka 저장

----------------------------------------------------------------------------------------------------
### Kafka -> Akka with Actor -> Cassandra ( Raw Data 저장 )
----------------------------------------------------------------------------------------------------
1) Akka Actor를 이용하여 프로세스 실행
2) 1초마다 Loop를 돌아 Kafka 데이터 체크
3) Insert json into Cassandra
DC/OS Service json
{ "id": "/kafka-to-cassandra", "instances": 1, "cpus": 1, "mem": 2048, "disk": 0, "gpus": 0, "constraints": [], "fetch": [], "storeUrls": [], "backoffSeconds": 1, "backoffFactor": 1.15, "maxLaunchDelaySeconds": 3600, "container": { "type": "DOCKER", "volumes": [], "docker": { "image": "mashine/kafka-to-cassandra", "network": "BRIDGE", "portMappings": [ { "containerPort": 0, "hostPort": 10120, "servicePort": 10120, "protocol": "tcp", "name": "default" } ], "privileged": false, "parameters": [], "forcePullImage": true } }, "healthChecks": [], "readinessChecks": [], "dependencies": [], "upgradeStrategy": { "minimumHealthCapacity": 1, "maximumOverCapacity": 1 }, "unreachableStrategy": { "inactiveAfterSeconds": 300, "expungeAfterSeconds": 600 }, "killSelection": "YOUNGEST_FIRST", "requirePorts": true, "env": { "KAFKA_HOSTS": "broker.kafka.l4lb.thisdcos.directory", "twitter4j.oauth.consumerSecret": "44QsnMytptwe5bMmY7bDkuEgG3Bod1RI5JoawjMmfgyhj2YtCg", "twitter4j.oauth.consumerKey": "0vz48WJnCLnmdt62g88qNd5fD", "twitter4j.trace": "true", "twitter4j.oauth.accessToken": "110605978-fm3juyjVcEiP4uIu3ZWRGzQSS69JsqGhOGa45y5A", "KAFKA_PORT": "9092", "twitter4j.oauth.accessTokenSecret": "1BRrH0MWB7nmcXDS84MhJol83KkKOwu2FRUqzXLe5Y0Zb", "KAFKA_TOPIC": "tweet_spark" } }

----------------------------------------------------------------------------------------------------
### Kafka -> Spark with Streamig -> Cassandra ( Realtime Data 가공 )
----------------------------------------------------------------------------------------------------
1) DCOS spark 명령으로 프로세스 실행
2) Spark Streaming으로 데이터 유실없는 실시간 가공데이터 저장( Cassandra )
dcos spark run --submit-args='--driver-cores 0.1 --driver-memory 1024M --total-executor-cores 4 --class com.nexcloud.speed.SparkStreamingKafkaConsumer http://192.168.0.120/Lambda-Arch-Spark-assembly-0.1.0.jar tweets node.cassandra.l4lb.thisdcos.directory:9042 broker.kafka.l4lb.thisdcos.directory:9092'

http://192.168.0.120/Lambda-Arch-Spark-assembly-0.1.0.jar : 변경 될 URL정보
Cassandra with raw data -> Spark with sql( Akka Actor ) -> Cassandra ( Batch Data 가공 )

----------------------------------------------------------------------------------------------------
### Akka Actor로 프로세스 실행
----------------------------------------------------------------------------------------------------
1) Spark Sql(RDD)를 이용한 raw data가공
2) 가공데이터 batch table에 저장
dcos spark run --submit-args='--driver-cores 0.1 --driver-memory 1024M --total-executor-cores 4 --class com.nexcloud.batch.BatchProcessor http://192.168.0.120/Lambda-Arch-Spark-assembly-0.1.0.jar tweets node.cassandra.l4lb.thisdcos.directory:9042 broker.kafka.l4lb.thisdcos.directory:9092'

http://192.168.0.120/Lambda-Arch-Spark-assembly-0.1.0.jar : 변경 될 URL정보
