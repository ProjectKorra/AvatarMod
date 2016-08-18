package com.crowsofwar.gorecore.command;

/**
 * Describes whether a player can use the command, and reasons why someone might not be able to use
 * the command.
 * 
 * @author CrowsOfWar
 */
public enum GoreCoreCommandProblem {
	
	MUST_BE_OP, MUST_BE_PLAYER, PLAYER_NOT_ONLINE, INVALID_INT, INVALID_FLOAT, INVALID_DOUBLE, INVALID_LONG, INVALID_BOOLEAN, INCORRECT_USAGE, PLAYER_DOESNT_EXIST, GET_UUID_ERROR;
	
}
