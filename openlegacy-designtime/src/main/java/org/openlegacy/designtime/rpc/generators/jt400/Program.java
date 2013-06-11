package org.openlegacy.designtime.rpc.generators.jt400;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "program")
@XmlAccessorType(XmlAccessType.FIELD)
public class Program {

	@XmlElement
	List<Data> datas;

	public List<Data> getDatas() {
		return datas;
	}

	public void setDatas(List<Data> datas) {
		this.datas = datas;
	}
}
