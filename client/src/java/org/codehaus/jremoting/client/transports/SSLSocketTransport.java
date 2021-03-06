/* ====================================================================
 * Copyright 2005-2006 JRemoting Committers
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
import org.codehaus.jremoting.client.*;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;

public class SSLSocketTransport extends SocketTransport {

    public SSLSocketTransport(ClientMonitor clientMonitor, ScheduledExecutorService executorService, ConnectionPinger connectionPinger,
                              ClassLoader facadesClassLoader, Stream Stream, SocketDetails addr) throws ConnectionException {
        super(clientMonitor, executorService, connectionPinger, facadesClassLoader, Stream, addr);
    }

    public SSLSocketTransport(ClientMonitor clientMonitor, Stream Stream, SocketDetails addr) throws ConnectionRefusedException, ConnectionException {
        super(clientMonitor, Stream, addr);
    }

    /**
     * {@inheritDoc}
     */
    protected Socket makeSocket(SocketDetails addr) throws IOException {
        return SSLSocketFactory.getDefault().createSocket(addr.getHostName(), addr.getPort());
    }
}
