package com.crowsofwar.gorecore.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.crowsofwar.gorecore.util.GoreCoreParsingResult;
import com.crowsofwar.gorecore.util.GoreCoreParsingUtil;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

/**
 * GoreCoreMultiCommand sets the base for a multi-type command, so commands made with
 * GoreCoreMultiCommand have many subtypes.
 * 
 * @author CrowsOfWar
 */
public abstract class GoreCoreMultiCommand implements ICommand {
	
	private List<Subcommand> subcommands;
	private String aliasString;
	private String subcommandString;
	private List<String> aliases;
	
	public GoreCoreMultiCommand() {
		subcommands = new ArrayList<GoreCoreMultiCommand.Subcommand>();
		
		aliases = new ArrayList<String>(Arrays.asList(getMulticommandAliases()));
		
		aliasString = "";
		for (int i = 0; i < aliases.size(); i++) {
			aliasString += "|" + aliases.get(i);
		}
		if (!aliasString.equals("")) aliasString = "[" + aliasString + "]";
		
		compileSubcommandString();
		
		if (addHelpSubcommand()) {
			addSubcommand(new Subcommand("[subcommand]", "Displays help for the given subcommand or for the command itself.", "help", "?") {
				@Override
				public GoreCoreCommandExecutionReport execute(ICommandSender sender, String[] arguments) {
					if (arguments.length == 2) {
						
						// Display help for subcommand
						Subcommand subcommand = getSubcommandByName(arguments[1]);
						if (subcommand == null) {
							displayErrorSCNotFound(sender, arguments[1]);
						} else {
							displaySubcommandHelp(sender, subcommand);
						}
						
					} else {
						// Display general help
						displayHelp(sender);
					}
					
					return new GoreCoreCommandExecutionReport();
				}
			});
		}
		
	}
	
	// Utility
	
	private void compileSubcommandString() {
		subcommandString = "";
		for (int i = 0; i < subcommands.size(); i++) {
			subcommandString += (i > 0 ? "|" : "") + subcommands.get(i).getSubcommandName();
		}
		if (subcommandString.equals("")) {
			subcommandString = "<no subcommands>";
		} else {
			subcommandString = "<" + subcommandString + ">";
		}
	}
	
	protected void displayHelp(ICommandSender sender) {
		sendChat(sender, getMulticommandHead(), "help", null);
		sendChat(sender, getMulticommandNotice(), "help", null);
		sendChat(sender, getHelpKey(), "help", null);
		
		sendChat(sender, getSubcommandListTop(), "help", null);
		for (Subcommand subcommand : subcommands)
			sendChat(sender, getSubcommandListItem(), "help", subcommand.getSubcommandName());
	}
	
	protected void displaySubcommandHelp(ICommandSender sender, Subcommand subcommand) {
		sendChat(sender, getSubcommandHelpHead(), "help", subcommand.getSubcommandName());
		sendChat(sender, getSubcommandHelpDescription(), "help", subcommand.getSubcommandName());
		sendChat(sender, getSubcommandHelpUsage(), "help", subcommand.getSubcommandName());
	}
	
	protected void displayErrorSCNotFound(ICommandSender sender, String subcommandName) {
		sender.addChatMessage(
				new ChatComponentTranslation(getSubcommandNotFound(), sender.getCommandSenderName(), getCommandName(), subcommands.size(),
						"nusc", "nusc", "nusc", "nusc", "nusc", subcommandName, "ntsc", "ntsc", "ntsc", "ntsc", subcommandString));
	}
	
	protected void displayErrorNotOp(ICommandSender sender, Subcommand using) {
		sendChat(sender, getSubcommandNotOp(), using.getSubcommandName(), null);
	}
	
	protected void displayErrorNotPlayer(ICommandSender sender, Subcommand using) {
		sendChat(sender, getSubcommandNotPlayer(), using.getSubcommandName(), null);
	}
	
