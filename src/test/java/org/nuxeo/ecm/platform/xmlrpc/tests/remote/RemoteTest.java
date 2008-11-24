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
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import junit.framework.TestCase;

public class RemoteTest extends TestCase {

    public void testPing() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/"));
        client.setConfig(config);
        //Object[] params = new Object[]{new Integer(33), new Integer(9)};
        Object[] params = new Object[]{};
        String result = (String) client.execute("simpleTest.ping", params);
        assertEquals("pong", result);
    }

    public void testPingCPS() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);
        //Object[] params = new Object[]{new Integer(33), new Integer(9)};
        Object[] params = new Object[]{};
        String result = (String) client.execute("ping", params);
        assertEquals("pong", result);
    }

    public void testHello() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/"));
        client.setConfig(config);
        Object[] params = new Object[]{"Tiry"};
        String result = (String) client.execute("simpleTest.sayHello", params);
        assertEquals("Hello Tiry", result);
    }

    public void testHelloCPS() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);
        Object[] params = new Object[]{"Tiry"};
        String result = (String) client.execute("sayHello", params);
        assertEquals("Hello Tiry", result);
    }

    public void testList() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/"));
        client.setConfig(config);

        Object[] params = new Object[]{"A:B:C:D", ":"};
        Object[] result = (Object[]) client.execute("simpleTest.splitToList",
                params);
        assertEquals(4, result.length);
        System.out.print(result);
    }

    public void testListCPS() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);

        Object[] params = new Object[]{"A:B:C:D", ":"};
        Object[] result = (Object[]) client.execute("splitToList", params);
        assertEquals(4, result.length);
        System.out.print(result);
    }

    public void testMap() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/"));
        client.setConfig(config);

        Object[] params = new Object[]{"A:B;C:D", ";", ":"};
        Map<String, String> result = (Map<String, String>) client.execute(
                "simpleTest.splitToMap", params);
        System.out.print(result);
    }

    public void testMapCPS() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);

        Object[] params = new Object[]{"A:B;C:D", ";", ":"};
        Map<String, String> result = (Map<String, String>) client.execute(
                "splitToMap", params);
        System.out.print(result);
    }

    public void testUserName() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);

        Object[] params = new Object[]{};
        String result = (String) client.execute("getUserName", params);
        System.out.print(result);
        assertNotNull(result);
        assertEquals(result, "Administrator");
    }

    public void testSession() throws MalformedURLException, XmlRpcException {
        XmlRpcClient client = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setBasicUserName("Administrator");
        config.setBasicPassword("Administrator");
        config.setServerURL(
                new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
        client.setConfig(config);

        Object[] params = new Object[]{};
        Integer result = (Integer) client.execute("incrementCounter", params);
        System.out.print(result);
        assertNotNull(result);
        assertTrue(result==1);
        //result = (Integer) client.execute("incrementCounter", params);
        //assertNotNull(result);
        //assertTrue(result==2);
    }

}
