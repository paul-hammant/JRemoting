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
package org.codehaus.jremoting.client.transports.socket;

import org.codehaus.jremoting.ConnectionException;
import org.codehaus.jremoting.client.ClientMonitor;
import org.codehaus.jremoting.client.ConnectionPinger;
import org.codehaus.jremoting.client.ConnectionRefusedException;
import org.codehaus.jremoting.client.StreamEncoding;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;

public class SSLSocketTransport extends SocketTransport {

    public SSLSocketTransport(ClientMonitor clientMonitor, ScheduledExecutorService executorService, ConnectionPinger connectionPinger, ClassLoader facadesClassLoader, StreamEncoding streamEncoding, InetSocketAddress addr) throws ConnectionException {
        super(clientMonitor, executorService, connectionPinger, facadesClassLoader, streamEncoding, addr);
    }

    public SSLSocketTransport(ClientMonitor clientMonitor, ScheduledExecutorService executorService, ConnectionPinger connectionPinger, ClassLoader facadesClassLoader, StreamEncoding streamEncoding, InetSocketAddress addr, int socketTimeout) throws ConnectionRefusedException, ConnectionException {
        super(clientMonitor, executorService, connectionPinger, facadesClassLoader, streamEncoding, addr, socketTimeout);
    }

    public SSLSocketTransport(ClientMonitor clientMonitor, StreamEncoding streamEncoding, InetSocketAddress addr) throws ConnectionRefusedException, ConnectionException {
        super(clientMonitor, streamEncoding, addr);
    }

    public SSLSocketTransport(ClientMonitor clientMonitor, StreamEncoding streamEncoding, InetSocketAddress addr, int socketTimeout) throws ConnectionRefusedException, ConnectionException {
        super(clientMonitor, streamEncoding, addr, socketTimeout);
    }

    protected Socket makeSocket(InetSocketAddress addr) throws IOException {
        return SSLSocketFactory.getDefault().createSocket(addr.getHostName(), addr.getPort());
    }
}
