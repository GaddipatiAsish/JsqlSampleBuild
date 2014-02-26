package com.Jsqlbuildsample;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

public class SelectStatementEvaluation implements SelectVisitor {
	/*
	 * 1. Implement SelectVistor to start visiting the components of a Select Query
	 * 2. Implement FromItemVistor to get the table names;
	 * 3. Implement ExpressionVisitor Where ever expressions are used.
	 */
	/********************************************************************************************************************
	 * This is a constructor form which the execution starts.
	 */
	FromItem tableName;
	List<SelectExpressionItem> columnList;
	List<Column> groupByColumns;
	Expression whereExp;
	HashMap<String, String> tableSchema= new HashMap<String, String>();
	HashMap<String, Integer> schemaIndex = new HashMap<String, Integer>();
	
	public SelectStatementEvaluation(Select selectStmt,HashMap<String, String> tableSchema, HashMap<String, Integer> schemaIndex) {
		System.out.println("====================================");
		System.out.println("Started reading Hirearchy of Class Objects into Strings to display");
		this.tableSchema=tableSchema;
		this.schemaIndex=schemaIndex;
		selectStmt.getSelectBody().accept(this);
		//System.out.println("IN SELECT EVAL CLASS"+tableSchema);
		
		
	}
	
	/********************************************************************************************************************
	 * All these methods came because of implementing SelectVisitor Interface
	 */
	@Override
	public void visit(PlainSelect plainSelect) {
		// TODO Auto-generated method stub
		/*
		 * This method is used indirectly to display get the table name form the Query and this points to "visit(Table tabName)"
		 */
		System.out.println("IN SELECT EVAL CLASS"+ tableSchema);
		System.out.println("====================================");
		System.out.println("Trying to get Table Name");
		System.out.println("....................................");
		tableName=plainSelect.getFromItem(); // FromItemVisitor is added because this gives methods to get table names
		System.out.println("====================================");
		System.out.println("Where Clause expression Break Up");
		System.out.println("....................................");
		whereExp=plainSelect.getWhere();  // ExpressionVisitor is added because where clause contains expressions.
		System.out.println("====================================");
		System.out.println("====================================");
		System.out.println("Trying to get Columns and Expressions in the Select Statement and react accordingly");
		System.out.println("....................................");
		columnList= plainSelect.getSelectItems();
		System.out.println("SELECT LIST IS : "+columnList);
		//System.out.println("IN SELECT CLASS"+tableSchema);
		
		groupByColumns=plainSelect.getGroupByColumnReferences();
		
		
		System.out.println(groupByColumns);
		
		SelectionOperator scanOperator= new SelectionOperator(tableName, columnList, whereExp,tableSchema,schemaIndex,groupByColumns);
		scanOperator.readCompleteTable(new File("C:\\Users\\GVRao\\Downloads\\datasets\\datasets\\personInfo\\personInfo.dat")); //Dynamic FIle Name Passing
		
		
	}

	@Override
	public void visit(Union arg0) {
		// TODO Auto-generated method stub
		
	}
	
}