	protected void displayErrorPlayerNotOnline(ICommandSender sender, Subcommand using, String playerName) {
		sendChat(sender, getSubcommandPlayerNotOnline(), using.getSubcommandName(), null, playerName);
	}
	
	protected void displayErrorInvalidInt(ICommandSender sender, Subcommand using, String invalid) {
		sendChat(sender, getSubcommandInvalidInt(), using.getSubcommandName(), null, invalid);
	}
	
	protected void displayErrorInvalidFloat(ICommandSender sender, Subcommand using, String invalid) {
		sendChat(sender, getSubcommandInvalidFloat(), using.getSubcommandName(), null, invalid);
	}
	
	protected void displayErrorInvalidDouble(ICommandSender sender, Subcommand using, String invalid) {
		sendChat(sender, getSubcommandInvalidDouble(), using.getSubcommandName(), null, invalid);
	}
	
	protected void displayErrorInvalidLong(ICommandSender sender, Subcommand using, String invalid) {
		sendChat(sender, getSubcommandInvalidLong(), using.getSubcommandName(), null, invalid);
	}
	
	protected void displayErrorInvalidBoolean(ICommandSender sender, Subcommand using, String invalid) {
		sendChat(sender, getSubcommandInvalidBoolean(), using.getSubcommandName(), null, invalid);
	}
	
	protected void displayErrorIncorrectUsage(ICommandSender sender, Subcommand using) {
		sendChat(sender, getSubcommandIncorrectUsage(), using.getSubcommandName(), null);
	}
	
	protected void displayErrorPlayerDoesntExist(ICommandSender sender, Subcommand using, String player) {
		sendChat(sender, getSubcommandPlayerDoesntExist(), using.getSubcommandName(), null, player);
	}
	
	protected void displayErrorGetUUIDError(ICommandSender sender, Subcommand using, String player) {
		sendChat(sender, getSubcommandGetUUIDError(), using.getSubcommandName(), null, player);
	}
	
	protected void displayUUIDError(ICommandSender sender, Subcommand using, String player, GoreCorePlayerUUIDs.ResultOutcome error) {
		switch (error) {
		case SUCCESS: {
			break;
		}
		
		case USERNAME_DOES_NOT_EXIST: {
			displayErrorPlayerDoesntExist(sender, using, player);
			break;
		}
		
		case BAD_HTTP_CODE:
		case EXCEPTION_OCCURED: {
			displayErrorGetUUIDError(sender, using, player);
			break;
		}
		}
	}
	
	/**
	 * <p>
	 * Gets arguments to use for sending chat messages.
	 * </p>
	 * 
	 * <p>
	 * e.g. For <code>/gc ? version</code>, the arguments would be gotten like this:
	 * <code>getChatArguments(sender, getSubcommandByName("help"), getSubcommandByName("version")</code>
	 * </p>
	 * 
	 * @param sender
	 *            Whoever the chat message is for
	 * @param usingSC
	 *            The subcommand that is being used to send the chat, null if this is not from any
	 *            subcommands
	 * @param talkingOf
	 *            The subcommand that is being talked about, null if this chat is not talking of any
	 *            subcommands
	 * @param extraArguments
	 *            Extra arguments to be added onto the end of the returned array
	 */
	protected Object[] getChatArguments(ICommandSender sender, Subcommand usingSC, Subcommand talkingOf, Object[] extraArguments) {
		return ArrayUtils.addAll(new Object[] { sender.getCommandSenderName(), getCommandName(), subcommands.size(),
				usingSC == null ? "nusc" : usingSC.getSubcommandName(), usingSC == null ? "nusc" : usingSC.getSubcommandDescription(),
				usingSC == null ? "nusc" : usingSC.getSubcommandUsageRaw(), usingSC == null ? "nusc" : usingSC.getAllNamesString(),
				usingSC == null ? "nusc" : usingSC.getAliasesString(), talkingOf == null ? "ntsc" : talkingOf.getSubcommandName(),
				talkingOf == null ? "ntsc" : talkingOf.getSubcommandDescription(),
				talkingOf == null ? "ntsc" : talkingOf.getSubcommandUsageRaw(), talkingOf == null ? "ntsc" : talkingOf.getAllNamesString(),
				talkingOf == null ? "ntsc" : talkingOf.getAliasesString(), subcommandString }, extraArguments);
	}
	
