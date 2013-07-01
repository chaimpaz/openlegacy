package org.openlegacy.designtime.rpc.source.parsers;

import org.antlr.runtime.tree.CommonTree;

import java.util.ArrayList;
import java.util.List;

public class KoopaParameterStructure implements ParameterStructure {

	private int fieldLevel;
	private String fieldName;

	private String variableDeclartion;

	private List<ParameterStructure> subFieldsList = null;

	private static final char OPEN_BRACKET_SMBOL = '(';

	// private final static Log logger = LogFactory.getLog(DefaultRegistryLoader.class);
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
	public String getVariableDeclartion() {
		return variableDeclartion;
	}

	KoopaParameterStructure(CommonTree parameterNode) {

		fieldLevel = Integer.parseInt(parameterNode.getChild(0).getChild(0).getText());
		fieldName = parameterNode.getChild(1).getChild(0).getChild(0).getText();
		if (parameterNode.getChildCount() == 4) {
			variableDeclartion = genarteFlatPic((CommonTree)parameterNode.getChild(2).getChild(1));

		} else {
			subFieldsList = new ArrayList<ParameterStructure>();
		}
	}

	public void CollectSubFields(List<ParameterStructure> paramtersNodes, int startIdx) {
		int idx = startIdx + 1;
		int handleLevel = paramtersNodes.get(idx).getLevel();
		while (idx < paramtersNodes.size()) {
			KoopaParameterStructure cobolField = (KoopaParameterStructure)paramtersNodes.get(idx);
			if (cobolField.getLevel() == handleLevel) {
				subFieldsList.add(cobolField);
				paramtersNodes.remove(idx);
				if (!cobolField.isSimple()) {
					cobolField.CollectSubFields(paramtersNodes, idx);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.ParameterStructure#getSubFieldsList()
	 */
	public List<ParameterStructure> getSubFieldsList() {
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
