package cn.zyp.rpc;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.*;
import java.lang.reflect.Method;

public class JmsInvokerServiceExporter implements SessionAwareMessageListener ,InitializingBean{

    private Object target;
    private Class serviceInterface;

    private static final String RESPONSE="response";

    public JmsInvokerServiceExporter() {
    }

    public JmsInvokerServiceExporter(Object target) {
        this.target = target;
        this.serviceInterface = target.getClass().getInterfaces()[0];
    }

    public void afterPropertiesSet() throws Exception {
        this.serviceInterface = target.getClass().getInterfaces()[0];
    }
    public void onMessage(Message message, Session session) throws JMSException {
        //序列化JmsInvocation
        ActiveMQObjectMessage activeMQMessage = (ActiveMQObjectMessage) message;
        JmsInvocation jmsInvocation = (JmsInvocation) activeMQMessage.getObject();
        System.out.println("jmsInvocation"+jmsInvocation.getMethodName()+" tempQueue:"+jmsInvocation.getQueue());

        //临时队列
        String queue = jmsInvocation.getQueue();
        Queue temporyQueue = session.createQueue(queue);
        System.out.println("temporyQueue  "+temporyQueue.getQueueName());

        JmsResult jmsResult = null;
        try {
            //反射调用方法
            Method method = target.getClass().getMethod(jmsInvocation.getMethodName(), jmsInvocation.getParameterTypes());

            Object invoke = method.invoke(target, jmsInvocation.getArguments());
            System.out.println("invoker"+invoke);
            jmsResult = new JmsResult(invoke);
            System.out.println("jsmResult"+jmsResult);
        } catch (Exception e) {
            e.printStackTrace();
            jmsResult.setTx(e);
        }

        //创建MessageProducer ActiveMQObjectMessage
        MessageProducer producer = session.createProducer(temporyQueue);
        try {
            ActiveMQObjectMessage activeMQObjectMessage = new ActiveMQObjectMessage();
            activeMQObjectMessage.setObject(jmsResult);

            //发送请求
            producer.send(activeMQObjectMessage);
        } finally {
            producer.close();
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class serviceInterface) {
        this.serviceInterface = serviceInterface;
    }


}