	protected void sendChat(ICommandSender sender, String key, String using, String talkingOf, Object... extraArguments) {
		sender.addChatMessage(new ChatComponentTranslation(key, getChatArguments(sender, using == null ? null : getSubcommandByName(using),
				talkingOf == null ? null : getSubcommandByName(talkingOf), extraArguments)));
	}
	
	// Subclass hooks/methods
	protected void addSubcommand(Subcommand subcommand) {
		subcommands.add(subcommand);
		compileSubcommandString();
	}
	
	protected abstract String[] getMulticommandAliases();
	
	protected String getMulticommandHead() {
		return "gc.multicommand.help.head";
	}
	
	protected String getMulticommandNotice() {
		return "gc.multicommand.help.multicommandNotice";
	}
	
	protected String getHelpKey() {
		return "gc.multicommand.help.key";
	}
	
	protected String getSubcommandListTop() {
		return "gc.multicommand.help.list.top";
	}
	
	protected String getSubcommandListItem() {
		return "gc.multicommand.help.list.item";
	}
	
	protected String getSubcommandHelpHead() {
		return "gc.multicommand.helpsc.head";
	}
	
	protected String getSubcommandHelpDescription() {
		return "gc.multicommand.helpsc.desc";
	}
	
	protected String getSubcommandHelpUsage() {
		return "gc.multicommand.helpsc.usag";
	}
	
	protected String getPermissionCannotUseCommand() {
		return "gc.multicommand.permission.opOnly";
	}
	
	protected String getPermissionPlayerOnly() {
		return "gc.multicommand.permission.playerOnly";
	}
	
	protected String getSubcommandNotFound() {
		return "gc.multicommand.error.scNotFound";
	}
	
	protected String getSubcommandNotOp() {
		return "gc.multicommand.error.opOnly";
	}
	
	protected String getSubcommandNotPlayer() {
		return "gc.multicommand.error.playerOnly";
	}
	
	protected String getSubcommandPlayerNotOnline() {
		return "gc.multicommand.error.playerNotOnline";
	}
	
	protected String getSubcommandInvalidInt() {
		return "gc.multicommand.error.invalidInt";
	}
	
	protected String getSubcommandInvalidFloat() {
		return "gc.multicommand.error.invalidFloat";
	}
	
	protected String getSubcommandInvalidDouble() {
		return "gc.multicommand.error.invalidDouble";
	}
	
	protected String getSubcommandInvalidLong() {
		return "gc.multicommand.error.invalidLong";
	}
	
	protected String getSubcommandInvalidBoolean() {
		return "gc.multicommand.error.invalidBoolean";
	}
	
	protected String getSubcommandIncorrectUsage() {
		return "gc.multicommand.error.incorrectUsage";
	}
	
	protected String getSubcommandPlayerDoesntExist() {
		return "gc.multicommand.error.playerDoesntExist";
	}
	
	protected String getSubcommandGetUUIDError() {
		return "gc.multicommand.error.getUUIDError";
	}
	
	protected boolean addHelpSubcommand() {
		return true;
	}
	
	// Parsing methods
	
