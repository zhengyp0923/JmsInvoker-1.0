package cn.zyp.rpc;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.*;

public class JmsInvokerClientInterceptor implements MethodInterceptor, InitializingBean {

    private String destination;

    private ActiveMQConnectionFactory connectionFactory;
    private static final String RESPONSE = "response";

    public JmsInvokerClientInterceptor() {
    }

    public JmsInvokerClientInterceptor(String destination, ActiveMQConnectionFactory connectionFactory) {
        this.destination = destination;
        this.connectionFactory = connectionFactory;
    }


    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //创建session
        Connection connection = connectionFactory.createConnection();
        connection.start();//start()  坑爹

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(destination);
        Queue resp = session.createQueue(destination + RESPONSE);

        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(resp);
        try {
            //封装请求参数
            JmsInvocation jmsInvocation = new JmsInvocation(methodInvocation.getMethod().getName(), methodInvocation.getMethod().getParameterTypes(), methodInvocation.getArguments(), resp.getQueueName());
            ActiveMQObjectMessage activeMQObjectMessage = new ActiveMQObjectMessage();
            activeMQObjectMessage.setObject(jmsInvocation);

            //发送请求
            producer.send(activeMQObjectMessage);

            //接收请求
            ActiveMQObjectMessage receive = (ActiveMQObjectMessage) consumer.receive();
            JmsResult result = (JmsResult) receive.getObject();
            return result.getResult();
        } finally {
            producer.close();
            consumer.close();
        }
    }

    public void afterPropertiesSet() throws Exception {

    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public ActiveMQConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
