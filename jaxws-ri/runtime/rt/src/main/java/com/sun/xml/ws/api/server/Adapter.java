/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.server;

import com.sun.xml.ws.api.config.management.Reconfigurable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.ws.util.Pool;

/**
 * Receives incoming messages from a transport (such as HTTP, JMS, etc)
 * in a transport specific way, and delivers it to {@link WSEndpoint.PipeHead#process}.
 *
 * <p>
 * Since this class mostly concerns itself with converting a
 * transport-specific message representation to a {@link Packet},
 * the name is the "adapter".
 *
 * <p>
 * The purpose of this class is twofolds:
 *
 * <ol>
 * <li>
 * To hide the logic of converting a transport-specific connection
 * to a {@link Packet} and do the other way around.
 *
 * <li>
 * To manage thread-unsafe resources, such as {@link WSEndpoint.PipeHead},
 * and {@link Codec}.
 * </ol>
 *
 * <p>
 * {@link Adapter}s are extended to work with each kind of transport,
 * and therefore {@link Adapter} class itself is not all that
 * useful by itself --- it merely provides a design template
 * that can be followed.
 *
 * <p>
 * For managing resources, an adapter uses an object called {@link Toolkit}
 * (think of it as a tray full of tools that a dentist uses ---
 * trays are identical, but each patient has to get one. You have
 * a pool of them and you assign it to a patient.)
 *
 * {@link Adapter.Toolkit} can be extended by derived classes.
 * That actual type is the {@code TK} type parameter this class takes.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Adapter<TK extends Adapter.Toolkit> 
	implements Reconfigurable, Component {

    protected final WSEndpoint<?> endpoint;

    /**
     * Object that groups all thread-unsafe resources.
     */
    public class Toolkit {
        /**
         * For encoding/decoding infoset to/from the byte stream.
         */
        public final Codec codec;
        /**
         * This object from {@link WSEndpoint} serves the request.
         */
        public final PipeHead head;

        public Toolkit() {
            this.codec = endpoint.createCodec();
            this.head = endpoint.createPipeHead();
        }
    }

    /**
     * Pool of {@link Toolkit}s.
     *
     * Instances of this pool may be replaced at runtime. Therefore, when you take
     * an object out of the pool, you must make sure that it is recycled by the
     * same instance of the pool.
     */
    protected volatile Pool<TK> pool = new Pool<TK>() {
        protected TK create() {
            return createToolkit();
        }
    };

    /**
     * Creates an {@link Adapter} that delivers
     * messages to the given endpoint.
     */
    protected Adapter(WSEndpoint endpoint) {
        assert endpoint!=null;
        this.endpoint = endpoint;
        // Enables other components to reconfigure this adapter
        endpoint.getComponents().add(getEndpointComponent());
    }
    
    protected Component getEndpointComponent() {
    	return new Component() {
			public <S> S getSPI(Class<S> spiType) {
		        if (spiType.isAssignableFrom(Reconfigurable.class)) {
		            return spiType.cast(Adapter.this);
		        }
				return null;
			}
    	};
    }

    /**
     * The pool instance needs to be recreated to prevent reuse of old Toolkit instances.
     */
    public void reconfigure() {
        this.pool = new Pool<TK>() {
            protected TK create() {
                return createToolkit();
            }
        };
    }

    public <S> S getSPI(Class<S> spiType) {
        if (spiType.isAssignableFrom(Reconfigurable.class)) {
            return spiType.cast(this);
        }
        if (endpoint != null) {
        	return endpoint.getSPI(spiType);
        }
        return null;
    }

    /**
     * Gets the endpoint that this {@link Adapter} is serving.
     *
     * @return
     *      always non-null.
     */
    public WSEndpoint<?> getEndpoint() {
        return endpoint;
    }

    /**
     * Returns a reference to the pool of Toolkits for this adapter.
     *
     * The pool may be recreated during runtime reconfiguration and this method
     * will then return a reference to a new instance. When you recycle a toolkit,
     * you must make sure that you return it to the same pool instance that you
     * took it from.
     *
     */
    protected Pool<TK> getPool() {
        return pool;
    }

    /**
     * Creates a {@link Toolkit} instance.
     *
     * <p>
     * If the derived class doesn't have to add any per-thread state
     * to {@link Toolkit}, simply implement this as {@code new Toolkit()}.
     */
    protected abstract TK createToolkit();
}
