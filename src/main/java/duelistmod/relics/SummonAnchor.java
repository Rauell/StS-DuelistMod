package duelistmod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.*;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistRelic;
import duelistmod.actions.unique.SummonAnchorAction;

public class SummonAnchor extends DuelistRelic {

	/*
	 * https://github.com/daviscook477/BaseMod/wiki/Custom-Relics
	 * 
	 * Summon 1 on combat start
	 */

	// ID, images, text.
	public static final String ID = duelistmod.DuelistMod.makeID("SummonAnchor");
    public static final String IMG = DuelistMod.makeRelicPath("AnchorRelic.png");
    public static final String OUTLINE = DuelistMod.makeRelicOutlinePath("AnchorRelic_Outline.png");
    private int summs = 1;

	public SummonAnchor() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.MAGICAL);
	}
	
	@Override
	public boolean canSpawn()
	{
		if (AbstractDungeon.player.hasRelic(SummonAnchorRare.ID)) { return false; }
		else { return true; }
	}


	@Override
	public void atBattleStart() 
	{
		AbstractDungeon.actionManager.addToBottom(new SummonAnchorAction(summs));
		this.flash();
		AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
		this.grayscale = true;
	}
	
	@Override
    public void onVictory() 
    {
		this.grayscale = false;
    }

	// Description
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	// Which relic to return on making a copy of this relic.
	@Override
	public AbstractRelic makeCopy() {
		return new SummonAnchor();
	}
}
