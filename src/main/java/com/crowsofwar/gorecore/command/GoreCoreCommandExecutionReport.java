package com.crowsofwar.gorecore.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.FMLLog;

/**
 * Contains the results when executing a subcommand.
 * 
 * @author CrowsOfWar
 */
public class GoreCoreCommandExecutionReport {
	
	private final List<GoreCoreCommandProblem> problems;
	
	/**
	 * A list of objects to be formatted when sending error messages.
	 * <code>formattingData.get(<em>n</em>)</code> corresponds to <code>problems.get(<em>n</em>
	 * )</code>.
	 */
	private final List<Object> formattingData;
	
	/**
	 * Creates a new "report" which describes errors and such.
	 */
	public GoreCoreCommandExecutionReport() {
		this.problems = new ArrayList<GoreCoreCommandProblem>();
		this.formattingData = new ArrayList<Object>();
		if (problems.size() != formattingData.size()) {
			FMLLog.warning("GoreCore> Warning: somebody created GoreCoreCommandExecuteReport "
					+ "with unmatching problems/formattingData. This is a bug, report it to CrowsOfWar!");
		}
	}
	
	public boolean wasSuccessful() {
		return problems.isEmpty();
	}
	
	/**
	 * Gets a list of problems that happened during execution. Empty if there were none.
	 */
	public List<GoreCoreCommandProblem> getProblems() {
		return problems;
	}
	
	/**
	 * Gets objects to be formatted into the format args when sending error messages
	 */
	public List<Object> getFormattingData() {
		return formattingData;
	}
	
	/**
	 * Same as {@link #getFormattingData()}, but returns formattingData in array form.
	 */
	public Object[] getFormattingDataArr() {
		return formattingData.toArray(new Object[formattingData.size()]);
	}
	
	/**
	 * Add a problem to this report.
	 * 
	 * @param problem
	 *            The problem that happened
	 * @param format
	 *            Extra data to help describe the problem. This is dependent on which problem
	 *            happened. For example, if the problem was that a player didn't exist, format
	 *            should be the player's name. Set to null if this isn't necessary.
	 */
	public void addProblem(GoreCoreCommandProblem problem, Object format) {
		problems.add(problem);
		formattingData.add(format);
	}
	
}
