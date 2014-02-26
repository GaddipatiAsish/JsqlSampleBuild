package com.Jsqlbuildsample;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.create.table.CreateTable;

public class CreateTableStatementEvaluation {
	
HashMap<String, String> tableSchema = new HashMap<String, String>();
HashMap<String, Integer> schemaIndex = new HashMap<String, Integer>();
List colsAndDatatpes;

	public CreateTableStatementEvaluation(CreateTable crtTable) {
		// TODO Auto-generated constructor stub
		
		colsAndDatatpes=crtTable.getColumnDefinitions();		
		System.out.println(colsAndDatatpes);
		Iterator iter= colsAndDatatpes.iterator();
		int index=0;
		while(iter.hasNext()){
		String temp[]= iter.next().toString().split("\\s");
		tableSchema.put(temp[0], temp[1]);
		schemaIndex.put(temp[0], index++);
		}
		System.out.println(tableSchema);
		System.out.println(schemaIndex);
	}

	HashMap<String,String> getTableSchema(){
		return tableSchema;
	}
	
	HashMap<String,Integer> getSchemaIndex(){
		return schemaIndex;
	}
}
