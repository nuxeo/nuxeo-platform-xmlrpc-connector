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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.common.ServerStreamConnection;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.parser.XmlRpcRequestParser;
import org.apache.xmlrpc.util.SAXParsers;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Overrides default XmlRpcServer to change methodName parsing.
 * <p>
 * The purpose is to be compliant with CPSRemoteControler calls,
 * ie the same method should be callable via 2 ways :
 *
 * <ol>
 * <li>Standard XML-RPC:
 * <code><pre>
 * config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/"));
 * client.setConfig(config);
 * String result = (String) client.execute("simpleTest.ping", new Object[]{});
 * </pre></code>
 *
 * <li> CPS way
 * <code><pre>
 * config.setServerURL(new URL("http://127.0.0.1:8080/nuxeo/rpc/simpleTest"));
 * client.setConfig(config);
 * String result = (String) client.execute("ping", new Object[]{});
 * </pre></code>
 * </ol>
 *
 * @author tiry
 *
 */
public class NuxeoXmlRpcServletServer extends XmlRpcServletServer {

    private static final Log log = LogFactory.getLog(NuxeoXmlRpcServletServer.class);


    static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();


    public static HttpServletRequest getRequest()
    {
    	return requests.get();
    }

    public static NuxeoPrincipal getCallerPrincipal()
    {
    	return (NuxeoPrincipal) getRequest().getUserPrincipal();
    }

    public static HttpSession getSession()
    {
    	return getRequest().getSession(true);
    }

    /**
     * Same as base class method, but with an additionnal parameter
     * that contains component name extracted from request.
     *
     * @param pConfig
     * @param pStream
     * @param handlerPrefix componentName extracted from request
     * @return
     * @throws XmlRpcException
     */
    protected XmlRpcRequest getRequest(final XmlRpcStreamRequestConfig pConfig,
            InputStream pStream,final String handlerPrefix) throws XmlRpcException {

        final XmlRpcRequestParser parser = new XmlRpcRequestParser(pConfig,
                getTypeFactory());
        final XMLReader xr = SAXParsers.newXMLReader();
        xr.setContentHandler(parser);
        try {
            xr.parse(new InputSource(pStream));
        } catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof XmlRpcException) {
                throw (XmlRpcException) ex;
            }
            throw new XmlRpcException("Failed to parse XML-RPC request: "
                    + e.getMessage(), e);
        } catch (IOException e) {
            throw new XmlRpcException("Failed to read XML-RPC request: "
                    + e.getMessage(), e);
        }
        final List params = parser.getParams();
        return new XmlRpcRequest() {
            public XmlRpcRequestConfig getConfig() {
                return pConfig;
            }

            public String getMethodName() {
                String mName=parser.getMethodName();
                if (handlerPrefix == null || "".equals(handlerPrefix)) {
                    return mName;
                } else {
                    return handlerPrefix + '.' + mName;
                }
            }

            public int getParameterCount() {
                return params == null ? 0 : params.size();
            }

            public Object getParameter(int pIndex) {
                return params.get(pIndex);
            }
        };
    }

    @Override
    public void execute(HttpServletRequest pRequest,
            HttpServletResponse pResponse) throws ServletException, IOException {
        XmlRpcHttpRequestConfigImpl config = getConfig(pRequest);
        ServletStreamConnection ssc = newStreamConnection(pRequest, pResponse);
        try {
        	requests.set(pRequest);
            execute(config, ssc);
        } catch (XmlRpcException e) {
            throw new ServletException(e);
        }
        finally{
        	requests.set(null);
        }
    }

    @Override
    public void execute(XmlRpcStreamRequestConfig pConfig,
            ServerStreamConnection pConnection) throws XmlRpcException {

        ServletStreamConnection ssc = (ServletStreamConnection) pConnection;
        String uri = ssc.getRequest().getRequestURI();
        String handlerPrefix="";
        if (!uri.endsWith("/rpc") && !uri.endsWith("/rpc/")) {
            String[] urlParts = uri.split("/");
            handlerPrefix = urlParts[urlParts.length - 1];
        }
        log.debug("execute: ->");
        try {
            Object result;
            Throwable error;
            InputStream istream = null;
            XmlRpcRequest request = null;
            try {
                istream = getInputStream(pConfig, pConnection);
                request = getRequest(pConfig, istream, handlerPrefix);
                result = execute(request);
                istream.close();
                istream = null;
                error = null;
                XmlRpcLogger.log(ssc.getRequest(), request, result);
                log.debug("execute: Request performed successfully");
            } catch (Throwable t) {
                log.error("execute: Error while performing request", t);
                if ( request != null ) {// try to log the request also
                	 XmlRpcLogger.log(ssc.getRequest(), request, "ERR: request failed: " + t.getMessage());
                }
                result = null;
                error = t;
            } finally {
                if (istream != null) {
                    try {
                        istream.close();
                    } catch (Throwable ignore) {

                    }
                }
            }
            boolean contentLengthRequired = isContentLengthRequired(pConfig);
            ByteArrayOutputStream baos;
            OutputStream ostream;
            if (contentLengthRequired) {
                baos = new ByteArrayOutputStream();
                ostream = baos;
            } else {
                baos = null;
                ostream = pConnection.newOutputStream();
            }
            ostream = getOutputStream(pConnection, pConfig, ostream);
            try {
                if (error == null) {
                    writeResponse(pConfig, ostream, result);
                } else {
                    writeError(pConfig, ostream, error);
                }
                ostream.close();
                ostream = null;
            } finally {
                if (ostream != null) {
                    try {
                        ostream.close();
                    } catch (Throwable ignore) {
                    }
                }
            }
            if (baos != null) {
                OutputStream dest = getOutputStream(pConfig, pConnection,
                        baos.size());
                try {
                    baos.writeTo(dest);
                    dest.close();
                    dest = null;
                } finally {
                    if (dest != null) {
                        try {
                            dest.close();
                        } catch (Throwable ignore) {
                        }
                    }
                }
            }
            pConnection.close();
            pConnection = null;
        } catch (IOException e) {
            throw new XmlRpcException("I/O error while processing request: "
                    + e.getMessage(), e);
        } finally {
            if (pConnection != null) {
                try {
                    pConnection.close();
                } catch (Throwable ignore) {
                }
            }
        }
        log.debug("execute: <-");
    }

}
