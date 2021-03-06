package hk.hku.cecid.corvus.test;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

/**
 * The <code>AS2ReceiverList</code> is a simple web service client for sending
 * web services request to Hermes2 for querying all incoming <code>AS2 Message</code>
 * which are ready to download.
 * 
 * Creation Date: 21/05/2007
 * 
 * @author Twinsen Tsang
 */
public class AS2ReceiverList {

	// This is the XML namspace URI for Ebms. It is used when creating web services request.
	private String nsURI = "http://service.as2.edi.cecid.hku.hk/";
	
	// This is the XML namespace prefix for Ebms.
	private String nsPrefix="tns";
	
	// The "receiverList" web services URL. It should be "http://[ip]:[port]/corvus/httpd/as2/receiver_list".	
	private URL receiverListWSURL;
	
	/*
	 * The following are the required parameters to deliver the AS2 Message. 
	 */
	private String as2From;
	private String as2To;
	
	// The maximum number of ready d/l messages retrieved.  
	private int numOfMessages;
 
	/** 
	 * Explicit Constructor.
	 * 
	 * @param receiverListWSURL 
	 * 			The "receiver list" web services URL. It should be "http://[ip]:[port]/corvus/httpd/as2/receiver_list".
	 * @param as2From 	
	 * @param as2To
	 * @param numOfMessages The maximum number of ready d/l messages retrieved.
	 */
	public AS2ReceiverList(String receiverListWSURL, String as2From, String as2To, int numOfMessages)
		throws MalformedURLException
	{
		this.receiverListWSURL = new URL(receiverListWSURL);
		this.as2From = as2From;
		this.as2To	 = as2To;
		this.numOfMessages = numOfMessages > 0 ? numOfMessages : 1;
	}
	
	/**
	 * Send the web service request to Hermes2 requesting for querying  
	 * all message id of the AS2 message(s) which are ready to download from Hermes2.
	 * 
	 * @return 	An iterator which contains all message id of the message(s) which 
	 * 			are ready to download.
	 * 			 			
	 * @throws Exception 			
	 */
	public Iterator getReceivedMessagesIds() throws Exception {
		// Make a SOAP Connection and SOAP Message.		
		SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage request = MessageFactory.newInstance().createMessage();
		
		// Populate the SOAP Body
		/* This is the sample WSDL request for the sending EbMS message WS request.
		/* This is the sample WSDL request for the sending AS2 message WS request.
		 *  
		 * <as2_from> as2from </as2_from>
		 * <as2_to> as2to </as2_to>
		 * <numOfMessages> 100 </numOfMessages>  
		 */
		SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
		soapBody.addChildElement(createElement("as2From" , nsPrefix, nsURI, this.as2From));
		soapBody.addChildElement(createElement("as2To"	 , nsPrefix, nsURI, this.as2To));
		soapBody.addChildElement(createElement("numOfMessages", nsPrefix, nsURI, this.numOfMessages + ""));
		 
		// Send the request to Hermes and return the set of message id that are ready to d/l.
		SOAPMessage response = soapConn.call(request, receiverListWSURL);
		SOAPBody responseBody = response.getSOAPBody();
				
		/*
		 * The response is something like:
		 * <soap-body>
		 * 	<messageIds>
		 * 		<messageId> .. </messageId>
		 * 		<messageId>	.. </messageId>
		 * 			..
		 * 			..
		 * 	</messageIds>
		 * </soap-body>	 
		 */ 
		if (!responseBody.hasFault()){
			SOAPElement messageIdsElement = getFirstChild(responseBody, "messageIds", nsURI);
			Iterator messageIdElementIter = getChildren(messageIdsElement, "messageId", nsURI);
			
			ArrayList messageIdsList = new ArrayList();
			while(messageIdElementIter.hasNext()) {
				SOAPElement messageIdElement = (SOAPElement)messageIdElementIter.next();
				messageIdsList.add(messageIdElement.getValue());
			}
			return messageIdsList.iterator();
		} else {
			throw new SOAPException(responseBody.getFault().getFaultString());
		}		
	}
	
	/**
	 * Create a SOAP Element with specified <code>localName</code> (TagName), <code>nsPrefix</code>
	 * (Namespace prefix), <code>nsURI</code> (Namespace URI) and <code>value</code> (TagValue).
	 * 
	 * @param localName
	 * 			The tag name of SOAP Element.
	 * @param nsPrefix
	 * 			The namespace prefix of SOAP Element.
	 * @param nsURI
	 * 			The namespace URI of SOAP Element
	 * @param value
	 * 			The tag value of SOAP Element
	 * @return an SOAP Element. 			
	 * @throws SOAPException unable to create the SOAPElement
	 */
	private SOAPElement createElement(String localName, String nsPrefix, String nsURI, String value) 
		throws SOAPException 
	{
		SOAPElement soapElement = SOAPFactory.newInstance().createElement(localName, nsPrefix, nsURI); 
		soapElement.addTextNode(value);
		return soapElement;
	}
	
	/**
	 * Get the first child with <code>childLocalName</code> and <code>childNsURI</code>
	 * at the specified <code>soapElement</code>. 
	 * 
	 * @param soapElement
	 * 			The parent element you want to search from.
	 * @param childLocalName
	 * 			The element's tag name you want to search.
	 * @param childNsURI
	 * 			The element's namespace uri you want to search. 
	 * @return an SOAP Element if found, otherwise null.
	 * @throws SOAPException unable to get the children. 				
	 */
	private SOAPElement getFirstChild(SOAPElement soapElement, String childLocalName, String childNsURI) 
		throws SOAPException 
	{
		Name childName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childIter = soapElement.getChildElements(childName);
		if (childIter.hasNext())
			return (SOAPElement)childIter.next();	
		return null;
	}
	
	/**
	 * Get the iterator of children with <code>childLocalName</code> and <code>childNsURI</code>
	 * at the specified <code>soapElement</code>.  
	 * 
	 * @param soapElement
	 * 			The parent element you want to search from.
	 * @param childLocalName
	 * 			The elements' tag name you want to search.
	 * @param childNsURI
	 * 			The element's namespace uri you want to search. 
	 * @return
	 * @throws SOAPException unable to get the children.
	 */
	private Iterator getChildren(SOAPElement soapElement, String childLocalName, String childNsURI) 
		throws SOAPException 
	{
		Name childrenName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
		Iterator childrenIter = soapElement.getChildElements(childrenName);
		return childrenIter;
	}
}
