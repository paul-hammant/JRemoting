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

package org.codehaus.jremoting.server.transport;

import org.codehaus.jremoting.server.transports.ConnectingServer;
import org.codehaus.jremoting.server.context.ThreadLocalServerContextFactory;
import org.codehaus.jremoting.server.monitors.ConsoleServerMonitor;
import org.codehaus.jremoting.server.adapters.DefaultServerDelegate;
import org.codehaus.jremoting.server.ServerMonitor;
import org.codehaus.jremoting.server.authenticators.NullAuthenticator;
import org.codehaus.jremoting.server.stubretrievers.RefusingStubRetriever;
import org.codehaus.jremoting.requests.Ping;

import java.util.concurrent.Executors;

import junit.framework.TestCase;

public class ConnectingServerTestCase extends TestCase {

    public void testSessionsCanBePruned() throws InterruptedException {
        ServerMonitor sm = new ConsoleServerMonitor();
        TstInvocationHandler ihd = new TstInvocationHandler(sm);
        ConnectingServer cs = new ConnectingServer(sm, ihd, Executors.newScheduledThreadPool(10)){};
        assertTrue(ihd.sessionExists(ihd.tstSession));
        cs.setPruneSessionsInterval(1);
        cs.setPruneStaleLongerThan(1000);
        cs.start();
        Thread.sleep(1500);
        assertFalse(ihd.sessionExists(ihd.tstSession));
        cs.stop();
    }

    public void testSessionsAreNotPrunedIfTheServerIsStopped() throws InterruptedException {
        ServerMonitor sm = new ConsoleServerMonitor();
        TstInvocationHandler ihd = new TstInvocationHandler(sm);
        ConnectingServer cs = new ConnectingServer(sm, ihd, Executors.newScheduledThreadPool(10)){};
        assertTrue(ihd.sessionExists(ihd.tstSession));
        cs.setPruneSessionsInterval(1);
        cs.setPruneStaleLongerThan(1000);
        cs.start();
        cs.stop();
        Thread.sleep(1500);
        assertTrue(ihd.sessionExists(ihd.tstSession));
    }

    public void testSessionsCannotBePrunedIfRefreshed() throws InterruptedException {
        ServerMonitor sm = new ConsoleServerMonitor();
        TstInvocationHandler ihd = new TstInvocationHandler(sm);
        ConnectingServer cs = new ConnectingServer(sm, ihd, Executors.newScheduledThreadPool(10)){};
        assertTrue(ihd.sessionExists(ihd.tstSession));
        cs.setPruneSessionsInterval(1);
        cs.setPruneStaleLongerThan(1000);
        cs.start();
        Thread.sleep(300);
        ihd.doesSessionExistAndRefreshItIfItDoes(ihd.tstSession);
        Thread.sleep(300);
        ihd.doesSessionExistAndRefreshItIfItDoes(ihd.tstSession);
        Thread.sleep(300);
        ihd.doesSessionExistAndRefreshItIfItDoes(ihd.tstSession);
        Thread.sleep(300);
        ihd.doesSessionExistAndRefreshItIfItDoes(ihd.tstSession);
        assertTrue(ihd.sessionExists(ihd.tstSession));
        Thread.sleep(2000);
        assertFalse(ihd.sessionExists(ihd.tstSession));        
        cs.stop();

    }

    private static class TstInvocationHandler extends DefaultServerDelegate {
        Long tstSession;
        public TstInvocationHandler(ServerMonitor sm) {
            super(sm, new RefusingStubRetriever(), new NullAuthenticator(), new ThreadLocalServerContextFactory());
            tstSession = super.newSession("blah");
        }

        public boolean doesSessionExistAndRefreshItIfItDoes(long session) {
            return super.doesSessionExistAndRefreshItIfItDoes(session);
        }
        protected boolean sessionExists(long session) {
            return super.sessionExists(session);
        }
    }

    public void testSessionIsKeptAliveByPing() throws InterruptedException {
        ServerMonitor sm = new ConsoleServerMonitor();
        TstInvocationHandler ihd = new TstInvocationHandler(sm);
        ConnectingServer cs = new ConnectingServer(sm, ihd, Executors.newScheduledThreadPool(10)){};
        assertTrue(ihd.sessionExists(ihd.tstSession));
        cs.setPruneSessionsInterval(1);
        cs.setPruneStaleLongerThan(1000);
        cs.start();
        Thread.sleep(300);
        Ping ping = new Ping();
        ping.setSession(ihd.tstSession);
        ihd.invoke(ping, "");
        Thread.sleep(300);
        ihd.invoke(ping, "");
        Thread.sleep(300);
        ihd.invoke(ping, "");
        Thread.sleep(300);
        ihd.invoke(ping, "");
        assertTrue(ihd.sessionExists(ihd.tstSession));
        Thread.sleep(2000);
        assertFalse(ihd.sessionExists(ihd.tstSession));
        cs.stop();

    }


}
