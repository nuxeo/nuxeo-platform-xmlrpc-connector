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

package org.nuxeo.ecm.platform.xmlrpc.tests.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import junit.framework.TestCase;

public class RemoteControlerTests extends TestCase {

    public void testProductVersion()
            throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/remoteControler"));
        client.setConfig(config);

        Object[] params = new Object[]{"CPSRemoteController"};
        String result = (String) client.execute("getProductVersion", params);
        assertEquals("AfpCps", result);
    }

}