	// Public methods
	public Subcommand getSubcommandByName(String name) {
		for (Subcommand sc : subcommands) {
			if (sc.isNameOrAlias(name)) return sc;
		}
		
		return null;
	}
	
	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return getCommandName() + aliasString + " " + subcommandString + " <...>";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		if (args.length == 0) {
			// Display help.
			displayHelp(sender);
		} else if (args.length >= 1) {
			// Do the given subcommand
			String subcommandName = args[0];
			Subcommand subcommand = getSubcommandByName(subcommandName);
			if (subcommand == null) {
				// Invalid subcommand
				displayErrorSCNotFound(sender, subcommandName);
			} else {
				List<GoreCoreCommandProblem> allProblems = new ArrayList<GoreCoreCommandProblem>();
				List<Object> formatArgs = new ArrayList<Object>();
				
				GoreCoreCommandExecutionReport permissionReport = subcommand.checkCanUseCommand(sender);
				if (!permissionReport.getProblems().isEmpty()) allProblems.addAll(permissionReport.getProblems());
				if (!permissionReport.getFormattingData().isEmpty()) formatArgs.addAll(permissionReport.getFormattingData());
				
				if (permissionReport.getProblems().isEmpty()) {
					
					// Execute subcommand
					GoreCoreCommandExecutionReport runtimeReport = subcommand.execute(sender, args);
					if (!runtimeReport.getProblems().isEmpty()) allProblems.addAll(runtimeReport.getProblems());
					if (!runtimeReport.getFormattingData().isEmpty()) formatArgs.addAll(runtimeReport.getFormattingData());
					
				}
				
				// Display errors
				for (int i = 0; i < allProblems.size(); i++) {
					GoreCoreCommandProblem problem = allProblems.get(i);
					Object format = formatArgs.get(i);
					
					switch (problem) {
					case MUST_BE_OP: {
						displayErrorNotOp(sender, subcommand);
						break;
					}
					case MUST_BE_PLAYER: {
						displayErrorNotPlayer(sender, subcommand);
						break;
					}
					case PLAYER_NOT_ONLINE: {
						displayErrorPlayerNotOnline(sender, subcommand, (String) format);
						break;
					}
					case INVALID_INT: {
						displayErrorInvalidInt(sender, subcommand, (String) format);
						break;
					}
					case INVALID_FLOAT: {
						displayErrorInvalidFloat(sender, subcommand, (String) format);
						break;
					}
					case INVALID_DOUBLE: {
						displayErrorInvalidDouble(sender, subcommand, (String) format);
						break;
					}
					case INVALID_LONG: {
						displayErrorInvalidLong(sender, subcommand, (String) format);
						break;
					}
					case INVALID_BOOLEAN: {
						displayErrorInvalidBoolean(sender, subcommand, (String) format);
						break;
					}
					case INCORRECT_USAGE: {
						displayErrorIncorrectUsage(sender, subcommand);
						break;
					}
					case PLAYER_DOESNT_EXIST: {
						displayErrorPlayerDoesntExist(sender, subcommand, (String) format);
						break;
					}
					case GET_UUID_ERROR: {
						displayErrorGetUUIDError(sender, subcommand, (String) format);
						break;
					}
					}
					
				}
				
				// Done
				
			}
			
		}
		
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}
	
	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
	
	public abstract class Subcommand {
		private String name;
		private String[] allNames;
		private String allNamesString;
		private String aliasesString;
		private String usage;
		private String description;
		
		public Subcommand(String usage, String description, String... names) {
			this.name = names[0];
			this.allNames = names;
			this.usage = usage;
			this.description = description;
			
			this.allNamesString = "";
			this.aliasesString = "";
			for (int i = 0; i < names.length; i++) {
				allNamesString += (i > 0 ? "|" : "") + names[i];
				aliasesString += i > 0 ? "|" + names[i] : "";
			}
			if (!allNamesString.equals("")) allNamesString = "<" + allNamesString + "> ";
			if (!aliasesString.equals("")) aliasesString = "<" + aliasesString + "> ";
			
		}
		
		public abstract GoreCoreCommandExecutionReport execute(ICommandSender sender, String[] arguments);
		
		public boolean isPlayerEntityOnly() {
			return false;
		}
		
		public boolean isOpOnly() {
			return false;
		}
		
		/**
		 * Checks if the given ICS can use the command. Returns a report based on whether the ICS is
		 * allowed to do this.
		 */
		public GoreCoreCommandExecutionReport checkCanUseCommand(ICommandSender sender) {
			GoreCoreCommandExecutionReport report = new GoreCoreCommandExecutionReport();
			
			if (isPlayerEntityOnly() && !(sender instanceof EntityPlayer)) {
				report.addProblem(GoreCoreCommandProblem.MUST_BE_PLAYER, sender.getCommandSenderName());
			}
			
			if (isOpOnly() && sender instanceof EntityPlayer
					&& !sender.canCommandSenderUseCommand(2, GoreCoreMultiCommand.this.getCommandName())) {
				
				report.addProblem(GoreCoreCommandProblem.MUST_BE_OP, sender.getCommandSenderName());
				
			}
			
			return report;
			
		}
		
		public String getSubcommandName() {
			return name;
		}
		
		/**
		 * Returns whether the string is either a name or an alias of the subcommand.
		 */
		public boolean isNameOrAlias(String str) {
			for (String s : allNames)
				if (s.toLowerCase().equals(str.toLowerCase())) return true;
			return false;
		}
		
		public String getSubcommandUsageRaw() {
			return usage;
		}
		
		public String getSubcommandUsageRich() {
			return GoreCoreMultiCommand.this.getCommandName() + " " + usage;
		}
		
		public String getSubcommandDescription() {
			return description;
		}
		
		public String getAllNamesString() {
			return allNamesString;
		}
		
		public String getAliasesString() {
			return aliasesString;
		}
		
		protected boolean checkIsOp(ICommandSender sender) {
			return sender.canCommandSenderUseCommand(2, GoreCoreMultiCommand.this.getCommandName());
		}
		
		/**
		 * <p>
		 * Parse the string into an integer. If the string is not an integer, then the
		 * "Invalid Integer" problem is added to the execution report.
		 * </p>
		 * 
		 * <p>
		 * This is intended as a wrapper for directly using
		 * {@link GoreCoreParsingUtil#parseInt(String)}, so that problems can more easily be added
		 * to the report.
		 * </p>
		 * 
		 * <p>
		 * Returns null {@link GoreCoreParsingResult.ResultInteger#wasSuccessful() on failure}.
		 * </p>
		 * 
		 * @param report
		 *            The execution report, to add problems to if they occur
		 * @param str
		 *            The string to parse
		 * @return The parsed integer, or null if an error occured
		 * @see GoreCoreParsingUtil#parseInt(String)
		 */
		protected Integer parseInt(GoreCoreCommandExecutionReport report, String str) {
			GoreCoreParsingResult.ResultInteger parse = GoreCoreParsingUtil.parseInt(str);
			if (parse.wasSuccessful()) {
				return parse.getResult();
			} else {
				report.addProblem(GoreCoreCommandProblem.INVALID_INT, str);
				return null;
			}
		}
		
		/**
		 * <p>
		 * Parse the string into an float. If the string is not an float, then the "Invalid Float"
		 * problem is added to the execution report.
		 * </p>
		 * 
		 * <p>
		 * This is intended as a wrapper for directly using
		 * {@link GoreCoreParsingUtil#parseFloat(String)}, so that problems can more easily be added
		 * to the report.
		 * </p>
		 * 
		 * <p>
		 * Returns null {@link GoreCoreParsingResult.ResultFloat#wasSuccessful() on failure}.
		 * </p>
		 * 
		 * @param report
		 *            The execution report, to add problems to if they occur
		 * @param str
		 *            The string to parse
		 * @return The parsed float, or null if an error occured
		 * @see GoreCoreParsingUtil#parseFloat(String)
		 */
		protected Float parseFloat(GoreCoreCommandExecutionReport report, String str) {
			GoreCoreParsingResult.ResultFloat parse = GoreCoreParsingUtil.parseFloat(str);
			if (parse.wasSuccessful()) {
				return parse.getResult();
			} else {
				report.addProblem(GoreCoreCommandProblem.INVALID_FLOAT, str);
				return null;
			}
		}
		
		/**
		 * <p>
		 * Parse the string into an double. If the string is not an double, then the
		 * "Invalid Double" problem is added to the execution report.
		 * </p>
		 * 
		 * <p>
		 * This is intended as a wrapper for directly using
		 * {@link GoreCoreParsingUtil#parseDouble(String)}, so that problems can more easily be
		 * added to the report.
		 * </p>
		 * 
		 * <p>
		 * Returns null {@link GoreCoreParsingResult.ResultDouble#wasSuccessful() on failure}.
		 * </p>
		 * 
		 * @param report
		 *            The execution report, to add problems to if they occur
		 * @param str
		 *            The string to parse
		 * @return The parsed double, or null if an error occured
		 * @see GoreCoreParsingUtil#parseDouble(String)
		 */
		protected Double parseDouble(GoreCoreCommandExecutionReport report, String str) {
			GoreCoreParsingResult.ResultDouble parse = GoreCoreParsingUtil.parseDouble(str);
			if (parse.wasSuccessful()) {
				return parse.getResult();
			} else {
				report.addProblem(GoreCoreCommandProblem.INVALID_DOUBLE, str);
				return null;
			}
		}
		
		/**
		 * <p>
		 * Parse the string into an long. If the string is not an long, then the "Invalid Long"
		 * problem is added to the execution report.
		 * </p>
		 * 
		 * <p>
		 * This is intended as a wrapper for directly using
		 * {@link GoreCoreParsingUtil#parseLong(String)}, so that problems can more easily be added
		 * to the report.
		 * </p>
		 * 
		 * <p>
		 * Returns null {@link GoreCoreParsingResult.ResultLong#wasSuccessful() on failure}.
		 * </p>
		 * 
		 * @param report
		 *            The execution report, to add problems to if they occur
		 * @param str
		 *            The string to parse
		 * @return The parsed long, or null if an error occured
		 * @see GoreCoreParsingUtil#parseLong(String)
		 */
		protected Long parseLong(GoreCoreCommandExecutionReport report, String str) {
			GoreCoreParsingResult.ResultLong parse = GoreCoreParsingUtil.parseLong(str);
			if (parse.wasSuccessful()) {
				return parse.getResult();
			} else {
				report.addProblem(GoreCoreCommandProblem.INVALID_LONG, str);
				return null;
			}
		}
		
		/**
		 * <p>
		 * Parse the string into an boolean. If the string is not an boolean, then the
		 * "Invalid Boolean" problem is added to the execution report.
		 * </p>
		 * 
		 * <p>
		 * This is intended as a wrapper for directly using
		 * {@link GoreCoreParsingUtil#parseBoolean(String)}, so that problems can more easily be
		 * added to the report.
		 * </p>
		 * 
		 * <p>
		 * Returns null {@link GoreCoreParsingResult.ResultBoolean#wasSuccessful() on failure}.
		 * </p>
		 * 
		 * @param report
		 *            The execution report, to add problems to if they occur
		 * @param str
		 *            The string to parse
		 * @return The parsed boolean, or null if an error occured
		 * @see GoreCoreParsingUtil#parseBoolean(String)
		 */
		protected Boolean parseBoolean(GoreCoreCommandExecutionReport report, String str) {
			GoreCoreParsingResult.ResultBoolean parse = GoreCoreParsingUtil.parseBoolean(str);
			if (parse.wasSuccessful()) {
				return parse.getResult();
			} else {
				report.addProblem(GoreCoreCommandProblem.INVALID_BOOLEAN, str);
				return null;
			}
		}
		
	}
	
}
