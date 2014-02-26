package com.Jsqlbuildsample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectionOperator {

	File inputFilePath;
	String tuple;
	FromItem TableName;
	List<SelectExpressionItem> columnList;
	List<Column> groupByColumns;
	HashMap<Object, Integer> gByCTemp = new HashMap<Object, Integer>();
	Expression whereExp;
	List<String> outputTuples;
	HashMap<String, String> tableSchema = new HashMap<String, String>();
	HashMap<String, Integer> schemaIndex = new HashMap<String, Integer>();
	float sumValueforAVG = 0;
	float countValueforAVG = 0;
	int countgBy = 0;

	public SelectionOperator(FromItem TableName,
			List<SelectExpressionItem> columnList, Expression whereExp,
			HashMap<String, String> tableSchema,
			HashMap<String, Integer> schemaIndex, List<Column> groupByColumns) {
		System.out.println("In Selection Operator");
		this.columnList = columnList;
		this.TableName = TableName;
		this.whereExp = whereExp;
		this.schemaIndex = schemaIndex;
		this.tableSchema = tableSchema;
		this.groupByColumns = groupByColumns;
	}

	void readCompleteTable(File inputFilePath) {
		Set<String> sortedRows = new LinkedHashSet<String>();
		String addRow = new String();

		try {

			BufferedReader inputDataRead = new BufferedReader(new FileReader(
					inputFilePath));
			BufferedReader inputDataRead2 = new BufferedReader(new FileReader(
					inputFilePath));
			/*
			 * This prints only if multiple/single tuple is returned after
			 * select query processing
			 */
			if (whereExp == null) {// GROUP BY EXP PROCESSING STARTS HERE
				while ((tuple = inputDataRead.readLine()) != null) {
					gbyList(tuple);
				}
				// System.out.println(gByCTemp); // displaying group by list

				while ((tuple = inputDataRead2.readLine()) != null) {
					evaluateGroupByClause(tuple);
				}
				System.out.println(gByCTemp);

			} else { // WHERE EXP PROCESSING STARTS HERE
				while ((tuple = inputDataRead.readLine()) != null) {
					sortedRows.add(evaluateWhereClause(tuple));

				}
				Iterator<String> iter = sortedRows.iterator();
				while (iter.hasNext()) {
					System.out.println(iter.next());
				}
			}

			/*
			 * This prints only if there is a group by
			 */
			if (whereExp == null) {
				Set<Object> s = gByCTemp.keySet();
				Iterator<Object> i = s.iterator();

				while (i.hasNext()) {
					Object key = i.next();
					if (((SelectExpressionItem) columnList.get(0))
							.getExpression() instanceof Function)
						System.out.println(gByCTemp.get(key) + "|" + key);
					else {
						System.out.println(key + "|" + gByCTemp.get(key));
					}
				}

			}

			/*
			 * This prints only if an avg comes in select statement
			 */
			for (int i = 0; i < columnList.size(); i++) {
				if (((SelectExpressionItem) columnList.get(i)).getExpression() instanceof Function)

					if (((Function) ((SelectExpressionItem) columnList.get(i))
							.getExpression()).getName().equals("AVG")) {
						System.out.println(sumValueforAVG / countValueforAVG);
					}

			}
			inputDataRead.close();
			inputDataRead2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private void gbyList(String tuple2) {
		// TODO Auto-generated method stub
		String tableData[] = tuple2.split("\\|");
		gByCTemp.put(
				tableData[schemaIndex.get(groupByColumns.get(0).getColumnName()
						.trim())], 0);

		
	}

	private void evaluateGroupByClause(String row) {
		/*
		 * GROUP BY EXPRESSION LOGIC STARTS HERE!!
		 */
		

		String output = new String();
		String tableData[] = row.split("\\|");
		Set<String> setForCount = new LinkedHashSet<String>();
		for (int i = 0; i < columnList.size(); i++) {
			if (columnList.get(i).getExpression() instanceof Function) {

				if (((Function) columnList.get(i).getExpression()).getName()
						.equalsIgnoreCase("COUNT")) {

					Set<Object> s = gByCTemp.keySet();
					Iterator<Object> it = s.iterator();
					while (it.hasNext()) {
						if (it.next().equals(
								tableData[schemaIndex.get(groupByColumns.get(0)
										.getColumnName().toString())])) {

							int prevValue = gByCTemp
									.get(tableData[schemaIndex
											.get(groupByColumns.get(0)
													.getColumnName())]);

							gByCTemp.put(
									tableData[schemaIndex.get(groupByColumns
											.get(0).getColumnName().trim())],
									++prevValue);

						}
					}

				}
			}
		}
	}

	String evaluateWhereClause(String row) {

		String output = new String();
		String tableData[] = row.split("\\|");

		/*
		 * WHERE EXPRESSION LOGIC STARTS HERE!!
		 */

		if (whereExp instanceof AndExpression) {

			Expression leftExp = ((AndExpression) whereExp).getLeftExpression();
			Expression rightExp = ((AndExpression) whereExp)
					.getRightExpression();

			if (evaluateExpr(leftExp, row) && evaluateExpr(rightExp, row)) {
				countValueforAVG++; // counting to calculate Average
				for (int i = 0; i < columnList.size(); i++) {
					SelectExpressionItem temp = columnList.get(i);
					if (temp.getExpression() instanceof Column) {

						Set set = schemaIndex.keySet();
						Iterator<String> iter = set.iterator();
						while (iter.hasNext()) {
							if (iter.next().contains(
									temp.getExpression().toString())) {
								output += tableData[schemaIndex.get(temp
										.getExpression().toString())] + "|";
							}
						}
					} else if (temp.getExpression() instanceof Function) {
						Function fun = (Function) temp.getExpression();
						if (fun.getName().equals("AVG")) {
							List<ExpressionList> explist = fun.getParameters()
									.getExpressions();
							// System.out.println("explist.size()"
							// +explist.size());
							for (int j = 0; j < explist.size(); j++) {

								String colu = ((Column) (Expression) explist
										.get(j)).getColumnName();

								Set set = schemaIndex.keySet();
								Iterator<String> iter = set.iterator();
								while (iter.hasNext()) {
									if (iter.next().contains(colu)) {
										sumValueforAVG += Integer
												.parseInt(tableData[schemaIndex
														.get(colu.toString())]);
									}
								}
							}
						} else {
							System.out
									.println("SOme other function like AVG has encountered");
						}
					}
				}
			}
		} else { // simple where starts here.
			if (evaluateExpr(whereExp, row)) {
				for (int i = 0; i < columnList.size(); i++) {
					SelectExpressionItem temp = columnList.get(i);
					if (temp.getExpression() instanceof Column) {

						Set set = schemaIndex.keySet();
						Iterator<String> iter = set.iterator();
						while (iter.hasNext()) {
							if (iter.next().contains(
									temp.getExpression().toString())) {
								output += tableData[schemaIndex.get(temp
										.getExpression().toString())] + "|";
							}
						}
					} else {
						System.out
								.println("Expression Encountered!!!!!!!!!!!!!!!!!!!!!!!!!");
					}
				}
			}

		}
		return output;
	}

	boolean evaluateExpr(Expression exp, String row) {
		boolean flag = false;
		String col = new String();
		String tableDataCheck[] = row.split("\\|");

		if (exp instanceof GreaterThan) {

			if (((GreaterThan) exp).getLeftExpression() instanceof Column) {
				col = ((GreaterThan) exp).getLeftExpression().toString();
			}

			if (tableSchema.get(col).equals("int")) {
				int valQuery = Integer.parseInt(((GreaterThan) exp)
						.getRightExpression().toString());
				int valTable = Integer.parseInt(tableDataCheck[schemaIndex
						.get(col)]);
				if (valQuery < valTable) {
					return true;
				} else {
					return false;
				}
			}

		} else if (exp instanceof EqualsTo) {

			if (((EqualsTo) exp).getLeftExpression() instanceof Column) {
				col = ((EqualsTo) exp).getLeftExpression().toString();
			}

			if (tableSchema.get(col).equals("int")) {
				int valQuery = Integer.parseInt(((GreaterThan) exp)
						.getRightExpression().toString());
				int valTable = Integer.parseInt(tableDataCheck[schemaIndex
						.get(col)]);
				if (valQuery < valTable) {
					return true;
				} else {
					return false;
				}
			} else if (tableSchema.get(col).equals("string")) {
				String valQuery = ((EqualsTo) exp).getRightExpression()
						.toString();
				valQuery = valQuery.substring(1, valQuery.length() - 1);
				String valTable = tableDataCheck[schemaIndex.get(col)];
				if (valQuery.equals(valTable)) {
					return true;
				} else {
					return false;
				}
			}

		} else if (exp instanceof GreaterThanEquals) {

			if (((GreaterThanEquals) exp).getLeftExpression() instanceof Column) {
				col = ((GreaterThanEquals) exp).getLeftExpression().toString();
			}
			if (tableSchema.get(col).equals("int")) {
				int valQuery = Integer.parseInt(((GreaterThanEquals) exp)
						.getRightExpression().toString());
				int valTable = Integer.parseInt(tableDataCheck[schemaIndex
						.get(col)]);
				if (valQuery <= valTable) {
					return true;
				} else {
					return false;
				}
			}
		}

		return flag;
	}

}
