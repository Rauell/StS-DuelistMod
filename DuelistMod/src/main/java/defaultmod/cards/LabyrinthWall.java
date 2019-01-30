package defaultmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;
import defaultmod.DefaultMod;
import defaultmod.patches.AbstractCardEnum;
import defaultmod.powers.SummonPower;

public class LabyrinthWall extends CustomCard {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     *
     * In order to understand how image paths work, go to defaultmod/DefaultMod.java, Line ~140 (Image path section).
     *
     * Strike Deal 7(9) damage.
     */

    // TEXT DECLARATION

    public static final String ID = defaultmod.DefaultMod.makeID("LabyrinthWall");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    // Yes, you totally can use "defaultModResources/images/cards/Attack.png" instead and that would work.
    // It might be easier to use that while testing.
    // Using makePath is good practice once you get the hand of things, as it prevents you from
    // having to change *every single card/file/path* if the image path changes due to updates or your personal preference.

    public static final String IMG = DefaultMod.makePath(DefaultMod.LABYRINTH_WALL);

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    // /TEXT DECLARATION/

    
    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = AbstractCardEnum.DEFAULT_GRAY;

    private static final int COST = 1;
    private static final int BLOCK = 30;
    private static final int UPGRADE_PLUS_BLK = 10;
    private static final int TRIBUTES = 2;

    // /STAT DECLARATION/

    public LabyrinthWall() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseBlock = BLOCK;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	if (this.upgraded) { this.baseBlock = BLOCK + UPGRADE_PLUS_BLK; }
    	if (p.hasPower(SummonPower.POWER_ID)) 
    	{
    		this.magicNumber = (p.getPower(SummonPower.POWER_ID).amount);
    		if (this.magicNumber >= TRIBUTES)
    		{
    			AbstractDungeon.actionManager
    				.addToBottom(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(p, p, SummonPower.POWER_ID, TRIBUTES));
    			AbstractDungeon.actionManager
    				.addToBottom(new com.megacrit.cardcrawl.actions.common.GainBlockAction(p, p, BLOCK));
    		}
    	} 
    	else 
    	{
    		this.magicNumber = 0;
    	} 
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new LabyrinthWall();
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLK);
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
    
    // If player doesn't have enough summons, can't play card
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m)
    {
    	if (p.hasPower(SummonPower.POWER_ID)) 
    	{
    		this.magicNumber = (p.getPower(SummonPower.POWER_ID).amount);
    		if (this.magicNumber >= TRIBUTES)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	
    	return false;
    }
   
}