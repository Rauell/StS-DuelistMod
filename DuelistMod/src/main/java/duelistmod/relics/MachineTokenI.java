package duelistmod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistRelic;
import duelistmod.helpers.StarterDeckSetup;

public class MachineTokenI extends DuelistRelic 
{
	// ID, images, text.
	public static final String ID = duelistmod.DuelistMod.makeID("MachineTokenI");
	public static final String IMG = DuelistMod.makeRelicPath("MachineRelic.png");
	public static final String OUTLINE = DuelistMod.makeRelicPath("MachineRelic.png");

	public MachineTokenI() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.MAGICAL);
	}

	@Override
	public boolean canSpawn()
	{
		String deck = StarterDeckSetup.getCurrentDeck().getSimpleName();
		if (deck.equals("Machine Deck")) { return true; }
		else { return false; }
	}

	// Description
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public void onPassRoulette()
	{
		boolean toFlash = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group)
		{
			if (c.costForTurn > 0)
			{
				if (!c.type.equals(CardType.CURSE) && !c.type.equals(CardType.STATUS))
				{
					c.setCostForTurn(-1);
					toFlash = true;
				}
			}
		}
		if (toFlash) { this.flash(); }
	}
	
	// Which relic to return on making a copy of this relic.
	@Override
	public AbstractRelic makeCopy() {
		return new MachineTokenI();
	}
}
