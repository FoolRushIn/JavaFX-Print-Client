package com.javafx.printclient.mina.handler;

import com.sie.infor.message.SocketMessage;
import com.sie.infor.mina.handler.SocketMessageDispatcher;
import com.sie.util.LogUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类描述
 *
 * @author hxy
 */
public class ClientHandler extends IoHandlerAdapter {
    static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    final static SocketMessageDispatcher socketMessageDispatcher = new SocketMessageDispatcher();

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        LogUtils.DT("session:" + session.getId() + " 已关闭");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionOpened(session);
        LogUtils.DT("session:" + session.getId() + " 已建立");
    }

    @Override
    public void messageSent(IoSession session, Object message)
            throws Exception {
        super.messageSent(session, message);
        LogUtils.DT("session:" + session.getId() + " messageSent："+message);
    }

    @Override
    public void messageReceived(final IoSession session,
                                final Object message) throws Exception {
        super.messageReceived(session, message);
        socketMessageDispatcher.handleMessage(session,
                (SocketMessage) message);
        LogUtils.DT("session:" + session.getId() + " messageReceived："+message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        LogUtils.DT("session:" + session.getId() + " exceptionCaught："+cause);
        super.exceptionCaught(session, cause);
        cause.printStackTrace();
        session.closeNow();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        LogUtils.DT("sessionIdleClient------>"+session);
    }
}
