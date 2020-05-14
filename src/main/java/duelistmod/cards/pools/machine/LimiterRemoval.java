package duelistmod.cards.pools.machine;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistCard;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.variables.Tags;

public class LimiterRemoval extends DuelistCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("LimiterRemoval");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makeCardPath("LimiterRemoval.png");
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/

    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_SPELLS;
    private static final int COST = 2;
    // /STAT DECLARATION/

    public LimiterRemoval() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.originalName = this.name;
        this.magicNumber = this.baseMagicNumber = 9;
        this.tags.add(Tags.SPELL);
        this.tags.add(Tags.PHARAOH_SERVANT);
        this.isMultiDamage = true;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	this.damage = this.baseDamage;
    	if (p.hasPower(ArtifactPower.POWER_ID) && this.damage > 0)
    	{
    		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SMASH));
    	}
    }

    @Override
    public void applyPowers() 
    {
        super.applyPowers();
        if (AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID))
        {
        	this.damage = this.baseDamage = AbstractDungeon.player.getPower(ArtifactPower.POWER_ID).amount * this.magicNumber;
        	this.initializeDescription();
        }
        else
        {
        	this.baseDamage = this.damage = 0;
        	this.initializeDescription();
        }
    }
    
    @Override
    public void calculateCardDamage(AbstractMonster mo) 
    {
        super.calculateCardDamage(mo);
        if (AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID))
        {
        	this.damage = this.baseDamage = AbstractDungeon.player.getPower(ArtifactPower.POWER_ID).amount * this.magicNumber;
        	this.initializeDescription();
        }
        else
        {
        	this.baseDamage = this.damage = 0;
        	this.initializeDescription();
        }
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new LimiterRemoval();
    }

    // Upgraded stats.
    @Override
    public void upgrade() 
    {
        if (!upgraded) 
        {
        	if (this.timesUpgraded > 0) { this.upgradeName(NAME + "+" + this.timesUpgraded); }
	    	else { this.upgradeName(NAME + "+"); }
        	this.upgradeMagicNumber(2);
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

	@Override
	public void onTribute(DuelistCard tributingCard) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResummon(int summons) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var) 
	{
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) 
	{
		
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}