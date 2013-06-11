package org.openlegacy.designtime.rpc.generators.jt400;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "pcml")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pcml {

	private List<Structure> structures;
	private List<Program> programs;

	public List<Structure> getStructures() {
		return structures;
	}

	public void setStructures(List<Structure> structures) {
		this.structures = structures;
	}

	public List<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}
}
