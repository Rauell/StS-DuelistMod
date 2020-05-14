package duelistmod.cards.other.tempCards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.DuelistMod;
import duelistmod.abstracts.*;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.powers.duelistPowers.*;

public class LeafBlockCard extends TokenCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("LeafBlockCard");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makeCardPath("VineBlockCard.png");
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/

    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST;
    private static final int COST = -2;
    // /STAT DECLARATION/

    public LeafBlockCard(float magic, float leafLoss) 
    { 
    	super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET); 
    	this.dontTriggerOnUseCard = true;
    	this.magicNumber = this.baseMagicNumber = (int) magic;
    	this.secondMagic = this.baseSecondMagic = (int) leafLoss;
    }

    @Override public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	block(this.magicNumber);
    	if (p.hasPower(NaturiaLeodrakePower.POWER_ID))
		{
			NaturiaLeodrakePower pow = (NaturiaLeodrakePower)AbstractDungeon.player.getPower(NaturiaLeodrakePower.POWER_ID);
			pow.trigger();
		}
    	if (p.hasPower(LeavesPower.POWER_ID))
    	{
    		LeavesPower pow = (LeavesPower)p.getPower(LeavesPower.POWER_ID);
    		pow.halfReset(this.secondMagic);
    	}
    }
    @Override public AbstractCard makeCopy() { return new LeafBlockCard(this.magicNumber, this.secondMagic); }

    
    
	@Override public void onTribute(DuelistCard tributingCard) 
	{
		
	}
	
	@Override public void onResummon(int summons) 
	{ 
		
	}
	
	@Override public void summonThis(int summons, DuelistCard c, int var) {  }
	@Override public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) { }
	@Override public void upgrade() {}
	
	@Override
	public String getID() {
		return ID;
	}
	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}