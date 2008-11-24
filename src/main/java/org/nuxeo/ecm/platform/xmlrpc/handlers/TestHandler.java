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

package org.nuxeo.ecm.platform.xmlrpc.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nuxeo.ecm.platform.xmlrpc.connector.NuxeoXmlRpcServletServer;

public class TestHandler {

    // simple test methods
    public String ping() {
        return "pong";
    }

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public List<String> splitToList(String data, String sep) {
        List<String> result = new ArrayList<String>();
        String[] dataParts = data.split(sep);

        for (String dataPart : dataParts) {
            result.add(dataPart);
        }
        return result;
    }

    public Map<String, String> splitToMap(String data, String sep1,
            String sep2) {
        Map<String, String> result = new HashMap<String, String>();

        String[] dataParts = data.split(sep1);

        for (String item : dataParts) {
            String[] mapParts = item.split(sep2);

            result.put(mapParts[0], mapParts[1]);
        }
        return result;
    }

    public String getUserName()
    {
    	return NuxeoXmlRpcServletServer.getCallerPrincipal().getName();
    }

    public String getRequestURL()
    {
    	return NuxeoXmlRpcServletServer.getRequest().getRequestURI();
    }

    public Integer incrementCounter()
    {
    	HttpSession session = NuxeoXmlRpcServletServer.getSession();
    	Integer counter = (Integer) session.getAttribute("counter");
    	if (counter==null)
    		counter=1;
    	else
    		counter+=1;
    	session.setAttribute("counter", counter);

    	return counter;
    }
}
