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

package org.nuxeo.ecm.platform.xmlrpc.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class XmlRpcService extends DefaultComponent implements XmlRpcManager {

    //XmlRpcHandlerMapping

    private final Map<String, XmlRpcHandlerMapping> mappingHandlers = new HashMap<String, XmlRpcHandlerMapping>();
    private final Map<String, XmlRpcHandlerDescriptor> handlerDescriptors = new HashMap<String, XmlRpcHandlerDescriptor>();
    private final Map<String, Class> handlerClasses = new HashMap<String, Class>();

    @Override
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) {

        if (EP_MAPPING_HANDLERS.equals(extensionPoint)) {
            registerMappingHandler(contribution);
        } else if (EP_HANDLERS.equals(extensionPoint)) {
            registerHandler(contribution);
        }
    }

    public void registerMappingHandler(Object contribution) {

        XmlRpcHandlerMappingDescriptor descriptor = (XmlRpcHandlerMappingDescriptor) contribution;

        try {
            XmlRpcHandlerMapping newMapper = (XmlRpcHandlerMapping) descriptor.getMapperClass().newInstance();
            mappingHandlers.put(descriptor.getName(), newMapper);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void registerHandler(Object contribution) {

        XmlRpcHandlerDescriptor descriptor = (XmlRpcHandlerDescriptor) contribution;
        handlerDescriptors.put(descriptor.getName(), descriptor);
        if (descriptor.getHandlerClass() != null) {
            handlerClasses.put(descriptor.getName(),
                    descriptor.getHandlerClass());
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
    }

    public XmlRpcHandlerMapping getHandlerMapping(String handler) {
        XmlRpcHandlerDescriptor descriptor = handlerDescriptors.get(handler);
        if (descriptor == null) {
            return null;
        }

        String mapping = descriptor.getMapping();

        return mappingHandlers.get(mapping);
    }

    public Class getPojoClassForHandler(String handler) {
        return handlerClasses.get(handler);
    }

}
