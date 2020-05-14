package duelistmod.potions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistPotion;
import duelistmod.actions.common.RandomizedHandAction;
import duelistmod.characters.TheDuelist;
import duelistmod.variables.Colors;

public class SealedPackD extends DuelistPotion {


	public static final String POTION_ID = DuelistMod.makeID("SealedPackD");
	private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

	public static final String NAME = potionStrings.NAME;
	public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

	public SealedPackD() {
		// The bottle shape and inside is determined by potion size and color. The actual colors are the main DefaultMod.java
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.CARD, PotionEffect.NONE, Colors.DARK_PURPLE, Colors.WHITE, Colors.BLACK);
		
		// Potency is the damage/magic number equivalent of potions.
		this.potency = this.getPotency();

		// Initialize the Description
		this.description = DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];

		// Do you throw this potion at an enemy or do you just consume it.
		this.isThrown = false;

		// Initialize the on-hover name + description
		//this.tips.add(new PowerTip(this.name, this.description));

	}

	@Override
	public void use(AbstractCreature target) 
	{
		ArrayList<AbstractCard> packCards = new ArrayList<>();
		ArrayList<AbstractCard> raresInPool = new ArrayList<>();
		for (AbstractCard c : TheDuelist.cardPool.group) { if (c.rarity.equals(CardRarity.RARE)) { raresInPool.add(c.makeStatEquivalentCopy()); }}
		if (raresInPool.size() > 0) { packCards.add(raresInPool.get(AbstractDungeon.cardRandomRng.random(raresInPool.size() - 1))); }
		else { 
			for (AbstractCard c : DuelistMod.myCards) { if (c.rarity.equals(CardRarity.RARE)) { raresInPool.add(c.makeStatEquivalentCopy()); }}
			if (raresInPool.size() > 0) { packCards.add(raresInPool.get(AbstractDungeon.cardRandomRng.random(raresInPool.size() - 1))); }
		}
		
		int loopMax = 500;
		int counter = 0;
		counter += packCards.size();
		while (counter < this.potency && loopMax > 0)
		{
			packCards.add(TheDuelist.cardPool.getRandomCard(true).makeStatEquivalentCopy());
			counter++;
			loopMax--;
		}

		
		while (packCards.size() > this.potency)
		{
			packCards.remove(AbstractDungeon.cardRandomRng.random(packCards.size() - 1));
		}
		
		// Randomize pack cards and add to hand (10% chance to upgrade each card)
		for (AbstractCard c : packCards)
		{
			int roll = AbstractDungeon.cardRandomRng.random(1, 10);
			if (roll == 1)
			{
				AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(c, true, true, true, false, false, false, false, false, 1, 3, 0, 0, 0, 0));
			}
			else
			{
				AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(c, false, true, true, false, false, false, false, false, 1, 3, 0, 0, 0, 0));
			}
		}
	}

	@Override
	public AbstractPotion makeCopy() {
		return new SealedPackD();
	}

	// This is your potency.
	@Override
	public int getPotency(final int potency) 
	{
    	int pot = 5;
    	return pot;
	}
	
    @Override
    public void initializeData() {
        this.potency = this.getPotency();
        this.description =  DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

	public void upgradePotion()
	{
		this.potency += 2;
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
	}
}
