/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.message.saaj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.namespace.QName;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeader;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.oracle.webservices.api.message.MessageContextFactory;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.encoding.SOAPBindingCodec;
import com.sun.xml.ws.message.stream.StreamMessage;

import junit.framework.TestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SAAJFactoryTest extends TestCase {
    private static final String CUSTOM_MIME_HEADER_NAME = "custom-header";
    private static final String CUSTOM_MIME_HEADER_NAME2 = "Content-custom-header";
    private static final String CUSTOM_MIME_HEADER_VALUE = "custom-value";
    private static final String CUSTOM_MIME_HEADER_VALUE2 = "content-custom-value";

    // Test that SAAJ message converted to JAX-WS logical message and then
    // back to SAAJ message keeps all MIME headers in the attachment part.
    public void testMimeHeadersPreserved() throws Exception {
        // Create a test SAAJ message.
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();
        msg.getSOAPBody()
                .addBodyElement(new QName("http://test/", "myelement"));

        // Add an attachment with extra MIME headers.
        addAttachmentPart(msg, "hello1");

        // Convert the SAAJ message to a logical message.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        msg.writeTo(bos);
        String contentType = msg.getMimeHeaders().getHeader("Content-Type")[0];
        Message message = getLogicalMessage(bos.toByteArray(), contentType);

        // Convert the logical message back to a SAAJ message and ensure
        // the extra headers are present.
        SOAPMessage msg2 = SAAJFactory.read(SOAPVersion.SOAP_11, message);
        assertCustomMimeHeadersOnAttachments(msg2);
        msg2.writeTo(System.out);
    }
    
    /**
     * Test whether SAAJFactory.readAsSOAPMessage can handle null namespace prefixes if the 
     * appropriate flag is set on Woodstox
     */
    public void testNullNamespacePrefix() throws Exception {
    	XMLInputFactory infact = XMLInputFactory.newFactory();
    	try {
    		//for Woodstox, set property that ensures it will return null prefixes for default namespace
    		infact.setProperty("com.ctc.wstx.returnNullForDefaultNamespace", Boolean.TRUE);
    	} catch(Throwable t) {
    		//ignore - it is not Woodstox or it is an old version of Woodstox, so this
    		//test is irrelevant. Note this try/catch is needed because Woodstox isPropertySupported
    		//is unreliable
    		return;
    	}
    	
		String soap = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
			    "<soap:Body>" + 
			        "<sendMessage xmlns=\"http://www.foo.bar/schema/\" xmlns:ns2=\"http://www.foo.bar/types/\">;" +
			        "    <message xsi:type=\"ns2:someType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
			            "</message>" + 
			        "</sendMessage></soap:Body></soap:Envelope>";
		XMLStreamReader envelope = infact.createXMLStreamReader(new StringReader(soap));
		StreamMessage smsg = new StreamMessage(SOAPVersion.SOAP_11, 
				envelope, null);
		SAAJFactory saajFac = new SAAJFactory();
		try {
			//Previously this line failed with NPE - should be fixed now. 
			SOAPMessage msg = saajFac.readAsSOAPMessage(SOAPVersion.SOAP_11, smsg);
		} catch (NullPointerException npe) {
			fail("NPE for null namespace prefix is not fixed!");
			npe.printStackTrace();
		}
    }

    /**
     * Test whether SAAJFactory.readAsSOAPMessage can handle default namespace reset correctly.
     *
     * <p>
     * This test emulates JDK-8159058 issue. The issue is that the default namespace reset was not respected
     * with built-in JDK XML input factory (it worked well with woodstax).
     * </p>
     *
     * <p>
     * This test operates against JDK XMLInputFactory.
     * </p>
     *
     */
    public void testResetDefaultNamespaceToGlobalWithJDK() throws Exception {
        XMLInputFactory inputFactory = getBuiltInJdkXmlInputFactory();
        XMLStreamReader envelope = inputFactory.createXMLStreamReader(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<s:Body>" +
                "<SampleServiceRequest xmlns=\"http://sample.ex.org/\">" +
                "<RequestParams xmlns=\"\">" +
                "<Param1>hogehoge</Param1>" +
                "<Param2>fugafuga</Param2>" +
                "</RequestParams>" +
                "</SampleServiceRequest>" +
                "</s:Body>" +
                "</s:Envelope>"));
        StreamMessage streamMessage = new StreamMessage(SOAPVersion.SOAP_11,
                envelope, null);
        SAAJFactory saajFac = new SAAJFactory();
        SOAPMessage soapMessage = saajFac.readAsSOAPMessage(SOAPVersion.SOAP_11, streamMessage);
        // check object model
        SOAPElement request = (SOAPElement)soapMessage.getSOAPBody().getFirstChild();
        assertEquals("SampleServiceRequest", request.getLocalName());
        assertEquals("http://sample.ex.org/", request.getNamespaceURI());
        SOAPElement params = (SOAPElement)request.getFirstChild();
        assertEquals("RequestParams", params.getLocalName());
        assertNull(params.getNamespaceURI());
        SOAPElement param1 = (SOAPElement)params.getFirstChild();
        assertEquals("Param1", param1.getLocalName());
        assertNull(param1.getNamespaceURI());
        Element param2 = (Element) params.getChildNodes().item(1);
        assertEquals("Param2", param2.getLocalName());
        assertNull(param2.getNamespaceURI());
        // check the message as string
        assertEquals("<SampleServiceRequest xmlns=\"http://sample.ex.org/\">" +
                        "<RequestParams xmlns=\"\">" +
                        "<Param1>hogehoge</Param1>" +
                        "<Param2>fugafuga</Param2>" +
                        "</RequestParams>" +
                        "</SampleServiceRequest>",
                nodeToText(request));
    }

    /**
     * Test whether SAAJFactory.readAsSOAPMessage can handle default namespace reset correctly.
     *
     * <p>
     * This test emulates JDK-8159058 issue. The issue is that the default namespace reset was not respected
     * with built-in JDK XML input factory (it worked well with woodstax).
     * </p>
     *
     * <p>
     * This test operates against woodstax.
     * </p>
     *
     */
    public void testResetDefaultNamespaceToGlobalWithWoodstax() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLStreamReader envelope = inputFactory.createXMLStreamReader(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<s:Body>" +
                "<SampleServiceRequest xmlns=\"http://sample.ex.org/\">" +
                "<RequestParams xmlns=\"\">" +
                "<Param1>hogehoge</Param1>" +
                "<Param2>fugafuga</Param2>" +
                "</RequestParams>" +
                "</SampleServiceRequest>" +
                "</s:Body>" +
                "</s:Envelope>"));
        StreamMessage streamMessage = new StreamMessage(SOAPVersion.SOAP_11,
                envelope, null);
        SAAJFactory saajFac = new SAAJFactory();
        SOAPMessage soapMessage = saajFac.readAsSOAPMessage(SOAPVersion.SOAP_11, streamMessage);
        // check object model
        SOAPElement request = (SOAPElement)soapMessage.getSOAPBody().getFirstChild();
        assertEquals("SampleServiceRequest", request.getLocalName());
        assertEquals("http://sample.ex.org/", request.getNamespaceURI());
        SOAPElement params = (SOAPElement)request.getFirstChild();
        assertEquals("RequestParams", params.getLocalName());
        assertNull(params.getNamespaceURI());
        SOAPElement param1 = (SOAPElement)params.getFirstChild();
        assertEquals("Param1", param1.getLocalName());
        assertNull(param1.getNamespaceURI());
        Element param2 = (Element) params.getChildNodes().item(1);
        assertEquals("Param2", param2.getLocalName());
        assertNull(param2.getNamespaceURI());
        // check the message as string
        assertEquals("<SampleServiceRequest xmlns=\"http://sample.ex.org/\">" +
                        "<RequestParams xmlns=\"\">" +
                        "<Param1>hogehoge</Param1>" +
                        "<Param2>fugafuga</Param2>" +
                        "</RequestParams>" +
                        "</SampleServiceRequest>",
                nodeToText(request));
    }
    
    public void testDuplicatedContentID() throws Exception {
        String ctype = "multipart/related; boundary=MIME_Boundary; "+ 
                    "start=\"<6232425701115978772--54bee05.140acdf4f8a.-7f3f>\"; " + 
                    "type=\"text/xml\"; start-info=\"text/xml\"";
        InputStream is = getClass().getClassLoader().getResourceAsStream("etc/bug17367334InputMsg.txt");
        MessageContextFactory mcf = MessageContextFactory.createFactory();
        Packet packet = (Packet) mcf.createContext(is, ctype);
        Message message = packet.getInternalMessage();

        SAAJFactory factory = new SAAJFactory();
        SOAPMessage saajMessage = factory.readAsSOAPMessage(SOAPVersion.SOAP_11, message);

        AttachmentPart ap = (AttachmentPart) saajMessage.getAttachments().next();
        Iterator it = ap.getAllMimeHeaders();
        int countContentID = 0;
        while (it.hasNext()) {
            MimeHeader mh = (MimeHeader)it.next();
            if ("Content-Id".equalsIgnoreCase(mh.getName())) {
                countContentID++;
            }
        }
        assertEquals("More than one Content-Id", 1, countContentID);
    }

    private AttachmentPart addAttachmentPart(SOAPMessage msg, String value) {
        AttachmentPart att = msg.createAttachmentPart(value, "text/html");
        att.addMimeHeader(CUSTOM_MIME_HEADER_NAME, CUSTOM_MIME_HEADER_VALUE);
        att.addMimeHeader(CUSTOM_MIME_HEADER_NAME2, CUSTOM_MIME_HEADER_VALUE2);
        msg.addAttachmentPart(att);
        return att;
    }

    private Message getLogicalMessage(byte[] bytes, String contentType)
            throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        WSBinding binding = BindingImpl.create(BindingID.SOAP11_HTTP);
        Codec codec = new SOAPBindingCodec(binding.getFeatures());
        Packet packet = new Packet();
        codec.decode(in, contentType, packet);
        return packet.getMessage();
    }

    private void assertCustomMimeHeadersOnAttachments(SOAPMessage msg) {
        @SuppressWarnings("unchecked")
        Iterator<AttachmentPart> attachments = msg.getAttachments();
        assertTrue(attachments.hasNext());
        while (attachments.hasNext()) {
            AttachmentPart part = attachments.next();
            String[] hdr = part.getMimeHeader(CUSTOM_MIME_HEADER_NAME);
            assertNotNull("Missing first custom MIME header", hdr);
            assertEquals("Expected one header value", hdr.length, 1);
            assertEquals("Wrong value for first header", hdr[0],
                    CUSTOM_MIME_HEADER_VALUE);
            hdr = part.getMimeHeader(CUSTOM_MIME_HEADER_NAME2);
            assertNotNull("Missing second custom MIME header", hdr);
            assertEquals("Expected one header value", hdr.length, 1);
            assertEquals("Wrong value for second header", hdr[0],
                    CUSTOM_MIME_HEADER_VALUE2);
            assertNull("Unexpected header found",
                    part.getMimeHeader("not found header"));
        }
    }

    private XMLInputFactory getBuiltInJdkXmlInputFactory() {
        final String factoryId = "test.only.xml.input.factory.class.name";
        final String className = "com.sun.xml.internal.stream.XMLInputFactoryImpl";
        System.setProperty(factoryId, className);
        XMLInputFactory infact = XMLInputFactory.newFactory(factoryId, null);
        if (!className.equals(infact.getClass().getName())) {
            throw new IllegalStateException("Can not obtain requested XMLInputFactory. Desired: " + className + ", actual: " + infact.getClass().getName());
        }
        return infact;
    }

    private String nodeToText(Node node) throws TransformerException {
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        trans.transform(new DOMSource(node), result);
        return writer.toString();
    }
}
