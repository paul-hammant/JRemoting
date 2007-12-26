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
package org.codehaus.jremoting.client.transports.piped;

import org.codehaus.jremoting.ConnectionException;
import org.codehaus.jremoting.responses.ConnectionOpened;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jremoting.client.ClientMonitor;
import org.codehaus.jremoting.client.ConnectionPinger;
import org.codehaus.jremoting.client.InvocationException;
import org.codehaus.jremoting.client.pingers.NeverConnectionPinger;
import org.codehaus.jremoting.client.transports.StreamClientInvoker;
import org.codehaus.jremoting.client.transports.ClientStreamDriverFactory;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Class PipedClientStreamInvoker
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class PipedClientStreamInvoker extends StreamClientInvoker {

    private PipedInputStream inputStream;
    private PipedOutputStream outputStream;

    /**
     * Constructor PipedClientStreamInvoker
     *
     * @param clientMonitor
     * @param executorService
     * @param connectionPinger
     * @param facadesClassLoader
     * @param is
     * @param os
     */
    public PipedClientStreamInvoker(ClientMonitor clientMonitor, ScheduledExecutorService executorService,
                                        ConnectionPinger connectionPinger, ClassLoader facadesClassLoader, ClientStreamDriverFactory clientStreamDriverFactory, PipedInputStream is,
                                        PipedOutputStream os) {

        super(clientMonitor, executorService, connectionPinger, facadesClassLoader, clientStreamDriverFactory);

        inputStream = is;
        outputStream = os;
    }


    public PipedClientStreamInvoker(ClientMonitor clientMonitor,
                                        ClientStreamDriverFactory streamDriverFactory,
                                        PipedInputStream inputStream, PipedOutputStream outputStream) {
        this(clientMonitor, Executors.newScheduledThreadPool(10), new NeverConnectionPinger(),
                Thread.currentThread().getContextClassLoader(), streamDriverFactory, inputStream, outputStream);
    }

    /**
     * Method openConnection
     *
     * @throws ConnectionException
     */
    public ConnectionOpened openConnection() throws ConnectionException {
        setObjectDriver(streamDriverFactory.makeDriver(inputStream, outputStream, getFacadesClassLoader()));
        return super.openConnection();
    }

    protected boolean tryReconnect() {

        // blimey how do we reconnect this?
        throw new InvocationException("Piped connection broken, unable to reconnect.");
    }

}