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
package org.codehaus.jremoting.client.transports;

import org.codehaus.jremoting.ConnectionException;
import org.codehaus.jremoting.JRemotingException;
import org.codehaus.jremoting.client.ClientMonitor;
import org.codehaus.jremoting.client.ConnectionClosedException;
import org.codehaus.jremoting.client.ConnectionPinger;
import org.codehaus.jremoting.client.InvocationException;
import org.codehaus.jremoting.client.Transport;
import org.codehaus.jremoting.requests.CloseConnection;
import org.codehaus.jremoting.requests.InvokeMethod;
import org.codehaus.jremoting.requests.OpenConnection;
import org.codehaus.jremoting.requests.Ping;
import org.codehaus.jremoting.requests.Request;
import org.codehaus.jremoting.responses.ConnectionClosed;
import org.codehaus.jremoting.responses.ConnectionOpened;
import org.codehaus.jremoting.responses.ProblemResponse;
import org.codehaus.jremoting.responses.Response;
import org.codehaus.jremoting.responses.TryLater;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class StatefulTransport
 *
 * @author Paul Hammant
 * @version $Revision: 1.3 $
 */
public abstract class StatefulTransport implements Transport {

    protected final ConnectionPinger connectionPinger;
    protected final ClientMonitor clientMonitor;
    private final ClassLoader facadesClassLoader;
    protected final ScheduledExecutorService executorService;
    protected final boolean methodLogging;
    private long session;
    private long lastRealRequest = System.currentTimeMillis();
    private boolean open;

    public StatefulTransport(ClientMonitor clientMonitor, ScheduledExecutorService executorService,
                                           ConnectionPinger connectionPinger, ClassLoader facadesClassLoader) {
        this.executorService = executorService;
        this.clientMonitor = clientMonitor;
        this.facadesClassLoader = facadesClassLoader;
        methodLogging = clientMonitor.methodLogging();
        this.connectionPinger = connectionPinger;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return executorService;
    }

    public ClientMonitor getClientMonitor() {
        return clientMonitor;
    }

    public ConnectionOpened openConnection() throws ConnectionException {
        synchronized(this) {
            if (open) {
                throw new IllegalStateException("already open");
            }
            open = true;
        }
        Response resp = invoke(new OpenConnection(), true);
        connectionPinger.start(this);
        if (!(resp instanceof ConnectionOpened)) {
            throw new ConnectionException("Setting of host context blocked for reasons of unknown, server-side response: (" + resp.getClass().getName() + ")");
        }
        return (ConnectionOpened) resp;
    }

    public void closeConnection(long session) {
        synchronized(this) {
            if (!open) {
                throw new IllegalStateException("not open");
            }
            open = false;
        }
        ConnectionClosed closed = (ConnectionClosed) invoke(new CloseConnection(session), true);
        connectionPinger.stop();
    }

    public void ping() {

        if (!open) {
            throw new ConnectionClosedException("Connection closed");
        }

        try {
            invoke(new Ping(), true);
        } catch (JRemotingException e) {
            clientMonitor.pingFailure(this.getClass(), e);
        }
    }

    protected abstract boolean tryReconnect();

    public ClassLoader getFacadesClassLoader() {
        return facadesClassLoader;
    }


    /**
     * resolveArgument can handle any changes that one has to  do to the arguments being
     * marshalled to the server.
     * Noop Default behaviour.
     *
     * @param remoteObjName
     * @param objClass
     * @param obj
     * @return Object
     */

    public Object resolveArgument(String remoteObjName, String methodSignature, Class objClass, Object obj) {
        return obj;
    }

    public Response invoke(Request request, boolean retry) {
        if (request instanceof Ping) {
            ((Ping) request).setSession(session);
        } else {
            lastRealRequest = System.currentTimeMillis();
        }

        try {
            while (true) {
                boolean again = true;
                Response response = null;
                int tries = 0;
                long start = 0;

                if (methodLogging) {
                    start = System.currentTimeMillis();
                }

                while (again) {
                    tries++;
                    again = false;
                    try {
                        response = performInvocation(request);
                        again = retryIfProblemResponse(request, again, response, tries);
                        if (response instanceof ConnectionOpened) {
                            session = ((ConnectionOpened) response).getSessionID();
                        }
                    } catch (IOException ioe) {
                        again = retryOrThrowAfterIoException(request, retry, ioe);
                    }
                }
                if (methodLogging) {
                    if (request instanceof InvokeMethod) {
                        clientMonitor.methodCalled(this.getClass(), ((InvokeMethod) request).getMethodSignature(), System.currentTimeMillis() - start, "");
                    }
                }

                return response;
            }
        } catch (ClassNotFoundException e) {
            throw new InvocationException("Class definition missing on Deserialization: " + e.getMessage(), e);
        }
    }

    private boolean retryIfProblemResponse(Request request, boolean again, Response response, int tries) {
        if (response instanceof ProblemResponse) {
            if (response instanceof TryLater) {
                int millis = ((TryLater) response).getSuggestedDelayMillis();
                clientMonitor.serviceSuspended(this.getClass(), request, tries, millis);
                again = true;
            }
        }
        return again;
    }

    private boolean retryOrThrowAfterIoException(Request request, boolean retry, IOException ioe) {
        boolean again;
        if (isSafeEnd(ioe) && !(ioe instanceof SocketTimeoutException && request instanceof OpenConnection)) {
            int retryConnectTries = 0;

            again = true;

            while (retry && !tryReconnect()) {
                clientMonitor.serviceAbend(this.getClass(), retryConnectTries, ioe);

                retryConnectTries++;
            }
        } else {
            clientMonitor.unexpectedIOException(StatefulTransport.class, "invoke(), request:'" + request.getClass().getName() + "'", ioe);
            throw new InvocationException("unexpected IOException", ioe);
        }
        return again;
    }

    protected abstract Response performInvocation(Request request) throws IOException, ClassNotFoundException;

    private boolean isSafeEnd(IOException ioe) {
        if (ioe instanceof SocketException | ioe instanceof EOFException | ioe instanceof InterruptedIOException) {
            return true;
        }
        if (ioe.getMessage() != null) {
            String msg = ioe.getMessage();
            if (msg.equals("Read end dead") | msg.equals("Pipe closed")) {
                return true;
            }
        }
        return false;
    }

    public long getLastRealRequestTime() {
        return lastRealRequest;
    }


}
