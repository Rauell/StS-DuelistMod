package duelistmod.cards.pools.dragons;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistCard;
import duelistmod.dto.AnyDuelist;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.powers.duelistPowers.IronChainDragonPower;
import duelistmod.variables.Tags;

import java.util.List;

public class IronChainDragon extends DuelistCard {
    public static final String ID = DuelistMod.makeID("IronChainDragon");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makeCardPath("IronChainDragon.png");
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_MONSTERS;
    private static final int COST = 1;

    public IronChainDragon() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.tributes = this.baseTributes 			= 3;
        this.specialCanUseLogic = true;							// for any summon or tribute card
        this.useTributeCanUse   = true;							// for tribute cards
        this.useBothCanUse      = false;						// for hybrid tribute/summon cards
        this.baseMagicNumber = this.magicNumber 	= 2;		//
        this.tags.add(Tags.MONSTER);
        this.tags.add(Tags.DRAGON);
        this.misc = 0;
        this.originalName = this.name;
        this.baseAFX = AttackEffect.FIRE;
        this.enemyIntent = AbstractMonster.Intent.BUFF;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        duelistUseCard(p, m);
    }

    @Override
    public void duelistUseCard(AbstractCreature owner, List<AbstractCreature> targets) {
        AnyDuelist duelist = AnyDuelist.from(this);
        duelist.applyPowerToSelf(new IronChainDragonPower(duelist.creature(), duelist.creature(), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new IronChainDragon();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            if (this.timesUpgraded > 0) { this.upgradeName(NAME + "+" + this.timesUpgraded); }
	    	else { this.upgradeName(NAME + "+"); }
            this.upgradeMagicNumber(1);
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.fixUpgradeDesc();
            this.initializeDescription(); 
        }
    }
}
