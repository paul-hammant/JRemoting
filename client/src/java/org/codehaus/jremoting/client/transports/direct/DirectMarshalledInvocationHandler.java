/* ====================================================================
 * Copyright 2005-2006 JRemoting Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.codehaus.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.codehaus.jremoting.client.transports.direct;

import org.codehaus.jremoting.JRemotingException;
import org.codehaus.jremoting.SerializationHelper;

import java.util.concurrent.ExecutorService;
import org.codehaus.jremoting.client.ClientMonitor;
import org.codehaus.jremoting.client.ConnectionPinger;
import org.codehaus.jremoting.requests.AbstractRequest;
import org.codehaus.jremoting.responses.AbstractResponse;
import org.codehaus.jremoting.server.ServerMarshalledInvocationHandler;

/**
 * Class DirectInvocationHandler
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class DirectMarshalledInvocationHandler extends AbstractDirectInvocationHandler {

    private ServerMarshalledInvocationHandler invocationHandler;
    private ClassLoader interfacesClassLoader;


    /**
     * Constructor DirectInvocationHandler
     *
     * @param executor
     * @param clientMonitor
     * @param connectionPinger
     * @param invocationHandler
     */
    public DirectMarshalledInvocationHandler(ExecutorService executor, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ServerMarshalledInvocationHandler invocationHandler, ClassLoader classLoader) {
        super(executor, clientMonitor, connectionPinger);
        this.invocationHandler = invocationHandler;
        interfacesClassLoader = classLoader;
    }

    protected AbstractResponse performInvocation(AbstractRequest request) {

        try {
            byte[] serRequest = SerializationHelper.getBytesFromInstance(request);
            byte[] serResponse = invocationHandler.handleInvocation(serRequest, null);

            Object instanceFromBytes = SerializationHelper.getInstanceFromBytes(serResponse, interfacesClassLoader);
            return (AbstractResponse) instanceFromBytes;
        } catch (ClassNotFoundException cnfe) {
            String msg = "Some ClassNotFoundException on client side";
            clientMonitor.classNotFound(DirectMarshalledInvocationHandler.class, msg, cnfe);
            throw new JRemotingException(msg, cnfe);
        }
    }

    public ClassLoader getInterfacesClassLoader() {
        return interfacesClassLoader;
    }

}
