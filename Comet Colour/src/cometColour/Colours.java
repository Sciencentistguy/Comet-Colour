package cometColour;


public enum Colours {

	A("4619652/3005", 96, 160, 185),
	B("4541376/3005", 132, 48, 63),
	C("6057986/3005", 157, 113, 78),
	D("4211098/3005", 101, 101, 101),
	E("6022035/3005", 140, 60, 123),
	F("300521/3005", 188, 6, 5),
	G("4651903/3005", 139, 97, 161),
	H("4521948/3005", 117, 155, 130),
	I("300523/3005", 36, 97, 177),
	J("300501/3005", 213, 214, 216),
	K("4255413/3005", 72, 103, 150),
	L("4211389/3005", 146, 146, 146),
	M("4173805/3005", 217, 125, 40),
	N("4179830/3005", 115, 151, 202),
	O("4220634/3005", 151, 187, 64),
	P("300524/3005", 243, 195, 1),
	Q("4113915/3005", 177, 161, 110),
	R("4211242/3005", 84, 42, 20),
	S("300526/3005", 47, 47, 47),
	T("4286050/3005", 238, 149, 197);

	private final String name;
	private final int r;
	private final int g;
	private final int b;

	Colours(String name, int r, int g, int b) {
		this.name = name;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public String getName() {
		return name;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

}
