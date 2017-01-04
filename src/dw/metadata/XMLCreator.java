package dw.metadata;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 



import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dw.cadmdm.Attribute;
import dw.cadmdm.ExportedKey;
import dw.cadmdm.ForeignKey;
import dw.cadmdm.Graph;
import dw.cadmdm.Table;

public class XMLCreator {
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	
	public XMLCreator() throws ParserConfigurationException{
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
	}
	
	public void saveOLTPModel(Graph g,int state, String path) throws ParserConfigurationException, TransformerException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// root elements
		doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("Model");
		doc.appendChild(rootElement);
		Attr attr = doc.createAttribute("type");
		attr.setValue("OLTP");
		Attr attr2 = doc.createAttribute("state");
		attr2.setValue(String.valueOf(state));
		Attr attr3 = doc.createAttribute("name");
		attr3.setValue(g.getName());
		rootElement.setAttributeNode(attr);
		rootElement.setAttributeNode(attr2);
		rootElement.setAttributeNode(attr3);

		// Tables
		Element Tables = doc.createElement("Tables");
		rootElement.appendChild(Tables);
		
		//Each Table...
		for(Table t : g.getIdDictionary().values()){
			Element Table = doc.createElement("Table");
			Tables.appendChild(Table);
			
			Attr tableopname = doc.createAttribute("Name");
			tableopname.setValue(t.getOperationalName());
			Table.setAttributeNode(tableopname);
			
			Attr tablealias = doc.createAttribute("AliasName");
			tablealias.setValue(t.getAlias());
			Table.setAttributeNode(tablealias);
			
			Attr classification = doc.createAttribute("classification");
			classification.setValue(t.getClassification());
			Table.setAttributeNode(classification);
			
			//attribute nodes
			Element Columns = doc.createElement("Columns");
			Table.appendChild(Columns);
			for(Attribute a : t.getAttributes()){
				Element column = doc.createElement("Column");
				Columns.appendChild(column);
				
				Attr opname = doc.createAttribute("Name");
				opname.setValue(a.getOperationalName());
				column.setAttributeNode(opname);
				
				Attr alias = doc.createAttribute("AliasName");
				alias.setValue(a.getAlias());
				column.setAttributeNode(alias);
				
				Attr type = doc.createAttribute("Type");
				type.setValue(a.getType());
				column.setAttributeNode(type);
				
				Attr size = doc.createAttribute("Size");
				size.setValue(String.valueOf(a.getSize()));
				column.setAttributeNode(size);
				
				Attr src = doc.createAttribute("Source");
				src.setValue(a.fromTable());
				column.setAttributeNode(src);
				
			}
			
			//primary key nodes
			Element pks = doc.createElement("PrimaryKeys");
			Table.appendChild(pks);
			for(Attribute a : t.getPrimaryKeys()){
				Element key = doc.createElement("PrimaryKey");
				pks.appendChild(key);
				
				Attr opname = doc.createAttribute("Name");
				opname.setValue(a.getOperationalName());
				key.setAttributeNode(opname);
				
				Attr alias = doc.createAttribute("AliasName");
				alias.setValue(a.getAlias());
				key.setAttributeNode(alias);
				
				Attr type = doc.createAttribute("Type");
				type.setValue(a.getType());
				key.setAttributeNode(type);
				
				Attr size = doc.createAttribute("Size");
				size.setValue(String.valueOf(a.getSize()));
				key.setAttributeNode(size);
				
				Attr src = doc.createAttribute("Source");
				src.setValue(a.fromTable());
				key.setAttributeNode(src);
				
			}
			

			//foreign key nodes
			Element ForeignKeys = doc.createElement("ForeignKeys");
			Table.appendChild(ForeignKeys);
			for(ForeignKey fk : t.getAllforeignKeys()){
				Element foreignkey = doc.createElement("ForeignKey");
				ForeignKeys.appendChild(foreignkey);
				
				Attr opname = doc.createAttribute("Name");
				opname.setValue(fk.getOperationalName());
				foreignkey.setAttributeNode(opname);
				
				Attr alias = doc.createAttribute("AliasName");
				alias.setValue(fk.getAlias());
				foreignkey.setAttributeNode(alias);
				
				Attr type = doc.createAttribute("Type");
				type.setValue(fk.getType());
				foreignkey.setAttributeNode(type);
				
				Attr size = doc.createAttribute("Size");
				size.setValue(String.valueOf(fk.getSize()));
				foreignkey.setAttributeNode(size);
				
				Attr refTable = doc.createAttribute("ReferenceTable");
				refTable.setValue(fk.getRefTable());
				foreignkey.setAttributeNode(refTable);
				
				Attr refTableAttr = doc.createAttribute("ReferenceTableAttribute");
				refTableAttr.setValue(fk.getRefTableAttribute());
				foreignkey.setAttributeNode(refTableAttr);
			}
			
			
			//exported key nodes
			Element ExportedKeys = doc.createElement("ExportedKeys");
			Table.appendChild(ExportedKeys);
			for(ExportedKey ek : t.getExportedKeys()){
				Element exportedkey = doc.createElement("ExportedKey");
				ExportedKeys.appendChild(exportedkey);
				
				Attr destTable = doc.createAttribute("DestinationTable");
				destTable.setValue(ek.getDestTable());
				exportedkey.setAttributeNode(destTable);
				
				Attr destTableFK = doc.createAttribute("DestinationTableFK");
				destTableFK.setValue(ek.getDestTableAttribute());
				exportedkey.setAttributeNode(destTableFK);
			}
			
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
		
	}
	
	
	public void saveMDModel(Graph g,int state, String path) throws ParserConfigurationException, TransformerException{
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// root elements
		doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("Model");
		doc.appendChild(rootElement);
		Attr attr = doc.createAttribute("type");
		attr.setValue("MDM");
		Attr attr2 = doc.createAttribute("state");
		attr2.setValue(String.valueOf(state));
		Attr attr3 = doc.createAttribute("name");
		attr3.setValue(g.getName());
		rootElement.setAttributeNode(attr);
		rootElement.setAttributeNode(attr2);
		rootElement.setAttributeNode(attr3);
		
		// Dimension Tables
		Element Dimensions = doc.createElement("Dimensions");
		rootElement.appendChild(Dimensions);
		
		//Fact Tables
		Element FactTables = doc.createElement("FactTables");
		rootElement.appendChild(FactTables);
		
		//Each Table...
		for(Table t : g.getIdDictionary().values()){
			
			if(t.isDimension()){
				
				Element Dimension = doc.createElement("Dimension");
				Dimensions.appendChild(Dimension);
				
				Attr tableName = doc.createAttribute("DimensionName");
				tableName.setValue(t.getName());
				Dimension.setAttributeNode(tableName);
				
				Element Columns = doc.createElement("Levels");
				Dimension.appendChild(Columns);
				for(Attribute a:t.getAttributes())
					saveAttribute(a,Columns);
				
				Element PrimaryKeys = doc.createElement("PrimaryKeys");
				Dimension.appendChild(PrimaryKeys);
				for(Attribute a : t.getPrimaryKeys())
					savePrimaryKey(a,PrimaryKeys);
				
			}
			
			else if(t.isFactTable()){
				
				Element FactTable = doc.createElement("FactTable");
				FactTables.appendChild(FactTable);
				
				Attr tableName = doc.createAttribute("FactTableName");
				tableName.setValue(t.getName());
				FactTable.setAttributeNode(tableName);
				
				Element measures = doc.createElement("Measures");
				FactTable.appendChild(measures);
				
				
				for(Attribute a:t.getAttributes())
					if(a.isNumeric())
						saveMeasure(a,measures);
				
				Element grain = doc.createElement("Grain");
				FactTable.appendChild(grain);
				
				for(ForeignKey fk : t.getAllforeignKeys()){
					Element lvl= doc.createElement("Level");
					grain.appendChild(lvl);
					
					Attr ref = doc.createAttribute("LevelID");
					ref.setValue(fk.getRefTableAttribute());
					lvl.setAttributeNode(ref);
				}
				
				Element PrimaryKeys = doc.createElement("PrimaryKeys");
				FactTable.appendChild(PrimaryKeys);
				for(Attribute pk : t.getPrimaryKeys()){
					savePrimaryKey(pk,PrimaryKeys);
				}
				
			}
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
		
	}

	private void saveMeasure(Attribute a, Element parent) {
		Element column = doc.createElement("Measure");
		parent.appendChild(column);
		
		Attr opname = doc.createAttribute("MeasureName");
		opname.setValue(a.getOperationalName());
		column.setAttributeNode(opname);
		
		Attr exp = doc.createAttribute("Expression");
		exp.setValue(a.getExpression());
		column.setAttributeNode(exp);
		
		Attr aggFun = doc.createAttribute("AggregationFunction");
		aggFun.setValue(a.getAggregateFunction());
		column.setAttributeNode(aggFun);
		
		Attr type = doc.createAttribute("Type");
		type.setValue(a.getType());
		column.setAttributeNode(type);
		
	}

	private void savePrimaryKey(Attribute a, Element parent) {
		Element key = doc.createElement("PrimaryKey");
		parent.appendChild(key);
		
		Attr opname = doc.createAttribute("ColumnName");
		opname.setValue(a.getOperationalName());
		key.setAttributeNode(opname);
		
		Attr alias = doc.createAttribute("AliasName");
		alias.setValue(a.getAlias());
		key.setAttributeNode(alias);
		
		Attr type = doc.createAttribute("Type");
		type.setValue(a.getType());
		key.setAttributeNode(type);
		
	}

	private void saveAttribute(Attribute a, Element parent) {
		Element column = doc.createElement("Level");
		parent.appendChild(column);
		
		Attr opname = doc.createAttribute("ColumnName");
		opname.setValue(a.getOperationalName());
		column.setAttributeNode(opname);
		
		Attr alias = doc.createAttribute("AliasName");
		alias.setValue(a.getAlias());
		column.setAttributeNode(alias);
		
		Attr type = doc.createAttribute("Type");
		type.setValue(a.getType());
		column.setAttributeNode(type);
		
		Attr src = doc.createAttribute("SourceTable");
		src.setValue(a.fromTable());
		column.setAttributeNode(src);
		
		/**Attr cpath = doc.createAttribute("ColapsePath");
		cpath.setValue(a.getPath());
		column.setAttributeNode(cpath);**/
	}

	
}
