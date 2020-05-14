package duelistmod.powers.duelistPowers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.localization.PowerStrings;

import duelistmod.DuelistMod;
import duelistmod.abstracts.*;

public class NoResummoningAttacksCombatPower extends NoStackDuelistPower 
{
    public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("NoResummoningAttacksCombatPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePowerPath("NoResummonPower.png");

    public NoResummoningAttacksCombatPower(final AbstractCreature owner, final AbstractCreature source) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;        
        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        this.updateDescription();
    }

    @Override
    public boolean allowResummon(AbstractCard resummoningCard) { if (resummoningCard.type.equals(CardType.ATTACK)) { return false; } return true; }

    @Override
	public void updateDescription() 
    {
        this.description = DESCRIPTIONS[0];
    }
}
