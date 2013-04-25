package gov.va.med.iss.mdebugger.vo;

public class ReadResultsVO {
	private Integer maxChars;
	private Integer timeout;
	private boolean starRead;
	private boolean typeAhead;

	public ReadResultsVO(Integer maxChars, Integer timeout, boolean starRead,
			boolean typeAhead) {
		super();
		this.maxChars = maxChars;
		this.timeout = timeout;
		this.starRead = starRead;
		this.typeAhead = typeAhead;
	}

	public Integer getMaxChars() {
		return maxChars;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public boolean isStarRead() {
		return starRead;
	}

	public boolean isTypeAhead() {
		return typeAhead;
	}

}
