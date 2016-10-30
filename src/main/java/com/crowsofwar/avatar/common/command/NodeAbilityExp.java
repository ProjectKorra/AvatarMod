package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;
import com.crowsofwar.gorecore.tree.TreeCommandException;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeAbilityExp extends NodeFunctional {
	
	private static final ITypeConverter<BendingAbility<?>> convertAbility = new ITypeConverter<BendingAbility<?>>() {
		@Override
		public BendingAbility<?> convert(String str) {
			List<BendingAbility> allAbilities = BendingManager.allAbilities();
			for (BendingAbility<?> ability : allAbilities) {
				if (ability.getClass().getSimpleName().equals(str)) return ability;
			}
			
			throw new TreeCommandException(Reason.NOT_OPTION);
		}
		
		@Override
		public String toString(BendingAbility<?> obj) {
			return obj.getClass().getSimpleName();
		}
		
		@Override
		public String getTypeName() {
			return "Ability";
		}
	};
	
	private static final BendingAbility[] allAbilities;
	static {
		List<BendingAbility> list = BendingManager.allAbilities();
		allAbilities = new BendingAbility[list.size()];
		for (int i = 0; i < list.size(); i++)
			allAbilities[i] = list.get(i);
	}
	
	private final IArgument<String> argOperation;
	private final IArgument<String> argPlayer;
	private final IArgument<BendingAbility<?>> argAbility;
	private final IArgument<Integer> argSetTo;
	
	/**
	 * @param name
	 * @param op
	 */
	public NodeAbilityExp() {
		super("ability", true);
		argOperation = addArgument(
				new ArgumentOptions<>(ITypeConverter.CONVERTER_STRING, "operation", "get", "set"));
		argPlayer = addArgument(new ArgumentPlayerName("player"));
		argAbility = addArgument(new ArgumentOptions<>(convertAbility, "ability", allAbilities));
		argSetTo = addArgument(new ArgumentDirect<Integer>("value", ITypeConverter.CONVERTER_INTEGER, -1));
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ArgumentList args = call.popArguments(this);
		String operation = args.get(argOperation);
		String player = args.get(argPlayer);
		BendingAbility ability = args.get(argAbility);
		int setXp = args.get(argSetTo);
		
		if (operation.equals("set")) {
			
			if (setXp >= 0 && setXp <= 100) {
				AvatarPlayerData data = AvatarPlayerData.fetcher()
						.fetchPerformance(call.getFrom().getEntityWorld(), player);
				if (data != null) {
					
					data.getAbilityData(ability).setXp(setXp);
					AvatarChatMessages.MSG_ABILITY_SET_SUCCESS.send(call.getFrom(), player,
							ability.getClass().getSimpleName(), setXp);
				}
			} else {
				AvatarChatMessages.MSG_ABILITY_SET_RANGE.send(call.getFrom());
			}
			
		} else if (operation.equals("get")) {
			
			AvatarPlayerData data = AvatarPlayerData.fetcher()
					.fetchPerformance(call.getFrom().getEntityWorld(), player);
			if (data != null) {
				
				int xp = data.getAbilityData(ability).getXp();
				AvatarChatMessages.MSG_ABILITY_GET.send(call.getFrom(), player,
						ability.getClass().getSimpleName(), xp);
				
			}
			
		}
		
		return null;
	}
	
}
