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
package org.codehaus.jremoting.client.factories;

import org.codehaus.jremoting.ConnectionException;
import org.codehaus.jremoting.client.ClientInvocationHandler;
import org.codehaus.jremoting.client.ProxyHelper;

/**
 * DynamicStubFactory creates stubs(@see DynamicStub) for the given
 * publishedName at the time of invocation.Using this factory removes the need
 * for any compiled stubs corresponding to the remote interface
 * to be present on the client side to invoke any remote method on the server.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class DynamicStubFactory extends AbstractFactory {

    public DynamicStubFactory(ClientInvocationHandler clientInvocationHandler) throws ConnectionException {
        super(clientInvocationHandler);
    }

    protected Class getStubClass(String publishedServiceName, String objectName) throws ConnectionException, ClassNotFoundException {
        //NOT USED
        return null;
    }

    protected Object getInstance(String publishedServiceName, String objectName, ProxyHelper proxyHelper) throws ConnectionException {
        return new DynamicStub(publishedServiceName, objectName, proxyHelper);
    }

}
