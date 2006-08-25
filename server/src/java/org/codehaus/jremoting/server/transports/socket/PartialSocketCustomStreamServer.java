/* ====================================================================
 * Copyright 2005-2006 JRemoting Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.codehaus.jremoting.server.transports.socket;

import org.codehaus.jremoting.api.ThreadPool;
import org.codehaus.jremoting.server.ServerMonitor;
import org.codehaus.jremoting.server.ServerSideClientContextFactory;
import org.codehaus.jremoting.server.adapters.InvocationHandlerAdapter;
import org.codehaus.jremoting.server.transports.AbstractServerStreamDriver;
import org.codehaus.jremoting.server.transports.ServerCustomStreamDriver;

/**
 * Class PartialSocketCustomStreamServer
 *
 * @author Paul Hammant
 * @author Peter Royal
 * @version $Revision: 1.3 $
 */
public class PartialSocketCustomStreamServer extends AbstractPartialSocketStreamServer {

    /**
     * Construct a PartialSocketCustomStreamServer
     *
     * @param invocationHandlerAdapter the handler
     * @param serverMonitor            the monitor
     */
    public PartialSocketCustomStreamServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
    }


    /**
     * Stop
     */
    public void stop() {
        super.stop();
    }

    /**
     * Create a Server Stream Read Writer.
     *
     * @return The Server Stream Read Writer.
     */
    protected AbstractServerStreamDriver createServerStreamReadWriter() {
        return new ServerCustomStreamDriver(serverMonitor, threadPool);
    }

}
