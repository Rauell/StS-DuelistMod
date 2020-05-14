package duelistmod.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

import duelistmod.abstracts.DuelistCard;

public class ModifyTributePermAction extends AbstractGameAction 
{
	DuelistCard cardToModify;	
	
	public ModifyTributePermAction(DuelistCard card, int addAmount) 
	{
		this.setValues(this.target, this.source, addAmount);
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.cardToModify = card;
	}
	
	@Override
	public void update() 
	{
		if (this.amount == 0) { this.isDone = true; return; }
		this.cardToModify.modifyTributesPerm(this.amount); 	
		//this.cardToModify.originalDescription = this.cardToModify.rawDescription;
		this.isDone = true;
	}
	
}
