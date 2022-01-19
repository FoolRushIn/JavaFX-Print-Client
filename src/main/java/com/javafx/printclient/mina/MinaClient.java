/**
 * Copyright (c) 2019-2099, Raptor
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javafx.printclient.mina;

import com.javafx.printclient.common.Constant;
import com.javafx.printclient.mina.handler.ClientHandler;
import com.sie.infor.message.ClientNotifyServerMessage;
import com.sie.infor.message.FileTask;
import com.sie.infor.mina.BaseCodecFactory;
import com.sie.util.LogUtils;
import lombok.Data;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 类描述
 *
 * @author hxy
 */
@Data
public class MinaClient {

    private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    private String ip;

    private int port;

    private Map<Byte, FileTask> fileTasks;

    NioSocketConnector connector = null;
    ConnectFuture connectFuture = null;
    IoSession session = null;
    ConnectFuture future = null;

    private boolean isConnecting;

    public MinaClient(String ip, int port, Map<Byte, FileTask> fileTasks){
        this.ip = ip;

        this.port = port;
        this.fileTasks = fileTasks;
    }

    public void initConnector() {
        if(isConnecting || connector != null) {
            return;
        }

        connector = new NioSocketConnector(Runtime
                .getRuntime().availableProcessors() + 1);
        connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE,
                Constant.READ_IDLE_TIMEOUT);
        connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE,
                Constant.WRITE_IDLE_TIMEOUT);
        connector.getSessionConfig().setWriteTimeout(2000);
        connector.getSessionConfig().setTcpNoDelay(true);

        connector.getFilterChain().addLast("BaseFilter",
                new ProtocolCodecFilter(new BaseCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        // 设置连接超时检查时间
        connector.getFilterChain().addLast(
                "threadpool",
                new ExecutorFilter(Executors.newFixedThreadPool(Runtime
                        .getRuntime().availableProcessors() + 1)));

        /** 心跳 **/
        connector.getFilterChain().addLast("heart",  getKeepAliveSetting ()); // 说明：该过滤器加入到整个通信的过滤链中。

        connector.setConnectTimeoutCheckInterval(5000);
        connector.setConnectTimeoutMillis(60000); // 60秒后超时
        connector.setHandler(new ClientHandler());

        // 设置默认访问地址
        connector.setDefaultRemoteAddress(new InetSocketAddress(this.getIp(), this.getPort()));

        //断线重连回调拦截器
        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
            @Override
            public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession ioSession) throws Exception {
                //持续连接
                LogUtils.DT("触发重连");
                executeConnect();
            }
        });

        //持续连接
        executeConnect();
    }

    /**
     * 执行mina连接
     *
     * @param
     * @return void
     */
    private void executeConnect() {
        //单独开启一个线程持续去连接
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(() -> {
            continuedConnect();
            LogUtils.DT("销毁持续连接线程池fixedThreadPool");
            fixedThreadPool.shutdown();
        });
    }

    private void continuedConnect() {
        //开始连接
        for (;;) {
            try {
                LogUtils.DT("开始连接" + this.getIp() + ":" +this.getPort() + "connector：" +connector);
                if(connector == null) {
                    break;
                }
                future = connector.connect();
                // 等待连接创建成功
                future.awaitUninterruptibly();
                // 获取会话
                session = future.getSession();

                if(session.isConnected()){

                    //添加监听器，客户端定时请求
                    registerClientRequest();
                    isConnecting = true;
                    LogUtils.DT("连接服务端" + this.getIp() + ":" +this.getPort() + "[成功]" +",isConnecting:" +isConnecting);
                    break;
                }
            } catch (Exception e) {
                LogUtils.DT("连接服务端" + this.getIp() + ":" + this.getPort() +
                        "失败, 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:" + e.getMessage() + ",isConnecting:" +isConnecting);
                // 连接失败后,重连10次,间隔30s
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    LogUtils.DT("连接服务端失败后，睡眠5秒发生异常！");
                }
            }
        }
    }

    private KeepAliveFilter getKeepAliveSetting () {
        /** 检测每一个连接的IoSession的心跳包，定时进入Idle状态，用一下方法，以上备注不可行 **/
        KeepAliveMessageFactoryImpl ckafi = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter kaf = new KeepAliveFilter(ckafi, IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
        kaf.setForwardEvent(true); // 说明：继续调用 IoHandlerAdapter 中的 sessionIdle时间
        kaf.setRequestInterval(10); // 说明：设置当连接的读取通道空闲的时候，心跳包请求时间间隔，设置心跳频率，单位是秒
        kaf.setRequestTimeout(60);// 说明：设置心跳包请求后 等待反馈超时时间。// 超过该时间后则调用KeepAliveRequestTimeoutHandler.CLOSE
        return kaf;
    }

    private void registerClientRequest () {
        future.addListener(new IoFutureListener<ConnectFuture>() {
            @Override
            public void operationComplete(final ConnectFuture ioFuture) {
                if (ioFuture.isDone() && ioFuture.isConnected()) {

                    getFileTasks().forEach((type, fileTask) -> {
                        scheduledThreadPool.scheduleAtFixedRate((() -> {

                            ClientNotifyServerMessage clientNotifyServerMessage =
                                    new ClientNotifyServerMessage(type, fileTask);

                            ioFuture.getSession().write(clientNotifyServerMessage);

                        }) , 1, fileTask.period, fileTask.unit);

                    });

                }
            }
        });
    }


    /**
     * 关闭连接，根据传入的参数设置session是否需要重新连接
     */
    public synchronized void closeConnect() {

        //单独开启一个线程持续去连接
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(() -> {
            if (connector != null && !connector.isDisposed()) {
                connector.dispose();
                LogUtils.DT("关闭connector:"+ connector.isDisposed());
                connector = null;
            }
            if (session != null) {
                session.closeOnFlush();
                session.getService().dispose();
                LogUtils.DT("关闭session:"+session.isClosing());
                session = null;
            }
            if (future != null && future.isConnected()) {
                future.cancel();
                future = null;
            }
            isConnecting = false;
            fixedThreadPool.shutdown();
        });
    }


}