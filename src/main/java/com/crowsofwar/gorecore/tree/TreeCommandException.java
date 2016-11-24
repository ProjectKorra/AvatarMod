/* 
  This file is part of AvatarMod.
  
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.tree;

public class TreeCommandException extends RuntimeException {
	
	private final Reason reason;
	private final Object[] format;
	
	public TreeCommandException(Reason reason, Object... format) {
		super(reason.getMessage());
		this.reason = reason;
		this.format = format;
	}
	
	public String getMessage() {
		return reason.getMessage();
	}
	
	public Object[] getFormattingArgs() {
		return format;
	}
	
	public static enum Reason {
		ARGUMENT_MISSING("gc.tree.error.missingArgs"), NO_BRANCH_NODE("gc.tree.error.noBranchNode"), CANT_CONVERT(
				"gc.tree.error.cantConvert"), NO_PERMISSION("gc.tree.error.needsOp"), NOT_OPTION("gc.tree.error.notOption");
		
		private final String message;
		
		private Reason(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	
}
