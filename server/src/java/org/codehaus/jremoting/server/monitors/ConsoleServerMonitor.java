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
package org.codehaus.jremoting.server.monitors;

import org.codehaus.jremoting.server.ServerMonitor;
import org.codehaus.jremoting.server.Session;

import java.io.IOException;

/**
 * Class ConsoleServerMonitor
 *
 * @author Paul Hammant
 *
 */
public class ConsoleServerMonitor implements ServerMonitor {

    public void closeError(Class clazz, String s, IOException e) {
        System.out.println("[closeError] " + s + " : " + e.getMessage());
    }

    public void classNotFound(Class clazz, ClassNotFoundException e) {
        System.out.println("[classNotFound] " + e.getMessage());
    }

    public void unexpectedException(Class clazz, String s, Throwable e) {
        System.out.println("[unexpectedException] " + s + " : " + e.getMessage());
        e.printStackTrace();
    }

    public void stopServerError(Class clazz, String s, Exception e) {
        System.out.println("[stopServerError] " + s + " : " + e.getMessage());
    }

    public void newSession(Session session, int numberOfSessions, String connectionDetails) {
        System.out.println("[newSession] " + session.getSession() + " , sessions: " + numberOfSessions);
    }

    public void removeSession(Session session, int newSize) {
        System.out.println("[removeSession] " + session.getSession() + " , sessions: " + newSize);
    }

    public void staleSession(Session session, int numberOfSessions) {
        System.out.println("[staleSession] " + session.getSession() + " , sessions: " + numberOfSessions);
    }
}
