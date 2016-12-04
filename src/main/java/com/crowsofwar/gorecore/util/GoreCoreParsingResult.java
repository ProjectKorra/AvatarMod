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

package com.crowsofwar.gorecore.util;

/**
 * A list of results for generic type parsing given using
 * {@link GoreCoreParsingUtil}.
 * 
 * @author CrowsOfWar
 * @see GoreCoreParsingUtil
 */
public final class GoreCoreParsingResult {
	
	public static GoreCoreParsingResult.ResultInteger generateIntegerResult(int value, boolean successful) {
		return new ResultInteger(value, successful);
	}
	
	public static GoreCoreParsingResult.ResultFloat generateFloatResult(float value, boolean successful) {
		return new ResultFloat(value, successful);
	}
	
	public static GoreCoreParsingResult.ResultDouble generateDoubleResult(double value, boolean successful) {
		return new ResultDouble(value, successful);
	}
	
	public static GoreCoreParsingResult.ResultLong generateLongResult(long value, boolean successful) {
		return new ResultLong(value, successful);
	}
	
	public static GoreCoreParsingResult.ResultBoolean generateBooleanResult(boolean value,
			boolean successful) {
		return new ResultBoolean(value, successful);
	}
	
	private static class ResultBase<T> {
		private final T value;
		private final boolean successful;
		
		protected ResultBase(T value, boolean successful) {
			this.value = value;
			this.successful = successful;
		}
		
		/**
		 * Get the result of parsing the string. This will be the default value
		 * for the generic type if {@link #wasSuccessful() an error occured}.
		 */
		public T getResult() {
			return value;
		}
		
		/**
		 * Returns whether parsing the string was successful - that is, if the
		 * string was correct for this type.
		 */
		public boolean wasSuccessful() {
			return successful;
		}
		
	}
	
	public static class ResultInteger extends ResultBase<Integer> {
		public ResultInteger(Integer value, boolean successful) {
			super(value, successful);
		}
	}
	
	public static class ResultFloat extends ResultBase<Float> {
		public ResultFloat(Float value, boolean successful) {
			super(value, successful);
		}
	}
	
	public static class ResultDouble extends ResultBase<Double> {
		public ResultDouble(Double value, boolean successful) {
			super(value, successful);
		}
	}
	
	public static class ResultLong extends ResultBase<Long> {
		public ResultLong(Long value, boolean successful) {
			super(value, successful);
		}
	}
	
	public static class ResultBoolean extends ResultBase<Boolean> {
		public ResultBoolean(Boolean value, boolean successful) {
			super(value, successful);
		}
	}
	
}
