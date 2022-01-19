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

import com.sie.infor.message.KeepAliveMessage;
import com.sie.infor.message.SocketMessage;
import com.sie.util.LogUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * 类描述
 *
 * @author hxy
 */
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

    public boolean isRequest(IoSession session, Object message) {
        LogUtils.DT("isRequest：" + message.toString());
        if (message instanceof SocketMessage) {
            if(((SocketMessage) message).getType() == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isResponse(IoSession session, Object message) {
        LogUtils.DT("isResponse：" + message.toString());
        if (message instanceof SocketMessage) {
            if(((SocketMessage) message).getType() == -1) {
                return true;
            }
        }
        return false;
    }

    public Object getRequest(IoSession session) {
        LogUtils.DT("getRequest：" + session + "发送心跳");
        KeepAliveMessage keepAliveMessage = new KeepAliveMessage();
        return keepAliveMessage;
    }

    public Object getResponse(IoSession session, Object request) {
        return null;
    }

}