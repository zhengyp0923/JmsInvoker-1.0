package cn.zyp.rpc;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

public class JmsInvokerProxyFactoryBean extends JmsInvokerClientInterceptor implements FactoryBean<Object> {

    private Object serviceProxy;
    //接口
    private Class<?> serviceInterface;

    public JmsInvokerProxyFactoryBean() {
    }

    public JmsInvokerProxyFactoryBean(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.serviceProxy = new ProxyFactory(serviceInterface, this).getProxy();
    }

    public Object getObject() throws Exception {
        return serviceProxy;
    }

    public Class<?> getObjectType() {
        return null;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}
