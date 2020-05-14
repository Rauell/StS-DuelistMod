package duelistmod.characters.Loadouts;

import java.util.ArrayList;

import duelistmod.characters.DuelistCustomLoadout;

public class RandomDeckMetronome extends DuelistCustomLoadout
{
    public RandomDeckMetronome()
    {
        this.ID = 30;
        this.Name = "Metronome Deck";
        this.longDesc = true;
        this.unlockReq = "Defeat the Heart with a Random Deck";
        this.permaLockMessage = "Locked";
    }

    @Override
    public ArrayList<String> GetStartingDeck()
    {
        ArrayList<String> res = super.GetStartingDeck();
        // grab starter deck from defaultmod -> get arraylist of cards -> for each card in list add card.getID() to res
        return res;
    }
}
