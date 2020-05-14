package duelistmod.actions.unique;

import java.util.*;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.abstracts.DuelistCard;
import duelistmod.cards.other.tokens.Token;

public class PlayRandomFromDiscardAction extends AbstractGameAction
{
	private AbstractPlayer p;
	private boolean upgrade = false;
	private UUID callingCard;
	private AbstractMonster m;
	private int copies = 1;

	public PlayRandomFromDiscardAction(int amount)
	{
		this(amount, new Token().uuid);
	}
	
	public PlayRandomFromDiscardAction(int amount, UUID callingCard)
	{
		this.p = AbstractDungeon.player;
		setValues(this.p, AbstractDungeon.player, amount);
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_MED;
		this.m = AbstractDungeon.getRandomMonster();
		this.callingCard = callingCard;
	}
	
	public PlayRandomFromDiscardAction(int amount, int copies, UUID callingCard)
	{
		this.p = AbstractDungeon.player;
		setValues(this.p, AbstractDungeon.player, amount);
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_MED;
		this.m = AbstractDungeon.getRandomMonster();
		this.callingCard = callingCard;
		this.copies = copies;
	}
	
	public PlayRandomFromDiscardAction(int amount, AbstractMonster m, UUID callingCard)
	{
		this.p = AbstractDungeon.player;
		setValues(this.p, AbstractDungeon.player, amount);
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_MED;
		this.m = m;
		this.callingCard = callingCard;
	}
	
	public PlayRandomFromDiscardAction(int amount, boolean upgraded, AbstractMonster m, UUID callingCard)
	{
		this.p = AbstractDungeon.player;
		setValues(this.p, AbstractDungeon.player, amount);
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_MED;
		this.upgrade = upgraded;
		this.m = m;
		this.callingCard = callingCard;
	}

	public void update() {
		CardGroup tmp;
		if (this.duration == Settings.ACTION_DUR_MED) 
		{
			tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			for (AbstractCard c : this.p.discardPile.group) 
			{
				if (DuelistCard.allowResummonsWithExtraChecks(c) && !c.uuid.equals(callingCard))
				{
					tmp.addToRandomSpot(c);
				}
			}

			// No cards in discard pile
			if (tmp.size() == 0) 
			{
				this.isDone = true;
				return; 
			}
			
			// Only 1 card in discard pile
			else if (tmp.size() == 1) 
			{
				AbstractCard card = tmp.getTopCard();
				if (DuelistCard.allowResummonsWithExtraChecks(card))
				{
					for (int i = 0; i < this.amount; i++)
					{
						// Play card
						AbstractCard cardCopy = card;
		    			if (cardCopy != null && m != null)
		    			{
		    				DuelistCard.resummon(cardCopy, m, this.copies, this.upgrade, false);	    				
		    			}
					}
				}
				//this.p.discardPile.removeCard(card);
				this.isDone = true;
				return; 
			}
			
			// Not enough cards in discard to satisfy requested # cards, but some cards in discard
			else if (tmp.size() <= this.amount) 
			{
				ArrayList<AbstractCard> cardsToPlayFrom = new ArrayList<AbstractCard>();
				for (int i = 0; i < tmp.size(); i++) 
				{
					AbstractCard card = tmp.getNCardFromTop(AbstractDungeon.cardRandomRng.random(tmp.size() - 1));
					while (cardsToPlayFrom.contains(card))
					{
						card = tmp.getNCardFromTop(AbstractDungeon.cardRandomRng.random(tmp.size() - 1));
					}
					cardsToPlayFrom.add(card);
				}
				
				for (int i = 0; i < cardsToPlayFrom.size(); i++)
				{
					// Play card
					AbstractCard cardCopy = cardsToPlayFrom.get(i);
	    			if (cardCopy != null && m != null)
	    			{
	    				DuelistCard.resummon(cardCopy, m, this.copies, this.upgrade, false);	    				
	    			}
				}
				this.isDone = true;
				return;
			}
			
			else if (tmp.size() >= this.amount)
			{
				ArrayList<AbstractCard> cardsToPlayFrom = new ArrayList<AbstractCard>();
				for (int i = 0; i < this.amount; i++) 
				{
					AbstractCard card = tmp.getNCardFromTop(AbstractDungeon.cardRandomRng.random(tmp.size() - 1));
					while (cardsToPlayFrom.contains(card))
					{
						card = tmp.getNCardFromTop(AbstractDungeon.cardRandomRng.random(tmp.size() - 1));
					}
					cardsToPlayFrom.add(card);
				}
				
				for (int i = 0; i < cardsToPlayFrom.size(); i++)
				{
					// Play card
					AbstractCard cardCopy = cardsToPlayFrom.get(i);
	    			if (cardCopy != null && m != null)
	    			{
	    				DuelistCard.resummon(cardCopy, m, this.copies, this.upgrade, false);	    				
	    			}
				}
				this.isDone = true;
				return;
			}

			tickDuration();
			return;
		}
		tickDuration();
	}
}


