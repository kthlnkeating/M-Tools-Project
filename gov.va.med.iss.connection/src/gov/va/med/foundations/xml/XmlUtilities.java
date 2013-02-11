package gov.va.med.foundations.xml;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

//import x.gov.va.med.iss.log4j.*;

/**
 * This class contains a number of static utility methods to
 * help developers work with XML documents, nodes, attributes and strings
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class XmlUtilities {

	/**
	 * Represents the default header used for all xml documents that communicate with
	 * an M server via VistALink. It is important to use this header as keeps the
	 * client and M server in sync.
	 */
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

	/**
	 * The logger used bythis class
	 */
	private static final Logger logger = Logger.getLogger(XmlUtilities.class);

	/**
	 * The document builder factory used by this class to construct 
	 * a DocumentBuilder
	 */
	private static DocumentBuilderFactory documentBuilderFactory = null;

	/**
	 * The document builder used to construct DOM documents
	 */
	private static DocumentBuilder builder = null;

	/*
	 * Since DocumentBuilder is initialized within static class code block,
	 * we can not throw exceptions from the static code block.
	 * Instead we save exception and logger string and throw it later on when instance methods are called
	 * if there were any problems intializing DocumentBuilder.
	 */
	private static FoundationsException documentBuilderInitException = null;

	/*
	 * Since DocumentBuilder is initialized within static class code block,
	 * we can not throw exceptions from the static code block.
	 * Instead we save exception and logger string and throw it later on when instance methods are called
	 * if there were any problems intializing DocumentBuilder.
	 */
	private static String documentBuilderInitExceptionLogString = null;

	/**
	 * The transformer factory used by this class to construct 
	 * a Transformer
	 */
	private static TransformerFactory transformerFactory = null;

	/**
	 * The Transformer used to convert DOM to String
	 */
	private static Transformer transformer = null;

	/*
	 * Since Transformer is initialized within static class code block,
	 * we can not throw exceptions from the static code block.
	 * Instead we save exception and logger string and throw it later on when instance methods are called
	 * if there were any problems intializing Transformer.
	 */
	private static FoundationsException transformerInitException = null;

	/*
	 * Since Transformer is initialized within static class code block,
	 * we can not throw exceptions from the static code block.
	 * Instead we save exception and logger string and throw it later on when instance methods are called
	 * if there were any problems intializing Transformer.
	 */
	private static String transformerInitExceptionLogString = null;


	/*
	 * Static method to intialize Transformer & DocumentBuilder.
	 * Since initialization is expensive, we need to do it once per class life-cycle.
	 * Performing synchronized() in the regular method would impact performance.
	 * Double-checking is not guaranteed to work in different java implementations.
	 * 
	 * Static block implementation is executed when class is loaded and will ensure initialization 
	 * of these objects before their usage.  
	 */
	static {

		// start: initialize DocumentBuilder
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			builder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			String errStr =
				"Can not get DocumentBuilder from DocumentBuilderFactory - check JAXP configuration/classpaths.";

			if (logger.isEnabledFor(Priority.ERROR)) {
				documentBuilderInitExceptionLogString =
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString();
				logger.error(documentBuilderInitExceptionLogString);
			}
			documentBuilderInitException = new FoundationsException(errStr, e);
		}
		// only proceed with further initialization if no exceptions were found creating DocumentBuilder
		if (documentBuilderInitException == null) {
			// Log implementation of Document Builder class 
			if (logger.isDebugEnabled()) {
				logger.debug("Using DocumentBuilder implementation: " + builder.getClass().getName());
			}
		}
		// end: initialize DocumentBuilder


		// start: initialize Transformer
		transformerFactory = TransformerFactory.newInstance();
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			String errStr = "Exception occured trying to create Transformer.";
			if (logger.isEnabledFor(Priority.ERROR)) {
				transformerInitExceptionLogString =
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString();
				logger.error(transformerInitExceptionLogString);
			}
			transformerInitException = new FoundationsException(errStr, e);
		}

		// only proceed with further initialization if no exceptions were found creating Transformer
		if (transformerInitException == null) {
			// Setup transformer properties
			Properties oprops = new Properties();
			oprops.put(OutputKeys.METHOD, "xml");
			transformer.setOutputProperties(oprops);

			// Log implementation of DOM parser class 
			if (logger.isDebugEnabled()) {
				logger.debug("Using Transformer implementation: " + transformer.getClass().getName());
			}
		}
		// end: initialize Transformer
	}


	/**
	 * Method checkDocumentBuilder.
	 * Checks if DocumentBuilder initialization was successfull. DocumentBuilder was initialized during
	 * class loading sequence. If exception occured while initializing DocumentBuilder log them here 
	 * and rethrow that exception. 
	 * @throws FoundationsException
	 */
	private static void checkDocumentBuilder() throws FoundationsException {
		if (builder == null) {
			logger.error(documentBuilderInitExceptionLogString);
			throw documentBuilderInitException;
		};
	}

	/**
	 * Method checkTransformer.
	 * Checks if Transformer initialization was successfull. Transformer was initialized during
	 * class loading sequence. If exception occured while initializing Transformer log them here 
	 * and rethrow that exception. 
	 * @throws FoundationsException
	 */
	private static void checkTransformer() throws FoundationsException {
		if (transformer == null) {
			logger.error(transformerInitExceptionLogString);
			throw transformerInitException;
		};
	}

	/**
	 * Method ConvertXmlToStr.
	 * <br> Converts a DOM document to a string
	 * @param doc
	 * @return String
	 * @throws FoundationsException 
	 */
	public static String convertXmlToStr(Document doc) throws FoundationsException {

		checkTransformer();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			synchronized (transformer) {
				transformer.transform(new DOMSource(doc), new StreamResult(os));
			}
		} catch (TransformerException e) {
			String errStr = "Exception occured transforming DOM to String.";
			if (logger.isEnabledFor(Priority.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw new FoundationsException(errStr, e);
		}
		return new String(os.toByteArray());
	}

	//	public static String convertXmlToStr(Document doc)
	//		throws FoundationsException {
	//	ByteArrayOutputStream os = new ByteArrayOutputStream();
	//	OutputFormat outputFormat = new OutputFormat(doc);
	//	outputFormat.setPreserveSpace(false);
	//	outputFormat.setIndenting(true);
	//	outputFormat.setIndent(1);
	//	outputFormat.setLineWidth(200);
	//	XMLSerializer serializer = new XMLSerializer(os, outputFormat);
	//	try {
	//		serializer.serialize(doc);
	//	} catch (IOException e) {
	//		String errStr =
	//			"Exception occured trying to serialize Document to ByteArrayOutputStream.";
	//
	//		if(logger.isEnabledFor(Priority.ERROR)){
	//			logger.error((new StringBuffer())
	//				.append(errStr)
	//				.append("\n\t")
	//				.append(ExceptionUtils.getFullStackTrace(e))
	//				.toString());
	//		}
	//
	//		throw new FoundationsException(errStr, e);
	//	}
	//	return new String(os.toByteArray());
	// }

	/**
	 * Method getNode.
	 * <br> Returns the first node at the specified XPath location
	 * <br> Example: This example returns the Customer/Address node in the specified document object
	 * <br>    Node address = XmlUtilities.getNode("/Customer/Address", custDoc);
	 * 
	 * @param xpathStr - XPath str
	 * @param node - Node to search
	 * @return Node - first node found
	 */
	public static Node getNode(String xpathStr, Node node) {
		try {
			XPath xpath = new DOMXPath(xpathStr);
			// selectNodes was used instead of selectSingleNode because of these tests
			// (http://dom4j.org/benchmarks/xpath/index.html).
			// Eventually we will be using dom4j.
			List list = xpath.selectNodes(node);
			if (logger.isDebugEnabled()) {
				logger.debug(
					(new StringBuffer())
						.append("XPath ")
						.append(xpathStr)
						.append(" returned  ")
						.append(list.size())
						.append("nodes.")
						.toString());
			}
			if (list.size() == 0) {
				return null;
			} else {
				return (Node) list.get(0);
			}
		} catch (JaxenException e) {
			if (logger.isEnabledFor(Priority.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append("Exception occured.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}

			return null;
		}
	}

	/**
	 * Method getAttr.
	 * <br>Returns the Attribute with the given attrName at node
	 * <br>Example<br>
	 * This example returns the 'state' attribute from the address node:
	 *      Attr state = getAttr(address,"state")
	 * @param node - Node to search
	 * @param attrName - Name of the attribute to find
	 * @return Attr - Attribute found
	 */
	public static Attr getAttr(Node node, String attrName) {
		NamedNodeMap attrs = node.getAttributes();
		return (Attr) attrs.getNamedItem(attrName);
	}

	/**
	 * Method getDocumentForXmlString.
	 * <br>Returns an XML DOM Document for the specified String
	 * <br>Example:<br>
	 * This example creates a customer XML document for a serialized customer
	 * <br>		Document cust = XmlUtilities.getDocumentForXmlString(custXmlString);
	 * @param xml - serialized XML document
	 * @return Document - XML document
	 * @throws FoundationsException 
	 */
	public static Document getDocumentForXmlString(String xml) throws FoundationsException {
		return getDocumentForXmlInputStream(new ByteArrayInputStream(xml.getBytes()));

	}

	/**
	 * Method getDocumentForXmlInputStream.
	 * <br>Returns an XML DOM Document for the specified InputStream
	 * <br>Example:<br>
	 * This example creates a customer XML document from an input stream
	 * <br>		Document cust = XmlUtilities.getDocumentForXmlInputStream(custStream);
	 * @param xml - input stream to processed
	 * @return Document - XML document
	 * @throws FoundationsException 
	 */
	public static Document getDocumentForXmlInputStream(InputStream xml) throws FoundationsException {

		checkDocumentBuilder();

		// Output debug info
		if (logger.isDebugEnabled()) {
			logger.debug(
				(new StringBuffer())
					.append("Using DocumentBuilderFactory factory ")
					.append(documentBuilderFactory)
					.append("; and DocumentBuilder builder ")
					.append(builder)
					.append("; InputStream xml = ")
					.append(xml)
					.toString());
		}

		Document returnDoc = null;
		try {
			synchronized (builder) {
				returnDoc = builder.parse(xml);
			}
		} catch (SAXException e) {
			String errStr = "Can not parse xml into a Document - SAXException.";

			if (logger.isEnabledFor(Priority.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw new FoundationsException(errStr, e);
		} catch (IOException e) {
			String errStr = "Can not parse xml into a Document - IOException.";
			if (logger.isEnabledFor(Priority.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw new FoundationsException(errStr, e);
		}
		return returnDoc;

	}

}
