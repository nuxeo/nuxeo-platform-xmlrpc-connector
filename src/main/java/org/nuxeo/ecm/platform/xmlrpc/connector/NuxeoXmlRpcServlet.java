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

package org.nuxeo.ecm.platform.xmlrpc.connector;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.nuxeo.ecm.platform.xmlrpc.mapping.NuxeoMetaXmlRpcHandlerMapping;

public class NuxeoXmlRpcServlet extends XmlRpcServlet {

    private static final long serialVersionUID = 98789765789651L;

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        return new NuxeoMetaXmlRpcHandlerMapping();
    }

    @Override
    public void doPost(HttpServletRequest pRequest,
            HttpServletResponse pResponse) throws IOException, ServletException {
        super.doPost(pRequest, pResponse);
    }

    @Override
    protected XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig)
            throws XmlRpcException {
        return new NuxeoXmlRpcServletServer();
    }

}
