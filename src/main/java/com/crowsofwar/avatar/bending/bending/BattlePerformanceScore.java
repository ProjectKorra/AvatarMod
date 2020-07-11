package com.crowsofwar.avatar.bending.bending;

import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.DataCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

/**
 * Represents a numeric score of how well a bender is doing in a battle. This score is increased by
 * actions like damaging the enemy, and decreased by taking damage. It can be used for modifiers
 * and boosts; for example, airbenders get faster chi regeneration when having high battle
 * performance.
 * <p>
 * The actual battle performance score is obtained with {@link #getScore()} and is between -100 to
 * 100.
 * <p>
 * The BattlePerformanceScore is synced between server and client. Please note that client attempts
 * to change the value don't do anything, and the score can only be changed by the server.
 *
 * @author CrowsOfWar
 */
public class BattlePerformanceScore {

	public static final double SCORE_MOD_SMALL = 16;
	public static final double SCORE_MOD_MEDIUM = 30;
	public static final double SCORE_MOD_LARGE = 50;

	private final BendingData data;
	private double score;

	public BattlePerformanceScore(BendingData data) {
		this(data, 0);
	}

	public BattlePerformanceScore(BendingData data, double score) {
		this.data = data;
		this.score = score;
	}

	public static void addScore(EntityLivingBase entity, int amount) {
		BendingData bendingData = BendingData.get(entity);
		if (bendingData != null) {
			bendingData.getPerformance().modifyScore(amount);
		}
	}

	public static void addSmallScore(EntityLivingBase entity) {
		BendingData bendingData = BendingData.get(entity);
		if (bendingData != null) {
			bendingData.getPerformance().modifyScore(SCORE_MOD_SMALL);
		}
	}

	public static void addMediumScore(EntityLivingBase entity) {
		BendingData bendingData = BendingData.get(entity);
		if (bendingData != null) {
			bendingData.getPerformance().modifyScore(SCORE_MOD_MEDIUM);
		}
	}

	public static void addLargeScore(EntityLivingBase entity) {
		BendingData bendingData = BendingData.get(entity);
		if (bendingData != null) {
			bendingData.getPerformance().modifyScore(SCORE_MOD_LARGE);
		}
	}

	/**
	 * Attaining a high battle performance score is temporary; it passively moves towards zero every
	 * second. Gets the amount of score to change per second, given the current score. Values of
	 * higher magnitude (i.e. closer to 100 or -100) move towards zero faster, while scores already
	 * close to zero move towards zero more slowly.
	 * <p>
	 */
	private static final double getScoreChangePerSecond(double currentScore) {

		// Minimum / maximum score change per second, where min. change occurs when score is near 0,
		// and max. change occurs when score is at 100
		final double min = 2;
		final double max = 4;

		// Generates a curve from minimum to maximum

		// not sure how to explain this but got it using math. K then...
		double k = 10000 / (max - min);
		return -Math.signum(currentScore) * (currentScore * currentScore + k * min) / k;

	}

	/**
	 * Updates the battle performance number, to be called every tick
	 */
	public void update() {
		double changePerSecond = getScoreChangePerSecond(score);
		modifyScore(changePerSecond / 20);
	}

	/**
	 * Add or subtract a certain amount of score from the bender.
	 */
	public void modifyScore(double amount) {
		setScore(MathHelper.clamp(score + amount, -500, 500));
	}

	/**
	 * Returns the numeric rating of how well the bender is doing in combat right now, ranging from
	 * -100 to 100 (0 is neutral).
	 */
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		if (this.score != score) {
			data.save(DataCategory.PERFORMANCE);
		}
		this.score = score;
	}

}
