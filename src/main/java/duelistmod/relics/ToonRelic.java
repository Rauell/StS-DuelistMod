package duelistmod.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.*;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistRelic;
import duelistmod.helpers.*;

public class ToonRelic extends DuelistRelic 
{
	// ID, images, text.
	public static final String ID = DuelistMod.makeID("ToonRelic");
	public static final String IMG = DuelistMod.makeRelicPath("ToonRelic.png");
	public static final String OUTLINE = DuelistMod.makeRelicPath("ToonRelic.png");
	
	public ToonRelic() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	@Override
	public boolean canSpawn()
	{
		String deck = StarterDeckSetup.getCurrentDeck().getSimpleName();
    	boolean allowSpawn = false;
    	if (DuelistMod.toonBtnBool) 
    	{ 
    		if (Util.deckIs("Toon Deck")) { allowSpawn = true; }
    		if (DuelistMod.setIndex == 6) { allowSpawn = true; }
    	}
    	else
    	{
    		if (Util.deckIs("Toon Deck")) { allowSpawn = true; }
    		if (DuelistMod.setIndex == 6) { allowSpawn = true; }
    	}
		return allowSpawn;
	}
	
	@Override
	public void onEquip()
	{
		setDescription();
		DuelistMod.toonVuln = 2;
	}
	
	@Override
	public void onUnequip()
	{
		DuelistMod.toonVuln = 2;
	}

	// Description
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void setDescription()
	{
		description = getUpdatedDescription();
        tips.clear();
        tips.add(new PowerTip(name, description));
        initializeTips();
	}

	// Which relic to return on making a copy of this relic.
	@Override
	public AbstractRelic makeCopy() {
		return new ToonRelic();
	}
}
