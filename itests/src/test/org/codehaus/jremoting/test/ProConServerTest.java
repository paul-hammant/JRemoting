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
package org.codehaus.jremoting.test;

import org.codehaus.jremoting.server.transports.ConnectingServer;
import org.codehaus.jremoting.server.transports.socket.SelfContainedSocketStreamServer;
import org.codehaus.jremoting.server.monitors.ConsoleServerMonitor;

/**
 * Class ProConServerTest
 *
 * @author Vinay Chandrasekharan
 */
public class ProConServerTest {

    //TODO - redo

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Stream over Socket Server");

        ConnectingServer as;

        if (args[1].equals("ObjectStream")) {
            System.out.println("(Object Stream)");

            as = new SelfContainedSocketStreamServer(new ConsoleServerMonitor(), 1234, SelfContainedSocketStreamServer.OBJECTSTREAM);
        } else {

            // CustomStream
            System.out.println("(Custom Stream)");

            //as = new SelfContainedSocketCustomStreamServer(1235);
        }

        if (args[0].equals("S")) {
            //as = new SelfContainedSocketCustomStreamServer(new JarFileStubRetriever("build/classes2"), new NullAuthenticator(), new NullServerMonitor(), Executors.newCachedThreadPool(), new DefaultServerSideClientContextFactory(), 1235);
        }

        //provider
        TestProvider tpi = new TestProviderImpl();

        //as.publish(tpi, "P", TestProvider.class);

        //consumer
        TestConsumer tci = new TestConsumerImpl();

        //as.publish(tci, "C", TestConsumer.class);

        //start
        //as.start();
    }
}
