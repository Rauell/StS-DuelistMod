package duelistmod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.*;

import duelistmod.*;
import duelistmod.abstracts.*;
import duelistmod.actions.common.SummonAction;

public class SummonAnchorRare extends DuelistRelic {

	/*
	 * https://github.com/daviscook477/BaseMod/wiki/Custom-Relics
	 * 
	 * Summon 1 on combat start
	 */

	// ID, images, text.
	public static final String ID = duelistmod.DuelistMod.makeID("SummonAnchorRare");
    public static final String IMG = DuelistMod.makeRelicPath("AnchorRelicRare.png");
    public static final String OUTLINE = DuelistMod.makeRelicOutlinePath("AnchorRelic_Outline.png");

	public SummonAnchorRare() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.MAGICAL);
	}

	@Override
	public boolean canSpawn()
	{
		if (AbstractDungeon.player.hasRelic(SummonAnchor.ID)) { return false; }
		else { return true; }
	}

	@Override
	public void atBattleStart() 
	{
		DuelistCard randTokenA = DuelistCardLibrary.getRandomTokenForCombat(true, true, true, false, false, false);
		DuelistCard randTokenB = DuelistCardLibrary.getRandomTokenForCombat(true, true, true, false, false, false);
		AbstractDungeon.actionManager.addToBottom(new SummonAction(1, randTokenA));
		AbstractDungeon.actionManager.addToBottom(new SummonAction(1, randTokenB));
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
		return new SummonAnchorRare();
	}
}
