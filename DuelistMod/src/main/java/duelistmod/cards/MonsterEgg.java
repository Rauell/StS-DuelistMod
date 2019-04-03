package duelistmod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.*;
import duelistmod.patches.*;

public class MonsterEgg extends DuelistCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("MonsterEgg");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makePath(Strings.MONSTER_EGG);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/
    
    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_MONSTERS;
    //private static final AttackEffect AFX = AttackEffect.SLASH_HORIZONTAL;
    private static final int COST = 1;
    // /STAT DECLARATION/

    public MonsterEgg() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.summons = this.baseSummons = 1;
        this.baseDamage = this.damage = 0;
        this.tags.add(Tags.MONSTER);
        this.tags.add(Tags.ALL);
        this.tags.add(Tags.LEGEND_BLUE_EYES);
        this.tags.add(Tags.GENERATION_DECK);
		this.startingGenDeckCopies = 1;
        this.misc = 0;
		this.originalName = this.name;
		this.isSummon = true;
		this.setupStartingCopies();
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	// Tribute and attack
    	summon(p, this.summons, this);
    	//attack(m, AFX, this.damage);
    	
    	// Generate 2 random dragons and target them at the same target as the attack() above
    	// If this card is upgraded, the two dragons get upgraded as well
    	DuelistCard extraDragA = (DuelistCard) returnTrulyRandomFromOnlyFirstSet(Tags.MONSTER, Tags.TOON);    	
    	while (extraDragA.hasTag(Tags.EXEMPT)) { extraDragA = (DuelistCard) returnTrulyRandomFromOnlyFirstSet(Tags.MONSTER, Tags.TOON); }
    	String cardNameA = extraDragA.originalName;    	
    	if (DuelistMod.debug) { System.out.println("theDuelist:MonsterEgg --- > Generated: " + cardNameA); }
    	if (!extraDragA.tags.contains(Tags.TRIBUTE)) { extraDragA.misc = 52; }    	
        extraDragA.freeToPlayOnce = true;       
        extraDragA.applyPowers();      
        extraDragA.purgeOnUse = true;
        if (this.upgraded) { extraDragA.upgrade();  }
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(extraDragA, m));    	
        extraDragA.onResummon(1);
        extraDragA.checkResummon();
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new MonsterEgg();
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //this.upgradeBaseCost(2);
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

	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}