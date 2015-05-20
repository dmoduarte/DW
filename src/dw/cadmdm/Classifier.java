package dw.cadmdm;

import java.util.Iterator;

public class Classifier {
	
	//Default config
	public final double MANYFKPERCENTAGE = 100;//dos atributos foreign key 100 % sao de relaçoes 1:N
	public final double MEASURESPERCENTAGE = 100;//dos atributos que nao sao keys 100% sao numericos
	
	//heuristis
	private double percFK; //no unique constraints
	private double percMeasure;
	
	public Classifier(){
		this.percFK = MANYFKPERCENTAGE;
		this.percMeasure = MEASURESPERCENTAGE;
	}
	
	public void classify(Graph model){
		System.out.println("ssss");
		//Iterator<Table> it = model.getIdDictionary().values().iterator();
		//while(it.hasNext()){
		System.out.println(model.getIdDictionary().size());
		for(int i = 0; i<model.getIdDictionary().size(); i++){	
			//Table t = it.next();
			Table t = model.getIdDictionary().get(i);
			System.out.println("yes");
			if(t.getAllforeignKeys().size() > 0){
				
				if(t.hasNumeric()){
					
					if(t.hasDateTypes() || isRelatedWithDate(t) )
						t.setToTransaction();
					else
						t.setToComponent();
						
				}else
					t.setToComponent();
				
			}
			else //N importa fk's, logo e' um classificador
				t.setToClassifier();
		}
	}

	private boolean isRelatedWithDate(Table t) {
		return false;
	}
	
}
