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
package org.codehaus.jremoting.requests;

import org.codehaus.jremoting.Contextualizable;
import org.codehaus.jremoting.requests.AbstractPublishedNameRequest;
import org.codehaus.jremoting.requests.RequestConstants;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class InvokeAsyncMethod
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class InvokeAsyncMethod extends AbstractPublishedNameRequest implements Contextualizable {
    static final long serialVersionUID = -5928783250529633953L;

    private GroupedMethodRequest[] groupedRequests;
    private Long referenceID;
    private Long session;

    /**
     * Constructor InvokeMethod
     *
     * @param publishedServiceName the published service name
     * @param objectName           the object Name
     * @param rawRequests          The raw requests
     * @param referenceID          the reference ID
     * @param session              the session ID
     */
    public InvokeAsyncMethod(String publishedServiceName, String objectName, GroupedMethodRequest[] rawRequests, Long referenceID, Long session) {

        super(publishedServiceName, objectName);

        groupedRequests = rawRequests;
        this.referenceID = referenceID;
        this.session = session;
    }

    /**
     * Constructor InvokeMethod for Externalization
     */
    public InvokeAsyncMethod() {
    }


    public GroupedMethodRequest[] getGroupedRequests() {
        return groupedRequests;
    }

    /**
     * Get the reference ID.
     *
     * @return the reference ID
     */
    public Long getReferenceID() {
        return referenceID;
    }

    /**
     * Gets number that represents type for this class.
     * This is quicker than instanceof for type checking.
     *
     * @return the representative code
     * @see org.codehaus.jremoting.requests.RequestConstants
     */
    public int getRequestCode() {
        return RequestConstants.METHODASYNCREQUEST;
    }

    /**
     * Get the session ID.
     *
     * @return the session ID
     */
    public Long getSession() {
        return session;
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @throws IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe
     * the data layout of this Externalizable object.
     * List the sequence of element types and, if possible,
     * relate the element to a public/protected field and/or
     * method of this Externalizable class.
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        super.writeExternal(out);
        out.writeObject(groupedRequests);
        out.writeObject(referenceID);
        out.writeObject(session);
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException            if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        super.readExternal(in);

        groupedRequests = (GroupedMethodRequest[]) in.readObject();
        referenceID = (Long) in.readObject();
        session = (Long) in.readObject();
    }
}
