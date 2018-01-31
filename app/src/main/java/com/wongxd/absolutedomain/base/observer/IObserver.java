package com.wongxd.absolutedomain.base.observer;

/**
 * 消息订阅者（观察者）
 * @see <a>http://blog.csdn.net/chengliang0315/article/details/53381539</a>
 */
public interface IObserver {

    /**
     * 当消息发布者发布消息的时候调用该方法
     * @param observable  消息发布者
     * @param msg  发布的消息内容
     * @param flag 消息的类型标记
     */
    void onMessageReceived(IObservable observable, Object msg, int flag);

}
