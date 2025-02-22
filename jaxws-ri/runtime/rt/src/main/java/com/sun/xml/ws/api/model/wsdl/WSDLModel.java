/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.model.wsdl;


import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.wsdl.parser.RuntimeWSDLParser;
import com.sun.xml.ws.policy.PolicyMap;

import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.util.Map;

/**
 * Provides abstraction of wsdl:definitions.
 *
 * @author Vivek Pandey
 */
public interface WSDLModel extends WSDLExtensible {
    /**
     * Gets {@link WSDLPortType} that models <code>wsdl:portType</code>
     *
     * @param name non-null quaified name of wsdl:message, where the localName is the value of <code>wsdl:portType@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link com.sun.xml.ws.api.model.wsdl.WSDLPortType} or null if no wsdl:portType found.
     */
    WSDLPortType getPortType(@NotNull QName name);

    /**
     * Gets {@link WSDLBoundPortType} that models <code>wsdl:binding</code>
     *
     * @param name non-null quaified name of wsdl:binding, where the localName is the value of <code>wsdl:binding@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLBoundPortType} or null if no wsdl:binding found
     */
    WSDLBoundPortType getBinding(@NotNull QName name);

    /**
     * Give a {@link WSDLBoundPortType} for the given wsdl:service and wsdl:port names.
     *
     * @param serviceName service QName
     * @param portName    port QName
     * @return A {@link WSDLBoundPortType}. null if the Binding for the given wsd:service and wsdl:port name are not
     *         found.
     */
    WSDLBoundPortType getBinding(@NotNull QName serviceName, @NotNull QName portName);

    /**
     * Gets {@link WSDLService} that models <code>wsdl:service</code>
     *
     * @param name non-null quaified name of wsdl:service, where the localName is the value of <code>wsdl:service@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLService} or null if no wsdl:service found
     */
    WSDLService getService(@NotNull QName name);

    /**
     * Gives a {@link Map} of wsdl:portType {@link QName} and {@link WSDLPortType}
     *
     * @return an empty Map if the wsdl document has no wsdl:portType
     */
    @NotNull Map<QName, ? extends WSDLPortType> getPortTypes();

    /**
     * Gives a {@link Map} of wsdl:binding {@link QName} and {@link WSDLBoundPortType}
     *
     * @return an empty Map if the wsdl document has no wsdl:binding
     */
    @NotNull Map<QName, ? extends WSDLBoundPortType> getBindings();

    /**
     * Gives a {@link Map} of wsdl:service qualified name and {@link com.sun.xml.ws.api.model.wsdl.WSDLService}
     *
     * @return an empty Map if the wsdl document has no wsdl:service
     */
    @NotNull Map<QName, ? extends WSDLService> getServices();

    /**
     * Returns the first service QName from insertion order
     */
    public QName getFirstServiceName();
    
    /**
     * Returns the message with the given QName
     * @param name Message name
     * @return Message
     */
    public WSDLMessage getMessage(QName name);
    
    /**
     * Gives a {@link Map} of wsdl:message qualified name and {@link com.sun.xml.ws.api.model.wsdl.WSDLMessage}
     *
     * @return an empty Map if the wsdl document has no wsdl:message
     */
    @NotNull Map<QName, ? extends WSDLMessage> getMessages();

    /**
     * Gives the PolicyMap associated with the WSDLModel
     *
     * @return PolicyMap
     *
     * @deprecated
     * Do not use this method as the PolicyMap API is not final yet and might change in next few months.
     */
    public PolicyMap getPolicyMap();

    /**
     * Main purpose of this class is to  parsing of a WSDL and get the {@link WSDLModel} from it.
     */
    public class WSDLParser{
       /**
         * Parses WSDL from the given wsdlLoc and gives a {@link WSDLModel} built from it.
         *
         * @param wsdlEntityParser  Works like an entityResolver to resolve WSDLs
         * @param resolver  {@link XMLEntityResolver}, works at XML infoset level
         * @param isClientSide  true - its invoked on the client, false means its invoked on the server
         * @param extensions var args of {@link com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension}s
         * @return A {@link WSDLModel} built from the given wsdlLocation}
        */
        public static @NotNull WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return parse(wsdlEntityParser, resolver, isClientSide, Container.NONE, extensions);
        }

        /**
         * Parses WSDL from the given wsdlLoc and gives a {@link WSDLModel} built from it.
         *
         * @param wsdlEntityParser  Works like an entityResolver to resolve WSDLs
         * @param resolver  {@link XMLEntityResolver}, works at XML infoset level
         * @param isClientSide  true - its invoked on the client, false means its invoked on the server
         * @param container - container in which the parser is run
         * @param extensions var args of {@link com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension}s
         * @return A {@link WSDLModel} built from the given wsdlLocation}
         */
        public static @NotNull WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return parse(wsdlEntityParser, resolver, isClientSide, container, PolicyResolverFactory.create(),extensions);
        }


        /**
         * Parses WSDL from the given wsdlLoc and gives a {@link WSDLModel} built from it.
         *
         * @param wsdlEntityParser  Works like an entityResolver to resolve WSDLs
         * @param resolver  {@link XMLEntityResolver}, works at XML infoset level
         * @param isClientSide  true - its invoked on the client, false means its invoked on the server
         * @param container - container in which the parser is run
         * @param policyResolver - PolicyResolver for resolving effective Policy
         * @param extensions var args of {@link com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension}s
         * @return A {@link WSDLModel} built from the given wsdlLocation}
         */
        public static @NotNull WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, PolicyResolver policyResolver,  WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
            return RuntimeWSDLParser.parse(wsdlEntityParser, resolver, isClientSide, container, policyResolver, extensions);
        }

    }
}
