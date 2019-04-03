package duelistmod.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import basemod.BaseMod;
import duelistmod.DuelistMod;
import duelistmod.patches.DuelistCard;

public class RandomizedAction extends AbstractGameAction {

	private AbstractCard cardRef;
	private boolean exhaustCheck = false;
	private boolean etherealCheck = false;
	private boolean costChangeCheck = false;
	private boolean upgradeCheck = false;
	private boolean summonCheck = false;
	private boolean tributeCheck = false;
	private boolean summonChangeCombatCheck = false;
	private boolean tributeChangeCombatCheck = false;
	private int lowCostRoll = 1;
	private int highCostRoll = 4;
	private int lowSummonRoll = 1;
	private int highSummonRoll = 2;
	private int lowTributeRoll = 1;
	private int highTributeRoll = 3;
	
    public RandomizedAction(AbstractCard c, 
    		boolean upgrade, boolean ethereal, boolean exhaust,
    		boolean costChange, boolean tributeChange, boolean summonChange, 
    		boolean tribChangeCombat, boolean summonChangeCombat,
    		int lowCost, int highCost,
    		int lowTrib, int highTrib,
    		int lowSummon, int highSummon) 
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.cardRef = c;
        this.lowCostRoll = lowCost;
        this.highCostRoll = highCost;
        this.lowSummonRoll = lowSummon;
        this.highSummonRoll = highSummon;
        this.lowTributeRoll = lowTrib;
        this.highTributeRoll = highTrib;
        this.tributeChangeCombatCheck = tribChangeCombat;
        this.summonChangeCombatCheck = summonChangeCombat;
        if (upgrade)
        {
        	this.upgradeCheck = true;
        }
		if (ethereal)
		{
			this.etherealCheck = true;
		}
		if (exhaust)
		{			
			this.exhaustCheck = true;
		}
		if (costChange)
		{
			this.costChangeCheck = true;
		}
		if (tributeChange)
		{
			this.tributeCheck = true;
		}
		if (summonChange)
		{
			this.summonCheck = true;
		}
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) 
        {
            AbstractCard c = cardRef.makeStatEquivalentCopy();
            if (!c.upgraded && upgradeCheck)
    		{
    			c.upgrade();
    		}
            
            if (!c.isEthereal && etherealCheck) {
                c.isEthereal = true;
                c.rawDescription = "Ethereal NL " + c.rawDescription;
               // c.initializeDescription();
    		}
    		
    		if (!c.exhaust && exhaustCheck) {
                c.exhaust = true;
                c.rawDescription = c.rawDescription + " NL Exhaust.";
               // c.initializeDescription();
    		}
    		
    		if (costChangeCheck)
    		{
    			int randomNum = AbstractDungeon.cardRandomRng.random(lowCostRoll, highCostRoll);
    			c.costForTurn = randomNum;
    			c.isCostModifiedForTurn = true;
    			//c.initializeDescription();
    		}       
    		
    		if (summonCheck && c instanceof DuelistCard)
    		{
    			int randomNum = AbstractDungeon.cardRandomRng.random(lowSummonRoll, highSummonRoll);
    			DuelistCard dC = (DuelistCard)c;
    			if (summonChangeCombatCheck)
    			{
    				dC.modifySummons(randomNum);
    			}
    			else
    			{
    				dC.modifySummonsForTurn(randomNum);
    			}
    		}
    		
    		if (tributeCheck && c instanceof DuelistCard)
    		{
    			int randomNum = AbstractDungeon.cardRandomRng.random(lowTributeRoll, highTributeRoll);
    			DuelistCard dC = (DuelistCard)c;
    			if (tributeChangeCombatCheck)
    			{
    				dC.modifyTributes(-randomNum);
    			}
    			else
    			{
    				dC.modifyTributesForTurn(-randomNum);
    			}
    		}
    		
            c.initializeDescription();
            
            if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE)
            {
            	AbstractDungeon.actionManager.addToBottom(new MakeStatEquivalentLocal(c));
            }
            else
            {
            	if (DuelistMod.debug)
            	{
            		System.out.println("theDuelist:RandomizedAction:update() ---> got a hand size bigger than allowed, so skipped adding card to hand");
            	}
            }
            this.tickDuration();
        }
        this.isDone = true;
    }

    public class MakeStatEquivalentLocal extends AbstractGameAction {
        private AbstractCard c;

        public MakeStatEquivalentLocal(AbstractCard c) {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
            this.c = c;

        }

        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
            	if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE)
            	{
            		AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(c));
            	}
            	else
            	{
            		AbstractDungeon.player.createHandIsFullDialog();
            	}
                tickDuration();
                this.isDone = true;
            }
        }
    }

}