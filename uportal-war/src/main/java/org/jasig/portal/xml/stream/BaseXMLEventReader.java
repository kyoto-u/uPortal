/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package org.jasig.portal.xml.stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

/**
 * Base for XMLEventReader that implements the {@link #getElementText()} and {@link #nextTag()} APIs in a
 * way that is agnostic from the rest of the XMLEventReader implementation. Both will use the subclasses
 * {@link #internalNextEvent()} as the exclusive way to read events.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public abstract class BaseXMLEventReader extends EventReaderDelegate {
    private XMLEvent previousEvent;
    
    public BaseXMLEventReader(XMLEventReader reader) {
        super(reader);
    }

    /**
     * Subclass's version of {@link #nextEvent()}, called by {@link #next()}
     */
    protected abstract XMLEvent internalNextEvent() throws XMLStreamException;
    
    /**
     * @return The XMLEvent returned by the last call to {@link #internalNextEvent()}
     */
    protected final XMLEvent getPreviousEvent() {
        return this.previousEvent;
    }
    
    @Override
    public final XMLEvent nextEvent() throws XMLStreamException {
        this.previousEvent = this.internalNextEvent();
        return this.previousEvent;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public final Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException e) {
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see javax.xml.stream.XMLEventReader#getElementText()
     */
    @Override
    public final String getElementText() throws XMLStreamException {
        XMLEvent event = this.previousEvent;
        if (event == null || !event.isStartElement()) {
            throw new XMLStreamException("Must be on START_ELEMENT to read next text", event.getLocation());
        }
        
        final StringBuilder text = new StringBuilder();
        while (!event.isEndDocument()) {
            switch (event.getEventType()) {
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.SPACE:
                case XMLStreamConstants.CDATA: {
                    final Characters characters = event.asCharacters();
                    text.append(characters.getData());
                    break;
                }
                case XMLStreamConstants.ENTITY_REFERENCE: {
                    final EntityReference entityReference = (EntityReference)event;
                    final EntityDeclaration declaration = entityReference.getDeclaration();
                    text.append(declaration.getReplacementText());
                    break;
                }
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                    //Ignore
                    break;
                }
                default: {
                    throw new XMLStreamException("Unexpected event type '" + XMLStreamConstantsUtils.getEventName(event.getEventType()) + "' encountered. Found event: " + event, event.getLocation());
                }
            }
            
            event = this.nextEvent();
        }
        
        return text.toString();
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLEventReader#nextTag()
     */
    @Override
    public final XMLEvent nextTag() throws XMLStreamException {
        XMLEvent event = this.nextEvent();
        while ((event.isCharacters() && event.asCharacters().isWhiteSpace())
                || event.isProcessingInstruction()
                || event.getEventType() == XMLStreamConstants.COMMENT) {
            
            event = this.nextEvent();
        }

        if (!event.isStartElement()  && event.isEndElement()) {
            throw new XMLStreamException("Unexpected event type '" + XMLStreamConstantsUtils.getEventName(event.getEventType()) + "' encountered. Found event: " + event, event.getLocation());
        }

        return event;
    }
}