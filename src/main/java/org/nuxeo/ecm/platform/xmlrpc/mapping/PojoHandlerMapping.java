/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.xmlrpc.mapping;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.nuxeo.ecm.platform.xmlrpc.service.XmlRpcManager;
import org.nuxeo.runtime.api.Framework;

public class PojoHandlerMapping extends AbstractReflectiveHandlerMapping {

    protected XmlRpcManager manager;

    protected XmlRpcManager getManager() throws XmlRpcException {
        if (manager == null) {
            try {
                manager = Framework.getService(XmlRpcManager.class);
            } catch (Exception e) {
                throw new XmlRpcException("Unable to get XmlRpcManager service",
                        e);
            }
        }

        return manager;
    }

    @Override
    public XmlRpcHandler getHandler(String pHandlerName)
            throws XmlRpcNoSuchHandlerException, XmlRpcException {

        XmlRpcHandler result = (XmlRpcHandler) handlerMap.get(pHandlerName);
        if (result == null) {
            String[] handlerParts = pHandlerName.split("\\.");
            Class c = getManager().getPojoClassForHandler(handlerParts[0]);
            if (c == null) {
                throw new XmlRpcNoSuchHandlerException(
                        "No such handler: " + handlerParts[0]);
            }
            registerPublicMethods(handlerParts[0], c);
            result = (XmlRpcHandler) handlerMap.get(pHandlerName);
            if (result == null) {
                throw new XmlRpcNoSuchHandlerException(
                        "No such handler: " + pHandlerName);
            }
        }
        return result;
    }

}
