package duelistmod.cards;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.*;
import duelistmod.abstracts.DuelistCard;
import duelistmod.helpers.Util;
import duelistmod.patches.*;
import duelistmod.powers.SummonPower;
import duelistmod.variables.*;

public class MonsterEggSpecial extends DuelistCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("MonsterEggSpecial");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makePath(Strings.MONSTER_EGG);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/
    
    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_SPECIAL;
    //private static final AttackEffect AFX = AttackEffect.SLASH_HORIZONTAL;
    private static final int COST = 0;
    // /STAT DECLARATION/

    public MonsterEggSpecial() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.summons = this.baseSummons = 1;
        this.baseDamage = this.damage = 0;
		this.baseMagicNumber = this.magicNumber = 1;
        this.tags.add(Tags.MONSTER);
        this.tags.add(Tags.WARRIOR);
        this.misc = 0;
		this.originalName = this.name;
		this.isSummon = true;
		this.exhaust = true;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	summon();
    	ArrayList<AbstractCard> resummons = DuelistCard.findAllOfTypeForResummon(Tags.MONSTER, this.magicNumber);
    	for (AbstractCard c : resummons)
    	{
    		Util.log("theDuelist:MonsterEggSpecial --- > Generated: " + c.cardID);
    		resummon(c, m, false, this.upgraded);
    	}
    	draw(1);
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new MonsterEggSpecial();
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

	@Override
	public void onTribute(DuelistCard tributingCard) 
	{
		
		
	}


	@Override
	public void onResummon(int summons) 
	{
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var) 
	{
		
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) {
		 
		
	}

	@Override
	public String getID() {
		return ID;
	}
	
    // Checking for Monster Zones if the challenge is enabled
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m)
    {
    	// Check super canUse()
    	boolean canUse = super.canUse(p, m); 
    	if (!canUse) { return false; }

    	if (Util.isCustomModActive("theDuelist:SummonersChallenge") || DuelistMod.challengeLevel20)
    	{
    		if ((DuelistMod.getChallengeDiffIndex() < 3) && this.misc == 52) { return true; }
    		if (p.hasPower(SummonPower.POWER_ID))
    		{
    			int sums = DuelistCard.getSummons(p); int max = DuelistCard.getMaxSummons(p);
    			if (sums + this.summons <= max) 
    			{ 
    				return true; 
    			}
    			else 
    			{ 
    				if (sums < max) 
    				{ 
    					if (max - sums > 1) { this.cantUseMessage = "You only have " + (max - sums) + " monster zones"; }
    					else { this.cantUseMessage = "You only have " + (max - sums) + " monster zone"; }
    					
    				}
    				else { this.cantUseMessage = "No monster zones remaining"; }
    				return false; 
    			}
    		}
    		else
    		{
    			return true;
    		}
    	}
    	
    	else
    	{
    		return true;
    	}
    }

	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}