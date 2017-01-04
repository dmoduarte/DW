package dw.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import dw.cadmdm.Attribute;
import dw.cadmdm.ExportedKey;
import dw.cadmdm.ForeignKey;
import dw.cadmdm.Table;

public class XMLLoader {
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	public String modelName;
	
	public XMLLoader() throws ParserConfigurationException{
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
	}
	
	
	
	public int LoadOLTPModel(String path, List<Table> bucket) throws SAXException, IOException{
		File metadata = new File(path);
		doc = docBuilder.parse(metadata);
		doc.getDocumentElement().normalize();
		
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		Element model = (Element)doc.getElementsByTagName("Model").item(0);
		int state = Integer.parseInt(model.getAttribute("state"));
		modelName = model.getAttribute("name");
		
		NodeList nList = doc.getElementsByTagName("Table");
		
		for (int temp = 0; temp < nList.getLength(); temp++) { 
			Node nNode = nList.item(temp);
			 
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				Node columns = eElement.getElementsByTagName("Columns").item(0);
				List<Attribute> attrs =  loadAttributes(columns);
				Node pks = eElement.getElementsByTagName("PrimaryKeys").item(0);
				List<Attribute> pkeys = loadPrimaryKeys(pks);
				Node fks = eElement.getElementsByTagName("ForeignKeys").item(0);
				List<ForeignKey> fkeys = loadForeignKeys(fks);
				Node eks = eElement.getElementsByTagName("ExportedKeys").item(0);
				List<ExportedKey> ekeys = loadExportedKeys(eks);
				
				String TableName = eElement.getAttribute("Name");
				String ALTableName = eElement.getAttribute("AliasName");
				System.out.println("Finished Loading for table: "+TableName);
				String classification = eElement.getAttribute("classification");
				
				Table t = new Table(pkeys,attrs,fkeys,ekeys,TableName,temp);
				t.setAlias(ALTableName);
				if(classification.equalsIgnoreCase(t.TRANS))
					t.setToTransaction();
				else if (classification.equalsIgnoreCase(t.CLASS))
					t.setToClassifier();
				else if (classification.equalsIgnoreCase(t.COMP))
					t.setToComponent();
				
				bucket.add(t);
			}
			
		}
		
		return state;
	}
	
	private List<ExportedKey> loadExportedKeys(Node eks) {
		List<ExportedKey> result = new ArrayList<ExportedKey>();
		
		NodeList nodes = ((Element)eks).getElementsByTagName("ExportedKey");
		
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String destTable = eElement.getAttribute("DestinationTable");
				String destFK = eElement.getAttribute("DestinationTableFK");
				result.add(new ExportedKey(destTable,destFK));
			}
		}
		return result;
	}

	private List<ForeignKey> loadForeignKeys(Node fks) {
		List<ForeignKey> result = new ArrayList<ForeignKey>();
		
		NodeList nodes = ((Element)fks).getElementsByTagName("ForeignKey");
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String alias = eElement.getAttribute("AliasName");
				String opname = eElement.getAttribute("Name");
				String type = eElement.getAttribute("Type");
				String refTable = eElement.getAttribute("ReferenceTable");
				String refTableAttribute = eElement.getAttribute("ReferenceTableAttribute");
				int size = Integer.parseInt(eElement.getAttribute("Size"));
				ForeignKey fk = new ForeignKey(opname,type,refTable,refTableAttribute);
				fk.setSize(size);
				fk.setAlias(alias);
				result.add(fk);
			}
		}
		return result;
	}

	private List<Attribute> loadPrimaryKeys(Node pks) {
		List<Attribute> result = new ArrayList<Attribute>();
		
		NodeList nodes = ((Element)pks).getElementsByTagName("PrimaryKey");
		for (int temp = 0; temp < nodes.getLength(); temp++) { 
			Node nNode = nodes.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String alias = eElement.getAttribute("AliasName");
				String opname = eElement.getAttribute("Name");
				String type = eElement.getAttribute("Type");
				String source = eElement.getAttribute("Source");
				int size = Integer.parseInt(eElement.getAttribute("Size"));
				Attribute attr = new Attribute(opname,type,source,true);
				attr.setSize(size);
				attr.setAlias(alias);
				result.add(attr);
			}
		}
		return result;
	}

	private List<Attribute> loadAttributes(Node columns) {
		List<Attribute> result = new ArrayList<Attribute>();
		
		NodeList nodes = ((Element)columns).getElementsByTagName("Column");
		for (int temp = 0; temp < nodes.getLength(); temp++) { 
			Node nNode = nodes.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String alias = eElement.getAttribute("AliasName");
				String opname = eElement.getAttribute("Name");
				String type = eElement.getAttribute("Type");
				String source = eElement.getAttribute("Source");
				int size = Integer.parseInt(eElement.getAttribute("Size"));
				Attribute attr = new Attribute(opname,type,source,false);
				attr.setAlias(alias);
				attr.setSize(size);
				result.add(attr);
			}
		}
		
		return result;
	}

}
