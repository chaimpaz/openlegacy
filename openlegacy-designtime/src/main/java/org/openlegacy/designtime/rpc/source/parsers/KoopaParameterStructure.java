package org.openlegacy.designtime.rpc.source.parsers;

import org.antlr.runtime.tree.CommonTree;

import java.util.ArrayList;
import java.util.List;

import koopa.trees.antlr.jaxen.Jaxen;

public class KoopaParameterStructure implements ParameterStructure {

	private int fieldLevel;
	private String fieldName;
	private int occurs = 1;

	private String variableDeclartion;

	private List<ParameterStructure> subFieldsList = null;

	private static final char OPEN_BRACKET_SMBOL = '(';

	private static String genarteFlatPic(CommonTree picNodes) {

		StringBuilder x = new StringBuilder();
		x.append(picNodes.getChild(0).getText());
		Integer fieldLenght = x.length();
		for (int idx = 1; idx < picNodes.getChildCount(); idx++) {
			String line = picNodes.getChild(idx).getText();
			if (line.charAt(0) == OPEN_BRACKET_SMBOL) {
				int times = Integer.parseInt(picNodes.getChild(idx + 1).getText());
				char repetingChar = x.charAt(fieldLenght - 1);
				idx += 2;
				for (int i = 1; i < times; i++) {
					x.append(repetingChar);
				}
			} else {
				x.append(line);
				fieldLenght += line.length();
			}

		}
		return x.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#getvariableDeclartion()
	 */
	public String getVariableDeclaration() {
		return variableDeclartion;
	}

	@SuppressWarnings("unchecked")
	public KoopaParameterStructure(CommonTree parameterNode) {
		// TODO What are the number

		parameterNode.setParent(null);

		CommonTree tempNode = (CommonTree)Jaxen.evaluate(parameterNode, "//levelNumber").get(0);
		fieldLevel = Integer.parseInt(tempNode.getChild(0).getText());
		tempNode = (CommonTree)Jaxen.evaluate(parameterNode, "//dataName//cobolWord").get(0);
		fieldName = tempNode.getChild(0).getText();

		List<CommonTree> tempNodeList = (List<CommonTree>)Jaxen.evaluate(parameterNode, "//occurs//integer");
		if (tempNodeList.isEmpty()) {
			occurs = 1;
		} else {
			occurs = Integer.parseInt(tempNodeList.get(0).getChild(0).getText());
		}

		tempNodeList = (List<CommonTree>)Jaxen.evaluate(parameterNode, "//picture//pictureString");
		if (tempNodeList.isEmpty()) {
			subFieldsList = new ArrayList<ParameterStructure>();
		} else {
			variableDeclartion = genarteFlatPic(tempNodeList.get(0));

		}
	}

	public void collectSubFields(List<ParameterStructure> paramtersNodes, int startIdx) {
		int idx = startIdx + 1;
		int handleLevel = paramtersNodes.get(idx).getLevel();
		while (idx < paramtersNodes.size()) {
			KoopaParameterStructure cobolField = (KoopaParameterStructure)paramtersNodes.get(idx);
			if (cobolField.getLevel() == handleLevel) {
				subFieldsList.add(cobolField);
				paramtersNodes.remove(idx);
				if (!cobolField.isSimple()) {
					cobolField.collectSubFields(paramtersNodes, idx);
				}
			} else {
				return;
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#getLevel()
	 */
	public int getLevel() {
		return fieldLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#getFieldName()
	 */
	public String getFieldName() {
		return fieldName;
	}

	public int getOccurs() {
		return occurs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#getSubFieldsList()
	 */
	public List<ParameterStructure> getSubFields() {
		return subFieldsList;
	}

	// @Override
	// public String toString() {
	// String result = "";
	//
	// if (isSimple()) {
	// FieldFormater foramtDef = fieldFormatFactory.getObject(variableDeclartion);
	//
	// return "private " + foramtDef.getJavaType().getSimpleName() + " " + fieldName + ";\n";
	//
	// } else {
	// result = "public class " + fieldName + "{\n";
	// for (KoopaParmeterStructure subField : subFieldsList) {
	// result += subField.toString();
	// }
	// result += "};";
	// }
	// return result;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#isSimple()
	 */
	public boolean isSimple() {
		return (subFieldsList == null);
	}

}
