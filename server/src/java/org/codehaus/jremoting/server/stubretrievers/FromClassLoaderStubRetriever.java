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
package org.codehaus.jremoting.server.stubretrievers;

import org.codehaus.jremoting.server.StubRetrievalException;
import org.codehaus.jremoting.server.StubRetriever;
import org.codehaus.jremoting.util.StaticStubHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class FromClassLoaderStubRetriever
 *
 * @author Paul Hammant
 */
public class FromClassLoaderStubRetriever implements StubRetriever {

    private ClassLoader classLoader;


    public FromClassLoaderStubRetriever() {
        this(FromClassLoaderStubRetriever.class.getClassLoader());
    }

    public FromClassLoaderStubRetriever(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public final byte[] getStubClassBytes(String publishedName) throws StubRetrievalException {
        String thingName = StaticStubHelper.formatStubClassName(publishedName);

        InputStream is = null;

        thingName = thingName.replace('.', File.separatorChar) + ".class";

        try {
            is = classLoader.getResourceAsStream(thingName);
        } catch (Exception e) {
            throw new StubRetrievalException("Generated class not found in classloader specified : " + e.getMessage());
        }

        if (is == null) {
            throw new StubRetrievalException("Generated class not found in classloader specified.");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;

        try {
            while (-1 != (i = is.read())) {
                baos.write(i);
            }

            is.close();
        } catch (IOException e) {
            throw new StubRetrievalException("Error retrieving generated class bytes : " + e.getMessage());
        }

        return baos.toByteArray();
    }

}
