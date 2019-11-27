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
import javax.xml.ws.soap.AddressingFeature;
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "addressing")
public class XmlAddressing implements javax.xml.ws.soap.Addressing {

    @XmlAttribute(name = "enabled")
    protected Boolean enabled;

    @XmlAttribute(name = "required")
    protected Boolean required;

    public Boolean getEnabled() {
        return enabled();
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRequired() {
        return required();
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public boolean enabled() {
        return nullSafe(enabled, true);
    }

    @Override
    public boolean required() {
        return nullSafe(required, false);
    }

    @Override
    public AddressingFeature.Responses responses() {
        return AddressingFeature.Responses.ALL;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return javax.xml.ws.soap.Addressing.class;
    }
}
