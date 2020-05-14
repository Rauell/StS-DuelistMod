package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;

import duelistmod.*;
import duelistmod.abstracts.DuelistCard;
import duelistmod.variables.Strings;

public class SarraceniantPower extends AbstractPower 
{
    public AbstractCreature source;
    public static final String POWER_ID = DuelistMod.makeID("SarraceniantPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.SARRACENIANT_POWER);
    
    private static int poisonAmt = 3;

    public SarraceniantPower(final AbstractCreature owner, final AbstractCreature source, int newAmount) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;       
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        this.amount = newAmount;
        poisonAmt = newAmount;
        this.updateDescription();
    }
    
    @Override
    public int onAttacked(DamageInfo info, int damageAmount)
    {
    	if ((info.type != DamageInfo.DamageType.THORNS) && (info.type != DamageInfo.DamageType.HP_LOSS) && (info.owner != null) && (info.owner != this.owner) && (damageAmount > 0) && (!this.owner.hasPower("Buffer")))
    	{
    		DuelistCard.poisonAllEnemies(AbstractDungeon.player, poisonAmt);
    	}
    	return damageAmount;
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (this.amount != poisonAmt) { this.amount = poisonAmt; }
    }
    
    @Override
    public void atStartOfTurn() 
    {
    	if (this.amount != poisonAmt) { this.amount = poisonAmt; }
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (this.amount != poisonAmt) { this.amount = poisonAmt; }
    }
    
    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (this.amount != poisonAmt) { this.amount = poisonAmt; }
	}

    @Override
	public void updateDescription() {
        this.description = DESCRIPTIONS[0] + poisonAmt + DESCRIPTIONS[1];
    }
}
