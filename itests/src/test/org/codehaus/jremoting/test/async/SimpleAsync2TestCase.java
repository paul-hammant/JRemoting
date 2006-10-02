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
package org.codehaus.jremoting.test.async;

import junit.framework.TestCase;
import org.codehaus.jremoting.client.Factory;
import org.codehaus.jremoting.client.monitors.ConsoleClientMonitor;
import org.codehaus.jremoting.client.factories.ServerSideStubFactory;
import org.codehaus.jremoting.client.transports.socket.SocketClientStreamInvocationHandler;
import org.codehaus.jremoting.client.transports.ClientCustomStreamDriverFactory;
import org.codehaus.jremoting.server.PublicationDescription;
import org.codehaus.jremoting.server.ServerMonitor;
import org.codehaus.jremoting.server.stubretrievers.BcelDynamicGeneratorStubRetriever;
import org.codehaus.jremoting.server.authenticators.NullAuthenticator;
import org.codehaus.jremoting.server.monitors.ConsoleServerMonitor;
import org.codehaus.jremoting.server.transports.DefaultServerSideClientContextFactory;
import org.codehaus.jremoting.server.transports.ServerCustomStreamDriverFactory;
import org.codehaus.jremoting.server.transports.socket.SelfContainedSocketStreamServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class SimpleAsync2TestCase extends TestCase {

    AsyncTestImpl asyncTestImpl;
    AsyncTest testClient;
    Factory factory;
    SelfContainedSocketStreamServer server;

    /**
     * Fetch the directory to store the classes and java source generated by the dynamic class retrievers
     */
    private String getClassGenDir() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        return path;
    }

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        BcelDynamicGeneratorStubRetriever stubRetriever = new BcelDynamicGeneratorStubRetriever(this.getClass().getClassLoader());
        String class_gen_dir = getClassGenDir();
        stubRetriever.setClassGenDir(class_gen_dir);


        ServerMonitor serverMonitor = new ConsoleServerMonitor();
        ExecutorService executorService = Executors.newCachedThreadPool();
        server = new SelfContainedSocketStreamServer(serverMonitor, stubRetriever, new NullAuthenticator(),
                new ServerCustomStreamDriverFactory(), executorService,
                new DefaultServerSideClientContextFactory(), 11009);

        asyncTestImpl = new AsyncTestImpl();
        // automatic determination of async elements.
        PublicationDescription pd = new PublicationDescription(AsyncTest.class);
        stubRetriever.generate("AsyncTestB", pd, this.getClass().getClassLoader());
        server.publish(asyncTestImpl, "AsyncTestB", pd);
        server.start();

        // Client side setup
        factory = new ServerSideStubFactory(new SocketClientStreamInvocationHandler(new ConsoleClientMonitor(),
                new ClientCustomStreamDriverFactory(), "127.0.0.1", 11009));
        testClient = (AsyncTest) factory.lookupService("AsyncTestB");

        // just a kludge for unit testing given we are intrinsically dealing with
        // threads, JRemoting being a client/server thing
        Thread.yield();
    }

    public void testSimpleAsync() throws Exception {

        testClient.setOne("one");
        testClient.setTwo("two");
        testClient.setThree("three");

        assertNull("Field 'One' should be null", asyncTestImpl.one);
        assertNull("Field 'Two' should be null", asyncTestImpl.two);
        assertNull("Field 'Tree' should be null", asyncTestImpl.three);
        assertFalse("Field 'Fire' should be false", asyncTestImpl.fired);

        testClient.fire();

        assertNotNull("Field 'One' should not be null", asyncTestImpl.one);
        assertNotNull("Field 'Two' should not be null", asyncTestImpl.two);
        assertNotNull("Field 'Tree' should not be null", asyncTestImpl.three);
        assertTrue("Field 'Fire' should not be false", asyncTestImpl.fired);

    }

    public void testRollback() throws Exception {

        testClient.setOne("111");

        assertNull("Field 'One' should be null #1", asyncTestImpl.one);

        testClient.whoa();
        testClient.fire();

        assertNull("Field 'One' should be null #2", asyncTestImpl.one);
        assertTrue("Field 'Whoa' should not be false", asyncTestImpl.whoa);
        assertTrue("Field 'Fire' should not be false", asyncTestImpl.fired);

        testClient.setOne("222");
        testClient.fire();

        assertNotNull("Field 'One' should not be null", asyncTestImpl.one);

    }


    protected void tearDown() throws Exception {
        testClient = null;
        System.gc();
        Thread.yield();
        factory.close();
        Thread.yield();
        server.stop();
        Thread.yield();
        server = null;
        asyncTestImpl = null;
        super.tearDown();
    }

    public static void main(String[] args) throws Exception {
        SimpleAsync2TestCase simp = new SimpleAsync2TestCase();
        simp.setUp();
        simp.testSimpleAsync();
        simp.tearDown();
    }

}
