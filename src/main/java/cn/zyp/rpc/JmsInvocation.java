package cn.zyp.rpc;

import java.io.Serializable;

public class JmsInvocation implements Serializable{
    private String methodName;
    private Class[] parameterTypes;
    private Object[] arguments;
    private String   queue;

    public JmsInvocation() {
    }

    public JmsInvocation(String methodName, Class[] parameterTypes, Object[] arguments, String queue) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
        this.queue = queue;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
