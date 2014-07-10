package recognition;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import main.CloudletTransmitter;
import utils.CloudletUtils;
import utils.RabbitMQUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class TransmissionClient implements Runnable {

	private final CountDownLatch signalToAllProcess;
	private final CountDownLatch signalToServersNumber;

	private String ipServer;
	private CloudletUtils tcpClientCloudlet;
	private String result;
	private CloudletTransmitter cloudletTransmitterAbstract;
	RabbitMQUtils rabbitMQUtils = new RabbitMQUtils();

	private String requestQueueName;
	private String replyQueueName;
	private QueueingConsumer consumer;
	private String type;
	private String zipFileName;

	public TransmissionClient(String type) {
		signalToAllProcess = null;
		signalToServersNumber = null;
		this.type = type; // recognition or scanning
	}

	public TransmissionClient(String zipFileName, String nameServer,
			CloudletTransmitter cloudletTransmitterAbstract,
			CountDownLatch signalToAllProcess, CountDownLatch signalToServersNumber, String type)
			throws IOException, InterruptedException {
		super();
		this.zipFileName = zipFileName;
		this.type = type;
		this.ipServer = nameServer + "-" + type;
		this.requestQueueName = nameServer + "-" + type;
		this.tcpClientCloudlet = new CloudletUtils();
		this.cloudletTransmitterAbstract = cloudletTransmitterAbstract;
		this.signalToAllProcess = signalToAllProcess;
		this.signalToServersNumber = signalToServersNumber;

		rabbitMQUtils.connect();

		replyQueueName = rabbitMQUtils.getChannel().queueDeclare().getQueue();
		consumer = new QueueingConsumer(rabbitMQUtils.getChannel());
		rabbitMQUtils.getChannel().basicConsume(replyQueueName, true, consumer);
	}

	public String getResult() {
		return this.result;
	}

	public void sendFileToProcessRemotely() throws IOException,
			ShutdownSignalException, ConsumerCancelledException,
			InterruptedException {
		String response = null;
		String corrId = java.util.UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties.Builder()
				.correlationId(corrId).replyTo(replyQueueName).build();

		byte[] zipFileInBytes = CloudletUtils.fileToBytes(this.zipFileName);

		rabbitMQUtils.getChannel().basicPublish("", requestQueueName, props,
				zipFileInBytes);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = new String(delivery.getBody());
				break;
			}
		}
		this.result += response;
	}

	public void run() {
		try {
			signalToAllProcess.await();
			sendFileToProcessRemotely();
			signalToServersNumber.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
