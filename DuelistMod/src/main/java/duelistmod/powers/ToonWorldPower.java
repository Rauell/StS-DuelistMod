package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import duelistmod.*;
import duelistmod.patches.DuelistCard;

// Passive no-effect power, just lets Toon Monsters check for playability

public class ToonWorldPower extends AbstractPower 
{
    public AbstractCreature source;
    public static final String POWER_ID = DuelistMod.makeID("ToonWorldPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.TOON_WORLD_POWER);
    public static int TOON_DMG = 5;
    
    public ToonWorldPower(final AbstractCreature owner, final AbstractCreature source, int toonDmg, boolean playedCard) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        TOON_DMG = toonDmg;
        this.amount = TOON_DMG;
        if (DuelistMod.challengeMode)
        {
        	this.amount += 5;
        	TOON_DMG += 5;
        }
        if (playedCard)
        {
        	DuelistMod.toonWorldTemp = false;
        }
        this.updateDescription();
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (this.amount != TOON_DMG) { this.amount = TOON_DMG; }
    	if (AbstractDungeon.player.hasPower("ToonKingdomPower"))
    	{
    		DuelistCard.removePower(this, AbstractDungeon.player);
    	}
    }
    
    @Override
    public void atStartOfTurn() 
    {
    	if (this.amount != TOON_DMG) { this.amount = TOON_DMG; }
    	if (AbstractDungeon.player.hasPower("ToonKingdomPower"))
    	{
    		DuelistCard.removePower(this, AbstractDungeon.player);
    	}
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (AbstractDungeon.player.hasPower("ToonKingdomPower"))
    	{
    		DuelistCard.removePower(this, AbstractDungeon.player);
    	}
    	else
    	{
	    	if (this.amount != TOON_DMG) { this.amount = TOON_DMG; }
	    	if (c.hasTag(Tags.TOON) && !c.originalName.equals("Toon World") && !c.originalName.equals("Toon Kingdom")) 
	    	{ 
	    		if (TOON_DMG > 0) { DuelistCard.damageSelf(TOON_DMG); TOON_DMG--;  }
	    	}
	    	
	    	this.amount = TOON_DMG;
	    	updateDescription();
    	}
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) 
    {
    	if (DuelistMod.toonWorldTemp)
    	{
    		DuelistCard.removePower(this, this.owner);
    		DuelistMod.toonWorldTemp = false;
    	}
    }
    
    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (this.amount != TOON_DMG) { this.amount = TOON_DMG; }
    	if (AbstractDungeon.player.hasPower("ToonKingdomPower"))
    	{
    		DuelistCard.removePower(this, AbstractDungeon.player);
    	}
    	else
    	{
    		if (TOON_DMG > 0)
    		{
	    		TOON_DMG--;
	    		this.amount = TOON_DMG;
	    		updateDescription();
    		}
    	}
	}

    @Override
	public void updateDescription() 
    {
    	if (TOON_DMG < 1) { this.description = DESCRIPTIONS[2]; }
    	else { this.description = DESCRIPTIONS[0] + TOON_DMG + DESCRIPTIONS[1]; }
    }
}