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

import org.codehaus.jremoting.server.classretrievers.AbstractDynamicGeneratorClassRetriever;
import org.codehaus.jremoting.server.classretrievers.JavacDynamicGeneratorClassRetriever;


public class JavacSimpleAsyncTestCase extends AbstractSimpleAsyncTestCase {

    public JavacSimpleAsyncTestCase(String name) {
        super(name);
    }

    protected AbstractDynamicGeneratorClassRetriever getAbstractDynamicGeneratorClassRetriever(ClassLoader cl) {
        return new JavacDynamicGeneratorClassRetriever(cl);

    }

    public static void main(String[] args) throws Exception {
        AbstractSimpleAsyncTestCase simp = new JavacSimpleAsyncTestCase("testSimpleAsync");
        simp.setUp();
        simp.testSimpleAsync();
        simp.tearDown();
    }

    public void testSimpleAsync() throws Exception {
        super.testSimpleAsync();
    }


}
