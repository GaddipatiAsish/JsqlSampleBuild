package com.Jsqlbuildsample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.print.attribute.standard.SheetCollate;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class StartHereMainClass{
	public static void main(String args[]) throws FileNotFoundException, ParseException {

		/*
		 * Read the SQL Queries from the file and identify what type of Query it
		 * is and react accordingly
		 */
		
		 HashMap<String, String> tableSchema= new HashMap<String, String>();
		 HashMap<String, Integer> schemaIndex = new HashMap<String, Integer>();
		
		
		File dir= new File("C:\\Users\\GVRao\\Desktop\\DB Input\\");
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			System.out.println("FILE NAME:"+ files[i]);
			BufferedReader stream = new BufferedReader(new FileReader(files[i]));
			CCJSqlParser parser= new CCJSqlParser(stream);
			Statement stmt = (Statement) parser.Statement();
			
			
			/* checking here what type of statement it is */
			
			if(stmt instanceof CreateTable){
				System.out.println("Create Statement Encountered");
				CreateTable crtTable = (CreateTable) stmt;
				CreateTableStatementEvaluation objForPlainSelectEval= new CreateTableStatementEvaluation(crtTable);
				tableSchema=objForPlainSelectEval.getTableSchema();
				schemaIndex=objForPlainSelectEval.getSchemaIndex();
			}else if(stmt instanceof Select){
				System.out.println("Select Statement Encountered!");
				/* Select statement is Encountered! Now make use of this tokens to run on my data File */
				System.out.println(stmt.toString());
				Select selectStmt = (Select) stmt;
				System.out.println("IN MAIN CLASS"+ tableSchema);
					SelectStatementEvaluation objForPlainSelectEval= new SelectStatementEvaluation(selectStmt,tableSchema,schemaIndex);
			}
		}
		
	}

	
}
