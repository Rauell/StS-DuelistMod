package duelistmod.actions.unique;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.*;

import duelistmod.DuelistMod;

@SuppressWarnings("unused")
public class ReduceRandomCardsAction extends AbstractGameAction
{
	private static final float DURATION_PER_CARD = 0.35F;
	private AbstractCard c;
	private static final float PADDING = 25.0F * Settings.scale;
	private boolean isOtherCardInCenter = true;
	
	private static int reduction = 1;

	public ReduceRandomCardsAction(int amount, int reduceCostBy) 
	{
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.amount = amount;
		this.duration = 0.35F;
		reduction = reduceCostBy;
	}

	@Override
	public void update() 
	{
		if (this.amount == 0) {
			this.isDone = true;
			return;
		}

		runAction();
		
		if (this.amount > 0) {
			AbstractDungeon.actionManager.addToTop(new WaitAction(0.8F));
		}

		this.isDone = true;

	}
	
	public void runAction()
	{
		// Create empty list of cards
    	ArrayList<AbstractCard> modCards = new ArrayList<AbstractCard>();
    	
    	// Add all spells and traps from hand to list
    	for (AbstractCard c : AbstractDungeon.player.hand.group) { modCards.add(c); }
    	
    	// Remove all 0 cost spells and traps from list
    	if (modCards.size() > 0) { for (int i = 0; i < modCards.size(); i++) { if (modCards.get(i).cost == 0) { modCards.remove(i); } } }
    	
    	// For the amount of times equal to power stacks, grab a random card from the remaining list and set cost to 0
    	// Do this until no cards remain in list, or iterations = power stacks
    	for (int i = 0; i < amount; i++)
    	{
    		if (modCards.size() > 0)
    		{
    			int randomNum = AbstractDungeon.cardRandomRng.random(modCards.size() - 1);
	        	modCards.get(randomNum).setCostForTurn(-1 * reduction);
	        	//System.out.println("theDuelist:ReducerOrbPassiveAction --- > card reduced: " + modCards.get(randomNum).name);
	        	modCards.remove(randomNum);
    		}
    	}
    	AbstractDungeon.player.hand.glowCheck();
    	
    	// Set amount to 0 so update() knows to return
    	amount = 0;
	}
}