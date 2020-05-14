package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import duelistmod.*;
import duelistmod.cards.DarkMirrorForce;
import duelistmod.helpers.DebuffHelper;
import duelistmod.variables.Strings;


public class DarkMirrorPower extends AbstractPower 
{
    public AbstractCreature source;
    public static final String POWER_ID = DuelistMod.makeID("DarkMirrorPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.DARK_POWER);

    public DarkMirrorPower(AbstractCreature owner) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;        
        this.img = new Texture(IMG);
        this.isTurnBased = false;
        this.type = PowerType.BUFF;
        this.updateDescription();
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (this.amount > 0) { this.amount = 0; }
    }
    
    @Override
    public void atStartOfTurn() 
    {
    	if (this.amount > 0) { this.amount = 0; }
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (this.amount > 0) { this.amount = 0; }
    }
    
    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (this.amount > 0) { this.amount = 0; }
	}

    @Override
    public int onLoseHp(int damageAmount)
    {
    	DarkMirrorForce.applyPowerToSelf(DebuffHelper.getRandomPlayerDebuff(AbstractDungeon.player, damageAmount));
    	DarkMirrorForce.removePower(this, AbstractDungeon.player);
    	return 0;
    }
    
    @Override
	public void updateDescription() 
    {
    	this.description = DESCRIPTIONS[0];
    }
}
