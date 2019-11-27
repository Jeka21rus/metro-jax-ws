/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.oracle.xmlns.webservices.jaxws_databinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.lang.annotation.Annotation;

import static com.oracle.xmlns.webservices.jaxws_databinding.Util.nullSafe;


/**
 * This file was generated by JAXB-RI v2.2.6 and afterwards modified
 * to implement appropriate Annotation
 *
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="local-name" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;attribute name="target-namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;attribute name="class-name" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;attribute name="part-name" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "request-wrapper")
public class XmlRequestWrapper implements javax.xml.ws.RequestWrapper {

    @XmlAttribute(name = "local-name")
    protected String localName;
    @XmlAttribute(name = "target-namespace")
    protected String targetNamespace;
    @XmlAttribute(name = "class-name")
    protected String className;
    @XmlAttribute(name = "part-name")
    protected String partName;

    /**
     * Gets the value of the localName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalName() {
        if (localName == null) {
            return "";
        } else {
            return localName;
        }
    }

    /**
     * Sets the value of the localName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalName(String value) {
        this.localName = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        if (targetNamespace == null) {
            return "";
        } else {
            return targetNamespace;
        }
    }

    /**
     * Sets the value of the targetNamespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    /**
     * Gets the value of the className property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassName() {
        if (className == null) {
            return "";
        } else {
            return className;
        }
    }

    /**
     * Sets the value of the className property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassName(String value) {
        this.className = value;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    @Override
    public String localName() {
        return nullSafe(localName);
    }

    @Override
    public String targetNamespace() {
        return nullSafe(targetNamespace);
    }

    @Override
    public String className() {
        return nullSafe(className);
    }

    @Override
    public String partName() {
        return nullSafe(partName);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return javax.xml.ws.RequestWrapper.class;
    }
}
