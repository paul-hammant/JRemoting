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
package org.codehaus.jremoting.itests.mismatch;

import org.codehaus.jremoting.ConnectionException;
import org.codehaus.jremoting.client.streams.ByteStream;
import org.codehaus.jremoting.client.streams.ObjectStream;
import org.codehaus.jremoting.client.resolver.ServiceResolver;
import org.codehaus.jremoting.client.monitors.ConsoleClientMonitor;
import org.codehaus.jremoting.client.transports.RmiTransport;
import org.codehaus.jremoting.client.transports.SocketTransport;
import org.codehaus.jremoting.client.SocketDetails;
import org.codehaus.jremoting.itests.TestFacade;
import org.codehaus.jremoting.itests.TestFacade2;
import org.codehaus.jremoting.itests.TestFacade3;
import org.codehaus.jremoting.itests.TestFacadeImpl;
import org.codehaus.jremoting.server.Publication;
import org.codehaus.jremoting.server.ServerMonitor;
import org.codehaus.jremoting.server.transports.RmiServer;
import org.codehaus.jremoting.server.transports.SocketServer;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.net.InetSocketAddress;

/**
 * Test Custom Stream over sockets.
 *
 * @author Paul Hammant
 */
public class SocketMismatchTestCase extends MockObjectTestCase {
    private Class x_class;
    private String x_msg;
    private ConnectionException x_bce;
    private Mock mockServerMonitor;

    protected void setUp() throws Exception {
        super.setUp();
        mockServerMonitor = mock(ServerMonitor.class);
    }

    public void testByteStreamObjectStreamMismatchCanCauseTimeOut() throws Exception {

        SocketServer server = new SocketServer((ServerMonitor) mockServerMonitor.proxy(), new InetSocketAddress(12001));

        TestFacadeImpl testServer = new TestFacadeImpl();
        Publication pd = new Publication(TestFacade.class).addAdditionalFacades(TestFacade3.class, TestFacade2.class);
        server.publish(testServer, "Hello", pd);
        server.start();

        ServiceResolver sr = null;
        TestFacade testClient;
        try {

            // Client side setup
            sr = new ServiceResolver(new SocketTransport(new ConsoleClientMonitor(),
                new ObjectStream(), new SocketDetails("127.0.0.1", 12001)));
            testClient = sr.resolveService("Hello");

            testClient.hello("hello");


            fail("ByteStreams and ObjectStreams cannot interoperate");
        } catch (ConnectionException bce) {

            // expected

        } finally {

            testClient = null;
            System.gc();

            try {
                sr.close();
            } catch (Exception e) {
            }

            server.stop();


        }
    }

    public void dont_testObjectStreamByteStreamMismatch() throws Exception {

        // server side setup.
        SocketServer server = new SocketServer((ServerMonitor) mockServerMonitor.proxy(), new org.codehaus.jremoting.server.streams.ObjectStream(), new InetSocketAddress(12002));
        TestFacadeImpl testServer = new TestFacadeImpl();
        Publication pd = new Publication(TestFacade.class).addAdditionalFacades(TestFacade3.class, TestFacade2.class);
        server.publish(testServer, "Hello", pd);
        server.start();

        ServiceResolver sr = null;
        try {

            // Client side setup
            sr = new ServiceResolver(new SocketTransport(new ConsoleClientMonitor(),
                new ByteStream(), new SocketDetails("127.0.0.1", 12002)));
            TestFacade testClient = sr.resolveService("Hello");


            testClient.hello("hello");
            fail("ByteStreams and ObjectStreams cannot interoperate");
        } catch (ConnectionException bce) {
            // expected.
        } finally {

            System.gc();


            try {
                sr.close();
            } catch (Exception e) {
            }

            server.stop();

        }
    }


    public void dont_testByteStreamRmiMismatch() throws Exception {

        // server side setup.
        SocketServer server = new SocketServer((ServerMonitor) mockServerMonitor.proxy(), new InetSocketAddress(12003));
        TestFacadeImpl testServer = new TestFacadeImpl();
        Publication pd = new Publication(TestFacade.class).addAdditionalFacades(TestFacade3.class, TestFacade2.class);
        server.publish(testServer, "Hello", pd);
        server.start();

        ServiceResolver sr = null;
        try {

            // Client side setup
            sr = new ServiceResolver(new RmiTransport(new ConsoleClientMonitor(), new SocketDetails("127.0.0.1", 12003)));
            TestFacade testClient = sr.resolveService("Hello");


            testClient.hello("hello");
            fail("ByteStreams and RMI trasnports cannot interoperate");
        } catch (ConnectionException bce) {
            // expected.
        } finally {

            System.gc();


            try {
                sr.close();
            } catch (Exception e) {
            }

            server.stop();

        }
    }

    public void dont_testRmiByteStreamMismatch() throws Exception {

        // server side setup.
        RmiServer server = new RmiServer((ServerMonitor) mockServerMonitor.proxy(), new InetSocketAddress("127.0.0.1", 12004));
        TestFacadeImpl testServer = new TestFacadeImpl();
        Publication pd = new Publication(TestFacade.class).addAdditionalFacades(TestFacade3.class, TestFacade2.class);
        server.publish(testServer, "Hello", pd);
        server.start();

        ServiceResolver sr = null;
        try {

            // Client side setup
            sr = new ServiceResolver(new SocketTransport(new ConsoleClientMonitor(),
                new ObjectStream(), new SocketDetails("127.0.0.1", 12004)));
            TestFacade testClient = sr.resolveService("Hello");

            testClient.hello("hello");
            fail("ByteStreams and RMI trasnports cannot interoperate");
        } catch (ConnectionException bce) {
            // expected.
        } finally {

            System.gc();

            try {
                sr.close();
            } catch (Exception e) {
            }

            server.stop();

        }
    }

}
