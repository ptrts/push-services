package io.fourfinanceit.push;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jms")
public class JmsProperties {

    private SendReceive send = new SendReceive();

    private SendReceive receive = new SendReceive();

    private Broker broker = new Broker();

    private Queue queue = new Queue();

    public Broker getBroker() {
        return broker;
    }

    public Queue getQueue() {
        return queue;
    }

    public SendReceive getSend() {
        return send;
    }

    public SendReceive getReceive() {
        return receive;
    }

    public static class SendReceive {

        private boolean enable = false;

        private SessionFactory sessionFactory = new SessionFactory();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public SessionFactory getSessionFactory() {
            return sessionFactory;
        }
    }

    public static class Broker {

        private String url;

        private String user;

        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class SessionFactory {

        private int cacheSize = 20;

        public int getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
        }
    }

    public static class Queue {

        private String name = "push-services";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
