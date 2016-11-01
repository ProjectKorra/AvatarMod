package com.crowsofwar.gorecore.tree;

public class TreeCommandException extends RuntimeException {
	
	private final String reason;
	private final Object[] format;
	
	public TreeCommandException(Reason reason, Object... format) {
		this(reason.getMessage(), format);
	}
	
	public TreeCommandException(String message, Object... format) {
		super(message);
		this.reason = message;
		this.format = format;
	}
	
	public String getMessage() {
		return reason;
	}
	
	public Object[] getFormattingArgs() {
		return format;
	}
	
	public static enum Reason {
		ARGUMENT_MISSING("gc.tree.error.missingArgs"),
		NO_BRANCH_NODE("gc.tree.error.noBranchNode"),
		CANT_CONVERT("gc.tree.error.cantConvert"),
		NO_PERMISSION("gc.tree.error.needsOp"),
		NOT_OPTION("gc.tree.error.notOption");
		
		private final String message;
		
		private Reason(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	
}
