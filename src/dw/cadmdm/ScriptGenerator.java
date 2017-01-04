package dw.cadmdm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

public class ScriptGenerator {
	
	//private static final int MYSQLMAXCHAR = 64;
	
	private String script;
	
	public ScriptGenerator(){
		script = "";
	}
	
	public void generateScript(Graph g,String path) throws IOException{
		
		List<Table> factTables = g.factTables();
		List<Table> dimensionTables = g.dimensionTables();
		
		for(Table t : dimensionTables)
			generate(t);
		
		for(Table t : factTables)
			generate(t);
		
		File newf = new File(path);	
		newf.delete();
		RandomAccessFile raf = new RandomAccessFile(newf,"rw");
		raf.write(script.getBytes());
		raf.close();
		System.out.println("Script Generated!");
	}

	private void generate(Table t) {
		
		script += "\nCREATE TABLE `"+t.getName()+"` (\n";
		
		Iterator<Attribute> attrIt = t.getAllColumns().iterator();
		int c  = 0;
		while(attrIt.hasNext()){
			
			if(c > 0)
				script += ",\n";
			
			Attribute a = attrIt.next();
			
			String type = a.getType()+" ";
			if(a.isVariable()){
			
				String[] ty = a.getType().split(" ");
				ty[0] = ty[0] + "("+a.getSize()+")";
				
				StringBuilder builder = new StringBuilder();
				for(String s : ty) {
				    builder.append(s+" ");
				}
				
				type = builder.toString();
				
			}
			
			String name = a.getColumnName();
		
			script += "`"+name+"`"+type+"NOT NULL";
			c++;
		}
		
		if(!t.getPrimaryKeys().isEmpty()){
			script += ",";
		
			Iterator<Attribute> pkIt = t.getPrimaryKeys().iterator();
			c = 0;
			script += "\nPRIMARY KEY (";
			while(pkIt.hasNext()){
				
				if(c > 0)
					script += ",";
				
				Attribute pk = pkIt.next();
				String name = pk.getColumnName();
				
				script += "`"+name+"`";
				c++;
			}
			script += ")";
		}
		
		/*Iterator<ForeignKey> fkIt = t.getAllforeignKeys().iterator();
		while(fkIt.hasNext()){
			
			script += ",\nFOREIGN KEY (";
			
			ForeignKey fkey = fkIt.next();
			
			String name = fkey.getColumnName();
			
			if(name.toCharArray().length > MYSQLMAXCHAR)
				name = fkey.getOperationalName()+"_"+c;
			
			script += "`"+name+"`) REFERENCES `"+fkey.getColapsedRefTable()+"` (`"+fkey.getRefTableAttribute()+"`)";
		}*/
		
		script += "\n) ENGINE = InnoDB DEFAULT CHARSET=latin1;\n";
	
	}
		
	
}
