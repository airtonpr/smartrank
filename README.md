<h1>***** SmartRank ***** </h1>
<br>

Resource scarcity is a major obstacle for many mobile applications, since devices have limited energy power and processing
potential. As an example, there are applications that seamlessly augment human cognition and typically require resources that
far outstrip mobile hardware's capabilities, such as language translation, speech recognition, and face recognition. A new trend
has been explored to tackle this problem, the use of cloud computing. This study presents SmartRank, a scheduling framework to
perform load partitioning and offloading for mobile applications using cloud computing to increase performance in terms of response
time. 
<br>

Projects 

<p>
<ul>
<li>ClientAndroidFaceRecognition: A simple Android client application that send pictures for a server to perform face recognition.
The main class is ClientAndroidFaceRecognition\src\com\main\MainImageActivity.java</li>

<li>Cloudlet: A cloudlet is a resource-rich computer or cluster of computers with fast Internet and available for use by nearby mobile
devices. In our case is a simple server that receive the requests and schedule for a bunch of other servers.
The main class is Cloudlet\src\main\CloudletTransmitter.java</li>

<li>FaceDetectionAndRecognitionServer: It includes the software responsible for performing the face recognition process. 
The main class is FaceDetectionAndRecognitionServer\src\main\TCPServerManager.java</li>
</ul>

The code is configured to run as on Cloudlet as on Server in localhost. You must only to isntall the message server that
interconnect both parts: RabbitMq Server: http://www.rabbitmq.com/download.html 

In case you want to change the servers location just reconfigure the properties files: 
Cloudlet\config-cloudlet.properties e FaceDetectionAndRecognitionServer\config-server.properties

</p>