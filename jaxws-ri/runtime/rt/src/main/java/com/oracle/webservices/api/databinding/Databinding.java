/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.oracle.webservices.api.databinding;

import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import jakarta.xml.ws.WebServiceFeature;

import org.xml.sax.EntityResolver;

import com.oracle.webservices.api.message.MessageContext;

/**
 * {@code Databinding} is the entry point for all the WebService Databinding
 * functionality. Primarily, a Databinding is to serialize/deserialize an
 * XML(SOAP) message to/from a JAVA method invocation and return which are
 * represented as <code>JavaCallInfo</code> instances. A WSDLGenerator can
 * be created from a Databinding object to genreate WSDL representation of 
 * a JAVA service endpoint interface.
 * <p>
 * </p>
 * The supported databinding modes(flavors) are:
 * <ul>
 * <li>"toplink.jaxb"</li>
 * <li>"glassfish.jaxb"</li>
 * </ul>
 * <blockquote> Following is an example that creates a {@code Databinding} which
 * provides the operations to serialize/deserialize a JavaCallInfo to/from a
 * SOAP message:<br>
 * 
 * <pre>
 * DatabindingFactory factory = DatabindingFactory.newInstance();
 * Databinding.Builder builder = factory.createBuilder(seiClass, endpointClass);
 * Databinding databinding = builder.build(); 
 * </pre>
 * 
 * </blockquote>
 * 
 * @see com.oracle.webservices.api.databinding.DatabindingFactory
 * 
 * @author shih-chang.chen@oracle.com
 */
public interface Databinding {
	
	/**
	 * Creates a new instance of a <code>JavaCallInfo</code>.
	 * 
     * @param method The JAVA method
     * @param args The parameter objects
	 * 
	 * @return New instance of a <code>JavaCallInfo</code>
	 */
	JavaCallInfo createJavaCallInfo(Method method, Object[] args);
	
	/**
	 * Serializes a JavaCallInfo instance representing a JAVA method call to a
	 * request XML(SOAP) message.
	 * 
	 * @param call The JavaCallInfo representing a method call
	 * 
	 * @return The request XML(SOAP) message
	 */
	MessageContext serializeRequest(JavaCallInfo call);
	
	/**
	 * Deserializes a response XML(SOAP) message to a JavaCallInfo instance
	 * representing the return value or exception of a JAVA method call.
	 * 
	 * @param message The response message
	 * @param call The JavaCallInfo instance to be updated
	 * 
	 * @return The JavaCallInfo updated with the return value or exception of a
	 *         JAVA method call
	 */
	JavaCallInfo deserializeResponse(MessageContext message, JavaCallInfo call);

	/**
	 * Deserializes a request XML(SOAP) message to a JavaCallInfo instance
	 * representing a JAVA method call.
	 * 
	 * @param message The request message
	 * 
	 * @return The JavaCallInfo representing a method call
	 */
	JavaCallInfo deserializeRequest(MessageContext message);
	
	/**
	 * Serializes a JavaCallInfo instance representing the return value or
	 * exception of a JAVA method call to a response XML(SOAP) message.
	 * 
	 * @param call The JavaCallInfo representing the return value or exception 
	 *             of a JAVA method call
	 * 
	 * @return The response XML(SOAP) message
	 */
	MessageContext serializeResponse(JavaCallInfo call);

    /*
      Gets the MessageContextFactory

      @return The MessageContextFactory
     */
//Wait for WLS/src1212 - wls.jaxrpc wrapper
//	MessageContextFactory getMessageContextFactory();
	
	/**
	 * {@code Databinding.Builder}, created from the DatabindingFactory, is used to 
	 * configure how a Databinding instance is to be built from this builder. 
	 * 
     * @see com.oracle.webservices.api.databinding.DatabindingFactory
	 * @author shih-chang.chen@oracle.com
	 */
	public interface Builder {
		
		/**
		 * Sets the targetNamespace of the WSDL
		 * 
		 * @param targetNamespace The targetNamespace to set 
		 * 
         * @return this Builder instance
		 */
		Builder targetNamespace(String targetNamespace);

		/**
		 * Sets the service name of the WSDL
		 * 
		 * @param serviceName The serviceName to set
		 * 
         * @return this Builder instance
		 */
		Builder serviceName(QName serviceName);

		/**
		 * Sets the port name of the WSDL
		 * 
		 * @param portName The portName to set
		 * 
         * @return this Builder instance
		 */
		Builder portName(QName portName);
		
		/**
                 * @deprecated - no replacement - this was never implemented
                 *
		 * Sets the WSDL URL where the WSDL can be read from
		 * 
		 * @param wsdlURL The wsdlURL to set
		 * 
         * @return this Builder instance
		 */
		Builder wsdlURL(URL wsdlURL);

		/**
                 * @deprecated - no replacement - this was never implemented
                 *
		 * Sets the WSDL Source where the WSDL can be read from
		 * 
		 * @param wsdlSource The wsdlSource to set
		 * 
         * @return this Builder instance
		 */
		Builder wsdlSource(Source wsdlSource);

		/**
                 * @deprecated - no replacement - this was never implemented
                 *
		 * Sets the {@link EntityResolver} for reading the WSDL
		 * 
		 * @param entityResolver The {@link EntityResolver} to set
		 * 
         * @return this Builder instance
		 */
		Builder entityResolver(EntityResolver entityResolver);
		
		/**
		 * Sets the ClassLoader which is used to load the service endpoint 
		 * interface, implementation bean, and all the value types. If this 
		 * value is not set, the default it uses contractClass.getClassLoader().
		 * 
		 * @param classLoader The classLoader to set
		 * 
         * @return this Builder instance
		 */
		Builder classLoader(ClassLoader classLoader);
		
		/**
		 * Sets A list of WebServiceFeatures 
		 * 
		 * @param features The list of WebServiceFeatures 
		 * 
         * @return this Builder instance
		 */
		Builder feature(WebServiceFeature... features);

		/**
		 * Sets A property of the Databinding object to be created
		 * 
		 * @param name The name of the property
		 * @param value The value of the property
		 * 
         * @return this Builder instance
		 */
		Builder property(String name, Object value);

		/**
		 * Builds a new Databinding instance
		 *  
         * @return The Builder instance
		 */
		Databinding build();
	    
	    /**
	     * Creates the WSDLGenerator which can be used to generate the WSDL
	     * representation of the service endpoint interface of this Databinding
	     * object.
	     * 
	     * @return WSDLGenerator The WSDLGenerator
	     */
		com.oracle.webservices.api.databinding.WSDLGenerator createWSDLGenerator();	
	}
}
