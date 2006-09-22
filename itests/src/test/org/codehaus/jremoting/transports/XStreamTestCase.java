package org.codehaus.jremoting.transports;

import org.codehaus.jremoting.test.*;
import org.codehaus.jremoting.server.transports.socket.SelfContainedSocketXStreamServer;
import org.codehaus.jremoting.server.transports.DefaultServerSideClientContextFactory;
import org.codehaus.jremoting.server.*;
import org.codehaus.jremoting.server.monitors.NullServerMonitor;
import org.codehaus.jremoting.server.monitors.ConsoleServerMonitor;
import org.codehaus.jremoting.server.authenticators.DefaultAuthenticator;
import org.codehaus.jremoting.server.classretrievers.NoStubRetriever;
import org.codehaus.jremoting.client.factories.ClientSideStubFactory;
import org.codehaus.jremoting.client.transports.socket.SocketXStreamHostContext;

import java.util.concurrent.Executors;

/**
 * Test XStream over sockets.
 *
 * @author Paul Hammant
 */
public class XStreamTestCase extends AbstractHelloTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        server = new SelfContainedSocketXStreamServer(new NoStubRetriever(), new DefaultAuthenticator(), new ConsoleServerMonitor(),
                Executors.newCachedThreadPool(), new DefaultServerSideClientContextFactory(), 10099);
        testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        factory = new ClientSideStubFactory(new SocketXStreamHostContext("127.0.0.1", 10099), false);
        testClient = (TestInterface) factory.lookupService("Hello");

    }

    protected void tearDown() throws Exception {

        testClient = null;

        System.gc();


        Thread.sleep(2000);

        factory.close();


        Thread.sleep(1000);

        server.stop();
        server = null;
        testServer = null;
        super.tearDown();
    }


}